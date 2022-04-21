package io.github.contractautomata.catapp.converters;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mxgraph.model.mxCell;

import io.github.contractautomata.catapp.castate.MxState;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.converters.AutConverter;

/**
 * Import/export in xml (mxe) format. This is the format 
 * used by mxGraph library.
 * 
 * @author Davide Basile
 *
 */
public class MxeConverter implements AutConverter<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>,Automaton<String, Action,State<String>, ModalTransition<String,Action,State<String>,CALabel>>> {

	/**
	 * Import the mxGraphModel XML description (used by the mxGraph) into an MSCA object
	 * 
	 * @param filename  the XML file name
	 * @return the MSCA parsed from the XML
	 * @throws IOException  exception problems when reading the file
	 */
	@Override
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> importMSCA(String filename) throws IOException, ParserConfigurationException, SAXException {
		//TODO long function
		File inputFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("mxCell");

		Map<Integer,Set<BasicState<String>>> princ2bs = new HashMap<>();
		Map<Integer, State<String>> id2castate = new HashMap<>();
		Set<ModalTransition<String,Action,State<String>,CALabel>> transitions= new HashSet<>();

		//first read all basic states and castates, then all the edges.
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node nNode = nodeList.item(i);
			if ((nNode.getNodeType() == Node.ELEMENT_NODE))
			{
				Element eElement = (Element) nNode;
				
				if (Integer.parseInt(eElement.getAttribute("id"))>1 && !eElement.hasAttribute("edge"))
				{
					String value = eElement.getAttribute("value");
					if (value.startsWith("principal"))//basic state
					{
						Integer principal = Integer.parseInt(value.substring(value.indexOf("=")+1, value.indexOf(",")));//first entry is principal
						BasicState<String> bs = readCSV(value.substring(value.indexOf(",")+1));

						if (princ2bs.containsKey(principal))
						{
							Set<BasicState<String>> sbs = princ2bs.get(principal);
							if (!sbs.isEmpty()&&sbs.stream()
									.map(BasicState::getState)
									.anyMatch(s->s.equals(bs.getState())))
								throw new IOException("Duplicate basic states labels");
							else
								sbs.add(bs);
						}
						else 
							princ2bs.put(principal, new HashSet<>(List.of(bs)));
					}
					else {//castate
						Element geom= (Element) eElement.getElementsByTagName("mxGeometry").item(0);
						String[] st=Arrays.stream(eElement.getAttribute("value").replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\s", "").split(","))
								.toArray(String[]::new);
						mxCell cell = new mxCell(null,null,eElement.getAttribute("style"));
						
						Function<String,BasicState<String>> convertToBs= s->{
							//return new BasicState(s, s.equals("0"),eElement.getAttribute("style").contains("terminate.png"));
							return new BasicState<>(s, MxState.isInitial.test(cell), MxState.isFinal.test(cell));
						};
						
						//\forall i. \exists bs \in princ2bs(i). bs==st[i]

						List<BasicState<String>> lbs = IntStream.range(0, st.length)
								.mapToObj(ind-> {
									if (princ2bs.containsKey(ind))
									{
										Set<BasicState<String>> sbs = princ2bs.get(ind);

										return sbs.stream()
												.filter(bs->bs.getState().equals(st[ind]))
												.findFirst()
												.orElseGet(()->{
													BasicState<String> bs =convertToBs.apply(st[ind]);
													sbs.add(bs);	
													return bs;}); //orElseGet is needed when those basicstates are not written in the xml, e.g. when 
										// one is editing with mxGraph.
									}
									else 
									{
										BasicState<String> bs =convertToBs.apply(st[ind]);
										princ2bs.put(ind, new HashSet<>(List.of(bs)));
										return bs;  // this else branch is needed when those basicstates are not written in the xml, e.g. when 
										// one is editing with mxGraph.
									}})
								.collect(Collectors.toList());
						State<String> castate = new MxState(lbs, 
								geom.hasAttribute("x")?Float.parseFloat(geom.getAttribute("x")):0,
										geom.hasAttribute("y")?Float.parseFloat(geom.getAttribute("y")):0);
						//useful when not morphing (e.g. adding handles to edges)					

						if (castate.isFinalState()!=
								//eElement.getAttribute("style").contains("terminate.png"))
								MxState.isFinal.test(cell))
							throw new IOException("Problems with final states in .mxe "+cell.getStyle());

						if (id2castate.put(Integer.parseInt(eElement.getAttribute("id")), castate)!=null)
							throw new IOException("Duplicate states!");

					}
				}
			}
		}

		if (id2castate.isEmpty())
			throw new IOException("No states!");
		
		//transitions
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node nNode = nodeList.item(i);
			if ((nNode.getNodeType() == Node.ELEMENT_NODE))
			{
				Element eElement = (Element) nNode;	

				if (Integer.parseInt(eElement.getAttribute("id"))>1 && eElement.hasAttribute("edge")) {
					List<String> labels = Arrays.asList(eElement.getAttribute("value").replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\s", "").split(","));

					CALabel lab;
					try { 	lab = new CALabel(labels.stream().map(this::parseAction).collect(Collectors.toList()));}
					catch (IllegalArgumentException e) {
						//parsing failed
						throw new IOException("The label is not well formed");
					}


					transitions.add(new ModalTransition<>(id2castate.get(Integer.parseInt(eElement.getAttribute("source"))),
							lab,//label
							id2castate.get(Integer.parseInt(eElement.getAttribute("target"))), 
							(eElement.getAttribute("style").contains("strokeColor=#FF0000"))? ModalTransition.Modality.URGENT: //red
								(eElement.getAttribute("style").contains("strokeColor=#00FF00"))? ModalTransition.Modality.LAZY: //green
									ModalTransition.Modality.PERMITTED));
				}
			}
		}

		return new Automaton<>(transitions);

	}

