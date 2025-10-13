package it.danielezotta.albotelematico.data.repository

import it.danielezotta.albotelematico.data.model.Municipality
import it.danielezotta.albotelematico.data.model.Territory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerritoryRepository @Inject constructor(
    private val okHttpClient: OkHttpClient
) {

    suspend fun getTerritories(): Result<List<Territory>> = runCatching {
        val html = fetch("$BASE_URL/territori/")
        parseTerritories(html)
    }

    suspend fun getMunicipalities(territorySlug: String): Result<List<Municipality>> = runCatching {
        val normalisedSlug = territorySlug.trim().lowercase(Locale.ROOT)
        if (normalisedSlug.isEmpty()) return@runCatching emptyList()
        val html = fetch("$BASE_URL/territori/$normalisedSlug")
        parseMunicipalities(html)
    }

    private suspend fun fetch(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Richiesta fallita con codice ${response.code}")
            }
            response.body?.string() ?: throw IOException("Risposta vuota da $url")
        }
    }

    private fun parseTerritories(html: String): List<Territory> {
        val document = Jsoup.parse(html, BASE_URL)
        val results = linkedMapOf<String, Territory>()
        document.select("div.zonaBox > a[href]")
            .forEach { element ->
                val href = element.attr("href")
                val absoluteHref = resolveUrl(href)
                val slug = absoluteHref.removePrefix("$BASE_URL/territori/").trimEnd('/')
                if (slug.isBlank()) return@forEach
                val text = element.text().trim()
                if (text.isBlank()) return@forEach
                val countMatch = COUNT_REGEX.find(text)
                val cleanName = countMatch?.groups?.get(1)?.value?.trim() ?: text
                val count = countMatch?.groups?.get(2)?.value?.toIntOrNull()
                val territory = Territory(
                    slug = slug,
                    name = cleanName,
                    municipalitiesCount = count
                )
                results.putIfAbsent(slug, territory)
            }
        return results.values.toList()
    }

    private fun parseMunicipalities(html: String): List<Municipality> {
        val document = Jsoup.parse(html, BASE_URL)
        val results = linkedMapOf<String, Municipality>()
        document.select("div.col-sm-12 a[href]")
            .asSequence()
            .filter { element ->
                val href = resolveUrl(element.attr("href"))
                href.startsWith("$BASE_URL/bacheca/")
            }
            .forEach { element ->
                val absoluteHref = resolveUrl(element.attr("href"))
                val slug = absoluteHref.removePrefix("$BASE_URL/bacheca/").trimEnd('/')
                if (slug.isBlank()) return@forEach
                val name = element.text().trim()
                if (name.isBlank()) return@forEach
                results.putIfAbsent(slug, Municipality(slug = slug, name = name))
            }
        return results.values.toList()
    }

    companion object {
        private const val BASE_URL = "https://www.albotelematico.tn.it"
        private val COUNT_REGEX = Regex("^([^\\(]+)\\((\\d+)\\)")

        private fun resolveUrl(href: String): String {
            if (href.startsWith("http")) return href
            return "$BASE_URL/${href.removePrefix("/")}"
        }
    }
}
