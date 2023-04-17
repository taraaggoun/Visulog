package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.time.LocalDateTime;

/**
 * Ce fichier implémente la classe Commit
 */
public class Commit {
    //Attributs
    public final String id;
    public final LocalDateTime date;
    public final String weekDayofDate;
    public final String author;
    public final String description;
    public final String mergedFrom;
    public int linesAdded = 0;
    public int linesDeleted = 0;

    //Constructeur
    /**
     * Construit un objet de type Commit 
     * @param id
     * @param author
     * @param date
     * @param weekDayofDate
     * @param description
     * @param mergedFrom
     */
    public Commit(String id, String author, LocalDateTime date, String weekDayofDate, String description, String mergedFrom) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.weekDayofDate = weekDayofDate;
        this.description = description;
        this.mergedFrom = mergedFrom;
    }

    //Methodes
    /** 
     * 
     * @param reader
     * @return
    */
    public static List<Commit> parseLog(BufferedReader reader) {
        var result = new ArrayList<Commit>();
        Optional<Commit> commit = parseCommit(reader);
        while (commit.isPresent()) {
            result.add(commit.get());
            commit = parseCommit(reader);
        }
        return result;
    }

    /**
     * Recupere le nombre de lignes ajoutées et le nombre de lignes supprimées de chaque commit de la liste de commit
     * @param path
     * @param commits
     * @return
     */
    public static List<Commit> getNumberLines(Path path, List<Commit> commits){
        for(Commit commit : commits){
            if(commit.mergedFrom == null){
                int[] lines = parseLine(command(path,"git","show","--format=oneline",commit.id,"--numstat"));
                commit.linesAdded = lines[0];
                commit.linesDeleted = lines[1];
            }
        }
        return commits;
    }

    /**
     * Découpe les résultats de « git log » en une liste de commits.
     * @param gitPath
     * @return  
     */
    public static List<Commit> parseLogFromCommand(Path gitPath) {
        BufferedReader reader = command(gitPath, "git", "log");
         return getNumberLines(gitPath,parseLog(reader));
     }

     /**
     * Découpe les résultats de « git log --all » en une liste de commits.
     * @param gitPath
     * @return 
     */
     public static List<Commit> parseLogFromCommandall(Path gitPath) {
        BufferedReader reader = command(gitPath, "git", "log","--all");
      return getNumberLines(gitPath,parseLog(reader));
  }

    /**
     *
     * @param path
     * @param args
     * @return
     */
    public static BufferedReader command(Path path, String...  args) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(path.toFile());
        builder.command(args);

        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            String s = "";
            for(int i=0; i<args.length; i++)
                s += args[i] + " ";
            throw new RuntimeException("Error running \"git log\".", e);
        }
        InputStream is = process.getInputStream();
        return new BufferedReader(new InputStreamReader(is));
    }
    
    /**
     * Compte le nombre de lignes ajoutées et supprimées et le revoie dans un tableau d'entier 
     * @param reader
     * @return
     */
    public static int [] parseLine(BufferedReader reader){
        try{
            String line;
            reader.readLine();
            int added = 0;
            int deleted = 0;
            while((line = reader.readLine()) != null){
                if(!line.equals("")){
                    Scanner sc = new Scanner(line);
                    String s = sc.next();
                    if(s.equals("-"))
                        added += 0;
                    else
                        added += Integer.parseInt(s);
                    s=sc.next();
                    if(s.equals("-"))
                        deleted += 0;
                    else 
                        deleted += Integer.parseInt(s);
                }
            }
            int [] result = {added,deleted};
            return result;
        } catch (IOException e){
            throw new RuntimeException("Error", e);
        }
    }

    /**
     * Analyse les log et les sortie du commit
     * @return
     */
    public static Optional<Commit> parseCommit(BufferedReader input) {
        try {

            var line = input.readLine();
            if (line == null) return Optional.empty(); // si il y a pas de ligne a lire, on a fini de lire
            var idChunks = line.split(" ");
            if (!idChunks[0].equals("commit")) parseError();
            var builder = new CommitBuilder(idChunks[1]);

            line = input.readLine();
            while (!line.isEmpty()) {
                var colonPos = line.indexOf(":");
                var fieldName = line.substring(0, colonPos);
                var fieldContent = line.substring(colonPos + 1).trim();
                switch (fieldName) {
                    case "Author":
                        builder.setAuthor(fieldContent);
                        break;
                    case "Merge":
                        builder.setMergedFrom(fieldContent);
                        break;
                    case "Date":
                    builder.setDate(fieldContent);    
                        break;
                    default:
                        System.out.println("Champs manquant !");
                        break;
                }
                line = input.readLine(); //prepare la prochaine iteration
                if (line == null) parseError(); // end of stream is not supposed to happen now (commit data incomplete)
            }
            // lis le message du commit
            var description = input
                    .lines() // get a stream of lines to work with
                    .takeWhile(currentLine -> !currentLine.isEmpty()) // take all lines until the first empty one (commits are separated by empty lines). Remark: commit messages are indented with spaces, so any blank line in the message contains at least a couple of spaces.
                    .map(String::trim) // remove indentation
                    .reduce("", (accumulator, currentLine) -> accumulator + currentLine); // concatenate everything
            builder.setDescription(description);
            return Optional.of(builder.createCommit());
        } catch (IOException e) {
            parseError();
        }
        return Optional.empty(); // this is supposed to be unreachable, as parseError should never return
    }

    /**
     * Aide les fonction pour genere des exception
     * @exception RuntimeException
     */
    private static void parseError() {
        throw new RuntimeException("Wrong commit format.");
    }

    /**
     * Renvoie la liste des informations du Commit sous forme de String
     * @return 
     */
    @Override
    public String toString() {
        return "Commit{" +
                "id='" + id + '\'' +
                (mergedFrom != null ? ("mergedFrom...='" + mergedFrom + '\'') : "") +
                ", date='" + date + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}