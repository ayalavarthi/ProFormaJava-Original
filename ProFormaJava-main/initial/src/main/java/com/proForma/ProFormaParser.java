package com.proForma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Quintet;

// Parser for the ProForma proteoform notation (link here to published manuscript)
public class ProFormaParser {
	// Parses the ProForma string.
	// @param name = "proFormaString" (pro forma string)
	// @return
	// @exception cref = "ArgumentNullException"
	// @exception cref = "ProFormaParseException"
	// X is not allowed
	public ProFormaTerm parseString(String proFormaString) {
		if (StringUtils.isEmpty(proFormaString)) {
			throw new IllegalArgumentException();
		}   

		List<ProFormaTag> tags = new ArrayList<>();
		List<ProFormaDescriptor> nTerminalDescriptors = new ArrayList<>();
		List<ProFormaDescriptor> cTerminalDescriptors = new ArrayList<>();
		List<ProFormaDescriptor> labileDescriptors = new ArrayList<>();
		List<ProFormaUnlocalizedTag> unlocalizedTags = new ArrayList<>();
		Map<String, ProFormaTagGroup> tagGroups = new HashMap<>();
		List<ProFormaGlobalModification> globalModifications = new ArrayList<>();

		StringBuilder sequence = new StringBuilder();
		StringBuilder tag = new StringBuilder();
		boolean inTag = false;
		boolean inGlobalTag = false;
		boolean inCTerminalTag = false;
		int openLeftBrackets = 0;
		int openLeftBraces = 0;
		Integer startRange = 0;
		Integer endRange = 0;

		// Don't love doing a global index of performance wise, but would need to
		// restructure things to handle multiple unlocalized tags
		int unlocalizedIndex = proFormaString.indexOf("?");

		String tagText = "";

		for (int i = 0; i < proFormaString.length(); i++) {
			if ((unlocalizedIndex == i)) {
				// Skip unlocalized separator
				continue;
			}

			char current = proFormaString.charAt(i);

			if (current == '<') {
				inGlobalTag = true;
			} else if (current == '>') {
				tagText = tag.toString();
				// Make sure nothing happen before this global mod
				if (sequence.length() > 0 || unlocalizedTags.size() > 0 || nTerminalDescriptors.size() > 0
						|| tagGroups.size() > 0) {
					throw new ProFormaParseException(
							"Global modifications must be the first element in ProForma string.");
				}

				handleGlobalModification(tagGroups, globalModifications, sequence, startRange, endRange, tagText);

				inGlobalTag = false;
				tag.setLength(0);
			} else if (current == '(' && !inTag) {
				if (startRange != 0) {
					throw new ProFormaParseException("Overlapping ranges are not allowed.");
				}

				startRange = sequence.length();
			} else if (current == ')' && !inTag) {
				endRange = sequence.length();

				// Ensure a tag comes next
				if (proFormaString.charAt(i + 1) != '[') {
					throw new ProFormaParseException("Ranges must end next to a tag.");
				}
			} else if (current == '{' && openLeftBraces++ == 0) {
				inTag = true;
			} else if (current == '}' && --openLeftBraces == 0) {
				int tempEndRange = 0;

				if (endRange > 0) {
					tempEndRange = startRange;
				}
				tagText = tag.toString();
				labileDescriptors = processTag(tagText, tempEndRange, sequence.length() - 1, tagGroups);
				inTag = false;
				tag.setLength(0);
			} else if (!inGlobalTag && current == '[' && openLeftBrackets++ == 0) {
				inTag = true;
			} else if (!inGlobalTag && current == ']' && --openLeftBrackets == 0) {
				// Don't allow 2 tags right next to each other in the sequence
				if (sequence.length() > 0 && proFormaString.length() > i + 1 && proFormaString.charAt(i + 1) == '[') {
					throw new ProFormaParseException("Two tags next to eachother are not allowed.");
				}
				tagText = tag.toString();
				// Handle terminal modifications and prefix tags
				if (inCTerminalTag) {
					cTerminalDescriptors = processTag(tagText, -1, -1, tagGroups);
				} else if (sequence.length() == 0 && proFormaString.charAt(i + 1) == '-') {
					nTerminalDescriptors = processTag(tagText, -1, -1, tagGroups);
					i++; // Skip the - character
				} else if (unlocalizedIndex >= i) {
					// Make sure the prefix came before the N-Terminal modification
					if (nTerminalDescriptors != null) {
						throw new ProFormaParseException(
								"Unlocalized modification must come before an N-terminal modification.");
					}
					List<ProFormaDescriptor> descriptors = processTag(tagText, -1, -1, tagGroups);
					if (descriptors != null) {
						int count = 1;
						// Check for higher count
						if (proFormaString.charAt(i + 1) == '^') {
							int j = i + 2;
							while (Character.isDigit(proFormaString.charAt(j))) {
								j++;
							}

							try {
								count = Integer.parseInt(proFormaString.substring(i + 2, j - i - 2));
							} catch (Exception e) {
								throw new ProFormaParseException("Can't process number after '^' character.");
							}
							i = j - 1; // POint i at the last digit
						}
						if (unlocalizedTags == null) {
							unlocalizedTags = new ArrayList<ProFormaUnlocalizedTag>();
						}
						unlocalizedTags.add(new ProFormaUnlocalizedTag(count, descriptors));
					}
					// i++;
					// Skip the ? character
				} else {
					int tempEndRange = 0;
					if (endRange != null)
						tempEndRange = startRange;

					processTag(tagText, tempEndRange, sequence.length() - 1, tags, tagGroups);
				}
				inTag = false;
				tag.setLength(0);
				;
				// Reset the range if we have processed the tag on the end of it
				if (endRange != 0) {
					startRange = 0;
					endRange = 0;
				}
			} else if (inTag || inGlobalTag) {
				tag.append(current);
			} else if (current == '-') {
				if (inCTerminalTag)
					throw new ProFormaParseException("- at index " + i + " is not allowed.");

				inCTerminalTag = true;
			} else {
				// Validate amino acid character
				if (!Character.isUpperCase(current))
					throw new ProFormaParseException(current + " is not an upper case letter.");
				// else if (current == 'X')
				// throw new ProFormaParseException("X is not allowed.");

				sequence.append(current);
			}
		}

		if (openLeftBrackets != 0)
			throw new ProFormaParseException("There are " + Math.abs(openLeftBrackets)
					+ " open brackets in ProForma string " + proFormaString.toString());

		if (openLeftBraces != 0)
			throw new ProFormaParseException("There are " + Math.abs(openLeftBraces)
					+ " open braces in ProForma string " + proFormaString.toString());

		List<ProFormaTagGroup> proFormaTagGroupList = new ArrayList<>();

		if (tagGroups != null) {
			tagGroups.forEach((k, v) -> {
				proFormaTagGroupList.add(v);
			});
		}

		return new ProFormaTerm(sequence.toString(), tags, nTerminalDescriptors, cTerminalDescriptors,
				labileDescriptors, unlocalizedTags, proFormaTagGroupList, globalModifications);
	}

