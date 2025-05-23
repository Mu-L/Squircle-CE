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

package com.blacksquircle.ui.ds.progress

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class CircularProgressSize(
    val circleSize: Dp,
    val strokeWidth: Dp,
)

object CircularProgressSizeDefaults {

    val XS: CircularProgressSize
        get() = CircularProgressSize(
            circleSize = 12.dp,
            strokeWidth = 2.dp,
        )

    val S: CircularProgressSize
        get() = CircularProgressSize(
            circleSize = 28.dp,
            strokeWidth = 3.dp,
        )

    val M: CircularProgressSize
        get() = CircularProgressSize(
            circleSize = 40.dp,
            strokeWidth = 4.dp,
        )
}