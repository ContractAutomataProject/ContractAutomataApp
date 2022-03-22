package io.github.contractautomata.catapp.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

import io.github.contractautomata.catapp.castate.MxState;

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
		if (MxState.isFinal.test(node)) {
			if (MxState.isInitial.test(node))
				node.setStyle(MxState.initialnodestylevalue);
			else
				node.setStyle(MxState.nodestylevalue);
		}
		else {
			if (MxState.isInitial.test(node))
				node.setStyle(MxState.initialfinalnodestylevalue);			
			else
				node.setStyle(MxState.finalnodestylevalue);				
		};
		mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
		graphComponent.refresh();
	}
}
