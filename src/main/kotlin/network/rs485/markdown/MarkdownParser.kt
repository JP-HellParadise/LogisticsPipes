/*
 * Copyright (c) 2020  RS485
 *
 * "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public
 * License 1.0.1, or MMPL. Please check the contents of the license located in
 * https://github.com/RS485/LogisticsPipes/blob/dev/LICENSE.md
 *
 * This file can instead be distributed under the license terms of the
 * MIT license:
 *
 * Copyright (c) 2020  RS485
 *
 * This MIT license was reworded to only match this file. If you use the regular
 * MIT license in your project, replace this copyright notice (this line and any
 * lines below and NOT the copyright line above) with the lines from the original
 * MIT license located here: http://opensource.org/licenses/MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this file and associated documentation files (the "Source Code"), to deal in
 * the Source Code without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Source Code, and to permit persons to whom the Source Code is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Source Code, which also can be
 * distributed under the MIT.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package network.rs485.markdown

import java.lang.Integer.min

object MarkdownParser {
    private val htmlBreakRegex = Regex("<br(\\s*/)?>")

    internal fun splitToInlineElements(inputChars: CharSequence): List<InlineElement> {
        // TODO: parse TokenText(s)

        // newlines are already handled as special break characters at this point
        val chars = htmlBreakRegex.replace(inputChars, "\n")

        // breaks up all the words and returns them as inline elements without spaces
        return chars.split(' ').flatMap { word ->
            when {
                // new-line character is replaced with a Break element
                word.contains('\n') -> word.split('\n').zipWithNext { a, b ->
                    listOfNotNull(parseTextElement(a), Break, parseTextElement(b))
                }.flatten()
                // parse anything else as text element
                else -> listOfNotNull(parseTextElement(word))
            }
        }
    }

    private fun parseTextElement(word: String) = Text(word).takeIf { word.isNotBlank() }

    private fun countChars(char: Char, str: String, index: Int = 0, maximum: Int = str.length): Int {
        if (str[index] != char) return 0
        var count = 1
        val actualMax = min(str.length - index, maximum)
        while (count < actualMax && str[index + count] == char) ++count
        return count
    }

    fun parseParagraphs(str: String): List<Paragraph> {
        val paragraphs = ArrayList<Paragraph>()
        val sb = StringBuilder()
        val lines = str.split('\n')

        fun completeParagraph() {
            if (sb.isNotBlank()) {
                paragraphs.add(RegularParagraph(splitToInlineElements(sb)))
                // TODO: add ImageParagraph
            }
            sb.clear()
        }

        lines.flatMap { line ->
            when {
                line.endsWith('\\') -> {
                    // special handling for escaped line endings
                    sb.append(line.substring(0, line.length - 1))
                    return@flatMap emptyList<String>()
                }
                line.endsWith("  ") -> {
                    // special handling for special line breaks
                    sb.append(line.substring(0, line.length - 2))
                    sb.append('\n')
                }
                else -> sb.append(line)
            }
            val concatLine = sb.toString()
            sb.clear()
            return@flatMap listOf(concatLine)
        }.forEach { dirtyLine ->
            val line = dirtyLine.trimStart()
            when {
                line.isEmpty() -> completeParagraph()
                line.startsWith(Tag.HEADER.char) -> {
                    completeParagraph()
                    val headerLevel = countChars(Tag.HEADER.char, line, maximum = 4)
                    paragraphs.add(HeaderParagraph(splitToInlineElements(line.trimStart(Tag.HEADER.char)), headerLevel))
                }
                // TODO: add MenuParagraph
                // TODO: add ListParagraph
                else -> {
                    if (sb.isNotEmpty()) sb.append(' ')
                    sb.append(line)
                }
            }
        }
        if (sb.isNotEmpty()) completeParagraph()
        return paragraphs
    }

    enum class Tag(val char: Char) {
        HEADER('#')
    }

}
