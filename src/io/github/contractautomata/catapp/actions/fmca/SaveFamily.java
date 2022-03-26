package io.github.contractautomata.catapp.actions.fmca;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;

import io.github.contractautomata.catapp.App;
import io.github.contractautomata.catapp.DefaultFileFilter;
import io.github.contractautomata.catapp.EditorActions;
import io.github.contractautomata.catapp.EditorMenuBar;
import io.github.contractautomata.catapp.ProductFrame;
import io.github.contractautomata.catlib.family.converters.ProdFamilyConverter;

@SuppressWarnings("serial")
public class SaveFamily extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		App editor = (App) EditorActions.getEditor(e);
		
		if (editor != null)
		{
			EditorMenuBar menuBar = (EditorMenuBar) editor.getMenuFrame().getJMenuBar();

			mxGraphComponent graphComponent = editor.getGraphComponent();
			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}
			FileFilter selectedFilter = null;
			DefaultFileFilter prodFilter = new DefaultFileFilter(".prod","Family");

			JFileChooser fc = new JFileChooser(
					(menuBar.lastDir != null)?menuBar.lastDir:(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent():
						System.getProperty("user.dir"));

			// Adds the default file format
			fc.addChoosableFileFilter(prodFilter);

			int rc = fc.showDialog(null, mxResources.get("save"));
			if (rc != JFileChooser.APPROVE_OPTION)
				return;

			menuBar.lastDir = fc.getSelectedFile().getParent();
			String filename = fc.getSelectedFile().getAbsolutePath();
			selectedFilter = fc.getFileFilter();

			if (selectedFilter instanceof DefaultFileFilter)
			{
				String ext = ((DefaultFileFilter) selectedFilter)
						.getExtension();

				if (!filename.toLowerCase().endsWith(ext))
					filename += ext;
			}

			if (new File(filename).exists()
					&& JOptionPane.showConfirmDialog(graphComponent,
							mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
				return;


			try
			{
				new ProdFamilyConverter().exportFamily(filename, pf.getFamily());
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(graphComponent,
						ex.toString(), mxResources.get("error"),
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

}
