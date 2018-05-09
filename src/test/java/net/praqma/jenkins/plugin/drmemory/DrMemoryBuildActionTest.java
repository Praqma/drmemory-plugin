package net.praqma.jenkins.plugin.drmemory;

import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.Builder;
import net.praqma.drmemory.DrMemory;
import net.praqma.drmemory.DrMemoryResult;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DrMemoryBuildActionTest {

    @Rule
    public JenkinsRule jr = new JenkinsRule();



    @Test
    public void testJEP200() throws Exception {
        Node s = jr.createOnlineSlave();
        final String in = getClass().getClassLoader().getResource( "results.txt" ).getFile();
        FreeStyleProject fp = jr.createFreeStyleProject("JEP-200");
        fp.setAssignedNode(s);
        AbstractBuild<?,?> r = jr.buildAndAssertSuccess(fp);
        DrMemoryBuildAction drMemory = new DrMemoryBuildAction(r);
        DrMemoryResult res = DrMemoryResult.parse(new File(in));

        drMemory.addResult(res);

        r.addAction(drMemory);
        r.save();
    }
    /*


    */
}
