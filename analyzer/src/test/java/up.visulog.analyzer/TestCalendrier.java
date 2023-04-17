import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Cette classe test le calendrier utilisé dans le plugin ActivityUserGraphPlugin.java
 */
class TestCalendrier {

    public TestCalendrier(String a, String b) {
        // ne sert que pour le test d'instanciateur
    }

    public String toString() {
        return "juste un Calendrier"; // même chose
    }

    private static String deux_chiffres(int n) {
        if (n < 10) {
            return "0" + Integer.toString(n);
        }
        return Integer.toString(n);
    }

    static class Date {
        int annee;
        int mois;
        int jour;

        Date(String expr) {
            String[] args = expr.split(" ");
            annee = Integer.parseInt(args[0]);
            mois = Integer.parseInt(args[1]);
            jour = Integer.parseInt(args[2]);
        }

        Date(int annee, int mois, int jour) {
            this.annee = annee;
            this.mois = mois;
            this.jour = jour;
            Map<String, String> MoisToNombre = new HashMap<>();
            MoisToNombre.put("Jan", "00");
            MoisToNombre.put("Feb", "01");
            MoisToNombre.put("Mar", "02");
            MoisToNombre.put("Apr", "03");
            MoisToNombre.put("May", "04");
            MoisToNombre.put("Jun", "05");
            MoisToNombre.put("Jul", "06");
            MoisToNombre.put("Aug", "07");
            MoisToNombre.put("Sep", "08");
            MoisToNombre.put("Oct", "09");
            MoisToNombre.put("Nov", "10");
            MoisToNombre.put("Dec", "11");
        }

        public String toString() {
            return String.format("%d %d %d", annee, mois, jour);
        }

        boolean estAnterieure(Date d2) {
            return (annee < d2.annee) || (annee == d2.annee && (mois < d2.mois || (mois == d2.mois && jour < d2.jour)));
        }

        boolean pendantAnneeBissextile() {
            return (annee % 4 == 0 && annee % 100 != 0) || annee % 400 == 0;
        }

        Date nextDay() {
            if (jour < 28) {
                return new Date(annee, mois, jour+1);
            }
            else if (jour == 28) {
                if (mois != 1) { // 1 : mois de février car janvier est 0
                    return new Date(annee, mois, jour+1);
                }
                else if (pendantAnneeBissextile()) {
                    return new Date(annee, 1, 29); // 29 février bissextile
                }
                else {
                    return new Date(annee, 2, 1); // "29 février" pas bissextile, soit 1er mars
                }
            }
            else if (jour == 29) {
                if (mois == 1) {
                    return new Date(annee, 2, 1); // 1er mars après un 29 février bissextile
                }
                else {
                    return new Date(annee, mois, jour+1);
                }
            }
            else if (jour == 30) {
                if (mois == 3 || mois == 5 || mois == 8 || mois == 10) { // mois à trente jours
                    return new Date(annee, mois+1, 1);
                }
                else {
                    return new Date(annee, mois, jour+1); // 31 d'un mois à 31 jours. Le 30 février n'existe pas, donc pas de test supplémentaire.
                }
            }
            else { // 31 du mois
                if (mois == 11) {
                    return new Date(annee+1, 0, 1); // nouvelle année en décembre
                }
                else {
                    return new Date(annee, mois+1, 1); // sinon, nouveau mois
                }
            }
        }
    }

    // conversion d'une date en journée.
    static String getDay(String date) {
        Map<String, String> MoisToNombre = new HashMap<>();
        MoisToNombre.put("Jan", "00");
        MoisToNombre.put("Feb", "01");
        MoisToNombre.put("Mar", "02");
        MoisToNombre.put("Apr", "03");
        MoisToNombre.put("May", "04");
        MoisToNombre.put("Jun", "05");
        MoisToNombre.put("Jul", "06");
        MoisToNombre.put("Aug", "07");
        MoisToNombre.put("Sep", "08");
        MoisToNombre.put("Oct", "09");
        MoisToNombre.put("Nov", "10");
        MoisToNombre.put("Dec", "11");

        String[] args = date.split(" "); // NomJour mois numérojour heure:minutes:secondes année fuseau
        return args[4] + " " + MoisToNombre.get(args[1]) + " " + deux_chiffres(Integer.parseInt(args[2]));
    }

