package com.proForma;

import java.util.*;

public class ProFormaTagGroupChangingValue extends ProFormaTagGroup {
	public ProFormaTagGroupChangingValue(String name, ProFormaKey key, ProFormaEvidenceType evidenceType,
			ArrayList<ProFormaMembershipDescriptor> members) {
		super(name, key, evidenceType, "", members);
	}
    
	public final String getValueFlux() {
		return this.value;
	}

	public final void setValueFlux(String value) {
		if (value != null) {
			this.value = value;
		}
	}

	public final ProFormaKey getKeyFlux() {
		return this.key;
	}

	public final void setKeyFlux(ProFormaKey value) {
		if (value != null) {
			this.key = value;
		}
	}

	public final ProFormaEvidenceType getEvidenceFlux() {
		return this.evidenceType;
	}

	public final void setEvidenceFlux(ProFormaEvidenceType value) {
		if (value != null) {
			this.evidenceType = value;
		}
	}
}
