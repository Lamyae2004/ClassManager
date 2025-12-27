package com.ensa.mobile.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EmploiUtils {

    // Méthode pour calculer l'état
    public static String calculerEtat(String jour, String debut, String fin) {
        // Comparer le jour
        Calendar c = Calendar.getInstance();
        String[] jours = {"Dimanche","Lundi","Mardi","Mercredi","Jeudi","Vendredi","Samedi"};
        String jourActuel = jours[c.get(Calendar.DAY_OF_WEEK) - 1];

        if (!jourActuel.equalsIgnoreCase(jour)) {
            return (c.get(Calendar.DAY_OF_WEEK) < jourEnNumero(jour)) ? "À venir" : "Terminé";
        }

        // Comparer l'heure
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date now = new Date();
            Date hDebut = sdf.parse(debut);
            Date hFin = sdf.parse(fin);

            if (now.after(hDebut) && now.before(hFin)) {
                return "En cours";
            } else if (now.before(hDebut)) {
                return "À venir";
            } else {
                return "Terminé";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Inconnu";
        }
    }

    // Convertir nom du jour en numéro
    private static int jourEnNumero(String jour) {
        switch(jour.toLowerCase()) {
            case "dimanche": return 1;
            case "lundi": return 2;
            case "mardi": return 3;
            case "mercredi": return 4;
            case "jeudi": return 5;
            case "vendredi": return 6;
            case "samedi": return 7;
            default: return 0;
        }
    }
}
