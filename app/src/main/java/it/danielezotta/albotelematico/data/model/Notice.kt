package it.danielezotta.albotelematico.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

/**
 * Data class representing a notice from Albo Telematico
 */
@Parcelize
data class Notice(
    val id: String,
    val title: String,
    val description: String? = null,
    val publishDate: String? = null,
    val expiryDate: String? = null,
    val category: String? = null,
    val municipality: String? = null,
    val publisher: String? = null,
    val attachments: List<Attachment> = emptyList(),
    val url: String
): Parcelable

/**
 * Data class representing an attachment for a notice
 */
@Parcelize
data class Attachment(
    val name: String,
    val url: String,
    val type: AttachmentType = AttachmentType.OTHER
): Parcelable

/**
 * Enum for different types of attachments
 */
enum class AttachmentType {
    PDF,
    DOC,
    DOCX,
    XLS,
    XLSX,
    IMAGE,
    OTHER;

    companion object {
        fun fromUrl(url: String): AttachmentType {
            val lower = url.substringAfterLast('.', "").lowercase(Locale.ROOT)
            return when (lower) {
                "pdf" -> PDF
                "doc" -> DOC
                "docx" -> DOCX
                "xls" -> XLS
                "xlsx" -> XLSX
                "png", "jpg", "jpeg", "gif", "bmp", "webp" -> IMAGE
                else -> OTHER
            }
        }
    }
}