	private void handleGlobalModification(Map<String, ProFormaTagGroup> tagGroups,
			List<ProFormaGlobalModification> globalModifications, StringBuilder sequence, int startRange, int endRange,
			String tagText) {
		// Check for '@' to specify targets
		int atSymbolIndex = tagText.lastIndexOf('@');
		String innerTagText;
		List<Character> targets = new ArrayList<Character>();
		if (atSymbolIndex > 0) {
			// Handle fixed modification with targets
			innerTagText = tagText.substring(1, atSymbolIndex - 1);
			for (int k = atSymbolIndex + 1; k < tagText.length(); k++) {
				if (Character.isUpperCase(tagText.charAt(k))) {
					targets.add(tagText.charAt(k));
				} else if (tagText.charAt(k) != ',')
					throw new ProFormaParseException(
							"Unexpected character" + tagText.charAt(k) + "in global modification target list.");

				else if (tagText.charAt(k) != ',') {
					throw new ProFormaParseException(
							"Unexpected character" + tagText.charAt(k) + "in global modification target list.");
				}
			}
		} else {
			// No targets, global isotope ... assume whole thing should be read
			innerTagText = tagText;
		}

		List<ProFormaDescriptor> descriptors = processTag(innerTagText, endRange != 0 ? startRange : 0,
				sequence.length() - 1, tagGroups);

		if (descriptors != null) {
			if (globalModifications == null) {
				globalModifications = new ArrayList<ProFormaGlobalModification>();
			}
			globalModifications.add(new ProFormaGlobalModification(descriptors, targets));
		}
	}

