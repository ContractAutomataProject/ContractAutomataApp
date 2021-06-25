package com.mxgraph.examples.swing.editor.actions;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import org.w3c.dom.Document;

import com.mxgraph.examples.swing.editor.App;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.DefaultFileFilter;
import com.mxgraph.examples.swing.editor.EditorActions.ExitAction;
import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.examples.swing.editor.EditorActions.NewAction;
import com.mxgraph.examples.swing.editor.EditorActions.OpenAction;
import com.mxgraph.examples.swing.editor.EditorActions.PageSetupAction;
import com.mxgraph.examples.swing.editor.EditorActions.PrintAction;
import com.mxgraph.examples.swing.editor.EditorActions.SaveAction;
import com.mxgraph.examples.swing.editor.EditorActions.ScaleAction;
import com.mxgraph.examples.swing.editor.ProductFrame;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

/**
 * 
 * This is the Menu of the application
 * adapted the MenuBar of BasicGraphEditor with functionalities of FMCA
 * 
 * @author Davide Basile
 *
 */
public class EditorMenuBar extends JMenuBar 
{
	String lastDir;

	Predicate<App> loseChanges = x->((x != null)&&(!x.isModified()
			|| JOptionPane.showConfirmDialog(x,	mxResources.get("loseChanges")) == JOptionPane.YES_OPTION));

	final String errorMsg = "States or transitions contain syntax errors."+System.lineSeparator()+" "
			+ 		"Please, check that each state has the following format:"+System.lineSeparator()
			+		"[STRING, ..., STRING]"+System.lineSeparator() 
			+		"and that each transition label has the following format:"+System.lineSeparator()
			+		"[(TYPE)STRING, ...,(TYPE)STRING]"+System.lineSeparator()+" where (TYPE) is either ! or ?";

	
	public String getErrorMsg() {
		return errorMsg;
	}


	private static final long serialVersionUID = 4060203894740766714L;

