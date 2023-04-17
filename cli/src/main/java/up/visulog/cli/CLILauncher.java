package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * crée un nouvel Analyser et va s’occuper de le remplir, lancer ses plugins, et afficher ses résultats, à partir d’une configuration lue depuis la ligne de commande. 
 */
public class CLILauncher {
    /**
     * Methode princital qui permet l'execution des autre
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            var res = results.toHTML();
            try {
                //ecrit sur le fichier result.html le resultat results.toHTML()
                generateHtmlFile(res);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        } else displayHelpAndExit();
    }

    /**
     * lit la ligne de commande et la décompose en arguments. 
     * @param args
     * @return optional<Configuration>
     */
    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
        var gitPath = FileSystems.getDefault().getPath(".");
        var plugins = new HashMap<String, PluginConfig>();
        for (var arg : args) {
            if (arg.startsWith("--")) {
                String[] parts = arg.split("=");
                if (parts.length != 2) return Optional.empty();
                else {
                    String pName = parts[0];
                    String pValue = parts[1];
                    switch (pName) {
                        case "--addPlugin":
                            // TODO: parse argument and make an instance of PluginConfig
                            // Let's just trivially do this, before the TODO is fixed:

                            if (pValue.equals("countTotal")) plugins.put("CountTotalCommits", new PluginConfig() {});
                            else if (pValue.equals("dailyGraph")) plugins.put("ActivityUserGraphPlugin", new PluginConfig() {});
                            else if (pValue.equals("hourGraph")) plugins.put("ActivityUserGraphPluginHour", new PluginConfig() {});
                            else if (pValue.equals("weekGraph")) plugins.put("ActivityUserGraphPluginWeekday", new PluginConfig() {});
                            else if (pValue.equals("commitsPerAuthor")) plugins.put("CountCommitsPerAuthorPlugin", new PluginConfig() {});
                            else if (pValue.equals("countLines")) plugins.put("CountLinesperAuthorPlugin", new PluginConfig() {});
                            else if(pValue.equals("countMergeRequest")) plugins.put("CountMergeRequest", new PluginConfig() {});
                            else if (pValue.equals("countCommitsPerBranch")) plugins.put("CountCommitPerBranchPlugin", new PluginConfig() {});
                            else if(pValue.equals("averageCommitHour")) plugins.put("AverageCommitHour", new PluginConfig() {});
                            else if(pValue.equals("mergeRequest")) plugins.put("MergeRequest", new PluginConfig() {});
                            else if(pValue.equals("contributionLines")) plugins.put("ContributionLines", new PluginConfig() {});
                            else if(pValue.equals("contribution")) plugins.put("ContributionPlugin", new PluginConfig() {});
                            else if(pValue.equals("commitsPerDate")) plugins.put("CommitsPerDatePlugin", new PluginConfig() {});
                            else if(pValue.equals("mergeRequestPerDate")) plugins.put("MergeRequestPerDate", new PluginConfig() {});
                          
                            else displayHelpAndExit();

                            break;
                        case "--loadConfigFile":
                            // TODO (load options from a file)
                            break;
                        case "--justSaveConfigFile":
                            // TODO (save command line options to a file instead of running the analysis)
                            break;
                        default:
                            return Optional.empty();
                    }
                }
            } else {
                gitPath = FileSystems.getDefault().getPath(arg);
            }
        }
        return Optional.of(new Configuration(gitPath, plugins));
    }
    /**
     * Affiche la liste des option et leurs syntaxe quand on lance une mauvaise commande
     */
    private static void displayHelpAndExit() {
        System.out.println("Wrong command...");
	System.out.println("--addPlugin allows you to add new plugins");
	System.out.println("'--addPlugin=countTotal' shows the total number of commits.");
	System.out.println("'--addPlugin=dailyGraph' shows the author's activity by the number of his commits in the day.");
	System.out.println("'--addPlugin=hourGraph' shows the number of commits per hour for each author.");
	System.out.println("'--addPlugin=weekGraph' shows the author's activity by the number of his commits in the week.");
	System.out.println("'--addPlugin=commitsPerAuthor' shows the number of commits per author.");
	System.out.println("'--addPlugin=countLines' shows the number of lines added and deleted per author.");
	System.out.println("'--addPlugin=countMergeRequest' shows the percentage and the number of merge requests per author with a diagram in pie chart.");
	System.out.println("'--addPlugin=countCommitsPerBranch' shows the total number of commits in each branch.");
	System.out.println("'--addPlugin=averageCommitHour' shows the average time of commits.");
	System.out.println("'--addPlugin=mergeRequest' shows the number of merge requests per author with a diagram in bar chart.");
	System.out.println("'--addPlugin=contributionLines' shows the percentage and the number of lines added and deleted per author with diagrams in pie chart.");
	System.out.println("'--addPlugin=contribution' shows each author's contribution as a percentage (in the current branch and in the whole project)");
	System.out.println("'--addPlugin=commitsPerDate' shows the total activity per day by the number of commits in the day");
	System.out.println("'--addPlugin=mergeRequestPerDate' shows the total number of merge request per day.");
	System.out.println("--loadConfigFile allows you to load options from a file");
        System.out.println("--justSaveConfigFile allows you to save command line options to a file instead of running the analyses");
        System.out.println("The last option return an empty Optional instance");
        System.exit(0);
    }

    /**
     * Génère la page HTML des plugins
     * @param res
     * @throws IOException
     */
    private static void generateHtmlFile(String res) throws IOException {
        Path fichier = Paths.get("result.html");
        Files.write(fichier, Collections.singleton(res));
        File htmlFile = new File("result.html");
        Desktop.getDesktop().browse(htmlFile.toURI());
    }
}
