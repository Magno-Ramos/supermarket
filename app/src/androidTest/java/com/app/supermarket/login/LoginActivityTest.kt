package com.app.supermarket.login

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import android.widget.EditText
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.app.supermarket.MarketApplication
import com.app.supermarket.R
import com.app.supermarket.di.DaggerAppTestComponent
import com.app.supermarket.di.modules.MockLoginModule
import com.app.supermarket.di.modules.MockNetworkModule
import com.app.supermarket.main.MainActivity
import com.app.supermarket.model.User
import org.hamcrest.Description
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest {

    @get:Rule
    val liveRule = InstantTaskExecutorRule()

    @get:Rule
    val rule = IntentsTestRule<LoginActivity>(LoginActivity::class.java, false, false)

    @Mock
    lateinit var repository: Repository

    private val signInLiveData = MutableLiveData<LoginState?>()
    private val userLiveData = MutableLiveData<User?>()

    private val email = "user_test@admin.com"
    private val password = "adminTest123"
    private val fakeUser = User("Josh Test", email)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        Mockito.`when`(repository.signInStateLiveData).thenReturn(signInLiveData)
        Mockito.`when`(repository.userLiveData).thenReturn(userLiveData)

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val application = (instrumentation.targetContext.applicationContext as MarketApplication)
        val testComponent = DaggerAppTestComponent
            .builder()
            .mockLoginModule(MockLoginModule(repository))
            .mockNetworkModule(MockNetworkModule(("")))
            .build()

        application.appComponent = testComponent
    }

    @Test
    fun whenLaunchActivity_shouldDisplayInitialState() {
        rule.launchActivity(Intent())

        // check visibility
        onView(withId(R.id.edt_email)).check(matches(isDisplayed()))
        onView(withId(R.id.edt_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_sign)).check(matches(isDisplayed()))

        // check email hint text
        onView(withId(R.id.edt_email)).check(matches(withHint(R.string.hint_email)))

        // check password hint text
        onView(withId(R.id.edt_password)).check(matches(withHint(R.string.hint_password)))
    }

    @Test
    fun whenEmailIsEmpty_andClickLoginButton_shouldDisplayError() {
        rule.launchActivity(Intent())

        // actions
        onView(withId(R.id.edt_password)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.edt_email)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.btn_sign)).perform(click())

        // check
        val errorEmail = rule.activity.resources.getString(R.string.error_email)
        onView(withId(R.id.edt_email)).check(matches(hasErrorText(errorEmail)))
        onView(withId(R.id.edt_email)).check(matches(hasFocus()))
    }

    @Test
    fun whenPasswordIsEmpty_andClickLoginButton_shouldDisplayError() {
        rule.launchActivity(Intent())

        // actions
        onView(withId(R.id.edt_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.edt_password)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.btn_sign)).perform(click())

        // check
        val errorMsg = rule.activity.resources.getString(R.string.error_password)
        onView(withId(R.id.edt_password)).check(matches(hasErrorText(errorMsg)))
        onView(withId(R.id.edt_password)).check(matches(hasFocus()))
    }

    @Test
    fun whenEmailAndPasswordAreFilled_andClickLoginButton_checkThatEmailAndPasswordNoHaveErrors() {
        Mockito.`when`(repository.signInWithEmailAndPassword(email, password)).thenAnswer {}

        rule.launchActivity(Intent())

        // actions
        onView(withId(R.id.edt_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.edt_password)).perform(typeText(password), closeSoftKeyboard())

        val activityResult = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        intending(hasExtraWithKey(MainActivity.INTENT_KEY_USER)).respondWith(activityResult)

        onView(withId(R.id.btn_sign)).perform(click())

        // check
        onView(withId(R.id.edt_email))
            .inRoot(withDecorView(`is`(rule.activity.window.decorView)))
            .check(matches(not(hasErrorText(""))))

        onView(withId(R.id.edt_password))
            .inRoot(withDecorView(`is`(rule.activity.window.decorView)))
            .check(matches(not(hasErrorText(""))))
    }

    @Test
    fun whenEmailAndPasswordAreFilled_andClickLoginButton_shouldDisplayProgressDialog() {
        Mockito.`when`(repository.signInWithEmailAndPassword(email, password))
            .thenAnswer { signInLiveData.setValue(LoginState.LOADING) }

        rule.launchActivity(Intent())

        // actions
        onView(withId(R.id.edt_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.edt_password)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.btn_sign)).perform(click())

        // check dialog is open
        onView(withText(R.string.loading))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenLoginFailed_shouldHideDialogAndDisplayToastError() {
        val errorMsg = "Email ou senha inválida"
        val email = "user_error@gmail.com"
        Mockito.`when`(repository.signInWithEmailAndPassword(email, password))
            .thenAnswer { signInLiveData.setValue(LoginState.error(errorMsg)) }

        rule.launchActivity(Intent())

        // actions
        onView(withId(R.id.edt_email))
            .perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.edt_password)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.btn_sign)).perform(click())

        // check dialog is hidden
        onView(withText(R.string.loading)).check(doesNotExist())

        // check toast
        onView(withText(errorMsg))
            .inRoot(withDecorView(not(`is`(rule.activity.window.decorView))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenLoginSuccess_shouldOpenMainActivity() {
        Mockito.`when`(repository.signInWithEmailAndPassword(email, password))
            .thenAnswer {
                signInLiveData.postValue(LoginState.SUCCESS)
                userLiveData.postValue(fakeUser)
            }

        rule.launchActivity(Intent())

        // actions
        onView(withId(R.id.edt_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.edt_password)).perform(typeText(password), closeSoftKeyboard())

        // don't open activity
        val matcher = IntentMatchers.hasExtra(MainActivity.INTENT_KEY_USER, fakeUser)
        val activityResult = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        intending(matcher).respondWith(activityResult)

        // click
        onView(withId(R.id.btn_sign)).perform(click())

        // check intent
        intended(matcher)
    }

    private fun hasErrorText(expected: String): BoundedMatcher<View, EditText> {
        return object : BoundedMatcher<View, EditText>(EditText::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("has error text")
            }

            override fun matchesSafely(item: EditText?): Boolean {
                return item?.error == expected
            }
        }
    }
}