package net.praqma.jenkins.plugin.drmemory.graphs;

import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import net.praqma.jenkins.plugin.drmemory.DrMemoryBuildAction;

public class BytesOfLeakGraph extends AbstractGraph {

	@Override
	public float[] getNumber( DrMemoryBuildAction action ) {
		return new float[] { action.getResult().getBytesOfLeaks().total, action.getResult().getBytesOfPossibleLeaks().total };
	}

	@Override
	public void addX( DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb, float[] values, NumberOnlyBuildLabel label ) {
		dsb.add( values[0], "Total Leaks", label );
		dsb.add( values[1], "Possible Leaks", label );
	}

	@Override
	public String getTitle() {
		return "Bytes of Leaks";
	}

	@Override
	public String getYAxis() {
		return "Bytes";
	}

}
