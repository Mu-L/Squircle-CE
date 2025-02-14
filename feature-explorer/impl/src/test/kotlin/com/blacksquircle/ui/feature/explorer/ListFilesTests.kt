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

package com.blacksquircle.ui.feature.explorer

import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.domain.model.Task
import com.blacksquircle.ui.feature.explorer.domain.model.TaskType
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerViewState
import com.blacksquircle.ui.feature.explorer.ui.fragment.model.BreadcrumbState
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerErrorAction
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerIntent
import com.blacksquircle.ui.feature.explorer.ui.mvi.ToolbarViewState
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.feature.servers.api.interactor.ServersInteractor
import com.blacksquircle.ui.filesystem.base.exception.PermissionException
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.blacksquircle.ui.ds.R as UiR

class ListFilesTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>()
    private val settingsManager = mockk<SettingsManager>()
    private val taskManager = mockk<TaskManager>()
    private val explorerRepository = mockk<ExplorerRepository>()
    private val editorInteractor = mockk<EditorInteractor>()
    private val serversInteractor = mockk<ServersInteractor>()

    @Before
    fun setup() {
        every { stringProvider.getString(UiR.string.common_no_result) } returns "No result"
        every { stringProvider.getString(R.string.message_access_denied) } returns "Access denied"
        every { stringProvider.getString(R.string.message_access_required) } returns "Access required"

        every { settingsManager.showHidden } returns true
        every { settingsManager.showHidden = any() } just Runs
        every { settingsManager.foldersOnTop } returns true
        every { settingsManager.foldersOnTop = any() } just Runs
        every { settingsManager.viewMode } returns "0"
        every { settingsManager.viewMode = any() } just Runs
        every { settingsManager.sortMode } returns "0"
        every { settingsManager.sortMode = any() } just Runs
        every { settingsManager.filesystem } returns "local"
        every { settingsManager.filesystem = any() } just Runs

        every { taskManager.monitor(any()) } returns MutableStateFlow(Task("", TaskType.CREATE))

        coEvery { serversInteractor.serverFlow } returns MutableStateFlow(emptyList())
        coEvery { explorerRepository.selectFilesystem(any()) } just Runs
        every { explorerRepository.createFile(any()) } returns ""
        every { explorerRepository.renameFile(any(), any()) } returns ""
        every { explorerRepository.deleteFiles(any()) } returns ""
        every { explorerRepository.copyFiles(any(), any()) } returns ""
        every { explorerRepository.cutFiles(any(), any()) } returns ""
        every { explorerRepository.compressFiles(any(), any()) } returns ""
        every { explorerRepository.extractFiles(any(), any()) } returns ""
    }

    @Test
    fun `When the user opens the app Then load default directory and select tab`() = runTest {
        // Given
        val rootTree = BreadcrumbState(
            fileModel = createFolder("Documents"),
            fileList = listOf(
                createFolder(fileName = "Documents/first"),
                createFolder(fileName = "Documents/second"),
                createFolder(fileName = "Documents/third"),
            )
        )
        coEvery { explorerRepository.listFiles(null) } returns rootTree

        // When
        val viewModel = explorerViewModel()
        viewModel.obtainEvent(ExplorerIntent.OpenFolder())

        // Then
        val toolbarViewState =
            ToolbarViewState.ActionBar(listOf(rootTree.fileModel), emptyList(), TaskType.CREATE)
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)

        val explorerViewState = ExplorerViewState.Files(rootTree.fileList)
        assertEquals(explorerViewState, viewModel.viewState.value)
    }

    @Test
    fun `When opening a folder Then load files and select tab`() = runTest {
        // Given
        val rootTree = BreadcrumbState(
            fileModel = createFolder("Documents"),
            fileList = listOf(
                createFolder(fileName = "Documents/folder_1"),
                createFolder(fileName = "Documents/folder_2"),
                createFolder(fileName = "Documents/folder_3"),
            )
        )
        val dirTree = BreadcrumbState(
            fileModel = rootTree.fileList[0],
            fileList = listOf(
                createFile(fileName = "Documents/folder_1/test_1.txt"),
                createFile(fileName = "Documents/folder_1/test_2.txt"),
                createFile(fileName = "Documents/folder_1/test_3.txt"),
            )
        )
        coEvery { explorerRepository.listFiles(null) } returns rootTree
        coEvery { explorerRepository.listFiles(dirTree.fileModel) } returns dirTree

        // When
        val viewModel = explorerViewModel()
        viewModel.obtainEvent(ExplorerIntent.OpenFolder())
        viewModel.obtainEvent(ExplorerIntent.OpenFolder(dirTree.fileModel))

        // Then
        val toolbarViewState = ToolbarViewState.ActionBar(
            listOf(rootTree.fileModel, dirTree.fileModel), emptyList(), TaskType.CREATE,
        )
        assertEquals(toolbarViewState, viewModel.toolbarViewState.value)

        val explorerViewState = ExplorerViewState.Files(dirTree.fileList)
        assertEquals(explorerViewState, viewModel.viewState.value)
    }

    @Test
    fun `When opening an empty folder Then display empty state`() = runTest {
        // Given
        val rootTree = BreadcrumbState(
            fileModel = createFolder("Documents"),
            fileList = listOf(
                createFolder(fileName = "Documents/folder_1"),
                createFolder(fileName = "Documents/folder_2"),
                createFolder(fileName = "Documents/folder_3"),
            )
        )
        val dirTree = BreadcrumbState(
            fileModel = rootTree.fileList[0],
            fileList = emptyList()
        )
        coEvery { explorerRepository.listFiles(null) } returns rootTree
        coEvery { explorerRepository.listFiles(dirTree.fileModel) } returns dirTree

        // When
        val viewModel = explorerViewModel()
        viewModel.obtainEvent(ExplorerIntent.OpenFolder())
        viewModel.obtainEvent(ExplorerIntent.OpenFolder(dirTree.fileModel))

        // Then
        val explorerViewState = ExplorerViewState.Error(
            image = UiR.drawable.ic_file_find,
            title = stringProvider.getString(UiR.string.common_no_result),
            subtitle = "",
            action = ExplorerErrorAction.Undefined,
        )
        assertEquals(explorerViewState, viewModel.viewState.value)
    }

    @Test
    fun `When opening a folder Then display loading state`() = runTest {
        // Given
        coEvery { explorerRepository.listFiles(any()) } coAnswers {
            delay(200)
            BreadcrumbState(mockk(), emptyList())
        }

        // When
        val viewModel = explorerViewModel()
        viewModel.obtainEvent(ExplorerIntent.OpenFolder())

        // Then
        val explorerViewState = ExplorerViewState.Loading
        assertEquals(explorerViewState, viewModel.viewState.value)
    }

    @Test
    fun `When opening a folder without storage access Then display permission state`() = runTest {
        // Given
        coEvery { explorerRepository.listFiles(any()) } answers { throw PermissionException() }

        // When
        val viewModel = explorerViewModel()
        viewModel.obtainEvent(ExplorerIntent.OpenFolder())

        // Then
        val explorerViewState = ExplorerViewState.Error(
            image = UiR.drawable.ic_file_error,
            title = stringProvider.getString(R.string.message_access_denied),
            subtitle = stringProvider.getString(R.string.message_access_required),
            action = ExplorerErrorAction.RequestPermission,
        )
        assertEquals(explorerViewState, viewModel.viewState.value)
    }

    private fun explorerViewModel(): ExplorerViewModel {
        return ExplorerViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            taskManager = taskManager,
            editorInteractor = editorInteractor,
            explorerRepository = explorerRepository,
            serversInteractor = serversInteractor,
        )
    }
}