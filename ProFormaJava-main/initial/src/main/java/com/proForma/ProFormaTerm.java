package com.proForma;

import java.util.*;

// Represents a ProForma string in memory
public class ProFormaTerm {
	String sequence = "";   
	List<ProFormaTag> tags = null;
	List<ProFormaDescriptor> nTerminalDescriptors = null;
	List<ProFormaDescriptor> cTerminalDescriptors = null;
	List<ProFormaDescriptor> labileDescriptors = null;
	List<ProFormaUnlocalizedTag> unlocalizedTags = null;
	List<ProFormaTagGroup> tagGroups = null;
	List<ProFormaGlobalModification> globalModifications = null;

	public ProFormaTerm(String sequence, List<ProFormaTag> tags, List<ProFormaDescriptor> nTerminalDescriptors,
			List<ProFormaDescriptor> cTerminalDescriptors, List<ProFormaDescriptor> labileDescriptors,
			List<ProFormaUnlocalizedTag> unlocalizedTags, List<ProFormaTagGroup> tagGroups,
			List<ProFormaGlobalModification> globalModifications) {
		this.sequence = sequence;
		this.tags = tags;
		this.nTerminalDescriptors = nTerminalDescriptors;
		this.cTerminalDescriptors = cTerminalDescriptors;
		this.labileDescriptors = labileDescriptors;
		this.unlocalizedTags = unlocalizedTags;
		this.tagGroups = tagGroups;
		this.globalModifications = globalModifications;
	}

	// The amino acid sequence
	public String getSequence() {
		return sequence;
	}

	// All tags on this term
	public List<ProFormaTag> getTags() {
		return tags;
	}

	// N-Terminal descriptors
	public List<ProFormaDescriptor> getNTerminalDescriptors() {
		return nTerminalDescriptors;
	}

	// C-Terminal descriptors
	public List<ProFormaDescriptor> getCTerminalDescriptors() {
		return cTerminalDescriptors;
	}

	// Labile modifications (not visible in the fragmentation MS2 spectrum)
	// descriptors
	public List<ProFormaDescriptor> getLabileDescriptors() {
		return labileDescriptors;
	}

	// Descriptors for modifications that are completely unlocalized
	public List<ProFormaUnlocalizedTag> getUnlocalizedTags() {
		return unlocalizedTags;
	}

	// All tag groups for this term
	public List<ProFormaTagGroup> getTagGroups() {
		return tagGroups;
	}

	// Modifications that apply globally based on a target or targets
	public List<ProFormaGlobalModification> getGlobalModifications() {
		return globalModifications;
	}
}
