package com.proForma;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;

public class ProFormaParserTest {
	public static ProFormaParser parser = new ProFormaParser();

	@Test(expected = IllegalArgumentException.class)
	public void invalidProFormaEmptyStrings() {
		parser.parseString("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidProFormaNullStrings() {
		parser.parseString(null);
	}

	@Test
	public void noModifications() {
		final String proFormaString = "PROTEOFORM";
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals(proFormaString, term.sequence);
		assertEquals(0, term.tags.size());
	}

	@Test
	public void simpleInfoTag() {
		// Test Case 1
		String proFormaString = "PRO[info:test]TEOFORM";
		String sequence = "PROTEOFORM";
		String value = "test";
		ProFormaTerm term = parser.parseString(proFormaString);
		assertEquals(sequence, term.sequence);
		assertNotNull(term.tags);
		assertEquals(1, term.tags.size());
		assertEquals(2, term.tags.get(0).zeroBasedStartIndex);
		assertEquals(1, term.tags.get(0).descriptors.size());
		assertEquals(ProFormaKey.INFO, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals(value, term.tags.get(0).descriptors.get(0).getValue());

		// Test Case 2
		proFormaString = "PRO[info:test[nested]]TEOFORM";
		sequence = "PROTEOFORM";
		value = "test[nested]";
		term = parser.parseString(proFormaString);
		assertEquals(sequence, term.sequence);
		assertNotNull(term.tags);
		assertEquals(1, term.tags.size());
		assertEquals(2, term.tags.get(0).zeroBasedStartIndex);
		assertEquals(1, term.tags.get(0).descriptors.size());
		assertEquals(ProFormaKey.INFO, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals(value, term.tags.get(0).descriptors.get(0).getValue());
	}

	@Test
	public void handleExtraTagSpaces() {
		ProFormaTerm term = parser.parseString("PRO[info:test]TEOFORM");
		assertEquals(ProFormaKey.INFO, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals("test", term.tags.get(0).descriptors.get(0).getValue());

		// Trim extra spaces from beginning of the descriptor
		term = parser.parseString("PRO[ info:test]TEOFORM");
		assertEquals(ProFormaKey.INFO, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals("test", term.tags.get(0).descriptors.get(0).getValue());

		term = parser.parseString("PRO[info:test ]TEOFORM");
		assertEquals(ProFormaKey.INFO, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals("test ", term.tags.get(0).descriptors.get(0).getValue());

		term = parser.parseString("PRO[     info:test  ]TEOFORM");
		assertEquals(ProFormaKey.INFO, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals("test  ", term.tags.get(0).descriptors.get(0).getValue());

		// Keep everything after the colon
		term = parser.parseString("PRO[info: test]TEOFORM");
		assertEquals(ProFormaKey.INFO, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals(" test", term.tags.get(0).descriptors.get(0).getValue());
	}

	@Test
	public void multipleDescriptorTag() {
		// Tweaking for v2
		final String proFormaString = "SEQUEN[Methyl|+14.02]CE";
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals("SEQUENCE", term.sequence);
		assertNotNull(term.tags);
		assertEquals(1, term.tags.stream().count());

		ProFormaTag tag = term.tags.get(0);
		assertEquals(5, tag.zeroBasedStartIndex);
		assertEquals(2, tag.descriptors.stream().count());

		assertEquals(ProFormaKey.NAME, tag.descriptors.get(0).getKey());
		assertEquals("Methyl", tag.descriptors.get(0).getValue());
		assertEquals(ProFormaKey.MASS, tag.descriptors.get(1).getKey());
		assertEquals("+14.02", tag.descriptors.get(1).getValue());
	}

	@Test
	public void valueOnlyDescriptor() {
		// Test Case 1
		String proFormaString = "PRO[Methyl]TEOFORM";
		String sequence = "PROTEOFORM";
		String modName = "Methyl";
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals(sequence, term.sequence);
		assertNotNull(term.tags);
		assertEquals(1, term.tags.stream().count());
		assertEquals(2, term.tags.get(0).zeroBasedStartIndex);
		assertEquals(1, term.tags.get(0).descriptors.stream().count());
		assertEquals(ProFormaKey.NAME, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals(modName, term.tags.get(0).descriptors.get(0).getValue());

		// Test Case 2
		proFormaString = "PRO[Cation:Fe[III]]TEOFORM";
		sequence = "PROTEOFORM";
		modName = "Cation:Fe[III]";
		term = parser.parseString(proFormaString);

		assertEquals(sequence, term.sequence);
		assertNotNull(term.tags);
		assertEquals(1, term.tags.stream().count());
		assertEquals(2, term.tags.get(0).zeroBasedStartIndex);
		assertEquals(1, term.tags.get(0).descriptors.stream().count());
		assertEquals(ProFormaKey.NAME, term.tags.get(0).descriptors.get(0).getKey());
		assertEquals(modName, term.tags.get(0).descriptors.get(0).getValue());
	}

	@Test
	public void mixedDescriptor() {
		// Tweaking for v2
		final String proFormaString = "SEQUEN[Methyl|+14.02]CE";
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals("SEQUENCE", term.sequence);
		assertNotNull(term.tags);
		assertEquals(1, term.tags.stream().count());

		ProFormaTag tag = term.tags.get(0);
		assertEquals(5, tag.zeroBasedStartIndex);
		assertEquals(2, tag.descriptors.stream().count());

		assertEquals(ProFormaKey.NAME, tag.descriptors.get(0).getKey());
		assertEquals("Methyl", tag.descriptors.get(0).getValue());
		assertEquals(ProFormaKey.MASS, tag.descriptors.get(tag.descriptors.size() - 1).getKey());
		assertEquals("+14.02", tag.descriptors.get(tag.descriptors.size() - 1).getValue());
	}

//	@Test
//	// [Ignore("Invalid in v2.")]
//	public void rule6() {
//		final String proFormaString = "[mass]+S[80]EQVE[14]NCE";
//		ProFormaTerm term = parser.parseString(proFormaString);
//
//		assertEquals("SEQVENCE", term.sequence);
//		assertNotNull(term.tags);
//		assertEquals(2, term.tags.stream().count());
//
//		ProFormaTag tag80 = term.tags.get(0);
//		assertEquals(0, tag80.zeroBasedStartIndex);
//		assertEquals(1, tag80.descriptors.stream().count());
//		assertEquals(ProFormaKey.MASS, tag80.descriptors.get(0).getKey());
//		assertEquals("80", tag80.descriptors.get(0).getValue());
//
//		ProFormaTag tag14 = term.tags.get(1);
//		assertEquals(4, tag14.zeroBasedStartIndex);
//		assertEquals(1, tag14.descriptors.stream().count());
//		assertEquals(ProFormaKey.MASS, tag14.descriptors.get(0).getKey());
//		assertEquals("14", tag14.descriptors.get(0).getValue());
//	}

//	@Test
//	// Rule 6 with incompatible tag values
//	// Syntactically valid but logically invalid
//	// Pick up the error at the next level of validation
//	// [Ignore("Invalid in v2.")]
//
//	public void rule6_WithModificationNames() {
//		final String proFormaString = "[mass]+S[Methyl]EQVE[14]NCE";
//		ProFormaTerm term = parser.parseString(proFormaString);
//
//		ProFormaTag tagMethyl = term.tags.get(0);
//		assertEquals(ProFormaKey.MASS, tagMethyl.descriptors.get(0).getKey());
//		assertEquals("Methyl", tagMethyl.descriptors.get(0).getValue());
//
//		ProFormaTag tag14 = term.tags.get(1);
//		assertEquals(ProFormaKey.MASS, tag14.descriptors.get(0).getKey());
//		assertEquals("14", tag14.descriptors.get(0).getValue());
//	}

	@Test
	public void rule7() {
		// Tweaking for v2
		final String proFormaString = "[-17.027]-SEQVENCE-[Amidation]";
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals("SEQVENCE", term.sequence);
		assertEquals(0, term.tags.stream().count());
		assertNotNull(term.nTerminalDescriptors);
		assertEquals(1, term.nTerminalDescriptors.stream().count());
		assertNotNull(term.cTerminalDescriptors);
		assertEquals(1, term.cTerminalDescriptors.stream().count());

		ProFormaDescriptor nTerm = term.nTerminalDescriptors.get(0);
		assertEquals(ProFormaKey.MASS, nTerm.getKey());
		assertEquals("-17.027", nTerm.getValue());

		ProFormaDescriptor cTerm = term.cTerminalDescriptors.get(0);
		assertEquals(ProFormaKey.NAME, cTerm.getKey());
		assertEquals("Amidation", cTerm.getValue());
	}

//	@Test
//	// [Ignore("Invalid in v2.")]
//	public void rule7andRule6() {
//		final String proFormaString = "[mass]+[-17.027]-SEQ[14.05]VENCE";
//		ProFormaTerm term = parser.parseString(proFormaString);
//
//		assertEquals("SEQVENCE", term.sequence);
//		assertNotNull(term.tags);
//		assertEquals(1, term.tags.stream().count());
//		assertNotNull(term.nTerminalDescriptors);
//		assertEquals(1, term.nTerminalDescriptors.stream().count());
//		assertNull(term.cTerminalDescriptors);
//
//		ProFormaDescriptor nTerm = term.nTerminalDescriptors.get(0);
//		assertEquals(ProFormaKey.MASS, nTerm.getKey());
//		assertEquals("-17.027", nTerm.getValue());
//
//		ProFormaTag tag1 = term.tags.get(0);
//		assertEquals(2, tag1.zeroBasedStartIndex);
//		assertEquals(1, tag1.descriptors.stream().count());
//		assertEquals(ProFormaKey.MASS, tag1.descriptors.get(0).getKey());
//		assertEquals("14.05", tag1.descriptors.get(0).getValue());
//	}

//	@Test
//	// [Ignore("Invalid in v2.")]
//	// [TestCase("[mass]+S[mod:Methyl]EQVE[14]NCE")]
//	// [TestCase("[mass]+[mod:Methyl]-SEQVENCE")]
//	// [TestCase("[Methyl]-[mass]+SEQ[14.05]VENCE")]
//	public void rule6_7_Invalid(String proFormaString) {
//		assertThrows(ProFormaParseException.class, () -> {
//			parser.parseString(proFormaString);
//		});
//	}

	@Test(expected = ProFormaParseException.class)
	// Terminal mods must be adjacent to sequence
	public void ambiguityRulesInvalidTestCase1() {
		parser.parseString("[Acetyl]-[Phospho]?PROTEOFORM");
	}

	@Test(expected = ProFormaParseException.class)
	// Empty group string
	public void ambiguityRulesInvalidTestCase2() {
		parser.parseString("PROT[Phospho|#]EOFORMS[#]");
	}

	@Test
	public void bestPractice_i() {
		// Tweaking for v2
		final String proFormaString = "[Acetyl]-S[Phospho|+79.966331]GRGK[Acetyl|UNIMOD:1|+42.010565]QGGKARAKAKTRSSRAGLQFPVGRVHRLLRKGNYAERVGAGAPVYLAAVLEYLTAEILELAGNAARDNKKTRIIPRHLQLAIRNDEELNKLLGKVTIAQGGVLPNIQAVLLPKKT[UNIMOD:21]ESHHKAKGK";
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals(
				"SGRGKQGGKARAKAKTRSSRAGLQFPVGRVHRLLRKGNYAERVGAGAPVYLAAVLEYLTAEILELAGNAARDNKKTRIIPRHLQLAIRNDEELNKLLGKVTIAQGGVLPNIQAVLLPKKTESHHKAKGK",
				term.sequence);
		assertNotNull(term.tags);
		assertEquals(3, term.tags.stream().count());
		assertNotNull(term.nTerminalDescriptors);
		assertEquals(1, term.nTerminalDescriptors.stream().count());
		assertEquals(0, term.cTerminalDescriptors.size());

		ProFormaDescriptor nTerm = term.nTerminalDescriptors.get(0);
		assertEquals(ProFormaKey.NAME, nTerm.getKey());
		assertEquals("Acetyl", nTerm.getValue());

		ProFormaTag tag1 = term.tags.get(0);
		assertEquals(0, tag1.zeroBasedStartIndex);
		assertEquals(2, tag1.descriptors.stream().count());
		assertEquals(ProFormaKey.NAME, tag1.descriptors.get(0).getKey());
		assertEquals("Phospho", tag1.descriptors.get(0).getValue());
		assertEquals(ProFormaKey.MASS, tag1.descriptors.get(tag1.descriptors.size() - 1).getKey());
		assertEquals("+79.966331", tag1.descriptors.get(tag1.descriptors.size() - 1).getValue());

		ProFormaTag tag5 = term.tags.get(1);
		assertEquals(4, tag5.zeroBasedStartIndex);
		assertEquals(3, tag5.descriptors.stream().count());
		assertEquals(ProFormaKey.NAME, tag5.descriptors.get(0).getKey());
		assertEquals("Acetyl", tag5.descriptors.get(0).getValue());
		assertEquals(ProFormaKey.IDENTIFIER, tag5.descriptors.get(1).getKey());
		assertEquals(ProFormaEvidenceType.UNIMOD, tag5.descriptors.get(1).getEvidenceType());
		assertEquals("UNIMOD:1", tag5.descriptors.get(1).getValue());
		assertEquals(ProFormaKey.MASS, tag5.descriptors.get(2).getKey());
		assertEquals("+42.010565", tag5.descriptors.get(2).getValue());

		ProFormaTag tag120 = term.tags.get(2);
		assertEquals(119, tag120.zeroBasedStartIndex);
		assertEquals(1, tag120.descriptors.stream().count());
		assertEquals(ProFormaKey.IDENTIFIER, tag120.descriptors.get(0).getKey());
		assertEquals(ProFormaEvidenceType.UNIMOD, tag120.descriptors.get(0).getEvidenceType());
		assertEquals("UNIMOD:21", tag120.descriptors.get(0).getValue());
	}

//	@Test
//	// [Ignore("Invalid in v2.")]
//	public void bestPractice_ii() {
//		final String proFormaString = "[Unimod]+[1]-S[21]GRGK[1]QGGKARAKAKTRSSRAGKVTIAQGGVLPNIQAVLLPKKT[21]ESHHKAKGK";
//		ProFormaTerm term = parser.parseString(proFormaString);
//
//		assertEquals("SGRGKQGGKARAKAKTRSSRAGKVTIAQGGVLPNIQAVLLPKKTESHHKAKGK", term.sequence);
//		assertNotNull(term.tags);
//		assertEquals(3, term.tags.stream().count());
//		assertNotNull(term.nTerminalDescriptors);
//		assertEquals(1, term.nTerminalDescriptors.stream().count());
//		assertNull(term.cTerminalDescriptors);
//
//		ProFormaDescriptor nTerm = term.nTerminalDescriptors.get(0);
//		assertEquals(ProFormaKey.IDENTIFIER, nTerm.getKey());
//		assertEquals(ProFormaEvidenceType.UNIMOD, nTerm.getEvidenceType());
//		assertEquals("1", nTerm.getValue());
//
//		ProFormaTag tag1 = term.tags.get(0);
//		assertEquals(0, tag1.zeroBasedStartIndex);
//		assertEquals(1, tag1.descriptors.stream().count());
//		assertEquals(ProFormaKey.IDENTIFIER, tag1.descriptors.get(0).getKey());
//		assertEquals(ProFormaEvidenceType.UNIMOD, tag1.descriptors.get(0).getEvidenceType());
//		assertEquals("21", tag1.descriptors.get(0).getValue());
//
//		ProFormaTag tag5 = term.tags.get(1);
//		assertEquals(4, tag5.zeroBasedStartIndex);
//		assertEquals(1, tag5.descriptors.stream().count());
//		assertEquals(ProFormaKey.IDENTIFIER, tag5.descriptors.get(0).getKey());
//		assertEquals(ProFormaEvidenceType.UNIMOD, tag5.descriptors.get(0).getEvidenceType());
//		assertEquals("1", tag5.descriptors.get(0).getValue());
//
//		ProFormaTag tag44 = term.tags.get(2);
//		assertEquals(43, tag44.zeroBasedStartIndex);
//		assertEquals(1, tag44.descriptors.stream().count());
//		assertEquals(ProFormaKey.IDENTIFIER, tag44.descriptors.get(0).getKey());
//		assertEquals(ProFormaEvidenceType.UNIMOD, tag44.descriptors.get(0).getEvidenceType());
//		assertEquals("21", tag44.descriptors.get(0).getValue());
//	}

	@Test
	public void bestPractice_iii() {
		// Tweaking for v2
		final String proFormaString = "MTLFQLREHWFVYKDDEKLTAFRNK[p-adenosine|R:N6-(phospho-5'-adenosine)-L-lysine| RESID:AA0227| MOD:00232| N6AMPLys]SMLFQRELRPNEEVTWK";
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals("MTLFQLREHWFVYKDDEKLTAFRNKSMLFQRELRPNEEVTWK", term.sequence);
		assertNotNull(term.tags);
		assertEquals(1, term.tags.stream().count());
		assertEquals(0, term.nTerminalDescriptors.size());
		assertEquals(0, term.cTerminalDescriptors.size());

		ProFormaTag tag25 = term.tags.get(0);
		assertEquals(24, tag25.zeroBasedStartIndex);
		assertEquals(5, tag25.descriptors.stream().count());
		assertEquals(ProFormaKey.NAME, tag25.descriptors.get(0).getKey());
		assertEquals("p-adenosine", tag25.descriptors.get(0).getValue());
		assertEquals(ProFormaKey.NAME, tag25.descriptors.get(1).getKey());
		assertEquals(ProFormaEvidenceType.RESID, tag25.descriptors.get(1).getEvidenceType());
		assertEquals("N6-(phospho-5'-adenosine)-L-lysine", tag25.descriptors.get(1).getValue());
		assertEquals(ProFormaKey.IDENTIFIER, tag25.descriptors.get(2).getKey());
		assertEquals(ProFormaEvidenceType.RESID, tag25.descriptors.get(2).getEvidenceType());
		assertEquals("AA0227", tag25.descriptors.get(2).getValue());
		assertEquals(ProFormaKey.IDENTIFIER, tag25.descriptors.get(3).getKey());
		assertEquals(ProFormaEvidenceType.PSIMOD, tag25.descriptors.get(3).getEvidenceType());
		assertEquals("MOD:00232", tag25.descriptors.get(3).getValue());
		assertEquals(ProFormaKey.NAME, tag25.descriptors.get(4).getKey());
		assertEquals("N6AMPLys", tag25.descriptors.get(4).getValue());
	}

	@Test
	public void bestPractice_iv() {
		// Tweaking for v2
		final String proFormaString = "MTLFQLDEKLTA[-37.995001|info:unknown modification]FRNKSMLFQRELRPNEEVTWK";
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals("MTLFQLDEKLTAFRNKSMLFQRELRPNEEVTWK", term.sequence);
		assertNotNull(term.tags);
		assertEquals(1, term.tags.stream().count());
		assertEquals(0, term.nTerminalDescriptors.size());
		assertEquals(0, term.cTerminalDescriptors.size());

		ProFormaTag tag12 = term.tags.get(0);
		assertEquals(11, tag12.zeroBasedStartIndex);
		assertEquals(2, tag12.descriptors.stream().count());
		// Unimod
		assertEquals(ProFormaKey.MASS, tag12.descriptors.get(0).getKey());
		assertEquals("-37.995001", tag12.descriptors.get(0).getValue());
		// RESID
		assertEquals(ProFormaKey.INFO, tag12.descriptors.get(1).getKey());
		assertEquals("unknown modification", tag12.descriptors.get(1).getValue());
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase1() {
		// "Cannot have an empty descriptor."
		parser.parseString("PRO[]TEOFORM");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase2() {
		// "Cannot have an empty descriptor."
		parser.parseString("PRO[mod:Methyl|]TEOFORM");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase3() {
		// " is not an upper case letter."
		parser.parseString("PRO[mod:jk :] lol]TEOFORM");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase4() {
		parser.parseString("PRO[fake:Formaldehyde]TEOFORM");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase5() {
		parser.parseString("PROTEOFXRM");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase6() {
		// "} is not an upper case letter."
		parser.parseString("{Name}}PROTEOFORM");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase7() {
		// "There are 1 open braces in ProForma string {Name{}PROTEOFORM"
		parser.parseString("{Name{}PROTEOFORM");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase8() {
		// "@ is not an upper case letter."
		parser.parseString("PROTEOF@RM");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase9() {
		// "P is not an upper case letter."
		parser.parseString("proteoform");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase10() {
		// " is not an upper case letter."
		parser.parseString(" ");
	}

	@Test(expected = ProFormaParseException.class)
	public void badInputTestCase11() {
		// "- at index 1 is not allowed"
		parser.parseString("----");
	}

	@Test
	// #region Version 2.0 Tests
	public void modificationNameUsage_4_2_1() {
		// Test Case 1
		String proFormaString = "EM[Oxidation]EVEES[Phospho]PEK";
		ProFormaTerm term = parser.parseString(proFormaString);
		assertEquals("EMEVEESPEK", term.sequence);
		assertEquals(2, term.tags.stream().count());

		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(0);
		ProFormaDescriptor desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());

		assertEquals(ProFormaKey.NAME, desc2.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc2.getEvidenceType());

		// Test Case 2
		proFormaString = "EM[L-methionine sulfoxide]EVEES[O-phospho-L-serine]PEK";
		term = parser.parseString(proFormaString);
		assertEquals("EMEVEESPEK", term.sequence);
		assertEquals(2, term.tags.stream().count());

		desc1 = term.tags.get(0).descriptors.get(0);
		desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());

		assertEquals(ProFormaKey.NAME, desc2.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc2.getEvidenceType());

		// Test Case 3
		proFormaString = "EM[Oxidation]EVEES[Cation:Mg[II]]PEK";
		term = parser.parseString(proFormaString);
		assertEquals("EMEVEESPEK", term.sequence);
		assertEquals(2, term.tags.stream().count());

		desc1 = term.tags.get(0).descriptors.get(0);
		desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());

		assertEquals(ProFormaKey.NAME, desc2.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc2.getEvidenceType());
	}

	@Test
	public void modificationNameUsage_4_2_1_Prefixes() {
		// RESID is R
		ProFormaTerm term = parser.parseString("EM[R:Methionine sulfone]EVEES[O-phospho-L-serine]PEK");
		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(0);
		ProFormaDescriptor desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.RESID, desc1.getEvidenceType());
		assertEquals("Methionine sulfone", desc1.getValue());

		assertEquals(ProFormaKey.NAME, desc2.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc2.getEvidenceType());

		// XL-MOD is X
		term = parser.parseString("EMEVTK[X:DSS#XL1]SESPEK");
		ProFormaTagGroup tag1 = term.tagGroups.get(0);

		assertEquals(ProFormaKey.NAME, tag1.getKey());
		assertEquals(ProFormaEvidenceType.XLMOD, tag1.getEvidenceType());
		assertEquals("DSS", tag1.getValue());

		// GNO is G
		term = parser.parseString("NEEYN[G:G59626AS]K");
		desc1 = term.tags.get(0).descriptors.get(0);

		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.GNO, desc1.getEvidenceType());
		assertEquals("G59626AS", desc1.getValue());
	}

	// TODO: 4.2.1.1 -> Validation (not parsing)

	@Test
	public void modificationAccessionNumbers_4_2_2TestCase1() {
		// Test Case 1
		String proFormaString = "EM[MOD:00719]EVEES[MOD:00046]PEK";
		ProFormaEvidenceType modType = ProFormaEvidenceType.PSIMOD;
		ProFormaTerm term = parser.parseString(proFormaString);

		assertEquals("EMEVEESPEK", term.sequence);
		assertEquals(2, term.tags.stream().count());

		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(0);
		ProFormaDescriptor desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(modType, desc1.getEvidenceType());
		assertEquals(modType, desc2.getEvidenceType());

		// Test Case 2
		proFormaString = "EM[UNIMOD:15]EVEES[UNIMOD:56]PEK";
		modType = ProFormaEvidenceType.UNIMOD;
		term = parser.parseString(proFormaString);

		assertEquals("EMEVEESPEK", term.sequence);
		assertEquals(2, term.tags.stream().count());

		desc1 = term.tags.get(0).descriptors.get(0);
		desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(modType, desc1.getEvidenceType());
		assertEquals(modType, desc2.getEvidenceType());

		// Test Case 3
		proFormaString = "EM[RESID:AA0581]EVEES[RESID:AA0037]PEK";
		modType = ProFormaEvidenceType.RESID;
		term = parser.parseString(proFormaString);

		assertEquals("EMEVEESPEK", term.sequence);
		assertEquals(2, term.tags.stream().count());

		desc1 = term.tags.get(0).descriptors.get(0);
		desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(modType, desc1.getEvidenceType());
		assertEquals(modType, desc2.getEvidenceType());
	}

	@Test
	public void crosslinkers_XL_MOD_4_2_3() {
		// Single group ProFormaTerm
		ProFormaTerm term = parser.parseString("EMEVTK[XLMOD:02001#XL1]SESPEK[#XL1]");
		assertEquals(0, term.tags.size());
		assertEquals(1, term.tagGroups.stream().count());

		ProFormaTagGroup tagGroup = term.tagGroups.get(0);

		assertEquals("XL1", tagGroup.name);
		assertEquals(ProFormaKey.IDENTIFIER, tagGroup.getKey());
		assertEquals(ProFormaEvidenceType.XLMOD, tagGroup.getEvidenceType());
		assertEquals("XLMOD:02001", tagGroup.getValue());
		assertEquals(2, tagGroup.members.stream().count());
		assertEquals(5, tagGroup.members.get(0).zeroBasedStartIndex);
		assertEquals(11, tagGroup.members.get(1).zeroBasedStartIndex);

		// Multiple groups
		term = parser.parseString("EMK[XLMOD:02000#XL1]EVTK[XLMOD:02001#XL2]SESK[#XL1]PEK[#XL2]");
		assertEquals(0, term.tags.size());
		assertEquals(2, term.tagGroups.stream().count());

		ProFormaTagGroup tagGroup1 = term.tagGroups.stream().filter(tg -> tg.getName().contentEquals("XL1"))
				.collect(Collectors.toList()).get(0);
		ProFormaTagGroup tagGroup2 = term.tagGroups.stream().filter(tg -> tg.getName().contentEquals("XL2"))
				.collect(Collectors.toList()).get(0);

		assertEquals("XL1", tagGroup1.getName());
		assertEquals(ProFormaKey.IDENTIFIER, tagGroup1.getKey());
		assertEquals(ProFormaEvidenceType.XLMOD, tagGroup1.getEvidenceType());
		assertEquals("XLMOD:02000", tagGroup1.getValue());
		assertEquals(2, tagGroup1.members.stream().count());
		assertEquals(2, tagGroup1.members.get(0).zeroBasedStartIndex);
		assertEquals(10, tagGroup1.members.get(1).zeroBasedStartIndex);

		assertEquals("XL2", tagGroup2.name);
		assertEquals(ProFormaKey.IDENTIFIER, tagGroup2.getKey());
		assertEquals(ProFormaEvidenceType.XLMOD, tagGroup2.getEvidenceType());
		assertEquals("XLMOD:02001", tagGroup2.getValue());
		assertEquals(2, tagGroup2.members.stream().count());
		assertEquals(6, tagGroup2.members.get(0).zeroBasedStartIndex);
		assertEquals(13, tagGroup2.members.get(1).zeroBasedStartIndex);

		// "Dead end" crosslinks term =
		term = parser.parseString("EMEVTK[XLMOD:02001#XL1]SESPEK");
		assertEquals(0, term.tags.size());
		assertEquals(1, term.tagGroups.stream().count());

		tagGroup = term.tagGroups.get(0);

		assertEquals("XL1", tagGroup.name);
		assertEquals(ProFormaKey.IDENTIFIER, tagGroup.getKey());
		assertEquals(ProFormaEvidenceType.XLMOD, tagGroup.getEvidenceType());
		assertEquals("XLMOD:02001", tagGroup.getValue());
		assertEquals(1, tagGroup.members.stream().count());
		assertEquals(5, tagGroup.members.get(0).zeroBasedStartIndex);

	}

	@Test(expected = ProFormaParseException.class)
	public void crosslinks_4_2_3_No_InterchainTestCase1() {
		// "// is not an upper case letter."
		parser.parseString("SEK[XLMOD:02001#XL1]UENCE\\EMEVTK[XLMOD:02001#XL1]SESPEK");
	}

	@Test(expected = ProFormaParseException.class)
	public void crosslinks_4_2_3_No_InterchainTestCase2() {
		// "// is not an upper case letter."
		parser.parseString("SEK[XLMOD:02001#XL1]UENCE\\EMEVTK[#XL1]SESPEK");
	}

	@Test
	public void crosslinks_4_2_3_Disulfides() {
		// Test Case 1
		String proFormaString = "EVTSEKC[MOD:00034#XL1]LEMSC[#XL1]EFD";
		ProFormaKey proFormaKey = ProFormaKey.IDENTIFIER;
		ProFormaEvidenceType evidenceType = ProFormaEvidenceType.PSIMOD;
		String value = "MOD:00034";

		ProFormaTerm term = parser.parseString(proFormaString);
		assertEquals(0, term.tags.size());
		assertEquals(1, term.tagGroups.stream().count());

		ProFormaTagGroup tagGroup = term.tagGroups.get(0);

		assertEquals("XL1", tagGroup.name);
		assertEquals(proFormaKey, tagGroup.getKey());
		assertEquals(evidenceType, tagGroup.getEvidenceType());
		assertEquals(value, tagGroup.getValue());
		assertEquals(2, tagGroup.members.stream().count());
		assertEquals(6, tagGroup.members.get(0).zeroBasedStartIndex);
		assertEquals(11, tagGroup.members.get(1).zeroBasedStartIndex);

		// Test Case 2
		proFormaString = "EVTSEKC[L-cystine (cross-link)#XL1]LEMSC[#XL1]EFD";
		proFormaKey = ProFormaKey.NAME;
		evidenceType = ProFormaEvidenceType.NONE;
		value = "L-cystine (cross-link)";

		term = parser.parseString(proFormaString);
		assertEquals(0, term.tags.size());
		assertEquals(1, term.tagGroups.stream().count());

		tagGroup = term.tagGroups.get(0);

		assertEquals("XL1", tagGroup.name);
		assertEquals(proFormaKey, tagGroup.getKey());
		assertEquals(evidenceType, tagGroup.getEvidenceType());
		assertEquals(value, tagGroup.getValue());
		assertEquals(2, tagGroup.members.stream().count());
		assertEquals(6, tagGroup.members.get(0).zeroBasedStartIndex);
		assertEquals(11, tagGroup.members.get(1).zeroBasedStartIndex);

		// Test Case 3
		proFormaString = "EVTSEKC[X:Disulfide#XL1]LEMSC[#XL1]EFD";
		proFormaKey = ProFormaKey.NAME;
		evidenceType = ProFormaEvidenceType.XLMOD;
		value = "Disulfide";

		term = parser.parseString(proFormaString);
		assertEquals(0, term.tags.size());
		assertEquals(1, term.tagGroups.stream().count());

		tagGroup = term.tagGroups.get(0);

		assertEquals("XL1", tagGroup.name);
		assertEquals(proFormaKey, tagGroup.getKey());
		assertEquals(evidenceType, tagGroup.getEvidenceType());
		assertEquals(value, tagGroup.getValue());
		assertEquals(2, tagGroup.members.stream().count());
		assertEquals(6, tagGroup.members.get(0).zeroBasedStartIndex);
		assertEquals(11, tagGroup.members.get(1).zeroBasedStartIndex);
	}

	@Test
	public void crosslinks_4_2_3_Extra_Descriptors() {
		// If a tag with a group contains another descriptor,it is considered to be NOT
		// part of that group
		ProFormaTerm term = parser.parseString("EMEVTK[XLMOD:02001#XL1|info:stuff]SESPEK[#XL1]");
		assertEquals(1, term.tags.stream().count());
		assertEquals(1, term.tagGroups.stream().count());
	}

	@Test
	public void glycans_GNO_MOD_4_2_4() {
		ProFormaTerm term = parser.parseString("YPVLN[GNO:G62765YT]VTMPN[GNO:G02815KT]NSNGKFDK");
		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(0);
		ProFormaDescriptor desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(ProFormaKey.IDENTIFIER, desc1.getKey());
		assertEquals(ProFormaEvidenceType.GNO, desc1.getEvidenceType());
		assertEquals("GNO:G62765YT", desc1.getValue());

		assertEquals(ProFormaKey.IDENTIFIER, desc2.getKey());
		assertEquals(ProFormaEvidenceType.GNO, desc2.getEvidenceType());
		assertEquals("GNO:G02815KT", desc2.getValue());
	}

	@Test
	public void deltaMassNotation_4_2_5() {
		// Add evidence type to descriptor to handle prefixes
		// No prefixes
		ProFormaTerm term = parser.parseString("EM[+15.9949]EVEES[-79.9663]PEK");
		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(0);
		ProFormaDescriptor desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(ProFormaKey.MASS, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("+15.9949", desc1.getValue());

		assertEquals(ProFormaKey.MASS, desc2.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc2.getEvidenceType());
		assertEquals("-79.9663", desc2.getValue());

		// Prefixes
		// TODO: One of these should not validate because these are theoretical masses
		// and must match ontology exactly.
		term = parser.parseString("EM[U:+15.9949]EVEES[M:+79.9663]PEK");
		desc1 = term.tags.get(0).descriptors.get(0);
		desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(ProFormaKey.MASS, desc1.getKey());
		assertEquals(ProFormaEvidenceType.UNIMOD, desc1.getEvidenceType());
		assertEquals("+15.9949", desc1.getValue());

		assertEquals(ProFormaKey.MASS, desc2.getKey());
		assertEquals(ProFormaEvidenceType.PSIMOD, desc2.getEvidenceType());
		assertEquals("+79.9663", desc2.getValue());

		// Observed mass
		term = parser.parseString("EM[U:+15.995]EVEES[Obs:+79.978]PEK");
		desc1 = term.tags.get(0).descriptors.get(0);
		desc2 = term.tags.get(1).descriptors.get(0);

		assertEquals(ProFormaKey.MASS, desc1.getKey());
		assertEquals(ProFormaEvidenceType.UNIMOD, desc1.getEvidenceType());
		assertEquals("+15.995", desc1.getValue());

		assertEquals(ProFormaKey.MASS, desc2.getKey());
		assertEquals(ProFormaEvidenceType.OBSERVED, desc2.getEvidenceType());
		assertEquals("+79.978", desc2.getValue());
	}

	@Test
	public void gapOfKnownMass_4_2_6() {
		// Parse straight, consider some validation change
		// e.g. force a mass to be specified, etc.
		ProFormaTerm term = parser.parseString("RTAAX[+367.0537]WT");

		assertEquals(1, term.tags.stream().count());
		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(0);

		assertEquals(ProFormaKey.MASS, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("+367.0537", desc1.getValue());
	}

	@Test
	public void chemicalFormulas_4_2_7() {
		ProFormaTerm term = parser.parseString("SEQUEN[Formula:C12H20O2]CE");
		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(0);

		assertEquals(ProFormaKey.FORMULA, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("C12H20O2", desc1.getValue());

		// TODO: Make sure all of these cases below are handled in the formula parser
		// and validation

		// SEQUEN[Formula:C12 H20 O2]CE
		// SEQUEN[Formula:HN-1O2]CE
		// SEQUEN[Formula:[13C2][12C-2]H2N]CE
		// SEQUEN[Formula:[13C2]C-2H2N]CE
	}

	@Test
	public void glycanComposition_4_2_8() {
		ProFormaTerm term = parser.parseString("SEQUEN[Glycan:HexNAc1Hex2]CE");
		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(0);

		assertEquals(ProFormaKey.GLYCAN, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("HexNAc1Hex2", desc1.getValue());
	}

	@Test
	public void terminalModifications_4_3_1() {
		ProFormaTerm term = parser.parseString("[iTRAQ4plex]-EM[Hydroxylation]EVNES[Phospho]PEK");
		assertEquals(2, term.tags.stream().count());
		assertNotNull(term.nTerminalDescriptors);
		assertEquals(0, term.cTerminalDescriptors.size());

		ProFormaDescriptor desc1 = term.nTerminalDescriptors.get(0);
		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("iTRAQ4plex", desc1.getValue());

		// N and C term
		term = parser.parseString("[iTRAQ4plex]-EM[U:Hydroxylation]EVNES[Phospho]PEK[iTRAQ4plex]-[Methyl]");
		assertEquals(3, term.tags.stream().count());
		assertNotNull(term.nTerminalDescriptors);
		assertNotNull(term.cTerminalDescriptors);

		desc1 = term.nTerminalDescriptors.get(0);
		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("iTRAQ4plex", desc1.getValue());

		desc1 = term.cTerminalDescriptors.get(0);
		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("Methyl", desc1.getValue());

		// Check for using negative delta mass on C terminus
		// Might interfere with the dash notation
		term = parser.parseString("EMEVNESPEK-[-15.9949]");
		assertEquals(0, term.tags.size());
		assertEquals(0, term.nTerminalDescriptors.size());
		assertNotNull(term.cTerminalDescriptors);

		desc1 = term.cTerminalDescriptors.get(0);
		assertEquals(ProFormaKey.MASS, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("-15.9949", desc1.getValue());
	}

	@Test
	public void labileModifications_4_3_2() {
		// Add labile descriptor list to term
		ProFormaTerm term = parser.parseString("{Glycan:Hex}EM[U:Hydroxylation]EVNES[Phospho]PEK[iTRAQ4plex]");
		assertEquals(3, term.tags.stream().count());

		ProFormaDescriptor desc1 = term.labileDescriptors.get(0);

		assertEquals(ProFormaKey.GLYCAN, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("Hex", desc1.getValue());

		assertEquals("Hydroxylation", term.tags.get(0).descriptors.get(0).getValue());

		// Labile and terminal mods
		term = parser.parseString("{Glycan:Hex}[iTRAQ4plex]-EM[Hydroxylation]EVNES[Phospho]PEK[iTRAQ4plex]");
		assertEquals(3, term.tags.stream().count());
		assertNotNull(term.labileDescriptors);
		assertNotNull(term.nTerminalDescriptors);
		assertEquals(0, term.cTerminalDescriptors.size());

		desc1 = term.labileDescriptors.get(0);

		assertEquals(ProFormaKey.GLYCAN, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("Hex", desc1.getValue());

		desc1 = term.nTerminalDescriptors.get(0);

		assertEquals(ProFormaKey.NAME, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("iTRAQ4plex", desc1.getValue()); // Make sure next tag is ok
		assertEquals("Hydroxylation", term.tags.get(0).descriptors.get(0).getValue());
	}

	@Test(expected = ProFormaParseException.class)
	public void ambiguity_UnknownPosition_4_4_1() {
		// Use unlocalized list on term
		ProFormaTerm term = parser.parseString("[Phospho]?EM[Hydroxylation]EVTSESPEK");
		assertEquals(1, term.tags.stream().count());
		assertEquals(1, term.unlocalizedTags.stream().count());

		ProFormaDescriptor tag = term.tags.get(0).descriptors.get(0);
		assertEquals("Hydroxylation", tag.getValue());
		assertEquals(ProFormaKey.NAME, tag.getKey());
		assertEquals(ProFormaEvidenceType.NONE, tag.getEvidenceType());

		ProFormaDescriptor unlocal = term.unlocalizedTags.get(0).getDescriptors().get(0);
		assertEquals(1, term.unlocalizedTags.get(0).getCount());
		assertEquals("Phospho", unlocal.getValue());
		assertEquals(ProFormaKey.NAME, unlocal.getKey());
		assertEquals(ProFormaEvidenceType.NONE, unlocal.getEvidenceType());

		// Check multiple unlocalized mods with a terminal mod
		term = parser.parseString("[Phospho][Phospho2]?[Acetyl]-EM[Hydroxylation]EVTSESPEK");
		assertEquals(1, term.tags.stream().count());
		assertEquals(0, term.cTerminalDescriptors.size());
		assertEquals(1, term.nTerminalDescriptors.stream().count());
		assertEquals(2, term.unlocalizedTags.stream().count());

		ProFormaDescriptor nTerm = term.nTerminalDescriptors.get(0);
		assertEquals("Accetyl", nTerm.getValue());

		unlocal = term.unlocalizedTags.get(0).getDescriptors().get(0);
		assertEquals(1, term.unlocalizedTags.get(0).getCount());
		assertEquals("Phospho", unlocal.getValue());
		ProFormaDescriptor unlocal2 = term.unlocalizedTags.get(term.unlocalizedTags.size() - 1).getDescriptors().get(0);
		assertEquals(1, term.unlocalizedTags.get(term.unlocalizedTags.size() - 1).getCount());
		assertEquals("Phospho2", unlocal2.getValue());

		// Check ^{count} format
		term = parser.parseString("[Phospho]^2[Methyl]?[Acetyl]-EM[Hydroxylation]EVTSESPEK");
		assertEquals(1, term.tags.stream().count());
		assertEquals(0, term.cTerminalDescriptors.size());
		assertEquals(1, term.nTerminalDescriptors.stream().count());
		assertEquals(2, term.unlocalizedTags.stream().count());

		nTerm = term.nTerminalDescriptors.get(0);
		assertEquals("Acetyl", nTerm.getValue());

		unlocal = term.unlocalizedTags.get(0).getDescriptors().get(0);
		assertEquals(2, term.unlocalizedTags.get(0).getCount());
		assertEquals("Phospho", unlocal.getValue());
		unlocal2 = term.unlocalizedTags.get(term.unlocalizedTags.size() - 1).getDescriptors().get(0);
		assertEquals(1, term.unlocalizedTags.get(term.unlocalizedTags.size() - 1).getCount());
		assertEquals("Methyl", unlocal2.getValue());

		// Invalid to have terminal mod before unlocalized mods
		// "Unlocalized modification must come before an N-terminal modification."
		parser.parseString("[Acetyl]-[Phospho]^2?EM[Hydroxylation]EVTSESPEK");
	}

	@Test(expected = ProFormaParseException.class)
	public void ambiguity_PossiblePositions_4_4_2() {
		// Read as a named group 'g1' (indicates that a phosphorylation exists on either
		// T5, S6 or S8)
		ProFormaTerm term = parser.parseString("EM[Oxidation]EVT[#g1]S[#g1]ES[Phospho#g1]PEK");
		assertEquals(0, term.unlocalizedTags.size());
		assertEquals(1, term.tagGroups.stream().count());

		ProFormaTagGroup group = term.tagGroups.get(0);
		assertEquals("g1", group.name);
		assertEquals("Phospho", group.value);
		assertEquals(ProFormaKey.NAME, group.key);
		assertEquals(ProFormaEvidenceType.NONE, group.evidenceType);
		assertEquals(3, group.members.stream().count());

		List<Integer> expectedResult = Arrays.asList(new Integer[] { 4, 5, 7 });
		List<Integer> actualResult = group.getMembers().stream().map(obj -> obj.getZeroBasedStartIndex())
				.collect(Collectors.toList());
		Assert.assertEquals(expectedResult, actualResult);

		// The following example is not valid because a single preferred location must
		// be chosen for a modification
		// "You may only set the value of the group g1 once."
		parser.parseString("[Acetyl]-[Phospho]^2?EM[Hydroxylation]EVTSESPEK");
	}

	@Test
	public void ambiguity_Ranges_4_4_3Part1() {
		// Ranges of amino acids as possible locations for the modifications may be
		// represented using parentheses within the amino acid sequence.
		ProFormaTerm term = parser.parseString("PROT(EOSFORMS)[+19.0523]ISK");
		assertEquals(1, term.tags.stream().count());
		ProFormaTag tag = term.tags.get(0);
		assertEquals(4, tag.zeroBasedStartIndex);
		assertEquals(11, tag.zeroBasedEndIndex);
	}

	@Test(expected = ProFormaParseException.class)
	public void ambiguity_Ranges_4_4_3Part2() {
		// "Ranges must end next to a tag."
		parser.parseString("PROT(EOSFO)RMS[+19.0523]ISK");
	}

	@Test
	public void ambiguity_Ranges_4_4_3Part3() {
		// Range with a tag inside
		ProFormaTerm term = parser.parseString("PROT(EOC[Carbamidomethyl]FORMS)[+19.0523]ISK");
		assertEquals(2, term.tags.stream().count());
		ProFormaTag tag = term.tags.get(0);
//		assertEquals(6, tag.zeroBasedStartIndex);
		assertEquals(6, tag.zeroBasedEndIndex);
		assertEquals("Carbamidomethyl", tag.descriptors.get(0).getValue());

		ProFormaTag tag2 = term.tags.get(term.tags.size() - 1);
		assertEquals(4, tag2.zeroBasedStartIndex);
		assertEquals(11, tag2.zeroBasedEndIndex);
		assertEquals("+19.0523", tag2.descriptors.get(0).getValue());
	}

	@Test(expected = ProFormaParseException.class)
	public void ambiguity_Ranges_4_4_3Part4() {
		// Overlapping ranges represent a more complex case and are not yet supported,
		// and so, the following example would NOT be valid
		parser.parseString("P(ROT(EOSFORMS)[+19.0523]IS)[+19.0523]K");
	}

	@Test
	public void ambiguity_Ranges_4_4_3Part5() {
		// Ranges + groups + scores
		ProFormaTerm term = parser.parseString("PROT(EOSFORMS)[+19.0523#g1(0.01)]ISK[#g1(0.99)]");
		assertEquals(0, term.tags.size());
		assertEquals(1, term.tagGroups.stream().count());
		ProFormaMembershipDescriptor member1 = term.tagGroups.get(0).members.get(0);
		assertEquals(4, member1.zeroBasedStartIndex);
		assertEquals(11, member1.zeroBasedEndIndex);
		assertEquals(0.01, member1.weight, 0.001);
		ProFormaMembershipDescriptor member2 = term.tagGroups.get(0).members
				.get(term.tagGroups.get(0).members.size() - 1);
		assertEquals(14, member2.zeroBasedStartIndex);
		assertEquals(14, member2.zeroBasedEndIndex);
		assertEquals(0.99, member2.weight, 0.001);

		// More complex with inner tag not part of the group
		term = parser.parseString("PR[#g1(0.91)]OT(EOC[Carbamidomethyl]FORMS)[+19.05233#g1(0.09)]ISK");
		assertEquals(1, term.tags.stream().count());
		assertEquals(1, term.tagGroups.stream().count());
		member1 = term.tagGroups.get(0).members.get(0);
		assertEquals(1, member1.zeroBasedStartIndex);
		assertEquals(1, member1.zeroBasedEndIndex);
		assertEquals(0.91, member1.weight, 0.001);
		member2 = term.tagGroups.get(0).members.get(term.tagGroups.get(0).members.size() - 1);
		assertEquals(4, member2.zeroBasedStartIndex);
		assertEquals(11, member2.zeroBasedEndIndex);
		assertEquals(0.09, member2.weight, 0.001);
	}

	@Test
	public void ambiguity_PossiblePositionsWithScores_4_4_4Part1() {
		// The values of the modification localization scores can be indicated in
		// parentheses within the same group and brackets.
		ProFormaTerm term = parser.parseString("EM[Oxidation]EVT[#g1(0.01)]S[#g1(0.09)]ES[Phospho#g1(0.90)]PEK");
		assertEquals(1, term.tags.stream().count());
		assertEquals(1, term.tagGroups.stream().count());
		ProFormaTagGroup group = term.tagGroups.get(0);
		assertEquals("g1", group.name);
		assertEquals("Phospho", group.value);
		assertEquals(3, group.members.stream().count());

		ProFormaMembershipDescriptor member0 = group.members.get(0);
		assertEquals(4, member0.zeroBasedStartIndex);
		assertEquals(0.01, member0.weight, 0.001);
		ProFormaMembershipDescriptor member1 = group.members.get(1);
		assertEquals(5, member1.zeroBasedStartIndex);
		assertEquals(0.09, member1.weight, 0.001);
		ProFormaMembershipDescriptor member2 = group.members.get(2);
		assertEquals(7, member2.zeroBasedStartIndex);
		assertEquals(0.90, member2.weight, 0.001);
	}

	@Test(expected = ProFormaParseException.class)
	public void ambiguity_PossiblePositionsWithScores_4_4_4Part2() {
		// The additional option to represent localisation scores is to leave the
		// position of the modification as unknown using the ‘?’ notation,
		// but report the localization modification scores at specific sites.
		ProFormaTerm term = parser.parseString("[Phospho#s1]?EM[Oxidation]EVT[#s1(0.01)]S[#s1(0.09)]ES[#s1(0.90)]PEK");

		assertEquals(1, term.tags.stream().count());
		assertEquals(1, term.tagGroups.stream().count());
		assertEquals(0, term.unlocalizedTags);
		ProFormaTagGroup group = term.tagGroups.get(0);
		assertEquals("s1", group.name);
		assertEquals("Phospho", group.value);
		assertEquals(3, group.members.stream().count());

		ProFormaMembershipDescriptor member0 = group.members.get(0);
		assertEquals(4, member0.zeroBasedStartIndex);
		assertEquals(0.01, member0.weight, 0.001);
		ProFormaMembershipDescriptor member1 = group.members.get(1);
		assertEquals(5, member1.zeroBasedStartIndex);
		assertEquals(0.09, member1.weight, 0.001);
		ProFormaMembershipDescriptor member2 = group.members.get(2);
		assertEquals(7, member2.zeroBasedStartIndex);
		assertEquals(0.90, member2.weight, 0.001);
	}

	@Test(expected = ProFormaParseException.class)
	public void noMultipleModificationsSameSite_4_5() {
		// Currently, there is no need to chain two mods together on the same residue,
		// since complex glycans are not explicitly supported (see Section 3.4).
		// The solution in those rare cases not involving glycans is to have a single
		// PSI-MOD/RESID entry for the combination of mods.
		parser.parseString("EM[Oxidation][Phospho]EVTSESPEK");
	}

	@Test
	public void globalModifications_4_6() {
		// Use Fixed Modification LIST on term

		// Representation of isotopes
		ProFormaTerm term = parser.parseString("<13C>ATPEILTVNSIGQLK");
		assertEquals(0, term.tags.size());
		assertEquals(1, term.globalModifications.stream().count());

		ProFormaDescriptor globalMod = term.globalModifications.get(0).getDescriptors().get(0);
		assertEquals(ProFormaKey.NAME, globalMod.getKey());
		assertEquals(ProFormaEvidenceType.NONE, globalMod.getEvidenceType());
		assertEquals("13C", globalMod.getValue());
//		assertEquals(0, term.globalModifications.get(0).getTargetAminoAcids());

		// Two isotopes
		term = parser.parseString("<13C><15N>ATPEILTVNSIGQLK");
		assertEquals(0, term.tags.size());
		assertEquals(2, term.globalModifications.stream().count());

		ProFormaDescriptor globalMod0 = term.globalModifications.get(0).getDescriptors().get(0);
		assertEquals(ProFormaKey.NAME, globalMod0.getKey());
		assertEquals(ProFormaEvidenceType.NONE, globalMod0.getEvidenceType());
		assertEquals("13C", globalMod0.getValue());
		ProFormaDescriptor globalMod1 = term.globalModifications.get(1).getDescriptors().get(0);
		assertEquals(ProFormaKey.NAME, globalMod1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, globalMod1.getEvidenceType());
		assertEquals("15N", globalMod1.getValue());

		// Fixed protein modifications (single target)
		term = parser.parseString("<[MOD:01090]@C>ATPEILTCNSIGCLK");
		assertEquals(0, term.tags.size());
		assertEquals(1, term.globalModifications.stream().count());

		globalMod = term.globalModifications.get(0).getDescriptors().get(0);
		assertEquals(ProFormaKey.IDENTIFIER, globalMod.getKey());
		assertEquals(ProFormaEvidenceType.PSIMOD, globalMod.getEvidenceType());
		assertEquals("MOD:01090", globalMod.getValue());
		assertNotNull(term.globalModifications.get(0).getTargetAminoAcids());

		List<Character> expectedResult = Arrays.asList(new Character[] { 'C' });
		List<Character> actualResult = term.getGlobalModifications().get(0).getTargetAminoAcids();
		Assert.assertEquals(expectedResult, actualResult);

		// Fixed protein modifications (multiple targets)
		term = parser.parseString("<[Oxidation]@C,M>MTPEILTCNSIGCLK");
		assertEquals(0, term.tags.size());
		assertEquals(1, term.globalModifications.stream().count());

		globalMod = term.globalModifications.get(0).getDescriptors().get(0);
		assertEquals(ProFormaKey.NAME, globalMod.getKey());
//		assertEquals("Oxidation", globalMod.getValue());
		assertNotNull(term.globalModifications.get(0).getTargetAminoAcids());

		expectedResult = Arrays.asList(new Character[] { 'C', 'M' });
		actualResult = term.getGlobalModifications().get(0).getTargetAminoAcids();
		Assert.assertEquals(expectedResult, actualResult);
	}

	@Test(expected = ProFormaParseException.class)
	// Fixed modifications MUST be written prior to ambiguous modifications, and
	// similar to ambiguity notation,
	// N-terminal modifications MUST be the last ones written, just next to the
	// sequence.
	public void globalModifications_4_6TestCase1() {
		parser.parseString("[Phospho]?<[MOD:01090]@C>EM[Hydroxylation]EVTSESPEK");
	}

	@Test(expected = ProFormaParseException.class)
	public void globalModifications_4_6TestCase2() {
		parser.parseString("[Acetyl]-<[MOD:01090]@C>EM[Hydroxylation]EVTSESPEK");
	}

	@Test
	public void infoTag_4_7() {
		// Simple info tag
		ProFormaTerm term = parser.parseString("ELV[INFO:AnyString]IS");
		assertEquals(1, term.tags.stream().count());
		ProFormaDescriptor desc = term.tags.get(0).descriptors.get(0);
		assertEquals(ProFormaKey.INFO, desc.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc.getEvidenceType());
		assertEquals("AnyString", desc.getValue());

		// Multiple descriptors
		term = parser.parseString("ELVIS[Phospho|INFO:newly discovered|info:really awesome]K");
		assertEquals(1, term.tags.stream().count());
		ProFormaDescriptor desc0 = term.tags.get(0).descriptors.get(0);
		assertEquals(ProFormaKey.NAME, desc0.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc0.getEvidenceType());
		assertEquals("Phospho", desc0.getValue());
		ProFormaDescriptor desc1 = term.tags.get(0).descriptors.get(1);
		assertEquals(ProFormaKey.INFO, desc1.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc1.getEvidenceType());
		assertEquals("newly discovered", desc1.getValue());
		ProFormaDescriptor desc2 = term.tags.get(0).descriptors.get(2);
		assertEquals(ProFormaKey.INFO, desc2.getKey());
		assertEquals(ProFormaEvidenceType.NONE, desc2.getEvidenceType());
		assertEquals("really awesome", desc2.getValue());
	}

	@Test(expected = ProFormaParseException.class)
	public void infoTag_4_7TestCase1() {
		// Can't use [] in info tag
		parser.parseString("ELVIS[Phospho|INFO:newly]discovered]K");
	}

	@Test
	public void JointRepresentation_4_8() {
		// Alternative theoretical values
		// ELVIS[U:Phospho|+79.966331]K

		// Showing both the interpretation and measured mass:
		// ELVIS[U:Phospho|Obs:+79.978]K
	}
	// #endregion
}
