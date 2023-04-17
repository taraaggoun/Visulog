package up.visulog.analyzer;

import java.util.List;

/**
 *  La liste des résultats de chaque plugin de l’Analyser. 
 */
public class AnalyzerResult {

    //Attribut de type Liste de AnalyzerPlugin.Result
    private final List<AnalyzerPlugin.Result> subResults;

    //Getteur
    /**
     * Retourne subResults
     * @return 
     */
    public List<AnalyzerPlugin.Result> getSubResults() {
        return subResults;
    }

    //Constructeur
    /**
     * Construit un objet de type AnalyzerResult
     * @param subResults
     */
    public AnalyzerResult(List<AnalyzerPlugin.Result> subResults) {
        this.subResults = subResults;
    }

    /**
     * Retourne un resultat sous forme de String
     * @return
     */
    @Override
    public String toString() {
        return subResults.stream().map(AnalyzerPlugin.Result::getResultAsString).reduce("", (acc, cur) -> acc + "\n" + cur);
    }

    /**
     * Retourne un resultat sous forme HTML
     * @return
     */
    public String toHTML() {
        return "<html>\n<head>\n<title>Resultats</title>\n<link href=\"../cli/fond.css\" rel=\"stylesheet\" type=\"text/css\">\n</head>\n<body>\n"+subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur) + "</body></html>";
    }
}
