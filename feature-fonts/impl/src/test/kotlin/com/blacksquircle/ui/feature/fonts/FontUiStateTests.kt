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

package com.blacksquircle.ui.feature.fonts

import android.graphics.Typeface
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.fonts.FontsViewModel
import com.blacksquircle.ui.feature.fonts.ui.fonts.FontsViewState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FontUiStateTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>()
    private val fontsRepository = mockk<FontsRepository>()
    private val settingsManager = mockk<SettingsManager>()
    private val typeface = mockk<Typeface>()

    @Before
    fun setup() {
        every { settingsManager.fontType } returns ""
    }

    @Test
    fun `When opening the screen Then display loading state`() = runTest {
        // Given
        coEvery { fontsRepository.loadFonts("") } coAnswers { delay(200); emptyList() }

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = FontsViewState(
            searchQuery = "",
            fonts = emptyList(),
            isLoading = true
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user has no fonts in database Then display empty state`() = runTest {
        // Given
        coEvery { fontsRepository.loadFonts("") } returns emptyList()

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = FontsViewState(
            searchQuery = "",
            fonts = emptyList(),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user has fonts in database Then display font list`() = runTest {
        // Given
        val fontList = listOf(
            FontModel(
                uuid = "1",
                name = "Droid Sans Mono",
                typeface = typeface,
                isExternal = true,
            )
        )
        coEvery { fontsRepository.loadFonts("") } returns fontList

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = FontsViewState(
            searchQuery = "",
            fonts = fontList,
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When user typing in search bar Then update font list`() = runTest {
        // Given
        val fontList = listOf(
            FontModel(
                uuid = "1",
                name = "Droid Sans Mono",
                typeface = typeface,
                isExternal = true,
            ),
            FontModel(
                uuid = "2",
                name = "Source Code Pro",
                typeface = typeface,
                isExternal = true,
            ),
        )
        coEvery { fontsRepository.loadFonts("") } returns fontList
        coEvery { fontsRepository.loadFonts(any()) } coAnswers {
            fontList.filter { it.name.contains(firstArg<String>()) }
        }

        // When
        val viewModel = createViewModel()
        viewModel.onQueryChanged("Source")
        advanceUntilIdle()

        // Then
        val viewState = FontsViewState(
            searchQuery = "Source",
            fonts = fontList.drop(1),
            isLoading = false,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    private fun createViewModel(): FontsViewModel {
        return FontsViewModel(
            stringProvider = stringProvider,
            fontsRepository = fontsRepository,
            settingsManager = settingsManager,
        )
    }
}