	/**
	 * 
	 * @param s the encoding of the object as comma separated values
	 * @return a new BasicState<String> object constructed from the parameter s
	 */
	private BasicState<String> readCSV(String s) {
		boolean initial=false; 
		boolean	finalstate=false;
		String label="";
		String[] cs = s.split(",");
		for (String keyval : cs)
		{
			String[] kv = keyval.split("=");
			if(kv[0].equals("label"))
				label=kv[1];
			else if (kv[0].equals("initial"))
				initial=true;
			else finalstate=true;
		}
		return new BasicState<>(label,initial,finalstate);
	}

	/**
	 * Export the MSCA aut as a mxGraphModel  (used by mxGraph)  with XML extension (.mxe)
	 * @param fileName the name of the xml file where to write the automaton
	 * @param aut the automaton to be saved
	 * @throws ParserConfigurationException exception in parsing
	 * @throws TransformerException  exception with transformer
	 */
	@Override
	public  void exportMSCA(String fileName, Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbFactory =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = 
				dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		// root element
		Element rootElement = doc.createElement("mxGraphModel");
		doc.appendChild(rootElement);

		Element root = doc.createElement("root");
		rootElement.appendChild(root);


		Element mxcell0 = doc.createElement("mxCell");
		mxcell0.setAttribute("id", "0");
		root.appendChild(mxcell0);
		Element mxcell1 = doc.createElement("mxCell");
		mxcell1.setAttribute("id", "1");
		mxcell1.setAttribute("parent", "0");
		root.appendChild(mxcell1);

		int id=2;

		for (Entry<Integer,BasicState<String>> e : 
			aut.getBasicStates().entrySet().stream()
			.flatMap(e-> e.getValue().stream()
					.map(bs-> new AbstractMap.SimpleEntry<>(e.getKey(), bs)))
			.collect(Collectors.toSet()))
		{
			createElementBasicState(doc, root,Integer.toString(id), e.getKey(),e.getValue());
			id+=1;
		}

		Map<State<String>,Element> state2element = new HashMap<>();

		for (State<String> s : aut.getStates())
		{
			state2element.put(s, createElementState(doc, root,Integer.toString(id), s));
			id+=1;
		}

		Set<? extends ModalTransition<String,Action,State<String>,CALabel>> tr= aut.getTransition();
		for (ModalTransition<String,Action,State<String>,CALabel> t : tr)
		{
			createElementEdge(doc,root,Integer.toString(id),
					state2element.get(t.getSource()),
					state2element.get(t.getTarget()),
					Arrays.toString(t.getLabel().getContent().stream().map(Action::toString).toArray(String[]::new)),t.getModality());
			id+=1;
		}

		TransformerFactory transformerFactory =
				TransformerFactory.newInstance();
		Transformer transformer =
				transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		File file = new File((fileName.endsWith(".mxe"))?fileName:(fileName+".mxe"));
		StreamResult result =
				new StreamResult(file);
		transformer.transform(source, result);

	//	return file;
	}

	private static void createElementEdge(Document doc, Element root, String id, Element source, Element target, String label, ModalTransition.Modality type)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");
		Attr style=doc.createAttribute("style");

		if (type==ModalTransition.Modality.URGENT)
			style.setValue("edgeStyle=none;strokeColor=#FF0000;");
		else if (type==ModalTransition.Modality.LAZY)
			style.setValue("edgeStyle=none;strokeColor=#00FF00;");
		else
			style.setValue("edgeStyle=none;strokeColor=black;");
		Attr id1=doc.createAttribute("id");
		Attr as=doc.createAttribute("as");
		as.setValue("geometry");
		id1.setValue(id);

		Element mxcell1 = doc.createElement("mxCell");
		mxcell1.setAttribute("edge","1");
		mxcell1.setAttributeNode(id1);
		mxcell1.setAttributeNode(parent);
		mxcell1.setAttributeNode(style);
		mxcell1.setAttribute("source", source.getAttribute("id"));
		mxcell1.setAttribute("target", target.getAttribute("id"));
		mxcell1.setAttribute("value", label);

