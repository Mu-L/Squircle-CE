/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.plugin.shortcuts

import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import com.blacksquircle.ui.plugin.base.EditorPlugin

class ShortcutsPlugin : EditorPlugin(PLUGIN_ID) {

    var onShortcutListener: OnShortcutListener? = null

    override fun onAttached(editText: EditText) {
        super.onAttached(editText)
        Log.d(PLUGIN_ID, "Shortcuts plugin loaded successfully!")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && onShortcutListener != null) {
            val shortcut = Shortcut(
                ctrl = event.isCtrlPressed,
                shift = event.isShiftPressed,
                alt = event.isAltPressed,
                keyCode = keyCode
            )

            // Shortcuts can be handled only if one of these keys is pressed
            if (shortcut.ctrl || shortcut.shift || shortcut.alt) {
                if (onShortcutListener!!.onShortcut(shortcut)) {
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        const val PLUGIN_ID = "shortcuts-1095"
    }
}