package io.github.contractautomata.catapp.actions.fmca;

import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import io.github.contractautomata.catapp.App;
import io.github.contractautomata.catapp.EditorActions;
import io.github.contractautomata.catapp.EditorMenuBar;
import io.github.contractautomata.catapp.ProductFrame;
import io.github.contractautomata.catlib.family.FMCA;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.PartialProductGenerator;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.family.converters.DimacsFamilyConverter;
import io.github.contractautomata.catlib.family.converters.FeatureIDEfamilyConverter;

@SuppressWarnings("serial")
public class ImportProductLine extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);

		if (editor != null)
		{
			EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();

			if (!menuBar.loseChanges.test(editor)) return;

			mxGraph graph = editor.getGraphComponent().getGraph();
			if (graph == null) return;

			ProductFrame pf=editor.getProductFrame();
			if (pf!=null)
			{
				editor.setProductFrame(null);
				pf.dispose();
			}

			JFileChooser fc = new JFileChooser(
					(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent(): System.getProperty("user.dir"));

			// Adds file filter for supported file format
			menuBar.setDefaultFilter(fc,".xml"," Feature Model ", ".dimacs");

			
			int rc = fc.showDialog(null,
					mxResources.get("openFile"));

			if (rc == JFileChooser.APPROVE_OPTION)
			{
				menuBar.lastDir = fc.getSelectedFile().getParent();
				try
				{
					String fileName =fc.getSelectedFile().getAbsolutePath();
				//	System.out.println(fileName);
					Set<Product> prod; 
					if (fileName.endsWith("xml"))
						prod= new FeatureIDEfamilyConverter().importProducts(fileName);
					else {
						int reply=JOptionPane.showOptionDialog(editor.getGraphComponent(), 
								"Do you want to generate all total products?", 
								"Generate products", 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.INFORMATION_MESSAGE, 
								null, 
								new String[]{"Yes", "No"}, 
								"default");
						if (reply==JOptionPane.CLOSED_OPTION)
							return;
						else 
							prod=new DimacsFamilyConverter(reply==JOptionPane.YES_OPTION).importProducts(fileName);
						
					}

					int reply=JOptionPane.showOptionDialog(editor.getGraphComponent(), 
							"Do you want to generate super-products?", 
							"Generate super-products", 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							new String[]{"Yes", "No"}, 
							"default");
					if (reply==JOptionPane.YES_OPTION)
						prod=new PartialProductGenerator().apply(prod);
					else if (reply==JOptionPane.CLOSED_OPTION)
						return;

					reply=JOptionPane.showOptionDialog(editor.getGraphComponent(), 
							"Do you want to pre-process the product line against the automaton?", 
							"Pre-processing against automaton", 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							new String[]{"Yes", "No"}, 
							"default");
					if (reply==JOptionPane.YES_OPTION)
					{
						if (editor.lastaut==null)
						{
							JOptionPane.showMessageDialog(
									editor.getGraphComponent(),
									"No automaton loaded!",
									mxResources.get("error"),
									JOptionPane.ERROR_MESSAGE);
						}
						else
						{
							FMCA fa = new FMCA(editor.lastaut,prod);
							pf= new ProductFrame(fa.getFamily(), editor,editor.lastaut);
							editor.setProductFrame(pf);
						}
					}
					else if (reply==JOptionPane.CLOSED_OPTION)
						return;
					else 	{
						Family fam = new Family(prod); //(fc.getSelectedFile().getPath(),fileName);
						pf= new ProductFrame(fam, editor,null);
						editor.setProductFrame(pf);
					}
				
					Objects.requireNonNull(pf).setExtendedState(JFrame.MAXIMIZED_BOTH);
					//pf.setAlwaysOnTop(true);
					pf.setLocation(editor.getX() + editor.getWidth(), editor.getY());
					pf.setVisible(true);

				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(
							editor.getGraphComponent(),
							ex.toString(),
							mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}		
		}
	}
}
