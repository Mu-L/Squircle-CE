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

package com.blacksquircle.ui.feature.editor.ui.fragment.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
internal fun rememberCodeEditorState(): CodeEditorState {
    return remember { CodeEditorState() }
}

@Stable
internal class CodeEditorState {

    private val _eventBus = Channel<CodeEditorEvent>(Channel.BUFFERED)
    val eventBus: Flow<CodeEditorEvent> = _eventBus.receiveAsFlow()

    suspend fun send(event: CodeEditorEvent) {
        _eventBus.send(event)
    }
}