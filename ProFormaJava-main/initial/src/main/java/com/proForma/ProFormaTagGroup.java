package com.proForma;

import java.util.ArrayList;

// A tag that is spread across multiple distinct sites
public class ProFormaTagGroup implements IProFormaDescriptor {
	String name = "";
	String value = "";    
	ProFormaEvidenceType evidenceType = null;
	ProFormaKey key = null;
	ArrayList<ProFormaMembershipDescriptor> members = null;

	// Initializes a new instance of the <see cref = "ProFormaTagGroup" /> class
	// @param name = "name" (the name)
	// @param name = "key" (the key)
	// @param name = "value" (the value)
	// @param name = "members" (the members)
	public ProFormaTagGroup(String name, ProFormaKey key, String value,
			ArrayList<ProFormaMembershipDescriptor> members) {
		this.name = name;
		this.value = value;
		this.key = key;
		this.members = members;
		this.evidenceType = ProFormaEvidenceType.NONE;
	}

	// Initializes a new instance of the <see cref = "ProFormaTagGroup" /> class
	// @param name = "name" (the name)
	// @param name = "key" (the key)
	// @param name = "evidenceType" (type of the evidence)
	// @param name = "value" (the value)
	// @param name = "members" (the members)
	public ProFormaTagGroup(String name, ProFormaKey key, ProFormaEvidenceType evidenceType, String value,
			ArrayList<ProFormaMembershipDescriptor> members) {
		this.name = name;
		this.value = value;
		this.evidenceType = evidenceType;
		this.key = key;
		this.members = members;
	}

	// The name of the group
	public String getName() {
		return name;
	}

	// The value
	public String getValue() {
		return value;
	}

	// The type of the evidence
	public ProFormaEvidenceType getEvidenceType() {
		return evidenceType;
	}

	// The key
	public ProFormaKey getKey() {
		return key;
	}

	// The members of the group
	public ArrayList<ProFormaMembershipDescriptor> getMembers() {
		return members;
	}
}
