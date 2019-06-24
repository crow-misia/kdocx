package io.github.zncmn.kdocx.sample

import io.github.zncmn.kdocx.Context
import io.github.zncmn.kdocx.Kdocx
import java.io.File

fun main() {
    val inputFile = object {}.javaClass.classLoader.getResource("test.docx")!!
    val outputFile = File("output.docx")

    inputFile.openStream().buffered().use { input ->
        outputFile.outputStream().buffered().use { output ->
            val context = Context()
            context.putVar("message", "Hello World!!!")
            context.putVar("abc", "AABBCC")
            Kdocx().process(input, output, context)
        }
    }
}
