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

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.provider.typeface.TypefaceProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.api.navigation.CloseFileDialog
import com.blacksquircle.ui.feature.editor.createDocument
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.repository.DocumentRepository
import com.blacksquircle.ui.feature.editor.ui.editor.EditorViewModel
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.shortcuts.api.interactor.ShortcutsInteractor
import com.blacksquircle.ui.feature.themes.api.interactor.ThemesInteractor
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CloseFileTest {

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
    fun `When closing modified file Then open confirmation dialog`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0, modified = true),
        )
        val selected = documentList[0] // selected "first.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseFileClicked()

        // Then
        val expected = ViewEvent.Navigation(CloseFileDialog(selected.uuid, selected.name))
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When close confirmed Then close document`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0, modified = true),
        )
        val selected = documentList[0] // selected "first.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseFileClicked()
        viewModel.onCloseModifiedClicked(selected.uuid)

        // Then
        assertEquals(emptyList<DocumentState>(), viewModel.viewState.value.documents)
        assertEquals(-1, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When closing selected tab at the first position Then check documents list`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[0] // selected "first.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseFileClicked() // close selected

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "2", fileName = "second.txt", position = 0),
            createDocument(uuid = "3", fileName = "third.txt", position = 1),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When closing selected tab at the last position Then check documents list`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[2] // selected "third.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseFileClicked() // close selected

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(1, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When closing selected tab in the middle Then check documents list`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[1] // selected "second.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseFileClicked() // close selected

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "3", fileName = "third.txt", position = 1),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When closing unselected tab at the first position Then check documents list`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[2] // selected "third.txt"
        val unselected = documentList[0] // closing "first.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseClicked(unselected, fromUser = true)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "2", fileName = "second.txt", position = 0),
            createDocument(uuid = "3", fileName = "third.txt", position = 1),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(1, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When closing unselected tab at the last position Then check documents list`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[0] // selected "first.txt"
        val unselected = documentList[2] // closing "third.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseClicked(unselected, fromUser = true)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When closing all tabs but not selected Then check documents list`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[1] // selected "second.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseOthersClicked(selected)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "2", fileName = "second.txt", position = 0),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When closing all tabs but not unselected Then check documents list`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[2] // selected "third.txt"
        val unselected = documentList[1] // closing all except "second.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseOthersClicked(unselected)

        // Then
        val expectedDocuments = listOf(
            createDocument(uuid = "2", fileName = "second.txt", position = 0),
        )
        val actualDocuments = viewModel.viewState.value.documents
            .map(DocumentState::document)

        assertEquals(expectedDocuments, actualDocuments)
        assertEquals(0, viewModel.viewState.value.selectedDocument)
    }

    @Test
    fun `When closing all tabs Then check documents list`() = runTest {
        // Given
        val documentList = listOf(
            createDocument(uuid = "1", fileName = "first.txt", position = 0),
            createDocument(uuid = "2", fileName = "second.txt", position = 1),
            createDocument(uuid = "3", fileName = "third.txt", position = 2),
        )
        val selected = documentList[2] // selected "third.txt"

        every { settingsManager.selectedUuid } returns selected.uuid
        coEvery { documentRepository.loadDocuments() } returns documentList

        // When
        val viewModel = createViewModel()
        viewModel.onCloseAllClicked()

        // Then
        assertEquals(emptyList<DocumentState>(), viewModel.viewState.value.documents)
        assertEquals(-1, viewModel.viewState.value.selectedDocument)
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