	private void processTag(String tag, int startIndex, int index, List<ProFormaTag> tags,
			Map<String, ProFormaTagGroup> tagGroups) {
		List<ProFormaDescriptor> descriptors = processTag(tag, startIndex, index, tagGroups);

		// Only add a tag if descriptors come back
		if (descriptors != null) {
			if (tags == null)
				tags = new ArrayList<ProFormaTag>();

			if (startIndex != 0)
				tags.add(new ProFormaTag(startIndex, index, descriptors));
			else
				tags.add(new ProFormaTag(index, descriptors));
		}
	}

	private List<ProFormaDescriptor> processTag(String tag, int startIndex, int index,
			Map<String, ProFormaTagGroup> tagGroups) {
		List<ProFormaDescriptor> descriptors = null;
		String[] descriptorText = tag.split("\\|", -1);

		for (int i = 0; i < descriptorText.length; i++) {
			Quintet<ProFormaKey, ProFormaEvidenceType, String, String, Double> quintet = parseDescriptor(
					descriptorText[i].stripLeading());

			if (!StringUtils.isEmpty(quintet.getValue3())) {
				if (tagGroups == null) {
					tagGroups = new HashMap<String, ProFormaTagGroup>();
				}

				if (!tagGroups.containsKey(quintet.getValue3())) {
					tagGroups.put(quintet.getValue3(), new ProFormaTagGroupChangingValue(quintet.getValue3(),
							quintet.getValue0(), quintet.getValue1(), new ArrayList<ProFormaMembershipDescriptor>()));
				}

				Object currentGroup = tagGroups.get(quintet.getValue3());

				// Fix up name of TagGroup
				if (!StringUtils.isEmpty(quintet.getValue2())
						&& currentGroup instanceof ProFormaTagGroupChangingValue) {
					ProFormaTagGroupChangingValue x = (ProFormaTagGroupChangingValue) currentGroup;
					// Only allow the value of the group to be set once
					if (!StringUtils.isEmpty(x.getValue()))
						throw new ProFormaParseException("You may only set the value of the group {group} once.");

					x.setValueFlux(quintet.getValue2());
					x.key = quintet.getValue0();
					x.evidenceType = quintet.getValue1();

				}

				// If the group was defined before the sequence, don't include it in the
				// membership
				if (index >= 0) {
					if (startIndex != 0)
						tagGroups.get(quintet.getValue3()).getMembers()
								.add(new ProFormaMembershipDescriptor(startIndex, index, quintet.getValue4()));
					else
						tagGroups.get(quintet.getValue3()).getMembers()
								.add(new ProFormaMembershipDescriptor(index, quintet.getValue4()));
				}
			} else if (quintet.getValue0() != ProFormaKey.NONE) // typical descriptor
			{
				if (descriptors == null)
					descriptors = new ArrayList<ProFormaDescriptor>();

				descriptors.add(new ProFormaDescriptor(quintet.getValue0(), quintet.getValue1(), quintet.getValue2()));
			} else if (!StringUtils.isEmpty(quintet.getValue2())) // keyless descriptor (UniMod or PSI-MOD annotation)
			{
				if (descriptors == null)
					descriptors = new ArrayList<ProFormaDescriptor>();

				descriptors.add(new ProFormaDescriptor(quintet.getValue2()));
			} else {
				throw new ProFormaParseException("Empty descriptor within tag " + tag);
			}
		}

		return descriptors;
	}

