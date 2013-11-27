package net.praqma.jenkins.plugin.drmemory;

import hudson.AbortException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import net.praqma.drmemory.DrMemory;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import java.io.File;

public class DrMemoryBuilder extends Builder {

    private static final Logger logger = Logger.getLogger(DrMemoryBuilder.class.getName());
    private String executable;
    private String arguments;
    private String logPath;
    private boolean treatFailed;
    private String finalLogPath;

    @DataBoundConstructor
    public DrMemoryBuilder(String executable, String arguments, String logPath, boolean treatFailed) {
        this.executable = executable;
        this.arguments = arguments;
        this.logPath = logPath;
        this.treatFailed = treatFailed;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException {
        PrintStream out = listener.getLogger();

        /* Add the action */
        DrMemoryBuildAction dba = new DrMemoryBuildAction(build, this);
        build.addAction(dba);

        String version = Jenkins.getInstance().getPlugin("drmemory-plugin").getWrapper().getVersion();
        out.println("Dr Memory Plugin version " + version);

        try {
            finalLogPath = logPath += (logPath.endsWith(File.separator) ? "" : File.separator);
            finalLogPath += build.getNumber();
            build.getWorkspace().act(new DrMemoryRemoteBuilder(executable, arguments, finalLogPath, listener));
            return true;
        } catch (IOException e) {
            if (isTreatFailed()) {
                out.println("Dr. Memory command line program returned with error code. Continuing anyway.");
                out.println("The message was:");
                out.println(e.getMessage());
                build.setResult(Result.UNSTABLE);
                return true;
            } else {
                out.println("Unable to execute Dr. Memory: " + e.getMessage());
                return false;
            }
        }
    }

    public String getExecutable() {
        return executable;
    }

    public String getArguments() {
        return arguments;
    }

    public String getLogPath() {
        return logPath;
    }

    public String getFinalLogPath() {
        return finalLogPath;
    }

    /**
     * @return the treatFailed
     */
    public boolean isTreatFailed() {
        return treatFailed;
    }

    /**
     * @param treatFailed the treatFailed to set
     */
    public void setTreatFailed(boolean treatFailed) {
        this.treatFailed = treatFailed;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public DrMemoryBuilder newInstance(StaplerRequest req, JSONObject data) {
            DrMemoryBuilder instance = req.bindJSON(DrMemoryBuilder.class, data);
            save();
            return instance;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Execute with Dr. Memory";
        }
    }
}
