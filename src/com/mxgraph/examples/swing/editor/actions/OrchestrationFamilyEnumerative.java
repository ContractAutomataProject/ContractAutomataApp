package com.mxgraph.examples.swing.editor.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.editor.ProductFrame;
import com.mxgraph.util.mxResources;

import contractAutomata.MSCA;
import contractAutomata.converters.MxeConverter;
import family.FMCA;
import family.Family;

@SuppressWarnings("serial")
public class OrchestrationFamilyEnumerative extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();
		if (menuBar.checkAut(editor)) return;
		String filename=editor.getCurrentFile().getName();

		ProductFrame pf=editor.getProductFrame();
		if (pf==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		menuBar.lastDir=editor.getCurrentFile().getParent();
		MSCA aut=editor.lastaut;
		//		MSCA backup = aut.clone();
		//			
		//			String absfilename =editor.getCurrentFile().getAbsolutePath();
		//			MSCA aut;
		//			try {
		//				aut = new BasicMxeConverter().importMxe(absfilename);
		//			} catch (ParserConfigurationException|SAXException|IOException e1) {
		//				JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+System.lineSeparator()+errorMsg,mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
		//				return;
		//			}
		Family f=pf.getFamily();

		JOptionPane.showMessageDialog(editor.getGraphComponent(),"Warning : the enumerative computation may require several minutes!","Warning",JOptionPane.WARNING_MESSAGE);

		long start = System.currentTimeMillis();
		//int[][] vpdummy = new int[1][];
		//MSCA controller = f.getMPCofFamilyWithoutPO(aut, vpdummy);
		MSCA controller = new FMCA(aut,f).getOrchestrationOfFamilyEnumerative();
		//int[] vp = vpdummy[0];
		long elapsedTime = System.currentTimeMillis() - start;

		//			File file=null;
		//Product[] vpp=pf.getFamily().subsetOfProductsFromIndex(vp);

		if (controller==null)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"The orchestration is empty"+System.lineSeparator()+" Elapsed time : "+elapsedTime + " milliseconds","Empty",JOptionPane.WARNING_MESSAGE);
			//editor.lastaut=backup;
			return;
		}

		String K="Orc_familyWithoutPO_"+filename;
		File file;
		try {
			new MxeConverter().exportMSCA(menuBar.lastDir+File.separator+K,controller);
			file = new File(menuBar.lastDir+File.separator+K);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(editor.getGraphComponent(),
					"Error in saving the file "+e1.getMessage(),
					"Error",JOptionPane.ERROR_MESSAGE);

			return;			
		}

		String message = "The orchestration has been stored with filename "+menuBar.lastDir+File.separator+K;


		JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.WARNING_MESSAGE);
		editor.lastaut=controller;
		menuBar.loadMorphStore(K,editor,file);
		
	}

}
