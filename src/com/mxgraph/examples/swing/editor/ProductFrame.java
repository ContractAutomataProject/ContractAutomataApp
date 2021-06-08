package com.mxgraph.examples.swing.editor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import family.Family;
import family.Product;

/**
 * This class represents a frame visualising the products of a feature model
 * @author Davide Basile
 *
 */
public class ProductFrame extends JFrame{
	/**
	 * the family whose products are being displayed
	 */
	private Family fam;
	private List<Product> prod;
	JButton[] nodes;

	private static final long serialVersionUID = 1L;

	public ProductFrame(Family f, JPanel frame){
		this.fam=f;
		JPanel panel=new JPanel();
		this.setLocationRelativeTo(frame);
		this.setSize(800,800);
		setTitle("Family Products");
		panel.setAutoscrolls(true);
		prod = new ArrayList<>(f.getProducts());
		prod.sort((p1,p2)->p2.getForbiddenAndRequiredNumber()-p1.getForbiddenAndRequiredNumber());
		
		Map<Integer, Set<Product>> depth = f.getProducts().parallelStream()
		.collect(Collectors.groupingBy(Product::getForbiddenAndRequiredNumber,Collectors.toSet()));
				
		//int[][] depth = f.getDepth();
		JScrollPane scrollpanel= new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,  
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollpanel.setPreferredSize(new Dimension(600,600));
		scrollpanel.setAutoscrolls(true);

		int rows=depth.size();
		int columns = depth.entrySet().parallelStream()
				.mapToInt(e->e.getValue().size())
				.max().orElse(0);
		
		List<Map.Entry<Integer, Set<Product>>> depthlist=
				new ArrayList<Map.Entry<Integer, Set<Product>>>(depth.entrySet());
		Collections.sort(depthlist,(x,y)->y.getKey()-x.getKey());
	
		
//		int rows = depth.length;
//		int[] deleng=new int[depth.length];
//		for (int i=0;i<rows;i++)
//			deleng[i]=depth[i].length;	//number of products at each level		
//
//		Arrays.parallelSort(deleng);
//		int columns = deleng[deleng.length-1];//

		//JML indicates that Comparator has some problem, if used to get the maximum 


		GridLayout gl = new GridLayout(rows,columns);
		panel.setLayout(gl);
		gl.setHgap(10);
		gl.setVgap(10);
		nodes = new JButton[prod.size()];

		for (Map.Entry<Integer, Set<Product>> e : depthlist)
		{
			if (e.getValue().isEmpty())
			{
				for (int j=0;j<columns;j++)
					panel.add(new JLabel());
			}
			else 
			{
				int remainingColumns = columns - e.getValue().size();
				for (Product p : e.getValue())
				{
					int pindex = prod.indexOf(p);
					nodes[pindex] = new JButton();
					nodes[pindex].setBorder(BorderFactory.createLineBorder(Color.black));
					nodes[pindex].setBorderPainted(false);
					nodes[pindex].setText(p.toHTMLString("P"+pindex));
					nodes[pindex].putClientProperty("index", pindex+"");
					nodes[pindex].setSize(new Dimension(200,300));
					nodes[pindex].addActionListener(ae -> 
					{
						JButton source = (JButton)ae.getSource();

						int index=Integer.parseInt((String)source.getClientProperty("index"));
						String message="Subproducts of P"+index+" "+prod.get(index).toString()+" \n";
						Set<Product> subprod = fam.getSubProductsofProduct(prod.get(index));
						for (Product p2 : subprod)
						{
							int p2index = prod.indexOf(p2);
							message+="P"+p2index+" "+p2.toString()+"\n";
							if (!source.isBorderPainted())
							{
								try{
									nodes[p2index].setBorder(BorderFactory.createLineBorder(Color.red));
									nodes[p2index].setBorderPainted(true);
								} catch (Exception ee){
									ee.printStackTrace();
								}
							}
							else
							{
								nodes[p2index].setBorder(BorderFactory.createLineBorder(Color.black));
								nodes[p2index].setBorderPainted(false);
							}
						}
						if (!source.isBorderPainted())
						{
							source.setBorderPainted(true);
							source.setBorder(BorderFactory.createLineBorder(Color.red));
							JTextArea textArea = new JTextArea(200,200);
							textArea.setText(message);
							textArea.setEditable(true);

							JScrollPane scrollPane = new JScrollPane(textArea);
							JDialog jd = new JDialog();
							jd.add(scrollPane);
							jd.setTitle("Sub-Products");
							jd.setResizable(true);
							jd.setVisible(true);

							jd.setSize(500,500);
							jd.setLocationRelativeTo(null);

							//JOptionPane.showMessageDialog(null, message);
						}
						else{
							source.setBorderPainted(false);
							source.setBorder(BorderFactory.createLineBorder(Color.black));	        	                
						}
					});
					panel.add(nodes[pindex]);
				}
				for (int j=0;j<remainingColumns;j++)
					panel.add(new JLabel());
			}
		
		
//		for (int i=depth.length-1;i>=0;i--)
//		{
//			if (depth[i]==null)
//			{
//				for (int j=0;j<columns;j++)
//				{
//					panel.add(new JLabel());
//				}
//			}
//			else for (int j=0;j<columns;j++)
//			{
//				if(j<depth[i].length)
//				{
//					nodes[depth[i][j]] = new JButton();
//					nodes[depth[i][j]].setBorder(BorderFactory.createLineBorder(Color.black));
//					nodes[depth[i][j]].setBorderPainted(false);
//					nodes[depth[i][j]].setText(prod[depth[i][j]].toHTMLString("P"+depth[i][j]));
//					nodes[depth[i][j]].putClientProperty("i", i+"");
//					nodes[depth[i][j]].putClientProperty("j", j+"");
//					nodes[depth[i][j]].setSize(new Dimension(200,300));
//					nodes[depth[i][j]].addActionListener(ae -> 
//					{
//						JButton source = (JButton)ae.getSource();
//						String si=(String)source.getClientProperty("i");
//						String sj=(String)source.getClientProperty("j");
//						//	        	                int[] ptl=fam.getPointerToLevel();
//						//	        	                int[][] rpo=fam.getReversePO();
//						int[][] depthlambda=fam.getDepth();
//						Product[] prodlambda = fam.getElements();
//						int poindex=depthlambda[Integer.parseInt(si)][Integer.parseInt(sj)];
//						String message="Subproducts of P"+poindex+" "+prodlambda[poindex].toString()+" \n";
//						int[] subind = fam.getSubProductsofProduct(poindex);
//						Product[] subprod = fam.subsetOfProductsFromIndex(subind);
//						for (int ind=0;ind<subprod.length;ind++)
//						{
//							message+="P"+subind[ind]+" "+subprod[ind].toString()+"\n";
//							if (!source.isBorderPainted())
//							{
//								try{
//									nodes[subind[ind]].setBorder(BorderFactory.createLineBorder(Color.red));
//									nodes[subind[ind]].setBorderPainted(true);
//								} catch (Exception e){
//									//debug
//									//		int d=prod[ind].getForbiddenAndRequiredNumber();
//									//int dd=ptl[ind];
//									//int ddd=depth[prod[ind].getForbiddenAndRequiredNumber()][ptl[ind]]; 
//									//	   int x=0;
//									//System.out.println(d+dd+ddd+x);
//									e.printStackTrace();
//								}
//							}
//							else
//							{
//								nodes[subind[ind]].setBorder(BorderFactory.createLineBorder(Color.black));
//								nodes[subind[ind]].setBorderPainted(false);
//							}
//						}
//						if (!source.isBorderPainted())
//						{
//							source.setBorderPainted(true);
//							source.setBorder(BorderFactory.createLineBorder(Color.red));
//							JTextArea textArea = new JTextArea(200,200);
//							textArea.setText(message);
//							textArea.setEditable(true);
//
//							JScrollPane scrollPane = new JScrollPane(textArea);
//							JDialog jd = new JDialog();
//							jd.add(scrollPane);
//							jd.setTitle("Sub-Products");
//							jd.setResizable(true);
//							jd.setVisible(true);
//
//							jd.setSize(500,500);
//							jd.setLocationRelativeTo(null);
//
//							//JOptionPane.showMessageDialog(null, message);
//						}
//						else{
//							source.setBorderPainted(false);
//							source.setBorder(BorderFactory.createLineBorder(Color.black));	        	                
//						}
//					});
//					panel.add(nodes[depth[i][j]]);
//				}
//				else
//					panel.add(new JLabel());
//			}
		}

		panel.setBorder(new LineBorder(Color.BLACK));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		getContentPane().add(scrollpanel);
		this.pack();
		setVisible(true);
	}

