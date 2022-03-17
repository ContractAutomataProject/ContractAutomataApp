package io.github.contractautomataproject.catapp.actions.fmca;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mxgraph.util.mxResources;

import io.github.contractautomataproject.catapp.App;
import io.github.contractautomataproject.catapp.EditorActions;
import io.github.contractautomataproject.catapp.EditorMenuBar;
import io.github.contractautomataproject.catapp.ProductFrame;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.family.FMCA;
import io.github.contractautomataproject.catlib.family.Product;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

@SuppressWarnings("serial")
public class ProductsNonemptyOrc extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		if (menuBar.checkAut(editor)) return;

		menuBar.lastDir=editor.getCurrentFile().getParent();
		ModalAutomaton<CALabel> aut=editor.lastaut;

		ProductFrame pf=editor.getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		Instant start = Instant.now();

		Set<Product>  vpp;
		if (!aut.getForwardStar(aut.getInitial()).stream()
				.map(ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>::getLabel)
				.allMatch(l->l.getUnsignedAction().equals("dummy")))
			vpp=new FMCA(aut,pf.getFamily()).productsWithNonEmptyOrchestration();
		else
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"Operation not supported for an orchestration of a family","",JOptionPane.WARNING_MESSAGE);
			return;
		//				vpp=new FMCA(aut,pf.getFamily()).productsWithNonEmptyOrchestrationFamily();
		}
		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
	
		if (vpp==null)
		{			
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Products With non-empty orchestration"+ System.lineSeparator()+"Elapsed time : "+elapsedTime+ " milliseconds","",JOptionPane.WARNING_MESSAGE);
			return;
		}

		pf.setColorButtonProducts(vpp, Color.BLUE);
		String message=vpp.size( )+ " Products With non-empty orchestration Found:"+System.lineSeparator();
		for (Product p : vpp)
			message+= pf.indexOf(p)+" : "+System.lineSeparator()+p.toString()+System.lineSeparator();

		message += "Elapsed time : " + elapsedTime+ " milliseconds";
		JTextArea textArea = new JTextArea(200,200);
		textArea.setText(message);
		textArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		JDialog jd = new JDialog(pf);
		jd.add(scrollPane);
		jd.setTitle("Products With non-empty orchestration");
		jd.setResizable(true);
		jd.setVisible(true);

		jd.setSize(500,500);
		jd.setLocationRelativeTo(null);
		// JOptionPane.showMessageDialog(editor.getGraphComponent(), jd);
		//JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Valid Products",JOptionPane.PLAIN_MESSAGE);

		
	}

}
