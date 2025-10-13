package it.danielezotta.albotelematico.ui.navigation

/**
 * Sealed class representing all screens in the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Tenders : Screen("tenders")
    object Competitions : Screen("competitions")
    object ExpiredTenders : Screen("expired_tenders")
    object NoticeDetail : Screen("notice_detail?noticeJson={noticeJson}") {
        fun createRoute(noticeJson: String) = "notice_detail?noticeJson=$noticeJson"
    }
    object Search : Screen("search")
}
