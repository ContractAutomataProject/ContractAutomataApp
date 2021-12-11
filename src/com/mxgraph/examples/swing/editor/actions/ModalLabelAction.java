package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class ModalLabelAction extends AbstractAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	protected String name, key;

	/**
	 * 
	 * @param key
	 */
	public ModalLabelAction(String name, String key)
	{
		this.name = name;
		this.key = key;
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
				/*Color newColor = JColorChooser.showDialog(graphComponent,
						name, null);

				if (newColor != null)
				{
					graph.setCellStyles(key, mxUtils.hexString(newColor));
				}*/
				if (this.name=="Urgent")
					graph.setCellStyles(key, "#FF0000");// mxUtils.hexString( newColor));
				else if (this.name=="Greedy")
					graph.setCellStyles(key, "#FFA500");// mxUtils.hexString( newColor));
				else if (this.name=="Lazy")
					graph.setCellStyles(key, "#00FF00");// mxUtils.hexString( newColor));
				else if (this.name=="Permitted")
					graph.setCellStyles(key,"");
			}
		}
	}


}
