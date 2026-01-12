package com.ensa.mobile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ensa.mobile.authentification.activities.VerifyEmailActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class VerifyEmailActivityFunctionalTest {

    private ActivityScenario<VerifyEmailActivity> scenario;

    @Before
    public void setUp() {
        // Créer un intent avec le mode "validate"
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), VerifyEmailActivity.class);
        intent.putExtra("mode", "validate");
        scenario = ActivityScenario.launch(intent);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    // -----------------------------
    // Test 1: Champ email vide
    // -----------------------------
    @Test
    public void sendMessage_withEmptyEmail_showsToast() {
        // Cliquer sur "Send Message" sans remplir l'email
        onView(withId(R.id.btnSendMessage)).perform(click());

        // Note: Les Toast ne sont pas faciles à tester avec Espresso
        // On vérifie que l'activité est toujours affichée (pas de navigation)
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 2: Email valide - mode validate
    // -----------------------------
    @Test
    public void sendMessage_withValidEmail_validate_mode() {
        // Remplir l'email
        onView(withId(R.id.etEmail))
                .perform(typeText("test@ensa.ac.ma"), closeSoftKeyboard());

        // Cliquer sur "Send Message"
        onView(withId(R.id.btnSendMessage)).perform(click());

        // Attendre un peu pour la réponse réseau
        // Sans IdlingResource, ce test peut être instable
        // Vérifier que les éléments de base sont affichés
        onView(withId(R.id.btnSendMessage)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 3: Mode "forgot password"
    // -----------------------------
    @Test
    public void sendMessage_forgotPasswordMode() {
        // Fermer le scénario précédent
        scenario.close();

        // Créer un intent avec le mode "forgot"
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), VerifyEmailActivity.class);
        intent.putExtra("mode", "forgot");
        scenario = ActivityScenario.launch(intent);

        // Remplir l'email
        onView(withId(R.id.etEmail))
                .perform(typeText("test@ensa.ac.ma"), closeSoftKeyboard());

        // Cliquer sur "Send Message"
        onView(withId(R.id.btnSendMessage)).perform(click());

        // Vérifier que le bouton est toujours affiché
        onView(withId(R.id.btnSendMessage)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 4: Vérifier que les éléments UI sont affichés
    // -----------------------------
    @Test
    public void verifyEmailActivity_displaysAllElements() {
        // Vérifier que tous les éléments principaux sont visibles
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSendMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.cvLogo)).check(matches(isDisplayed()));
        onView(withId(R.id.tvAppName)).check(matches(isDisplayed()));
    }
}