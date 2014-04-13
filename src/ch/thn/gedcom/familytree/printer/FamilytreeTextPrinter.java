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
import ch.thn.util.tree.printable.printer.TextTreePrinterLines;
import ch.thn.util.tree.printable.printer.TreePrinterNode;
import ch.thn.util.tree.printable.printer.TreePrinterTree;
import ch.thn.util.tree.printable.printer.vertical.GenericVerticalTextTreePrinter;

/**
 * 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilytreeTextPrinter extends GenericVerticalTextTreePrinter<String, GedcomToFamilytreeIndividual[], FamilyTreeNode> implements FamilytreePrinter {	
	
	private FamilyTreePrintBuilder printBuilder = null;
	
	boolean addNodeSpace = false;
	
	/**
	 * 
	 * 
	 * @param toFamilyTree
	 * @param addNodeSpace
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
	public FamilytreeTextPrinter(GedcomToFamilytree toFamilyTree, boolean addNodeSpace, boolean showId, 
			boolean showGender, boolean showRelationship, boolean showEmail, 
			boolean showAddress, boolean showAgeForDead, boolean showBirthDate, 
			boolean showDeathDate, boolean showFirstName, boolean showMaidenName, boolean showMarriedName, 
			boolean showDivorcedPartnerWithoutChildren, boolean showDivorcedPartnerWithChildren) {
		super(true, true, true, false);
		
		this.addNodeSpace = addNodeSpace;
		
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
		
		return printBuilder.createNodeValueLines(node, this, addNodeSpace, false);
	
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
		
		values.add(printBuilder.getGender(indi, String.valueOf((char)0x2642), String.valueOf((char)0x2640), "", "").toString());
		
		if (family != null) {
			values.add(printBuilder.getRelationship(family, String.valueOf((char)0x26AD), String.valueOf((char)0x26AE), String.valueOf((char)0x26AF), "", "").toString());
		}
		
		values.add(printBuilder.getFirstName(indi, "", "").toString());
		
		String married = printBuilder.getMarriedName(indi, family, "", "", true).toString();
		String maiden = printBuilder.getMaidenName(indi, "", "", true).toString();
		
		if (!printBuilder.showMarriedName()) {
			if (maiden.length() > 0) {
				//Clear the married name only if there is a maiden name
				married = "";
			}
		}
		if (!printBuilder.showMaidenName()) {
			if (married.length() > 0) {
				//Clear the maiden name only if there is a maiden name
				maiden = "";
			}
		}
		
		values.add(married);
		
		if (married.length() == 0) {
			//If there is no married name, use the last name
			values.add(maiden);
		} else {
			//If there is a married name, also add the last name if there is one
			if (maiden.length() > 0) {
				values.add("(" + maiden + ")");
			}
		}
		
		StringBuilder birthDate = printBuilder.getBirthDate(indi, String.valueOf((char)0x274A), "");
		StringBuilder sbLifespan = new StringBuilder();
		
		if (birthDate.length() > 0) {
			StringBuilder deathDate = printBuilder.getDeathDate(indi, String.valueOf((char)0x271D), "");
			
			sbLifespan.append("[");
			sbLifespan.append(birthDate);
		
			if (deathDate.length() > 0) {
				sbLifespan.append(" - ");
				sbLifespan.append(deathDate);
				
				sbLifespan.append(printBuilder.getAge(indi, " | ", "").toString());
			}
			
			sbLifespan.append("]");
		}
		values.add(sbLifespan.toString());
		
		return values;
	}
	
	@Override
	public ArrayList<String> createAdditionalLine(GedcomToFamilytreeIndividual indi, 
			GedcomToFamilytreeIndividual partner, GedcomCreatorFamily family, boolean isPartner) {
		ArrayList<String> values = new ArrayList<String>(3);
		
		//A little space in front of the additional line
		values.add(FamilytreeTextPrinter.createColumnString(printBuilder.getId((isPartner ? partner : indi), "", "").length(), " "));
		
		if (isPartner) {
			//Extra space for partners on additional lines
			values.add(" ");
		}
		
		StringBuilder email = printBuilder.getEmail(indi, (char)0x2709 + " ", "");
		StringBuilder address = null;
		
		boolean empty = true;
		boolean withAddress = true;
		
		//Do not show the address here if both have the same address
		if ((indi == null || partner == null) 
				|| (indi.getAddress(0) != null && partner.getAddress(0) != null 
					&& indi.getAddress(0).equals(partner.getAddress(0)))) {
			withAddress = false;
		}
		
		if (withAddress) {
			//Hint: When using the bullet point (0x2981) as prefix, the very 
			//last line in the tree appears too far to the left. It seems like that 
			//the spaces in front of the bullet point are smaller than regular spaces. 
			//This only happens to the last line because it only happens if there is 
			//no character in front of the bullet point
			//It happens in the Eclipse Console output and gedit
			address = printBuilder.getAddress(indi, (char)0x25AA + " ", "");
		}
		
		if (email.length() > 0 || (withAddress && address.length() > 0)) {
			values.add(email.toString());
			empty = false;
			
			if (withAddress) {
				values.add(address.toString());
			}
		}
		
		
		if (empty) {
			//Do not print empty lines
			return null;
		} else {
			return values;
		}
	}
	
	
	@Override
	protected void appendValue(StringBuilder sb, String value, int lineIndex,
			int valueIndex, TreePrinterTree<String, TextTreePrinterLines> tree,
			TreePrinterNode<String, TextTreePrinterLines> node,
			TextTreePrinterLines lines) {
		
		if (value != null && value.length() > 0) {
			sb.append(value);
			sb.append(FamilyTreePrintBuilder.SPACE);
		}
		
		
	}
	
	
	/**
	 * This method just repeatedly appends the content of columnContent and returns 
	 * the result.
	 * 
	 * @param numberOfColumns
	 * @param columnContent
	 * @return
	 */
	public static String createColumnString(int numberOfColumns, String columnContent) {
		if (numberOfColumns < 0 || columnContent == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder(numberOfColumns * columnContent.length());
		
		for (int i = 0; i < numberOfColumns; i++) {
			sb.append(columnContent);
		}
		
		return sb.toString();
	}
	
	
}
