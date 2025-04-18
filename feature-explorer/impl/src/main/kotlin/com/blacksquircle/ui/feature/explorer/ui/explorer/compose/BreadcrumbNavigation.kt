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

package com.blacksquircle.ui.feature.explorer.ui.explorer.compose

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ScrollableTabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.tabs.TabIndicator

@Composable
internal fun BreadcrumbNavigation(
    tabs: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIndex: Int = -1,
    onHomeClicked: () -> Unit = {},
    homeIcon: Int? = R.drawable.ic_home,
    actionIcon: Int? = R.drawable.ic_plus,
    onActionClicked: () -> Unit = {},
) {
    Box(modifier) {
        Row(Modifier.zIndex(1f)) {
            if (homeIcon != null) {
                IconButton(
                    iconResId = homeIcon,
                    iconButtonSize = IconButtonSizeDefaults.XS,
                    onClick = onHomeClicked,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }

            if (selectedIndex > -1) {
                ScrollableTabRow(
                    backgroundColor = Color.Unspecified,
                    selectedTabIndex = selectedIndex,
                    indicator = { tabPositions ->
                        val tabIconSize = 24.dp
                        val currentTabPosition = tabPositions.getOrElse(selectedIndex) {
                            tabPositions.getOrElse(selectedIndex - 1) {
                                tabPositions[0]
                            }
                        }
                        val currentTabWidth by animateDpAsState(
                            targetValue = currentTabPosition.width - tabIconSize,
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = FastOutSlowInEasing
                            )
                        )
                        val indicatorOffset by animateDpAsState(
                            targetValue = currentTabPosition.left,
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = FastOutSlowInEasing
                            )
                        )
                        TabIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.BottomStart)
                                .offset {
                                    IntOffset(
                                        x = indicatorOffset.roundToPx(),
                                        y = 0,
                                    )
                                }
                                .width(currentTabWidth)
                        )
                    },
                    edgePadding = 0.dp,
                    divider = {},
                    tabs = tabs,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                )
            } else {
                Spacer(
                    Modifier
                        .weight(1f)
                        .height(36.dp)
                )
            }

            if (actionIcon != null) {
                IconButton(
                    iconResId = actionIcon,
                    iconButtonSize = IconButtonSizeDefaults.XS,
                    onClick = onActionClicked,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }

        HorizontalDivider(Modifier.align(Alignment.BottomCenter))
    }
}

@PreviewLightDark
@Composable
private fun BreadcrumbNavigationPreview() {
    PreviewBackground {
        BreadcrumbNavigation(
            selectedIndex = 0,
            tabs = {
                Breadcrumb(
                    title = "Hello World",
                    selected = true,
                    onClick = {}
                )
                Breadcrumb(
                    title = "Hello World",
                    selected = false,
                    onClick = {}
                )
            },
            onHomeClicked = {},
            onActionClicked = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}