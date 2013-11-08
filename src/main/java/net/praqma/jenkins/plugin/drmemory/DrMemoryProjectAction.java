package net.praqma.jenkins.plugin.drmemory;

import java.util.logging.*;
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
    private static final Logger log = Logger.getLogger(DrMemoryProjectAction.class.toString());

    public DrMemoryProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    @Override
    public String getDisplayName() {
        return "DrMemory";
    }

    @Override
    public String getSearchUrl() {
        return getUrlName();
    }

    @Override
    public String getIconFileName() {
        return "graph.gif";
    }

    @Override
    public String getUrlName() {
        return "drmemory";
    }
        

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (getLastResult() != null) {
            getLastResult().doGraph(req, rsp);
        }
    }

    public DrMemoryBuildAction getLastResult() {
        for (AbstractBuild<?, ?> b = getLastBuildToBeConsidered(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
                log.fine( String.format( "Using build %s to draw graph", b.number) ) ;
                DrMemoryBuildAction r = b.getAction(DrMemoryBuildAction.class);
                if (r != null) {
                    return r;
                }
            } else {
                continue;
            }
        }
        log.fine( String.format( "Found no results to draw graphs with") ) ;
        return null;
    }

    private AbstractBuild<?, ?> getLastBuildToBeConsidered() {
        return project.getLastSuccessfulBuild();
    }
}
