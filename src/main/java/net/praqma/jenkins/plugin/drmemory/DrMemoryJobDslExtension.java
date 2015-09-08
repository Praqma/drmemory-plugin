package net.praqma.jenkins.plugin.drmemory;

import hudson.Extension;
import javaposse.jobdsl.dsl.RequiresPlugin;
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional = true)
public class DrMemoryJobDslExtension extends ContextExtensionPoint {
    
    @RequiresPlugin(id = "drmemory-plugin", minimumVersion = "1.4.0")
    @DslExtensionMethod(context = StepContext.class)
    public Object drMemory(Runnable closure){
        DrMemoryJobDslContext context = new DrMemoryJobDslContext();
        executeInContext(closure, context);

        return new DrMemoryBuilder(context.executable, context.args, context.logPath, context.failedAsUnstable);
    }
    
    @RequiresPlugin(id = "drmemory-plugin", minimumVersion = "1.4.0")
    @DslExtensionMethod(context = PublisherContext.class)
    public Object drMemoryReport(Runnable closure){
        DrMemoryReportJobDslContext context = new DrMemoryReportJobDslContext();
        executeInContext(closure, context);

        DrMemoryPublisher publisher = new DrMemoryPublisher(context.logPath);
        publisher.setGraphs(context.graphs);
        
        return publisher;
    }
}
