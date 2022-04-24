package io.github.contractautomata.catapptest.convertersTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;

import io.github.contractautomata.catapp.castate.MxState;
import io.github.contractautomata.catapp.converters.MxeConverter;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutConverter;
import io.github.contractautomata.catlib.converters.AutDataConverter;

public class MxeConverterTest {
	
	@Before
	public void setup() {
		MxState.setShapes();
	}
	
	private final AutConverter<Automaton<String, Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>,Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> bmc = new MxeConverter();
	private final String dir = System.getProperty("user.dir")+File.separator+"test"+File.separator
			+"io"+File.separator+"github"+File.separator+"contractautomataproject"+File.separator+"catapptest"
			+File.separator+"resources"+File.separator;
	
	@Test
	public void parseAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if there are different objects for the same basic state
		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bmc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe");

		assertFalse(aut.getStates().stream()
				.flatMap(cs -> cs.getState().stream()
						.map(bs -> new AbstractMap.SimpleEntry<>(cs.getState().indexOf(bs), bs)))
				.anyMatch(e1 -> aut.getStates()
						.stream()
						.map(cs -> cs.getState().get(e1.getKey())).anyMatch(bs -> bs != e1.getValue() && bs.getState().equals(e1.getValue().getState()))));
	}

	
	@Test
	public void conversionXMLtestSCP2020_BusinessClientxHotel() throws Exception {
		//check if by converting and parsing the automaton does not change
		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp= bmc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe");
        try {
            bmc.exportMSCA(dir+"test.mxe",comp);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test=bmc.importMSCA(dir+"test.mxe");

		assertTrue(checkTransitions(comp, test));
	}
	
	@Test
	public void parse_noxy() throws Exception {
		//check if by parsing and printing the automaton does not change
		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bmc.importMSCA(dir+"test_parse_noxy.mxe");
        bmc.exportMSCA(dir+"test_parse_withxy.mxe",aut);

        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bmc.importMSCA(dir+"test_parse_withxy.mxe");
		assertTrue(checkTransitions(aut, test));

	}
	
	@Test
	public void importProvola() throws Exception {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = new AutDataConverter<>(CALabel::new).importMSCA(dir+"provola.data");
		bmc.exportMSCA(dir+"provola.mxe", aut);
    }
	
	//****************************Exceptions**********************************
	
	@Test
	public void importMXENewPrincipalNoBasicStates() {
		assertThatThrownBy(() -> bmc.importMSCA(dir+"test_newPrincipalWithNoBasicStates.mxe"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("source, label or target with different ranks");
	}
	

	
	
	@Test
	public void parseDuplicateStates_exception() throws NumberFormatException {
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Duplicate states!");
	}

	@Test
	public void parseIllActions_exception() throws NumberFormatException {
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed2.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("The label is not well formed");
	}
	
	@Test
	public void parseNoFinalStates_exception() throws NumberFormatException {
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed3.mxe"))
	    .isInstanceOf(IllegalArgumentException.class) //IOException.class)
	    .hasMessageContaining("No Final States!");
	}
	
	@Test
	public void parseEmptyElements_exception() throws NumberFormatException {
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed4.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("No states!");
	}
	
	@Test
	public void parseWrongFinalStates_exception() throws NumberFormatException {
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed5.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Problems with final states in .mxe");
	}

	@Test
	public void parseMxeDuplicateBasicStates() throws NumberFormatException {
		
		assertThatThrownBy(() -> bmc.importMSCA(dir+"illformed_duplicatebasicstates.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Duplicate basic states labels");
	}


	public static boolean checkTransitions(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut, Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test) {
		Set<String> autTr=aut.getTransition().parallelStream()
				.map(ModalTransition::toString)
				.collect(Collectors.toSet());
		Set<String> testTr=test.getTransition().parallelStream()
				.map(ModalTransition::toString)
				.collect(Collectors.toSet());
		return autTr.parallelStream()
				.allMatch(testTr::contains)
				&&
				testTr.parallelStream()
				.allMatch(autTr::contains);
	}

}