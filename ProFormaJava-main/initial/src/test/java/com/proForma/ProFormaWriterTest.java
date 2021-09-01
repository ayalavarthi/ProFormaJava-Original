package com.proForma;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class ProformaWriterTest {
	public static ProFormaWriter writer = new ProFormaWriter();

	@Test
	public void writeSequenceOnly() {
		ProFormaTerm term = new ProFormaTerm("SEQUENCE", null, null, null, null, null, null, null);
		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals(term.sequence, result);
	}

	@Test
	public void writeSingleTag() {
		List<ProFormaDescriptor> descriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.INFO, "test");
		descriptors.add(proformaDescriptor);

		List<ProFormaTag> tags = new ArrayList<>();
		ProFormaTag tag = new ProFormaTag(2, descriptors);
		tags.add(tag);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", tags, null, null, null, null, null, null);
		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("SEQ[Info:test]UENCE", result);
	}

	@Test
	public void writeMultipleTags() {
		List<ProFormaDescriptor> descriptors1 = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor1 = new ProFormaDescriptor(ProFormaKey.INFO, "test");
		descriptors1.add(proformaDescriptor1);

		List<ProFormaDescriptor> descriptors2 = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor2 = new ProFormaDescriptor(ProFormaKey.MASS, "+14.05");
		descriptors2.add(proformaDescriptor2);

		List<ProFormaTag> tags = new ArrayList<>();
		ProFormaTag tag1 = new ProFormaTag(2, descriptors1);
		tags.add(tag1);

		tag1 = new ProFormaTag(5, descriptors2);
		tags.add(tag1);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", tags, null, null, null, null, null, null);

		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("SEQ[Info:test]UEN[+14.05]CE", result);
	}

	@Test
	public void writeMultipleDescriptors() {
		List<ProFormaDescriptor> descriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor1 = new ProFormaDescriptor(ProFormaKey.INFO, "test");
		descriptors.add(proformaDescriptor1);

		ProFormaDescriptor proformaDescriptor2 = new ProFormaDescriptor(ProFormaKey.MASS, "+14.05");
		descriptors.add(proformaDescriptor2);

		List<ProFormaTag> tags = new ArrayList<>();
		ProFormaTag tag1 = new ProFormaTag(2, descriptors);
		tags.add(tag1);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", tags, null, null, null, null, null, null);

		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("SEQ[Info:test|+14.05]UENCE", result);
	}

	@Test
	public void writeAmbiguousPossibleSitesDescriptors() {
		ArrayList<ProFormaMembershipDescriptor> members = new ArrayList<>();
		members.add(new ProFormaMembershipDescriptor(2, 0.0));
		members.add(new ProFormaMembershipDescriptor(5, 0.0));

		List<ProFormaTagGroup> tagGroups = new ArrayList<>();
		tagGroups.add(new ProFormaTagGroup("test", ProFormaKey.MASS, "+14.05", members));

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", null, null, null, null, null, tagGroups, null);

		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {		
			e.printStackTrace();
		}

		assertEquals("SEQ[+14.05#test]UEN[#test]CE", result);

	}

	@Test
	public void writeAmbiguousRangeDescriptors() {
		List<ProFormaDescriptor> descriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.MASS, "+14.05");
		descriptors.add(proformaDescriptor);

		List<ProFormaTag> tags = new ArrayList<>();
		ProFormaTag tag = new ProFormaTag(2, 5, descriptors);
		tags.add(tag);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", tags, null, null, null, null, null, null);
		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("SE(QUEN)[+14.05]CE", result);
	}

	@Test
	public void writeAmbiguousUnlocalizedTags() {
		List<ProFormaDescriptor> descriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.MASS, "+14.05");
		descriptors.add(proformaDescriptor);

		List<ProFormaUnlocalizedTag> tags = new ArrayList<>();
		tags.add(new ProFormaUnlocalizedTag(1, descriptors));

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", null, null, null, null, tags, null, null);
		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("[+14.05]?SEQUENCE", result);
	}

	@Test
	public void writeTerminalModsOnly() {
		List<ProFormaDescriptor> nTerminalDescriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.INFO, "test");
		nTerminalDescriptors.add(proformaDescriptor);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", null, nTerminalDescriptors, null, null, null, null, null);

		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("[Info:test]-SEQUENCE", result);

		List<ProFormaDescriptor> cTerminalDescriptors = new ArrayList<>();
		cTerminalDescriptors.add(proformaDescriptor);

		term = new ProFormaTerm("SEQUENCE", null, null, cTerminalDescriptors, null, null, null, null);
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("SEQUENCE-[Info:test]", result);

		nTerminalDescriptors = new ArrayList<>();
		proformaDescriptor = new ProFormaDescriptor(ProFormaKey.INFO, "testN");
		nTerminalDescriptors.add(proformaDescriptor);

		cTerminalDescriptors = new ArrayList<>();
		proformaDescriptor = new ProFormaDescriptor(ProFormaKey.INFO, "testC");
		cTerminalDescriptors.add(proformaDescriptor);

		term = new ProFormaTerm("SEQUENCE", null, nTerminalDescriptors, cTerminalDescriptors, null, null, null, null);
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("[Info:testN]-SEQUENCE-[Info:testC]", result);
	}

	@Test
	public void WriteMultipleTagsTerminalMod() {
		List<ProFormaDescriptor> descriptors1 = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor1 = new ProFormaDescriptor(ProFormaKey.INFO, "test");
		descriptors1.add(proformaDescriptor1);

		List<ProFormaDescriptor> descriptors2 = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor2 = new ProFormaDescriptor(ProFormaKey.MASS, "+14.05");
		descriptors2.add(proformaDescriptor2);

		List<ProFormaDescriptor> nTerminalDescriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.INFO, "unknown");
		nTerminalDescriptors.add(proformaDescriptor);

		List<ProFormaTag> tags = new ArrayList<>();
		ProFormaTag tag1 = new ProFormaTag(2, descriptors1);
		tags.add(tag1);
		ProFormaTag tag2 = new ProFormaTag(5, descriptors2);
		tags.add(tag2);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", tags, nTerminalDescriptors, null, null, null, null, null);

		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("[Info:unknown]-SEQ[Info:test]UEN[+14.05]CE", result);
	}

	@Test
	public void WritePossibleSitesAmbiguousTagsTerminalMod() {
		ArrayList<ProFormaMembershipDescriptor> members = new ArrayList<>();
		members.add(new ProFormaMembershipDescriptor(2, 0.0));
		members.add(new ProFormaMembershipDescriptor(5, 0.0));

		List<ProFormaTagGroup> tagGroups = new ArrayList<>();
		tagGroups.add(new ProFormaTagGroup("test", ProFormaKey.MASS, "+14.05", members));

		List<ProFormaDescriptor> nTerminalDescriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.INFO, "unknown");
		nTerminalDescriptors.add(proformaDescriptor);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", null, nTerminalDescriptors, null, null, null, tagGroups, null);

		String result = null;
		try {
			// Null pointer exception
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("[Info:unknown]-SEQ[+14.05#test]UEN[#test]CE", result);
	}

	@Test
	public void WriteRangeAmbiguousTagsTerminalMod() {
		List<ProFormaDescriptor> descriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.MASS, "+14.05");
		descriptors.add(proformaDescriptor);

		List<ProFormaTag> tags = new ArrayList<>();
		tags.add(new ProFormaTag(2, 5, descriptors));

		List<ProFormaDescriptor> nTerminalDescriptors = new ArrayList<>();
		proformaDescriptor = new ProFormaDescriptor(ProFormaKey.INFO, "unknown");
		nTerminalDescriptors.add(proformaDescriptor);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", tags, nTerminalDescriptors, null, null, null, null, null);
		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("[Info:unknown]-SE(QUEN)[+14.05]CE", result);
	}

	@Test
	public void WriteUnlocalizedAmbiguousTagsTerminalMod() {
		List<ProFormaDescriptor> descriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.MASS, "+14.05");
		descriptors.add(proformaDescriptor);

		List<ProFormaUnlocalizedTag> tags = new ArrayList<>();
		tags.add(new ProFormaUnlocalizedTag(1, descriptors));

		List<ProFormaDescriptor> nTerminalDescriptors = new ArrayList<>();
		proformaDescriptor = new ProFormaDescriptor(ProFormaKey.INFO, "unknown");
		nTerminalDescriptors.add(proformaDescriptor);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", null, nTerminalDescriptors, null, null, tags, null, null);
		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("[+14.05]?[Info:unknown]-SEQUENCE", result);

	}

	@Test
	public void WriteModificationNameAndIdentifiers() {

		List<ProFormaDescriptor> descriptors1 = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor1 = new ProFormaDescriptor(ProFormaKey.IDENTIFIER,
				ProFormaEvidenceType.RESID, "AA0420");
		descriptors1.add(proformaDescriptor1);

		List<ProFormaDescriptor> descriptors2 = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor2 = new ProFormaDescriptor(ProFormaKey.NAME, ProFormaEvidenceType.RESID,
				"Test");
		descriptors2.add(proformaDescriptor2);

		List<ProFormaTag> tags = new ArrayList<>();
		ProFormaTag tag1 = new ProFormaTag(2, descriptors1);
		tags.add(tag1);
		ProFormaTag tag2 = new ProFormaTag(4, descriptors2);
		tags.add(tag2);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", tags, null, null, null, null, null, null);

		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("SEQ[RESID:AA0420]UE[R:Test]NCE", result);

		// PSI-MOD
		result = "";
		tags = new ArrayList<>();
		descriptors1 = new ArrayList<>();
		descriptors2 = new ArrayList<>();
		proformaDescriptor1 = new ProFormaDescriptor(ProFormaKey.IDENTIFIER, ProFormaEvidenceType.PSIMOD, "MOD:00232");
		proformaDescriptor2 = new ProFormaDescriptor(ProFormaKey.NAME, ProFormaEvidenceType.PSIMOD, "Test");
		descriptors1.add(proformaDescriptor1);
		descriptors2.add(proformaDescriptor2);

		tag1 = new ProFormaTag(2, descriptors1);
		tag2 = new ProFormaTag(4, descriptors2);
		tags.add(tag1);
		tags.add(tag2);

		term = new ProFormaTerm("SEQUENCE", tags, null, null, null, null, null, null);

		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("SEQ[MOD:00232]UE[M:Test]NCE", result);

		// UNIMOD
		result = "";
		tags = new ArrayList<>();
		descriptors1 = new ArrayList<>();
		descriptors2 = new ArrayList<>();
		proformaDescriptor1 = new ProFormaDescriptor(ProFormaKey.IDENTIFIER, ProFormaEvidenceType.UNIMOD, "UNIMOD:15");
		proformaDescriptor2 = new ProFormaDescriptor(ProFormaKey.NAME, ProFormaEvidenceType.UNIMOD, "Test");
		descriptors1.add(proformaDescriptor1);
		descriptors2.add(proformaDescriptor2);

		tag1 = new ProFormaTag(2, descriptors1);
		tag2 = new ProFormaTag(4, descriptors2);
		tags.add(tag1);
		tags.add(tag2);

		term = new ProFormaTerm("SEQUENCE", tags, null, null, null, null, null, null);

		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}

		assertEquals("SEQ[UNIMOD:15]UE[U:Test]NCE", result);
	}

	@Test
	public void WriteGlobalModifications() {
		// Representation of isotopes
		List<ProFormaGlobalModification> globalModifications = new ArrayList<>();
		List<ProFormaDescriptor> descriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor("13C");
		descriptors.add(proformaDescriptor);

		ProFormaGlobalModification gm = new ProFormaGlobalModification(descriptors, null);
		globalModifications.add(gm);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", null, null, null, null, null, null, globalModifications);

		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("<13C>SEQUENCE", result);

		// Two isotopes
		List<ProFormaDescriptor> descriptors1 = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor1 = new ProFormaDescriptor("13C");
		descriptors1.add(proformaDescriptor1);

		List<ProFormaDescriptor> descriptors2 = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor2 = new ProFormaDescriptor("15N");
		descriptors2.add(proformaDescriptor2);

		globalModifications = new ArrayList<>();
		ProFormaGlobalModification gm1 = new ProFormaGlobalModification(descriptors1, null);
		ProFormaGlobalModification gm2 = new ProFormaGlobalModification(descriptors2, null);
		globalModifications.add(gm1);
		globalModifications.add(gm2);

		term = new ProFormaTerm("SEQUENCE", null, null, null, null, null, null, globalModifications);
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("<13C><15N>SEQUENCE", result);

		// Fixed protein modifications (multiple targets)
		proformaDescriptor = new ProFormaDescriptor(ProFormaKey.NAME, "Oxidation");
		descriptors = new ArrayList<>();
		descriptors.add(proformaDescriptor);

		List<Character> taas = new ArrayList<Character>();
		taas.add('C');
		taas.add('M');

		globalModifications = new ArrayList<>();
		gm = new ProFormaGlobalModification(descriptors, taas);
		globalModifications.add(gm);

		term = new ProFormaTerm("SEQUENCE", null, null, null, null, null, null, globalModifications);
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals("<[Oxidation]@C,M>SEQUENCE", result);

	}

	@Test
	public void WriteLabileModifications() {
		List<ProFormaDescriptor> ldescriptors = new ArrayList<>();
		ProFormaDescriptor lproformaDescriptor = new ProFormaDescriptor(ProFormaKey.GLYCAN, "Hex");
		ldescriptors.add(lproformaDescriptor);

		List<ProFormaDescriptor> descriptors = new ArrayList<>();
		ProFormaDescriptor proformaDescriptor = new ProFormaDescriptor(ProFormaKey.NAME, ProFormaEvidenceType.UNIMOD,
				"Hydroxylation");
		descriptors.add(proformaDescriptor);

		List<ProFormaTag> tags = new ArrayList<>();
		ProFormaTag tag = new ProFormaTag(2, descriptors);
		tags.add(tag);

		ProFormaTerm term = new ProFormaTerm("SEQUENCE", tags, null, null, ldescriptors, null, null, null);
		String result = null;
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("{Glycan:Hex}SEQ[U:Hydroxylation]UENCE", result);

		List<ProFormaDescriptor> nTerminalDescriptors = new ArrayList<>();
		ProFormaDescriptor nTerminalDescriptor = new ProFormaDescriptor("iTRAQ4plex");
		nTerminalDescriptors.add(nTerminalDescriptor);

		term = new ProFormaTerm("SEQUENCE", tags, nTerminalDescriptors, null, ldescriptors, null, null, null);
		try {
			result = writer.WriteString(term);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("{Glycan:Hex}[iTRAQ4plex]-SEQ[U:Hydroxylation]UENCE", result);
	}
}
