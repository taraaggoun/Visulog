package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Plugin qui renvoie le nombre de commits par date
 */
public class CommitsPerDatePlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;
    
    //Constructeur
    /**
     * Construit un object de type CommitsPerDatePlugin
     * @param generalConfiguration
     */
    public CommitsPerDatePlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    
    //Methodes
    /**
     * Retourne un resultat en fonction de la liste de Commit
     * @param gitLog
     * @return
     */
    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        String[][] gen = new String[0][3]; //date, prenom, nombre commit
        //tableau servant à mettre en pos 0 la date et en pos 1 le nombre de commits
        String[][] tab = new String[0][0];
        //liste de date pour pouvoir ajouter les dates dans la Map
        ArrayList<Date> dat = new ArrayList<>();
        //format de date servant pour un meilleur affichage : on n'a pas l'heure !
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        // pour chaque commit,
        // s'il est dans le tableau, on incrémente son nombre de commit
        // s'il n'est pas dans le tableau, on l'ajoute et on ajoute la date à la liste
        for (var commit : gitLog){
            //Date -> String pour être utilisé dans le tableau
            Date dateform = Date.from(commit.date.atZone(ZoneId.systemDefault()).toInstant());
            String date = dateFormat.format(dateform);
            //vérifie si la date est dans le tableau
            int pos = estPresent(tab, date);
            //cas positif
            if ( pos != -1){
                int compt = Integer.parseInt(tab[pos][1]) + 1;
                tab[pos][1] = String.valueOf(compt);
            }
            //cas négatif
            else {
                tab = ajout(tab,date);
                dat.add(dateform);
            }
            // regarde si contient la date
            boolean datehere = false;
            boolean namehere = false;
            for (int i=0;i<gen.length;i++){
                if (gen[i][0].equals(date)){
                    datehere = true;
                        if (gen[i][1].equals(commit.author)){
                            namehere = true;
                            int compt = Integer.parseInt(gen[i][2]) + 1;
                            gen[i][2] = String.valueOf(compt);
                    }
                }
            }
            if (!datehere){
                gen = ajoutdate(gen,date,commit.author);
            }
            if (datehere && !namehere){
                gen = ajoutdate(gen,date,commit.author);
            }
            
        }
        // pour chaque ligne, on recupère les données pour les ajouter à result
        for (int i=0; i<tab.length;i++) {
            result.commitsPerDate.put(dat.get(i), Integer.valueOf(tab[i][1]));
        }
        result.general = gen;
        //retourne donc un résultat de commits par date (trié chronologiquement)
        return result;
    }
    /**
     * fonction regardant si le string voulu se trouve dans le tableau et retourne dans ce cas son indice
     * sinon -1
     * @param tab
     * @param a
     * @return 
     */
    static int estPresent(String[][] tab, String a){
        for (int i=0;i<tab.length;i++){
            if (tab[i][0].equals(a)){
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Ajout le string voulu à  la fin du tableau
     * @param tab
     * @param a
     * @return 
     */
    static String[][] ajout(String[][] tab, String a){
        String[][] res = new String[tab.length + 1][2];
        for (int i = 0;i<tab.length;i++){
            res[i][0] = tab[i][0];
            res[i][1] = tab[i][1];
        }
        res[tab.length][0] = a;
        res[tab.length][1] = Integer.toString(1);
        return res;
    }
    
    /**
     * Ajout d'une date
     * @param tab
     * @param a
     * @param b
     * @return
     */
    static String[][] ajoutdate(String[][] tab, String a, String b){
        String[][] res = new String[tab.length + 1][3];
        for (int i = 0;i<tab.length;i++){
            res[i][0] = tab[i][0];
            res[i][1] = tab[i][1];
            res[i][2] = tab[i][2];
        }
        res[tab.length][0] = a;
        res[tab.length][1] = b;
        res[tab.length][2] = Integer.toString(1);
        return res;
    }
    
    /**
     * lance le plugin
     */
    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommandall(configuration.getGitPath()));
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
        private Map<Date, Integer> commitsPerDate = new HashMap<>();
        private String[][] general;
        
        /**
         * Retourne le nombre de commit par date
         * @return
         */
        Map<Date, Integer> getCommitsPerAuthor() {
            return commitsPerDate;
        }
        
        /**
         * Retourne le resultat sous forme de String
         * @return
         */
        @Override
        public String getResultAsString() {
            return commitsPerDate.toString();
        }
        
        /**
        * Retourne le resultat sous forme de Div HTML
        * @return 
        */
        // script obtenu en libre service sur canvasjs.com
        @Override
        public String getResultAsHtmlDiv() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
            commitsPerDate = new TreeMap<Date, Integer>(commitsPerDate);
            //création d'un tableau pour les informations détaillées par jour
            StringBuilder html_head = new StringBuilder("<script> var MonTableau = [\n");
            for (String[] strings : general) {
                html_head.append("[ \"").append(strings[0]).append("\", \"").append(strings[1]).append("\", ").append(strings[2]).append("],\n");
            }
            html_head.append("\n]\n</script>\n<script>\n");
            //création d'un graph en barre representant le nombre de commits par jour
            html_head.append("window.onload = function () {" +
                    "var chart = new CanvasJS.Chart(\"chartContainer\", {\n" +
                    "                        animationEnabled: true,\n" +
                    "                        theme: \"light2\",\n" +
                    "                        title:{\n" +
                    "                    text: \"Nombre de Commits par jours\"\n" +
                    "                },\n" +
                    "                axisY: {\n" +
                    "                    title: \"Total\"\n" +
                    "                },\n" +
                    "                data: [{\n" +
                    "                    type: \"column\",\n" +
                    "                    click: onClick,"+
                    "                            showInLegend: false,\n" +
                    "                            color: \"#8c9eff\","+
                    "                            dataPoints: [");
            StringBuilder html_body = new StringBuilder("");
            for (var item : commitsPerDate.entrySet()) {
                String date = dateFormat.format(item.getKey());
                html_head.append("{ y : ").append(item.getValue()).append(", label : \"").append(date).append("\" },\n");
            }
            html_head.append("]\n" + "}]\n" + "});\n" + " chart.render();\n");
            //fonction permettant l'affichage du tableau et de ses données pour le jour correspondant
            html_head.append("function onClick(e) {\n" +
                    "\n" +
                    "                var txt = \"<table id=\\\"table_date\\\"><caption>Le  \" + e.dataPoint.label + \" </caption><tr><th>Nom</th><th>Nombre de Commits</th></tr>\";\n" +
                    "                for (var i = 0; i < MonTableau.length; i++) {\n" +
                    "                    if (MonTableau[i][0] == e.dataPoint.label) {\n" +
                    "                        txt += \"<tr><td>\" + MonTableau[i][1] + \"</td><td>\" + MonTableau[i][2] + \"</td></tr>\";\n" +
                    "                    }\n" +
                    "                }\n" +
                    "                txt += \"<tr><td>Total</td><th>\" + e.dataPoint.y + \"</th></tr>\";\n" +
                    "                document.getElementById(\"tests\").innerHTML = txt + \"</table>\";\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "    </script>");
            
            
            html_body.append("<div id=\"chartContainer\" style=\"height: 560px;widht:100%;\"></div>\n" +
                    "    <div id=\"tests\" style=\" font-size: 15px; margin-left:22%;margin-top:40px;\"></div>\n" +
                    "    <script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
            return html_head.toString() + html_body.toString();
        }
    }
}