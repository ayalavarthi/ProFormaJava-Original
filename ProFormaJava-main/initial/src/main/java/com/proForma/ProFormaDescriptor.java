package com.proForma;

// Member of the tag
// Could be a key-value pair or a keyless entry
public class ProFormaDescriptor implements IProFormaDescriptor {
	// Initializes a descriptor without value only
	// @param name = "value" (the value)
	public ProFormaDescriptor(String value) {
		this(ProFormaKey.NAME, ProFormaEvidenceType.NONE, value);
	}

	// Initializes a new instance of the <see cref="ProFormaDescriptor"/> class.
	// @param name = "key" (the key)
	// @param name = "value" (the value)
	public ProFormaDescriptor(ProFormaKey key, String value) {
		this(key, ProFormaEvidenceType.NONE, value);
	}

	// Initializes a new instance of the <see cref="ProFormaDescriptor" /> class.
	// @param name = "key" (the key)
	// @param name = "evidenceType" (type of the evidence)
	// @param name = "value" (the value)
	public ProFormaDescriptor(ProFormaKey key, ProFormaEvidenceType evidenceType, String value) {
		this.key = key;
		this.evidenceType = evidenceType;
		this.value = value;
	}

	private ProFormaKey key;
	private ProFormaEvidenceType evidenceType;
	private String value;

	public ProFormaKey getKey() {
		return key;
	}

	public void setKey(ProFormaKey key) {
		this.key = key;
	}

	public ProFormaEvidenceType getEvidenceType() {
		return evidenceType;
	}

	public void setEvidenceType(ProFormaEvidenceType evidenceType) {
		this.evidenceType = evidenceType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	// String representation of <see cref = "ProFormaDescriptor" />
	// @return
	@Override
	public String toString() {
		String result = getKey() + ":" + getEvidenceType() + ":" + getValue();
		return result;
	}
}
