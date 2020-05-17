# dagger-plugin-processor
Annotation processor to help with the <a href="https://github.com/Madrapps/dagger-plugin">dagger-plugin</a> project. This project is to be used exclusively with `dagger-plugin` project.

Spec
-----

`@GenerateTest`
  - `directory` => The root directory that contains the test case folders
  - `exclude `  => List of directories to exclude in the annotation processing
  
Example
-----

```
@GenerateTest(directory = "inject", exclude = ["assets"])
abstract class InjectTestCase : BaseTestCase("inject")
```

```
testData
|_inject
  |_assets
  |_AbstractClass
    |_AbstractClass.java
    |_ConcreteClass.java
    |_KSuccessComponent.kt
    |_KFailureComponent.kt
  |_FinalFields
    |_ClassWithFinalField.java
    |_ClassWithNonFinalField.java
    |_KFailureComponent.kt
    |_KSuccessComponent.kt
    
```

The above config will generate the following Test files under `build/generated/source/kapt/test/...` directory:
```
package com.madrapps.dagger

class AbstractClass : InjectTestCase() {
    fun testKFailureComponent() = testValidation()
    fun testConcreteClass() = testValidation()
    fun testAbstractClass() = testValidation()
    fun testKSuccessComponent() = testValidation()
}
```
```
package com.madrapps.dagger

class FinalFields : InjectTestCase() {
    fun testClassWithFinalField() = testValidation()
    fun testKFailureComponent() = testValidation()
    fun testKSuccessComponent() = testValidation()
    fun testClassWithNonFinalField() = testValidation()
}
```
