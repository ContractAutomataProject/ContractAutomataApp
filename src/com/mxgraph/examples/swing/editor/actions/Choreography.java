package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;

import contractAutomata.MSCA;
import contractAutomata.converters.MxeConverter;
import contractAutomata.operators.ChoreographySynthesisOperator;
import contractAutomata.requirements.StrongAgreement;

@SuppressWarnings("serial")
public class Choreography extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		if (menuBar.checkAut(editor)) return;
		String filename=editor.getCurrentFile().getName();

		menuBar.lastDir=editor.getCurrentFile().getParent();
		MSCA aut=editor.lastaut;
		//	MSCA backup = aut.clone();

		MSCA controller=null;
		long start = System.currentTimeMillis();
		try {
			controller = new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut);
		} catch(UnsupportedOperationException exc) {
			long elapsedTime = System.currentTimeMillis() - start;
			if (exc.getMessage()=="The automaton contains necessary requests that are not allowed in the choreography synthesis")
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),
						exc.getMessage()+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds",
						"Error",JOptionPane.ERROR_MESSAGE);
				//	editor.lastaut=backup;
				return;
			} else throw exc;
		}
		long elapsedTime = System.currentTimeMillis() - start;

		if (controller==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The choreography is empty"+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds","Empty",JOptionPane.WARNING_MESSAGE);
			//	editor.lastaut=backup;
			return;
		}
		String K="Chor_"+//"(R"+Arrays.toString(R)+"_F"+Arrays.toString(F)+")_"+
				filename;
		File file;
		try {
			new MxeConverter().exportMSCA(menuBar.lastDir+File.separator+K,controller);
			file = new File(menuBar.lastDir+File.separator+K);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(editor.getGraphComponent(),
					"Error in saving the file "+e1.getMessage(),
					"Error",JOptionPane.ERROR_MESSAGE);

			return;			
		};
		String message = "The choreography has been stored with filename "+menuBar.lastDir+File.separator+K
				+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds"
				+System.lineSeparator()+" Number of states : "+controller.getNumStates();

		JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.PLAIN_MESSAGE);

		editor.lastaut=controller;
		menuBar.loadMorphStore(menuBar.lastDir+File.separator+K,editor,file);
		
	}

}
