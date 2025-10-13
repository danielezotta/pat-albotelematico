package it.danielezotta.albotelematico.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import it.danielezotta.albotelematico.ui.screens.competitions.CompetitionsScreen
import it.danielezotta.albotelematico.ui.screens.home.HomeScreen
import it.danielezotta.albotelematico.ui.screens.noticedetail.NoticeDetailScreen
import it.danielezotta.albotelematico.ui.screens.tenders.ExpiredTendersScreen
import it.danielezotta.albotelematico.ui.screens.tenders.TendersScreen
import com.google.gson.Gson
import it.danielezotta.albotelematico.data.model.Notice

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNoticeClick = { notice ->
                    val payload = Uri.encode(Gson().toJson(notice))
                    navController.navigate(Screen.NoticeDetail.createRoute(payload))
                }
            )
        }
        
        composable(Screen.Tenders.route) {
            TendersScreen(
                onNoticeClick = { notice ->
                    val payload = Uri.encode(Gson().toJson(notice))
                    navController.navigate(Screen.NoticeDetail.createRoute(payload))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Competitions.route) {
            CompetitionsScreen(
                onNoticeClick = { notice ->
                    val payload = Uri.encode(Gson().toJson(notice))
                    navController.navigate(Screen.NoticeDetail.createRoute(payload))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ExpiredTenders.route) {
            ExpiredTendersScreen(
                onNoticeClick = { notice ->
                    val payload = Uri.encode(Gson().toJson(notice))
                    navController.navigate(Screen.NoticeDetail.createRoute(payload))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.NoticeDetail.route,
            arguments = listOf(navArgument("noticeJson") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val noticeJson = backStackEntry.arguments?.getString("noticeJson") ?: return@composable
            val decoded = Uri.decode(noticeJson)
            val notice = runCatching { Gson().fromJson(decoded, Notice::class.java) }.getOrNull() ?: return@composable
            NoticeDetailScreen(
                notice = notice,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
