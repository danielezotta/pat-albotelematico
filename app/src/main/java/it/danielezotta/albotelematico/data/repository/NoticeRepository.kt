package it.danielezotta.albotelematico.data.repository

import it.danielezotta.albotelematico.data.api.AlboTelematicoApi
import it.danielezotta.albotelematico.data.mapper.NoticeMapper
import it.danielezotta.albotelematico.data.model.NoticeFilter
import it.danielezotta.albotelematico.data.model.NoticePage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling notice data operations
 */
@Singleton
class NoticeRepository @Inject constructor(
    private val api: AlboTelematicoApi,
    private val noticeMapper: NoticeMapper
) {

    suspend fun getNotices(
        page: Int,
        pageSize: Int,
        filter: NoticeFilter
    ): Result<NoticePage> {
        return try {
            val start = (page - 1) * pageSize
            // Ensure session/cookies are established before hitting the AJAX endpoint.
            runCatching { api.warmUp() }
            val response = api.getAttiPage(
                tipo = filter.dataSource.tipo,
                chiaveHtaccess = filter.dataSource.chiaveHtaccess,
                chiaveHtaccess2 = filter.dataSource.chiaveHtaccess2,
                tipologia = filter.tipologia,
                ente = filter.ente,
                filtroEnte = filter.filtroEnte,
                organo = filter.organo,
                search = filter.search,
                dataFrom = filter.dateFrom,
                dataTo = filter.dateTo,
                echo = page,
                start = start,
                length = pageSize,
                cacheBuster = System.currentTimeMillis()
            )
            val notices = noticeMapper.map(response.rows)
            Result.success(NoticePage(notices = notices, total = response.totalDisplayRecords))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
