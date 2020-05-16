package com.madrapps.daggerprocessor

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic


class DaggerProcessor : AbstractProcessor() {

    lateinit var filer: Filer
    lateinit var elementUtils: Elements
    lateinit var messager: Messager

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        elementUtils = processingEnv.elementUtils
        messager = processingEnv.messager
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(GenerateTest::class.java).forEach { element ->
            messager.printMessage(Diagnostic.Kind.WARNING, "Processing $element")
            if (element.kind == ElementKind.CLASS) {
                val jfo = filer.createSourceFile("TestSample")
                val writer = jfo.openWriter()

                writer.use {
                    it.write("""
                        import org.junit.Test;
                        
                        public class TestSample {
                        
                            @Test
                            public void testSomething() {
                                assert(true);
                            }
                        }
                    """.trimIndent())
                }

            }
        }
        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(GenerateTest::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }
}