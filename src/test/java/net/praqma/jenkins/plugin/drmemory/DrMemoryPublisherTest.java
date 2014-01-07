package net.praqma.jenkins.plugin.drmemory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.tasks.Builder;

import net.praqma.drmemory.DrMemory;

import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.TestBuilder;

public class DrMemoryPublisherTest extends HudsonTestCase {
	
	@Test
	public void test() throws IOException, InterruptedException, ExecutionException {
		
		FreeStyleProject project = createFreeStyleProject( "drmemory" );
		
		System.out.println( "PATH: " + super.jenkins.getRootDir() );
		
		DrMemory.skipRun();
		final InputStream in = getClass().getClassLoader().getResourceAsStream( "results.txt" );
		System.out.println( "[TEST] " + in );
		
		/* To get the real workspace and copy the results.txt */
		project.getBuildersList().add(new TestBuilder() {
		    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
		        BuildListener listener) throws InterruptedException, IOException {
		        build.getWorkspace().child("drmemory").child( "1" ).child( "results.txt" ).copyFrom( in );
		        return true;
		    }
		});
		
		/* DrMemoryBuilder*/
		Builder builder = new DrMemoryBuilder( "", "", "drmemory", false );
		project.getBuildersList().add( builder );
		
		/* Recorder */
		DrMemoryPublisher publisher = new DrMemoryPublisher("drmemory");
		project.getPublishersList().add( publisher );
		
		FreeStyleBuild b = project.scheduleBuild2( 0, new Cause.UserIdCause() ).get();
		
		in.close();
		
		System.out.println( "Workspace: " + b.getWorkspace() );
		File drmFile = new File( b.getWorkspace() + "/drmemory/1/results.txt" );
		System.out.println( "DRMFILE: " + drmFile );
		drmFile.delete();
		drmFile.getParentFile().delete();
		drmFile.getParentFile().getParentFile().delete();
		drmFile.getParentFile().getParentFile().getParentFile().delete();
		drmFile.getParentFile().getParentFile().getParentFile().getParentFile().delete();
		
		DrMemoryBuildAction action = b.getAction( DrMemoryBuildAction.class );
		
		System.out.println( "Action: " + action );
		System.out.println( "Logfile: " + b.getLogFile() );
		
		BufferedReader br = new BufferedReader( new FileReader( b.getLogFile() ) );
		String line = "";	
	while( ( line = br.readLine() ) != null ) {
			System.out.println( "[JENKINS] " + line );
		}
		
		br.close();
		
		if( action != null ) {
			System.out.println( "Action: " + action.getResults().get(0) );
		} else {
			System.out.println( "ACTION IS NULL" );
		}
		
		assertNotNull( action );
	}
}
