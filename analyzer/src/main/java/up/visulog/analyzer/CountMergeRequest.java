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
public class CountMergeRequest implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    // constructeur 
    /**
     * Construit un object de type CountMergerequest
     * @param generalConfiguration
     */
    public CountMergeRequest(final Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    /**
     * Retourne un resultat en fonction de la liste de Commit
     */
    static Result processLog(List<Commit> gitLog) {
        Authors a = new Authors(); //creation d'une variable de type authors 
        ArrayList<Author> authors = new ArrayList<>();
        authors.addAll(a.getAuthorsFromJson("SZwzzCpTr5ZhQYWFj8cK",1601)); //creation de la collection qui contient tout les auteurs
        var result = new Result();
        for (var commit : gitLog) { //parcours de la liste de commit
            boolean added = false;
            for (var author : authors) { //parcours de collection d'auteur
                String commitauthor = commit.author.toLowerCase(); //convertie le nom de l'auteur du commit en minuscule 
                String authorname = author.getName().toLowerCase(); //convertie le nom de l'autheur dans la collection en minuscule
                String [] ca = commitauthor.split(" "); // separe le nom de l'auteur du commit en fonction des espace
                String [] an = authorname.split(" "); //separe le nom de l'auteur dans la collection en fonction des espace
                if(ca.length == 2) { // le cas ou le nom de l'auteur du commit et soit l'identifiant soit juste le nom soit juste le prenom
                    if(ca[0].equals(author.getUsername())){ // compare si c'est l'identifiant 
                        added = true;
                        if (commit.mergedFrom != null) {
                            int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                            result.mergesPerAuthor.put(author.getName(), nb + 1);
                        }
                    } 
                    if(an[0].equals(ca[0]) || an[1].equals(ca[0])){//compare si c'est le nom ou le prenom
                        added = true; 
                        if (commit.mergedFrom != null) {
                            int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                            result.mergesPerAuthor.put(author.getName(), nb + 1);
                        }
                    }
                    if(an.length == 3){ // si l'auteur a 2 nom
                        if(an[2].equals(ca[0])){ // compare si c'est le prenom
                            added = true;
                            if (commit.mergedFrom != null) {
                                int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                                result.mergesPerAuthor.put(author.getName(), nb + 1);
                            } 
                        }
                    }
                }
                if(ca.length == 3) { //le cas ou il y a soit nom prenom ou prenom nom
                    if(ca[1].equals(an[1]) || ca[0].equals(an[1])){
                        added = true;
                        if (commit.mergedFrom != null) {
                            int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                            result.mergesPerAuthor.put(author.getName(), nb + 1);
                        }
                    }
                    if(an.length == 3){ // si l'auteur a 2 nom 
                        if(ca[0].equals(an[2])){
                            added = true;
                            if (commit.mergedFrom != null) {
                                int nb = result.mergesPerAuthor.getOrDefault(author.getName(), 0);
                                result.mergesPerAuthor.put(author.getName(), nb + 1);
                            }
                        }
                    }
                }
                if(ca.length == 4){// le cas ou l'auteur a 2 nom de famille dans le nom du commit
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
            if(!added){
                Author au = new Author(commit.author, commit.author);
                authors.add(au);   
                if (commit.mergedFrom != null) {
                    result.mergesPerAuthor.put(commit.author, 1);
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
        this.result = CountMergeRequest.processLog(Commit.parseLogFromCommand(this.configuration.getGitPath()));
    }

    /**
     * Retourne un resultat (si il est null alors on le run d'abord)
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
         *  retoure le nombre de merge par auteur
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
        @Override
        public String getResultAsHtmlDiv() {
            mergesPerAuthor = new TreeMap<String,Integer>(mergesPerAuthor);
            StringBuilder htmlheadBuilder = new StringBuilder("<script> window.onload = function () {\n"+
            "var chart = new CanvasJS.Chart(\"chartContainer\", {\n animationEnabled: true,\n" +
            "title:{\n text: \"Merge Request par auteur\"\n,},\n"+
            "data : [{\n"+
            "type: \"doughnut\", startAngle: 60,indexLabelFontSize: 17,indexLabel: \"{label} - #percent%\",toolTipContent: \"<b {label}:</b> {y} (#percent%)\",\n dataPoints: [" );
            StringBuilder htmlbodyBuilder = new StringBuilder("");
            for(var item : mergesPerAuthor.entrySet() ){
                htmlheadBuilder.append("{ label : \"").append(item.getKey()).append("\", y : ").append(item.getValue()).append(" },\n");
            }
            htmlheadBuilder.append("]\n" + "}]\n" + "});\n" + " chart.render();}</script>\n" );
            htmlbodyBuilder.append("<div id=\"chartContainer\" style=\"height: 580px; width: 100%;\"></div>\n" +
                "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
            return htmlheadBuilder.toString() + htmlbodyBuilder.toString();
        }
    }
}
