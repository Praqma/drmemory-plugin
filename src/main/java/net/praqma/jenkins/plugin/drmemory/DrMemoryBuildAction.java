package net.praqma.jenkins.plugin.drmemory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import net.praqma.drmemory.DrMemoryResult;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Result;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;

public class DrMemoryBuildAction implements Action {
	
	private static final Logger logger = Logger.getLogger( DrMemoryBuildAction.class.getName() );
	
	public enum GraphType {
		TOTAL_LEAK
	}
	
	private DrMemoryResult result;
	private final AbstractBuild<?, ?> build;
	private DrMemoryPublisher publisher;
	
	public DrMemoryBuildAction( AbstractBuild<?, ?> build, DrMemoryPublisher publisher, DrMemoryResult result ) {
		this.result = result;
		this.build = build;
		this.publisher = publisher;
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
	
	public DrMemoryResult getResult() {
		return result;
	}
	
	static DrMemoryBuildAction getPreviousResult( AbstractBuild<?, ?> start ) {
		AbstractBuild<?, ?> b = start;
		while( true ) {
			b = b.getPreviousNotFailedBuild();
			if( b == null ) {
				return null;
			}

			assert b.getResult() != Result.FAILURE : "We asked for the previous not failed build";
			DrMemoryBuildAction r = b.getAction( DrMemoryBuildAction.class );
			if( r != null && b.getResult() != Result.SUCCESS ) {
				r = null;
			}

			if( r != null ) {
				return r;
			}
		}
	}
	
	public DrMemoryBuildAction getPreviousResult() {
		return getPreviousResult( build );
	}
	
	
	public void doGraph( StaplerRequest req, StaplerResponse rsp ) throws IOException {
		String type = req.getParameter( "type" );
		
		logger.fine( "Graphing " + type );

		int width = 500, height = 200;
		String w = req.getParameter( "width" );
		String h = req.getParameter( "height" );
		if( w != null && w.length() > 0 ) {
			width = Integer.parseInt( w );
		}

		if( h != null && h.length() > 0 ) {
			height = Integer.parseInt( h );
		}

		if( type == null ) {
			throw new IOException( "No type given" );
		}

		if( ChartUtil.awtProblemCause != null ) {
			// not available. send out error message
			rsp.sendRedirect2( req.getContextPath() + "/images/headless.png" );
			return;
		}

		Calendar t = build.getTimestamp();

		if( req.checkIfModified( t, rsp ) ) {
			return; // up to date
		}
		
		DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
		
		boolean latest = true;
		String yaxis = "???";
		int min = 999999999;
		int max = 0;
		
		/* For each build, moving backwards */
		for( DrMemoryBuildAction a = this; a != null; a = a.getPreviousResult() ) {
			logger.finest( "Build " + a.getDisplayName() );

			/* Make the x-axis label */
			ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel( a.build );
			
			if( type.equalsIgnoreCase( "total-leaks" ) ) {
				int number = a.getResult().getBytesOfLeaks().total;
				dsb.add( number, "Total leaks", label );
				
				if( number > max ) {
					max = number;
				}
				
				if( number < min ) {
					min = number;
				}
				
				if( latest ) {
					yaxis = "Number of leaks";
				}
			}
			
			latest = false;
		}
		
		ChartUtil.generateGraph( req, rsp, createChart( dsb.build(), type, yaxis, max, min ), width, height );
	}
	
	
	private JFreeChart createChart( CategoryDataset dataset, String title, String yaxis, int max, int min ) {

		final JFreeChart chart = ChartFactory.createLineChart( title, // chart
																		// title
				null, // unused
				yaxis, // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

		final LegendTitle legend = chart.getLegend();


		chart.setBackgroundPaint( Color.white );

		final CategoryPlot plot = chart.getCategoryPlot();

		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setBackgroundPaint( Color.WHITE );
		plot.setOutlinePaint( null );
		plot.setRangeGridlinesVisible( true );
		plot.setRangeGridlinePaint( Color.black );

		CategoryAxis domainAxis = new ShiftedCategoryAxis( null );
		plot.setDomainAxis( domainAxis );
		domainAxis.setCategoryLabelPositions( CategoryLabelPositions.UP_90 );
		domainAxis.setLowerMargin( 0.0 );
		domainAxis.setUpperMargin( 0.0 );
		domainAxis.setCategoryMargin( 0.0 );

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
		rangeAxis.setUpperBound( max );
		rangeAxis.setLowerBound( min );

		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseStroke( new BasicStroke( 2.0f ) );
		ColorPalette.apply( renderer );

		// crop extra space around the graph
		plot.setInsets( new RectangleInsets( 5.0, 0, 0, 5.0 ) );

		return chart;
	}

}
