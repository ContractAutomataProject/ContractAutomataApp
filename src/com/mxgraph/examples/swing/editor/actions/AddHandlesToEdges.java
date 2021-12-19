package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.util.mxResources;

import converters.MxeConverter;
import io.github.davidebasile.contractautomata.automaton.MSCA;

@SuppressWarnings("serial")
public class AddHandlesToEdges extends AbstractAction 
{

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();

		if (menuBar.checkAut(editor)) return;
		if (editor.isModified()) {
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"Save model first",mxResources.get("warning"),JOptionPane.PLAIN_MESSAGE);
			return;
		}

		menuBar.lastDir=editor.getCurrentFile().getParent();

		String absfilename =editor.getCurrentFile().getAbsolutePath();
		MSCA aut=editor.lastaut;

		try {
			aut=new MxeConverter().importMSCA(absfilename);
			new MxeConverter().exportMSCA(absfilename,aut);
			File file = new File(absfilename);
			menuBar.parseAndSet(absfilename, editor,file);
		} 
		catch(Exception e1)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+System.lineSeparator()+" "+menuBar.getErrorMsg(),mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
		}

	}
	
}
