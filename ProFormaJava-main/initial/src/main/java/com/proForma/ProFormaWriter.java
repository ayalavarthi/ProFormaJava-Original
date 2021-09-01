package com.proForma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Quintet;

// Writer for the ProForma proteoform notation
public class ProFormaWriter {
	// Writes the string
	public String WriteString(ProFormaTerm term) throws Exception {
		StringBuilder sb = new StringBuilder();
		// Check global modifications
		if (term.getGlobalModifications() != null) {
			for (ProFormaGlobalModification globalMod : term.getGlobalModifications()) {
				if (globalMod.getTargetAminoAcids() != null) {
					String getTargetAminoAcidsInStringFormat = globalMod.getTargetAminoAcids().stream()
							.map(Object::toString).collect(Collectors.joining(","));

					sb.append("<[" + CreateDescriptorsText(globalMod.getDescriptors()) + "]@"
							+ getTargetAminoAcidsInStringFormat + ">");
				} else {
					sb.append(String.format("<" + CreateDescriptorsText(globalMod.getDescriptors()) + ">"));
				}
			}
		}

		// Check labile modifications
		if (term.getLabileDescriptors() != null) {
			// sb.append("{{" + CreateDescriptorsText(term.getLabileDescriptors()) + "}}");
			sb.append(String.format("{%1$s", CreateDescriptorsText(term.getLabileDescriptors()) + "}"));

		}

		// Check unlocalized modifications
		if (term.getUnlocalizedTags() != null && term.getUnlocalizedTags().stream().count() > 0) {
			for (ProFormaUnlocalizedTag tag : term.getUnlocalizedTags()) {
				if (tag.getDescriptors() != null && tag.getDescriptors().stream().count() > 0) {
					sb.append("[" + CreateDescriptorsText(tag.getDescriptors()) + "]");
				}

				if (tag.getCount() != 1) {
					sb.append("^" + tag.getCount());
				}
			}

			// Only write out a single question mark
			sb.append('?');
		}

		// Check N-terminal modifications
		if (term.getNTerminalDescriptors() != null && term.getNTerminalDescriptors().stream().count() > 0) {
			sb.append("[" + CreateDescriptorsText(term.getNTerminalDescriptors()) + "]-");
		}

		List<Quintet<Object, Integer, Integer, Boolean, Double>> tagsAndGroups = new ArrayList<Quintet<Object, Integer, Integer, Boolean, Double>>();

		if (term.getTags() != null) {
			for (ProFormaTag x : term.getTags()) {
				tagsAndGroups.add(Quintet.with(x, x.getZeroBasedStartIndex(), x.getZeroBasedEndIndex(), true, 0.0));
			}

		}

		if (term.getTagGroups() != null) {
			for (ProFormaTagGroup x : term.getTagGroups()) {
				for (ProFormaMembershipDescriptor member : x.getMembers()) {
					tagsAndGroups.add(Quintet.with(x, member.getZeroBasedStartIndex(), member.getZeroBasedEndIndex(),
							member == x.getMembers().get(0), member.getWeight()));
				}

			}
		}

		// Check indexed modifications
		if (tagsAndGroups.size() > 0) {
			// Sort by start index
			Comparator<Quintet<Object, Integer, Integer, Boolean, Double>> comparator = new Comparator<Quintet<Object, Integer, Integer, Boolean, Double>>() {

				public int compare(Quintet<Object, Integer, Integer, Boolean, Double> tupleA,
						Quintet<Object, Integer, Integer, Boolean, Double> tupleB) {
					return tupleA.getValue1().compareTo(tupleB.getValue1());
				}

			};

			Collections.sort(tagsAndGroups, comparator);

			int currentIndex = 0;
			for (Object ob1 : tagsAndGroups) {
				Quintet<Object, Integer, Integer, Boolean, Double> ob = (Quintet<Object, Integer, Integer, Boolean, Double>) (ob1);
				if (ob.getValue1() == ob.getValue2()) {
					// Write sequence up to tag
					sb.append(term.getSequence().substring(currentIndex, ob.getValue1() + 1));
					currentIndex = ob.getValue1() + 1;
				} else // Handle ambiguity range
				{
					// Write sequence up to range
					sb.append(term.getSequence().substring(currentIndex, ob.getValue1()));

					// Write sequence in range
					sb.append(
							String.format("(%1$s)", term.getSequence().substring(ob.getValue1(), ob.getValue2() + 1)));
					currentIndex = ob.getValue2() + 1;
				}

				boolean tempVar = ob.getValue0() instanceof ProFormaTag;
				ProFormaTag tag = tempVar ? (ProFormaTag) ob.getValue0() : null;
				if (tempVar) {
					sb.append(String.format("[%1$s]", CreateDescriptorsText(tag.getDescriptors())));
				} else {
					boolean tempVar2 = ob.getValue0() instanceof ProFormaTagGroup;
					ProFormaTagGroup group = tempVar2 ? (ProFormaTagGroup) ob.getValue0() : null;
					if (tempVar2) {
						if (ob.getValue3()) {
							// Confirm with Mr.Fellers
							// Input for CreateDescriptorText is IProFormaDescriptor (not group)
							sb.append(String.format("[%1$s#%2$s", CreateDescriptorText(group), group.getName()));
						} else {
							sb.append(String.format("[#%1$s", group.getName()));
						}

						if (ob.getValue4() > 0.0) {
							sb.append(String.format("(%1$s)]", ob.getValue4()));
						} else {
							sb.append(']');
						}
					}
				}
			}

			// Write the rest of the sequence
			sb.append(term.getSequence().substring(currentIndex));
		} else {
			sb.append(term.getSequence());
		}

		// Check C-terminal modifications
		if (term.getCTerminalDescriptors() != null && term.getCTerminalDescriptors().stream().count() > 0) {
			sb.append(String.format("-[%1$s]", CreateDescriptorsText(term.getCTerminalDescriptors())));
		}

		return sb.toString();
	}

