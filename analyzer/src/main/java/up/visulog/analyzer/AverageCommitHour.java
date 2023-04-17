package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Plugin qui donne l'heure moyenne des commits
 */
public class AverageCommitHour implements AnalyzerPlugin {

    // Attributs
    private final Configuration configuration;
    private Result result;

    //Constructeur
    /**
     * Construit un object de type AverageCommitHour
     */
    public AverageCommitHour(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    

   //Methode
    /**
     * Recupere l'heure de tout les commits de la liste de commits et calcule la moyenne 
     * @param  gitLog
     * @return
     */
    Result processLog(List<Commit> gitLog) {

        var result = new Result();
        //Addition de toutes les secondes de chaque heure d'ajout de chaque commit par exemple un commit fait à 1h donne 3600 secondes

        int tailletotale=0; 

        // On regarde l'heure de tous les commits et on en fait une grande variable en secondes 

        for (var commit : gitLog){ 

            //On fait la conversion des heures en secondes ainsi que celles des minutes les champs hours,minutes,second réprésente les heures , minutes 
            //et secondes d'une date dans ce format ci : 00.00.00
            
            tailletotale += (commit.date.getHour() )*3600 
                         + (commit.date.getMinute())*60
                         + (commit.date.getSecond());
        }
        
        // On divise  la taille totale par le nombre de commit et on la remet en format horaire

            int compteur = tailletotale/gitLog.size();
            int minutes1 = compteur/60;
            int minutes2= minutes1 % 60;
            int heures = (minutes1-minutes2)/60;
            
            //On met l'heure moyenne sous forme de string car cela facilite l'affichage

            String heuremoyenne = String.valueOf(heures)+"."+String.valueOf(minutes2);
            result.HeureMoyenne = heuremoyenne;

       
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
     * Classe interne Result
     */
    static class Result implements AnalyzerPlugin.Result {

        // On retourne l'heure moyenne de tous les commits sous forme d'heure , on utilise le format 00.00
        private String HeureMoyenne;
        
        /**
         * Return HeureMoyenne
         * @return 
         */
        String getAverageCommitHour() { 
            return HeureMoyenne;
        }
        
        /**
         * Renvoie HeureMoyenne
         * @return 
         */
        public String getResultAsString() {
            return HeureMoyenne;
        }

        /**
         * Retourne le résultat sous forme html 
         * @return 
         */
        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<body style=\"background-color:#376c35; text-align:center;margin-top:450;font-size:275%;font-family:Avantgarde, TeX Gyre Adventor, URW Gothic L, sans-serif;\"> \n" +
                                                    "<div> Heure moyenne de tous les commits : <ul> \n" +
                                                    " <p style=\"color:#ffffff\";>");
            html.append(HeureMoyenne.toString());
            html.append("</ul></div> \n" +
                        "</body>");
            return html.toString();
        }
    }
}