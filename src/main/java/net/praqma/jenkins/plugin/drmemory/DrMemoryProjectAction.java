package net.praqma.jenkins.plugin.drmemory;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;

public class DrMemoryProjectAction {
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
