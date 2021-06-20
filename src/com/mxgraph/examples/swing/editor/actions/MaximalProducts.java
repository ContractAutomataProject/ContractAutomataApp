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

import family.Family;
import family.Product;

@SuppressWarnings("serial")
public class MaximalProducts extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
	//	EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		
		ProductFrame pf=editor.getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		Family fam= pf.getFamily();

		long start = System.currentTimeMillis();
		//int[] pid = fam.getMaximalProducts();
		Set<Product> cp= fam.getMaximalProducts(); //fam.subsetOfProductsFromIndex(pid);
		long elapsedTime = System.currentTimeMillis() - start;

		if (cp==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Maximal Products",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		pf.setColorButtonProducts(cp, Color.GREEN);
		String message=cp.size() + " Maximal Products Found:"+System.lineSeparator()+"";
		for (Product p : cp)
			message+= pf.indexOf(p)+" : "+System.lineSeparator()+""+p.toString()+""+System.lineSeparator()+"";

		message += "Elapsed time : "+elapsedTime+ " milliseconds";

		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message);
		textArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		JDialog jd = new JDialog(pf);
		jd.add(scrollPane);
		jd.setTitle("Maximal Products");
		jd.setResizable(true);
		jd.setVisible(true);
		jd.setSize(500,500);
		jd.setLocationRelativeTo(null);
	}

}