	private Quintet<ProFormaKey, ProFormaEvidenceType, String, String, Double> parseDescriptor(String text) {
		if (text.length() == 0) {
			throw new ProFormaParseException("Cannot have an empty descriptor.");
		}

		// Let's look for a group
		int groupIndex = text.indexOf('#');
		String groupName = null;
		double weight = 0.0;

		if (groupIndex >= 0) {
			// Check for weight
			int weightIndex = text.indexOf('(');

			if (weightIndex > groupIndex) {
				// Make sure descriptor ends in ')' to close out weight
				if (text.charAt(text.length() - 1) != ')') {
					throw new ProFormaParseException("Descriptor with weight must end in ')'.");
				}

				try {
					weight = Double.parseDouble(text.substring(weightIndex + 1, text.length() - 1));
				} catch (Exception e) {
					throw new ProFormaParseException(String.format("Could not parse weight: ",
							text.substring(weightIndex + 1, text.length() - weightIndex - 2)));
				}
				groupName = text.substring(groupIndex + 1,  weightIndex);
			} else {
				groupName = text.substring(groupIndex + 1);
			}

			text = text.substring(0, groupIndex);

			if (groupName == null || "".equals(groupName)) {
				throw new ProFormaParseException("Group name cannot be empty.");
			}
		}

		// Check for naked group tag
		if (text == null || "".equals(text)) {
			return Quintet.with(ProFormaKey.NONE, ProFormaEvidenceType.NONE, text, groupName, weight);
		}

		// Let's look for a colon
		int colon = text.indexOf(':');

		if (colon < 0) {
			boolean isMass2 = text.charAt(0) == '+' || text.charAt(0) == '-';
			return Quintet.with(getKey(isMass2), ProFormaEvidenceType.NONE, text, groupName, weight);
		}

		// Let's see if the bit before the colon is a known key
		String keyText = text.substring(0, colon).toLowerCase().strip();
		boolean isMass = text.charAt(colon + 1) == '+' || text.charAt(colon + 1) == '-';

		switch (keyText) {
		case "formula":
			return Quintet.with(ProFormaKey.FORMULA, ProFormaEvidenceType.NONE, text.substring(colon + 1), groupName,
					weight);
		case "glycan":
			return Quintet.with(ProFormaKey.GLYCAN, ProFormaEvidenceType.NONE, text.substring(colon + 1), groupName,
					weight);
		case "info":
			return Quintet.with(ProFormaKey.INFO, ProFormaEvidenceType.NONE, text.substring(colon + 1), groupName,
					weight);
		case "mod":
			return Quintet.with(ProFormaKey.IDENTIFIER, ProFormaEvidenceType.PSIMOD, text, groupName, weight);
		case "unimod":
			return Quintet.with(ProFormaKey.IDENTIFIER, ProFormaEvidenceType.UNIMOD, text, groupName, weight);
		case "xlmod":
			return Quintet.with(ProFormaKey.IDENTIFIER, ProFormaEvidenceType.XLMOD, text, groupName, weight);
		case "gno":
			return Quintet.with(ProFormaKey.IDENTIFIER, ProFormaEvidenceType.GNO, text, groupName, weight);
		// Special case for RESID id, don't inclue bit with colon
		case "resid":
			return Quintet.with(ProFormaKey.IDENTIFIER, ProFormaEvidenceType.RESID, text.substring(colon + 1),
					groupName, weight);
		// Handle names and masses
		case "u":
			return Quintet.with(getKey(isMass), ProFormaEvidenceType.UNIMOD, text.substring(colon + 1), groupName,
					weight);
		case "m":
			return Quintet.with(getKey(isMass), ProFormaEvidenceType.PSIMOD, text.substring(colon + 1), groupName,
					weight);
		case "r":
			return Quintet.with(getKey(isMass), ProFormaEvidenceType.RESID, text.substring(colon + 1), groupName,
					weight);
		case "x":
			return Quintet.with(getKey(isMass), ProFormaEvidenceType.XLMOD, text.substring(colon + 1), groupName,
					weight);

		case "g":
			return Quintet.with(getKey(isMass), ProFormaEvidenceType.GNO, text.substring(colon + 1), groupName, weight);
		case "b":
			return Quintet.with(getKey(isMass), ProFormaEvidenceType.BRNO, text.substring(colon + 1), groupName,
					weight);
		case "obs":
			return Quintet.with(getKey(isMass), ProFormaEvidenceType.OBSERVED, text.substring(colon + 1), groupName,
					weight);
		default:
			return Quintet.with(ProFormaKey.NAME, ProFormaEvidenceType.NONE, text, groupName, weight);
		}
	}

	private ProFormaKey getKey(boolean isMass) {
		return isMass ? ProFormaKey.MASS : ProFormaKey.NAME;
	}
}
