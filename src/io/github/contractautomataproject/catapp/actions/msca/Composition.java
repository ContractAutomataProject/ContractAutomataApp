package io.github.contractautomataproject.catapp.actions.msca;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import io.github.contractautomataproject.catapp.App;
import io.github.contractautomataproject.catapp.EditorActions;
import io.github.contractautomataproject.catapp.EditorMenuBar;
import io.github.contractautomataproject.catapp.converters.MxeConverter;
import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.operators.MSCACompositionFunction;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.contractautomataproject.catlib.requirements.StrongAgreement;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

@SuppressWarnings("serial")
public class Composition extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();

		if (!menuBar.loseChanges.test(editor)) return;

		mxGraph graph = editor.getGraphComponent().getGraph();
		if (graph == null) return;

		JFileChooser fc = new JFileChooser(
				(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent(): System.getProperty("user.dir"));

		menuBar.setDefaultFilter(fc,".mxe","FMCA Description",null);

		fc.setDialogTitle("Select an FMCA to be composed");

		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(3);
		List<String> names= new ArrayList<>(3);

		boolean lastIteration=false;
		int rc = fc.showDialog(editor.getGraphComponent(),mxResources.get("openFile"));
		if (rc!=JFileChooser.APPROVE_OPTION)
			return;
		while (true)
		{
			menuBar.lastDir = fc.getSelectedFile().getParent();
			try
			{
				String fileName =fc.getSelectedFile().toString();
				names.add(fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.indexOf(".")));
				aut.add(new MxeConverter().importMSCA(fileName));
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+System.lineSeparator()
				+menuBar.getErrorMsg(),mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (lastIteration)
				break;

			rc = fc.showDialog(editor.getGraphComponent(),mxResources.get("openFile"));
			int reply=-1;
			if (rc == JFileChooser.APPROVE_OPTION)
			{
				String fileName =fc.getSelectedFile().toString();
				reply=JOptionPane.showOptionDialog(editor.getGraphComponent(), 
						"You have selected: "+names.toString().substring(0, names.toString().length()-1)
						+","+fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.indexOf("."))+"]", 
						"Composition", 
						JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						new String[]{"Compute Composition", "Load other automata","Cancel"}, 
						"default");
				lastIteration=(reply != JOptionPane.NO_OPTION);
				if (reply== JOptionPane.CANCEL_OPTION)
					return;
			}
			else 
				return;
		}

		int pruningOption=JOptionPane.showOptionDialog(editor.getGraphComponent(), 
				"", "Composition Type", 
				JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.INFORMATION_MESSAGE, 
				null, 
				new String[]{"Open", "Close for Agreement","Close for Strong Agreement"}, 
				"default");
		//			if (pruningOption== JOptionPane.CANCEL_OPTION)
		//				return;

		Instant start = Instant.now();
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> composition = (Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>) new MSCACompositionFunction(aut,
				(pruningOption==JOptionPane.YES_OPTION)?null:
					(pruningOption==JOptionPane.NO_OPTION)?new Agreement().negate():
						new StrongAgreement().negate()).apply(Integer.MAX_VALUE); 
		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();

		if (composition==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"Empty composition",mxResources.get("Empty composition")
					+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds",JOptionPane.PLAIN_MESSAGE);
			return;
		}

		String compositionname="("+	names.stream().reduce((x,y)->x+"x"+y).orElse("")+")";

		File file;
		try {
			new MxeConverter().exportMSCA(menuBar.lastDir+File.separator+compositionname,composition);
			file = new File(menuBar.lastDir+File.separator+compositionname+".mxe");
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(editor.getGraphComponent(),
					"Error in saving the file "+e1.getMessage(),
					"Error",JOptionPane.ERROR_MESSAGE);

			return;			
		}

		editor.lastaut=composition;
		String message = "The composition has been stored with filename "+menuBar.lastDir+File.separator+compositionname
				+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds"
				+System.lineSeparator()+" Number of states : "+composition.getNumStates();
		;
		JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.PLAIN_MESSAGE);

		menuBar.loadMorphStore(compositionname, editor, file);


	}

}
