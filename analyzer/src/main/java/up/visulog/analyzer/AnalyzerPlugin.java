package up.visulog.analyzer;

import java.lang.module.Configuration;

/**
 * Une interface qui donne la structure générale des plugins : des méthodes de lancement
 */
public interface AnalyzerPlugin extends Runnable {

    /**
     *  Une interface qui contient des getters pour les résultats de type général Result (elle-même une sous-interface de AnalyserPlugin)
     */
    interface Result {
        String getResultAsString();
        String getResultAsHtmlDiv();
    }

    /**
     * lance l'analyse du plugin
     */
    void run();

    /**
     *
     * @return le resultat de cette analyse. Lance l'analyse d'abord si ça n'a pas été fait.
     */
    Result getResult();
}
