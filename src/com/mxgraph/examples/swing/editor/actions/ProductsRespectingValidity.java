package com.mxgraph.examples.swing.editor.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.ProductFrame;
import com.mxgraph.util.mxResources;

import contractAutomata.MSCA;
import contractAutomata.MSCATransition;
import family.FMCA;
import family.Product;

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

		MSCA aut=editor.lastaut;

		long start = System.currentTimeMillis();
		//int[] vp= pf.getFamily().validProducts(aut);

		Set<Product> vpp;

		if (!aut.getForwardStar(aut.getInitial()).stream()
				.map(MSCATransition::getLabel)
				.allMatch(l->l.getUnsignedAction().equals("dummy")))
			vpp= new FMCA(aut,pf.getFamily()).productsRespectingValidity();
		else
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"Operation not supported for an orchestration of a family","",JOptionPane.WARNING_MESSAGE);
			return;
			//vpp=new FMCA(aut,pf.getFamily()).respectingValidityFamily();
		}

		long elapsedTime= System.currentTimeMillis() - start;

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
