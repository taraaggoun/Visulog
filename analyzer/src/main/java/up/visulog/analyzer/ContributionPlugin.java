package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Author;
import up.visulog.gitrawdata.Authors;
import up.visulog.gitrawdata.Commit;

import java.util.*;

/**
 * Plugin qui compte la contribution de chaque auteur pour les commits
 */
public class ContributionPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;
    
    //Constructeur
    /**
     * Construit un object de type ContributionPlugin
     * @param generalConfiguration
     */
    public ContributionPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    
    //Methode
    /**
     * Recupere le nombre de commit par auteur dans une liste de plugin
     * @param gitLogthis
     * @param gitLogall
     * @return
     */
    //on a ici deux liste de commit differentes :
    //  - une qui recupère les commits de la branche (gitLogthis)
    //  - une qui recupère tous les commits réalisés (gitLogall)
    static Result processLog(List<Commit> gitLogthis, List<Commit> gitLogall) {
        Authors a = new Authors(); //creation d'une variable de type authors
        ArrayList<Author> authors = new ArrayList<>(); //creation d'un ArrayListe qui va contenir tout les auteur
        authors.addAll(a.getAuthorsFromJson("SZwzzCpTr5ZhQYWFj8cK",1601));// ajout de tout les membre du projet a authors
        boolean added; //boolean qui sert a savoir si l'auteur a été ajouté a authors

        var result = new ContributionPlugin.Result();
        //pour un plugin complet on a besoin de remplir les 2 Map en fonction de Liste de Commit différentes
        //on commence par remplir commitPerAuthor -> ne contient que le nb de commit par auteur dans la branche
        for (var commit : gitLogthis) {

            added = false; //initialisation du boolean a false;
            for (var author : authors) { //parcours de l'arrayList d'auteur
                String commitauthor = commit.author.toLowerCase(); //convertie le nom de l'auteur du commit en miniscule
                String authorname = author.getName().toLowerCase(); //convertie le nom de l'auteur dans authors en minuscule
                String [] ca = commitauthor.split(" "); // separe le nom de l'auteur du commit en fonction des espaces
                String [] an = authorname.split(" "); //separe le nom de l'auteur dans authors en fonction des espaces
                if(ca.length == 2) { // le cas ou le nom de l'auteur du commit et soit l'identifiant soit juste le nom soit juste le prenom
                    if(ca[0].equals(author.getUsername())){ // compare si c'est l'identifiant
                        added = true; //l'auteur a bien été ajouté dans la liste donc added est vrai
                        var nb = result.commitsPerAuthor.getOrDefault(author.getName(), 0); //recupere le nombre de commit pour l'auteur
                        result.commitsPerAuthor.put(author.getName(), nb + 1); //ajoute dans result le nom de l'auteur avec le nombre de commit
                    }
                    if( an.length == 2) {
                        if (an[0].equals(ca[0]) || an[1].equals(ca[0])) {//compare si c'est le nom ou le prenom
                            added = true;
                            var nb = result.commitsPerAuthor.getOrDefault(author.getName(), 0);
                            result.commitsPerAuthor.put(author.getName(), nb + 1);
                        }
                    }
                    if(an.length == 3){
                        if(an[2].equals(ca[0])){
                            added = true;
                            var nb = result.commitsPerAuthor.getOrDefault(author.getName(), 0);
                            result.commitsPerAuthor.put(author.getName(), nb + 1);
                        }
                    }
                }
                if(ca.length == 3) { //le cas ou il y a soit nom prenom ou prenom nom
                    if(an.length == 2) {
                        if(ca[1].equals(an[1]) || ca[0].equals(an[1]) || ca[0].equals(an[0])) {
                            added = true;
                            if (commitauthor.contains("colin")) {
                                var nb = result.commitsPerAuthor.getOrDefault("Colin Gonzales", 0);
                                result.commitsPerAuthor.put("Colin Gonzales", nb + 1);
                            } else {
                                var nb = result.commitsPerAuthor.getOrDefault(author.getName(), 0);
                                result.commitsPerAuthor.put(author.getName(), nb + 1);
                            }
                        }
                    }
                    if(an.length == 3){ // si l'auteur a 2 nom
                        if(ca[0].equals(an[2])){
                            added = true;
                            var nb = result.commitsPerAuthor.getOrDefault(author.getName(), 0);
                            result.commitsPerAuthor.put(author.getName(), nb + 1);
                        }
                    }
                }
                if(ca.length == 4){// le cas ou l'auteur a 2 nom de famille dans le nom du commit
                    if(an.length == 3){
                        if (an[2].equals(ca[2])){
                            added = true;
                            var nb = result.commitsPerAuthor.getOrDefault(author.getName(), 0);
                            result.commitsPerAuthor.put(author.getName(), nb + 1);
                        }
                    }
                }
            }
            if(!added){//si l'auteur n'a pas était ajouté a l'arrayList
                String [] ca = commit.author.split(" ");
                if(ca.length == 3){
                    Author au = new Author("Colin Gonzales","Colin Gonzales");
                    authors.add(au);
                    result.commitsPerAuthor.put("Colin Gonzales", 1);
                }
                if(ca.length == 2){
                    Author au = new Author(ca[0],ca[0]);
                    authors.add(au);
                    result.commitsPerAuthor.put(ca[0], 1);
                }
            }
        }

        Authors b = new Authors();
        ArrayList<Author> authors2 = new ArrayList<>();
        authors2.addAll(b.getAuthorsFromJson("SZwzzCpTr5ZhQYWFj8cK",1601));
        //on remplit ensuite commitPerAuthorAll -> contient le nb de commit par auteur dans toutes les branches donc dans la globalite
        for (var commit : gitLogall) {
            added = false;
            for (var author : authors2) {
                String commitauthor = commit.author.toLowerCase();
                String authorname = author.getName().toLowerCase();
                String [] ca = commitauthor.split(" ");
                String [] an = authorname.split(" ");
                if(ca.length == 2) {
                    if(ca[0].equals(author.getUsername())){
                        added = true;
                        var nb = result.commitsPerAuthorAll.getOrDefault(author.getName(), 0);
                        result.commitsPerAuthorAll.put(author.getName(), nb + 1);
                    }
                    if(an.length == 2) {
                        if (an[0].equals(ca[0]) || an[1].equals(ca[0])) {
                            added = true;
                            var nb = result.commitsPerAuthorAll.getOrDefault(author.getName(), 0);
                            result.commitsPerAuthorAll.put(author.getName(), nb + 1);
                        }

                    }
                    if(an.length == 3){
                        if(an[2].equals(ca[0])){
                            added = true;
                            var nb = result.commitsPerAuthorAll.getOrDefault(author.getName(), 0);
                            result.commitsPerAuthorAll.put(author.getName(), nb + 1);
                        }
                    }
                }
                if(ca.length == 3) {
                    if(an.length == 2) {
                        if(ca[1].equals(an[1]) || ca[0].equals(an[1]) || ca[0].equals(an[0])) {
                            added = true;
                            if (commitauthor.contains("colin")) {
                                var nb = result.commitsPerAuthorAll.getOrDefault("Colin Gonzales", 0);
                                result.commitsPerAuthorAll.put("Colin Gonzales", nb + 1);
                            } else {
                                var nb = result.commitsPerAuthorAll.getOrDefault(author.getName(), 0);
                                result.commitsPerAuthorAll.put(author.getName(), nb + 1);
                            }
                        }
                    }
                    if(an.length == 3){
                        if(ca[0].equals(an[2])){
                            added = true;
                            var nb = result.commitsPerAuthorAll.getOrDefault(author.getName(), 0);
                            result.commitsPerAuthorAll.put(author.getName(), nb + 1);
                        }
                    }
                }
                if(ca.length == 4){
                    if(an.length == 3){
                        if (an[2].equals(ca[2])){
                            added = true;
                            var nb = result.commitsPerAuthorAll.getOrDefault(author.getName(), 0);
                            result.commitsPerAuthorAll.put(author.getName(), nb + 1);
                        }
                    }
                }
            }
            if(!added){
                String [] ca = commit.author.split(" ");
                if(ca.length == 3){
                    Author au = new Author("Colin Gonzales","Colin Gonzales");
                    authors2.add(au);
                    result.commitsPerAuthorAll.put("Colin Gonzales", 1);
                }
                if(ca.length == 2){
                    Author au = new Author(ca[0],ca[0]);
                    authors2.add(au);
                    result.commitsPerAuthorAll.put(ca[0], 1);
                }
            }

        }

        // maintenant on regarde si tous les auteurs sont présents dans commitsPerAuthor
        for (Author nom : authors2) {
            if (! result.commitsPerAuthor.containsKey(nom.getName())) { //s'il n'est pas dedans on l'ajoute avec comme valeur 0
                result.commitsPerAuthor.put(nom.getName(), 0);
            }
        }
        //de même pour commitPerAuthorAll
        for (Author nom : authors) {
            if (! result.commitsPerAuthorAll.containsKey(nom.getName())) { //s'il n'est pas dedans on l'ajoute avec comme valeur 0
                result.commitsPerAuthorAll.put(nom.getName(), 0);
            }
        }
        return result;
    }
    
    /**
     * Lance le plugin
     */
    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()), Commit.parseLogFromCommandall(configuration.getGitPath()));
    }
    
    /**
     * Retourne un resultat (si il est null alors on le run d'abord)
     * @return
     */
    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }
    
    /**
     * Classe interne
     */
    static class  Result implements AnalyzerPlugin.Result {
        private Map<String, Integer> commitsPerAuthor = new HashMap<>();
        private Map<String, Integer> commitsPerAuthorAll = new HashMap<>();
        
        /**
         * Retourne le nombre de commit par auteur
         * @return  
         */
        Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }
        
        /**
         * Retourne le resultat sous forme de String
         * @return 
         */
        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
        }
        
        /**
         * Retourne le resultat sous forme de Div HTML 
         * Création de graphiques pour un affichage plus visuel
         * @return 
         */
        @Override
         // script obtenu en libre service sur canvasjs.com
        public String getResultAsHtmlDiv() {
            commitsPerAuthor = new TreeMap<String, Integer>(commitsPerAuthor);
            commitsPerAuthorAll = new TreeMap<String, Integer>(commitsPerAuthorAll);
            //creation d'un graphe circulaire pour voir la contribution dans la branche
            StringBuilder html_head2 = new StringBuilder("<script> window.onload = function () {\n" +
                    "var chartcclethis = new CanvasJS.Chart(\"chartContainer2\", {\nexportFileName: \"Doughnut Chart\", exportEnabled: true, animationEnabled: true,\n theme: \"light1\",\n" +
                    "title:{\n text: \"Contribution dans la branche\", \n horizontalAlign : \"center\" },\n" +
                    "data: [{\n" +
                    "   type: \"doughnut\", innerRadius:120,  \n toolTipContent: \"<b>{name}</b>: {y} (#percent%)\", indexLabel: \"{name} - #percent%\", dataPoints: [\n");
            StringBuilder html_body2 = new StringBuilder("");
            //creation d'un graphe circulaire pour voir la contribution dans le projet
            StringBuilder html_head3 = new StringBuilder(
                    "var chartccleall = new CanvasJS.Chart(\"chartContainer3\", {\nexportFileName: \"Doughnut Chart\", exportEnabled: true, animationEnabled: true,\n theme: \"light1\",\n" +
                            "title:{\n text: \"Contribution Totale\", \n horizontalAlign : \"center\" },\n" +
                            "data: [{\n" +
                            "   type: \"doughnut\", innerRadius:120,  \n toolTipContent: \"<b>{name}</b>: {y} (#percent%)\", indexLabel: \"{name} - #percent%\", dataPoints: [\n");
            StringBuilder html_body3 = new StringBuilder("");
            for (var item : commitsPerAuthor.entrySet()) {
                //remplis le graphe de pourcentage de contribution dans la branche
                html_head2.append("{ y : ").append(item.getValue()).append(", name : \"").append(item.getKey()).append("\" },\n");
            }
            for (var item : commitsPerAuthorAll.entrySet()) {
                //remplis le graphe de pourcentage de contribution totale
                html_head3.append("{ y : ").append(item.getValue()).append(", name : \"").append(item.getKey()).append("\" },\n");
            }
            html_head2.append("]\n" + "}]\n" + "});\n" + " chartcclethis.render();\n");
            html_body2.append("<div id=\"chartContainer2\" style=\"height: 500px; max-width: 80% auto; margin-bottom: 40px;\"></div>\n" +
                    "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
            
            html_head3.append("]\n" + "}]\n" + "});\n" + " chartccleall.render();\n");
            html_head3.append(
                    "function explodePie (e) {\n" +
                            "\tif(typeof (e.dataSeries.dataPoints[e.dataPointIndex].exploded) === \"undefined\" || !e.dataSeries.dataPoints[e.dataPointIndex].exploded) {\n" +
                            "\t\te.dataSeries.dataPoints[e.dataPointIndex].exploded = true;\n" +
                            "\t} else {\n" +
                            "\t\te.dataSeries.dataPoints[e.dataPointIndex].exploded = false;\n" +
                            "\t}\n" +
                            "\te.chart.render();\n" +
                            " \n}\n" +
                            "}\n" +
                            "</script>");
            html_body3.append("<div id=\"chartContainer3\" style=\"height: 500px; max-width: 80% auto;\"></div>\n" +
                    "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
            return html_head2.toString() + html_head3.toString() + html_body2.toString() + html_body3.toString();
        }
    }
}