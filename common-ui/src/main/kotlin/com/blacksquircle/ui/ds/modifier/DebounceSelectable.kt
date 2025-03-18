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

package com.blacksquircle.ui.ds.modifier

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role

fun Modifier.debounceSelectable(
    selected: Boolean,
    role: Role? = null,
    enabled: Boolean = true,
    debounceMs: Long = DefaultMs,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "debounceSelectable"
        properties["selected"] = selected
        properties["role"] = role
        properties["enabled"] = enabled
        properties["debounceMs"] = debounceMs
        properties["onClick"] = onClick
    }
) {
    val localIndication = LocalIndication.current
    val interactionSource = if (localIndication is IndicationNodeFactory) {
        // We can fast path here as it will be created inside clickable lazily
        null
    } else {
        // We need an interaction source to pass between the indication modifier and clickable, so
        // by creating here we avoid another composed down the line
        remember { MutableInteractionSource() }
    }
    Modifier.debounceSelectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = localIndication,
        role = role,
        enabled = enabled,
        debounceMs = debounceMs,
        onClick = onClick
    )
}

fun Modifier.debounceSelectable(
    selected: Boolean,
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    role: Role? = null,
    enabled: Boolean = true,
    debounceMs: Long = DefaultMs,
    onClick: (() -> Unit)? = null,
): Modifier {
    val onClickLambda = onClick?.let { debounceLambda(it, debounceMs) }
    if (onClickLambda == null) {
        return this
    }
    return this.selectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = indication,
        role = role,
        enabled = enabled,
        onClick = onClickLambda,
    )
}