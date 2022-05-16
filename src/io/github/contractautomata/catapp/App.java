package io.github.contractautomata.catapp;


import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.UIManager;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import io.github.contractautomata.catapp.castate.MxState;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;


/**
 * An adaptation of BasicGraphEditor of mxGraph to provide a GUI application for FMCA tool.
 *
 * @author Davide Basile
 *
 */
public class App extends BasicGraphEditor
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4601740824088314699L;

	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

    public Automaton<String, Action,State<String>, ModalTransition<String,Action,State<String>,CALabel>> lastaut = null;

	private ProductFrame pf=null;

	private JFrame menuFrame=null;

	public App()
	{
		this("Contract Automata Tool (May 2022)", new CustomGraphComponent(new CustomGraph()));
		MxState.setShapes();
	}

	/**
	 * the application used a frame for visualising the products of the feature model
	 */
	public void setProductFrame(ProductFrame pf)
	{
		this.pf=pf;
	}

	public ProductFrame getProductFrame()
	{
		return this.pf;
	}

	public JFrame getMenuFrame() {
		return menuFrame;
	}

	/**
	 * 
	 */
	public App(String appTitle, mxGraphComponent component)
	{
		super(appTitle, component);
		pf=null;
	}

	/**
	 * 
	 */
	public static class CustomGraphComponent extends mxGraphComponent
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -6833603133512882012L;

		/**
		 *
		 */
		public CustomGraphComponent(mxGraph graph)
		{
			super(graph);

			// Sets switches typically used in an editor
			setPageVisible(false);
			setGridVisible(false);
			setToolTips(true);
			setConnectable(true);
			graph.setResetEdgesOnConnect(false);



			getConnectionHandler().setCreateTarget(true);

			// Loads the default stylesheet from an external file
			mxCodec codec = new mxCodec();
			Document doc = mxUtils.loadDocument(Objects.requireNonNull(App.class.getResource(
							"/io/github/contractautomata/catapp/resources/default-style.xml"))
					.toString());
			codec.decode(doc.getDocumentElement(), graph.getStylesheet());

			// Sets the background to white
			getViewport().setOpaque(true);
			getViewport().setBackground(Color.WHITE);
		}

		/**
		 * Overrides drop behaviour to set the cell style if the target
		 * is not a valid drop target and the cells are of the same
		 * type (eg. both vertices or both edges). 
		 */
		public Object[] importCells(Object[] cells, double dx, double dy,
				Object target, Point location)
		{
			if (target == null && cells.length == 1 && location != null)
			{
				target = getCellAt(location.x, location.y);

				if (target instanceof mxICell && cells[0] instanceof mxICell)
				{
					mxICell targetCell = (mxICell) target;
					mxICell dropCell = (mxICell) cells[0];

					if (targetCell.isVertex() == dropCell.isVertex()
							|| targetCell.isEdge() == dropCell.isEdge())
					{
						mxIGraphModel model = graph.getModel();
						model.setStyle(target, model.getStyle(cells[0]));
						graph.setSelectionCell(target);

						return null;
					}
				}
			}

			return super.importCells(cells, dx, dy, target, location);
		}

	}

	/**
	 * A graph that creates new edges from a given template edge.
	 */
	public static class CustomGraph extends mxGraph
	{
		/**
		 * Holds the edge to be used as a template for inserting new edges.
		 */
		protected Object edgeTemplate;

		/**
		 * Custom graph that defines the alternate edge style to be used when
		 * the middle control point of edges is double clicked (flipped).
		 */
		public CustomGraph()
		{
			//setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
			//setAlternateEdgeStyle(EditorToolBar.edgestylevalue);

			int width=100;
			int height=100;

			//the new edge template is inizialised here
			mxGeometry geometry = new mxGeometry(0, 0, width, height);
			geometry.setTerminalPoint(new mxPoint(0, height), true);
			geometry.setTerminalPoint(new mxPoint(width, 0), false);
			geometry.setRelative(true);

			mxCell cell = new mxCell("[!a]", geometry,  EditorToolBar.edgestylevalue);
			cell.setEdge(true);
			setEdgeTemplate(cell);

		}

		/**
		 * Sets the edge template to be used to inserting edges.
		 */
		public void setEdgeTemplate(Object template)
		{
			edgeTemplate = template;
		}

		/**
		 * Prints out some useful information about the cell in the tooltip.
		 */
		public String getToolTipForCell(Object cell)
		{
			StringBuilder tip = new StringBuilder("<html>");
			mxGeometry geo = getModel().getGeometry(cell);
			mxCellState state = getView().getState(cell);

			if (getModel().isEdge(cell))
			{
				tip.append("points={");

				if (geo != null)
				{
					List<mxPoint> points = geo.getPoints();

					if (points != null)
					{

						for (mxPoint point : points) {
							tip.append("[x=").append(numberFormat.format(point.getX())).append(",y=").append(numberFormat.format(point.getY())).append("],");
						}

						tip = new StringBuilder(tip.substring(0, tip.length() - 1));
					}
				}

				tip.append("}<br>");
				tip.append(((mxCell) cell).getStyle()).append(" qui ");
				tip.append("absPoints={");

				if (state != null)
				{

					for (int i = 0; i < state.getAbsolutePointCount(); i++)
					{
						mxPoint point = state.getAbsolutePoint(i);
						tip.append("[x=").append(numberFormat.format(point.getX())).append(",y=").append(numberFormat.format(point.getY())).append("],");
					}

					tip = new StringBuilder(tip.substring(0, tip.length() - 1));
				}

				tip.append("}");
			}
			else
			{
				tip.append("geo=[");

				if (geo != null)
				{
					tip.append("x=").append(numberFormat.format(geo.getX())).append(",y=").append(numberFormat.format(geo.getY())).append(",width=").append(numberFormat.format(geo.getWidth())).append(",height=").append(numberFormat.format(geo.getHeight()));
				}

				tip.append("]<br>");
				tip.append("state=[");

				if (state != null)
				{
					tip.append("x=").append(numberFormat.format(state.getX())).append(",y=").append(numberFormat.format(state.getY())).append(",width=").append(numberFormat.format(state.getWidth())).append(",height=").append(numberFormat.format(state.getHeight()));
				}

				tip.append("]");
			}

			mxPoint trans = getView().getTranslate();

			tip.append("<br>scale=").append(numberFormat.format(getView().getScale())).append(", translate=[x=").append(numberFormat.format(trans.getX())).append(",y=").append(numberFormat.format(trans.getY())).append("]");
			tip.append("</html>");

			return tip.toString();
		}

		/**
		 * Overrides the method to use the currently selected edge template for
		 * new edges.
		 *
		 */
		@Override
		public Object createEdge(Object parent, String id, Object value,
				Object source, Object target, String style)
		{
			if (edgeTemplate != null)
			{
				mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
				edge.setId(id);

				return edge;
			}

			return super.createEdge(parent, id, value, source, target, style);
		}

		/**
		 * overrides the method for avoiding the duplication of a state generated 
		 * by dragging an arrow from a source state
		 */
		@Override
		public Object addCell(Object cell, Object parent, Integer index,
				Object source, Object target) {
			if (target!=null)
			{
				((mxCell) target).setStyle(MxState.nodestylevalue+";");
				mxCell n=(mxCell) target;
				double x=n.getGeometry().getX();
				double y=n.getGeometry().getY();
				n.setGeometry(new mxGeometry(x, y, 40, 40));

				n.setValue("["+EditorToolBar.incrementalStateLabel+"]");
				EditorToolBar.incrementalStateLabel++;
			}
			return super.addCell(cell, parent, index, source, target);
		}
	}

	/**
	 * Entry point for running FMCAT
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

		App editor = new App();
		editor.menuFrame = editor.createFrame(new EditorMenuBar(editor));
		editor.menuFrame.setVisible(true);
	}

	/**
	 * utility for rearranging the graphical display of the automaton
	 */
	public static void morphGraph(final mxGraph graph,
			mxGraphComponent graphComponent) 
	{
		// define layout
		mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);

		layout.setForceConstant(100);
		layout.setDisableEdgeStyle( false);
		//mxGraphModel mg=(mxGraphModel) graph.getModel();
		//mxCell cell = (mxCell) ((mxGraphModel)mg).getCell("3");

		// layout using morphing
		graph.getModel().beginUpdate();
		try {
			layout.execute(graph.getDefaultParent());
		} finally {
			mxMorphing morph = new mxMorphing(graphComponent, 20, 1.5, 20);
			morph.addListener(mxEvent.DONE, (Object arg0, mxEventObject arg1) -> graph.getModel().endUpdate());
			morph.startAnimation();
		}

	}

}
