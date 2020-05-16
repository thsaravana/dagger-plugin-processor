package com.madrapps.daggerprocessor

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GenerateTest(val directory: String, val exclude: Array<String>)