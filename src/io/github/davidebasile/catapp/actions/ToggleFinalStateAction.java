package io.github.davidebasile.catapp.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

import io.github.davidebasile.catapp.castate.MxCAState;

public class ToggleFinalStateAction extends AbstractAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private mxCell node;
	
	/**
	 * 
	 * @param key
	 */
	public ToggleFinalStateAction(mxCell node)
	{
		this.node=node;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (MxCAState.isFinal.test(node)) {
			if (MxCAState.isInitial.test(node))
				node.setStyle(MxCAState.initialnodestylevalue);
			else
				node.setStyle(MxCAState.nodestylevalue);
		}
		else {
			if (MxCAState.isInitial.test(node))
				node.setStyle(MxCAState.initialfinalnodestylevalue);			
			else
				node.setStyle(MxCAState.finalnodestylevalue);				
		};
		mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
		graphComponent.refresh();
	}
}
