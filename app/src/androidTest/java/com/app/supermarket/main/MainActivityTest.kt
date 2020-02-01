package com.app.supermarket.main

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.app.supermarket.MarketApplication
import com.app.supermarket.R
import com.app.supermarket.di.DaggerAppTestComponent
import com.app.supermarket.di.modules.MockLoginModule
import com.app.supermarket.di.modules.MockNetworkModule
import com.app.supermarket.login.Repository
import com.app.supermarket.mocks.Products
import com.app.supermarket.model.User
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

    private lateinit var mockWebServer: MockWebServer

    private lateinit var intent: Intent

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString()

        val component = DaggerAppTestComponent.builder()
            .mockLoginModule(MockLoginModule(Mockito.mock(Repository::class.java)))
            .mockNetworkModule(MockNetworkModule(baseUrl))
            .build()

        val marketApplication =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MarketApplication
        marketApplication.appComponent = component

        val mockUser = User("Josh Mock", "user@admin.com")

        intent = Intent()
        intent.putExtra(MainActivity.INTENT_KEY_USER, mockUser)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun whenLaunchActivity_shouldDisplayInitialState() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(Products.success))
        rule.launchActivity(intent)

        onView(withId(R.id.tv_welcome)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
    }

    @Test
    fun whenRequestSuccess_shouldPopulateAdapter() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(Products.success))
        rule.launchActivity(intent)

        onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
        onView(withId(R.id.image_view)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_price)).check(matches(isDisplayed()))
    }

    @Test
    fun whenRequestFailed_shouldNoPopulateAdapter_andDisplayToastError() {
        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody("{}"))
        rule.launchActivity(intent)

        onView(isRoot())
            .inRoot(withDecorView(not(`is`(rule.activity.window.decorView))))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tv_name)).check(doesNotExist())
        onView(withId(R.id.image_view)).check(doesNotExist())
        onView(withId(R.id.tv_price)).check(doesNotExist())
    }
}