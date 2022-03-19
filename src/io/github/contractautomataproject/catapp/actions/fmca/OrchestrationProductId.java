package io.github.contractautomataproject.catapp.actions.fmca;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.util.mxResources;

import io.github.contractautomataproject.catapp.App;
import io.github.contractautomataproject.catapp.EditorActions;
import io.github.contractautomataproject.catapp.EditorMenuBar;
import io.github.contractautomataproject.catapp.ProductFrame;
import io.github.contractautomataproject.catapp.converters.MxeConverter;
import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.family.Product;
import io.github.contractautomataproject.catlib.operators.ProductOrchestrationSynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

@SuppressWarnings("serial")
public class OrchestrationProductId extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		
		if (menuBar.checkAut(editor)) return;
		String filename=editor.getCurrentFile().getName();

		menuBar.lastDir=editor.getCurrentFile().getParent();

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut=editor.lastaut;
		//	MSCA backup = aut.clone();

		ProductFrame pf=editor.getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		//FMCA faut= new FMCA(aut,pf.getFamily());

		String S= (String) JOptionPane.showInputDialog(editor.getGraphComponent(), 
				"Insert Product id",
				JOptionPane.PLAIN_MESSAGE);
		if (S==null)
			return;

		//Product p=f.getElements()[Integer.parseInt(S)];
		Product p=pf.getProductAt(Integer.parseInt(S));

		Instant start = Instant.now();
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> controller = new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut);
		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
	
		if (controller==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The orchestration is empty"+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds","Empty",JOptionPane.WARNING_MESSAGE);
			//		editor.lastaut=backup;
			return;
		}
		String K="Orc_"+"(R"+p.getRequired().toString()+"_F"+p.getForbidden().toString()+")_"+filename;
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
