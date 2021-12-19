package io.github.davidebasile.catapp.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.AbstractAction;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

public class AddHandleAction extends AbstractAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Point pt;
	
	/**
	 * 
	 * @param key
	 */
	public AddHandleAction(Point pt)
	{
		this.pt=pt;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof mxGraphComponent)
		{
			mxGraphComponent graphComponent = (mxGraphComponent) e
					.getSource();
			mxGraph graph = graphComponent.getGraph();

			if (!graph.isSelectionEmpty())
			{
				mxCell edge = (mxCell) graph.getSelectionCell();
				mxGeometry eg = edge.getGeometry();				
				List<mxPoint> l = eg.getPoints()!=null
						?new ArrayList<mxPoint>(eg.getPoints())
						:new ArrayList<mxPoint>();
						
//				System.out.println(eg.getSourcePoint());
//				System.out.println(l);
//				System.out.println(eg.getTargetPoint());
//				System.out.println("You clicked on point "+pt);
				
				if (l.size()==0)
					l.add(new mxPoint(pt.x,pt.y));
				else {
					int ind = findIndex(l,eg.getSourcePoint(),eg.getTargetPoint());
					l.add(ind, new mxPoint(pt.x,pt.y));
				}
				eg.setPoints(l);
				graphComponent.refresh();
			}
		}
	}
	
	private int findIndex(List<mxPoint> l, mxPoint source, mxPoint target) {
		int ind = IntStream.rangeClosed(0, l.size())
				.mapToObj(i-> new Object() {int index=i; 
				int d= (int) ((i==0)?liesIn(source,l.get(0))
						:(i==l.size())?liesIn(l.get(l.size()-1),target)
						:liesIn(l.get(i-1),l.get(i)));
				})
				.sorted((x,y)->x.d-y.d)
				.mapToInt(o->o.index)
				.findFirst().orElse(0);
		return ind;
				
		
	}
	
	//using the equation of a line passing from two points
	private double liesIn(mxPoint p2, mxPoint p1) {
		if (p1.getY()==p2.getY())
			return Math.abs(pt.y-p1.getY());
		else {
			double lies = ((pt.y - p1.getY())/(p2.getY()-p1.getY()))*(p2.getX()-p1.getX())+p1.getX();
			//.out.println(Math.abs(pt.x-lies));
			return Math.abs(pt.x - lies);
		}
	}


}
