package io.github.contractautomata.catapp.actions.fmca;

import com.mxgraph.util.mxResources;
import io.github.contractautomata.catapp.App;
import io.github.contractautomata.catapp.EditorActions;
import io.github.contractautomata.catapp.EditorMenuBar;
import io.github.contractautomata.catapp.ProductFrame;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.family.FMCA;
import io.github.contractautomata.catlib.family.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class CanonicalProducts extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) Objects.requireNonNull(editor).getMenuFrame().getJMenuBar();
		if (menuBar.checkAut(editor)) return;

		ProductFrame pf=editor.getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		menuBar.lastDir=editor.getCurrentFile().getParent();

		Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut=editor.lastaut;


		//	int[][] ind=new int[1][];

		Map<Product,Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> cp;
		Instant start;
		if (!aut.getForwardStar(aut.getInitial()).stream()
				.map(ModalTransition<String,Action,State<String>,CALabel>::getLabel)
				.allMatch(l->l.getAction().getLabel().equals("dummy")))
		{
			start = Instant.now();
			cp=new FMCA(aut,pf.getFamily()).getCanonicalProducts();
		}
		else
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"Operation not supported for an orchestration of a family","",JOptionPane.WARNING_MESSAGE);
			return;
		}	
		//	Product[] cp=fam.getCanonicalProducts(aut,null,false,ind);


		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
	
		if (cp==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Canonical Products"+System.lineSeparator()+" Elapsed time : "+elapsedTime+ " milliseconds",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}



		pf.setColorButtonProducts(cp.keySet(), Color.ORANGE);
		StringBuilder message= new StringBuilder("Canonical Products:" + System.lineSeparator());
		for (Product p : cp.keySet())
			message.append(pf.indexOf(p)).append(" : ").append(System.lineSeparator()).append(p.toString()).append(System.lineSeparator());

		message.append("Elapsed time : ").append(elapsedTime).append(" milliseconds");
		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message.toString());
		textArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		JDialog jd = new JDialog(pf);
		jd.add(scrollPane);
		jd.setTitle("Canonical Products");
		jd.setResizable(true);
		jd.setVisible(true);
		jd.setSize(500,500);
		jd.setLocationRelativeTo(null);
		
	}

}
