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

package com.blacksquircle.ui.feature.editor.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.radio.Radio
import com.blacksquircle.ui.feature.editor.R

@Composable
internal fun ForceSyntaxScreen(
    selectedValue: String,
    onLanguageSelected: (String) -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    val entries = stringArrayResource(R.array.language_entries)
    val entryValues = stringArrayResource(R.array.language_values)

    AlertDialog(
        title = stringResource(R.string.dialog_title_force_syntax),
        verticalScroll = false,
        horizontalPadding = false,
        content = {
            LazyColumn {
                itemsIndexed(entryValues) { index, value ->
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(),
                                onClick = { onLanguageSelected(value) }
                            )
                            .padding(horizontal = 24.dp)
                    ) {
                        Radio(
                            title = entries[index],
                            checked = value == selectedValue,
                            onClick = { onLanguageSelected(value) },
                            textStyle = SquircleTheme.typography.text18Regular,
                            interactionSource = interactionSource,
                            indication = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
                }
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun ForceSyntaxScreenPreview() {
    PreviewBackground {
        ForceSyntaxScreen(
            selectedValue = "cplusplus",
        )
    }
}