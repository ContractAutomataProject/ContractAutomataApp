package FMCA;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Family {
	private Product[] elements;
	private int[][] po; //matrix po[i][j]==1 iff elements[i]<elements[j]
	private int[][] reversepo; //matrix po[i][j]==1 iff elements[i]>elements[j]
	private int[][] depth; //depth[i] level i -- list of products
	private int[] pointerToLevel; //i index to po, pointerLevel[i] index to depth[totfeatures i]
	private boolean[] hasParents;// hasParents[i]==true iff there exists j s.t. reversepo[i][j]=1
	public Family(Product[] elements, int[][] po)
	{
		this.elements=elements;
		this.po=po;
	}
	
	public Family(Product[] elements)
	{
		this.elements=elements;
		this.generatePO();
	}
	
	public Family(String filename)
	{
		this.elements=Family.readFile(System.getProperty("user.dir"),filename);
		this.generatePO();
	}
	
	public Product[] getProducts()
	{
		return elements;
	}
	
	public int[][] getPartialOrder()
	{
		return po;
	}
	
	public int[][] getReversePO()
	{
		return reversepo;
	}
	
	public int[][] getDepth()
	{
		return this.depth;
	}
	
	public int[] getPointerToLevel()
	{
		return this.pointerToLevel;
	}
	
	
	/**
	 * generate po of products, no transitive closure!
	 * @return
	 */
	protected void generatePO()
	{
		depth=new int[1000][1000];//TODO upper bounds;	
		int[] depthcount=new int[1000];//TODO upperbound  count the number of products at each level of depth
		for (int i=0;i<depthcount.length;i++)
			depthcount[i]=0;
		Product[] p=this.elements;
		po=new int[p.length][p.length]; 
		reversepo=new int[p.length][p.length]; 
		hasParents=new boolean[p.length];
		for (int i=0;i<p.length;i++)
			hasParents[i]=false;
		pointerToLevel=new int[p.length];
		int maxdepth=0;
		for (int i=0;i<p.length;i++)
		{
			if (p[i].getForbiddenAndRequiredNumber()>maxdepth)
				maxdepth=p[i].getForbiddenAndRequiredNumber();
			depth[p[i].getForbiddenAndRequiredNumber()][depthcount[p[i].getForbiddenAndRequiredNumber()]]=i;
			pointerToLevel[i]=depthcount[p[i].getForbiddenAndRequiredNumber()];
			depthcount[p[i].getForbiddenAndRequiredNumber()]+=1;
			for (int j=i+1;j<p.length;j++)
			{
				if (p[i].getForbiddenAndRequiredNumber()==p[j].getForbiddenAndRequiredNumber()+1)//1 level of depth
				{
					if (p[i].containsFeatures(p[j]))
					{
						po[i][j]=1;
						reversepo[j][i]=1;
						hasParents[i]=true;
					}
					else
					{
						po[i][j]=0;
						reversepo[j][i]=0;
					}
				}
				else
				{
					po[i][j]=0;
					reversepo[j][i]=0;
				}
				
				if (p[j].getForbiddenAndRequiredNumber()==p[i].getForbiddenAndRequiredNumber()+1)//1 level of depth
				{
					if (p[j].containsFeatures(p[i]))
					{
						po[j][i]=1;
						reversepo[i][j]=1;
						hasParents[j]=true;
					}
					else
					{
						po[j][i]=0;
						reversepo[i][j]=0;
					}
				}
				else
				{
					po[j][i]=0;
					reversepo[i][j]=0;
				}
			}
		}
		
		//remove tails null
		int newdepth[][] = new int[maxdepth+1][];
		for (int i=0;i<newdepth.length;i++)
		{
			newdepth[i]= new int[depthcount[i]];
			for (int j=0;j<newdepth[i].length;j++)
			{
				newdepth[i][j]=depth[i][j];
			}
		}
		depth=newdepth;
	}
	
	/**
	 * read products from file
	 * @param currentdir
	 * @param filename
	 * @return
	 */
	protected static Product[] readFile(String currentdir, String filename){
		//Path p=Paths.get(currentdir, filename);
		Path p=Paths.get("", filename);
		
		Charset charset = Charset.forName("ISO-8859-1");
		List<String> lines = null;
		try {
			lines = Files.readAllLines(p, charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] arr = lines.toArray(new String[lines.size()]);
		Product[] products=new Product[arr.length];//TODO fix max products
		for(int productsind=0;productsind<arr.length;productsind++)
		{
			String[] s=arr[productsind].split("}"); //each line identifies a product			
			String required=s[0].substring(s[0].indexOf("{")+1);
			String requireds[]=required.split(",");

			String forbidden=s[1].substring(s[1].indexOf("{")+1);
			String forbiddens[]=forbidden.split(",");

			products[productsind]=new Product(requireds,forbiddens);
		}
		return products;
	}
	
	/**
	 * loads the list of products generated through FeatureIDE
	 * the list of products and the xml model description must be inside 
	 * the same directory
	 * @param currentdir
	 * @param filename
	 * @return
	 */
	public static Product[] importFamily(String currentdir, String filename)
	{	
		String[] features=getFeatures(filename);
		File folder = new File(currentdir.substring(0, currentdir.lastIndexOf("\\")));
		File[] listOfFiles = folder.listFiles();
		Product[] pr=new Product[listOfFiles.length];
		int prlength=0;
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()&&listOfFiles[i].getName().contains("config")) {
		    	Path p=Paths.get("", listOfFiles[i].getAbsolutePath());
		  		Charset charset = Charset.forName("ISO-8859-1");
		  		List<String> lines = null;
		  		try {
		  			lines = Files.readAllLines(p, charset);
		  		} catch (IOException e) {
		  			e.printStackTrace();
		  		}
		  		String[] f1 = lines.toArray(new String[lines.size()]); //required features
		  		pr[prlength]=new Product(FMCAUtil.setIntersection(f1, features), FMCAUtil.setDifference(features, f1));
		  		prlength++;
		      }
		    }
		pr=FMCAUtil.removeTailsNull(pr, prlength);
		//return generateSuperProducts(pr,features);
		return pr;
	}
	
	/**
	 * 
	 * @param p list of pairwise different products
	 * @param features  the features of the products
	 * @return  list containing all valid superproducts (aka subfamily)
	 */
	private static Product[] generateSuperProducts(Product[] p, String[] features)
	{
		if ((p==null)||features==null)
			return null;

		Product[][] pl= new Product[features.length][];
		pl[features.length-1]=p;
		for (int level=features.length; level>0;level--)//start from the bottom of the tree, all features instantiated
		{
			Product[] newproducts= new Product[pl[level-1].length*(pl[level-1].length-1)]; //upperbound to the possible number of discovered new products 
			int newprodind=0;
			for (int removedfeature=0; removedfeature<features.length;removedfeature++) //for each possible feature to be removed
			{
				for (int prodind=0; prodind<pl[level-1].length;prodind++)
				{
					if (pl[level-1][prodind].getForbiddenAndRequiredNumber()==level && pl[level-1][prodind].containFeature(features[removedfeature]))
					{
						for (int prodcompare=prodind+1; prodcompare<pl[level-1].length;prodcompare++)
						{
							if (pl[level-1][prodcompare].getForbiddenAndRequiredNumber()==level && pl[level-1][prodcompare].containFeature(features[removedfeature])) 
								/*for each pair of products at the same level check if by removing the selected feature they 
								  are equals. This can happen only if the feature is forbidden in one product and required in the other 
								  product (the feature is contained in both products) otherwise the two products are equals, 
								  and initially no products are equal and this property is invariant.
								 */
							{
								Product debug=pl[level-1][prodind];
								Product debug2=pl[level-1][prodcompare];
								String[] rf=new String[1];
								rf[0]=features[removedfeature];
								Product p1 = new Product(FMCAUtil.setDifference(pl[level-1][prodind].getRequired(),rf),
										FMCAUtil.setDifference(pl[level-1][prodind].getForbidden(),rf));
								Product p2 = new Product(FMCAUtil.setDifference(pl[level-1][prodcompare].getRequired(),rf),
										FMCAUtil.setDifference(pl[level-1][prodind].getForbidden(),rf));
								if (p1.equals(p2))
								{	//new super product discovered!
									newproducts[newprodind]=p1;
									newprodind++;
								}
							}			
						}
					}
				}
			}
			if (newprodind>0)
			{
				newproducts=FMCAUtil.removeTailsNull(newproducts, newprodind);
				//p=FMCAUtil.concat(p, newproducts);  // this can be optimised, because in the next iteration only newproducts need to be checked
				pl[level-2]=newproducts;
			}
			else
				break; //stop earlier when no products are discovered
		}
		for (int i=features.length-2;i>0;i--)
			p=FMCAUtil.concat(p, pl[i]);  
		return p;
	}
	
	private static String[] getFeatures(String filename)
	{
		String[] features=null;
		try {
	         File inputFile = new File(filename);
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder;

	         dBuilder = dbFactory.newDocumentBuilder();

	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();

	         //XPath xPath =  XPathFactory.newInstance().newXPath();
	         
	         //NodeList nodeList = (NodeList) xPath.compile("").evaluate(doc, XPathConstants.NODESET);
	         NodeList nodeList = (NodeList) doc.getElementsByTagName("feature");
	         
	         features=new String[nodeList.getLength()];
	         /**
	          * first read all the states, then all the edges
	          */
	         int ind =0;
	         for (int i = 0; i < nodeList.getLength(); i++) 
	         {
	            Node nNode = nodeList.item(i);
	          //  System.out.println("\nCurrent Element :" 
	          //     + nNode.getNodeName());
	            if ((nNode.getNodeType() == Node.ELEMENT_NODE))//&&(nNode.getNodeName()=="mxCell")) {
	            {
	               Element eElement = (Element) nNode;
	               features[i]=eElement.getAttribute("name");    
	               ind++;
	            }       
	        }
	        features=FMCAUtil.removeTailsNull(features, ind);
	      } catch (ParserConfigurationException e) {
	         e.printStackTrace();
	      } catch (SAXException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
		         e.printStackTrace();
		      } 
		return features;		
	}

	
	public String toString()
	{
		String s="";
		for (int i=0;i<elements.length;i++)
			s+="Product "+i+"\n"+elements[i].toString()+"\n";
		s+="< Matrix:\n";
		for (int i=0;i<elements.length;i++)
			s+=Arrays.toString(po[i])+"\n";
		return s;
	}
	
	/**
	 * 
	 * @param aut
	 * @return an new family with only products valid in aut
	 */
	public Family validProducts(FMCA aut)
	{
		boolean[] valid=new boolean[elements.length];
		for (int i=0;i<elements.length;i++)
			valid[i]=false; //initialise
		int[] tv = getTopProducts();
		for (int i=0;i<tv.length;i++)
			valid(valid,tv[i],aut);
		
		Product[] newp=new Product[elements.length];
		int count=0;
		for (int i=0;i<newp.length;i++)
		{
			if (valid[i])
			{
				newp[count]=elements[i];
				count++;
			}
		}
		newp=FMCAUtil.removeTailsNull(newp, count);
		return new Family(newp);
	}
	
	private void valid(boolean[] valid, int i, FMCA aut)
	{
		if (elements[i].isValid(aut))
		{
			valid[i]=true;
			for (int j=0;j<reversepo[i].length;j++)
			{
				if (reversepo[i][j]==1)
					valid(valid,j,aut);
			}
		}//do not visit subtree if not valid
	}
	
	/**
	 * 
	 * @return all top products p s.t. there not exists p'>p
	 */
	public int[] getTopProducts()
	{
		int[] tp=new int[elements.length];
		int count=0;
		for (int i=0;i<elements.length;i++) 
		{
			if (!hasParents[i])
			{
				tp[count]=i;
				count++;
			}
		}
		tp=FMCAUtil.removeTailsNull(tp, count);
		return tp;
	}
	
	
	/**
	 * 
	 * @param aut
	 * @return  the indexes in this.elements of canonical products
	 */
	public Product[] getCanonicalProducts(FMCA aut)
	{
		Family f=this.validProducts(aut); //prefilter
		Product[] p=f.getProducts();
		int[] ind= f.getTopProducts(); 
		FMCA[] K= new FMCA[p.length];
		int nonemptylength=0;
		int[] nonemptyindex= new int[p.length];
		for (int i=0;i<ind.length;i++)
		{
			K[i]=aut.mpc(p[ind[i]]);
			if (K[i]!=null)
			{
				nonemptyindex[nonemptylength]=ind[i]; //index in the array of products
				nonemptylength++;
			}
		}
		
		//quotient by forbidden actions: initialise
		int[][] quotient = new int[nonemptylength][nonemptylength]; //upperbound
		int quotientclasses=0;
		int[] classlength=new int[nonemptylength]; //upperbound
		boolean[] addedToClass=new boolean[nonemptylength];
		for (int i=0;i<nonemptylength;i++)
		{
			addedToClass[i]=false;
			classlength[i]=0;
		}
		//build
		for (int i=0;i<nonemptylength;i++) 
		{
			if (addedToClass[i]==false) //not added previously
			{
				addedToClass[i]=true;
				quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[i]; //index in the array of products
				classlength[quotientclasses]++;
				for (int j=i+1;j<nonemptylength;j++)
				{
					if (p[nonemptyindex[i]].containsForbiddenFeatures(p[nonemptyindex[j]]))
					{
						addedToClass[j]=true;
						quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[j]; //index in the array of products
						classlength[quotientclasses]++;
					}
				}
				quotientclasses++;
			}
		}
		//take as canonical product the first element of each class
		Product[] canonicalproducts=new Product[quotientclasses];
		for (int i=0;i<quotientclasses;i++)
		{
			canonicalproducts[i]=p[quotient[i][0]];
		}
		return canonicalproducts;
	}
	
	public FMCA getMPCofFamily(FMCA aut)
	{
		Family f=this.validProducts(aut); //prefilter
		Product[] p=f.getProducts();
		int[] ind= f.getTopProducts(); 
		FMCA[] K= new FMCA[p.length];
		int nonemptylength=0;
		int[] nonemptyindex= new int[p.length];
		for (int i=0;i<ind.length;i++)
		{
			K[ind[i]]=aut.mpc(p[ind[i]]);
			if (K[ind[i]]!=null)
			{
				nonemptyindex[nonemptylength]=ind[i]; //index in the array of products
				nonemptylength++;
			}
		}
		
		//quotient by forbidden actions: initialise
		int[][] quotient = new int[nonemptylength][nonemptylength]; //upperbound
		int quotientclasses=0;
		int[] classlength=new int[nonemptylength]; //upperbound
		boolean[] addedToClass=new boolean[nonemptylength];
		for (int i=0;i<nonemptylength;i++)
		{
			addedToClass[i]=false;
			classlength[i]=0;
		}
		//build
		for (int i=0;i<nonemptylength;i++) 
		{
			if (addedToClass[i]==false) //not added previously
			{
				addedToClass[i]=true;
				quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[i]; //index in the array of products
				classlength[quotientclasses]++;
				for (int j=i+1;j<nonemptylength;j++)
				{
					if (p[nonemptyindex[i]].containsForbiddenFeatures(p[nonemptyindex[j]]))
					{
						addedToClass[j]=true;
						quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[j]; //index in the array of products
						classlength[quotientclasses]++;
					}
				}
				quotientclasses++;
			}
		}
		//take as canonical product the first element of each class
		
		FMCA[] K2= new FMCA[quotientclasses]; //K of all canonical products
		for (int i=0;i<quotientclasses;i++)
		{
			K2[i]=K[quotient[i][0]]; 
		}
		return FMCAUtil.union(K2);
	}
}
