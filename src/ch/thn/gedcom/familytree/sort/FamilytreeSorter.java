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
package ch.thn.gedcom.familytree.sort;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.creator.structures.GedcomFamily;
import ch.thn.gedcom.creator.structures.GedcomIndividual;
import ch.thn.gedcom.familytree.FamilyTreeNode;

/**
 * Sorts the families according to the birth date of the individual which follows 
 * the tree. If the birth date is the same, the marriage date is used.
 * 
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilytreeSorter implements FamilyTreeSorter {
	
	/**
	 * 
	 * 
	 */
	public FamilytreeSorter() {
		
	}

	@Override
	public int compare(FamilyTreeNode treeNode1, FamilyTreeNode treeNode2) {
		//This method does not only need to compare the birth dates, but it has to 
		//determine if two families are equal or not.
		//This is necessary since Guavas TreeMultiset (which is used in the FamilyTreeNode 
		//as backing set for the child nodes) counts any elements as equal if they 
		//return equal with this compare method.
		
		
		GedcomIndividual indi10 = treeNode1.getNodeValue()[0];
		GedcomIndividual indi11 = treeNode1.getNodeValue()[1];
		
		GedcomIndividual indi20 = treeNode2.getNodeValue()[0];
		GedcomIndividual indi21 = treeNode2.getNodeValue()[1];
		
		GedcomFamily fam1 = treeNode1.getFamily();
		GedcomFamily fam2 = treeNode2.getFamily();
		
		//Compare families if both nodes have families. Compare individuals otherwise
		if (fam1 != null && fam2 != null && fam1 == fam2) {
			//Same family
			return 0;
		} else if (indi11 == null || indi21 == null) {
			//One of them or both have no partner -> different family
			
			//It could be gone into more detail here by comparing the children of 
			//the families
			
		} else if ((indi10 == indi20 && indi11 == indi21) 
				|| (indi10 == indi21 && indi11 == indi20)) {
			//Same family
			return 0;
		}
		
		//Sort by birth date of the first individual (the first individual is the 
		//one which follows the family tree. The second individual is the partner).
		int birthBeforeOrAfter = GedcomHelper.isBeforeOrAfter(GedcomFormatter.getDateFromGedcom(indi10.getBirthDate()), GedcomFormatter.getDateFromGedcom(indi20.getBirthDate()));
		
		//If the birth dates are not equal, they are not the same person
		if (birthBeforeOrAfter != 0) {
			//Sort from old to young
			return (-1) * birthBeforeOrAfter;
		}
		
		//The birth dates are equal, but they are not the same family
		//-> try to compare the marriage dates
		
		if (fam1 == null || fam2 == null) {
			//No marriage dates to compare -> sort by user ID
			return sortById(indi10, indi11, indi20, indi21);
		}
		
		int marriageBeforeOrAfter = GedcomHelper.isBeforeOrAfter(GedcomFormatter.getDateFromGedcom(fam1.getMarriageDate()), GedcomFormatter.getDateFromGedcom(fam2.getMarriageDate()));
		
		
		if (marriageBeforeOrAfter == 0) {
			//Marriage dates are equal -> sort by user ID
			return sortById(indi10, indi11, indi20, indi21);
		}
		
		return marriageBeforeOrAfter;
	}
	
	/**
	 * 
	 * 
	 * @param indi10
	 * @param indi11
	 * @param indi20
	 * @param indi21
	 * @return
	 */
	private int sortById(GedcomIndividual indi10, GedcomIndividual indi11, 
			GedcomIndividual indi20, GedcomIndividual indi21) {
		
		//Tries to order by integer ID. If not an integer, order lexicographically
		
		String id1 = indi10.getId();
		String id2 = indi20.getId();
		
		int comp = 0;
		
		try {
			int idNum1 = Integer.parseInt(id1);
			int idNum2 = Integer.parseInt(id2);
			comp = Integer.compare(idNum1, idNum2);
		} catch (NumberFormatException e) {
			//Not integers (or invalid integers)
			
			//Compares the ID's lexicographically. This gives at least some ordering 
			//(an individual with a higher ID has most likely been added later), 
			//but it would still order I700 after I1000 (since 7 > 1)
			comp = id1.compareTo(id2);
		}
		
		if (comp != 0) {
			return comp;
		} else {
			if (indi11 == null || indi21 == null) {
				//At least one partner is missing -> different
				return 1;
			}
			
			id1 = indi11.getId();
			id2 = indi21.getId();
			
			try {
				int idNum1 = Integer.parseInt(id1);
				int idNum2 = Integer.parseInt(id2);
				return Integer.compare(idNum1, idNum2);
			} catch (NumberFormatException e) {
				//Not integers (or invalid integers)
				return id1.compareTo(id2);
			}
		}
		
	}
	
}
