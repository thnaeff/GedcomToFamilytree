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
import ch.thn.gedcom.creator.GedcomCreatorEnums.Sex;
import ch.thn.gedcom.familytree.FamilyTreeNode;
import ch.thn.gedcom.familytree.GedcomToFamilytree;
import ch.thn.gedcom.familytree.GedcomToFamilytreeIndividual;
import ch.thn.util.tree.printable.TreePrinter;
import ch.thn.util.tree.printable.printer.TextTreePrinterLines;
import ch.thn.util.tree.printable.printer.TreePrinterNode;
import ch.thn.util.tree.printable.printer.vertical.GenericVerticalHTMLTreePrinter;

/**
 * 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilytreeHTMLPrinter 
	extends GenericVerticalHTMLTreePrinter<String, GedcomToFamilytreeIndividual[], FamilyTreeNode> implements FamilytreePrinter {
	
	private static final String HTMLSPACE = "&nbsp;";
	
	private FamilyTreePrintBuilder printBuilder = null;
		
	/**
	 * 
	 * 
	 * @param toFamilyTree
	 * @param useColors
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
	public FamilytreeHTMLPrinter(GedcomToFamilytree toFamilyTree, 
			boolean useColors, boolean showId, 
			boolean showGender, boolean showRelationship, boolean showEmail, 
			boolean showAddress, boolean showAgeForDead, boolean showBirthDate, 
			boolean showDeathDate, boolean showFirstName, boolean showMaidenName, boolean showMarriedName, 
			boolean showDivorcedPartnerWithoutChildren, boolean showDivorcedPartnerWithChildren) {
		super(true, true, useColors, true, false);
		
		printBuilder = new FamilyTreePrintBuilder(toFamilyTree, showId, showGender, 
				showRelationship, showEmail, showAddress, showAgeForDead, 
				showBirthDate, showDeathDate, showFirstName, showMaidenName, showMarriedName, 
				showDivorcedPartnerWithoutChildren, showDivorcedPartnerWithChildren);
		
		//This makes sure that also the alignment of the additional lines at 
		//the very end of a branch are correct
		ADDITIONALLINEAFTEREND = "";
		
	}
	
	@Override
	public FamilyTreePrintBuilder getPrintBuilder() {
		return printBuilder;
	}
	
	
	@Override
	protected TextTreePrinterLines getNodeData(FamilyTreeNode node) {
		
		return printBuilder.createNodeValueLines(node, this, true, true);
		
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
		ArrayList<String> values = new ArrayList<String>(1);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(printBuilder.getId(indi, "<b>", "</b> "));	

		if (Sex.MALE.equals(indi.getSex())) {
			//Male
			sb.append(printBuilder.getGender(indi, String.valueOf((char)0x2642), String.valueOf((char)0x2640), "<span style='color:#6666FF;' title='Male'>", "</span> "));
		} else {
			//Female
			sb.append(printBuilder.getGender(indi, String.valueOf((char)0x2642), String.valueOf((char)0x2640), "<span style='color:#FF3399;' title='Female'>", "</span> "));
		}

		if (family != null) {
			StringBuilder relationship = printBuilder.getRelationship(family, String.valueOf((char)0x26AD), String.valueOf((char)0x26AE), String.valueOf((char)0x26AF), "", " ");
			
			if (relationship.length() > 0) {
				//Look for the relationship character and add the title
				if (relationship.indexOf(String.valueOf((char)0x26AD)) != -1) {
					sb.append("<span class='relationship' style='color:#002900;' title='");
					sb.append("Married");
				} else if (relationship.indexOf(String.valueOf((char)0x26AE)) != -1) {
					sb.append("<span class='relationship' style='color:#7A0000;' title='");
					sb.append("Divorced");
					sb.append(" (was married to: ");
					sb.append(printBuilder.getFirstName(partner, "", " "));
					sb.append(printBuilder.getMaidenName(partner, "", "", true));
					sb.append(")");
				} else if (relationship.indexOf(String.valueOf((char)0x26AF)) != -1) {
					sb.append("<span class='relationship' style='color:#858585;' title='");
					sb.append("Unmarried");
				} else {
					sb.append("<span class='relationship' style='color:#D1D1D1;' title='");
					sb.append("Unknown");
				}
				sb.append("'>");
				sb.append(relationship);
				sb.append("</span>");
				
			}
		}
		
		
		sb.append(printBuilder.getFirstName(indi, "", " "));
		
		String married = printBuilder.getMarriedName(indi, family, "", " ", true).toString();
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
		
		sb.append(married);
		
		if (married.length() == 0) {
			//If there is no married name, use the last name
			sb.append(maiden);
			sb.append(" ");
		} else {
			//If there is a married name, also add the last name if there is one
			if (maiden.length() > 0) {
				sb.append("(");
				sb.append(maiden);
				sb.append(") ");
			}
		}
		
		StringBuilder birthDate = printBuilder.getBirthDate(indi, String.valueOf((char)0x274A), "");
		
		if (birthDate.length() > 0) {
			StringBuilder deathDate = printBuilder.getDeathDate(indi, String.valueOf((char)0x271D), "");
			
			sb.append("<span style='color:#848484;'>");
			sb.append("[");
			sb.append(birthDate);
		
			if (deathDate.length() > 0) {
				sb.append(" - ");
				sb.append(deathDate);
				
				sb.append(printBuilder.getAge(indi, " | ", ""));
			}
			
			sb.append("]");
			sb.append("</span>");
		}
		
		values.add(sb.toString());
		return values;
	}
	
	@Override
	public ArrayList<String> createAdditionalLine(GedcomToFamilytreeIndividual indi, 
			GedcomToFamilytreeIndividual partner, GedcomCreatorFamily family, boolean isPartner) {
		ArrayList<String> values = new ArrayList<String>(7);
		
		StringBuilder sb = new StringBuilder();
		
		//A little space in front of the additional line
		sb.append(FamilytreeTextPrinter.createColumnString(printBuilder.getId((isPartner ? partner : indi), "", "").length(), HTMLSPACE));
		
		if (isPartner) {
			//Move the additional line of the partner to the right so that it lines up (about) the same
			sb.append(FamilytreeTextPrinter.createColumnString(printBuilder.getId(indi, "", "").length() + 2, HTMLSPACE));
		}
		
		StringBuilder email = printBuilder.getEmail(indi, (char)0x2709 + " ", " ");
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
			address = printBuilder.getAddress(indi, (char)0x25AA + " ", " ");
		}
		
		if (email.length() > 0 || (withAddress && address.length() > 0)) {
			sb.append("<span class='additionalinfo'>");
			sb.append(email);
			
			empty = false;
			
			if (withAddress) {
				sb.append(address);
			}
			sb.append("</span>");
		}
		
		
		if (empty) {
			//Do not print empty lines
			return null;
		} else {
			values.add(sb.toString());
			return values;
		}
		
	}
	
	
	@Override
	protected void appendHeaderData(StringBuilder sb) {
		
		sb.append("<style>" + TreePrinter.LINE_SEPARATOR);
//		sb.append("td {border:1px solid black}");
		sb.append(".printer_table {border-spacing:0px; border-collapse:collapse;}" + TreePrinter.LINE_SEPARATOR);
		sb.append(".printer_line {padding:0px; width:25px;}" + TreePrinter.LINE_SEPARATOR);
		sb.append(".additionalinfo {font-style:italic;}" + TreePrinter.LINE_SEPARATOR);
		sb.append(".relationship {vertical-align:bottom;}" + TreePrinter.LINE_SEPARATOR);
		sb.append("</style>" + TreePrinter.LINE_SEPARATOR);
		
	}
	
	
}
