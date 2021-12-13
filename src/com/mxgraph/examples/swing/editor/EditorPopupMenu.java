package com.mxgraph.examples.swing.editor;

import java.awt.Point;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.mxgraph.examples.swing.editor.actions.AddHandleAction;
import com.mxgraph.examples.swing.editor.actions.DeleteHandleAction;
import com.mxgraph.examples.swing.editor.actions.ToggleFinalStateAction;
import com.mxgraph.examples.swing.editor.actions.ToggleInitialStateAction;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxResources;

import actions.fmca.ModalLabelAction;

public class EditorPopupMenu extends JPopupMenu
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3132749140550242191L;

	public EditorPopupMenu(BasicGraphEditor editor, Point pt)
	{
		boolean selected = !editor.getGraphComponent().getGraph()
				.isSelectionEmpty();

		//		add(editor.bind(mxResources.get("undo"), new HistoryAction(true),
		//				"/com/mxgraph/examples/swing/images/undo.gif"));
		//
		//		addSeparator();



		mxCell edge = this.cellSelected(editor, pt, x->x.isEdge());
		if (edge!=null) {

		
			add(editor.bind("Add handle", new AddHandleAction(pt),"/com/mxgraph/examples/swing/images/diamond_end.gif"));

			List<mxPoint> l = edge.getGeometry().getPoints(); 
			if (l!=null)
			{
				mxPoint sp = l.stream()
						.filter(p->pt.distance(p.getX(), p.getY())<4)
						.findFirst().orElse(null);

				if (sp!=null)
					add(editor.bind("Delete handle", new DeleteHandleAction(sp),"/com/mxgraph/examples/swing/images/delete.gif"));
			}

			//JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("line")));

			add(editor.bind("Urgent", new ModalLabelAction("Urgent"),"/com/mxgraph/examples/swing/images/arrow.gif"));
			//"/com/mxgraph/examples/swing/images/linecolor.gif"));
			//add(editor.bind("Greedy", new FMCAAction("Greedy", mxConstants.STYLE_STROKECOLOR),""));  REMOVED!!
			add(editor.bind("Lazy", new ModalLabelAction("Lazy"),"/com/mxgraph/examples/swing/images/arrow.gif"));
			add(editor.bind("Permitted", new ModalLabelAction("Permitted"),"/com/mxgraph/examples/swing/images/arrow.gif"));

			addSeparator();

		}
		
		mxCell node = this.cellSelected(editor, pt, x->x.isVertex());
	
		if (node!=null) {
			add(editor.bind("Toggle Initial state", new ToggleInitialStateAction(node),"/com/mxgraph/examples/swing/images/sstate.png"));
			add(editor.bind("Toggle Final state", new ToggleFinalStateAction(node),"/com/mxgraph/examples/swing/images/fstate.png"));
			addSeparator();
		}
		if (selected) {			
			add(
					editor.bind(mxResources.get("delete"), mxGraphActions
							.getDeleteAction(),
							"/com/mxgraph/examples/swing/images/delete.gif"))
			.setEnabled(selected);

			add(
					editor.bind(mxResources.get("cut"), TransferHandler
							.getCutAction(),
							"/com/mxgraph/examples/swing/images/cut.gif"))
			.setEnabled(selected);
			add(
					editor.bind(mxResources.get("copy"), TransferHandler
							.getCopyAction(),
							"/com/mxgraph/examples/swing/images/copy.gif"))
			.setEnabled(selected);
		}

		if (!selected) {
			add(editor.bind(mxResources.get("paste"), TransferHandler
					.getPasteAction(),
					"/com/mxgraph/examples/swing/images/paste.gif"));
		}



		/*	addSeparator();

		// Creates the format menu
		JMenu menu = (JMenu) add(new JMenu(mxResources.get("format")));

		EditorMenuBar.populateFormatMenu(menu, editor);

		// Creates the shape menu
		menu = (JMenu) add(new JMenu(mxResources.get("shape")));

		EditorMenuBar.populateShapeMenu(menu, editor);

		addSeparator();

		add(
				editor.bind(mxResources.get("edit"), mxGraphActions
						.getEditAction())).setEnabled(selected);

		addSeparator();

		add(editor.bind(mxResources.get("selectVertices"), mxGraphActions
				.getSelectVerticesAction()));
		add(editor.bind(mxResources.get("selectEdges"), mxGraphActions
				.getSelectEdgesAction()));

		addSeparator();

		add(editor.bind(mxResources.get("selectAll"), mxGraphActions
				.getSelectAllAction()));
		 */	
	}


	
	private mxCell cellSelected(BasicGraphEditor editor, Point pt, Predicate<mxCell> pred) {
		if (!editor.getGraphComponent().getGraph()
				.isSelectionEmpty() && editor.getGraphComponent().getGraph().getSelectionCell() instanceof mxCell) {
			mxCell c =(mxCell)  editor.getGraphComponent().getGraph().getSelectionCell();
			if (pred.test(c)) return c;
		}
		Object o=editor.getGraphComponent().getCellAt(pt.x,pt.y);
		if(o!=null && o instanceof mxCell) {
			mxCell c=(mxCell)o;
			if (pred.test(c))
				return c;
		}

		return null;
	}

}
