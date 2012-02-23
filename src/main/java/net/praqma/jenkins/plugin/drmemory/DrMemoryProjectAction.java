package net.praqma.jenkins.plugin.drmemory;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;

public class DrMemoryProjectAction extends Actionable implements ProminentProjectAction {
	private final AbstractProject<?, ?> project;

	public DrMemoryProjectAction( AbstractProject<?, ?> project ) {
		this.project = project;
	}

	public String getDisplayName() {
		return "DrMemory";
	}

	public String getSearchUrl() {
		return getUrlName();
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getUrlName() {
		return "drmemory";
	}
	
    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (getLastResult() != null) {
            getLastResult().doGraph(req, rsp);
        }
    }

	public DrMemoryBuildAction getLastResult() {
		for( AbstractBuild<?, ?> b = getLastBuildToBeConsidered(); b != null; b = b.getPreviousNotFailedBuild() ) {
			if( b.getResult() == Result.FAILURE || ( b.getResult() != Result.SUCCESS ) ) {
				continue;
			}

			DrMemoryBuildAction r = b.getAction( DrMemoryBuildAction.class );
			if( r != null ) {
				return r;
			}
		}
		return null;
	}

	private AbstractBuild<?, ?> getLastBuildToBeConsidered() {
		return project.getLastSuccessfulBuild();
	}

}
