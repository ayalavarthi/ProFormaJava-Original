package com.proForma;

// Possible keys for a ProFormaDescriptor
public enum ProFormaKey {
	// No key provided
	NONE,

	// The exact modification name from an ontology
	NAME,  

	// An identifier from an ontology
	IDENTIFIER,

	// A delta mass of unknown annotation
	MASS,

	// A chemical formula in our notation
	FORMULA,

	// A glycan composition in our notation
	GLYCAN,

	// The user defined extra information
	INFO
}
