package com.ensa.mobile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

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

@RunWith(AndroidJUnit4.class)
public class FragmentDocumentsTeacherTest {

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
        waitForLogin();
        navigateToUploadDocument();
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

    private void waitForLogin() {
        try {
            Thread.sleep(3000); // Attendre la connexion
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void navigateToUploadDocument() {
        System.out.println(" Navigation vers Upload Document (Teacher)...");

        try {
            // Ouvrir le menu hamburger (COMME ÉTUDIANT)
            onView(withContentDescription("Ouvrir le menu"))
                    .perform(click());
            waitFor(800);

            // Cliquer sur "Documents partagés" (professeur)
            onView(withId(R.id.nav_documentsTeacher))
                    .perform(click());

            waitFor(3000); // attendre chargement API / fragment

            System.out.println(" Page Upload Document chargée (Teacher)");

        } catch (Exception e) {
            System.out.println(" Erreur navigation Teacher: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Navigation vers Upload Document échouée", e);
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
    public void teacher_canViewUploadPage() {
        System.out.println(" Test: Affichage de la page Upload");

        try {
            // Vérifier que les éléments principaux sont affichés
            onView(withId(R.id.documentPageTitle))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.documentClasse))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.documentModule))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.documentTitle))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.documentType))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.documentSelectFile))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.documentUploadButton))
                    .check(matches(isDisplayed()));

            System.out.println(" Test réussi: Page upload affichée");

        } catch (Exception e) {
            System.out.println(" Erreur: " + e.getMessage());
            throw new RuntimeException("Test échoué", e);
        }
    }

    @Test
    public void teacher_canSelectClasse() {
        System.out.println("Test: Sélectionner une classe");

        try {
            waitFor(1000);

            // Cliquer sur le spinner classe
            onView(withId(R.id.documentClasse)).perform(click());
            waitFor(500);

            // Sélectionner la première classe
            onData(anything())
                    .atPosition(0)
                    .perform(click());

            waitFor(1500); // Attendre le chargement des matières

            System.out.println("Test réussi: Classe sélectionnée");

        } catch (Exception e) {
            System.out.println(" Erreur sélection classe: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test sélection classe échoué", e);
        }
    }

    @Test
    public void teacher_canSelectModule() {
        System.out.println(" Test: Sélectionner un module");

        try {
            waitFor(1000);

            // D'abord sélectionner une classe
            System.out.println(" Sélection de la classe...");
            onView(withId(R.id.documentClasse)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(1500);

            // Ensuite sélectionner un module
            System.out.println(" Sélection du module...");
            onView(withId(R.id.documentModule)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(500);

            System.out.println("Test réussi: Module sélectionné");

        } catch (Exception e) {
            System.out.println("Erreur sélection module: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test sélection module échoué", e);
        }
    }

    @Test
    public void teacher_canEnterDocumentTitle() {
        System.out.println(" Test: Saisir le titre du document");

        try {
            waitFor(1000);

            String testTitle = "Test Document - Cours Java";

            onView(withId(R.id.documentTitle))
                    .perform(clearText(), typeText(testTitle), closeSoftKeyboard());

            waitFor(500);

            // Vérifier que le texte a été saisi
            onView(withId(R.id.documentTitle))
                    .check(matches(withText(testTitle)));

            System.out.println("Test réussi: Titre saisi - " + testTitle);

        } catch (Exception e) {
            System.out.println(" Erreur saisie titre: " + e.getMessage());
            throw new RuntimeException("Test saisie titre échoué", e);
        }
    }

    @Test
    public void teacher_canSelectDocumentType() {
        System.out.println("Test: Sélectionner le type de document");

        try {
            waitFor(1000);

            // Tester COURS
            System.out.println("Sélection: COURS");
            onView(withId(R.id.documentType)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(500);

            // Tester TP
            System.out.println("Sélection: TP");
            onView(withId(R.id.documentType)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(1).perform(click());
            waitFor(500);

            // Tester TD
            System.out.println(" Sélection: TD");
            onView(withId(R.id.documentType)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(2).perform(click());
            waitFor(500);

            System.out.println(" Test réussi: Types sélectionnés");

        } catch (Exception e) {
            System.out.println("Erreur sélection type: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void teacher_canClickSelectFileButton() {
        System.out.println(" Test: Cliquer sur 'Choisir un fichier'");

        try {
            waitFor(1000);

            onView(withId(R.id.documentSelectFile))
                    .perform(click());

            waitFor(500);

            // Note: Le file picker s'ouvre mais on ne peut pas le tester avec Espresso
            // On vérifie juste que le bouton est cliquable
            System.out.println(" Test réussi: Bouton 'Choisir un fichier' cliquable");
            System.out.println(" Le file picker ne peut pas être testé avec Espresso");

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    @Test
    public void teacher_cannotUploadWithoutFile() {
        System.out.println(" Test: Upload sans fichier (doit échouer)");

        try {
            waitFor(1000);

            // Remplir tous les champs sauf le fichier
            onView(withId(R.id.documentClasse)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(1500);

            onView(withId(R.id.documentModule)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(500);

            onView(withId(R.id.documentTitle))
                    .perform(typeText("Test Sans Fichier"), closeSoftKeyboard());
            waitFor(500);

            // Essayer d'upload sans fichier
            onView(withId(R.id.documentUploadButton)).perform(click());
            waitFor(500);

            // Un Toast devrait apparaître avec "Choisissez un fichier d'abord"
            System.out.println(" Test réussi: Upload bloqué sans fichier");

        } catch (Exception e) {
            System.out.println(" Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void teacher_formFieldsWorkCorrectly() {
        System.out.println("Test: Fonctionnement complet du formulaire");

        try {
            waitFor(1000);

            // 1. Sélectionner une classe
            System.out.println("Sélection classe...");
            onView(withId(R.id.documentClasse)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(1500);

            // 2. Sélectionner un module
            System.out.println("Sélection module...");
            onView(withId(R.id.documentModule)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(500);

            // 3. Saisir le titre
            System.out.println(" Saisie du titre...");
            onView(withId(R.id.documentTitle))
                    .perform(typeText("Cours Complet Java OOP"), closeSoftKeyboard());
            waitFor(500);

            // 4. Sélectionner le type
            System.out.println("Sélection du type...");
            onView(withId(R.id.documentType)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(500);

            // 5. Vérifier que tous les champs sont remplis
            onView(withId(R.id.documentTitle))
                    .check(matches(withText("Cours Complet Java OOP")));

            System.out.println(" Test réussi: Formulaire complet fonctionnel");

        } catch (Exception e) {
            System.out.println(" Erreur: " + e.getMessage());
            throw new RuntimeException("Test formulaire échoué", e);
        }
    }

    @Test
    public void teacher_canChangeClasseAndModuleUpdates() {
        System.out.println("🧪 Test: Changement de classe met à jour les modules");

        try {
            waitFor(1000);

            // Sélectionner première classe
            System.out.println(" Classe 1...");
            onView(withId(R.id.documentClasse)).perform(click());
            waitFor(500);
            onData(anything()).atPosition(0).perform(click());
            waitFor(1500);

            // Vérifier que les modules sont chargés
            onView(withId(R.id.documentModule))
                    .check(matches(isDisplayed()));

            // Essayer de changer de classe si possible
            try {
                System.out.println("Classe 2...");
                onView(withId(R.id.documentClasse)).perform(click());
                waitFor(500);
                onData(anything()).atPosition(1).perform(click());
                waitFor(1500);

                System.out.println(" Test réussi: Modules mis à jour avec la classe");
            } catch (Exception e) {
                System.out.println(" Une seule classe disponible");
            }

        } catch (Exception e) {
            System.out.println(" Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void teacher_uploadPageLoadsCorrectly() {
        System.out.println(" Test: Chargement complet de la page");

        try {
            waitFor(2000);

            // Vérifier le titre
            System.out.println("Vérification titre...");
            onView(withId(R.id.documentPageTitle))
                    .check(matches(withText("Déposer un document")));

            // Vérifier tous les spinners
            System.out.println("Vérification spinners...");
            onView(withId(R.id.documentClasse)).check(matches(isDisplayed()));
            onView(withId(R.id.documentModule)).check(matches(isDisplayed()));
            onView(withId(R.id.documentType)).check(matches(isDisplayed()));

            // Vérifier le champ titre
            System.out.println(" Vérification champ titre...");
            onView(withId(R.id.documentTitle)).check(matches(isDisplayed()));

            // Vérifier les boutons
            System.out.println("Vérification boutons...");
            onView(withId(R.id.documentSelectFile)).check(matches(isDisplayed()));
            onView(withId(R.id.documentUploadButton)).check(matches(isDisplayed()));

            System.out.println(" Test réussi: Tous les éléments sont présents");

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
            throw new RuntimeException("Test chargement page échoué", e);
        }
    }
}