	/**
	 * @param editor
	 */
	public EditorMenuBar(final App editor)
	{
		final mxGraph graphfinal = editor.getGraphComponent().getGraph();

		graphfinal.setDisconnectOnMove(false);
		graphfinal.setEdgeLabelsMovable(false);
		//graph.setAllowDanglingEdges(false);
		graphfinal.setAllowLoops(true);
		graphfinal.setCellsResizable(false);
		graphfinal.setCellStyles("width","50.0");
		graphfinal.setCellStyles("heigth","50.0");


		// Creates the file menu
		JMenu menu = add(new JMenu(mxResources.get("file")));
		JMenuItem item;

		EditorMenuBar menuBar = this;

		menu.add(editor.bind(mxResources.get("new"), new NewAction(), "/com/mxgraph/examples/swing/images/new.gif"));
		menu.add(editor.bind(mxResources.get("openFile"), new OpenAction(), "/com/mxgraph/examples/swing/images/open.gif"));
		//menu.add(editor.bind(mxResources.get("importStencil"), new ImportAction(), "/com/mxgraph/examples/swing/images/open.gif"));
		
		item = menu.add("Open New Window");
		item.addActionListener(e->{});
		item.addActionListener(e->
		{
			try {
				new ProcessBuilder("java", "-jar", "App.jar").start();
				JOptionPane.showMessageDialog(
						editor.getGraphComponent(),
						"A new App process is starting",
						mxResources.get("ok"),
						JOptionPane.PLAIN_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(
						editor.getGraphComponent(),
						e1.toString(),
						mxResources.get("error"),
						JOptionPane.ERROR_MESSAGE);
			}
		});
		
		menu.addSeparator();


		//menu.add(editor.bind("Import Automaton", new ImportAction(), "/com/mxgraph/examples/swing/images/save.gif"));
		menu.add(editor.bind(mxResources.get("save"), new SaveAction(false), "/com/mxgraph/examples/swing/images/save.gif"));
		menu.add(editor.bind(mxResources.get("saveAs"), new SaveAction(true), "/com/mxgraph/examples/swing/images/saveas.gif"));


		menu.addSeparator();
		
		menu.add(editor.bind("Import .data", new ImportData(),"/com/mxgraph/examples/swing/images/import.gif"));//mxResources.get("aboutGraphEditor")));
		menu.add(editor.bind("Export .data", new ExportData(),"/com/mxgraph/examples/swing/images/export.gif"));//mxResources.get("aboutGraphEditor")));

		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("pageSetup"), new PageSetupAction(), "/com/mxgraph/examples/swing/images/pagesetup.gif"));
		menu.add(editor.bind(mxResources.get("print"), new PrintAction(), "/com/mxgraph/examples/swing/images/print.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("exit"), new ExitAction()));

		// Creates the edit menu
		menu = add(new JMenu(mxResources.get("edit")));

		menu.add(editor.bind(mxResources.get("undo"), new HistoryAction(true), "/com/mxgraph/examples/swing/images/undo.gif"));
		menu.add(editor.bind(mxResources.get("redo"), new HistoryAction(false), "/com/mxgraph/examples/swing/images/redo.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("cut"), TransferHandler.getCutAction(), "/com/mxgraph/examples/swing/images/cut.gif"));
		menu.add(editor.bind(mxResources.get("copy"), TransferHandler.getCopyAction(), "/com/mxgraph/examples/swing/images/copy.gif"));
		menu.add(editor.bind(mxResources.get("paste"), TransferHandler.getPasteAction(), "/com/mxgraph/examples/swing/images/paste.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("delete"), mxGraphActions.getDeleteAction(), "/com/mxgraph/examples/swing/images/delete.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("selectAll"), mxGraphActions.getSelectAllAction()));
		menu.add(editor.bind(mxResources.get("selectNone"), mxGraphActions.getSelectNoneAction()));

		menu.addSeparator();

		menu.add(editor.bind("Add handles to edges", new AddHandlesToEdges(),"/com/mxgraph/examples/swing/images/straight.gif"));//mxResources.get("aboutGraphEditor")));

		//menu.add(editor.bind(mxResources.get("warning"), new WarningAction()));
		//menu.add(editor.bind(mxResources.get("edit"), mxGraphActions.getEditAction()));

		// Creates the view menu
		menu = add(new JMenu(mxResources.get("view")));


		JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("zoom")));

		submenu.add(editor.bind("400%", new ScaleAction(4)));
		submenu.add(editor.bind("200%", new ScaleAction(2)));
		submenu.add(editor.bind("150%", new ScaleAction(1.5)));
		submenu.add(editor.bind("100%", new ScaleAction(1)));
		submenu.add(editor.bind("75%", new ScaleAction(0.75)));
		submenu.add(editor.bind("50%", new ScaleAction(0.5)));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("custom"), new ScaleAction(0)));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("zoomIn"), mxGraphActions.getZoomInAction()));
		
		menu.add(editor.bind(mxResources.get("zoomOut"), mxGraphActions.getZoomOutAction()));

		menu = add(new JMenu("MSCA"));

		menu.add(editor.bind("Composition", new Composition()));//"/com/mxgraph/examples/swing/images/straight.gif"));
		
		menu.add(editor.bind("Most Permissive Controller", new MostPermissiveController()));
	
		menu.add(editor.bind("Orchestration", new Orchestration()));
		
		menu.add(editor.bind("Choreography", new Choreography()));


		menu = add(new JMenu("FMCA"));

		item = menu.add(new JMenuItem("Close Family"));
		item.addActionListener(e->
		{
			if (!menuBar.loseChanges.test(editor)) return; 

			mxGraph graph = editor.getGraphComponent().getGraph();
			if (graph==null) return;

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
				return;

			editor.setProductFrame(null);
			pf.dispose();
		});

		item = menu.add(new JMenuItem("Clear Colours of Family"));
		item.addActionListener(e->
		{
			if (!menuBar.loseChanges.test(editor)) return;
			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
				return;
			pf.resetColorButtonProducts();
		});
		
		
		menu.add(editor.bind("Load Family", new LoadFamily()));

		menu.add(editor.bind("Save Family", new SaveFamily()));

		menu.add(editor.bind("Import Product Line (.xml,.dimacs)", new ImportProductLine()));

		menu.addSeparator();

		menu.add(editor.bind("Maximal Products", new MaximalProducts()));

		menu.add(editor.bind("Sub-Products of Product", new SubProducts()));

		menu.add(editor.bind("Super-Products of Product", new SuperProducts()));

		menu.addSeparator();

		menu.add(editor.bind("Products Respecting Validity", new ProductsRespectingValidity()));

		menu.add(editor.bind("Canonical Products", new CanonicalProducts()));

		menu.add(editor.bind("Products with Non-empty Orchestration", new ProductsNonemptyOrc()));

		menu.add(editor.bind("Total Products with Non-empty Orchestration", new TotalProductsNonemptyOrc()));

		menu.add(editor.bind("Orchestration of Family", new OrchestrationFamily()));

		menu.add(editor.bind("Orchestration of Family (enumerative)", new OrchestrationFamilyEnumerative()));

		menu.add(editor.bind("Orchestration of a Product (type id)", new OrchestrationProductId()));

		menu.add(editor.bind("Orchestration of a Product (type product)", new OrchestrationProductType()));
	
		menu = add(new JMenu("TSCA"));


		// Creates the help menu
		menu = add(new JMenu(mxResources.get("help")));

		item = menu.add(new JMenuItem("about Contract Automata Tool"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e-> editor.about());
	}

	void loadMorphStore(String name, BasicGraphEditor editor, File file)
	{
		if (!name.endsWith(".mxe"))//&&!name.endsWith(".data"))
			name=name+".mxe";
		if (lastDir!=null && !name.startsWith(lastDir))
			name=lastDir+File.separator+name;
		
		try
		{	
			mxGraph graph = editor.getGraphComponent().getGraph();

			//TODO 
			//MSCAIO methods are used to convert and parse an MSCA, to update the GUI
			// I store, load, morph and store the file again, there should be a better method
			// I do this way because I use Document to update the window
			Document document = mxXmlUtils
					.parseXml(mxUtils.readFile(name));									

			mxGraphComponent mgc = new mxGraphComponent(new mxGraph((mxGraphModel) 
					new mxCodec(document).decode(
							document.getDocumentElement(),
							graph.getModel())));

			App.morphGraph(mgc.getGraph(), mgc);

			String xml = mxXmlUtils.getXml(new mxCodec().encode(mgc.getGraph().getModel()));
			mxUtils.writeFile(xml, name);


			parseAndSet(name, editor, file);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					editor.getGraphComponent(),
					ex.toString(),
					mxResources.get("error"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	void parseAndSet(String absfilename, BasicGraphEditor editor, File file)
	{
		//TODO there should be no need in parsing the xml and then converting to xml anymore
		try
		{								
			mxGraph graphfinal = editor.getGraphComponent().getGraph();
			Document document = mxXmlUtils
					.parseXml(mxUtils.readFile(absfilename));//menuBar.lastDir+File.separator+name));
			mxCodec codec = new mxCodec(document);
			codec.decode(
					document.getDocumentElement(),
					graphfinal.getModel());
			editor.setCurrentFile(file);
			editor.setModified(false);
			editor.getUndoManager().clear();
			editor.getGraphComponent().zoomAndCenter();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					editor.getGraphComponent(),
					ex.toString(),
					mxResources.get("error"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	boolean checkAut(App editor)
	{
		try
		{
			editor.getCurrentFile().getName();
			return false;

		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No automaton loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return true;
		}
	}

	DefaultFileFilter setDefaultFilter(JFileChooser fc,String type, String title, String type2) {
		DefaultFileFilter defaultFilter = new DefaultFileFilter(type, "")//mxResources.get("allSupportedFormats")
				//+ " (.mxe, .png, .vdx)")
				{
			public boolean accept(File file)
			{
				String lcase = file.getName().toLowerCase();
				if (type2==null)
					return super.accept(file)
							|| lcase.endsWith(type);
				else
					return super.accept(file)
							|| lcase.endsWith(type2);
			}
				};
				fc.addChoosableFileFilter(defaultFilter);
				fc.addChoosableFileFilter(new DefaultFileFilter(type,
						title+" "+  mxResources.get("file")
						+ " ("+type+")"));

				fc.setFileFilter(defaultFilter);
				return defaultFilter;
	}

}

//END OF CLASS










//		menu.addSeparator();
//
//		item = menu.add(new JMenuItem("Info about converting in MSCA without Lazy Transitions"));//mxResources.get("aboutGraphEditor")));
//		item.addActionListener(e->
//		{
//			if (menuBar.checkAut(editor)) return;
//
//			menuBar.lastDir=editor.getCurrentFile().getParent();
//			MSCA aut=editor.lastaut;
//
//			long l=aut.getTransition()
//					.parallelStream()
//					.filter(MSCATransition::isLazy)
//					.count();
//
//			long ns = aut.getNumStates()+1;
//
//			JOptionPane.showMessageDialog(editor.getGraphComponent(), 
//					"The automaton contains the following number of lazy transitions : "+l+" "+System.lineSeparator()
//							+"An encoding into an automaton with only urgent transitions in the worst case could have the following "
//							+ "number of states ("+ns+") * (2^"+l+"-1)",
//							"Result",JOptionPane.WARNING_MESSAGE);
//		});



//		item = menu.add(new JMenuItem("Valid Products (Only)"));//mxResources.get("aboutGraphEditor")));
//		item.addActionListener(e->
//		{
//
//			if (menuBar.checkAut(editor)) return;
//
//			ProductFrame pf=editor.getProductFrame();
//
//			if (pf==null)
//			{
//				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
//				return;
//			}
//
//			menuBar.lastDir=editor.getCurrentFile().getParent();
//
//			MSCA aut=editor.lastaut;
//
//			//int[] vp= pf.getFamily().validProductsNew(aut);
//			Set<Product> vpp=pf.getFamily().validProductsNew(aut); //subsetOfProductsFromIndex(vp);
//			
//			if (vpp==null || vpp.size()==0)
//			{
//				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No valid products!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
//				return;
//			}
//			pf=editor.getProductFrame();
//			if (pf!=null)
//				pf.dispose();
//
//			pf= new ProductFrame(new Family(vpp), (JPanel)editor);
//			editor.setProductFrame(pf);  
//		});


//		item = menu.add(new JMenuItem("Products with non-empty orchestration (Only)"));//mxResources.get("aboutGraphEditor")));
//		item.addActionListener(e->
//		{		
//
//			if (menuBar.checkAut(editor)) return;
//
//			ProductFrame pf=editor.getProductFrame();
//			if (pf==null)
//			{
//				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
//				return;
//			}
//
//			menuBar.lastDir=editor.getCurrentFile().getParent();
//
//			MSCA aut=editor.lastaut;
//
////			int[] vp= pf.getFamily().productsWithNonEmptyMPC(aut);
////			Product[] vpp=pf.getFamily().subsetOfProductsFromIndex(vp);
//			
//			Set<Product> vpp = pf.getFamily().productsWithNonEmptyOrchestrationNew(aut);
//			pf=editor.getProductFrame();
//			if (pf!=null)
//			{
//				editor.setProductFrame(null);
//				pf.dispose();
//			}
//
//			pf= new ProductFrame(new Family(vpp), (JPanel)editor);
//			editor.setProductFrame(pf);	
//		});







//
///**
// *
// */
//public static class InsertGraph extends AbstractAction
//{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 4010463992665008365L;
//
//	/**
//	 * 
//	 */
//	protected GraphType graphType;
//
//	protected mxAnalysisGraph aGraph;
//
//	/**
//	 * @param aGraph 
//	 * 
//	 */
//	public InsertGraph(GraphType tree, mxAnalysisGraph aGraph)
//	{
//		this.graphType = tree;
//		this.aGraph = aGraph;
//	}
//
//	/**
//	 * 
//	 */
//	public void actionPerformed(ActionEvent e)
//	{
//		if (e.getSource() instanceof mxGraphComponent)
//		{
//			mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
//			mxGraph graph = graphComponent.getGraph();
//
//			// dialog = new FactoryConfigDialog();
//			String dialogText = "";
//			if (graphType == GraphType.NULL)
//				dialogText = "Configure null graph";
//			else if (graphType == GraphType.COMPLETE)
//				dialogText = "Configure complete graph";
//			else if (graphType == GraphType.NREGULAR)
//				dialogText = "Configure n-regular graph";
//			else if (graphType == GraphType.GRID)
//				dialogText = "Configure grid graph";
//			else if (graphType == GraphType.BIPARTITE)
//				dialogText = "Configure bipartite graph";
//			else if (graphType == GraphType.COMPLETE_BIPARTITE)
//				dialogText = "Configure complete bipartite graph";
//			else if (graphType == GraphType.BFS_DIR)
//				dialogText = "Configure BFS algorithm";
//			else if (graphType == GraphType.BFS_UNDIR)
//				dialogText = "Configure BFS algorithm";
//			else if (graphType == GraphType.DFS_DIR)
//				dialogText = "Configure DFS algorithm";
//			else if (graphType == GraphType.DFS_UNDIR)
//				dialogText = "Configure DFS algorithm";
//			else if (graphType == GraphType.DIJKSTRA)
//				dialogText = "Configure Dijkstra's algorithm";
//			else if (graphType == GraphType.BELLMAN_FORD)
//				dialogText = "Configure Bellman-Ford algorithm";
//			else if (graphType == GraphType.MAKE_TREE_DIRECTED)
//				dialogText = "Configure make tree directed algorithm";
//			else if (graphType == GraphType.KNIGHT_TOUR)
//				dialogText = "Configure knight's tour";
//			else if (graphType == GraphType.GET_ADJ_MATRIX)
//				dialogText = "Configure adjacency matrix";
//			else if (graphType == GraphType.FROM_ADJ_MATRIX)
//				dialogText = "Input adjacency matrix";
//			else if (graphType == GraphType.PETERSEN)
//				dialogText = "Configure Petersen graph";
//			else if (graphType == GraphType.WHEEL)
//				dialogText = "Configure Wheel graph";
//			else if (graphType == GraphType.STAR)
//				dialogText = "Configure Star graph";
//			else if (graphType == GraphType.PATH)
//				dialogText = "Configure Path graph";
//			else if (graphType == GraphType.FRIENDSHIP_WINDMILL)
//				dialogText = "Configure Friendship Windmill graph";
//			else if (graphType == GraphType.INDEGREE)
//				dialogText = "Configure indegree analysis";
//			else if (graphType == GraphType.OUTDEGREE)
//				dialogText = "Configure outdegree analysis";
//			GraphConfigDialog dialog = new GraphConfigDialog(graphType, dialogText);
//			dialog.configureLayout(graph, graphType, aGraph);
//			dialog.setModal(true);
//			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//			Dimension frameSize = dialog.getSize();
//			dialog.setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
//			dialog.setVisible(true);
//		}
//	}
//}



// Creates a developer menu
/*menu = add(new JMenu("Generate"));
menu.add(editor.bind("Null Graph", new InsertGraph(GraphType.NULL, aGraph)));
menu.add(editor.bind("Complete Graph", new InsertGraph(GraphType.COMPLETE, aGraph)));
menu.add(editor.bind("Grid", new InsertGraph(GraphType.GRID, aGraph)));
menu.add(editor.bind("Bipartite", new InsertGraph(GraphType.BIPARTITE, aGraph)));
menu.add(editor.bind("Complete Bipartite", new InsertGraph(GraphType.COMPLETE_BIPARTITE, aGraph)));
menu.add(editor.bind("Knight's Graph", new InsertGraph(GraphType.KNIGHT, aGraph)));
menu.add(editor.bind("King's Graph", new InsertGraph(GraphType.KING, aGraph)));
menu.add(editor.bind("Petersen", new InsertGraph(GraphType.PETERSEN, aGraph)));
menu.add(editor.bind("Path", new InsertGraph(GraphType.PATH, aGraph)));
menu.add(editor.bind("Star", new InsertGraph(GraphType.STAR, aGraph)));
menu.add(editor.bind("Wheel", new InsertGraph(GraphType.WHEEL, aGraph)));
menu.add(editor.bind("Friendship Windmill", new InsertGraph(GraphType.FRIENDSHIP_WINDMILL, aGraph)));
menu.add(editor.bind("Full Windmill", new InsertGraph(GraphType.FULL_WINDMILL, aGraph)));
menu.add(editor.bind("Knight's Tour", new InsertGraph(GraphType.KNIGHT_TOUR, aGraph)));
menu.addSeparator();
menu.add(editor.bind("Simple Random", new InsertGraph(GraphType.SIMPLE_RANDOM, aGraph)));
menu.add(editor.bind("Simple Random Tree", new InsertGraph(GraphType.SIMPLE_RANDOM_TREE, aGraph)));
menu.addSeparator();
menu.add(editor.bind("Reset Style", new InsertGraph(GraphType.RESET_STYLE, aGraph)));

menu = add(new JMenu("Analyze"));
menu.add(editor.bind("Is Connected", new AnalyzeGraph(AnalyzeType.IS_CONNECTED, aGraph)));
menu.add(editor.bind("Is Simple", new AnalyzeGraph(AnalyzeType.IS_SIMPLE, aGraph)));
menu.add(editor.bind("Is Directed Cyclic", new AnalyzeGraph(AnalyzeType.IS_CYCLIC_DIRECTED, aGraph)));
menu.add(editor.bind("Is Undirected Cyclic", new AnalyzeGraph(AnalyzeType.IS_CYCLIC_UNDIRECTED, aGraph)));
menu.add(editor.bind("BFS Directed", new InsertGraph(GraphType.BFS_DIR, aGraph)));
menu.add(editor.bind("BFS Undirected", new InsertGraph(GraphType.BFS_UNDIR, aGraph)));
menu.add(editor.bind("DFS Directed", new InsertGraph(GraphType.DFS_DIR, aGraph)));
menu.add(editor.bind("DFS Undirected", new InsertGraph(GraphType.DFS_UNDIR, aGraph)));
menu.add(editor.bind("Complementary", new AnalyzeGraph(AnalyzeType.COMPLEMENTARY, aGraph)));
menu.add(editor.bind("Regularity", new AnalyzeGraph(AnalyzeType.REGULARITY, aGraph)));
menu.add(editor.bind("Dijkstra", new InsertGraph(GraphType.DIJKSTRA, aGraph)));
menu.add(editor.bind("Bellman-Ford", new InsertGraph(GraphType.BELLMAN_FORD, aGraph)));
menu.add(editor.bind("Floyd-Roy-Warshall", new AnalyzeGraph(AnalyzeType.FLOYD_ROY_WARSHALL, aGraph)));
menu.add(editor.bind("Get Components", new AnalyzeGraph(AnalyzeType.COMPONENTS, aGraph)));
menu.add(editor.bind("Make Connected", new AnalyzeGraph(AnalyzeType.MAKE_CONNECTED, aGraph)));
menu.add(editor.bind("Make Simple", new AnalyzeGraph(AnalyzeType.MAKE_SIMPLE, aGraph)));
menu.add(editor.bind("Is Tree", new AnalyzeGraph(AnalyzeType.IS_TREE, aGraph)));
menu.add(editor.bind("One Spanning Tree", new AnalyzeGraph(AnalyzeType.ONE_SPANNING_TREE, aGraph)));
menu.add(editor.bind("Make tree directed", new InsertGraph(GraphType.MAKE_TREE_DIRECTED, aGraph)));
menu.add(editor.bind("Is directed", new AnalyzeGraph(AnalyzeType.IS_DIRECTED, aGraph)));
menu.add(editor.bind("Indegree", new InsertGraph(GraphType.INDEGREE, aGraph)));
menu.add(editor.bind("Outdegree", new InsertGraph(GraphType.OUTDEGREE, aGraph)));
menu.add(editor.bind("Is cut vertex", new InsertGraph(GraphType.IS_CUT_VERTEX, aGraph)));
menu.add(editor.bind("Get cut vertices", new AnalyzeGraph(AnalyzeType.GET_CUT_VERTEXES, aGraph)));
menu.add(editor.bind("Get cut edges", new AnalyzeGraph(AnalyzeType.GET_CUT_EDGES, aGraph)));
menu.add(editor.bind("Get sources", new AnalyzeGraph(AnalyzeType.GET_SOURCES, aGraph)));
menu.add(editor.bind("Get sinks", new AnalyzeGraph(AnalyzeType.GET_SINKS, aGraph)));
menu.add(editor.bind("Is biconnected", new AnalyzeGraph(AnalyzeType.IS_BICONNECTED, aGraph)));
 */

/**
 *
 */
//	public static class AnalyzeGraph extends AbstractAction
//	{
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 6926170745240507985L;
//
//		mxAnalysisGraph aGraph;
//
//		/**
//		 * 
//		 */
//		protected AnalyzeType analyzeType;
//
//		/**
//		 * Examples for calling analysis methods from mxGraphStructure 
//		 */
//		public AnalyzeGraph(AnalyzeType analyzeType, mxAnalysisGraph aGraph)
//		{
//			this.analyzeType = analyzeType;
//			this.aGraph = aGraph;
//		}
//
//		public void actionPerformed(ActionEvent e)
//		{
//			if (e.getSource() instanceof mxGraphComponent)
//			{
//				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
//				mxGraph graph = graphComponent.getGraph();
//				aGraph.setGraph(graph);
//
//				if (analyzeType == AnalyzeType.IS_CONNECTED)
//				{
//					boolean isConnected = mxGraphStructure.isConnected(aGraph);
//
//					if (isConnected)
//					{
//						System.out.println("The graph is connected");
//					}
//					else
//					{
//						System.out.println("The graph is not connected");
//					}
//				}
//				else if (analyzeType == AnalyzeType.IS_SIMPLE)
//				{
//					boolean isSimple = mxGraphStructure.isSimple(aGraph);
//
//					if (isSimple)
//					{
//						System.out.println("The graph is simple");
//					}
//					else
//					{
//						System.out.println("The graph is not simple");
//					}
//				}
//				else if (analyzeType == AnalyzeType.IS_CYCLIC_DIRECTED)
//				{
//					boolean isCyclicDirected = mxGraphStructure.isCyclicDirected(aGraph);
//
//					if (isCyclicDirected)
//					{
//						System.out.println("The graph is cyclic directed");
//					}
//					else
//					{
//						System.out.println("The graph is acyclic directed");
//					}
//				}
//				else if (analyzeType == AnalyzeType.IS_CYCLIC_UNDIRECTED)
//				{
//					boolean isCyclicUndirected = mxGraphStructure.isCyclicUndirected(aGraph);
//
//					if (isCyclicUndirected)
//					{
//						System.out.println("The graph is cyclic undirected");
//					}
//					else
//					{
//						System.out.println("The graph is acyclic undirected");
//					}
//				}
//				else if (analyzeType == AnalyzeType.COMPLEMENTARY)
//				{
//					graph.getModel().beginUpdate();
//
//					mxGraphStructure.complementaryGraph(aGraph);
//
//					mxGraphStructure.setDefaultGraphStyle(aGraph, true);
//					graph.getModel().endUpdate();
//				}
//				else if (analyzeType == AnalyzeType.REGULARITY)
//				{
//					try
//					{
//						int regularity = mxGraphStructure.regularity(aGraph);
//						System.out.println("Graph regularity is: " + regularity);
//					}
//					catch (StructuralException e1)
//					{
//						System.out.println("The graph is irregular");
//					}
//				}
//				else if (analyzeType == AnalyzeType.COMPONENTS)
//				{
//					Object[][] components = mxGraphStructure.getGraphComponents(aGraph);
//					mxIGraphModel model = aGraph.getGraph().getModel();
//
//					for (int i = 0; i < components.length; i++)
//					{
//						System.out.print("Component " + i + " :");
//
//						for (int j = 0; j < components[i].length; j++)
//						{
//							System.out.print(" " + model.getValue(components[i][j]));
//						}
//
//						System.out.println(".");
//					}
//
//					System.out.println("Number of components: " + components.length);
//
//				}
//				else if (analyzeType == AnalyzeType.MAKE_CONNECTED)
//				{
//					graph.getModel().beginUpdate();
//
//					if (!mxGraphStructure.isConnected(aGraph))
//					{
//						mxGraphStructure.makeConnected(aGraph);
//						mxGraphStructure.setDefaultGraphStyle(aGraph, false);
//					}
//
//					graph.getModel().endUpdate();
//				}
//				else if (analyzeType == AnalyzeType.MAKE_SIMPLE)
//				{
//					mxGraphStructure.makeSimple(aGraph);
//				}
//				else if (analyzeType == AnalyzeType.IS_TREE)
//				{
//					boolean isTree = mxGraphStructure.isTree(aGraph);
//
//					if (isTree)
//					{
//						System.out.println("The graph is a tree");
//					}
//					else
//					{
//						System.out.println("The graph is not a tree");
//					}
//				}
//				else if (analyzeType == AnalyzeType.ONE_SPANNING_TREE)
//				{
//					try
//					{
//						graph.getModel().beginUpdate();
//						aGraph.getGenerator().oneSpanningTree(aGraph, true, true);
//						mxGraphStructure.setDefaultGraphStyle(aGraph, false);
//						graph.getModel().endUpdate();
//					}
//					catch (StructuralException e1)
//					{
//						System.out.println("The graph must be simple and connected");
//					}
//				}
//				else if (analyzeType == AnalyzeType.IS_DIRECTED)
//				{
//					boolean isDirected = mxGraphProperties.isDirected(aGraph.getProperties(), mxGraphProperties.DEFAULT_DIRECTED);
//
//					if (isDirected)
//					{
//						System.out.println("The graph is directed.");
//					}
//					else
//					{
//						System.out.println("The graph is undirected.");
//					}
//				}
//				else if (analyzeType == AnalyzeType.GET_CUT_VERTEXES)
//				{
//					Object[] cutVertices = mxGraphStructure.getCutVertices(aGraph);
//
//					System.out.print("Cut vertices of the graph are: [");
//					mxIGraphModel model = aGraph.getGraph().getModel();
//
//					for (int i = 0; i < cutVertices.length; i++)
//					{
//						System.out.print(" " + model.getValue(cutVertices[i]));
//					}
//
//					System.out.println(" ]");
//				}
//				else if (analyzeType == AnalyzeType.GET_CUT_EDGES)
//				{
//					Object[] cutEdges = mxGraphStructure.getCutEdges(aGraph);
//
//					System.out.print("Cut edges of the graph are: [");
//					mxIGraphModel model = aGraph.getGraph().getModel();
//
//					for (int i = 0; i < cutEdges.length; i++)
//					{
//						System.out.print(" " + Integer.parseInt((String) model.getValue(aGraph.getTerminal(cutEdges[i], true))) + "-"
//								+ Integer.parseInt((String) model.getValue(aGraph.getTerminal(cutEdges[i], false))));
//					}
//
//					System.out.println(" ]");
//				}
//				else if (analyzeType == AnalyzeType.GET_SOURCES)
//				{
//					try
//					{
//						Object[] sourceVertices = mxGraphStructure.getSourceVertices(aGraph);
//						System.out.print("Source vertices of the graph are: [");
//						mxIGraphModel model = aGraph.getGraph().getModel();
//
//						for (int i = 0; i < sourceVertices.length; i++)
//						{
//							System.out.print(" " + model.getValue(sourceVertices[i]));
//						}
//
//						System.out.println(" ]");
//					}
//					catch (StructuralException e1)
//					{
//						System.out.println(e1);
//					}
//				}
//				else if (analyzeType == AnalyzeType.GET_SINKS)
//				{
//					try
//					{
//						Object[] sinkVertices = mxGraphStructure.getSinkVertices(aGraph);
//						System.out.print("Sink vertices of the graph are: [");
//						mxIGraphModel model = aGraph.getGraph().getModel();
//
//						for (int i = 0; i < sinkVertices.length; i++)
//						{
//							System.out.print(" " + model.getValue(sinkVertices[i]));
//						}
//
//						System.out.println(" ]");
//					}
//					catch (StructuralException e1)
//					{
//						System.out.println(e1);
//					}
//				}
//				else if (analyzeType == AnalyzeType.PLANARITY)
//				{
//					// implement
//				}
//				else if (analyzeType == AnalyzeType.IS_BICONNECTED)
//				{
//					boolean isBiconnected = mxGraphStructure.isBiconnected(aGraph);
//
//					if (isBiconnected)
//					{
//						System.out.println("The graph is biconnected.");
//					}
//					else
//					{
//						System.out.println("The graph is not biconnected.");
//					}
//				}
//				else if (analyzeType == AnalyzeType.GET_BICONNECTED)
//				{
//					// implement
//				}
//				else if (analyzeType == AnalyzeType.SPANNING_TREE)
//				{
//					// implement
//				}
//				else if (analyzeType == AnalyzeType.FLOYD_ROY_WARSHALL)
//				{
//
//					ArrayList<Object[][]> FWIresult = new ArrayList<Object[][]>();
//					try
//					{
//						//only this line is needed to get the result from Floyd-Roy-Warshall, the rest is code for displaying the result
//						FWIresult = mxTraversal.floydRoyWarshall(aGraph);
//
//						Object[][] dist = FWIresult.get(0);
//						Object[][] paths = FWIresult.get(1);
//						Object[] vertices = aGraph.getChildVertices(aGraph.getGraph().getDefaultParent());
//						int vertexNum = vertices.length;
//						System.out.println("Distances are:");
//
//						for (int i = 0; i < vertexNum; i++)
//						{
//							System.out.print("[");
//
//							for (int j = 0; j < vertexNum; j++)
//							{
//								System.out.print(" " + Math.round((Double) dist[i][j] * 100.0) / 100.0);
//							}
//
//							System.out.println("] ");
//						}
//
//						System.out.println("Path info:");
//
//						mxCostFunction costFunction = aGraph.getGenerator().getCostFunction();
//						mxGraphView view = aGraph.getGraph().getView();
//
//						for (int i = 0; i < vertexNum; i++)
//						{
//							System.out.print("[");
//
//							for (int j = 0; j < vertexNum; j++)
//							{
//								if (paths[i][j] != null)
//								{
//									System.out.print(" " + costFunction.getCost(view.getState(paths[i][j])));
//								}
//								else
//								{
//									System.out.print(" -");
//								}
//							}
//
//							System.out.println(" ]");
//						}
//
//						try
//						{
//							Object[] path = mxTraversal.getWFIPath(aGraph, FWIresult, vertices[0], vertices[vertexNum - 1]);
//							System.out.print("The path from " + costFunction.getCost(view.getState(vertices[0])) + " to "
//									+ costFunction.getCost((view.getState(vertices[vertexNum - 1]))) + " is:");
//
//							for (int i = 0; i < path.length; i++)
//							{
//								System.out.print(" " + costFunction.getCost(view.getState(path[i])));
//							}
//
//							System.out.println();
//						}
//						catch (StructuralException e1)
//						{
//							System.out.println(e1);
//						}
//					}
//					catch (StructuralException e2)
//					{
//						System.out.println(e2);
//					}
//				}
//			}
//		}
//	}
//

