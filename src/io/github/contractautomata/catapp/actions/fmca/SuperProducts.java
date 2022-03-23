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
import io.github.contractautomataproject.catlib.family.Family;
import io.github.contractautomataproject.catlib.family.Product;

@SuppressWarnings("serial")
public class SuperProducts extends AbstractAction {

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

		Family f=pf.getFamily();

		String S= JOptionPane.showInputDialog(null,
				"Insert Product id",
				JOptionPane.PLAIN_MESSAGE);
		if (S==null)
			return;

		int pindex=Integer.parseInt(S);
		Product p =pf.getProductAt(pindex);

		Set<Product> supind =f.getSuperProductsofProduct(p);

		pf.setColorButtonProducts(supind, Color.RED);

		StringBuilder message= new StringBuilder(supind.size() + " Super-Products of Product " + pindex + System.lineSeparator() + p.toString() + System.lineSeparator());
		for (Product p2 : supind)
			message.append(pf.indexOf(p2)).append(" : ").append(System.lineSeparator()).append(p2.toString()).append(System.lineSeparator());
		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message.toString());
		textArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		JDialog jd = new JDialog(pf);
		jd.add(scrollPane);
		jd.setTitle("Super-Products");
		jd.setResizable(true);
		jd.setSize(500,500);
		jd.setLocationRelativeTo(null);
		jd.setVisible(true);

	}

}
