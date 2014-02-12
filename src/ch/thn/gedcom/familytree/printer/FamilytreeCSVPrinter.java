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
import ch.thn.gedcom.familytree.GedcomToFamilytree;
import ch.thn.gedcom.familytree.GedcomToFamilytreeIndividual;
import ch.thn.util.tree.printable.PrintableTreeNode;
import ch.thn.util.tree.printable.printer.TextTreePrinterLines;
import ch.thn.util.tree.printable.printer.TreePrinter;
import ch.thn.util.tree.printable.printer.TreePrinterTree;
import ch.thn.util.tree.printable.printer.vertical.VerticalCSVTreePrinter;

/**
 * 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilytreeCSVPrinter extends VerticalCSVTreePrinter<String, GedcomToFamilytreeIndividual[]> implements FamilytreePrinter {
	
	private FamilyTreePrintBuilder printBuilder = null;
		
	/**
	 * 
	 * 
	 * @param toFamilyTree
	 * @param alignValuesRight
	 * @param showGender
	 * @param showRelationship
	 * @param showEmail
	 * @param showAddress
	 * @param showAge
	 * @param showBirthDate
	 * @param showDeathDate
	 * @param showMarriedName
	 * @param printDivorced
	 */
	public FamilytreeCSVPrinter(GedcomToFamilytree toFamilyTree, boolean alignValuesRight, 
			boolean showGender, boolean showRelationship, boolean showEmail, 
			boolean showAddress, boolean showAge, boolean showBirthDate, boolean showDeathDate, 
			boolean showMarriedName, boolean printDivorced) {
		super(true, true, true, alignValuesRight, true);
		
		printBuilder = new FamilyTreePrintBuilder(toFamilyTree, showGender, 
				showRelationship, showEmail, showAddress, false, showBirthDate, 
				showDeathDate, showMarriedName, printDivorced);
		
	}
	
	@Override
	public FamilyTreePrintBuilder getPrintBuilder() {
		return printBuilder;
	}
	
	@Override
	protected TextTreePrinterLines getNodeData(
			PrintableTreeNode<String, GedcomToFamilytreeIndividual[]> node) {
		
		return printBuilder.createNodeValueLines(node, this, false, true);
		
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
		
		values.add(printBuilder.getLastName(indi, "", "").toString());
		values.add(printBuilder.getMarriedName(indi, family, "", "").toString());
		
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
		
		StringBuilder address = printBuilder.getAddress(indi, "", "");
		
		if (address.length() > 0) {
			values.add(address.toString());
		} else {
			//Empty placeholder for address
			values.add("");
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
				sb.append(DELIMITER);
			}
		}
		
		sb.append("id");
		sb.append(DELIMITER);
		sb.append("gender");
		sb.append(DELIMITER);
		sb.append("civil_status");
		sb.append(DELIMITER);
		sb.append("name");
		sb.append(DELIMITER);
		sb.append("middle_names");
		sb.append(DELIMITER);
		sb.append("family_name");
		sb.append(DELIMITER);
		sb.append("married_name");
		sb.append(DELIMITER);
		sb.append("birth_date");
		sb.append(DELIMITER);
		sb.append("death_date");
		sb.append(DELIMITER);
		sb.append("email");
		sb.append(DELIMITER);
		sb.append("address");	
		sb.append(DELIMITER);
		
		sb.append(TreePrinter.LINE_SEPARATOR);
		
		sb.append(super.createPrinterOutput(preparedTrees));
		
		return sb;
	}

	
}
