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

package com.blacksquircle.ui.feature.editor.ui.fragment.internal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.core.factory.LanguageFactory
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.ui.fragment.model.DocumentState
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun DocumentNavigation(
    tabs: List<DocumentState>,
    modifier: Modifier = Modifier,
    onDocumentClicked: (DocumentState) -> Unit = {},
    onCloseDocumentClicked: (DocumentState) -> Unit = {},
    selectedIndex: Int = -1,
) {
    val scrollState = rememberLazyListState()

    LazyRow(
        state = scrollState,
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
    ) {
        itemsIndexed(
            items = tabs,
            key = { _, state -> state.document.uuid }
        ) { i, state ->
            DocumentTab(
                title = state.document.name,
                iconResId = UiR.drawable.ic_close,
                selected = i == selectedIndex,
                onClick = { onDocumentClicked(state) },
                onActionClick = { onCloseDocumentClicked(state) },
                modifier = Modifier.animateItem(),
            )
        }
    }

    /* FIXME LaunchedEffect(selectedIndex) {
        if (selectedIndex > -1) {
            scrollState.animateScrollToItem(selectedIndex)
        }
    }*/
}

@PreviewLightDark
@Composable
private fun DocumentNavigationPreview() {
    PreviewBackground {
        DocumentNavigation(
            selectedIndex = 0,
            tabs = listOf(
                DocumentState(
                    document = DocumentModel(
                        uuid = "123",
                        fileUri = "file://storage/emulated/0/Downloads/untitled.txt",
                        filesystemUuid = "123",
                        language = LanguageFactory.fromName("plaintext"),
                        modified = false,
                        position = 0,
                        scrollX = 0,
                        scrollY = 0,
                        selectionStart = 0,
                        selectionEnd = 0,
                    ),
                )
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}