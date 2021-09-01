package com.proForma;

import java.util.List;

// The specified way of writing an unlocalized modification
// Everything between "[" and "]" (inclusive) that is following by a "?"
public class ProFormaUnlocalizedTag {
	private int count;    
	private List<ProFormaDescriptor> descriptors;

	// Initializes a new instance of the <see cref = "ProFormaUnlocalizedTag" />
	// class
	// @param name = "count" (the number of unlocalized modifications applied)
	// @param name = "descriptors" (the descriptors)
	public ProFormaUnlocalizedTag(int count, List<ProFormaDescriptor> descriptors2) {
		this.count = count;
		this.descriptors = descriptors2;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<ProFormaDescriptor> getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(List<ProFormaDescriptor> descriptors) {
		this.descriptors = descriptors;
	}
}
