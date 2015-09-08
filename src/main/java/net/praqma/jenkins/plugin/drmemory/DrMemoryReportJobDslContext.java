package net.praqma.jenkins.plugin.drmemory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javaposse.jobdsl.dsl.Context;
import static javaposse.jobdsl.dsl.Preconditions.checkArgument;

class DrMemoryReportJobDslContext implements Context {

    List<Graph> graphs = new ArrayList<Graph>();

    Set<String> graphTypes = new HashSet<String>() {
        {
            add("total-leaks");
            add("all-leaks");
            add("actual-leaks");
            add("bytes-of-leak");
            add("allocations");
            add("uninitialized-accesses");
            add("unaddressable-accesses");
            add("warnings");
            add("invalid-heap-arguments");
        }
    };

    public void graph(String type) {
        checkArgument(graphTypes.contains(type), "graph type must be one of " + graphTypes.toString());
        graphs.add(new Graph(type));
    }

    String logPath = "drmemory";

    public void logPath(String value) {
        logPath = value;
    }
}
