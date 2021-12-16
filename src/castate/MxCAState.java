package castate;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxPerimeter.mxPerimeterFunction;
import com.mxgraph.view.mxStyleRegistry;

import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;

public class MxCAState extends CAState {
	
	public final static Predicate<mxICell> isInitial = n -> (n!=null)&&(n.getStyle()!=null)&&(n.getStyle().contains((String) mxStyleRegistry.getValue("SHAPE_INITIALFINALSTATE"))
			||n.getStyle().contains((String) mxStyleRegistry.getValue("SHAPE_INITIALSTATE")));

	public final static Predicate<mxICell> isFinal = n -> (n!=null)&&(n.getStyle()!=null)&&(n.getStyle().contains((String) mxStyleRegistry.getValue("SHAPE_INITIALFINALSTATE"))
			||n.getStyle().contains(mxConstants.SHAPE_DOUBLE_ELLIPSE));

	private final static String stylevalue = mxConstants.STYLE_ROUNDED+";"
			 +mxConstants.STYLE_FILLCOLOR+"="+mxConstants.NONE+";"
			 +mxConstants.STYLE_STROKECOLOR+"=black;"
			 +mxConstants.STYLE_VERTICAL_LABEL_POSITION+"="+mxConstants.ALIGN_BOTTOM+";"
			 +mxConstants.STYLE_SPACING_TOP+"="+"0;"
			 +mxConstants.STYLE_FONTCOLOR+"=black;";
	
	public final static String nodestylevalue = stylevalue
				 +mxConstants.STYLE_SHAPE+"="+mxConstants.SHAPE_ELLIPSE+";"
				 +mxConstants.STYLE_PERIMETER+"="+mxConstants.PERIMETER_ELLIPSE+";";

	public final static String finalnodestylevalue = stylevalue
				 +mxConstants.STYLE_SHAPE+"="+mxConstants.SHAPE_DOUBLE_ELLIPSE+";"
				 +mxConstants.STYLE_PERIMETER+"="+mxConstants.PERIMETER_ELLIPSE+";";
	
	public final static String initialnodestylevalue = stylevalue
				 +mxConstants.STYLE_SHAPE+"=initialState;"
				 +mxConstants.STYLE_PERIMETER+"=InitialStatePerimeter;";

	public final static String initialfinalnodestylevalue = stylevalue
				 +mxConstants.STYLE_SHAPE+"=initialFinalState;"
				 +mxConstants.STYLE_PERIMETER+"=InitialStatePerimeter;";

	public MxCAState(List<BasicState> lstate, float x, float y) {
		super(lstate, x, y);
		isFinal.and(isInitial);
	}

	//the perimeter of the initial state
	public static mxPerimeterFunction InitialStatePerimeter = new mxPerimeterFunction() {
		@Override
		public mxPoint apply(mxRectangle bounds, mxCellState vertex, mxPoint next, boolean orthogonal) {
			double shift=16.5;
			mxRectangle rect =new mxRectangle(bounds.getX()+shift,bounds.getY(),bounds.getWidth()-shift,bounds.getHeight());
			return mxPerimeter.EllipsePerimeter.apply(rect, vertex, next, orthogonal) ;
		}
	};


	public static void setShapes() {
		mxStyleRegistry.putValue("SHAPE_INITIALSTATE","initialState");
		mxStyleRegistry.putValue("SHAPE_INITIALFINALSTATE","initialFinalState");
		
		//inserting entries for initial and initial final state
		String initialStateShape="";
		try {
			initialStateShape = mxUtils.readFile(System.getProperty("user.dir")+"/src/com/mxgraph/examples/swing/images/startstate.shape");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//		// Some editors place a 3 byte BOM at the start of files
		//		// Ensure the first char is a "<"
		//		int lessthanIndex = nodeXml.indexOf("<");
		//		nodeXml = nodeXml.substring(lessthanIndex);
		mxStencilShape newShape = new mxStencilShape(initialStateShape);
		mxGraphics2DCanvas.putShape( (String) mxStyleRegistry.getValue("SHAPE_INITIALSTATE"), newShape);


		String initialFinalStateShape="";
		try {
			initialFinalStateShape = mxUtils.readFile(System.getProperty("user.dir")+"/src/com/mxgraph/examples/swing/images/startfinalstate.shape");
		} catch (IOException e) {
			e.printStackTrace();
		}
		newShape = new mxStencilShape(initialFinalStateShape);
		mxGraphics2DCanvas.putShape((String) mxStyleRegistry.getValue("SHAPE_INITIALFINALSTATE"), newShape);

		mxStyleRegistry.putValue("InitialStatePerimeter", InitialStatePerimeter);
	}
	
	public static void toggleFinalState(mxCell node) {
		if (MxCAState.isFinal.test(node)) {
			if (MxCAState.isInitial.test(node))
				node.setStyle(initialnodestylevalue);
			else
				node.setStyle(nodestylevalue);
		}
		else {
			if (MxCAState.isInitial.test(node))
				node.setStyle(initialfinalnodestylevalue);			
			else
				node.setStyle(finalnodestylevalue);				
		}
	}
}