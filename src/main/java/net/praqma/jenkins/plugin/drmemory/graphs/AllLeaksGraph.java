package net.praqma.jenkins.plugin.drmemory.graphs;

import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import net.praqma.jenkins.plugin.drmemory.DrMemoryBuildAction;

public class AllLeaksGraph extends AbstractGraph {

	@Override
	public float[] getNumber( DrMemoryBuildAction action ) {
		return new float[] { 
				action.getResult().getLeakCount().total, 
				action.getResult().getLeakCount().unique,
				action.getResult().getPossibleLeakCount().total,
				action.getResult().getPossibleLeakCount().unique
				};
	}

	@Override
	public void addX( DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb, float[] values, NumberOnlyBuildLabel label ) {
		dsb.add( values[0], "Total Leaks", label );
		dsb.add( values[1], "Unique Leaks", label );
		dsb.add( values[2], "Total possible Leaks", label );
		dsb.add( values[3], "Unique possible Leaks", label );
	}

	@Override
	public String getTitle() {
		return "All Leaks";
	}

	@Override
	public String getYAxis() {
		return "Number of leaks";
	}

}
