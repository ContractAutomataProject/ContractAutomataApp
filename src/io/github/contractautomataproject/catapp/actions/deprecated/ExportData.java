package io.github.contractautomataproject.catapp.actions.deprecated;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.util.mxResources;

import io.github.contractautomataproject.catapp.App;
import io.github.contractautomataproject.catapp.EditorActions;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;

@SuppressWarnings("serial")
public class ExportData extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		
		String filename =editor.getCurrentFile().getAbsolutePath();
		filename = filename.substring(0,filename.length()-4);
		ModalAutomaton<CALabel> aut=editor.lastaut;
		//			try {
		//				aut = new BasicMxeConverter().importMxe(filename);
		//				editor.lastaut=aut;
		//			} catch (ParserConfigurationException|SAXException|IOException e1) {
		//				JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+System.lineSeparator()+errorMsg,mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
		//				return;
		//			}

		try {
			new AutDataConverter<CALabel>(CALabel::new).exportMSCA(filename,aut);
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The automaton has been stored with filename "+filename+".data","Success!",JOptionPane.PLAIN_MESSAGE);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"File not found"+e1.toString(),mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
		}	


		
	}

}
