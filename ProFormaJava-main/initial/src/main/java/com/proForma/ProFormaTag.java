package com.proForma;

import java.util.*;

// The specified way of writing a modification
// Everything between "[" and "]" (inclusive)
// A collections of descriptors
public class ProFormaTag {
	int zeroBasedStartIndex = 0;
	int zeroBasedEndIndex = 0;   
	List<ProFormaDescriptor> descriptors = null;

	// Initializes a new instance of the <see cref = "ProFormaTag" /> class
	// @param name = "zeroBasedIndex" (the zero-based index of the modified amino
	// acid in the sequence)
	// @param name = "descriptors" (the descriptors)
	public ProFormaTag(int zeroBasedIndex, List<ProFormaDescriptor> descriptors) {
		this.zeroBasedStartIndex = zeroBasedIndex;
		this.zeroBasedEndIndex = zeroBasedIndex;
		this.descriptors = descriptors;
	}

	// Initializes a new instance of the <see cref = "ProFormaTag" /> class
	// @param name = "zeroBasedStartIndex" (the zero-based start index of the
	// modified amino acid in the sequence)
	// @param name = "zeroBasedEndIndex" (the zero-based end index of the modified
	// amino acid in the sequence)
	// @param name = "descriptors" (the descriptors)
	public ProFormaTag(int zeroBasedStartIndex, int zeroBasedEndIndex, List<ProFormaDescriptor> descriptors) {
		this.zeroBasedStartIndex = zeroBasedStartIndex;
		this.zeroBasedEndIndex = zeroBasedEndIndex;
		this.descriptors = descriptors;
	}

	// Gets the zero-based start index in the sequence
	public int getZeroBasedStartIndex() {
		return zeroBasedStartIndex;
	}

	// Gets the zero-based end index in the sequence
	public int getZeroBasedEndIndex() {
		return zeroBasedEndIndex;
	}

	// Gets the descriptors
	public List<ProFormaDescriptor> getDescriptors() {
		return descriptors;
	}
}
