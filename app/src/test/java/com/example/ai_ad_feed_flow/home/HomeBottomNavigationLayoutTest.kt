package com.example.ai_ad_feed_flow.home

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.w3c.dom.Element

class HomeBottomNavigationLayoutTest {
    @Test
    fun bottomNavigationUsesCompactInsetLayout() {
        val document = DocumentBuilderFactory.newInstance()
            .apply { isNamespaceAware = true }
            .newDocumentBuilder()
            .parse(activityMainLayoutFile())

        val bottomNavigation = document.getElementsByTagName(
            "com.google.android.material.bottomnavigation.BottomNavigationView"
        ).item(0) as Element

        assertEquals("@dimen/home_bottom_nav_height", bottomNavigation.androidAttribute("layout_height"))
        assertEquals("@dimen/home_bottom_nav_horizontal_margin", bottomNavigation.androidAttribute("layout_marginStart"))
        assertEquals("@dimen/home_bottom_nav_horizontal_margin", bottomNavigation.androidAttribute("layout_marginEnd"))
        assertEquals(
            "@style/Widget.Aiadfeedflow.BottomNavigationActiveIndicator",
            bottomNavigation.appAttribute("itemActiveIndicatorStyle")
        )
    }

    private fun activityMainLayoutFile(): File {
        return listOf(
            File("src/main/res/layout/activity_main.xml"),
            File("app/src/main/res/layout/activity_main.xml")
        ).first { it.exists() }
    }

    private fun Element.androidAttribute(name: String): String {
        return getAttributeNS(ANDROID_NAMESPACE, name)
    }

    private fun Element.appAttribute(name: String): String {
        return getAttributeNS(APP_NAMESPACE, name)
    }

    companion object {
        private const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
        private const val APP_NAMESPACE = "http://schemas.android.com/apk/res-auto"
    }
}
