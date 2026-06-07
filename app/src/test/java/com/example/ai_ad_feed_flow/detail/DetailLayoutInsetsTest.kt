package com.example.ai_ad_feed_flow.detail

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

class DetailLayoutInsetsTest {
    @Test
    fun detailAppBarOwnsTopInsetWithoutRootFitsSystemWindows() {
        val document = DocumentBuilderFactory.newInstance()
            .apply { isNamespaceAware = true }
            .newDocumentBuilder()
            .parse(detailLayoutFile())

        val root = document.documentElement
        val appBar = document
            .getElementsByTagName("com.google.android.material.appbar.AppBarLayout")
            .item(0) as Element

        assertTrue(root.androidAttribute("fitsSystemWindows").isBlank())
        assertTrue(appBar.androidAttribute("fitsSystemWindows").isBlank())
        assertEquals("@color/feed_surface", appBar.androidAttribute("background"))
    }

    private fun detailLayoutFile(): File {
        return listOf(
            File("src/main/res/layout/activity_detail.xml"),
            File("app/src/main/res/layout/activity_detail.xml")
        ).first { it.exists() }
    }

    private fun Element.androidAttribute(name: String): String {
        return getAttributeNS(ANDROID_NAMESPACE, name)
    }

    companion object {
        private const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
    }
}
