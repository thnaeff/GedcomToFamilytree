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
import ch.thn.gedcom.familytree.FamilyTreeNode;
import ch.thn.gedcom.familytree.GedcomToFamilytreeIndividual;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilytreeBirthDateSorter implements FamilyTreeSorter {

	boolean oldToYoung = true;
	
	/**
	 * A birth date sorter with the default sorting direction from old to young
	 * 
	 */
	public FamilytreeBirthDateSorter() {
		//Sort direction up (old to young)
	}
	
	/**
	 * 
	 * 
	 * @param oldToYoung Choose the sorting direction. <code>true</code>: from old to young, 
	 * <code>false</code>: from young to old.
	 */
	public FamilytreeBirthDateSorter(boolean oldToYoung) {
		this.oldToYoung = oldToYoung;
	}

	@Override
	public int compare(FamilyTreeNode treeNode1, FamilyTreeNode treeNode2) {
		GedcomToFamilytreeIndividual indi1 = treeNode1.getNodeValue()[0];
		GedcomToFamilytreeIndividual indi2 = treeNode2.getNodeValue()[0];
		
		if (indi1 == null || indi2 == null) {
			return 0;
		}
		
		//Sort by birth date
		int beforeOrAfter = GedcomHelper.isBeforeOrAfter(GedcomFormatter.getDate(indi1.getBirthDate()), GedcomFormatter.getDate(indi2.getBirthDate()));
		
		if (oldToYoung) {
			return (-1) * beforeOrAfter;
		} else {
			return beforeOrAfter;
		}
	}
	


}
