package com.proForma;

// A member of a tag group
public class ProFormaMembershipDescriptor {
	int zeroBasedStartIndex = 0;
	int zeroBasedEndIndex = 0;
	double weight = 0.0;

	// Initializes a new instance of the <see cref = "ProFormaMembershipDescriptor"
	// /> class
	// @param name = "zeroBasedIndex" (the zero-based index of the modified amino
	// acid in the sequence)
	// @param name = "weight" (the weight)
	public ProFormaMembershipDescriptor(int zeroBasedIndex, double weight) {
		this.zeroBasedStartIndex = zeroBasedIndex;
		this.zeroBasedEndIndex = zeroBasedIndex;
		this.weight = weight;  
	}

	// Initializes a new instance of the <see cref="ProFormaMembershipDescriptor"/>
	// class.
	// @param name = "zeroBasedStartIndex" (the zero-based start index of the
	// modified amino acid in the sequence)
	// @param name = "zeroBasedEndIndex" (the zero-based end index of the modified
	// amino acid in the sequence)
	// @param name = "weight" (the weight)
	public ProFormaMembershipDescriptor(int zeroBasedStartIndex, int zeroBasedEndIndex, double weight) {
		this.zeroBasedStartIndex = zeroBasedStartIndex;
		this.zeroBasedEndIndex = zeroBasedEndIndex;
		this.weight = weight;
	}

	// Gets the zero-based start index in the sequence
	public int getZeroBasedStartIndex() {
		return zeroBasedStartIndex;
	}

	// Gets the zero-based end index in the sequence
	public int getZeroBasedEndIndex() {
		return zeroBasedEndIndex;
	}

	// The weight this member has on the group
	public double getWeight() {
		return weight;
	}
}
