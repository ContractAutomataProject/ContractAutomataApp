package io.github.davidebasile.catapp.actions;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.swing.AbstractAction;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.mxGraphComponent;

import io.github.davidebasile.catapp.castate.MxCAState;

public class ToggleInitialStateAction extends AbstractAction
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
	public ToggleInitialStateAction(mxCell node)
	{
		this.node=node;
	}

	public void actionPerformed(ActionEvent e)
	{
		mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
		
		Consumer<mxCell> reset = n->{
			if (MxCAState.isFinal.test(n))
				n.setStyle(MxCAState.finalnodestylevalue);
			else
				n.setStyle(MxCAState.nodestylevalue);
			
			double x=n.getGeometry().getX();
			double y=n.getGeometry().getY();
			n.setGeometry(new mxGeometry(x, y, 40, 40));
		};

		if (MxCAState.isInitial.test(node))
			reset.accept(node);//reset
		else {
			graphComponent.getGraph().selectAll();
			Arrays.stream(graphComponent.getGraph().getSelectionCells())
			.map(x->(mxCell)x)
			.filter(x->x!=null && MxCAState.isInitial.test(x))
			.forEach(reset);

			if (MxCAState.isFinal.test(node))
				node.setStyle(MxCAState.initialfinalnodestylevalue); 
			else
				node.setStyle(MxCAState.initialnodestylevalue);
	
			double x=node.getGeometry().getX();
			double y=node.getGeometry().getY();
			node.setGeometry(new mxGeometry(x, y, 40+MxCAState.initialStateWidthIncrement, 40));

			graphComponent.getGraph().clearSelection();
			graphComponent.getGraph().addSelectionCell(node);
		}
		graphComponent.refresh();
	}


	public static String addStencilShape(String nodeXml)
	{
		// Some editors place a 3 byte BOM at the start of files
		// Ensure the first char is a "<"
		int lessthanIndex = nodeXml.indexOf("<");
		nodeXml = nodeXml.substring(lessthanIndex);
		mxStencilShape newShape = new mxStencilShape(nodeXml);
		String name = newShape.getName();

		mxGraphics2DCanvas.putShape(name, newShape);
		return name;
	}

}
