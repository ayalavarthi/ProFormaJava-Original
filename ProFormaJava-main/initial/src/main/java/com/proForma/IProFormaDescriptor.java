package com.proForma;

// Anything that describes a modification
public interface IProFormaDescriptor {
	// The key
	ProFormaKey getKey();    

	// The type of the evidence
	ProFormaEvidenceType getEvidenceType();

	// The value
	String getValue();
}
