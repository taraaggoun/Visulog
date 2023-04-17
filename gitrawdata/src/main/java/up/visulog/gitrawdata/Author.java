package up.visulog.gitrawdata;

/**
 * Cette classe represente un auteur
 */
public class Author {
    //Attributs
    private String name; // type String, nom de l'auteur
    private String username; // type String, identifiant de l'auteur

    //constructeur 
    /**
     * Construit un object de type Author
     * @param  name
     * @param username
     */
    public Author(String name,String username){
        this.name = name;
        this.username = username;
    }

    //Getteurs
    /**
     * Renvoie la valeur de username 
     * @return 
     */ 
    public String getUsername() {
        return username;
    }

    /**
     * Retourne la valeur de name
     * @return
     */
    public String getName() {
        return name;
    }
 
    /**
     * Renvoie un String qui donne le nom et l'identifiant d'un auteur
     */
    @Override
    public String toString() { 
        return "Author{" +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}