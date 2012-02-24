package net.praqma.jenkins.plugin.drmemory.graphs;

import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import net.praqma.jenkins.plugin.drmemory.DrMemoryBuildAction;

public class UnaddressableAccessesGraph extends AbstractGraph {

	@Override
	public float[] getNumber( DrMemoryBuildAction action ) {
		return new float[] { action.getResult().getUnaddressableAccesses().total, action.getResult().getUnaddressableAccesses().unique };
	}

	@Override
	public void addX( DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb, float[] values, NumberOnlyBuildLabel label ) {
		dsb.add( values[0], "Total Unaddressable accesses", label );
		dsb.add( values[1], "Unique Unaddressable accesses", label );
	}

	@Override
	public String getTitle() {
		return "Unaddressable accesses";
	}

	@Override
	public String getYAxis() {
		return "Accesses";
	}

}
