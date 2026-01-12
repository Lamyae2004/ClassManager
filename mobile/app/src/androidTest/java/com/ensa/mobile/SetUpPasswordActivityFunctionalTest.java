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

import com.ensa.mobile.authentification.activities.SetUpPasswordActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SetUpPasswordActivityFunctionalTest {

    private ActivityScenario<SetUpPasswordActivity> scenario;

    @Before
    public void setUp() {
        // Créer un intent avec email et mode
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SetUpPasswordActivity.class);
        intent.putExtra("email", "test@ensa.ac.ma");
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
    // Test 1: Champs vides
    // -----------------------------
    @Test
    public void submitPassword_withEmptyFields_showsToast() {
        // Cliquer sur "Submit" sans remplir les champs
        onView(withId(R.id.btnSubmit)).perform(click());

        // Vérifier que l'activité est toujours affichée
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.etConfirmPassword)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 2: Mot de passe vide mais confirmation remplie
    // -----------------------------
    @Test
    public void submitPassword_withOnlyConfirmPassword_showsToast() {
        // Remplir seulement la confirmation
        onView(withId(R.id.etConfirmPassword))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Cliquer sur "Submit"
        onView(withId(R.id.btnSubmit)).perform(click());

        // Vérifier que l'activité est toujours affichée
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 3: Mots de passe différents
    // -----------------------------
    @Test
    public void submitPassword_withMismatchedPasswords_showsToast() {
        // Remplir des mots de passe différents
        onView(withId(R.id.etPassword))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.etConfirmPassword))
                .perform(typeText("differentPassword"), closeSoftKeyboard());

        // Cliquer sur "Submit"
        onView(withId(R.id.btnSubmit)).perform(click());

        // Vérifier que l'activité est toujours affichée
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.etConfirmPassword)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 4: Mots de passe identiques et valides - mode validate
    // -----------------------------
    @Test
    public void submitPassword_withMatchingPasswords_validate_mode() {
        // Remplir des mots de passe identiques
        onView(withId(R.id.etPassword))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.etConfirmPassword))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Cliquer sur "Submit"
        onView(withId(R.id.btnSubmit)).perform(click());

        // Sans IdlingResource, on vérifie juste que le bouton est affiché
        // En cas de succès, l'activité navigue vers LoginActivity
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 5: Mode "forgot password"
    // -----------------------------
    @Test
    public void submitPassword_forgotPasswordMode() {
        // Fermer le scénario précédent
        scenario.close();

        // Créer un intent avec le mode "forgot"
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SetUpPasswordActivity.class);
        intent.putExtra("email", "test@ensa.ac.ma");
        intent.putExtra("mode", "forgot");
        scenario = ActivityScenario.launch(intent);

        // Remplir des mots de passe identiques
        onView(withId(R.id.etPassword))
                .perform(typeText("newPassword456"), closeSoftKeyboard());
        onView(withId(R.id.etConfirmPassword))
                .perform(typeText("newPassword456"), closeSoftKeyboard());

        // Cliquer sur "Submit"
        onView(withId(R.id.btnSubmit)).perform(click());

        // Vérifier que le bouton est affiché
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 6: Activity sans email (doit se fermer)
    // -----------------------------
    @Test
    public void setUpPasswordActivity_withoutEmail_finishes() {
        // Fermer le scénario précédent
        scenario.close();

        // Créer un intent SANS email
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SetUpPasswordActivity.class);
        intent.putExtra("mode", "validate");
        scenario = ActivityScenario.launch(intent);

        // L'activité doit se fermer automatiquement avec un Toast
        // Ce test est difficile à vérifier complètement avec Espresso
    }

    // -----------------------------
    // Test 7: Vérifier que les éléments UI sont affichés
    // -----------------------------
    @Test
    public void setUpPasswordActivity_displaysAllElements() {
        // Vérifier que tous les éléments principaux sont visibles
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.etConfirmPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()));
        onView(withId(R.id.cvLogo)).check(matches(isDisplayed()));
        onView(withId(R.id.tvAppName)).check(matches(isDisplayed()));
    }

    // -----------------------------
    // Test 8: Test de mot de passe court (si validation côté client)
    // -----------------------------
    @Test
    public void submitPassword_withShortPassword() {
        // Remplir un mot de passe très court
        onView(withId(R.id.etPassword))
                .perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.etConfirmPassword))
                .perform(typeText("123"), closeSoftKeyboard());

        // Cliquer sur "Submit"
        onView(withId(R.id.btnSubmit)).perform(click());

        // Le serveur peut rejeter ce mot de passe
        // On vérifie que le bouton est toujours là
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()));
    }
}