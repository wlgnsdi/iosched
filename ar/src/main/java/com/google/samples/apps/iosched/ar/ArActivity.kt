/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.iosched.ar

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.web.webview.ArWebView
import com.google.samples.apps.iosched.domain.ar.ArConstants

class ArActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pinnedSessionsJson =
            intent?.extras?.getString(ArConstants.PINNED_SESSIONS_JSON_KEY, "") ?: ""
        val canSignedInUserDemoAr =
            intent?.extras?.getBoolean(ArConstants.CAN_SIGNED_IN_USER_DEMO_AR, false) ?: false

        val arWebView = ArWebView(this)
        setContentView(arWebView)
        arWebView.apply {
            webView.webViewClient = ArWebViewClient(pinnedSessionsJson, canSignedInUserDemoAr)
            webView.settings.mediaPlaybackRequiresUserGesture = false
            // Loading a single entry point because all the user flow happens in JavaScript from the
            // teaser page and requesting ARCore apk and camera permission
            loadUrl("https://sp-io2019.appspot.com/")
        }
    }

    /**
     * WebViewClient that sends the pinned sessions as json to the WebView.
     * Defining it as a class otherwise an anonymous class was stripped from proguard.
     */
    private class ArWebViewClient(val json: String, val canDemoAr: Boolean) : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val evalAgendaScript =
                "if (window.app && window.app.sendIOAppUserAgenda) " +
                        "window.app.sendIOAppUserAgenda('$json');"
            view?.evaluateJavascript(evalAgendaScript) {}
            if (canDemoAr) {
                val evalSetDebugUserScript = "if (window.app && window.app.setDebugUser) " +
                        "window.app.setDebugUser()"
                view?.evaluateJavascript(evalSetDebugUserScript) {}
            }
        }
    }
}