    static Map<String, Integer> createEmptyCalendarWithString(Date firstDay, Date lastDay) {
        Map<String, Integer> res = new HashMap<String, Integer>();
        Date day = firstDay;
        while (day.estAnterieure(lastDay)) {
            res.put(day.toString(), 0);
            day = day.nextDay();
        }
        return res;
    }

    static Map<Date, Integer> createEmptyCalendarWithDate(Date firstDay, Date lastDay) {
        Map<Date, Integer> res = new HashMap<Date, Integer>();
        Date day = firstDay;
        while (day.estAnterieure(lastDay)) {
            res.put(day, 0);
            day = day.nextDay();
        }
        return res;
    }

    static Map<Integer, Integer> createEmptyHourTable() {
        Map<Integer, Integer> res = new HashMap<Integer, Integer>();
        for (int i=0; i < 24; i++) {
            res.put(i, 0);
        }
        return res;
    }

    static Map<String, Integer> createEmptyWeek() {
        Map<String, Integer> res = new HashMap<String, Integer>();
        res.put("Mon", 0);
        res.put("Tue", 0);
        res.put("Wed", 0);
        res.put("Thu", 0);
        res.put("Fri", 0);
        res.put("Sat", 0);
        res.put("Sun", 0);
        return res;
    }

    public static void main(String[] args) {
        Date firstDay = new Date(2019,00,1);
        Date lastDay = new Date(2021,00,1);
        Map<Date, Integer> test = createEmptyCalendarWithDate(firstDay, lastDay);
        for (Date day : tri_dates_DatetoDate(test.keySet())) {
            System.out.println(String.format("%s %d", day, test.get(day)));
        }
        Date date = firstDay;
        while (date.estAnterieure(lastDay)) {
            System.out.println(date.toString());
            date = date.nextDay();
        }
        Map<Integer, Integer> test2 = createEmptyHourTable();
        for (int hour : test2.keySet()) {
            System.out.println(String.format("%d %d", hour, test2.get(hour)));
        }
        Map<String, Integer> test3 = createEmptyWeek();
        for (String day : test3.keySet()) {
            System.out.println(String.format("%s %d", day, test3.get(day)));
        }
    }

    static <T extends Collection<String>> ArrayList<String> tri_dates_StringtoString(T keySet) {
        ArrayList<String> res = new ArrayList<String>();
        ArrayList<Date> aux = new ArrayList<Date>();
        for (String date : keySet) {
            Date d = new Date(date);
            int i = 0;
            while (i < aux.size() && aux.get(i).estAnterieure(d)) {
                i++;
            }
            if (i == aux.size()) {
                aux.add(d);
            }
            else {
                aux.add(i, d);
            }
        }
        for (Date d : aux) {
            res.add(d.toString());
        }
        return res;
    }

    static <T extends Collection<Date>> ArrayList<String> tri_dates_DatetoString(T keySet) {
        ArrayList<String> res = new ArrayList<String>();
        ArrayList<Date> aux = new ArrayList<Date>();
        for (Date d : keySet) {
            int i = 0;
            while (i < aux.size() && aux.get(i).estAnterieure(d)) {
                i++;
            }
            if (i == aux.size()) {
                aux.add(d);
            }
            else {
                aux.add(i, d);
            }
        }
        for (Date d : aux) {
            res.add(d.toString());
        }
        return res;
    }

    static <T extends Collection<String>> ArrayList<Date> tri_dates_StringtoDate(T keySet) {
        ArrayList<Date> aux = new ArrayList<Date>();
        for (String date : keySet) {
            Date d = new Date(date);
            int i = 0;
            while (i < aux.size() && aux.get(i).estAnterieure(d)) {
                i++;
            }
            if (i == aux.size()) {
                aux.add(d);
            }
            else {
                aux.add(i, d);
            }
        }
        return aux;
    }

    static <T extends Collection<Date>> ArrayList<Date> tri_dates_DatetoDate(T keySet) {
        ArrayList<Date> aux = new ArrayList<Date>();
        for (Date d : keySet) {
            int i = 0;
            while (i < aux.size() && aux.get(i).estAnterieure(d)) {
                i++;
            }
            if (i == aux.size()) {
                aux.add(d);
            }
            else {
                aux.add(i, d);
            }
        }
        return aux;
    }
}