package io.github.davidebasile.catapptest.convertersTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import castate.MxCAState;
import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.converters.MSCAConverter;
import converters.MxeConverter;

public class MxeConverterTest {
	
	@Before
	public void setup() {
		MxCAState.setShapes();
	}
	
	private final MSCAConverter bmc = new MxeConverter();
	private final String dir = System.getProperty("user.dir")+File.separator+"test"+File.separator
			+"io"+File.separator+"github"+File.separator+"davidebasile"+File.separator+"catapptest"
			+File.separator+"resources"+File.separator;
	
	@Test
	public void parseAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if there are different objects for the same basic state
		
		MSCA aut = bmc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe");

		assertEquals(aut.getStates().stream()
		.flatMap(cs->cs.getState().stream()
				.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
		.anyMatch(e1->aut.getStates()
				.stream()
				.map(cs->cs.getState().get(e1.getKey()))
				.filter(bs->bs!=e1.getValue()&&bs.getState().equals(e1.getValue().getState()))
				.count()>0),false);
	}

	
	@Test
	public void conversionXMLtestSCP2020_BusinessClientxHotel() throws Exception, TransformerException {
		//check if by converting and parsing the automaton does not change
		
		MSCA comp= bmc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe");			
		bmc.exportMSCA(dir+"test.mxe",comp);
		MSCA test=bmc.importMSCA(dir+"test.mxe");

		assertEquals(checkTransitions(comp,test),true);
	}
	
	@Test
	public void parse_noxy() throws Exception, TransformerException {		
		//check if by parsing and printing the automaton does not change
		
		MSCA aut = bmc.importMSCA(dir+"test_parse_noxy.mxe");
		bmc.exportMSCA(dir+"test_parse_withxy.mxe",aut);

		MSCA test = bmc.importMSCA(dir+"test_parse_withxy.mxe");
		assertEquals(checkTransitions(aut,test),true);

	}
	
	//****************************Exceptions**********************************
	
	@Test
	public void importMXENewPrincipalNoBasicStates() throws Exception {
		

		assertThatThrownBy(() -> bmc.importMSCA(dir+"test_newPrincipalWithNoBasicStates.mxe"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("source, label or target with different ranks");
	}
	

	
	
	@Test
	public void parseDuplicateStates_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Duplicate states!");
	}

	@Test
	public void parseIllActions_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed2.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed action");
	}
	
	@Test
	public void parseNoFinalStates_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed3.mxe"))
	    .isInstanceOf(IllegalArgumentException.class) //IOException.class)
	    .hasMessageContaining("No Final States!");
	}
	
	@Test
	public void parseEmptyElements_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed4.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("No states!");
	}
	
	@Test
	public void parseWrongFinalStates_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed5.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Problems with final states in .mxe");
	}

	@Test
	public void parseMxeDuplicateBasicStates() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed_duplicatebasicstates.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Duplicate basic states labels");
	}


	public static boolean checkTransitions(MSCA aut, MSCA test) {
		Set<String> autTr=aut.getTransition().parallelStream()
				.map(t->t.toCSV())
				.collect(Collectors.toSet());
		Set<String> testTr=test.getTransition().parallelStream()
				.map(t->t.toCSV())
				.collect(Collectors.toSet());
		return autTr.parallelStream()
				.allMatch(t->testTr.contains(t))
				&&
				testTr.parallelStream()
				.allMatch(t->autTr.contains(t));
	}
}