package io.github.davidebasile.catapp.actions.fmca;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mxgraph.util.mxResources;

import io.github.davidebasile.catapp.App;
import io.github.davidebasile.catapp.EditorActions;
import io.github.davidebasile.catapp.ProductFrame;
import io.github.davidebasile.contractautomata.family.Family;
import io.github.davidebasile.contractautomata.family.Product;

@SuppressWarnings("serial")
public class SubProducts extends AbstractAction {

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
		//aut.printToFile(filename);
		Family f=pf.getFamily();

		String S= (String) JOptionPane.showInputDialog(null, 
				"Insert Product id",
				JOptionPane.PLAIN_MESSAGE);
		if (S==null)
			return;

		int pindex=Integer.parseInt(S);
		//Product p=f.getElements()[pindex];
		Product p = pf.getProductAt(pindex);
		//			int[] subind = f.getSubProductsofProduct(pindex);
		//			Product[] subprod = f.subsetOfProductsFromIndex(subind);
		Set<Product> subprod = f.getSubProductsofProduct(p);
		pf.setColorButtonProducts(subprod, Color.RED);

		String message=subprod.size() + " Sub-Products of Product "+pindex+System.lineSeparator()+p.toString()+System.lineSeparator();
		for (Product p2 : subprod)
			message+= pf.indexOf(p2)+" : "+System.lineSeparator()+p2.toString()+System.lineSeparator();
		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message);
		textArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		JDialog jd = new JDialog(pf);
		jd.add(scrollPane);
		jd.setTitle("Sub-Products");
		jd.setResizable(true);
		jd.setVisible(true);

		jd.setSize(500,500);
		jd.setLocationRelativeTo(null);

	}

}
