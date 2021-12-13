package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

import castate.MxCAState;

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
		MxCAState.toggleFinalState(node);
		mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
		graphComponent.refresh();
	}
}
