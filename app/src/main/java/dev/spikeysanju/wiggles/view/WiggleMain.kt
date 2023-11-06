/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.spikeysanju.wiggles.view

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.spikeysanju.wiggles.data.FakeDogDatabase
import dev.spikeysanju.wiggles.navigation.Screen

@ExperimentalAnimationApi
@Composable
fun WigglesMain(toggleTheme: () -> Unit) {
    // Remembering a NavController that handles navigation with animations in the app.
    val navController = rememberAnimatedNavController()
    // Setup for a navigation host that enables animated transitions between composables.
    AnimatedNavHost(navController, startDestination = Screen.Home.route) {
        // Define the home screen composable with associated transitions.
        composable(
            Screen.Home.route,
            // Transition for exiting the home screen, sliding out and fading out.
            exitTransition = { _, _ ->
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            },
            // Transition for returning to the home screen, sliding in and fading in.
            popEnterTransition = { _, _ ->
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            },
        ) {
            // The home screen UI content, passing in the navController, a list of dogs, and theme toggle function.
            Home(navController, FakeDogDatabase.dogList, toggleTheme)
        }
        // Define the details screen composable with associated transitions.
        composable(
            "${Screen.Details.route}/{id}/{title}/{location}",
            // Transition for entering the details screen, sliding in and fading in.
            enterTransition = { _, _ ->
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            },
            // Transition for exiting the details screen, sliding out and fading out.
            exitTransition = { _, _ ->
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            },
            // Arguments to be passed to the details screen, such as the ID.
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            // The details screen UI content, retrieving the 'id' from the arguments.
            Details(navController, it.arguments?.getInt("id") ?: 0)
        }
    }
}
