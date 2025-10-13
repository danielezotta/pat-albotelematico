package it.danielezotta.albotelematico.data.mapper

import android.net.Uri
import it.danielezotta.albotelematico.data.model.Attachment
import it.danielezotta.albotelematico.data.model.AttachmentType
import it.danielezotta.albotelematico.data.model.Notice
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.Locale
import javax.inject.Inject

class NoticeMapper @Inject constructor() {

    fun map(rows: List<List<String>>): List<Notice> {
        return rows.mapNotNull { columns ->
            val attiHtml = columns.getOrNull(0) ?: return@mapNotNull null
            val attiDoc = Jsoup.parse(attiHtml)
            val mainLink = attiDoc.selectFirst("a[href]")

            val title = mainLink?.text()?.takeIf { it.isNotBlank() }
                ?: attiDoc.text().lineSequence().firstOrNull()?.trim()
                ?: return@mapNotNull null

            val rawUrl = mainLink?.attr("href").orEmpty()
            val url = resolveUrl(rawUrl)
            val noticeId = extractId(url.ifBlank { rawUrl }) ?: title.lowercase(Locale.ROOT).hashCode().toString()

            val description = attiDoc.select("p, span, div, li")
                .map { it.text().trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .joinToString(separator = "\n")
                .ifBlank { null }

            val attachments = attiDoc.select("a[href]")
                .drop(1) // skip the primary link already used as detail
                .mapNotNull { anchor ->
                    val href = resolveUrl(anchor.attr("href"))
                    if (href.isBlank()) return@mapNotNull null
                    Attachment(
                        name = anchor.text().ifBlank { href.substringAfterLast('/') },
                        url = href,
                        type = AttachmentType.fromUrl(href)
                    )
                }

            val infoDoc = Jsoup.parse(columns.getOrNull(1) ?: "")
            val infoText = infoDoc.text()
            val publishDate = DATE_REGEX.find(infoText)?.value
            val expiryDate = EXPIRY_REGEX.find(infoText)?.value

            val category = Jsoup.parse(columns.getOrNull(2) ?: "")
                .text()
                .ifBlank { null }

            val municipality = findMunicipality(attiDoc, infoText)
            val publisher = extractPublisher(attiDoc.text()) ?: extractPublisher(infoText)

            Notice(
                id = noticeId,
                title = title.trim(),
                description = description,
                publishDate = publishDate,
                expiryDate = expiryDate,
                category = category,
                municipality = municipality,
                publisher = publisher,
                attachments = attachments,
                url = url.ifBlank { rawUrl }
            )
        }
    }

    private fun resolveUrl(url: String): String {
        if (url.isBlank()) return ""
        return if (url.startsWith("http")) url else BASE_URL + url.removePrefix("/")
    }

    private fun extractId(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val parsed = runCatching { Uri.parse(url) }.getOrNull()
        val idFromQuery = parsed?.getQueryParameter("id") ?: parsed?.getQueryParameter("ID")
        if (!idFromQuery.isNullOrBlank()) return idFromQuery
        val lastSegment = parsed?.lastPathSegment?.takeIf { it.isNotBlank() }
        if (!lastSegment.isNullOrBlank()) return lastSegment
        val afterEquals = url.substringAfterLast('=', "")
        return afterEquals.takeIf { it.isNotBlank() }
    }

    private fun findMunicipality(document: Document, info: String): String? {
        val fromHtml = document.select("span.badge, span.label, em, strong, small")
            .asSequence()
            .map { it.text().trim() }
            .firstOrNull { it.contains("Comune", ignoreCase = true) }
        if (!fromHtml.isNullOrBlank()) return fromHtml
        val match = MUNICIPALITY_REGEX.find(info)
        return match?.groups?.get(1)?.value
    }

    private fun extractPublisher(textSource: String?): String? {
        if (textSource.isNullOrBlank()) return null
        val match = PUBLISHER_REGEX.find(textSource)
        return match?.groups?.get(1)?.value?.trim()
    }

    companion object {
        private const val BASE_URL = "https://www.albotelematico.tn.it/"
        private val DATE_REGEX = Regex("\\d{2}/\\d{2}/\\d{4}")
        private val EXPIRY_REGEX = Regex("fino al (\\d{2}/\\d{2}/\\d{4})", RegexOption.IGNORE_CASE)
        private val MUNICIPALITY_REGEX = Regex("Comune di ([A-Za-zÀ-ÿ'\\s-]+)", RegexOption.IGNORE_CASE)
        private val PUBLISHER_REGEX = Regex("Atto pubblicato da\\s+([^\\.;]+)", RegexOption.IGNORE_CASE)
    }
}
