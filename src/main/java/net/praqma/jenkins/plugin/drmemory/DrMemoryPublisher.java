package net.praqma.jenkins.plugin.drmemory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.praqma.drmemory.DrMemoryResult;
import net.praqma.drmemory.exceptions.InvalidInputException;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Descriptor.FormException;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

public class DrMemoryPublisher extends Recorder {

	private static final Logger logger = Logger.getLogger( DrMemoryPublisher.class.getName() );

	private String logPath;

	public static final String __OUTPUT = "drmemory.txt";

	@DataBoundConstructor
	public DrMemoryPublisher( String logPath ) {
		this.logPath = logPath;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	@Override
	public boolean perform( AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener ) throws InterruptedException, IOException {

		FilePath workspaceResult = null;
		File path = build.getRootDir();
		File result = new File( path, __OUTPUT );
		FilePath buildTarget = new FilePath( build.getRootDir() );
		PrintStream out = listener.getLogger();

		out.println( "My workspace is " + build.getWorkspace() );
		out.println( "Looking(" + logPath + ") in " + new FilePath( build.getWorkspace(), logPath ) );

		try {
			workspaceResult = new FilePath( build.getWorkspace(), logPath + "/result.txt" );

			if( build.getResult().isWorseOrEqualTo( Result.FAILURE ) && !workspaceResult.exists() ) {
				return true;
			}

		} catch( IOException e ) {
			Util.displayIOException( e, listener );
			e.printStackTrace( listener.fatalError( "Unable to find Dr Memory file" ) );
			build.setResult( Result.FAILURE );
		}

		out.println( "I got " + workspaceResult );

		if( workspaceResult != null ) {

			/* Save output to build path */
			final FilePath targetPath = new FilePath( result );
			try {
				workspaceResult.copyTo( targetPath );
			} catch( IOException e ) {
				Util.displayIOException( e, listener );
				e.printStackTrace( listener.fatalError( "Unable to copy result file from " + workspaceResult + " to " + buildTarget ) );
				build.setResult( Result.FAILURE );
			}
		}

		DrMemoryResult dresult = null;
		try {
			dresult = DrMemoryResult.parse( result );

			DrMemoryBuildAction dba = new DrMemoryBuildAction( build, this, dresult );
			build.getActions().add( dba );

		} catch( InvalidInputException e ) {
			out.println( "Invalid input: " + e.getMessage() );
			return false;
		}

		return true;
	}
	
	public String getLogPath() {
		return logPath;
	}
	
	private Map<String, Boolean> selectedGraphs = new HashMap<String, Boolean>();
	
	public Map<String, Boolean> getSelectedGraphs() {
		return selectedGraphs;
	}
	
	public boolean isTotalLeak() {
		return selectedGraphs.containsKey( "total-leaks" );
	}

	@Extension
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		public String getDisplayName() {
			return "Dr. Memory Report";
		}

		@Override
		public boolean isApplicable( Class<? extends AbstractProject> arg0 ) {
			return true;
		}

		@Override
		public DrMemoryPublisher newInstance( StaplerRequest req, JSONObject formData ) throws FormException {
			DrMemoryPublisher instance = req.bindJSON( DrMemoryPublisher.class, formData );
			
			/* total leaks */
			boolean total_leaks = req.getParameter( "graph.total-leaks" ) != null;
			instance.selectedGraphs.put( "total-leaks", total_leaks );
			
			save();
			return instance;
		}

		/*GLOBAL*/
		@Override
		public boolean configure( StaplerRequest req, JSONObject formData ) throws FormException {
			logger.warning( "CONFIGURE" );
			req.bindParameters( this, "drmemory." );
			boolean total_leak = req.getParameter( "graph.total-leak" ) != null;
			logger.warning( "tll: " + total_leak );
			//DrMemoryPublisher.this.selectedGraphs.put( "total-leak", total_leak );
			save();
			return super.configure( req, formData );
		}

	}
}
