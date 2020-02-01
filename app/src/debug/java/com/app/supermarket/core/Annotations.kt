package com.app.supermarket.core

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class OpenClass


@OpenClass
@Target(AnnotationTarget.CLASS)
annotation class OpenForTesting