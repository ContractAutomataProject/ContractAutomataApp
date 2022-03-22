package io.github.contractautomata.catapp.castate;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxICell;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxPerimeter.mxPerimeterFunction;

import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;

import com.mxgraph.view.mxStyleRegistry;

/**
 * Extending State with information on x,y coordinates. 
 * Including static functionalities related to the style of mxCells containing MxStates.
 * 
 * @author Davide Basile
 *
 */
public class MxState extends State<String> {

	private final float x;
	private final float y;
	
	public MxState(List<BasicState<String>> lstate, float x, float y) {
		super(lstate);
		this.x=x;
		this.y=y;

	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public final static double initialStateWidthIncrement = 16.5;
	
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


	public static void setShapes() {
		mxStyleRegistry.putValue("SHAPE_INITIALSTATE","initialState");
		mxStyleRegistry.putValue("SHAPE_INITIALFINALSTATE","initialFinalState");
		
		//inserting entries for initial and initial final state
		String initialStateShape="";
		try {
			initialStateShape = mxUtils.readFile(System.getProperty("user.dir")+"/src/io/github/davidebasile/catapp/images/startstate.shape");
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
			initialFinalStateShape = mxUtils.readFile(System.getProperty("user.dir")+"/src/io/github/davidebasile/catapp/images/startfinalstate.shape");
		} catch (IOException e) {
			e.printStackTrace();
		}
		newShape = new mxStencilShape(initialFinalStateShape);
		mxGraphics2DCanvas.putShape((String) mxStyleRegistry.getValue("SHAPE_INITIALFINALSTATE"), newShape);
		

		mxStyleRegistry.putValue("InitialStatePerimeter", new mxPerimeterFunction() {
			@Override
			public mxPoint apply(mxRectangle bounds, mxCellState vertex, mxPoint next, boolean orthogonal) {
				mxRectangle rect =new mxRectangle(bounds.getX()+initialStateWidthIncrement,bounds.getY(),bounds.getWidth()-initialStateWidthIncrement,bounds.getHeight());
				return mxPerimeter.EllipsePerimeter.apply(rect, vertex, next, orthogonal) ;
			}
		});
	}
	
}
