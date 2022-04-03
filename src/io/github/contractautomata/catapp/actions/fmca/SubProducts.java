package io.github.contractautomata.catapp.actions.fmca;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mxgraph.util.mxResources;

import io.github.contractautomata.catapp.App;
import io.github.contractautomata.catapp.EditorActions;
import io.github.contractautomata.catapp.ProductFrame;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;

@SuppressWarnings("serial")
public class SubProducts extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
	//	EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		ProductFrame pf= Objects.requireNonNull(editor).getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}
		//aut.printToFile(filename);
		Family f=pf.getFamily();

		String S= JOptionPane.showInputDialog(null,
				"Insert Product id",
				JOptionPane.PLAIN_MESSAGE);
		if (S==null)
			return;

		int pindex=Integer.parseInt(S);
		//Product p=f.getElements()[pindex];
		Product p = pf.getProductAt(pindex);
		//			int[] subind = f.getSubProductsofProduct(pindex);
		//			Product[] subprod = f.subsetOfProductsFromIndex(subind);
		Set<Product> subprod = f.getSubProductsOfProduct(p);
		pf.setColorButtonProducts(subprod, Color.RED);

		StringBuilder message= new StringBuilder(subprod.size() + " Sub-Products of Product " + pindex + System.lineSeparator() + p.toString() + System.lineSeparator());
		for (Product p2 : subprod)
			message.append(pf.indexOf(p2)).append(" : ").append(System.lineSeparator()).append(p2.toString()).append(System.lineSeparator());
		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message.toString());
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
