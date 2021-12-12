package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

public class DeleteHandleAction extends AbstractAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private mxPoint pt;

	/**
	 * 
	 * @param key
	 */
	public DeleteHandleAction(mxPoint pt)
	{
		this.pt=pt;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof mxGraphComponent)
		{
			mxGraphComponent graphComponent = (mxGraphComponent) e
					.getSource();
			mxGraph graph = graphComponent.getGraph();

			if (!graph.isSelectionEmpty())
			{
				mxCell edge = (mxCell) graph.getSelectionCell();
				mxGeometry eg = edge.getGeometry();
				eg.setPoints(eg.getPoints().stream()
						.filter(p->!p.equals(pt))
						.collect(Collectors.toList()));
				graphComponent.refresh();
			}
		}
	}


}
