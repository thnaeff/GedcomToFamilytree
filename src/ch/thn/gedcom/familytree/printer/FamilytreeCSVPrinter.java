/**
 *    Copyright 2013 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.thn.gedcom.familytree.printer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.thn.gedcom.creator.structures.GedcomFamily;
import ch.thn.gedcom.creator.structures.GedcomIndividual;
import ch.thn.gedcom.familytree.FamilyTree;
import ch.thn.gedcom.familytree.FamilyTreeNode;
import ch.thn.gedcom.familytree.GedcomToFamilyTree;
import ch.thn.util.tree.onoff.OnOffTreeUtil;
import ch.thn.util.tree.printer.TreeNodeCSVPrinter;

/**
 * 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilytreeCSVPrinter
extends TreeNodeCSVPrinter<FamilyTreeNode> implements FamilytreePrinter {

	private FamilyTreePrintBuilder printBuilder = null;

	private GedcomToFamilyTree toFamilyTree = null;


	/**
	 * 
	 * 
	 * @param alignValuesRight
	 * @param showId
	 * @param showGender
	 * @param showRelationship
	 * @param showEmail
	 * @param showAddress
	 * @param showAgeForDead
	 * @param showBirthDate
	 * @param showDeathDate
	 * @param showFirstName
	 * @param showMaidenName
	 * @param showMarriedName
	 * @param showDivorcedPartnerWithoutChildren
	 * @param showDivorcedPartnerWithChildren
	 */
	public FamilytreeCSVPrinter(boolean alignValuesRight,
			boolean showId, boolean showGender, boolean showRelationship, boolean showEmail,
			boolean showAddress, boolean showAgeForDead, boolean showBirthDate,
			boolean showDeathDate, boolean showFirstName, boolean showMaidenName, boolean showMarriedName,
			boolean showDivorcedPartnerWithoutChildren, boolean showDivorcedPartnerWithChildren) {
		super(alignValuesRight);

		printBuilder = new FamilyTreePrintBuilder(showId, showGender,
				showRelationship, showEmail, showAddress, showAgeForDead,
				showBirthDate, showDeathDate, showFirstName, showMaidenName, showMarriedName,
				showDivorcedPartnerWithoutChildren, showDivorcedPartnerWithChildren);

	}

	@Override
	public StringBuilder print(FamilyTreeNode printNode) {
		throw new UnsupportedOperationException("The method print(FamilyTreeNode) is not supported. " +
				"Use print(GedcomToFamilyTree) instead.");
	}

	@Override
	public StringBuilder print(GedcomToFamilyTree toFamilyTree) {
		this.toFamilyTree = toFamilyTree;

		LinkedList<FamilyTreeNode> trees = OnOffTreeUtil.convertToSimpleTree((FamilyTreeNode)toFamilyTree.getFamilyTree(), true, true);

		StringBuilder sb = new StringBuilder();
		for (FamilyTreeNode tree : trees) {
			if (sb.length() > 0) {
				//Keep trees separated a little
				sb.append(LINE_SEPARATOR + LINE_SEPARATOR);
			}

			sb.append(super.print(tree));
		}

		return sb;
	}

	@Override
	protected Collection<String> getNodeValues(FamilyTreeNode node) {
		List<String> lines = new ArrayList<>();

		if (node instanceof FamilyTree) {
			//Only the title
			lines.add(((FamilyTree)node).getFamilyTreeTitle());
			return lines;
		}

		GedcomIndividual[] individuals = node.getNodeValue();

		List<List<String>> nodeValueLines = printBuilder.createNodeValueLines(individuals[0], individuals[1],
				toFamilyTree.getStorage().getFamilyOfParents(individuals[0], individuals[1]),
				this, false, false);

		for (List<String> valueLines : nodeValueLines) {
			if (valueLines == null || valueLines.size() == 0) {
				continue;
			}

			StringBuilder sb = new StringBuilder();
			for (String value : valueLines) {
				if (value == null) {
					continue;
				}

				//TODO for now, all csv separators are just removed so that the csv data is not messed up
				if (value.contains(CSV_SEPARATOR)) {
					value = value.replace(CSV_SEPARATOR, "");
				}

				sb.append(value);
				sb.append(CSV_SEPARATOR);
			}

			lines.add(sb.toString());

		}

		return lines;
	}

	@Override
	public ArrayList<String> createPrimaryLine(GedcomIndividual indi,
			GedcomIndividual partner, GedcomFamily family, boolean isPartner) {
		ArrayList<String> values = new ArrayList<String>(7);

		values.add(printBuilder.getId(indi, "", "").toString());

		values.add(printBuilder.getGender(indi, "M", "F", "", "").toString());

		if (family != null) {
			values.add(printBuilder.getRelationship(family, "married", "divorced", /*"unmarried"*/"", "", "").toString());
		} else {
			//Empty placeholder for relationship
			values.add("");
		}

		String names = printBuilder.getFirstName(indi, "", "").toString();
		int secondNamesStart = names.indexOf(" ");

		//Split first and second names by first space character
		if (secondNamesStart != -1) {
			//First name
			values.add(names.substring(0, secondNamesStart));
			//Second names
			values.add(names.substring(secondNamesStart + 1, names.length()));
		} else {
			//First name
			values.add(names);
			//Empty placeholder for second names
			values.add("");
		}

		values.add(printBuilder.getMaidenName(indi, "", "", false).toString());
		values.add(printBuilder.getMarriedName(indi, family, "", "", false).toString());

		StringBuilder birthDate = printBuilder.getBirthDate(indi, "", "");

		if (birthDate.length() > 0) {
			StringBuilder deathDate = printBuilder.getDeathDate(indi, "", "");

			values.add(birthDate.toString());

			if (deathDate.length() > 0) {
				values.add(deathDate.toString());
			} else {
				//Empty placeholder for death
				values.add("");
			}

		} else {
			//Empty placeholder for birth and death
			values.add("");
			values.add("");
		}


		StringBuilder email = printBuilder.getEmail(indi, "", "");

		if (email.length() > 0) {
			values.add(email.toString());
		} else {
			//Empty placeholder for email
			values.add("");
		}

		ArrayList<String> addressParts = printBuilder.getAddressParts(indi, true);

		for (String part : addressParts) {
			values.add(part);
		}

		return values;
	}

	@Override
	public ArrayList<String> createAdditionalLine(GedcomIndividual indi,
			GedcomIndividual partner, GedcomFamily family, boolean isPartner) {
		//No additional line. Everything is on one line
		return null;
	}

	/**
	 * This method has to create the headers for the table. The headers have
	 * to appear in the order of the columns.
	 * 
	 * @return
	 */
	protected ArrayList<String> getCSVHeader() {
		ArrayList<String> header = new ArrayList<String>(15);

		header.add("id");
		header.add("gender");
		header.add("civil_status");
		header.add("name");
		header.add("middle_names");
		header.add("maiden_name");
		header.add("married_name");
		header.add("birth_date");
		header.add("death_date");
		header.add("email");
		header.add("street1");
		header.add("street2");
		header.add("post");
		header.add("city");
		header.add("country");

		return header;

	}

}
