package com.mxgraph.examples.swing.editor.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.ProductFrame;
import com.mxgraph.util.mxResources;

import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.transition.MSCATransition;
import family.FMCA;
import family.Product;

@SuppressWarnings("serial")
public class CanonicalProducts extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		if (menuBar.checkAut(editor)) return;

		ProductFrame pf=editor.getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		menuBar.lastDir=editor.getCurrentFile().getParent();

		MSCA aut=editor.lastaut;


		//	int[][] ind=new int[1][];

		Map<Product,MSCA> cp;
		Instant start;
		if (!aut.getForwardStar(aut.getInitial()).stream()
				.map(MSCATransition::getLabel)
				.allMatch(l->l.getUnsignedAction().equals("dummy")))
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
		String message="Canonical Products:"+System.lineSeparator();
		for (Product p : cp.keySet())
			message+= pf.indexOf(p)+" : "+System.lineSeparator()+p.toString()+System.lineSeparator();

		message += "Elapsed time : "+elapsedTime+ " milliseconds";
		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message);
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
