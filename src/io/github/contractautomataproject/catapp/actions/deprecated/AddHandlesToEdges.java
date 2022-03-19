package io.github.contractautomataproject.catapp.actions.deprecated;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.util.mxResources;

import io.github.contractautomataproject.catapp.App;
import io.github.contractautomataproject.catapp.EditorActions;
import io.github.contractautomataproject.catapp.EditorMenuBar;
import io.github.contractautomataproject.catapp.converters.MxeConverter;
import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

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
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut=editor.lastaut;

		try {
			aut=new MxeConverter().importMSCA(absfilename);
			new MxeConverter().exportMSCA(absfilename,aut);
			File file = new File(absfilename);
			menuBar.parseAndSet(absfilename, editor,file);
		} 
		catch(Exception e1)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+System.lineSeparator()+" "
		+menuBar.getErrorMsg(),mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
		}

	}
	
}
