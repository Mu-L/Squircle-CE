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

package com.blacksquircle.ui.feature.explorer.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun RenameScreen(
    currentFileName: String,
    onConfirmClicked: (String) -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    var fileName by rememberSaveable { mutableStateOf(currentFileName) }
    var isError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        title = stringResource(R.string.dialog_title_rename),
        content = {
            TextField(
                inputText = fileName,
                onInputChanged = { value ->
                    fileName = value
                    isError = !value.isValidFileName()
                },
                labelText = stringResource(R.string.hint_enter_file_name),
                errorText = stringResource(R.string.message_invalid_file_name),
                placeholderText = stringResource(UiR.string.common_untitled),
                error = isError,
            )
        },
        confirmButton = stringResource(R.string.action_rename),
        dismissButton = stringResource(android.R.string.cancel),
        onConfirmClicked = {
            if (fileName.isValidFileName()) {
                onConfirmClicked(fileName)
            } else {
                isError = true
            }
        },
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun RenameScreenPreview() {
    PreviewBackground {
        RenameScreen(
            currentFileName = "untitled"
        )
    }
}