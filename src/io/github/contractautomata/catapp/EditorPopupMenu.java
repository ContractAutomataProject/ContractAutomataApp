package io.github.contractautomata.catapp;

import java.awt.Point;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxResources;

import io.github.contractautomata.catapp.actions.AddHandleAction;
import io.github.contractautomata.catapp.actions.DeleteHandleAction;
import io.github.contractautomata.catapp.actions.ToggleFinalStateAction;
import io.github.contractautomata.catapp.actions.ToggleInitialStateAction;
import io.github.contractautomata.catapp.actions.fmca.ModalLabelAction;

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
		//				"/io/github/davidebasile/catapp/images/undo.gif"));
		//
		//		addSeparator();



		mxCell edge = this.cellSelected(editor, pt, mxCell::isEdge);
		if (edge!=null) {

		
			add(editor.bind("Add handle", new AddHandleAction(pt),"/io/github/davidebasile/catapp/images/diamond_end.gif"));

			List<mxPoint> l = edge.getGeometry().getPoints(); 
			if (l!=null)
			{
				l.stream()
						.filter(p -> pt.distance(p.getX(), p.getY()) < 4)
						.findFirst().ifPresent(sp -> add(editor.bind("Delete handle", new DeleteHandleAction(sp), "/io/github/davidebasile/catapp/images/delete.gif")));

			}

			//JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("line")));

			add(editor.bind("Urgent", new ModalLabelAction("Urgent"),"/io/github/davidebasile/catapp/images/arrow.gif"));
			//"/io/github/davidebasile/catapp/images/linecolor.gif"));
			//add(editor.bind("Greedy", new FMCAAction("Greedy", mxConstants.STYLE_STROKECOLOR),""));  REMOVED!!
			add(editor.bind("Lazy", new ModalLabelAction("Lazy"),"/io/github/davidebasile/catapp/images/arrow.gif"));
			add(editor.bind("Permitted", new ModalLabelAction("Permitted"),"/io/github/davidebasile/catapp/images/arrow.gif"));

			addSeparator();

		}
		
		mxCell node = this.cellSelected(editor, pt, mxCell::isVertex);
	
		if (node!=null) {
			add(editor.bind("Toggle Initial state", new ToggleInitialStateAction(node),"/io/github/davidebasile/catapp/images/sstate.png"));
			add(editor.bind("Toggle Final state", new ToggleFinalStateAction(node),"/io/github/davidebasile/catapp/images/fstate.png"));
			addSeparator();
		}
		if (selected) {			
			add(
					editor.bind(mxResources.get("delete"), mxGraphActions
							.getDeleteAction(),
							"/io/github/davidebasile/catapp/images/delete.gif"))
			.setEnabled(true);

			add(
					editor.bind(mxResources.get("cut"), TransferHandler
							.getCutAction(),
							"/io/github/davidebasile/catapp/images/cut.gif"))
			.setEnabled(true);
			add(
					editor.bind(mxResources.get("copy"), TransferHandler
							.getCopyAction(),
							"/io/github/davidebasile/catapp/images/copy.gif"))
			.setEnabled(true);
		}

		if (!selected) {
			add(editor.bind(mxResources.get("paste"), TransferHandler
					.getPasteAction(),
					"/io/github/davidebasile/catapp/images/paste.gif"));
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
		if(o instanceof mxCell) {
			mxCell c=(mxCell)o;
			if (pred.test(c))
				return c;
		}

		return null;
	}

}
