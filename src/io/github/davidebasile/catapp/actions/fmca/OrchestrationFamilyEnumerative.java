package io.github.davidebasile.catapp.actions.fmca;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.util.mxResources;

import io.github.davidebasile.catapp.App;
import io.github.davidebasile.catapp.EditorActions;
import io.github.davidebasile.catapp.EditorMenuBar;
import io.github.davidebasile.catapp.ProductFrame;
import io.github.davidebasile.catapp.converters.MxeConverter;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.family.FMCA;
import io.github.davidebasile.contractautomata.family.Family;

@SuppressWarnings("serial")
public class OrchestrationFamilyEnumerative extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		if (menuBar.checkAut(editor)) return;
		String filename=editor.getCurrentFile().getName();

		ProductFrame pf=editor.getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		menuBar.lastDir=editor.getCurrentFile().getParent();
		ModalAutomaton<CALabel> aut=editor.lastaut;
		Family f=pf.getFamily();

		JOptionPane.showMessageDialog(editor.getGraphComponent(),"Warning : the enumerative computation may require several minutes!","Warning",JOptionPane.WARNING_MESSAGE);


		Instant start = Instant.now();
		ModalAutomaton<CALabel> controller = new FMCA(aut,f).getOrchestrationOfFamilyEnumerative();
		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
	

		if (controller==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The orchestration is empty"+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds","Empty",JOptionPane.WARNING_MESSAGE);
			return;
		}

		String K="Orc_familyWithoutPO_"+filename;
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

		String message = "The orchestration has been stored with filename "+menuBar.lastDir+File.separator+K;


		JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.WARNING_MESSAGE);
		editor.lastaut=controller;
		menuBar.loadMorphStore(K,editor,file);
		
	}

}
