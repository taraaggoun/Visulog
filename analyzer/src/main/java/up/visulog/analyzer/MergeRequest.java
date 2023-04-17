package up.visulog.analyzer;

import up.visulog.gitrawdata.Commit;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Author;
import up.visulog.gitrawdata.Authors;

import java.util.TreeMap;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Plugin qui compte le nombre de merges request 
 */
public class MergeRequest implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;
    
    // Constructeur
    /**
     *Construit un object de type MergeRequest 
     * @param generalConfiguration
     */ 
    public MergeRequest(final Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    /**
     * Retourne un résultat en fonction de la liste de Commit
     * @param gitLog
     * @return
     */
    static Result processLog(List<Commit> gitLog) {
        Authors a = new Authors(); //création d'une variable de type authors 
        ArrayList<Author> authors = new ArrayList<>();
        authors.addAll(a.getAuthorsFromJson("SZwzzCpTr5ZhQYWFj8cK",1601)); //création de la collection qui contient tout les auteurs
        var result = new Result();
        for (var commit : gitLog) { //parcours de la liste de commit
            boolean added = false;
            for (var author : authors) { //parcours de collection d'auteur
                String commitauthor = commit.author.toLowerCase(); //convertit le nom de l'auteur du commit en minuscule 
                String authorname = author.getName().toLowerCase(); //convertit le nom de l'autheur dans la collection en minuscule
                String [] ca = commitauthor.split(" "); // sépare le nom de l'auteur du commit en fonction des espace
                String [] an = authorname.split(" "); //sépare le nom de l'auteur dans la collection en fonction des espaces
                if(ca.length == 2) { // le cas où le nom de l'auteur du commit est : soit l'identifiant, soit juste le nom, soit juste le prenom
                    if(ca[0].equals(author.getUsername())){ // compare si c'est l'identifiant 
                        added = true;
                        if (commit.mergedFrom != null) {
                            int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                            result.mergesPerAuthor.put(author.getName(), nb + 1);
                        }
                    } 
                    if(an[0].equals(ca[0]) || an[1].equals(ca[0])){ //compare si c'est le nom ou le prenom
                        added = true; 
                        if (commit.mergedFrom != null) {
                            int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                            result.mergesPerAuthor.put(author.getName(), nb + 1);
                        }
                    }
                    if(an.length == 3){ // si l'auteur a 2 noms
                        if(an[2].equals(ca[0])){ // compare si c'est le prénom
                            added = true;
                            if (commit.mergedFrom != null) {
                                int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                                result.mergesPerAuthor.put(author.getName(), nb + 1);
                            } 
                        }
                    }
                }
                if(ca.length == 3) { // le cas où il y a soit nom prenom ou prenom nom
                    if(ca[1].equals(an[1]) || ca[0].equals(an[1])){
                        added = true;
                        if (commit.mergedFrom != null) {
                            int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                            result.mergesPerAuthor.put(author.getName(), nb + 1);
                        }
                    }
                    if(an.length == 3){ // si l'auteur a 2 noms
                        if(ca[0].equals(an[2])){
                            added = true;
                            if (commit.mergedFrom != null) {
                                int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                                result.mergesPerAuthor.put(author.getName(), nb + 1);
                            }
                        }
                    }
                }
                if(ca.length == 4){ // le cas ou l'auteur a 2 noms de famille dans le nom du commit
                    if(an.length == 3){
                        if (an[2].equals(ca[2])){
                            added = true;
                            if (commit.mergedFrom != null) {
                                int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                                result.mergesPerAuthor.put(author.getName(), nb + 1);
                            }
                        }
                    }
                }
            }
            if(!added){ //si l'auteur n'a pas été ajouté a l'arrayList
            String [] ca = commit.author.split(" "); // sépare le nom de l'auteur du commit en fonction des espaces
                if(ca.length == 3){
                    Author au = new Author(ca[0]+ " " + ca[1],ca[0]+ " " + ca[1]); //création d'un nouvel auteur
                    authors.add(au); //ajout de l'auteur à l'arrayList
                    if (commit.mergedFrom != null) {
                        result.mergesPerAuthor.put(ca[0]+" " +ca[1], 1); //ajoute dans result le nom de l'auteur avec le nombre de commits
                    }
                }
            }
        }
        return result;
    }

    /**
     * Lance le plugin
     */
    @Override
    public void run() {
        this.result = MergeRequest.processLog(Commit.parseLogFromCommand(this.configuration.getGitPath()));
    }

    /**
     * Retourne un résultat (si il est null alors on le run d'abord)
     * @return
     */
    @Override
    public Result getResult() {
        if (this.result == null) {
            this.run();
        }
        return this.result;
    }

    /**
     * Classe interne
     */
    static class Result implements AnalyzerPlugin.Result {
        private Map<String, Integer> mergesPerAuthor = new HashMap<>();

        /**
         * Retourne le nombre de merge par auteur
         * @return 
         */
        Map<String, Integer> getMergesPerAuthor() {
            return this.mergesPerAuthor;
        }

        /**
         * Retourne le resultat sous forme de String
         * @return 
         */
        @Override
        public String getResultAsString() {
            return this.mergesPerAuthor.toString();
        }

        /**
         * Retourne le resultat sous forme de Div HTML
         * @return 
         */
        // script obtenu en libre service sur canvasjs.com
        public String getResultAsHtmlDiv() {
            mergesPerAuthor = new TreeMap<String, Integer>(mergesPerAuthor);
            //création d'un tableau en barre pour visualiser les commits par auteur dans cette branche et au total dans le projet
            StringBuilder html_head1 = new StringBuilder("<script> window.onload = function () {\n" +
                "var chartbat = new CanvasJS.Chart(\"chartContainer1\", {\n animationEnabled: true,\n theme: \"light 2\",\n" +
                "title:{\n text: \"Nombre de merges par auteurs \"\n,},\n" +
                "toolTip: { shared: true }, " +
                "data: [{\n" +
                "   type: \"column\",\n" + "name : \"Merges\",\n dataPoints: [");
            StringBuilder html_body1 = new StringBuilder("");
            for (var item : mergesPerAuthor.entrySet()) {
                //remplis les premières colonnes qui correspondent aux commits dans la branche
                html_head1.append("{ label : \"").append(item.getKey()).append("\", y : ").append(item.getValue()).append(" },\n");
            }
            html_head1.append("]\n" + "}]\n" + "});\n" + " chartbat.render();}</script>\n" );
            html_body1.append("<div id=\"chartContainer1\" style=\"height: 700px; width: 100%;\"></div>\n" +
                "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
            return html_head1.toString() + html_body1.toString();
        }
    }
}