package up.visulog.gitrawdata;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
/**
 * Cette classe represente une collection d'auteurs
 */
public class Authors {
   
    //attribut, une collection d'auteurs
    private Collection<Author> authors;

    //constructeur
    /**
     * Construit un objet de type Authors
     */
    public Authors(){
        this.authors = null;
    }

    //Getteur
    /**
     * Renvoie la valeur de authors
     * @return
     */
    public Collection<Author> getAuthors() {
        return authors;
    }

 
    /**
     * Renvoyer une collection d'auteur grace a l'api Json
     * @param privateToken
     * @param idProject
     * @return 
     */
    public Collection<Author> getAuthorsFromJson(String privateToken,int idProject){
        Collection<Author> result = new ArrayList<Author>(); 
        RetourAPI authorsAPI = new RetourAPI();
        authorsAPI.createLogFileForAuthors(privateToken,idProject);
        Gson logs = new Gson();
        try (Reader reader = new FileReader("../GitLog/resultsAuthors.json")) {
            Collection<Author> authorsList = Arrays.asList(logs.fromJson(reader, Author[].class));
            result = authorsList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}