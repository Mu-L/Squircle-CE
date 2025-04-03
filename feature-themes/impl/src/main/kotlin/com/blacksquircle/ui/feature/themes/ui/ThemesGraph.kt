/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.themes.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.themes.api.navigation.EditThemeScreen
import com.blacksquircle.ui.feature.themes.api.navigation.ThemesScreen
import com.blacksquircle.ui.feature.themes.ui.thememaker.EditThemeScreen
import com.blacksquircle.ui.feature.themes.ui.themes.ThemesScreen

fun NavGraphBuilder.themesGraph(navController: NavHostController) {
    composable<ThemesScreen> {
        ThemesScreen(navController)
    }
    composable<EditThemeScreen> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<EditThemeScreen>()
        EditThemeScreen(navArgs, navController)
    }
}