package up.visulog.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import up.visulog.analyzer.CountCommitPerBranchPlugin;
/**
 * Cette classe sert a tester le plugin CountCommitPerBranchPlugin
 */
public class TestCountCommitPerBranchPlugin {

	/**
	 * Test Le plugin COuntCommitPerBranchPlugin
	 * @throws GitAPIException
	 * @throws IOException
	 */
	@Test
	public void checkCommitSumPerBranch() throws GitAPIException, IOException{
		
		CountCommitPerBranchPlugin c = new CountCommitPerBranchPlugin(null);
		c.run();
		Map<String, Long> listBranchs = new HashMap<>();
		listBranchs = c.commitCountOnBranch();
		assertNotNull(listBranchs);
		
	}
}
