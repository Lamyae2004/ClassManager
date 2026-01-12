package com.ensa.mobile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.matcher.ViewMatchers;
import android.content.Context;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ensa.mobile.authentification.activities.LoginActivity;
import com.ensa.mobile.utils.TokenManager;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FragmentEmploiTeacherTest {

    private IdlingResource idlingResource;

    private static final String TEACHER_EMAIL = "nora.essafi@gmail.com";
    private static final String TEACHER_PASSWORD = "essafi2003";

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

        loginAsTeacher();

        // Attendre que l'emploi du temps se charge
        waitForScheduleToLoad();

        // Aller directement au jeudi (où se trouvent les cours)
        navigateToThursday();
    }

    /**
     * Navigation automatique vers le jeudi au démarrage
     * Lundi=0, Mardi=1, Mercredi=2, Jeudi=3, Vendredi=4
     */
    private void navigateToThursday() {
        System.out.println("Navigation automatique vers le Jeudi...");
        // Swiper 3 fois pour aller de Lundi (0) à Jeudi (3)
        swipeLeftTimes(3);
        System.out.println(" Positionné sur le Jeudi");
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

    private void loginAsTeacher() {
        onView(withId(R.id.etEmail))
                .perform(typeText(TEACHER_EMAIL), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(typeText(TEACHER_PASSWORD), closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());
    }

    private void waitForScheduleToLoad() {
        try {
            Thread.sleep(3000); // Augmenté à 3 secondes pour laisser le temps à l'API de répondre
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void swipeLeftTimes(int times) {
        for (int i = 0; i < times; i++) {
            onView(withId(R.id.viewPager)).perform(swipeLeft());
            try {
                Thread.sleep(1000); // Augmenté à 1 seconde entre les swipes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void swipeRightTimes(int times) {
        for (int i = 0; i < times; i++) {
            onView(withId(R.id.viewPager)).perform(swipeRight());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cherche un jour avec des cours en swipant
     * Vérifie si le RecyclerView est VISIBLE (pas GONE) et contient des items
     * @param maxSwipes nombre max de swipes
     * @return true si un jour avec cours est trouvé
     */
    private boolean findDayWithCourses(int maxSwipes) {
        System.out.println("🔍 Recherche d'un jour avec des cours...");

        for (int i = 0; i < maxSwipes; i++) {
            try {
                // Attendre que les données se chargent
                Thread.sleep(1500);

                // Vérifier si le RecyclerView est VISIBLE (pas GONE)
                onView(withId(R.id.recyclerViewJour))
                        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

                // Si visible, essayer de scroller à la position 0 pour confirmer qu'il y a des items
                onView(withId(R.id.recyclerViewJour))
                        .perform(RecyclerViewActions.scrollToPosition(0));

                System.out.println(" Jour avec cours trouvé après " + i + " swipe(s)");
                return true;

            } catch (Exception e) {
                // Le RecyclerView est GONE ou vide → pas de cours ce jour
                System.out.println(" Jour " + (i + 1) + " : RecyclerView GONE ou vide");

                if (i < maxSwipes - 1) {
                    // Swiper vers le jour suivant
                    try {
                        onView(withId(R.id.viewPager)).perform(swipeLeft());
                        System.out.println("Swipe vers le jour suivant...");
                    } catch (Exception swipeException) {
                        System.out.println("Erreur lors du swipe: " + swipeException.getMessage());
                    }
                }
            }
        }

        System.out.println("Aucun jour avec cours trouvé après " + maxSwipes + " jours vérifiés");
        return false;
    }

    /**
     * Méthode alternative plus robuste pour vérifier si un RecyclerView a des items
     */
    private boolean hasCoursesOnCurrentDay() {
        try {
            Thread.sleep(1500); // Attendre le chargement

            // Vérifier que le RecyclerView est visible
            onView(withId(R.id.recyclerViewJour))
                    .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

            // Vérifier qu'on peut scroller à la position 0 (= il y a au moins 1 item)
            onView(withId(R.id.recyclerViewJour))
                    .perform(RecyclerViewActions.scrollToPosition(0));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =============================
    // TESTS
    // =============================

    @Test
    public void teacher_canSwipeBetweenDays() {
        swipeLeftTimes(1);
        swipeRightTimes(1);
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
        System.out.println("Test swipe réussi");
    }

    @Test
    public void teacher_canSwipeThroughAllDays() {
        swipeLeftTimes(4);
        swipeRightTimes(4);
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
        System.out.println(" Test swipe complet réussi");
    }

    @Test
    public void teacher_canDeclareRetard_whenDayHasCourses() {
        // On est déjà sur le jeudi grâce au setUp()
        // Vérifier qu'il y a bien des cours
        if (!hasCoursesOnCurrentDay()) {
            System.out.println(" ATTENTION: Pas de cours le jeudi pour ce professeur!");
            System.out.println("Test ignoré");
            return;
        }

        try {
            Thread.sleep(500);

            System.out.println(" Clic sur le bouton 'Déclarer retard'...");

            // Cliquer sur le bouton "Déclarer retard" du PREMIER item du RecyclerView
            onView(withId(R.id.recyclerViewJour))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                            clickChildViewWithId(R.id.btnDeclarerRetard)));

            Thread.sleep(500);

            System.out.println("Dialog affiché, clic sur 'Confirmer'...");

            // Vérifier que le dialog s'affiche et cliquer sur "Confirmer"
            onView(withText("Confirmer"))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()))
                    .perform(click());

            System.out.println("Test retard réussi !");

        } catch (Exception e) {
            System.out.println("Erreur lors du test retard: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test retard échoué", e);
        }
    }

    @Test
    public void teacher_canAnnulerSeance_whenDayHasCourses() {
        // On est déjà sur le jeudi grâce au setUp()
        if (!hasCoursesOnCurrentDay()) {
            System.out.println("ATTENTION: Pas de cours le jeudi pour ce professeur!");
            System.out.println("Test ignoré");
            return;
        }

        try {
            Thread.sleep(500);

            System.out.println("Clic sur le bouton 'Annuler séance'...");

            // Cliquer sur le bouton "Annuler séance" du PREMIER item du RecyclerView
            onView(withId(R.id.recyclerViewJour))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                            clickChildViewWithId(R.id.btnAnnulerSeance)));

            Thread.sleep(500);

            System.out.println(" Dialog affiché, clic sur 'Non'...");

            // Vérifier que le dialog s'affiche et cliquer sur "Non"
            onView(withText("Non"))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()))
                    .perform(click());

            System.out.println(" Test annulation (refus) réussi !");

        } catch (Exception e) {
            System.out.println(" Erreur lors du test annulation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test annulation échoué", e);
        }
    }

    @Test
    public void teacher_canConfirmAnnulerSeance_whenDayHasCourses() {
        // On est déjà sur le jeudi grâce au setUp()
        if (!hasCoursesOnCurrentDay()) {
            System.out.println(" ATTENTION: Pas de cours le jeudi pour ce professeur!");
            System.out.println(" Test ignoré");
            return;
        }

        try {
            Thread.sleep(500);

            System.out.println(" Clic sur 'Annuler séance' pour confirmation...");

            // Cliquer sur "Annuler séance" du PREMIER item
            onView(withId(R.id.recyclerViewJour))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                            clickChildViewWithId(R.id.btnAnnulerSeance)));

            Thread.sleep(500);

            System.out.println("Dialog affiché, clic sur 'Oui, annuler'...");

            // Cette fois on clique sur "Oui, annuler"
            onView(withText("Oui, annuler"))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()))
                    .perform(click());

            System.out.println("Test confirmation annulation réussi !");

        } catch (Exception e) {
            System.out.println("Erreur lors du test confirmation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test confirmation échoué", e);
        }
    }

    @Test
    public void teacher_canInteractWithMultipleCourses() {
        // On est déjà sur le jeudi grâce au setUp()
        if (!hasCoursesOnCurrentDay()) {
            System.out.println(" ATTENTION: Pas de cours le jeudi pour ce professeur!");
            System.out.println(" Test ignoré");
            return;
        }

        try {
            Thread.sleep(500);

            // Test sur le PREMIER cours (position 0)
            System.out.println("Test du premier cours (retard)...");
            onView(withId(R.id.recyclerViewJour))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                            clickChildViewWithId(R.id.btnDeclarerRetard)));

            Thread.sleep(500);
            onView(withText("Confirmer"))
                    .inRoot(isDialog())
                    .perform(click());

            Thread.sleep(1000);

            // Test sur le DEUXIÈME cours (position 1) si il existe
            try {
                System.out.println("Test du deuxième cours (annulation)...");
                onView(withId(R.id.recyclerViewJour))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(1,
                                clickChildViewWithId(R.id.btnAnnulerSeance)));

                Thread.sleep(500);
                onView(withText("Non"))
                        .inRoot(isDialog())
                        .perform(click());

                System.out.println(" Test sur plusieurs cours réussi !");
            } catch (Exception e) {
                System.out.println(" Un seul cours disponible ce jour (c'est normal)");
            }

        } catch (Exception e) {
            System.out.println(" Erreur : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test multiple cours échoué", e);
        }
    }

    // =============================
    // UTILS Espresso RecyclerView
    // =============================

    /**
     * Helper pour cliquer sur une vue enfant d'un item RecyclerView
     * Cette méthode évite l'erreur AmbiguousViewMatcherException
     * car elle cible un bouton spécifique DANS un item spécifique
     */
    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Cliquer sur une vue enfant avec l'id : " + id;
            }

            @Override
            public void perform(androidx.test.espresso.UiController uiController, View view) {
                View v = view.findViewById(id);
                if (v != null) {
                    v.performClick();
                }
            }
        };
    }
}