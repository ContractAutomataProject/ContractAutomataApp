package io.github.contractautomataproject.catapp.actions.fmca;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import io.github.contractautomataproject.catapp.App;
import io.github.contractautomataproject.catapp.EditorActions;
import io.github.contractautomataproject.catapp.EditorMenuBar;
import io.github.contractautomataproject.catapp.converters.MxeConverter;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.family.Product;
import io.github.contractautomataproject.catlib.operators.OrchestrationSynthesisOperator;
import io.github.contractautomataproject.catlib.operators.ProductOrchestrationSynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.Agreement;

@SuppressWarnings("serial")
public class OrchestrationProductType extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();

		if (menuBar.checkAut(editor)) return;
		String filename=editor.getCurrentFile().getName();

		menuBar.lastDir=editor.getCurrentFile().getParent();
		ModalAutomaton<CALabel> aut=editor.lastaut;
		//	MSCA backup = aut.clone();//in case aut becomes null

		String S= (String) JOptionPane.showInputDialog(null, 
				"Insert Required features separated by colon",
				JOptionPane.PLAIN_MESSAGE);
		if (S==null)
			return;
		String[] R=S.split(",");

		if (R[0].equals(""))
			R=new String[0];

		S= (String) JOptionPane.showInputDialog(null, 
				"Insert Forbidden actions separated by semicolon",
				JOptionPane.PLAIN_MESSAGE);
		if (S==null)
			return;
		String[] F=S.split(",");
		if (F[0].equals(""))
			F=new String[0];

		Product p=(R.length+F.length>0)?new Product(R,F):null;

		ModalAutomaton<CALabel> controller=null;
		//	FMCA faut= new FMCA(aut,editor.getProductFrame().getFamily());
		OrchestrationSynthesisOperator synth = (p!=null)?
				new ProductOrchestrationSynthesisOperator(new Agreement(),p)
				:new OrchestrationSynthesisOperator(new Agreement());

		Instant start = Instant.now();
		controller=  synth.apply(aut); //(p!=null)?faut.orchestration(p):aut.orchestration(); 

		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();

		if (controller==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The orchestration is empty"+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds","",JOptionPane.WARNING_MESSAGE);
			//	editor.lastaut=backup;
			return;
		}

		String K="Orc_"+"(R"+Arrays.toString(R)+"_F"+Arrays.toString(F)+")_"+filename;
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

		JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.WARNING_MESSAGE);
		editor.lastaut=controller;
		menuBar.loadMorphStore(menuBar.lastDir+File.separator+K,editor,file);



	}

}
