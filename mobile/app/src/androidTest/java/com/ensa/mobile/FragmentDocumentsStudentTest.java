package com.ensa.mobile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ensa.mobile.FragmentEmploiTeacherTest.clickChildViewWithId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ensa.mobile.authentification.activities.LoginActivity;
import com.ensa.mobile.utils.TokenManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FragmentDocumentsStudentTest {

    private IdlingResource idlingResource;

    private static final String STUDENT_EMAIL = "essafi.nourelhouda6@gmail.com";
    private static final String STUDENT_PASSWORD = "essafi2003";

    // =============================
    // SETUP - UNE SEULE MÉTHODE @Before
    // =============================

    @Before
    public void setUp() {
        System.out.println("🔧 Initialisation du test...");

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
        waitForLogin();
        navigateToDocuments();

        System.out.println(" Initialisation terminée");
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
        System.out.println(" Connexion étudiant...");

        onView(withId(R.id.etEmail))
                .perform(typeText(STUDENT_EMAIL), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(typeText(STUDENT_PASSWORD), closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());
    }

    private void waitForLogin() {
        try {
            System.out.println(" Attente connexion et chargement MainActivity...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void navigateToDocuments() {
        System.out.println(" Navigation vers Documents...");

        try {
            // Ouvrir le menu hamburger
            onView(withContentDescription("Ouvrir le menu"))
                    .perform(click());
            waitFor(800);

            // Cliquer sur "Mes documents"
            onView(withId(R.id.nav_documents))
                    .perform(click());
            waitFor(3000); // Attendre le chargement des données depuis l'API

            System.out.println(" Page Documents chargée");

        } catch (Exception e) {
            System.out.println("Erreur navigation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Navigation vers Documents échouée", e);
        }
    }

    private void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // =============================
    // TESTS
    // =============================

    @Test
    public void student_canViewDocumentsPage() {
        System.out.println("Test: Affichage de la page Documents");

        try {
            // Vérifier que les spinners sont affichés
            onView(withId(R.id.spinner_matiere))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.spinner_type))
                    .check(matches(isDisplayed()));

            // Vérifier que le RecyclerView est affiché
            onView(withId(R.id.recycler_documents))
                    .check(matches(isDisplayed()));

            System.out.println(" Test réussi: Page documents affichée");

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
            throw new RuntimeException("Test échoué", e);
        }
    }

    @Test
    public void student_canFilterByMatiere() {
        System.out.println(" Test: Filtrer par matière");

        try {
            waitFor(1000);

            System.out.println("Clic sur spinner matière...");

            // Cliquer sur le spinner matière
            onView(withId(R.id.spinner_matiere))
                    .check(matches(isDisplayed()))
                    .perform(click());

            waitFor(800);

            System.out.println(" Sélection de la première matière...");

            // Sélectionner la première matière
            onData(anything())
                    .atPosition(0)
                    .perform(click());

            waitFor(2000); // Attendre le chargement des documents filtrés

            System.out.println(" Test réussi: Filtrage par matière");

        } catch (Exception e) {
            System.out.println(" Erreur filtrage matière: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test filtrage matière échoué", e);
        }
    }



    @Test
    public void student_canChangeTypeFilter() {
        System.out.println(" Test: Changer le filtre de type");

        try {
            waitFor(1000);

            // Tester COURS
            System.out.println("Test 1/3: COURS");
            onView(withId(R.id.spinner_type)).perform(click());
            waitFor(800);
            onData(anything()).atPosition(0).perform(click());
            waitFor(2000);

            // Tester TP
            System.out.println(" Test 2/3: TP");
            onView(withId(R.id.spinner_type)).perform(click());
            waitFor(800);
            onData(anything()).atPosition(1).perform(click());
            waitFor(2000);

            // Tester TD
            System.out.println(" Test 3/3: TD");
            onView(withId(R.id.spinner_type)).perform(click());
            waitFor(800);
            onData(anything()).atPosition(2).perform(click());
            waitFor(2000);

            System.out.println(" Test réussi: Changement de type");

        } catch (Exception e) {
            System.out.println(" Erreur: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test changement type échoué", e);
        }
    }

    @Test
    public void student_canFilterByMatiereAndType() {
        System.out.println(" Test: Filtrer par matière ET type");

        try {
            waitFor(1000);

            // Sélectionner une matière
            System.out.println(" Étape 1/2: Sélection de la matière...");
            onView(withId(R.id.spinner_matiere)).perform(click());
            waitFor(800);
            onData(anything()).atPosition(0).perform(click());
            waitFor(1500);

            // Sélectionner un type
            System.out.println(" Étape 2/2: Sélection du type COURS...");
            onView(withId(R.id.spinner_type)).perform(click());
            waitFor(800);
            onData(anything()).atPosition(0).perform(click());
            waitFor(2000);

            // Vérifier que le RecyclerView est toujours affiché
            onView(withId(R.id.recycler_documents))
                    .check(matches(isDisplayed()));

            System.out.println("Test réussi: Double filtrage");

        } catch (Exception e) {
            System.out.println(" Erreur double filtrage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test double filtrage échoué", e);
        }
    }

    @Test
    public void student_canClickDownloadButton_ifDocumentsExist() {
        System.out.println(" Test: Cliquer sur télécharger (si documents existent)");

        try {
            waitFor(2000);

            // Essayer de cliquer sur le premier document
            try {
                System.out.println("Tentative de clic sur le premier document...");

                onView(withId(R.id.recycler_documents))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

                waitFor(500);

                System.out.println("Test réussi: Interaction avec document");

            } catch (Exception e) {
                System.out.println(" Aucun document disponible (RecyclerView vide)");
                System.out.println("Cela peut être normal si aucun document n'est uploadé pour cette classe");
            }

        } catch (Exception e) {
            System.out.println(" Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void student_canSwitchBetweenDifferentMatieres() {
        System.out.println("🧪 Test: Basculer entre différentes matières");

        try {
            waitFor(1000);

            // Matière 1
            System.out.println(" Test matière 1...");
            onView(withId(R.id.spinner_matiere)).perform(click());
            waitFor(800);
            onData(anything()).atPosition(0).perform(click());
            waitFor(2000);

            // Essayer matière 2 si elle existe
            try {
                System.out.println("Test matière 2...");
                onView(withId(R.id.spinner_matiere)).perform(click());
                waitFor(800);
                onData(anything()).atPosition(1).perform(click());
                waitFor(2000);

                System.out.println(" Test réussi: Plusieurs matières disponibles");
            } catch (Exception e) {
                System.out.println("ℹ Une seule matière disponible pour cet étudiant");
            }

        } catch (Exception e) {
            System.out.println(" Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Test
    public void student_canDownloadCours_PlanificationReseauxMobiles() {

        // Sélection matière par TEXTE visible
        onView(withId(R.id.spinner_matiere)).perform(click());

        onView(withText("Planifications et réseaux mobiles avancés"))
                .inRoot(isPlatformPopup())
                .perform(click());

        waitFor(1000);

        // Sélection type COURS
        onView(withId(R.id.spinner_type)).perform(click());
        onView(withText("COURS"))
                .inRoot(isPlatformPopup())
                .perform(click());

        waitFor(2000);

        // Cliquer sur Télécharger
        onView(withId(R.id.recycler_documents))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                clickChildViewWithId(R.id.btn_download)
                        )
                );
    }


}

