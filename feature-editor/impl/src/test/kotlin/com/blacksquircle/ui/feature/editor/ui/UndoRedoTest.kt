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

package com.blacksquircle.ui.feature.editor.ui

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.provider.typeface.TypefaceProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.createDocument
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewModel
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.feature.themes.api.interactor.ThemesInteractor
import io.github.rosemoe.sora.text.Content
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UndoRedoTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val documentRepository = mockk<DocumentRepository>(relaxed = true)
    private val editorInteractor = mockk<EditorInteractor>(relaxed = true)
    private val themesInteractor = mockk<ThemesInteractor>(relaxed = true)
    private val fontsInteractor = mockk<FontsInteractor>(relaxed = true)
    private val shortcutsInteractor = mockk<ShortcutsInteractor>(relaxed = true)
    private val languageInteractor = mockk<LanguageInteractor>(relaxed = true)

    @Before
    fun setup() {
        mockkObject(TypefaceProvider)
        every { TypefaceProvider.DEFAULT } returns mockk()
    }

    @Test
    fun `When undo clicked Then undo changes`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val content = mockk<Content>(relaxed = true)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.readOnly } returns false

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onUndoClicked()

        // Then
        coVerify(exactly = 1) { content.undo() }
    }

    @Test
    fun `When undo clicked with read only mode Then do nothing`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val content = mockk<Content>(relaxed = true)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.readOnly } returns true

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onUndoClicked()

        // Then
        coVerify(exactly = 0) { content.undo() }
    }

    @Test
    fun `When redo clicked Then redo changes`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val content = mockk<Content>(relaxed = true)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.readOnly } returns false

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onRedoClicked()

        // Then
        coVerify(exactly = 1) { content.redo() }
    }

    @Test
    fun `When redo clicked with read only mode Then do nothing`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
        )
        val selected = documentList[0]
        val content = mockk<Content>(relaxed = true)

        every { settingsManager.selectedUuid } returns selected.uuid
        every { settingsManager.readOnly } returns true

        coEvery { documentRepository.loadDocuments() } returns documentList
        coEvery { documentRepository.loadDocument(any()) } returns content

        // When
        val viewModel = createViewModel()
        viewModel.onRedoClicked()

        // Then
        coVerify(exactly = 0) { content.redo() }
    }

    private fun createViewModel(): EditorViewModel {
        return EditorViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            documentRepository = documentRepository,
            editorInteractor = editorInteractor,
            themesInteractor = themesInteractor,
            fontsInteractor = fontsInteractor,
            shortcutsInteractor = shortcutsInteractor,
            languageInteractor = languageInteractor,
        )
    }
}