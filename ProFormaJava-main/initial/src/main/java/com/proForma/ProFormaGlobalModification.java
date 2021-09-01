package com.proForma;

import java.util.List;

// A modification to applies globally based on a target or targets
public class ProFormaGlobalModification {
	public ProFormaGlobalModification(List<ProFormaDescriptor> descriptors, List<Character> targetAminoAcids) {
		this.descriptors = descriptors;
		this.targetAminoAcids = targetAminoAcids;
	}  

	private List<ProFormaDescriptor> descriptors;
	private List<Character> targetAminoAcids;

	public List<ProFormaDescriptor> getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(List<ProFormaDescriptor> descriptors) {
		this.descriptors = descriptors;
	}

	public List<Character> getTargetAminoAcids() {
		return targetAminoAcids;
	}

	public void setTargetAminoAcids(List<Character> targetAminoAcids) {
		this.targetAminoAcids = targetAminoAcids;
	}
}
