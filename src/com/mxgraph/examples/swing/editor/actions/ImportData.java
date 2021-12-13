package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import contractAutomata.automaton.MSCA;
import contractAutomata.converters.DataConverter;
import converters.MxeConverter;

@SuppressWarnings("serial")
public class ImportData extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		
		if (!menuBar.loseChanges.test(editor)) return;

		mxGraph graph = editor.getGraphComponent().getGraph();
		if (graph == null) return;

		JFileChooser fc = new JFileChooser(
				(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent(): System.getProperty("user.dir"));

		// Adds file filter for supported file format
		menuBar.setDefaultFilter(fc,".data","FMCA description",null);

		int rc = fc.showDialog(null,
				mxResources.get("openFile"));
		if (rc == JFileChooser.APPROVE_OPTION)
		{
			menuBar.lastDir = fc.getSelectedFile().getParent();	
			MSCA aut;
			try {
				String filename = fc.getSelectedFile().toString();
				aut = new DataConverter().importMSCA(filename);
				filename = filename.substring(0,filename.lastIndexOf("."));
				new MxeConverter().exportMSCA(filename,aut);

				filename = filename+".mxe";
				File file = new File(filename);
				editor.lastaut=aut;
				menuBar.loadMorphStore(file.getName(), editor, file);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.toString(),mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			}
		}

		
	}

}
