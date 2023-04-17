package up.visulog.analyzer;

import org.junit.Test;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.CommitBuilder;
import up.visulog.analyzer.CountLinesperAuthorPlugin.Result;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Cette classe test le plugin CountLinesperAuthorsPlugin
 */
public class TestCountLinesPerAuthorPlugin {
    /**
     * Test du plugin CountLinesperAuthor
     */
    @Test 
    public void checkLines() {
        //creation des commits 
        Commit c1 = new Commit ("1", "a 1", null, null, null,null);
        c1.linesAdded = 2;
        c1.linesDeleted = 2;
        Commit c2 = new Commit ("2", "b 2", null, null, null,null);
        c2.linesAdded = 3;
        c2.linesDeleted = 1;
        Commit c3 = new Commit ("3", "c 3", null, null, null, null);
        c3.linesAdded = 8;
        c3.linesDeleted = 4;

    //ajout des commits dans une liste de commit
    ArrayList<Commit> test = new ArrayList<Commit>();
    test.add(c1);
    test.add(c2);
    test.add(c3);

    // appelle du plugin pout voir combien de ligne ont était ajoutées;
    var plugin = new CountLinesperAuthorPlugin(null);
    Result result = plugin.processLog(test);
 
    String t = "{a=2, b=3, c=8}{a=2, b=1, c=4}";//ce qui est attendue
    assertEquals(t, result.getResultAsString());//test si ce qui est attendu et la meme chose que ce que le plugin revoie
    }
}
