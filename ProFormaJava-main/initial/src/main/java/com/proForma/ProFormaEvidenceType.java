package com.proForma;

// Evidence types to provide on a descriptor, typically an ontology identifier
public enum ProFormaEvidenceType {
	// No evidence provided
	NONE,  

	// Experimentally observed evidence
	OBSERVED,

	// The Unimod database identifier
	UNIMOD,

	// The UniProt database identifier
	UNIPROT,

	// The RESID database identifier
	RESID,

	// The PSI-MOD database identifier
	PSIMOD,

	// The XL-MOD identifier
	XLMOD,

	// The GNO identifier
	GNO,

	// The BRNO identifier (https://doi.org/10.1038/nsmb0205-110)
	BRNO
}
