package io.github.davidebasile.catapp.actions.fmca;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
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
	protected String name;

	/**
	 * 
	 * @param key
	 */
	public ModalLabelAction(String name)
	{
		this.name = name;
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
					graph.setCellStyles( mxConstants.STYLE_STROKECOLOR, "#FF0000");// mxUtils.hexString( newColor));
				else if (this.name=="Greedy")
					graph.setCellStyles( mxConstants.STYLE_STROKECOLOR, "#FFA500");// mxUtils.hexString( newColor));
				else if (this.name=="Lazy")
					graph.setCellStyles( mxConstants.STYLE_STROKECOLOR, "#00FF00");// mxUtils.hexString( newColor));
				else if (this.name=="Permitted")
					graph.setCellStyles( mxConstants.STYLE_STROKECOLOR, "black");
			}
		}
	}


}
