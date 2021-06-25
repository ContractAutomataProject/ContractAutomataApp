package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.util.mxResources;

import contractAutomata.automaton.MSCA;
import contractAutomata.converters.DataConverter;

@SuppressWarnings("serial")
public class ExportData extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		
		String filename =editor.getCurrentFile().getAbsolutePath();
		MSCA aut=editor.lastaut;
		//			try {
		//				aut = new BasicMxeConverter().importMxe(filename);
		//				editor.lastaut=aut;
		//			} catch (ParserConfigurationException|SAXException|IOException e1) {
		//				JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+System.lineSeparator()+errorMsg,mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
		//				return;
		//			}

		try {
			new DataConverter().exportMSCA(filename,aut);
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The automaton has been stored with filename "+filename+".data","Success!",JOptionPane.PLAIN_MESSAGE);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"File not found"+e1.toString(),mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
		}	


		
	}

}
