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

import java.util.ArrayList;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.creator.GedcomCreatorFamily;
import ch.thn.gedcom.creator.GedcomCreatorEnums.NameType;
import ch.thn.gedcom.creator.GedcomCreatorEnums.Sex;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.gedcom.familytree.FamilyTree;
import ch.thn.gedcom.familytree.FamilyTreeNode;
import ch.thn.gedcom.familytree.GedcomToFamilytree;
import ch.thn.gedcom.familytree.GedcomToFamilytreeIndividual;
import ch.thn.util.StringUtil;
import ch.thn.util.tree.printable.printer.TextTreePrinterLines;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilyTreePrintBuilder {
	
	protected static final String SPACE = " ";

	public static final String dateFormatYear = "yyyy";
	public static final String dateFormatYearMonth = "MM.yyyy";
	public static final String dateFormatYearMonthDay = "dd.MM.yyyy";
	
	private GedcomToFamilytree toFamilyTree = null;
		
	private boolean showId = true;
	private boolean showGender = true;
	private boolean showRelationship = true;
	private boolean showEmail = true;
	private boolean showAddress = true;
	private boolean showAgeForDead = true;
	private boolean showBirthDate = true;
	private boolean showDeathDate = true;
	private boolean showFirstName = true;
	private boolean showMaidenName = true;
	private boolean showMarriedName = true;
	private boolean showDivorcedPartnerWithoutChildren = true;
	private boolean showDivorcedPartnerWithChildren = true;
	
	//Useful UTF8 symbols: http://utf8-characters.com/miscellaneous-symbols/
	
	/**
	 * 
	 * 
	 * @param toFamilyTree
	 * @param showId
	 * @param showGender
	 * @param showRelationship
	 * @param showEmail
	 * @param showAddress
	 * @param showAgeForDead
	 * @param showBirthDate
	 * @param showDeathDate
	 * @param showMaidenName
	 * @param showMarriedName
	 * @param showDivorcedPartnerWithoutChildren
	 * @param showDivorcedPartnerWithChildren
	 */
	public FamilyTreePrintBuilder(GedcomToFamilytree toFamilyTree, boolean showId, 
			boolean showGender, boolean showRelationship, boolean showEmail, 
			boolean showAddress, boolean showAgeForDead, boolean showBirthDate, 
			boolean showDeathDate, boolean showFirstName, boolean showMaidenName, boolean showMarriedName, 
			boolean showDivorcedPartnerWithoutChildren, boolean showDivorcedPartnerWithChildren) {
		
		this.toFamilyTree = toFamilyTree;
		this.showId = showId;
		this.showGender = showGender;
		this.showRelationship = showRelationship;
		this.showEmail = showEmail;
		this.showAddress = showAddress;
		this.showAgeForDead = showAgeForDead;
		this.showBirthDate = showBirthDate;
		this.showDeathDate = showDeathDate;
		this.showFirstName = showFirstName;
		this.showMaidenName = showMaidenName;
		this.showMarriedName = showMarriedName;
		this.showDivorcedPartnerWithoutChildren = showDivorcedPartnerWithoutChildren;
		this.showDivorcedPartnerWithChildren = showDivorcedPartnerWithChildren;
		
	}
	
	/**
	 * @return the showId
	 */
	public boolean showId() {
		return showId;
	}
	
	/**
	 * @return the showGender
	 */
	public boolean showGender() {
		return showGender;
	}
	
	/**
	 * @return the showRelationship
	 */
	public boolean showRelationship() {
		return showRelationship;
	}
	
	/**
	 * @return the showEmail
	 */
	public boolean showEmail() {
		return showEmail;
	}
	
	/**
	 * @return the showAddress
	 */
	public boolean showAddress() {
		return showAddress;
	}
	
	/**
	 * @return the showAgeForDead
	 */
	public boolean showAgeForDead() {
		return showAgeForDead;
	}
	
	/**
	 * @return the showBirthDate
	 */
	public boolean showBirthDate() {
		return showBirthDate;
	}
	
	/**
	 * @return the showDeathDate
	 */
	public boolean showDeathDate() {
		return showDeathDate;
	}
	
	/**
	 * @return the showFirstName
	 */
	public boolean showFirstName() {
		return showFirstName;
	}
	
	/**
	 * @return the showMaidenName
	 */
	public boolean showMaidenName() {
		return showMaidenName;
	}
	
	/**
	 * @return the showMarriedName
	 */
	public boolean showMarriedName() {
		return showMarriedName;
	}
	
	/**
	 * @return the showDivorcedPartnerWithChildren
	 */
	public boolean showDivorcedPartnerWithChildren() {
		return showDivorcedPartnerWithChildren;
	}
	
	/**
	 * @return the showDivorcedPartnerWithoutChildren
	 */
	public boolean showDivorcedPartnerWithoutChildren() {
		return showDivorcedPartnerWithoutChildren;
	}
	
	
	
	/**
	 * Returns only the ID of the individual and removes any individual-id-prefix 
	 * {@link GedcomToFamilytree#indiXRefPrefix}.
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getId(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showId) {
			String id = indi.getId();
			
			sb.append(prefix);
			
			if (id.startsWith(GedcomToFamilytree.indiXRefPrefix)) {
				sb.append(id.substring(GedcomToFamilytree.indiXRefPrefix.length()));
			} else {
				sb.append(id);
			}
			
			sb.append(postfix);
		}
		
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
			
			if (indi.isBorn()) {
				sb.append(prefix);
				
				String birthDate = indi.getBirthDate();
				if (birthDate == null || birthDate.length() == 0) {
					sb.append("?");
				} else {
					sb.append(GedcomFormatter.convertGedcomDate(birthDate, dateFormatYear, dateFormatYearMonth, dateFormatYearMonthDay));
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
			
			if (indi.isDead()) {
				sb.append(prefix);
				
				String deathDate = indi.getDeathDate();
				if (deathDate == null || deathDate.length() == 0) {
					sb.append("?");
				} else {
					sb.append(GedcomFormatter.convertGedcomDate(deathDate, dateFormatYear, dateFormatYearMonth, dateFormatYearMonthDay));
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
		
		if (showAgeForDead) {
			
			if (indi.isBorn() && indi.isDead()) {
				sb.append(prefix);
				
				//Age of dead individual
				sb.append(GedcomHelper.getAge(
						GedcomFormatter.getDateFromGedcom(indi.getBirthDate()), 
						GedcomFormatter.getDateFromGedcom(indi.getDeathDate())));
				
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
			
			if (Sex.MALE.equals(indi.getSex())) {
				sb.append(prefix);
				sb.append(male);
				sb.append(postfix);
			} else if (Sex.FEMALE.equals(indi.getSex())) {
				sb.append(prefix);
				sb.append(female);
				sb.append(postfix);
			}
			
		}
		
		return sb;
	}
	
	/**
	 * Returns the first name which occurs last in the list of names
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public StringBuilder getFirstName(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		
		if (showFirstName) {
			int names = indi.getNumberOfNames();
			
			if (names > 0) {
				sb.append(prefix);
				
				//Use the first name of the name which occurs last in the list
				//Remove commas from given name
				sb.append(indi.getGivenName(names - 1).replace(",", ""));
				
				sb.append(postfix);
			}
		}
		
		return sb;
	}
	
	/**
	 * Returns the last name which occurs last in the list of non-married (maiden) names
	 * 
	 * @param indi
	 * @param prefix
	 * @param postfix
	 * @param forceReturnMaidenName
	 * @return
	 */
	public StringBuilder getMaidenName(GedcomToFamilytreeIndividual indi, 
			String prefix, String postfix, boolean forceReturnMaidenName) {
		StringBuilder sb = new StringBuilder();
		
		if (showMaidenName || forceReturnMaidenName) {
			int names = indi.getNumberOfNames();
			
			if (names > 0) {
				int lastOtherNameIndex = -1;
				
				//Get the last occurring married name and the last occurring other name
				for (int i = 0; i < names; i++) {
					if (!NameType.MARRIED.equals(indi.getNameType(i))) {
						lastOtherNameIndex = i;
					}
				}
				
				if (lastOtherNameIndex != -1) {
					sb.append(prefix);
					
					//Surname
					sb.append(indi.getSurname(lastOtherNameIndex));
					
					sb.append(postfix);
				}
				
			}
		}
		
		return sb;
	}
	
	/**
	 * Returns the married name which occurs last in the list or married names
	 * 
	 * @param indi
	 * @param family
	 * @param prefix
	 * @param postfix
	 * @param forceReturnMarriedName
	 * @return
	 */
	public StringBuilder getMarriedName(GedcomToFamilytreeIndividual indi, GedcomCreatorFamily family, 
			String prefix, String postfix, boolean forceReturnMarriedName) {
		StringBuilder sb = new StringBuilder();
		
		if (showMarriedName || forceReturnMarriedName) {
			int names = indi.getNumberOfNames();
			
			if (names > 0) {
				int lastMarriedNameIndex = -1;
				
				//Get the last occurring married name and the last occurring other name
				for (int i = 0; i < names; i++) {
					if (NameType.MARRIED.equals(indi.getNameType(i))) {
						lastMarriedNameIndex = i;
					}
				}
				
					
				//The married name if there is a married name
				if (lastMarriedNameIndex != -1) {
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
	 * Returns the last address in the list
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
			int numOfAddresses = indi.getNumberOfAddresses();
			String addr = indi.getAddress(numOfAddresses - 1);
			
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
	 * Returns a list of all the address parts after each other.
	 * 
	 * @param indi
	 * @param includeEmpty
	 * @return
	 */
	public ArrayList<String> getAddressParts(GedcomToFamilytreeIndividual indi, boolean includeEmpty) {
		ArrayList<String> addressParts = new ArrayList<String>();
		
		if (showAddress) {
			int numOfAddresses = indi.getNumberOfAddresses();
			
			String addrData = null;
			
			addrData = indi.getStreet1(numOfAddresses - 1);
			if (includeEmpty || addrData != null) {
				addressParts.add(addrData);
			}
			
			addrData = indi.getStreet2(numOfAddresses - 1);
			if (includeEmpty || addrData != null) {
				addressParts.add(addrData);
			}
			
			addrData = indi.getPost(numOfAddresses - 1);
			if (includeEmpty || addrData != null) {
				addressParts.add(addrData);
			}
			
			addrData = indi.getCity(numOfAddresses - 1);
			if (includeEmpty || addrData != null) {
				addressParts.add(addrData);
			}
			
			addrData = indi.getCountry(numOfAddresses - 1);
			if (includeEmpty || addrData != null) {
				addressParts.add(addrData);
			}
			
		}
		
		return addressParts;
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
				
				if (family.isMarried()) {
					sb.append(married);
				} else if (family.isDivorced()) {
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
	public TextTreePrinterLines createNodeValueLines(FamilyTreeNode currentNode, 
			FamilytreePrinter printer, boolean addEmptyLineAtEnd, boolean replaceNullValue) {
		TextTreePrinterLines lines = new TextTreePrinterLines(false, replaceNullValue, null, "");
		
		if (currentNode instanceof FamilyTree) {
			//Only the title
			lines.addNewLine(((FamilyTree)currentNode).getFamilyTreeTitle());
			
			return lines;
		}
		
		//The individuals
		GedcomToFamilytreeIndividual[] values = currentNode.getIndividuals();
		
		GedcomCreatorFamily family = getFamily(values[0], values[1]);
		
		//Descendant
		lines.addNewLine(printer.createPrimaryLine(values[0], values[1], family, false));
		lines.addNewLine(printer.createAdditionalLine(values[0], values[1], family, false));
		
		//Partner of descendant
		if (values[1] != null) {
			boolean printPartner = true;
			
			if (family.isDivorced()) {
				if (family.getNumberOfChildren() == 0) {
					//Divorced but without children
					if (!showDivorcedPartnerWithoutChildren) {
						printPartner = false;
					}
				} else {
					//Divorced and with children
					if (!showDivorcedPartnerWithChildren) {
						printPartner = false;
					}
				}
			}
			
			
			//Partner of descendant (partner 2)
			if (printPartner) {
				
				//Partner of descendant
				if (values[1] != null) {
					lines.addNewLine(printer.createPrimaryLine(values[1], values[0], family, true));
					lines.addNewLine(printer.createAdditionalLine(values[1], values[0], family, true));
				}
			}
		}
		
		if (addEmptyLineAtEnd) {
			lines.addNewLine("");
		}
		
		return lines;
	}

}
