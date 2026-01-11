package com.class_manager.class_responsibility_service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelEnumsTest {

    @Test
    void testNiveau_AllValues() {
        // Given/When/Then - Test all Niveau enum values
        Niveau[] niveaux = Niveau.values();
        assertEquals(5, niveaux.length);
        assertEquals(Niveau.CP1, Niveau.valueOf("CP1"));
        assertEquals(Niveau.CP2, Niveau.valueOf("CP2"));
        assertEquals(Niveau.CI1, Niveau.valueOf("CI1"));
        assertEquals(Niveau.CI2, Niveau.valueOf("CI2"));
        assertEquals(Niveau.CI3, Niveau.valueOf("CI3"));
    }

    @Test
    void testFiliere_AllValues() {
        // Given/When/Then - Test all Filiere enum values
        Filiere[] filieres = Filiere.values();
        assertEquals(7, filieres.length);
        assertEquals(Filiere.RST, Filiere.valueOf("RST"));
        assertEquals(Filiere.INFO, Filiere.valueOf("INFO"));
        assertEquals(Filiere.CIVIL, Filiere.valueOf("CIVIL"));
        assertEquals(Filiere.INDUS, Filiere.valueOf("INDUS"));
        assertEquals(Filiere.MECA, Filiere.valueOf("MECA"));
        assertEquals(Filiere.ELEC, Filiere.valueOf("ELEC"));
        assertEquals(Filiere.NONE, Filiere.valueOf("NONE"));
    }

    @Test
    void testRole_AllValues() {
        // Given/When/Then - Test all Role enum values (this covers the Role enum)
        Role[] roles = Role.values();
        assertEquals(3, roles.length);
        assertEquals(Role.STUDENT, Role.valueOf("STUDENT"));
        assertEquals(Role.TEACHER, Role.valueOf("TEACHER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }

    @Test
    void testRole_UsageInContext() {
        // Given/When/Then - Use Role enum in a context to ensure it's loaded
        Role studentRole = Role.STUDENT;
        Role teacherRole = Role.TEACHER;
        Role adminRole = Role.ADMIN;

        assertNotNull(studentRole);
        assertNotNull(teacherRole);
        assertNotNull(adminRole);
        assertEquals("STUDENT", studentRole.name());
        assertEquals("TEACHER", teacherRole.name());
        assertEquals("ADMIN", adminRole.name());
    }

    @Test
    void testNiveau_UsageInContext() {
        // Given/When/Then - Use Niveau enum
        Niveau cp1 = Niveau.CP1;
        assertNotNull(cp1);
        assertEquals("CP1", cp1.name());
    }

    @Test
    void testFiliere_UsageInContext() {
        // Given/When/Then - Use Filiere enum
        Filiere info = Filiere.INFO;
        assertNotNull(info);
        assertEquals("INFO", info.name());
    }
}
