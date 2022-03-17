package io.github.contractautomataproject.catapp.actions.fmca;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import io.github.contractautomataproject.catapp.App;
import io.github.contractautomataproject.catapp.EditorActions;
import io.github.contractautomataproject.catapp.EditorMenuBar;
import io.github.contractautomataproject.catapp.ProductFrame;
import io.github.contractautomataproject.catlib.family.Family;
import io.github.contractautomataproject.catlib.family.converters.ProdFamilyConverter;

@SuppressWarnings("serial")
public class LoadFamily extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		
		if (!menuBar.loseChanges.test(editor)) return;

		mxGraph graph = editor.getGraphComponent().getGraph();
		if (graph == null) return;

		ProductFrame pf=editor.getProductFrame();
		if (pf!=null)
		{
			editor.setProductFrame(null);
			pf.dispose();
		}

		JFileChooser fc = new JFileChooser(
				(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent(): System.getProperty("user.dir"));

		menuBar.setDefaultFilter(fc,".prod","Products list",null);

		int rc = fc.showDialog(null,mxResources.get("openFile"));
		

		if (rc == JFileChooser.APPROVE_OPTION)
		{
			menuBar.lastDir = fc.getSelectedFile().getParent();
			String fileName =fc.getSelectedFile().toString();
			try {
				Family fam=new Family(new ProdFamilyConverter().importProducts(fileName));
				pf= new ProductFrame(fam, (JPanel)editor,null);
				editor.setProductFrame(pf);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"IOException "+ex.getMessage(),
						mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}

}
