package io.github.contractautomata.catapp.castate;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxICell;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxPerimeter.mxPerimeterFunction;

import io.github.contractautomata.catapp.App;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;

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
			URL url = App.class.getClassLoader().getResource("io/github/contractautomata/catapp/images/startstate.shape");
			InputStream is = url.openStream();
			initialStateShape = new BufferedReader(
					new InputStreamReader(is, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining(System.lineSeparator()));
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
			URL url = App.class.getClassLoader().getResource("io/github/contractautomata/catapp/images/startfinalstate.shape");
			InputStream is = url.openStream();
			initialFinalStateShape = new BufferedReader(
					new InputStreamReader(is, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		newShape = new mxStencilShape(initialFinalStateShape);
		mxGraphics2DCanvas.putShape((String) mxStyleRegistry.getValue("SHAPE_INITIALFINALSTATE"), newShape);
		

		mxStyleRegistry.putValue("InitialStatePerimeter", (mxPerimeterFunction) (bounds, vertex, next, orthogonal) -> {
            mxRectangle rect =new mxRectangle(bounds.getX()+initialStateWidthIncrement,bounds.getY(),bounds.getWidth()-initialStateWidthIncrement,bounds.getHeight());
            return mxPerimeter.EllipsePerimeter.apply(rect, vertex, next, orthogonal) ;
        });
	}
	
}