	public void setColorButtonProducts(Set<Product> sprod, Color c)
	{
		for (Product p : sprod)
		{
			int i=prod.indexOf(p);
			nodes[i].setBorder(BorderFactory.createLineBorder(c));
			nodes[i].setBorderPainted(true);
		}
	}

	public void resetColorButtonProducts()
	{
		for (int i=0;i<nodes.length;i++)
		{
			nodes[i].setBorder(BorderFactory.createLineBorder(Color.black));
			nodes[i].setBorderPainted(false);
		}
	}

	public Family getFamily()
	{
		return fam;
	}
	
	public Integer indexOf(Product p)
	{
		return prod.indexOf(p);
	}
	
	public Product getProductAt(int i)
	{
		return prod.get(i);
	}
	
	public void paint(Graphics g) {
		super.paint(g);  // fixes the immediate problem.
		/*Graphics2D g2 = (Graphics2D) g;
        Product[] prod = fam.getProducts();
        int[][] depth = fam.getDepth();
        int[] ptl=fam.getPointerToLevel();
        int[][] po=fam.getPartialOrder();

        for (int i=0;i<po.length;i++)
        {
        	for (int j=0;j<po[i].length;j++)
        	{
        		if (po[i][j]==1)
        		{
        			Line2D lin = new Line2D.Float(
	                	nodes[depth[prod[i].getForbiddenAndRequiredNumber()][ptl[i]]].getX()+25,
		        		nodes[depth[prod[i].getForbiddenAndRequiredNumber()][ptl[i]]].getY()+50,
		        		nodes[depth[prod[j].getForbiddenAndRequiredNumber()][ptl[j]]].getX()+25,
		        		nodes[depth[prod[j].getForbiddenAndRequiredNumber()][ptl[j]]].getY()+60
	        		);
	        		g2.draw(lin);
        		}
        	}
        }*/
	}   
}