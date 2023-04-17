package up.visulog.analyzer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.Author;
import up.visulog.gitrawdata.Authors;

/**
 * Plugin qui compte le nombre de commits par jour par auteur
 */
public class ActivityUserGraphPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    // constructeur
    /**
     * Construit un object de type ActivityUserGraphPlugin
     * @param generalConfiguration
     */
    public ActivityUserGraphPlugin(Configuration generalConfiguration) {
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
        Date[] extremes = getBoundsDays(gitLog);
        Date firstDay = extremes[0];
        Date lastDay = extremes[1];
        result.firstDay = firstDay;
        result.lastDay = lastDay;
        Map<String, Integer> TotalActivity = createEmptyCalendar(firstDay, lastDay); // cette map est créée explicitement pour compter
                                                              // l'ensemble des commits par jour pour tout le monde.
        result.AuthorDailyActivityData.addFirst(TotalActivity); // on l'ajoute donc à la liste des stats d'activités
                                                           // journalières.
        result.AuthorToIndex.put("Total", 0); // et on lui assigne l'index 0 sous le nom "Total", puisque c'est le
                                              // premier élément ajouté.
        result.IndexToAuthor.put(0, "Total"); // on crée de même la liste réciproque (elle servira plus tard)

        Authors a = new Authors(); // création d'une variable de type authors 
        ArrayList<Author> authors = new ArrayList<>();// création d'une ArrayList qui va contenir tous les auteurs
        authors.addAll(a.getAuthorsFromJson("SZwzzCpTr5ZhQYWFj8cK",1601));// ajouts de tous les membres du projet à authors

        boolean added; // boolean qui sert a savoir si l'auteur a été ajouté ou non à authors

        for (var commit : gitLog) {
            added = false; // initialisation du boolean a false;
            // ici on récupère l'ancienne valeur de commits sur le jour du commit
            // dans Total (premier élément), zéro si elle n'existe pas, et on l'incrémente.
            result.AuthorDailyActivityData.getFirst().put(getDay(commit.date),
                    result.AuthorDailyActivityData.getFirst().getOrDefault(getDay(commit.date), 0) + 1); // TODO : getordefault
            // on joue ici sur l'erreur, en regardant si notre auteur a déjà un registre
            // d'activité journalier.
            try {
                for (var author : authors) { //parcours de collection d'auteur

                    String commitauthor = commit.author.toLowerCase(); // convertit le nom de l'auteur du commit en miniscules 
                    String authorname = author.getName().toLowerCase(); // convertit le nom de l'authors dans authors en minuscules
                    String [] ca = commitauthor.split(" "); // sépare le nom de l'auteur du commit en fonction des espaces
                    String [] an = authorname.split(" "); // sépare le nom de l'auteur dans authors en fonction des espaces
                
                    if(ca.length == 2) { // le cas où le nom de l'auteur du commit est soit l'identifiant soit juste le nom soit juste le prénom
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
                            AuthorActivity.put(getDay(commit.date), AuthorActivity.get(getDay(commit.date)) + 1);
                        } 
                        if(an[0].equals(ca[0]) || an[1].equals(ca[0])) { // compare si c'est le nom ou le prénom
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
                            AuthorActivity.put(getDay(commit.date), AuthorActivity.get(getDay(commit.date)) + 1);
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
                                AuthorActivity.put(getDay(commit.date), AuthorActivity.get(getDay(commit.date)) + 1);
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
                            AuthorActivity.put(getDay(commit.date), AuthorActivity.get(getDay(commit.date)) + 1);            
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
                                AuthorActivity.put(getDay(commit.date), AuthorActivity.get(getDay(commit.date)) + 1);                    
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
                                AuthorActivity.put(getDay(commit.date), AuthorActivity.get(getDay(commit.date)) + 1);
                            }
                        }
                    }
                }
                if(!added){ //si l'auteur n'a pas était ajouté a la liste 
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
                    AuthorActivity.put(getDay(commit.date), AuthorActivity.get(getDay(commit.date)) + 1);
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
                            result.AuthorDailyActivityData.addLast(createEmptyCalendar(firstDay, lastDay));
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                            AuthorActivity.put(getDay(commit.date), AuthorActivity.getOrDefault(getDay(commit.date), 0) + 1); // TODO : getordefault
                        } 
                        if(an[0].equals(ca[0]) || an[1].equals(ca[0])){ // compare si c'est le nom ou le prenom
                            added = true; // l'auteur a bien été ajouté dans la liste
                            result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                            result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                            result.AuthorDailyActivityData.addLast(createEmptyCalendar(firstDay, lastDay));
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                            AuthorActivity.put(getDay(commit.date), AuthorActivity.getOrDefault(getDay(commit.date), 0) + 1); // TODO : getordefault
                        }
                        if(an.length == 3){ // si l'auteur a 2 noms
                            if(an[2].equals(ca[0])){ // compare si c'est le même prenom
                                added = true; // l'auteur a bien été ajouté dans la liste
                                result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                                result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                                result.AuthorDailyActivityData.addLast(createEmptyCalendar(firstDay, lastDay));
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                                AuthorActivity.put(getDay(commit.date), AuthorActivity.getOrDefault(getDay(commit.date), 0) + 1); // TODO : getordefault
                            }
                        }
                    }
                   if(ca.length == 3) { // le cas où commit.author est de la forme nom prenom ou prenom nom
                        if(ca[1].equals(an[1]) || ca[0].equals(an[1])){
                            added = true; // l'auteur a bien été ajouté dans la liste
                            result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                            result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                            result.AuthorDailyActivityData.addLast(createEmptyCalendar(firstDay, lastDay));
                            Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                            AuthorActivity.put(getDay(commit.date), AuthorActivity.getOrDefault(getDay(commit.date), 0) + 1); // TODO : getordefault                
                        }
                       if(an.length == 3){ // si l'auteur a 2 noms 
                            if(ca[0].equals(an[2])){
                                added = true; // l'auteur a bien été ajouté dans la liste
                                result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                                result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), commit.author);
                                result.AuthorDailyActivityData.addLast(createEmptyCalendar(firstDay, lastDay));
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                                AuthorActivity.put(getDay(commit.date), AuthorActivity.getOrDefault(getDay(commit.date), 0) + 1); // TODO : getordefault                        
                            }
                        }
                    }
                    if(ca.length == 4){ // le cas où l'auteur a 2 noms
                        if(an.length == 3){
                            if (an[2].equals(ca[2])){
                                added = true;// l'auteur a bien été ajouté dans la liste
                                result.AuthorToIndex.put(author.getName(), result.AuthorDailyActivityData.size());
                                result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), author.getName());
                                result.AuthorDailyActivityData.addLast(createEmptyCalendar(firstDay, lastDay));
                                Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                                AuthorActivity.put(getDay(commit.date), AuthorActivity.getOrDefault(getDay(commit.date), 0) + 1); // TODO : getordefault
                            }
                        }
                    }
                }
                if(!added){ //si l'auteur n'a pas été ajouté a la liste 
                    Author au = new Author(commit.author, commit.author); // création d'un nouvelle auteur
                    authors.add(au);// ajout de l'auteur dans l'arrayList
                    result.AuthorToIndex.put(commit.author, result.AuthorDailyActivityData.size());
                    result.IndexToAuthor.put(result.AuthorDailyActivityData.size(), commit.author);
                    result.AuthorDailyActivityData.addLast(createEmptyCalendar(firstDay, lastDay));
                    Map<String, Integer> AuthorActivity = result.AuthorDailyActivityData.getLast();
                    AuthorActivity.put(getDay(commit.date), AuthorActivity.getOrDefault(getDay(commit.date), 0) + 1); // TODO : getordefault
                }
            }
        }
        return result; // on renvoie alors l'objet Result
    }

    /**
     * Renvoie un entier avec deux chiffres si il en comportais que un, et le renvoie sous forme de String
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
     * Classe interne Date de ActivityUserGraphPlugin
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

        /**
         * Verifie si l'annee est une annee bissextile
         * @return
         */
        boolean pendantAnneeBissextile() {
            return (annee % 4 == 0 && annee % 100 != 0) || annee % 400 == 0;
        }

        /**
         * Renvoie le jour d'apres 
         * @return
         */
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

    /**
     * Cree un calendrier vide
     * @param firstDay
     * @param lastDay
     * @return
     */
    static Map<String, Integer> createEmptyCalendar(Date firstDay, Date lastDay) {
        Map<String, Integer> res = new HashMap<String, Integer>();
        Date day = firstDay;
        while (day.estAnterieure(lastDay)) {
            res.put(day.toString(), 0);
            day = day.nextDay();
        }
        return res;
    }

    /**
     * Conversion d'une date en journée
     * @param date
     * @return
     */
    static String getDay(LocalDateTime date) {
        String[] args = date.toString().split("T")[0].split("-"); // YYYY-MM-DDThh:mm:ss
        return args[0] + " " + deux_chiffres(Integer.parseInt(args[1]) - 1) + " " + deux_chiffres(Integer.parseInt(args[2])); // le -1 vient du changement de comptage des mois
    }

    /**
     * Retourne les date de debut et de fin 
     * @param gitLog
     * @return
     */
    static Date[] getBoundsDays(List<Commit> gitLog) {
        ArrayList<Date> triee = new ArrayList<Date>();
        for (Commit c : gitLog) {
            triee.add(new Date(getDay(c.date)));
        }
        triee = tri_dates_DatetoDate(triee);
        Date[] res = {triee.get(0), triee.get(triee.size() - 1)};
        return res;
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
     * @return
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
        private Date firstDay;
        private Date lastDay;
        private LinkedList<Map<String, Integer>> AuthorDailyActivityData = new LinkedList<Map<String, Integer>>(); // array of the dayToActivity arrays, for every author + global project
        private Map<Integer, String> IndexToAuthor = new HashMap<>();
        private Map<String, Integer> AuthorToIndex = new HashMap<>(); // author -> index of this author in AuthorDailyActivity Data

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
        // script obtenu en libre service sur canvasjs.com
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
                for (String date : tri_dates_StringtoString(createEmptyCalendar(firstDay, lastDay).keySet())) {
                    String[] jour_args = date.split(" ");
                    html_head.append(String.format("        { x: new Date(%s,%s,%s), y: %d },\n", jour_args[0],
                            jour_args[1], jour_args[2], currentAuthorActivity.getOrDefault(date, 0))); // TODO : getordefault
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

    /**
    * Tri les dates
    * De String vers String
    * @param T
    * @return
    */
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
    
     /**
    * Tri les dates
    * De Date vers String
    * @param T
    * @return
    */
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

     /**
    * Tri les dates
    * De String vers Date
    * @param T
    * @return
    */
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

     /**
    * Tri les dates
    * De Date vers Date
    * @param T
    * @return
    */
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
    // remarque : le traitement des doublons dans ces fonctions n'est pas testé
}
