package ru.redenergy.report.common.compression

import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GZIPCompressor{
    /**
     * Compresses given string to byte array using gzip algorithm
     */
    fun compress(data: String): ByteArray {
        var stream = ByteArrayOutputStream()
        var gzip = GZIPOutputStream(stream)
        with(OutputStreamWriter(gzip, Charsets.UTF_8)) {
            write(data)
            close()
        }
        return stream.toByteArray()
    }

    /**
     * Decompress gzip byte array to string
     */
    fun decompress(data: ByteArray): String {
        var reader = InputStreamReader(GZIPInputStream(ByteArrayInputStream(data)), Charsets.UTF_8)
        var builder = StringBuilder()
        reader.buffered().forEachLine {
            builder.append(it)
        }
        return builder.toString()
    }
}
