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

package com.blacksquircle.ui.feature.explorer.ui

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.tests.MainDispatcherRule
import com.blacksquircle.ui.core.tests.TimberConsoleRule
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.explorer.api.navigation.StorageDeniedDialog
import com.blacksquircle.ui.feature.explorer.data.manager.TaskManager
import com.blacksquircle.ui.feature.explorer.defaultFilesystems
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.repository.ExplorerRepository
import com.blacksquircle.ui.feature.explorer.ui.explorer.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.BreadcrumbState
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.ErrorState
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.filesystem.base.exception.PermissionException
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StoragePermissionsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val stringProvider = mockk<StringProvider>(relaxed = true)
    private val settingsManager = mockk<SettingsManager>(relaxed = true)
    private val taskManager = mockk<TaskManager>(relaxed = true)
    private val explorerRepository = mockk<ExplorerRepository>(relaxed = true)
    private val editorInteractor = mockk<EditorInteractor>(relaxed = true)
    private val serverInteractor = mockk<ServerInteractor>(relaxed = true)

    private val filesystems = defaultFilesystems()
    private val selectedFilesystem = filesystems[0]
    private val defaultLocation = selectedFilesystem.defaultLocation

    @Before
    fun setup() {
        coEvery { explorerRepository.loadFilesystems() } returns filesystems
        coEvery { explorerRepository.loadBreadcrumbs(selectedFilesystem) } returns
            listOf(defaultLocation)

        every { settingsManager.filesystem } returns selectedFilesystem.uuid
        every { settingsManager.filesystem = any() } answers {
            every { settingsManager.filesystem } returns firstArg()
        }
    }

    @Test
    fun `When storage permission is missing Then display error state`() = runTest {
        // Given
        coEvery { explorerRepository.listFiles(any()) } throws PermissionException()

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val breadcrumbs = listOf(
            BreadcrumbState(
                fileModel = defaultLocation,
                fileList = emptyList(),
                errorState = ErrorState(
                    action = ErrorAction.REQUEST_PERMISSIONS,
                )
            )
        )
        assertEquals(breadcrumbs, viewModel.viewState.value.breadcrumbs)
        assertEquals(breadcrumbs.size - 1, viewModel.viewState.value.selectedBreadcrumb)
    }

    @Test
    fun `When storage permission is denied Then open storage dialog`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPermissionDenied()

        // Then
        val expected = ViewEvent.Navigation(StorageDeniedDialog)
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When storage permission is granted Then reload file list`() = runTest {
        // Given
        val viewModel = createViewModel()
        clearMocks(explorerRepository, answers = false, recordedCalls = true) // reset verify count

        // When
        viewModel.onPermissionGranted()

        // Then
        coVerify(exactly = 1) { explorerRepository.listFiles(selectedFilesystem.defaultLocation) }
    }

    private fun createViewModel(): ExplorerViewModel {
        return ExplorerViewModel(
            stringProvider = stringProvider,
            settingsManager = settingsManager,
            taskManager = taskManager,
            editorInteractor = editorInteractor,
            explorerRepository = explorerRepository,
            serverInteractor = serverInteractor,
        )
    }
}