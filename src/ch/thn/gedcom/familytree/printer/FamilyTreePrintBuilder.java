/**
 *    Copyright 2014 Thomas Naeff (github.com/thnaeff)
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

import java.text.SimpleDateFormat;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.creator.GedcomCreatorFamily;
import ch.thn.gedcom.creator.GedcomCreatorEnums.NameType;
import ch.thn.gedcom.creator.GedcomCreatorEnums.Sex;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.gedcom.familytree.FamilyTree;
import ch.thn.gedcom.familytree.GedcomToFamilytree;
import ch.thn.gedcom.familytree.GedcomToFamilytreeIndividual;
import ch.thn.util.StringUtil;
import ch.thn.util.tree.printable.PrintableTreeNode;
import ch.thn.util.tree.printable.printer.TextTreePrinterLines;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilyTreePrintBuilder {
	
	protected static final String SPACE = " ";

	
	private GedcomToFamilytree toFamilyTree = null;
	
	private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		
	private boolean showGender = true;
	private boolean showRelationship = true;
	private boolean showEmail = true;
	private boolean showAddress = true;
	private boolean showAge = true;
	private boolean showBirthDate = true;
	private boolean showDeathDate = true;
	private boolean showMarriedName = true;
	private boolean printDivorced = true;
	
	//Useful UTF8 symbols: http://utf8-characters.com/miscellaneous-symbols/
	
	/**
	 * 
	 * 
	 * @param toFamilyTree
	 * @param showGender
	 * @param showRelationship
	 * @param showEmail
	 * @param showAddress
	 * @param showAge
	 * @param showBirthDate
	 * @param showDeathDate
	 * @param showMarriedName
	 */
	public FamilyTreePrintBuilder(GedcomToFamilytree toFamilyTree, boolean showGender, 
			boolean showRelationship, boolean showEmail, boolean showAddress, 
			boolean showAge, boolean showBirthDate, boolean showDeathDate, 
			boolean showMarriedName, boolean printDivorced) {
		
		this.toFamilyTree = toFamilyTree;
		this.showGender = showGender;
		this.showRelationship = showRelationship;
		this.showEmail = showEmail;
		this.showAddress = showAddress;
		this.showAge = showAge;
		this.showBirthDate = showBirthDate;
		this.showDeathDate = showDeathDate;
		this.showMarriedName = showMarriedName;
		this.printDivorced = printDivorced;
		
		
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean printDivorced() {
		return printDivorced;
	}
	
	/**
	 * 
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getId(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		String id = indi.getId();
		
		sb.append(prefix);
		
		if (id.startsWith(GedcomToFamilytree.indiXRefPrefix)) {
			sb.append(id.substring(GedcomToFamilytree.indiXRefPrefix.length()));
		} else {
			sb.append(id);
		}
		
		sb.append(postfix);
		
		return sb;
	}
	
	/**
	 * 
	 * UTF8 asterisk birth symbol: Hex=0x274A,  HTML=&#10058;
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getBirthDate(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showBirthDate) {
			
			if (indi.getBirth()) {
				sb.append(prefix);
				
				String birthDate = indi.getBirthDate();
				if (birthDate == null || birthDate.length() == 0) {
					sb.append("?");
				} else {
					sb.append(df.format(GedcomFormatter.getDate(birthDate)));
				}
				
				sb.append(postfix);
			}
						
		}
		
		return sb;
	}
	
	/**
	 * 
	 * //UTF8 Latin cross death symbol: Hex=0x271D, HTML=&#10013;
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getDeathDate(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showDeathDate) {
			
			if (indi.getDeath()) {
				sb.append(prefix);
				
				String deathDate = indi.getDeathDate();
				if (deathDate == null || deathDate.length() == 0) {
					sb.append("?");
				} else {
					sb.append(df.format(GedcomFormatter.getDate(deathDate)));
				}
				
				sb.append(postfix);
			}
						
		}
		
		return sb;
	}
	
	/**
	 * 
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getAge(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showAge) {
			
			if (indi.getBirth() && indi.getDeath()) {
				sb.append(prefix);
				
				//Age of dead individual
				sb.append(GedcomHelper.getAge(
						GedcomFormatter.getDate(indi.getBirthDate()), 
						GedcomFormatter.getDate(indi.getDeathDate())));
				
				sb.append(postfix);
			}
			
		}
		
		
		return sb;
		
	}
	
	/**
	 * 
	 * //UTF8 male symbol: Hex=0x2642, HTML=&#9794;<br>
	 * //UTF8 female symbol: Hex=0x2640, &#9792;
	 *  
	 * @param indi
	 * @param male
	 * @param female
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getGender(GedcomToFamilytreeIndividual indi, 
			String male, String female, String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showGender) {
			
			if (Sex.MALE.getValue().equals(indi.getSex())) {
				sb.append(prefix);
				sb.append(male);
				sb.append(postfix);
			} else if (Sex.FEMALE.getValue().equals(indi.getSex())) {
				sb.append(prefix);
				sb.append(female);
				sb.append(postfix);
			}
			
		}
		
		return sb;
	}
	
	/**
	 * 
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getFirstName(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		int names = indi.getNumberOfNames();
		
		if (names > 0) {
			int lastMarriedNameIndex = -1;
			int lastOtherNameIndex = -1;
			
			//Get the last occurring married name and the last occurring other name
			for (int i = 0; i < names; i++) {
				if (NameType.MARRIED.getValue().equals(indi.getNameType(i))) {
					lastMarriedNameIndex = i;
				} else {
					lastOtherNameIndex = i;
				}
			}
			
			sb.append(prefix);
			
			if (lastOtherNameIndex != -1) {
				//Remove commas from given name
				sb.append(indi.getGivenName(lastOtherNameIndex).replace(",", ""));
			} else {
				//No other name -> use married name
				
				//Remove commas from given name
				sb.append(indi.getGivenName(lastMarriedNameIndex).replace(",", ""));
			}
			
			sb.append(postfix);
		}
		
		return sb;
	}
	
	/**
	 * 
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getLastName(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		int names = indi.getNumberOfNames();
		
		if (names > 0) {
			int lastMarriedNameIndex = -1;
			int lastOtherNameIndex = -1;
			
			//Get the last occurring married name and the last occurring other name
			for (int i = 0; i < names; i++) {
				if (NameType.MARRIED.getValue().equals(indi.getNameType(i))) {
					lastMarriedNameIndex = i;
				} else {
					lastOtherNameIndex = i;
				}
			}
			
			sb.append(prefix);
			
			if (lastOtherNameIndex != -1) {
				//Surname
				sb.append(indi.getSurname(lastOtherNameIndex));
			} else {
				//No other name -> use married name
				
				//Surname
				sb.append(indi.getSurname(lastMarriedNameIndex));
			}
			
			sb.append(postfix);
		}
		
		return sb;
	}
	
	/**
	 * 
	 * 
	 * @param indi
	 * @param family
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getMarriedName(GedcomToFamilytreeIndividual indi, GedcomCreatorFamily family, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		int names = indi.getNumberOfNames();
		
		if (names > 0) {
			int lastMarriedNameIndex = -1;
			
			//Get the last occurring married name and the last occurring other name
			for (int i = 0; i < names; i++) {
				if (NameType.MARRIED.getValue().equals(indi.getNameType(i))) {
					lastMarriedNameIndex = i;
				}
			}
			
			if (showMarriedName) {
				
				//The married name if there is a married name and if not divorced
				if (family != null && lastMarriedNameIndex != -1 && !family.getDivorced()) {
					sb.append(prefix);
					
					sb.append(indi.getSurname(lastMarriedNameIndex));
					
					sb.append(postfix);
				}
				
			}
			
		}
		
		return sb;
	}
	
	/**
	 * 
	 * //UTF8 Envelope symbol: Hex=0x2709, HTML=&#9993;
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getEmail(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showEmail) {
			String primaryEMail = indi.getEMail(0, 0);
			
			if (primaryEMail != null && primaryEMail.length() > 0) {
				sb.append(prefix);
				
				sb.append(primaryEMail);
				
				sb.append(postfix);
			}
		}
		
		return sb;
	}

	/**
	 * 
	 * //UTF8 Black dot symbol: Hex=0x2981, HTML=&#10625;
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getAddress(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showAddress) {
			String addr = indi.getAddress(0);
			
			if (addr != null && addr.length() > 0) {
				//Replace "empty" commas
				addr = StringUtil.replaceAll(", , ", addr, ", ", true);
				
				sb.append(prefix);
				
				sb.append(addr);
				
				sb.append(postfix);
			}
		}
		
		return sb;
	}
	
	/**
	 * 
	 * //UTF8 Marriage symbol: Hex=0x26AD, HTML=&#9901;<br>
	 * //UTF8 Divorce symbol: Hex=0x26AE, HTML=&#9902;<br>
	 * //UTF8 Unmarried symbol: Hex=0x26AF, HTML=&#9903;
	 * 
	 * @param family
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getRelationship(GedcomCreatorFamily family, 
			String married, String divorced, String unmarried, String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showRelationship) {
			
			if (family != null) {
				sb.append(prefix);
				
				if (family.getMarriage()) {
					sb.append(married);
				} else if (family.getDivorced()) {
					sb.append(divorced);
				} else {
					sb.append(unmarried);
				}
				
				sb.append(postfix);
			}
			
		}
		
		
		return sb;
	}
	
	
	/**
	 * 
	 * 
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public GedcomCreatorFamily getFamily(GedcomToFamilytreeIndividual parent1, 
			GedcomToFamilytreeIndividual parent2) {
		if (parent1 == null || parent2 == null) {
			return null;	
		}
		
		String familyXRef = toFamilyTree.getFamilyOfParents(parent1.getId(), parent2.getId());
		
		if (familyXRef != null) {
			GedcomNode family = toFamilyTree.getFamily(familyXRef);
			return new GedcomCreatorFamily(toFamilyTree.getStore(), family);
		}
		
		return null;
	}
	
	/**
	 * 
	 * 
	 * @param currentNode
	 * @param printer
	 * @param addEmptyLineAtEnd
	 * @param replaceNullValue Replace <code>null</code> values with an empty value
	 * @return
	 */
	public TextTreePrinterLines createNodeValueLines(
			PrintableTreeNode<String, GedcomToFamilytreeIndividual[]> currentNode, 
			FamilytreePrinter printer, boolean addEmptyLineAtEnd, boolean replaceNullValue) {
		TextTreePrinterLines lines = new TextTreePrinterLines(false, replaceNullValue, null, "");
		
		if (currentNode instanceof FamilyTree) {
			//Only the title
			lines.addNewLine(((FamilyTree)currentNode).getFamilyTreeTitle());
			
			return lines;
		}
		
		//The individuals
		GedcomToFamilytreeIndividual[] values = currentNode.getNodeValue();
		
		GedcomCreatorFamily family = getFamily(values[0], values[1]);
		
		//Descendant (partner 1)
		lines.addNewLine(printer.createPrimaryLine(values[0], values[1], family, false));
		lines.addNewLine(printer.createAdditionalLine(values[0], values[1], family, false));
		
		boolean divorced = false;
		
		if (!printDivorced()) {
			//Print always if no family exists
			if (values[0] == null || values[1] == null) {
				divorced = false;
			} else {
				//Do not print if divorced
				if (family.getDivorced()) {
					divorced = true;
				}
			}
		}
		
		
		//Partner of descendant (partner 2)
		//Only show if divorced should be printed
		//or if divorced should not be printed but partner is not divorced
		if ((values[1] != null && printDivorced()) || 
				(values[1] != null && !printDivorced() && !divorced)) {
			
			//Partner of descendant
			if (values[1] != null) {
				lines.addNewLine(printer.createPrimaryLine(values[1], values[0], family, true));
				lines.addNewLine(printer.createAdditionalLine(values[1], values[0], family, true));
			}
		}
		
		if (addEmptyLineAtEnd) {
			lines.addNewLine("");
		}
		
		return lines;
	}

}
