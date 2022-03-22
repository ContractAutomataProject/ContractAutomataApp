package io.github.contractautomata.catapp.actions.fmca;

import com.mxgraph.util.mxResources;
import io.github.contractautomata.catapp.App;
import io.github.contractautomata.catapp.EditorActions;
import io.github.contractautomata.catapp.EditorMenuBar;
import io.github.contractautomata.catapp.ProductFrame;
import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;
import io.github.contractautomataproject.catlib.family.FMCA;
import io.github.contractautomataproject.catlib.family.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@SuppressWarnings("serial")
public class ProductsRespectingValidity extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		ProductFrame pf=editor.getProductFrame();
		if (menuBar.checkAut(editor)) return;

		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		menuBar.lastDir=editor.getCurrentFile().getParent();

		Automaton<String,Action,State<String>, ModalTransition<String, Action,State<String>,CALabel>> aut=editor.lastaut;

		Instant start = Instant.now();
		Set<Product> vpp;

		if (!aut.getForwardStar(aut.getInitial()).stream()
				.map(ModalTransition<String,Action,State<String>,CALabel>::getLabel)
				.allMatch(l->l.getAction().getLabel().equals("dummy")))
			vpp= new FMCA(aut,pf.getFamily()).productsRespectingValidity();
		else
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"Operation not supported for an orchestration of a family","",JOptionPane.WARNING_MESSAGE);
			return;
			//vpp=new FMCA(aut,pf.getFamily()).respectingValidityFamily();
		}

		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();

		if (vpp==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Products Respecting Validity",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		pf.setColorButtonProducts(vpp, Color.BLUE);
		String message=vpp.size()+ " Products Respecting Validity Found:"+System.lineSeparator();

		for (Product p : vpp)
			message+= pf.indexOf(p)+" : "+System.lineSeparator()+p.toString()+System.lineSeparator();

		message += "Elapsed Time " + elapsedTime + " milliseconds";
		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message);
		textArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		JDialog jd = new JDialog(pf);
		jd.add(scrollPane);
		jd.setTitle("Products Respecting Validity");
		jd.setResizable(true);
		jd.setVisible(true);
		jd.setSize(500,500);
		jd.setLocationRelativeTo(null);

	}

}
