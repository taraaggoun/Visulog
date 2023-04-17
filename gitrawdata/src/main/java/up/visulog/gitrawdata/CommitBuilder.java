package up.visulog.gitrawdata;
import java.time.LocalDateTime;

/**
 * Définit les attributs que prendra le builder.
 */
public class CommitBuilder {
    private final String id;
    private String author;
    private LocalDateTime date;
    private String weekDayofDate;
    private String description;
    private String mergedFrom;

    //Constructeur
    /**
     * Construit un objet de type CommitBuilder
     * @param id
     */
    public CommitBuilder(String id) {
        this.id = id;
    }

    //Setteur
    /**
     * Change la valeur de author
     * @param author
     * @return
     */
    public CommitBuilder setAuthor(String author) { 
        this.author = author;
        return this;
    }
    /**
     * Change une date de type String en une de type LocalDateTime
     * @param date
     * @return
     */
    public CommitBuilder setDate(String date) {
        String[] d = date.split("\\s");
        weekDayofDate = d[0];
        int month = stringTomonth(d[1]);
        int day = Integer.parseInt(d[2]);
        int year = Integer.parseInt(d[4]);

        String[] t = d[3].split(":");
        int hour = Integer.parseInt(t[0]);
        int min = Integer.parseInt(t[1]);
        int sec = Integer.parseInt(t[2]);

        this.date = LocalDateTime.of(year, month, day, hour, min, sec);
        return this;
    }

    /**
     * Change la description
     * @param description
     * @return
     */
    public CommitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Change mergedFrom
     * @param mergedFrom
     * @return
     */
    public CommitBuilder setMergedFrom(String mergedFrom) {
        this.mergedFrom = mergedFrom;
        return this;
    }

    //Methodes
    /**
     * Construit un nouvel objet de type Commit.
     * @return
     */
    public Commit createCommit() {
        return new Commit(id, author, date, weekDayofDate, description, mergedFrom);
    }

    /**
     * Prend un mois en parametre et retourne le mois qui lui correspond
     * @param month
     * @return 
     */
    public int stringTomonth(String month){
        switch(month){
            case "Jan":
                return 1;
            case "Fev":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
            default :
                return 0;
        }
    }
}