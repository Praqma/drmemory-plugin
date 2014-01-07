package net.praqma.jenkins.plugin.drmemory.graphs;

import net.praqma.drmemory.DrMemoryResult;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;

public abstract class AbstractGraph {
	public abstract float[] getNumber( DrMemoryResult result );
	public abstract void addX( DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb, float[] values, ChartUtil.NumberOnlyBuildLabel label );
	public abstract String getTitle();
	public abstract String getYAxis();
}