	private String CreateDescriptorsText(List<ProFormaDescriptor> descriptors) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < descriptors.size(); i++) {
			try {
				sb.append(CreateDescriptorText(descriptors.get(i)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (i < descriptors.size() - 1) {
				sb.append('|');
			}
		}

		return sb.toString();
	}

	private String CreateDescriptorText(IProFormaDescriptor descriptor) throws Exception {
		switch (descriptor.getKey()) {
		case FORMULA:
			return "Formula:" + descriptor.getValue();
		case GLYCAN:
			return "Glycan:" + descriptor.getValue();
		case INFO:
			return "Info:" + descriptor.getValue();
		case NAME: {
			switch (descriptor.getEvidenceType()) {
			case NONE:
				return descriptor.getValue();
			case OBSERVED:
				return "Obs:" + descriptor.getValue();
			case UNIMOD:
				return "U:" + descriptor.getValue();
			case RESID:
				return "R:" + descriptor.getValue();
			case PSIMOD:
				return "M:" + descriptor.getValue();
			case XLMOD:
				return "X:" + descriptor.getValue();
			case GNO:
				return "G:" + descriptor.getValue();
			case BRNO:
				return "B:" + descriptor.getValue();
			default:
				throw new Exception("Can't handle " + descriptor.getKey() + " with evidence type: "
						+ descriptor.getEvidenceType() + ".");
			}
		}
		case MASS: {
			
			switch (descriptor.getEvidenceType()) {
			case NONE:
				return descriptor.getValue();
			case OBSERVED:
				return "Obs:" + descriptor.getValue();
			case UNIMOD:
				return "U:" + descriptor.getValue();
			case RESID:
				return "R:" + descriptor.getValue();
			case PSIMOD:
				return "M:" + descriptor.getValue();
			case XLMOD:
				return "X:" + descriptor.getValue();
			case GNO:
				return "G:" + descriptor.getValue();
			case BRNO:
				return "B:" + descriptor.getValue();
			default:
				throw new Exception("Can't handle " + descriptor.getKey() + " with evidence type: "
						+ descriptor.getEvidenceType() + ".");

			}
		}

		case IDENTIFIER: {
			switch (descriptor.getEvidenceType()) {
			case OBSERVED:
				return "Obs:" + descriptor.getValue();

			case RESID:
				return "RESID:" + descriptor.getValue();

			default:
				return descriptor.getValue();

			}
		}
		default:
			return descriptor.getValue();
		}
	}
}
