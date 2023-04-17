package up.visulog.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InvalidAttributeValueException;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.Author;
import up.visulog.gitrawdata.Authors;

/**
 * Plugin qui compte le nombre de commits par semaine par auteur
 */
public class ActivityUserGraphPluginWeekday implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    // Constructeur
    /**
     * Construit un object de type ActivityUserGraphPluginWeekday
     * @param generalConfiguration
     */
    public ActivityUserGraphPluginWeekday(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    /**
     * Retourne un resultat en fonction de la liste de Commit
     * @param gitLog
     * @return 
     */
    // feature possible : caler tous les jours écoulés en légende du graphique (pour
    // que tout le monde ait une même légende, et que le graphe ait un axe des
    // abscisses continu et linéaire)
    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        Map<String, Integer> TotalActivity = createEmptyWeek(); // cette map est créée explicitement pour compter
                                                              // l'ensemble des commits par jour pour tout le monde.
        result.AuthorDailyActivityData.add(TotalActivity); // on l'ajoute donc à la liste des stats d'activités
                                                           // journalières.
        result.AuthorToIndex.put("Total", 0); // et on lui assigne l'index 0 sous le nom "Total", puisque c'est le
                                              // premier élément ajouté.
        result.IndexToAuthor.put(0, "Total"); // on crée de même la liste réciproque (elle servira plus tard)

        Authors a = new Authors(); // création d'une variable de type authors 
        ArrayList<Author> authors = new ArrayList<>();// création d'une ArrayList qui va contenir tout les auteurs
        authors.addAll(a.getAuthorsFromJson("SZwzzCpTr5ZhQYWFj8cK",1601));// ajouts de tous les membres du projet à authors

        boolean added; // boolean qui sert a savoir si l'auteur a été ajouté ou non à authors

        for (var commit : gitLog) {
            added = false; // initialisation du boolean a false;
            // OK donc ici on récupère l'ancienne valeur de commits sur le jour du commit
            // dans Total (premier élément), zéro si elle n'existe pas, et on l'incrémente.
            result.AuthorDailyActivityData.getFirst().put(commit.weekDayofDate,
                    result.AuthorDailyActivityData.getFirst().get(commit.weekDayofDate) + 1);
            // on joue ici sur l'erreur, en regardant si notre auteur a déjà un registre
            // d'activité journalier. Si non, on associe un indice -1 qui crééra
            // automatiquement une erreur
            // à la ligne suivante.
            try {
                for (var author : authors) { // parcours de collection d'auteur

                    String commitauthor = commit.author.toLowerCase(); // convertit le nom de l'auteur du commit en miniscules 
                    String authorname = author.getName().toLowerCase(); // convertit le nom de l'authors dans authors en minuscules
                    String [] ca = commitauthor.split(" "); // sépare le nom de l'auteur du commit en fonction des espaces
                    String [] an = authorname.split(" "); // sépare le nom de l'auteur dans authors en fonction des espaces
                
                    if(ca.length == 2) { // le cas où le nom de l'auteur du commit et soit l'identifiant soit juste le nom soit juste le prenom
                        if(ca[0].equals(author.getUsername())){ // compare si c'est l'identifiant 
                            added = true; // l'auteur a bien été ajouté dans la liste
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData
                            .get(result.AuthorToIndex.getOrDefault(author.getName(), -1));
                            // c'est cette deuxième ligne qui est susceptible de renvoyer une erreur. Elle
                            // associe au registre journalier de l'auteur une valeur incrémentée de 1 pour
                            // le jour du commit
                            // (si cette valeur n'est pas encore existante en mémoire, elle est fixée par
                            // défaut à zéro)
                            // notons l'usage ici et pour Total de la fonction getDay(String date) qui
                            // convertit la date du log en une simple journée.
                            // (car deux heures différentes d'une même journée seraient interprétées comme
                            // deux dates différentes sinon)
                            AuthorActivity.put(commit.weekDayofDate, AuthorActivity.get(commit.weekDayofDate) + 1);
                        } 
                        if(an[0].equals(ca[0]) || an[1].equals(ca[0])){ // compare si c'est le nom ou le prenom
                            added = true; // l'auteur a bien été ajouté dans la liste
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData
                            .get(result.AuthorToIndex.getOrDefault(author.getName(), -1));
                            // c'est cette deuxième ligne qui est susceptible de renvoyer une erreur. Elle
                            // associe au registre journalier de l'auteur une valeur incrémentée de 1 pour
                            // le jour du commit
                            // (si cette valeur n'est pas encore existante en mémoire, elle est fixée par
                            // défaut à zéro)
                            // notons l'usage ici et pour Total de la fonction getDay(String date) qui
                            // convertit la date du log en une simple journée.
                            // (car deux heures différentes d'une même journée seraient interprétées comme
                            // deux dates différentes sinon)
                            AuthorActivity.put(commit.weekDayofDate, AuthorActivity.get(commit.weekDayofDate) + 1);
                        }
                        if(an.length == 3){ // si l'auteur a nom prenom adressemail
                            if(an[2].equals(ca[0])){ // compare si c'est le même prenom
                                added = true; // l'auteur a bien été ajouté dans la liste
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData
                                .get(result.AuthorToIndex.getOrDefault(author.getName(), -1));
                                // c'est cette deuxième ligne qui est susceptible de renvoyer une erreur. Elle
                                // associe au registre journalier de l'auteur une valeur incrémentée de 1 pour
                                // le jour du commit
                                // (si cette valeur n'est pas encore existante en mémoire, elle est fixée par
                                // défaut à zéro)
                                 // notons l'usage ici et pour Total de la fonction getDay(String date) qui
                                // convertit la date du log en une simple journée.
                                // (car deux heures différentes d'une même journée seraient interprétées comme
                                // deux dates différentes sinon)
                                AuthorActivity.put(commit.weekDayofDate, AuthorActivity.get(commit.weekDayofDate) + 1);
                            }
                        }
                    }
                    if(ca.length == 3) { // le cas où commit.author est de la forme nom prenom ou prenom nom
                        if(ca[1].equals(an[1]) || ca[0].equals(an[1])){
                            added = true; // l'auteur a bien été ajouté dans la liste
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData
                            .get(result.AuthorToIndex.getOrDefault(author.getName(), -1));
                            // c'est cette deuxième ligne qui est susceptible de renvoyer une erreur. Elle
                            // associe au registre journalier de l'auteur une valeur incrémentée de 1 pour
                            // le jour du commit
                            // (si cette valeur n'est pas encore existante en mémoire, elle est fixée par
                            // défaut à zéro)
                            // notons l'usage ici et pour Total de la fonction getDay(String date) qui
                            // convertit la date du log en une simple journée.
                            // (car deux heures différentes d'une même journée seraient interprétées comme
                            // deux dates différentes sinon)
                            AuthorActivity.put(commit.weekDayofDate, AuthorActivity.get(commit.weekDayofDate) + 1);            
                        }
                        if(an.length == 3){ // si l'auteur a 2 noms 
                            if(ca[0].equals(an[2])){
                                added = true; // l'auteur a bien été ajouté dans la liste
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData
                                .get(result.AuthorToIndex.getOrDefault(author.getName(), -1));
                                // c'est cette deuxième ligne qui est susceptible de renvoyer une erreur. Elle
                                // associe au registre journalier de l'auteur une valeur incrémentée de 1 pour
                                // le jour du commit
                                // (si cette valeur n'est pas encore existante en mémoire, elle est fixée par
                                // défaut à zéro)
                                // notons l'usage ici et pour Total de la fonction getDay(String date) qui
                                // convertit la date du log en une simple journée.
                                // (car deux heures différentes d'une même journée seraient interprétées comme
                                // deux dates différentes sinon)
                                AuthorActivity.put(commit.weekDayofDate, AuthorActivity.get(commit.weekDayofDate) + 1);                    
                            }
                        }
                    }
                    if(ca.length == 4){ // le cas où l'auteur a 2 noms
                        if(an.length == 3){
                            if (an[2].equals(ca[2])){
                                added = true;// l'auteur a bien été ajouté dans la liste
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData
                                .get(result.AuthorToIndex.getOrDefault(author.getName(), -1));
                                // c'est cette deuxième ligne qui est susceptible de renvoyer une erreur. Elle
                                // associe au registre journalier de l'auteur une valeur incrémentée de 1 pour
                                // le jour du commit
                                // (si cette valeur n'est pas encore existante en mémoire, elle est fixée par
                                // défaut à zéro)
                                // notons l'usage ici et pour Total de la fonction getDay(String date) qui
                                // convertit la date du log en une simple journée.
                                // (car deux heures différentes d'une même journée seraient interprétées comme
                                // deux dates différentes sinon)
                                AuthorActivity.put(commit.weekDayofDate, AuthorActivity.get(commit.weekDayofDate) + 1);
                            }
                        }
                    }
                }
                if(!added){ //si l'auteur n'a pas été ajouté a la liste 
                    Author au = new Author(commit.author, commit.author); // création d'un nouvelle auteur
                    authors.add(au);//ajout de l'auteur dans l'arrayList
                    Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData
                    .get(result.AuthorToIndex.getOrDefault(commit.author, -1));
                    // c'est cette deuxième ligne qui est susceptible de renvoyer une erreur. Elle
                    // associe au registre journalier de l'auteur une valeur incrémentée de 1 pour
                    // le jour du commit
                    // (si cette valeur n'est pas encore existante en mémoire, elle est fixée par
                    // défaut à zéro)
                    // notons l'usage ici et pour Total de la fonction getDay(String date) qui
                    // convertit la date du log en une simple journée.
                    // (car deux heures différentes d'une même journée seraient interprétées comme
                    // deux dates différentes sinon)
                    AuthorActivity.put(commit.weekDayofDate, AuthorActivity.get(commit.weekDayofDate) + 1);
                }      
            } catch (IndexOutOfBoundsException e) {
                // si l'utilisateur n'avait pas encore de registre journalier, on en créé un et
                // on fait comme avant.
                for (var author : authors) { //parcours de collection d'auteur

                    String commitauthor = commit.author.toLowerCase(); // convertit le nom de l'auteur du commit en miniscule 
                    String authorname = author.getName().toLowerCase(); // convertit le nom de l'authors dans authors en minuscule
                    String [] ca = commitauthor.split(" "); // sépare le nom de l'auteur du commit en fonction des espaces
                    String [] an = authorname.split(" "); //sépare le nom de l'auteur dans authors en fonction des espaces
                
                    if(ca.length == 2) { // le cas où le nom de l'auteur du commit et soit l'identifiant soit juste le nom soit juste le prenom
                        if(ca[0].equals(author.getUsername())){ // compare si c'est l'identifiant 
                            added = true; // l'auteur a bien été ajouté dans la liste
                            result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                            result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                            result.AuthorDailyActivityData.addLast(createEmptyWeek());
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                            AuthorActivity.put(commit.weekDayofDate, AuthorActivity.getOrDefault(commit.weekDayofDate, 0) + 1); // TODO : getordefault
                        } 
                        if(an[0].equals(ca[0]) || an[1].equals(ca[0])){ // compare si c'est le nom ou le prenom
                            added = true; // l'auteur a bien été ajouté dans la liste
                            result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                            result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                            result.AuthorDailyActivityData.addLast(createEmptyWeek());
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                            AuthorActivity.put(commit.weekDayofDate, AuthorActivity.getOrDefault(commit.weekDayofDate, 0) + 1); // TODO : getordefault
                        }
                        if(an.length == 3){ // si l'auteur a 2 noms
                            if(an[2].equals(ca[0])){ // compare si c'est le même prenom
                                added = true; // l'auteur a bien été ajouté dans la liste
                                result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                                result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                                result.AuthorDailyActivityData.addLast(createEmptyWeek());
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                                AuthorActivity.put(commit.weekDayofDate, AuthorActivity.getOrDefault(commit.weekDayofDate, 0) + 1); // TODO : getordefault
                            }
                        }
                    }
                   if(ca.length == 3) { // le cas où commit.author est de la forme nom prenom ou prenom nom
                        if(ca[1].equals(an[1]) || ca[0].equals(an[1])){
                            added = true; // l'auteur a bien été ajouté dans la liste
                            result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                            result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                            result.AuthorDailyActivityData.addLast(createEmptyWeek());
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                            AuthorActivity.put(commit.weekDayofDate, AuthorActivity.getOrDefault(commit.weekDayofDate, 0) + 1); // TODO : getordefault                
                        }
                       if(an.length == 3){ // si l'auteur a 2 noms 
                            if(ca[0].equals(an[2])){
                                added = true; // l'auteur a bien été ajouté dans la liste
                                result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                                result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), commit.author);
                                result.AuthorDailyActivityData.addLast(createEmptyWeek());
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                                AuthorActivity.put(commit.weekDayofDate, AuthorActivity.getOrDefault(commit.weekDayofDate, 0) + 1); // TODO : getordefault                        
                            }
                        }
                    }
                    if(ca.length == 4){ // le cas où l'auteur a 2 noms
                        if(an.length == 3){
                            if (an[2].equals(ca[2])){
                                added = true;// l'auteur a bien été ajouté dans la liste
                                result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                                result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                                result.AuthorDailyActivityData.addLast(createEmptyWeek());
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                                AuthorActivity.put(commit.weekDayofDate, AuthorActivity.getOrDefault(commit.weekDayofDate, 0) + 1); // TODO : getordefault
                            }
                        }
                    }
                }
                if(!added){ // si l'auteur n'a pas été ajouté à la liste 
                    Author au = new Author(commit.author, commit.author); // création d'un nouvel auteur
                    authors.add(au);// ajout de l'auteur dans l'arrayList
                    result.AuthorToIndex.put(commit.author, result.AuthorDailyActivityData.size());
                    result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), commit.author);
                    result.AuthorDailyActivityData.addLast(createEmptyWeek());
                    Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                    AuthorActivity.put(commit.weekDayofDate, AuthorActivity.getOrDefault(commit.weekDayofDate, 0) + 1); // TODO : getordefault
                }
            }
        }
        return result; // on renvoie alors l'objet Result
    }
    
    /**
     * Renvoie un entier avec deux chiffres si il en comportais que un, le renvoie sous forme de String
     * @param n
     * @return 
     */
    private static String deux_chiffres(int n) {
        if (n < 10) {
            return "0" + Integer.toString(n);
        }
        return Integer.toString(n);
    }

    /**
     * Classe interne Date de ActivityUserGraphPluginWeekday
     */
    static class Date {
        int annee;
        int mois;
        int jour;

        /**
         * Construit un object de type Date
         * @param expr
         */
        Date(String expr) {
            String[] args = expr.split(" ");
            annee = Integer.parseInt(args[0]);
            mois = Integer.parseInt(args[1]);
            jour = Integer.parseInt(args[2]);
        }

        /**
         * Construit un object de type Date
         * @param annee
         * @param mois
         * @param jour
         */
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

        /**
         * Retourne sous forme de String
         * @return
         */
        public String toString() {
            return String.format("%d %s %s", annee, deux_chiffres(mois), deux_chiffres(jour));
        }

        /**
         * Verifie si une date est anterieur a une autre
         * @param d2
         * @return
         */
        boolean estAnterieure(Date d2) {
            return (annee < d2.annee) || (annee == d2.annee && (mois < d2.mois || (mois == d2.mois && jour < d2.jour)));
        }
    }

    /**
     * Cree une map pour les semaine vide
     * @return 
     */
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

    /**
     * Renvoie l'ordre des jours dans la semaine
     * @return 
     */
    static String[] orderedWeek() {
        String[] res = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        return res;
    }

    /**
     * Trouve a quel position de la semaine le jours est
     * @param day
     * @return
     */
    static int getDayPositionInWeek(String day){
        String[] week = orderedWeek();
        for (int i = 0; i < 7; i++) {
            if (week[i].equals(day)) {
                return i;
            }
        }
        return 0; // in case the day doesn't exist, it'll automatically be init to Monday to avoid errors. A throw declaration would have been better, but requires throw heritage that we don't want.
    }

    /**
     * Lance le plugin
     */
    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    /**
     * Retourne un resultat (si il est null alors on le run d'abord)
     */
    @Override
    public Result getResult() {
        if (result == null)
            run();
        return result;
    }

    /**
     * Classe interne
     */
    static class Result implements AnalyzerPlugin.Result {
        private LinkedList<Map<String, Integer>> AuthorDailyActivityData = new LinkedList<Map<String, Integer>>(); // array of the dayToActivity arrays, for every author + global project
        private Map<Integer, String> IndexToAuthor = new HashMap<>();
        private Map<String, Integer> AuthorToIndex = new HashMap<>(); // author -> index of this author in AuthorDailyActivity Data

        /**
         * Retourne la map AuthorDailyActivityData
         * @return 
         */
        LinkedList<Map<String, Integer>> getAuthorDailyActivityData() {
            return AuthorDailyActivityData;
        }

        /**
         * Retourne la map AuthorToIndex
         * @return 
         */
        Map<String, Integer> getAuthorToIndex() {
            return AuthorToIndex;
        }

        /**
         * Retourne le resultat sous forme de String
         */
        @Override
        public String getResultAsString() {
            return "N'a pas été implémenté car ce plugin est clairement fait pour renvoyer du HTML.";
        }

        /**
         * Retourne le resultat sous forme de Div HTML
         */
        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html_head = new StringBuilder();
            StringBuilder html_body = new StringBuilder();
            html_head.append("\n  <script type=\"text/javascript\">  window.onload = function () {\n");
            html_body.append("\n");
            for (int i = 0; i < IndexToAuthor.keySet().size(); i++) {
                String currentAuthor = IndexToAuthor.get(i);
                Map<String, Integer> currentAuthorActivity = AuthorDailyActivityData.get(i);
                html_head.append(String.format("    var chart%d = new CanvasJS.Chart(\"chart%d\",\n    {\n", i, i));
                html_head.append(String.format("\n      title:{\n      text: %s\n      },\n",
                        "\"Activity of user " + currentAuthor + "\""));
                html_head.append("      data : [\n      {\n        type: \"line\",\n\n        dataPoints: [\n");
                html_body.append(
                        String.format("  <div id=\"chart%d\" style=\"height: 300px; width: 100%%;\">\n  </div>\n", i));
                for (String jour : orderedWeek()) {
                    html_head.append(String.format("        { x: \"%s\", y: %d },\n", getDayPositionInWeek(jour),
                            currentAuthorActivity.get(jour)));
                }
                html_head.replace(html_head.length() - 2, html_head.length() - 1, "\n"); // retrait de la virgule
                html_head.append(String.format("        ]\n      }\n      ]\n    });\n\n    chart%d.render();\n\n", i));
            }
            html_head.append(
                    "  }\n  </script>\n  <script type=\"text/javascript\" src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script></head>\n");
            // ce script ne nous appartient pas
            html_body.append("\n");
            return html_head.toString() + html_body.toString();
        }

    }
}