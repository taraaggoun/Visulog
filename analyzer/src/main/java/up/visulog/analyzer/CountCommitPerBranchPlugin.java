package up.visulog.analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import up.visulog.config.Configuration;

/**
 * Plugin qui compte le nombre de commits par branche
 */
public class CountCommitPerBranchPlugin implements AnalyzerPlugin {

	private final Configuration configuration;
	private static Long numberOfCommit;
	private String uriGitRepo;
	private static String brancheName;
	static boolean exist = true;
	static Map<String, Long> listOfBranchesNames = new HashMap<>();

	/**
	 * Construit un object de type CountCommitPerBranchPlugin
	 * @param generalConfiguration
	 */
	public CountCommitPerBranchPlugin(Configuration generalConfiguration) {
		this.configuration = generalConfiguration;
	}
	
	/**
	 * Construit un object de type CountCommitPerBranchPlugin
	 */
	public CountCommitPerBranchPlugin() {
		this.configuration = null;	
	}
	
	/**
	 * Fonction qui va retourner le repertoire .git
	 * @return
	 */
	public String getUriGitRepo() {

		String userDirectory = Paths.get("").toAbsolutePath().getParent().toString();
		String uriGit = null;
		File file = new File(userDirectory);
		String[] names = file.list();

		for (String name : names) {
			if (name.equals(".git")) {
				uriGit = userDirectory + "/" + name;
				break;
			}
		}
		return uriGit;
	}

	/**
	 * Retourne une liste avec le nom des branches
	 * @return
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public List<String> getBranchName() throws IOException, GitAPIException {

		Repository repository = createRepo();
		List<String> listOfBranchesNames = new ArrayList<>();
		Git git = new Git(repository);
		String nom = git.getRepository().getBranch();
		return listOfBranchesNames;
	}

	/**
	 * Fonction qui permetde creer un repertoire git a partir de getUriGitRepo()
	 * @throws IOException
	 * @return 
	 */
	public Repository createRepo() throws IOException {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		uriGitRepo = getUriGitRepo();
		Repository repository = repositoryBuilder.setGitDir(new File(uriGitRepo)).readEnvironment().findGitDir()
				.setMustExist(true).build();
		return repository;
	}

	/**
	 * Fonction qui retourne une Map avec les noms des branches et le nombre de commits dans ces branches
	 * @throws GitAPIException
	 * @throws IOException
	 * @return
	 */
	public Map<String, Long> commitCountOnBranch() throws GitAPIException, IOException {

		Map<String, Long> listOfBranchesNames = new HashMap<>();
		Repository repository = createRepo();
		Git git = new Git(repository);
		//List<Ref> branches = git.branchList().call();
		List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();

		for (Ref branch : branches) {
			Iterable<RevCommit> commits = git.log().add(branch.getObjectId()).call();
			long count = 0;
			for (RevCommit commit : commits) {
				count++;
			}
			listOfBranchesNames.put(branch.getName().substring(11), count);
		}
		return listOfBranchesNames;
	}

	/**
	 * Fontion qui permet de lancer le plugin
	 */
	@Override
	public void run() {
		try {
			listOfBranchesNames = commitCountOnBranch();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retourne result (si il est null lance le plugin d'abord)
	 * @return
	 */
	@Override
	public Result getResult() {
		if (numberOfCommit == null)
			run();
		return new Result();
	}

	/**
	 * Classe interne
	 */
	public static class Result implements AnalyzerPlugin.Result {

		private final Map<String, Integer> commitsPerBranch = new HashMap<>();

		/**
		 * Fonction qui permet de representer le resultat sous forme d'un graphe html en utilisant CanvasJS(site qui permet de generer un script donnant un graph)
		 * @return
		 */
		@Override
		public String getResultAsString() {
			/*
			 * String resultat = null; for (Map.Entry mapEntry :
			 * listOfBranchesNames.entrySet()) { if (resultat != null) { resultat = resultat
			 * + "\nNombre des commits de la branche " + mapEntry.getKey() + " est : " +
			 * mapEntry.getValue() + " ."; }else { resultat =
			 * "Nombre des commits de la branche " + mapEntry.getKey() + " est : " +
			 * mapEntry.getValue(); } } return resultat;
			 */
			
			StringBuilder html_head = new StringBuilder("<script> window.onload = function () {" +
                    "var chart = new CanvasJS.Chart(\"chartContainer\", {\n" +
                    "                        animationEnabled: true,\n" +
                    "                        theme: \"light2\",\n" +
                    "                        title:{\n" +
                    "                    text: \"Nombre de Commits par branches\"\n" +
                    "                },\n" +
                    "                axisY: {\n" +
                    "                    title: \"Total Commits\"\n" +
                    "                },\n" +
                    "                data: [{\n" +
                    "                    type: \"column\",\n" +
                    "                            showInLegend: false,\n" +
                    "                            dataPoints: [");
			StringBuilder html_body = new StringBuilder("");
			for (var item : listOfBranchesNames.entrySet()) {
                String nomBranch = item.getKey();
                html_head.append("{ y : ").append(item.getValue()).append(", label : \"").append(nomBranch).append("\" },");
            }
			html_body.append("<div id=\"chartContainer\" style=\"height: 480px; width: 100%;\"></div>\n" +
                    "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
			html_head.append("]\n" + "}]\n" + "});\n" + " chart.render();\n" + "\n" + " }\n" +  "</script>");
			
			return html_head.toString() + html_body.toString();
		}

		/**
		 * Fonction qui permet de representer le resultat sous forme d'un graphe html en utilisant CanvasJS(site qui permet de generer un script donnant un graph)
		 * @return
		 */
		// script obtenu en libre service sur canvasjs.com
		@Override
		public String getResultAsHtmlDiv() {
			StringBuilder html_head = new StringBuilder("<script> window.onload = function () {" +
                    "var chart = new CanvasJS.Chart(\"chartContainer\", {\n" +
                    "                        animationEnabled: true,\n" +
                    "                        theme: \"light2\",\n" +
                    "                        title:{\n" +
                    "                    text: \"Nombre de Commits par branches\"\n" +
                    "                },\n" +
                    "                axisY: {\n" +
                    "                    title: \"Total Commits\"\n" +
                    "                },\n" +
                    "                data: [{\n" +
                    "                    type: \"column\",\n" +
                    "                            showInLegend: false,\n" +
                    "                            dataPoints: [");
			StringBuilder html_body = new StringBuilder("");
			for (var item : listOfBranchesNames.entrySet()) {
                String nomBranch = item.getKey();
                html_head.append("{ y : ").append(item.getValue()).append(", label : \"").append(nomBranch).append("\" },");
            }
			html_body.append("<div id=\"chartContainer\" style=\"height: 480px; width: 100%;\"></div>\n" +
                    "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
			html_head.append("]\n" + "}]\n" + "});\n" + " chart.render();\n" + "\n" + " }\n" +  "</script>");
			
			return html_head.toString() + html_body.toString();
		}

	}

}