package io.github.contractautomata.catapp.actions.fmca;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.Instant;
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
public class MaximalProducts extends AbstractAction {

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

		Family fam= pf.getFamily();

		Instant start = Instant.now();
		//int[] pid = fam.getMaximalProducts();
		Set<Product> cp= fam.getMaximalProducts(); //fam.subsetOfProductsFromIndex(pid);
		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
	
		if (cp==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Maximal Products",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		pf.setColorButtonProducts(cp, Color.GREEN);
		StringBuilder message= new StringBuilder(cp.size() + " Maximal Products Found:" + System.lineSeparator() + "");
		for (Product p : cp)
			message.append(pf.indexOf(p)).append(" : ").append(System.lineSeparator()).append(p.toString()).append(System.lineSeparator());

		message.append("Elapsed time : ").append(elapsedTime).append(" milliseconds");

		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message.toString());
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
