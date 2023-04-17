package up.visulog.cli;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Ce test tente de créer un CLILauncher, pour voir si la configuration est bien lue, et en demandant de compter les commits. Tente ensuite avec une option volontairement invalide. 
 */

public class TestCLILauncher {
        
    @Test
    public void testArgumentParser() {
	
        var config1 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=countTotal"});
        assertTrue(config1.isPresent());

	var config2 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=dailyGraph"});
        assertTrue(config2.isPresent());

	var config3 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=hourGraph"});
        assertTrue(config3.isPresent());

	var config4 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=weekGraph"});
        assertTrue(config4.isPresent());

	var config5 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=commitsPerAuthor"});
        assertTrue(config5.isPresent());

	var config6 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=countLines"});
        assertTrue(config6.isPresent());

	var config7 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=countMergeRequest"});
        assertTrue(config7.isPresent());

	var config8 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=countCommitsPerBranch"});
        assertTrue(config8.isPresent());

	var config9 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=averageCommitHour"});
        assertTrue(config9.isPresent());

	var config10 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=mergeRequest"});
        assertTrue(config10.isPresent());

	var config11 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=contributionLines"});
        assertTrue(config11.isPresent());

	var config12 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=contribution"});
        assertTrue(config12.isPresent());

	var config13 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=commitsPerDate"});
        assertTrue(config13.isPresent());

	var config14 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", "--addPlugin=mergeRequestPerDate"});
        assertTrue(config14.isPresent());
	
	var config15 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", " --loadConfigFile"});
        assertTrue(config15.isPresent());
	
	var config16 = CLILauncher.makeConfigFromCommandLineArgs(new String[]{".", " --justSaveConfigFile"});
        assertTrue(config16.isPresent());
	
        var config17 = CLILauncher.makeConfigFromCommandLineArgs(new String[] {
            "--nonExistingOption"
        });
        assertFalse(config17.isPresent());
    }
}
