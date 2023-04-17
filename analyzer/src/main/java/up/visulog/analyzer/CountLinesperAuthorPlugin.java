package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.Author;
import up.visulog.gitrawdata.Authors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * Plugin qui compte le nombre de lignes ajoutées/supprimées par auteur
 */
public class CountLinesperAuthorPlugin implements AnalyzerPlugin {
    //Attributs
    private final Configuration configuration; //type Configuration
    private Result result; // type AnalyzerResult

    //Constructeur 
    /**
     * Construit un object de type CountLinesperAuthorPlugin
     * @param generalConfiguration
     */
    public CountLinesperAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    //Methode
     /**
    * Récupere le nombre de ligne ajoutées et supprimées de chaque commit dans la liste de commits 
    * @param gitLog
    * @return
    */
    static Result processLog(List<Commit> gitLog) {

        Authors a = new Authors(); //creation d'une variable de type authors 
        ArrayList<Author> authors = new ArrayList<>();//creation d'une ArrayListe qui va contenir tout les auteurs
        authors.addAll(a.getAuthorsFromJson("SZwzzCpTr5ZhQYWFj8cK",1601));// ajouts de tout les membres du projet a authors

        boolean added; //boolean qui sert a savoir si l'auteur a été ajouté ou non a authors
        var result = new Result(); //initalisation de la variable result

        for (var commit : gitLog) { //parcours de la liste de commit
            added = false;//initialisation du boolean a false;

            for (var author : authors) { //parcours de collection d'auteur

                String commitauthor = commit.author.toLowerCase(); //convertie le nom de l'auteur du commit en miniscule 
                String authorname = author.getName().toLowerCase(); //convertie le nom de l'authors dans authors en minuscule
                String [] ca = commitauthor.split(" "); // sépare le nom de l'auteur du commit en fonction des espaces
                String [] an = authorname.split(" "); //sépare le nom de l'auteur dans authors en fonction des espaces
                
                if(ca.length == 2) { // le cas ou le nom de l'auteur du commit et soit l'identifiant soit juste le nom soit juste le prenom
                    if(ca[0].equals(author.getUsername())){ // compare si c'est l'identifiant 
                        added = true; //l'auteur a bien était ajouté dans la liste
                        var nb = result.Added.getOrDefault(author.getName(), 0); //récupere le nombre de lignes ajoutés pour l'auteur
                        result.Added.put(author.getName(), nb + commit.linesAdded); //ajoute dans la map  Added de result le nombre le lignes ajoutés a son auteur
                        var d = result.Deleted.getOrDefault(author.getName(), 0);//récupere le nombre de lignes supprimés pour l'auteur
                        result.Deleted.put(author.getName(), d + commit.linesDeleted);//ajoute dans la map Deleted de result le nombre le lignes supprimés a son auteur
                    } 

                    if(an[0].equals(ca[0]) || an[1].equals(ca[0])){ //compare si c'est le nom ou le prenom
                        added = true; //l'auteur a bien était ajouté dans la liste
                        var nb = result.Added.getOrDefault(author.getName(), 0); //récupere le nombre de lignes ajoutés pour l'auteur
                        result.Added.put(author.getName(), nb + commit.linesAdded);  //ajoute dans la map Added de result le nombre le lignes ajoutés a son auteur
                        var d = result.Deleted.getOrDefault(author.getName(), 0);//récupere le nombre de ligne supprimés pour l'auteur
                        result.Deleted.put(author.getName(), d + commit.linesDeleted);//ajoute dans la map Deletd de result le nombre le lignes supprimés a son auteur
                    }
                    if(an.length == 3){ // si l'auteur a 2 noms
                        if(an[2].equals(ca[0])){ // compare si c'est le même prenom
                            added = true; //l'auteur a bien était ajouté dans la liste
                            var nb = result.Added.getOrDefault(author.getName(), 0); //récupere le nombre de lignes ajoutés pour l'auteur
                            result.Added.put(author.getName(), nb + commit.linesAdded); //ajoute dans la map Added de result le nombre le lignes ajoutés a son auteur
                            var d = result.Deleted.getOrDefault(author.getName(), 0);//récupere le nombre de lignes supprimés pour l'auteur
                            result.Deleted.put(author.getName(), d + commit.linesDeleted);//ajoute dans la map Deletd de result le nombre le lignes supprimés a son auteur
                        }
                    }
                }

                if(ca.length == 3) { //le cas ou commit.author est de la forme nom prenom ou prenom nom
                    if(ca[1].equals(an[1]) || ca[0].equals(an[1])){
                        added = true; //l'auteur a bien était ajouté dans la liste
                        var nb = result.Added.getOrDefault(author.getName(), 0); //récupere le nombre de lignes ajoutés pour l'auteur
                        result.Added.put(author.getName(), nb + commit.linesAdded); //ajoute dans la map Added de result le nombre le lignes ajoutés a son auteur
                        var d = result.Deleted.getOrDefault(author.getName(), 0);//récupere le nombre de lignes supprimés pour l'auteur
                        result.Deleted.put(author.getName(), d + commit.linesDeleted); //ajoute dans la map Deleted de result le nombre le lignes supprimés a son auteur
                    }
                    if(an.length == 3){ // si l'auteur a 2 noms 
                        if(ca[0].equals(an[2])){
                            added = true; //l'auteur a bien était ajouté dans la liste
                            var nb = result.Added.getOrDefault(author.getName(), 0); //récupere le nombre de lignes ajoutés pour l'auteur
                            result.Added.put(author.getName(), nb + commit.linesAdded); //ajoute dans la map Added dans result le nombre le lignes ajoutés a son auteur  
                            var d = result.Deleted.getOrDefault(author.getName(), 0); //récupere le nombre de lignes supprimés pour l'auteur
                            result.Deleted.put(author.getName(), d + commit.linesDeleted); //ajoute dans la map Deleted de result le nombre le lignes supprimés a son auteur
                        }
                    }
                }
                if(ca.length == 4){// le cas ou l'auteur a 2 noms
                    if(an.length == 3){
                        if (an[2].equals(ca[2])){
                            added = true;//l'auteur a bien était ajouté dans la liste
                            var nb = result.Added.getOrDefault(author.getName(), 0); //récupere le nombre de lignes ajoutés pour l'auteur
                            result.Added.put(author.getName(), nb + commit.linesAdded); //ajoute dans la map Added de result le nombre le lignes ajoutés a son auteur
                            var d = result.Deleted.getOrDefault(author.getName(), 0);//récupere le nombre de lignes supprimés pour l'auteur
                            result.Deleted.put(author.getName(), d + commit.linesDeleted); //ajoute dans la map Deleted de result le nombre le lignes supprimés a son auteur
                        }
                    }
                }
            }
            if(!added){ //si l'auteur n'a pas été ajouté a l'arrayList
            String [] ca = commit.author.split(" "); // sépare le nom de l'auteur du commit en fonction des espaces
                if(ca.length == 3){
                    Author au = new Author(ca[0]+ " " + ca[1],ca[0]+ " " + ca[1]); //création d'un nouvel auteur
                    authors.add(au); //ajout de l'auteur à l'arrayList
                    result.Added.put(ca[0]+ " " + ca[1], commit.linesAdded); //ajoute dans la map Added de result le nombre le lignes ajoutés a son auteur
                    result.Deleted.put(ca[0]+ " " + ca[1], commit.linesDeleted); //ajoute dans la map Deleted de result le nombre le lignes supprimés a son auteur
                }
                if(ca.length == 2){
                    Author au = new Author(ca[0] + " _", ca[0]); //création d'un nouvel auteur
                    authors.add(au); //ajout de l'auteur à l'arrayList
                    result.Added.put(ca[0], commit.linesAdded); //ajoute dans la map Added de result le nombre le lignes ajoutés a son auteur
                    result.Deleted.put(ca[0], commit.linesDeleted); //ajoute dans la map Deleted de result le nombre le lignes supprimés a son auteur
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
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    /**
     * Retourne un result, si il est null appelle run() d'abord
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
        // attributs map qui associe un auteur a son nombre de ligne
        private Map<String, Integer> Added = new HashMap<>(); //pour les ligne ajoutés
        private Map<String, Integer> Deleted = new HashMap<>(); //pour les lignes supprimés
        
        //Getteur
        /**
         * Renvoie la map Added
         * @return
         */
        Map<String, Integer> getAdded() {
            return Added;
        }
         /**
          * Renvoie la map Deleted
          * @return 
          */
        Map<String, Integer> getDeleted() {
            return Deleted;
        }
       
        /**
         * Retourne les Map sous forme d'une chaine de caractères
         * @return
         */
        @Override
        public String getResultAsString() {
            return Added.toString() + Deleted.toString();
        }

        /**
         * retourne les Map sous forme de un graphique avec des colones, chaque auteur a une colone ligne ajoutés et une ligne supprimés
         * @return
         */
        // script obtenu en libre service sur canvasjs.com
        @Override
        public String getResultAsHtmlDiv() {
            Added = new TreeMap<String, Integer>(Added);
            Deleted = new TreeMap<String, Integer>(Deleted);
            StringBuilder html_head1 = new StringBuilder("<script> window.onload = function () {\n" +
                "var chartbat = new CanvasJS.Chart(\"chartContainer1\", {\n animationEnabled: true,\n theme: \"light2\",\n" +
                "title:{\n text: \"Nombre de lignes par auteurs \"\n,},\n" +
                "axisY: {\n title: \"Nombre de lignes\",\n},\n" +
                "axisY2: {\n title: \"Nombre de lignes\",\n},\n" +
                "toolTip: { shared: true }, " +
                "legend: { cursor:\"pointer\" },"+
                "data: [{\n" +
                "   type: \"column\",\n" + "name : \"Lignes ajout&eacutes\", legendText:\"Lignes ajoutés\", showInLegend: true,\n dataPoints: [");
            StringBuilder html_body1 = new StringBuilder("");
            for (var item : Added.entrySet()) {
                html_head1.append("{ label : \"").append(item.getKey()).append("\", y : ").append(item.getValue()).append(" },\n");
            }
            html_head1.append("]},{ type : \"column\", name : \"Lignes supprim&eacutes\", legendText:\"Lignes supprimés\", axisYType: \"secondary\", showInLegend: true, dataPoints:[");
            for (var item : Deleted.entrySet()) {
               html_head1.append("{ label : \"").append(item.getKey()).append("\", y : ").append(item.getValue()).append(" },\n");
            }
            html_head1.append("]\n" + "}]\n" + "});\n" + " chartbat.render();}</script>\n" );
            html_body1.append("<div id=\"chartContainer1\" style=\"height: 580px; width: 100%;\"></div>\n" +
                "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
            return html_head1.toString() + html_body1.toString();
        }
    }
}
