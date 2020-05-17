package com.madrapps.daggerprocessor

import com.sun.tools.javac.code.Symbol
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation.SOURCE_OUTPUT

class DaggerProcessor : AbstractProcessor() {

    private lateinit var filer: Filer
    private lateinit var projectPath: String
    private lateinit var testDateFile: File

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        val jfo = filer.createSourceFile("TestSample")
        projectPath = jfo.toUri().path.substringBeforeLast("build")
        testDateFile = File(File(projectPath), "src/test/testData")
        jfo.delete()
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(GenerateTest::class.java).forEach { element ->
            if (element is Symbol.ClassSymbol) {
                val ann: GenerateTest = element.getAnnotation(GenerateTest::class.java)
                val packageName = element.owner.qualifiedName.toString()
                val className = element.simpleName.toString()
                val injectFile = File(testDateFile, ann.directory)

                (injectFile.listFiles() ?: emptyArray()).filterNot { it.name in ann.exclude }.forEach { folder ->
                    val testClassName = folder.name
                    val ko = filer.createResource(SOURCE_OUTPUT, packageName, "${testClassName}.kt")
                    ko.openWriter().use {
                        it.write(
                            """
package $packageName

class $testClassName : $className() {
${getTestMethods(folder)}
}
""".trimIndent()
                        )
                    }
                }
            }
        }
        return true
    }

    private fun getTestMethods(folder: File): String {
        val builder = StringBuilder()
        folder.listFiles()?.forEach { testMethodFile ->
            builder
                .append("    ")
                .append("fun test${testMethodFile.nameWithoutExtension}() = testValidation()").append("\n")
        }
        return builder.toString()
    }

    override fun getSupportedAnnotationTypes() = mutableSetOf(GenerateTest::class.java.canonicalName)
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
}