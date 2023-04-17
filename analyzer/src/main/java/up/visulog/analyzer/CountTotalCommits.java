package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plugin qui compte le nombre de commits
 */
public class CountTotalCommits implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;
    
    //Constructeur
     /**
      * Construit un object de type CountTitalCommits
      * @param generalConfiguration
      */
    public CountTotalCommits(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    /**
     * //retourne un resultat en fonction de la liste de Commit
     * @param gitLog
     * @return
     */
    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            result.TotalCommits++;
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
    static class Result implements AnalyzerPlugin.Result {
        private int TotalCommits = 0;

        /**
         * Retourne TotalCommits
         * @return
         */
        int getTotalCommits() {
            return TotalCommits;
        }

        /**
         * Retourne le resultat sous forme de String
         * @return
         */
        @Override
        public String getResultAsString() {
            return Integer.toString(TotalCommits);
        }

        /**
         * Retourne le resultat sous forme de Div HTML
         * @return
         */
        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<body style=\"background-color:#ffffff; text-align:center;margin-top:450;font-size:275%;font-family:Avantgarde, TeX Gyre Adventor, URW Gothic L, sans-serif;\"> \n" +
                                                    "<div> Nombre de commit : <ul> \n" +
                                                    " <p style=\"color:#000000\";>");
            html.append(TotalCommits);
            html.append("</ul></div> \n" +
                        "</body>");
            return html.toString();
        }
    }
}
