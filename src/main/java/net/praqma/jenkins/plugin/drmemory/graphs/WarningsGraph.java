package net.praqma.jenkins.plugin.drmemory.graphs;

import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import net.praqma.jenkins.plugin.drmemory.DrMemoryBuildAction;

public class WarningsGraph extends AbstractGraph {

	@Override
	public float[] getNumber( DrMemoryBuildAction action ) {
		return new float[] { action.getResult().getWarnings().total, action.getResult().getWarnings().unique };
	}

	@Override
	public void addX( DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb, float[] values, NumberOnlyBuildLabel label ) {
		dsb.add( values[0], "Total Warnings", label );
		dsb.add( values[1], "Unique Warnings", label );
	}

	@Override
	public String getTitle() {
		return "Warnings";
	}

	@Override
	public String getYAxis() {
		return "Warnings";
	}

}
