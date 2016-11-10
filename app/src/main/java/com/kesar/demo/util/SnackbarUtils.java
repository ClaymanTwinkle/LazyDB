/*
 * Copyright [2016] [Clayman Twinkle]
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

package com.kesar.demo.util;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Snackbar提示工具
 * Created by kesar on 2016/10/29 0029.
 */
public final class SnackbarUtils {
    public static void show(View view, @StringRes int stringId)
    {
        if(view==null) return;
        Snackbar.make(view, stringId, Snackbar.LENGTH_SHORT).show();
    }

    public static void show(View view, String text)
    {
        if (view == null || text == null) return;
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    public static void showLong(View view,@StringRes int stringId)
    {
        if(view==null) return;
        Snackbar.make(view, stringId, Snackbar.LENGTH_LONG).show();
    }

    public static void showLong(View view, String text)
    {
        if (view == null || text == null) return;
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }
}
