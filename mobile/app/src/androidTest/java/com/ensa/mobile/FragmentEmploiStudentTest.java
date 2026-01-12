package com.ensa.mobile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ensa.mobile.authentification.activities.LoginActivity;
import com.ensa.mobile.utils.TokenManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fonctionnels pour l'emploi du temps - Vue ÉTUDIANT
 */
@RunWith(AndroidJUnit4.class)
public class FragmentEmploiStudentTest {

    private IdlingResource idlingResource;

    private static final String STUDENT_EMAIL = "essafi.nourelhouda6@gmail.com";
    private static final String STUDENT_PASSWORD = "essafi2003";

    // =============================
    // SETUP
    // =============================

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TokenManager.getInstance(context).clearToken();

        ActivityScenario<LoginActivity> scenario =
                ActivityScenario.launch(LoginActivity.class);

        scenario.onActivity(activity -> {
            idlingResource = activity.getIdlingResource();
            if (idlingResource != null) {
                IdlingRegistry.getInstance().register(idlingResource);
            }
        });

        loginAsStudent();
    }

    @After
    public void tearDown() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TokenManager.getInstance(context).clearToken();
    }

    // =============================
    // UTILS
    // =============================

    private void loginAsStudent() {
        onView(withId(R.id.etEmail))
                .perform(typeText(STUDENT_EMAIL), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(typeText(STUDENT_PASSWORD), closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());
    }

    private void swipeLeftTimes(int times) {
        for (int i = 0; i < times; i++) {
            onView(withId(R.id.viewPager)).perform(swipeLeft());
        }
    }

    private void swipeRightTimes(int times) {
        for (int i = 0; i < times; i++) {
            onView(withId(R.id.viewPager)).perform(swipeRight());
        }
    }

    // =============================
    // TESTS
    // =============================

    @Test
    public void displaysBasicElements() {
        onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
    }

    @Test
    public void canSwipeBetweenDays() {
        swipeLeftTimes(1);
        swipeRightTimes(1);

        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
    }

    @Test
    public void canSwipeThroughAllDays() {
        swipeLeftTimes(4);
        swipeRightTimes(4);

        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
    }

    @Test
    public void loadsStudentSchedule() {
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
    }
}
