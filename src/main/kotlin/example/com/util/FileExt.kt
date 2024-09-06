package example.com.util

import io.ktor.http.content.*
import java.io.File
import java.util.*

fun PartData.FileItem.saveFile(folderPath: String): String {
    val fileName = "${UUID.randomUUID()}.${File(originalFileName as String).extension}"
    val fileBytes = streamProvider().readBytes()

    val folder = File(folderPath)
    folder.mkdirs()
    File("$folder/$fileName").writeBytes(fileBytes)
    return fileName
}