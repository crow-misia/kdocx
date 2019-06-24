package io.github.zncmn.kdocx

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFRun
import java.io.InputStream
import java.io.OutputStream

class Kdocx {
    private val expressionRegex = "\\$\\{([^}]+)}".toRegex()

    fun process(input: InputStream, output: OutputStream, context: Context) {
        val document = XWPFDocument(input)

        // Paragraph
        document.paragraphs.forEach { paragraph ->
            replaceParagraph(context, paragraph) { run, text -> run.setText(text, 0) }
        }

        // Header
        document.headerList.flatMap { it.paragraphs }.forEach { paragraph ->
            replaceParagraph(context, paragraph) { run, text -> run.setText(text, 0) }
        }

        // Footer
        document.footerList.flatMap { it.paragraphs }.forEach { paragraph ->
            replaceParagraph(context, paragraph) { run, text -> run.setText(text, 0) }
        }

        // Table
        document.tables.flatMap { it.rows }.flatMap { it.tableCells }.flatMap { it.paragraphs }.forEach { paragraph ->
            replaceParagraph(context, paragraph) { run, text -> run.setText(text, 0) }
        }

        // End Note
        document.endnotes.flatMap { it.paragraphs }.forEach { paragraph ->
            replaceParagraph(context, paragraph) { run, text -> run.setText(text, 0) }
        }

        document.write(output)
    }

    private fun replaceParagraph(
        context: Context,
        paragraph: XWPFParagraph,
        replaceFunc: (XWPFRun, String) -> Unit
    ) {
        val paragraphText = paragraph.paragraphText
        val expSequence = expressionRegex.findAll(paragraphText).map { regex ->
            regex.range to context.evaluate(regex.groupValues[1])
        }.asSequence()

        var offset = 0
        var replaceFlg = false
        paragraph.runs.forEach { run ->
            val text = run.getText(run.textPosition) ?: return@forEach
            val len = text.length

            expSequence.forEach expLoop@ { (range, replaceText) ->
                val startIdx = range.first - offset
                val endIdx = range.last - offset
                if (len <= startIdx) {
                    return@expLoop
                }

                if (replaceFlg) {
                    if (endIdx >= len) {
                        replaceFunc(run, "")
                    } else if (endIdx >= 0) {
                        replaceFunc(run, text.replaceRange(0, endIdx + 1, ""))
                        replaceFlg = false
                    }
                } else if (startIdx >= 0) {
                    replaceFlg = true
                    if (len > endIdx) {
                        replaceFunc(run, text.replaceRange(startIdx, endIdx + 1, replaceText.toString()))
                    } else {
                        replaceFunc(run, text.replaceRange(startIdx, text.length, replaceText.toString()))
                    }
                }
            }
            offset += len
        }
    }
}
