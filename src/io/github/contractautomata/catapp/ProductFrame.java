package io.github.contractautomata.catapp;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents a frame visualising the products of a feature model
 * @author Davide Basile
 *
 */
public class ProductFrame extends JFrame{
	/**
	 * the family whose products are being displayed
	 */
	private final Family fam;
	private final List<Product> prod;
	final JButton[] nodes;

	private static final long serialVersionUID = 1L;

	public ProductFrame(Family f, JPanel frame, Automaton<String,Action,State<String>, ModalTransition<String,Action,State<String>,CALabel>> aut){
		this.fam=f;
		JPanel panel=new JPanel();
		this.setLocationRelativeTo(frame);
		this.setSize(800,800);
		setTitle("Family Products");
		panel.setAutoscrolls(true);
		Function<Product,Integer> fpi = p->(aut==null)?p.getForbiddenAndRequiredNumber():p.getForbidden().size();
	
		prod = new ArrayList<>(f.getProducts());
		prod.sort((p1,p2)-> fpi.apply(p2)-fpi.apply(p1));//p2.getForbiddenAndRequiredNumber()-p1.getForbiddenAndRequiredNumber());
		
		Map<Integer, Set<Product>> depth = f.getProducts().parallelStream()
		.collect(Collectors.groupingBy(fpi, //Product::getForbiddenAndRequiredNumber,
				Collectors.toSet()));
				
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
				new ArrayList<>(depth.entrySet());
		depthlist.sort((x, y) -> y.getKey() - x.getKey());
	
		
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
						StringBuilder message= new StringBuilder("Subproducts of P" + index + " " + prod.get(index).toString() + " \n");
						Set<Product> subprod = fam.getSubProductsofProduct(prod.get(index));
						for (Product p2 : subprod)
						{
							int p2index = prod.indexOf(p2);
							message.append("P").append(p2index).append(" ").append(p2.toString()).append("\n");
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
							textArea.setText(message.toString());
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
		for (JButton node : nodes) {
			node.setBorder(BorderFactory.createLineBorder(Color.black));
			node.setBorderPainted(false);
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
		super.paint(g);
	}
}