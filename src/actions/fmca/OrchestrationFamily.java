package actions.fmca;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.examples.swing.editor.ProductFrame;
import com.mxgraph.util.mxResources;

import converters.MxeConverter;
import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.family.FMCA;
import io.github.davidebasile.contractautomata.family.Family;

@SuppressWarnings("serial")
public class OrchestrationFamily extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		if (menuBar.checkAut(editor)) return;
		String filename=editor.getCurrentFile().getName();

		ProductFrame pf=editor.getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		menuBar.lastDir=editor.getCurrentFile().getParent();

		MSCA aut=editor.lastaut;

		Family f=pf.getFamily();


		Instant start = Instant.now();
		MSCA controller = new FMCA(aut,f).getOrchestrationOfFamily();
		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
	

		if (controller==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The orchestration is empty"+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds","",JOptionPane.WARNING_MESSAGE);
			//	editor.lastaut=backup;
			return;
		}

		String K="Orc_family_"+filename;

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

		String message = "The orchestration has been stored with filename "+menuBar.lastDir+File.separator
				+ K
				+ System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds"
				+ System.lineSeparator()+" Number of states : "+controller.getNumStates();
		;

		JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.WARNING_MESSAGE);

		editor.lastaut=controller;
		menuBar.loadMorphStore(K,editor,file);
		
	}

}
