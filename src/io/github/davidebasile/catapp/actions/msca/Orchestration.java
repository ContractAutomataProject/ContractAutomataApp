package io.github.davidebasile.catapp.actions.msca;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import io.github.davidebasile.catapp.App;
import io.github.davidebasile.catapp.EditorActions;
import io.github.davidebasile.catapp.EditorMenuBar;
import io.github.davidebasile.catapp.converters.MxeConverter;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;

@SuppressWarnings("serial")
public class Orchestration extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		if (menuBar.checkAut(editor)) return;
		String filename=editor.getCurrentFile().getName();

		menuBar.lastDir=editor.getCurrentFile().getParent();

		ModalAutomaton<CALabel> aut=editor.lastaut;

		ModalAutomaton<CALabel> controller=null;
		Instant start = Instant.now();
	
		try {
			controller = new OrchestrationSynthesisOperator(new Agreement()).apply(aut);
		} catch(UnsupportedOperationException exc) {
			Instant stop = Instant.now();
			long elapsedTime = Duration.between(start, stop).toMillis();
			if (exc.getMessage()=="The automaton contains necessary offers that are not allowed in the orchestration synthesis")
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),
						exc.getMessage()+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds",
						"Error",JOptionPane.ERROR_MESSAGE);
				//	editor.lastaut=backup;
				return;
			} else throw exc;
		}

		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
	
		if (controller==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The orchestration is empty"+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds","Empty",JOptionPane.WARNING_MESSAGE);
			//editor.lastaut=backup;
			return;
		}
		String K="Orc_"+filename;

		File file;
		try {
			new MxeConverter().exportMSCA(menuBar.lastDir+File.separator+K,controller);
			file = new File(menuBar.lastDir+File.separator+K);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(editor.getGraphComponent(),
					"Error in saving the file "+e1.getMessage(),
					"Error",JOptionPane.ERROR_MESSAGE);

			return;			
		}
		String message = "The orchestration has been stored with filename "+menuBar.lastDir+File.separator+K
				+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds"
				+System.lineSeparator()+" Number of states : "+controller.getNumStates();

		JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.PLAIN_MESSAGE);

		editor.lastaut=controller;
		menuBar.loadMorphStore(menuBar.lastDir+File.separator+K,editor,file);

	}

}