		Element mxGeometry1=doc.createElement("mxGeometry");
		mxGeometry1.setAttributeNode(as);
		mxGeometry1.setAttribute("relative","1");

		Element mxPointSource=doc.createElement("mxPoint");
		mxPointSource.setAttribute("as","sourcePoint");
		mxPointSource.setAttribute("x", ((Element)source.getChildNodes().item(0)).getAttribute("x"));
		mxPointSource.setAttribute("y", ((Element)source.getChildNodes().item(0)).getAttribute("y"));
		mxGeometry1.appendChild(mxPointSource);

		Element mxPointTarget=doc.createElement("mxPoint");
		mxPointTarget.setAttribute("as","targetPoint");
		mxPointTarget.setAttribute("x", ((Element)target.getChildNodes().item(0)).getAttribute("x"));
		mxPointTarget.setAttribute("y", ((Element)target.getChildNodes().item(0)).getAttribute("y"));
		mxGeometry1.appendChild(mxPointTarget);

		Element pointArray=doc.createElement("Array");
		pointArray.setAttribute("as","points");
		Element mxPoint=doc.createElement("mxPoint");

		float xs=Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("x"));
		float xt=Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("x"));
		float ys=Float.parseFloat(((Element)source.getChildNodes().item(0)).getAttribute("y"));	
		float yt=Float.parseFloat(((Element)target.getChildNodes().item(0)).getAttribute("y"));

		float coordinate=(xs+xt)/2;
		mxPoint.setAttribute("x", coordinate+"");

		coordinate=(ys+yt)/2;
		mxPoint.setAttribute("y", coordinate+"");
		pointArray.appendChild(mxPoint);

		mxGeometry1.appendChild(pointArray);
		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
	}

	private static void createElementBasicState(Document doc, Element root, String id, Integer principal, BasicState<String> bs)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");

		Element mxcell1 = doc.createElement("mxCell");

		mxcell1.setAttributeNode(parent);

		Attr id1=doc.createAttribute("id");		
		id1.setValue(id);
		mxcell1.setAttributeNode(id1);

		mxcell1.setAttribute("value", "principal="+principal+
				",label="+bs.getState()+((bs.isFinalState())?",final=true":"")+((bs.isInitial())?",initial=true":""));


		root.appendChild(mxcell1);
	}

	private static Element createElementState(Document doc, Element root,String id, State<String> castate)
	{
		Attr parent=doc.createAttribute("parent");
		parent.setValue("1");
		Attr style=doc.createAttribute("style");
		
		if (castate.isFinalState())
		{
			if (castate.isInitial())
				style.setValue(MxState.initialfinalnodestylevalue);
			else
				style.setValue(MxState.finalnodestylevalue);
		}
		else 
		{
			if (castate.isInitial())
				style.setValue(MxState.initialnodestylevalue);			
			else
				style.setValue(MxState.nodestylevalue);				
		}

		Attr value=doc.createAttribute("value");
		value.setValue(castate.getState().stream()
				.map(BasicState<String>::getState)
				.collect(Collectors.toList()).toString());

		Element mxcell1 = doc.createElement("mxCell");
		Attr id1=doc.createAttribute("id");
		Attr vertex=doc.createAttribute("vertex");
		vertex.setNodeValue("1");		
		id1.setValue(id);
		mxcell1.setAttributeNode(id1);
		mxcell1.setAttributeNode(parent);
		mxcell1.setAttributeNode(style);
		mxcell1.setAttributeNode(value);
		mxcell1.setAttributeNode(vertex);

		Element mxGeometry1=doc.createElement("mxGeometry");
		Attr as=doc.createAttribute("as");
		as.setValue("geometry");
		mxGeometry1.setAttributeNode(as);

		double incrementedWidth = 40 + MxState.initialStateWidthIncrement;
		mxGeometry1.setAttribute("width", (castate.isInitial())?incrementedWidth+"":"40.0");
		mxGeometry1.setAttribute("height", "40.0");



		//createElement does not set attributes x and y
		//		if (!mxGeometry1.hasAttribute("x"))
		//		{
		Attr x=doc.createAttribute("x");
		x.setNodeValue(((castate instanceof MxState)?((MxState)castate).getX():0.0)+"");
		mxGeometry1.setAttributeNode(x);

		//		}
		//		else
		//			mxGeometry1.setAttribute("x", castate.getX()+"");

		//		if (!mxGeometry1.hasAttribute("y"))
		//		{
		Attr y=doc.createAttribute("y");
		y.setNodeValue(((castate instanceof MxState)?((MxState)castate).getY():0.0)+"");
		mxGeometry1.setAttributeNode(y);

		//		}
		//		else
		//			mxGeometry1.setAttribute("y", castate.getY()+"");

		mxcell1.appendChild(mxGeometry1);
		root.appendChild(mxcell1);
		return mxcell1;		
	}
}

