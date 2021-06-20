package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.ProductFrame;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import family.FMCA;
import family.Family;
import family.PartialProductGenerator;
import family.Product;
import family.converters.DimacFamilyConverter;
import family.converters.FeatureIDEfamilyConverter;

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
							prod=new DimacFamilyConverter(reply==JOptionPane.YES_OPTION).importProducts(fileName);
						
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
							pf= new ProductFrame(fa.getFamily(), (JPanel)editor,editor.lastaut);
							editor.setProductFrame(pf);
						}
					}
					else if (reply==JOptionPane.CLOSED_OPTION)
						return;
					else 	{
						Family fam = new Family(prod); //(fc.getSelectedFile().getPath(),fileName);
						pf= new ProductFrame(fam, (JPanel)editor,null);
						editor.setProductFrame(pf);
					}
				
					pf.setExtendedState(JFrame.MAXIMIZED_BOTH); 
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
