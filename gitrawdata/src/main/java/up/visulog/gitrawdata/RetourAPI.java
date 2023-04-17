package up.visulog.gitrawdata;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.File;

/**
 * Cette classe contient le retour de l'API Json
 */
public class RetourAPI {

    //constructeur
    /**
     * Construit un object de type RetourAPI
     */
    public RetourAPI(){}

    /**
     * Cree un fichier resultsAuthors.json et ecris dessus les info des auteurs 
     * @param privateToken
     * @param idProject
     */
    public void createLogFileForAuthors(String privateToken,int idProject){
        Process process;
        try {
            File jsonFile = new File("../GitLog/resultsAuthors.json"); //cree un fichier resultAuthors.json dans le fichier GitLog
            FileWriter writeJsonFile = new FileWriter("../GitLog/resultsAuthors.json");//ecris dans le fichier la liste des membres du projet
            String[] command = {"curl",
                    "--header",
                    "PRIVATE-TOKEN:"+privateToken,
                    "https://gaufre.informatique.univ-paris-diderot.fr/api/v4/projects/"+idProject+"/users?per_page=1000"};
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                writeJsonFile.write(line);
            }
           writeJsonFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
}