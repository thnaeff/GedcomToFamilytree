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
package ch.thn.gedcom.familytree;

import java.util.List;

import ch.thn.gedcom.creator.GedcomCreatorStructureStorage;
import ch.thn.gedcom.creator.GedcomEnums.Sex;
import ch.thn.gedcom.creator.structures.GedcomFamily;
import ch.thn.gedcom.creator.structures.GedcomIndividual;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomToFamilyTree {
	
	private GedcomCreatorStructureStorage structureStorage = null;
		
	private FamilyTree familyTree = null;
	
	
	public GedcomToFamilyTree(GedcomCreatorStructureStorage structureStorage) {
		this.structureStorage = structureStorage;
		
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomCreatorStructureStorage getStorage() {
		return structureStorage;
	}
	
	/**
	 * Returns the family tree structure (the first node of the tree).
	 * 
	 * @return
	 */
	public FamilyTree getFamilyTree() {
		return familyTree;
	}
	
	
	/**
	 * Build the family tree using the added individuals and families. The starting 
	 * individual of the tree will be the one which has the given individual ID
	 * 
	 * @param individualId The ID to start with
	 * @return
	 * @throws GedcomToFamilytreeError If there is no individual with the given ID
	 */
	public FamilyTree buildFamilyTree(String individualId) {
		return buildFamilyTree(individualId, null);
	}
	
	/**
	 * Build the family tree using the added individuals and families. The starting 
	 * individual of the tree will be the one which has the given individual ID
	 * 
	 * @param individualId The ID to start with
	 * @param treeTitle
	 * @return
	 * @throws GedcomToFamilytreeError If there is no individual with the given ID
	 */
	public FamilyTree buildFamilyTree(String individualId, String treeTitle) {
		
		if (!structureStorage.hasIndividual(individualId)) {
			throw new GedcomToFamilytreeError("Failed to build family tree. Individual with ID " + 
					individualId + " does not exist.");
		}
		
		if (structureStorage.structuresModified()) {
			//Make sure the relations are built
			structureStorage.buildFamilyRelations();
		}
		
		FamilyTree familyTree = new FamilyTree(treeTitle);
		
		//Start building the tree by adding the first child
		addChild(familyTree, structureStorage.getIndividual(individualId));
		
		this.familyTree = familyTree;
		
		return familyTree;
	}
	
	/**
	 * Add a child for building the family tree
	 * 
	 * @param treeNode
	 * @param child
	 */
	private void addChild(FamilyTreeNode treeNode, GedcomIndividual child) {		
				
		//Get all the links to this individuals families
		List<String> famsLinks = child.getSpouseFamilyLinks();
		
		//No family for this individual
		if (famsLinks == null || famsLinks.size() == 0) {
			addOneIndividualFamily(treeNode, child);
			return;
		}
		
		
		boolean familyAdded = false;
		
		//Process the family (or families) for this individual
		for (String famXRef : famsLinks) {
			if (famXRef == null || famXRef.length() == 0) {
				//An empty FAMS link
				continue;
			}
			
			if (!structureStorage.hasFamily(famXRef)) {
//				throw new GedcomToFamilytreeError("Individual " + childId + " is linked to family " + 
//						famXRef + ", but such a family has not been found. Family skipped.");
				System.err.println("Individual " + child.getId() + " is linked to family " + 
						famXRef + ", but such a family has not been found. Family skipped.");
				continue;
			}
			
			
			GedcomFamily family = structureStorage.getFamily(famXRef);
						
			String husbXRef = family.getHusbandLink();
			String wifeXRef = family.getWifeLink();				
			List<String> childXRefs = family.getChildLinks();
						
			addFamily(treeNode, husbXRef, wifeXRef, child.getId(), childXRefs);
			
			familyAdded = true;
		}
		
		if (!familyAdded) {
			addOneIndividualFamily(treeNode, child);
		}
	}
	
	/**
	 * 
	 * 
	 * @param treeNode
	 * @param indi
	 */
	private void addOneIndividualFamily(FamilyTreeNode treeNode, GedcomIndividual indi) {
		String husbXRef = null;
		String wifeXRef = null;
		
		Sex sex = indi.getSex();
		
		if (sex == Sex.FEMALE) {
			wifeXRef = indi.getId();
		} else {
			//Assume a male individual if set is not set and it is not a female
			husbXRef = indi.getId();
		}
		
		addFamily(treeNode, husbXRef, wifeXRef, indi.getId(), null);
	}
	
	/**
	 * Add a family for building the family tree
	 * 
	 * @param treeNode
	 * @param husbXRef
	 * @param wifeXRef
	 * @param childOfParentsId The ID of the husband or wife which is the child 
	 * of the parents of this new family -> childOfParentsId matches either husbXRef 
	 * or wifeXRef
	 * @param childXRefs
	 */
	private void addFamily(FamilyTreeNode treeNode, String husbXRef, String wifeXRef, 
			String childOfParentsId, List<String> childXRefs) {
		
		GedcomIndividual husband = null;
		GedcomIndividual wife = null;
		
		if (husbXRef != null) {
			if (!structureStorage.hasIndividual(husbXRef)) {
				System.out.println("Can not create family with husband " + husbXRef + 
						", wife " + wifeXRef + " and children " + childXRefs + 
						". ID of husband not found.");
				return;
			}

			husband = structureStorage.getIndividual(husbXRef);
		}
		
		if (wifeXRef != null) {
			if (!structureStorage.hasIndividual(wifeXRef)) {
				System.out.println("Can not create family with husband " + husbXRef + 
						", wife " + wifeXRef + " and children " + childXRefs + 
						". ID of wife not found.");
				return;
			}

			wife = structureStorage.getIndividual(wifeXRef);
		}
		
		FamilyTreeNode newNode = null;
		GedcomFamily family = structureStorage.getFamilyOfParents(husbXRef, wifeXRef);
		
		//
		if (childOfParentsId.equals(husbXRef)) {
			newNode = new FamilyTreeNode(husband, wife, family);
		} else {
			newNode = new FamilyTreeNode(wife, husband, family);
		}
		
		treeNode.addChildNode(newNode);
				
		if (childXRefs != null && childXRefs.size() > 0) {
			//Add all the children of this family
			for (String childXRef : childXRefs) {
				if (structureStorage.hasIndividual(childXRef)) {
					addChild(newNode, structureStorage.getIndividual(childXRef));
				}
			}	
		}
		
	}
	

}
