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

import ch.thn.gedcom.creator.GedcomCreatorFamily;
import ch.thn.gedcom.familytree.FamilyTreeNode;
import ch.thn.gedcom.familytree.GedcomToFamilytree;
import ch.thn.gedcom.familytree.GedcomToFamilytreeIndividual;
import ch.thn.util.tree.printable.TreePrinter;
import ch.thn.util.tree.printable.printer.TextTreePrinterLines;
import ch.thn.util.tree.printable.printer.TreePrinterNode;
import ch.thn.util.tree.printable.printer.TreePrinterTree;
import ch.thn.util.tree.printable.printer.vertical.GenericVerticalCSVTreePrinter;

/**
 * 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilytreeCSVPrinter 
	extends GenericVerticalCSVTreePrinter<String, GedcomToFamilytreeIndividual[], FamilyTreeNode> implements FamilytreePrinter {
	
	private FamilyTreePrintBuilder printBuilder = null;
		
	/**
	 * 
	 * 
	 * @param toFamilyTree
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
	public FamilytreeCSVPrinter(GedcomToFamilytree toFamilyTree, boolean alignValuesRight, 
			boolean showId, boolean showGender, boolean showRelationship, boolean showEmail, 
			boolean showAddress, boolean showAgeForDead, boolean showBirthDate, 
			boolean showDeathDate, boolean showFirstName, boolean showMaidenName, boolean showMarriedName, 
			boolean showDivorcedPartnerWithoutChildren, boolean showDivorcedPartnerWithChildren) {
		super(true, true, true, alignValuesRight, true);
		
		printBuilder = new FamilyTreePrintBuilder(toFamilyTree, showId, showGender, 
				showRelationship, showEmail, showAddress, showAgeForDead, 
				showBirthDate, showDeathDate, showFirstName, showMaidenName, showMarriedName, 
				showDivorcedPartnerWithoutChildren, showDivorcedPartnerWithChildren);
		
	}
	
	@Override
	public FamilyTreePrintBuilder getPrintBuilder() {
		return printBuilder;
	}
	
	@Override
	protected TextTreePrinterLines getNodeData(FamilyTreeNode node) {
		
		return printBuilder.createNodeValueLines(node, this, false, true);
		
	}
	
	@Override
	protected void preProcessingNode(
			TreePrinterNode<String, TextTreePrinterLines> printerNode,
			int currentNodeLevel, int currentNodeIndex, int currentNodeCount,
			boolean isHeadNode, boolean isFirstChildNode,
			boolean isLastChildNode, boolean hasChildNodes) {
	}

	@Override
	protected void postProcessingNode(
			TreePrinterNode<String, TextTreePrinterLines> printerNode,
			int currentNodeLevel, int currentNodeIndex, int currentNodeCount,
			boolean isHeadNode, boolean isFirstChildNode,
			boolean isLastChildNode, boolean hasChildNodes) {
	}
	
	@Override
	public ArrayList<String> createPrimaryLine(GedcomToFamilytreeIndividual indi, 
			GedcomToFamilytreeIndividual partner, GedcomCreatorFamily family, boolean isPartner) {
		ArrayList<String> values = new ArrayList<String>(7);
		
		values.add(printBuilder.getId(indi, "", "").toString());
		
		values.add(printBuilder.getGender(indi, "M", "F", "", "").toString());
		
		if (family != null) {
			values.add(printBuilder.getRelationship(family, "married", "divorced", "unmarried", "", "").toString());
		} else {
			//Empty placeholder for relationship
			values.add("");
		}
		
		String names = printBuilder.getFirstName(indi, "", "").toString();
		int secondNamesStart = names.indexOf(" ");
		
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
	public ArrayList<String> createAdditionalLine(GedcomToFamilytreeIndividual indi, 
			GedcomToFamilytreeIndividual partner, GedcomCreatorFamily family, boolean isPartner) {
		//No additional line. Everything is on one line
		return null;
	}
	
	
	
	@Override
	protected StringBuilder createPrinterOutput(
			ArrayList<TreePrinterTree<String, TextTreePrinterLines>> preparedTrees) {
		
		StringBuilder sb = new StringBuilder();
		
		//The header for the very first tree only. If the head node is invisible, 
		//TreePrinter would create a tree for each child node -> should not happen 
		//anyways in a family tree...
		
		if (alignValuesRight()) {
			//Align the header over the actual data by creating empty cells 
			//over the connector lines
			for (int i = 0; i < preparedTrees.get(0).getHightestNodeLevel(); i++) {
				sb.append("L_" + i + DELIMITER);
			}
		}
		
		ArrayList<String> header = getCSVHeader();
		
		for (String s : header) {
			sb.append(s);
			sb.append(DELIMITER);
		}
		
		sb.append(TreePrinter.LINE_SEPARATOR);
		
		sb.append(super.createPrinterOutput(preparedTrees));
		
		return sb;
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
