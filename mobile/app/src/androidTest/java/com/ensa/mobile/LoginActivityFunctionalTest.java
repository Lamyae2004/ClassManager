package com.ensa.mobile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ensa.mobile.authentification.activities.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityFunctionalTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    private IdlingResource idlingResource;

    @Before
    public void setUp() {
        // Enregistrer l'IdlingResource si vous en avez un
        activityRule.getScenario().onActivity(activity -> {
            idlingResource = activity.getIdlingResource();
            if (idlingResource != null) {
                IdlingRegistry.getInstance().register(idlingResource);
            }
        });
    }

    @After
    public void tearDown() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }

    // -----------------------------
    // Test 1: Login avec champs vides
    // -----------------------------
    @Test
    public void loginEmptyFields_showsErrorMessage() {
        // Cliquer sur le bouton login sans remplir les champs
        onView(withId(R.id.btnLogin)).perform(click());

        // Vérifier que le message d'erreur s'affiche
        onView(withId(R.id.tvError))
                .check(matches(isDisplayed()))
                .check(matches(withText("All fields are required!")));
    }

    // -----------------------------
    // Test 2: Login avec mauvais credentials
    // -----------------------------
    @Test
    public void loginWrongCredentials_showsErrorMessage() {
        // Remplir avec des credentials incorrects
        onView(withId(R.id.etEmail))
                .perform(typeText("wrong@mail.com"), closeSoftKeyboard());
        onView(withId(R.id.etPassword))
                .perform(typeText("wrongpass"), closeSoftKeyboard());

        // Cliquer sur login
        onView(withId(R.id.btnLogin)).perform(click());

        // Attendre que la requête réseau se termine
        // Note: Sans IdlingResource, vous devrez peut-être augmenter le timeout d'Espresso
        // ou utiliser un mock du service API

        // Vérifier que le message d'erreur s'affiche
        onView(withId(R.id.tvError))
                .check(matches(isDisplayed()));

        // Le message peut varier selon la réponse du serveur
        // Vous pouvez vérifier qu'il contient certains mots-clés
    }

    // -----------------------------
    // Test 3: Login correct
    // -----------------------------
    @Test
    public void loginCorrect_navigatesToMainActivity() {
        // Remplir avec des credentials corrects
        onView(withId(R.id.etEmail))
                .perform(typeText("inassemossalli2004@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.etPassword))
                .perform(typeText("inasse2004"), closeSoftKeyboard());

        // Cliquer sur login
        onView(withId(R.id.btnLogin)).perform(click());

        // Attendre que l'authentification et la navigation se terminent
        // Avec IdlingResource, Espresso attendra automatiquement
        // Sans IdlingResource, ce test peut échouer

        // Vérifier que nous sommes sur MainActivity
        onView(withId(R.id.drawerLayout))
                .check(matches(isDisplayed()));
    }
}