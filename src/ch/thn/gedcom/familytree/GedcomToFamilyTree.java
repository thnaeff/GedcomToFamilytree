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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import ch.thn.gedcom.creator.GedcomEnums.Sex;
import ch.thn.gedcom.creator.GedcomFamily;
import ch.thn.gedcom.creator.GedcomIndividual;
import ch.thn.gedcom.data.GedcomError;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.gedcom.store.GedcomStore;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomToFamilyTree {
	
	
	/** All the individuals with their ID */
	private HashMap<String, GedcomIndividual> individuals = null;
	/** All the families with their ID */
	private HashMap<String, GedcomFamily> families = null;
	/** All individual IDs and their families of which they are a parent of */
	private HashMultimap<String, GedcomFamily> familiesAsParent = null;
	/** All individual IDs and their families of which they are a child of */
	private HashMultimap<String, GedcomFamily> familiesAsChild = null;
	
	
	private GedcomStore store = null;
	
	private FamilyTree familyTree = null;
	
	
	public GedcomToFamilyTree(GedcomStore store) {
		this.store = store;
		
		individuals = new HashMap<>();
		families = new HashMap<>();
		familiesAsParent = HashMultimap.create();
		familiesAsChild = HashMultimap.create();
		
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
	 * 
	 * 
	 * @param individual
	 * @return
	 */
	public boolean addIndividual(GedcomIndividual individual) {
		String id = individual.getId();
		
		if (id == null || id.length() == 0) {
			throw new GedcomToFamilytreeError("No ID found for individual");
		}
		
		if (individuals.containsKey(id)) {
			throw new GedcomToFamilytreeError("Can not add individual with ID " + id + ". ID already exists.");
		}
		
		individuals.put(id, individual);
		
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param indiNode
	 * @return
	 */
	public boolean addIndividual(GedcomNode indiNode) {
		verifyNodeStructure(indiNode, "INDIVIDUAL_RECORD");
		return addIndividual(new GedcomIndividual(store, indiNode));
	}
	
	/**
	 * 
	 * 
	 * @param family
	 * @return
	 */
	public boolean addFamily(GedcomFamily family) {
		String id = family.getId();
		
		if (id == null || id.length() == 0) {
			throw new GedcomToFamilytreeError("No ID found for family");
		}
		
		if (families.containsKey(id)) {
			throw new GedcomToFamilytreeError("Can not add family with ID " + id + ". ID already exists.");
		}
		
		families.put(id, family);
		
		String husbXRef = family.getHusbandLink();
		String wiveXRef = family.getWifeLink();
		
		//Save husband link to family
		if (husbXRef != null && husbXRef.length() > 0) {
			familiesAsParent.put(husbXRef, family);
		}
		
		//Save wife link to family
		if (wiveXRef != null && wiveXRef.length() > 0) {
			familiesAsParent.put(wiveXRef, family);
		}
		
		//Save all children links to family
		List<String> children = family.getChildLinks();
		for (String childLink : children) {
			familiesAsChild.put(childLink, family);
		}
		
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param familyNode
	 * @return
	 */
	public boolean addFamily(GedcomNode familyNode) {
		verifyNodeStructure(familyNode, "FAM_RECORD");
		return addFamily(new GedcomFamily(store, familyNode));
	}
	
	/**
	 * 
	 * 
	 * @param indiId
	 * @return
	 */
	public boolean hasIndividual(String indiId) {
		return individuals.containsKey(indiId);
	}
	
	/**
	 * 
	 * 
	 * @param individual
	 * @return
	 */
	public boolean hasIndividual(GedcomIndividual individual) {
		return hasIndividual(individual.getId());
	}
	
	/**
	 * 
	 * 
	 * @param indiNode
	 * @return
	 */
	public boolean hasIndividual(GedcomNode indiNode) {
		verifyNodeStructure(indiNode, "INDIVIDUAL_RECORD");
		return hasIndividual(indiNode.followPath("INDIVIDUAL_RECORD", "INDI").getTagLineXRef());
	}
	
	/**
	 * 
	 * 
	 * @param famId
	 * @return
	 */
	public boolean hasFamily(String famId) {
		return families.containsKey(famId);
	}
	
	/**
	 * 
	 * 
	 * @param family
	 * @return
	 */
	public boolean hasFamily(GedcomIndividual family) {
		return hasFamily(family.getId());
	}
	
	/**
	 * 
	 * 
	 * @param famNode
	 * @return
	 */
	public boolean hasFamily(GedcomNode famNode) {
		verifyNodeStructure(famNode, "FAM_RECORD");
		return hasFamily(famNode.followPath("FAM_RECORD", "FAM").getTagLineXRef());
	}
	
	/**
	 * 
	 * 
	 * @param parentId
	 * @return
	 */
	public Set<GedcomFamily> getFamiliesOfParent(String parentId) {
		return familiesAsParent.get(parentId);
	}
	
	/**
	 * 
	 * 
	 * @param parent
	 * @return
	 */
	public Set<GedcomFamily> getFamiliesOfParent(GedcomIndividual parent) {
		return getFamiliesOfParent(parent.getId());
	}
	
	/**
	 * 
	 * 
	 * @param parentNode
	 * @return
	 */
	public Set<GedcomFamily> getFamiliesOfParent(GedcomNode parentNode) {
		verifyNodeStructure(parentNode, "INDIVIDUAL_RECORD");
		return getFamiliesOfParent(parentNode.followPath("INDIVIDUAL_RECORD", "INDI").getTagLineXRef());
	}
	
	/**
	 * 
	 * 
	 * @param childId
	 * @return
	 */
	public Set<GedcomFamily> getFamilesOfChild(String childId) {
		return familiesAsChild.get(childId);
	}
	
	/**
	 * 
	 * 
	 * @param child
	 * @return
	 */
	public Set<GedcomFamily> getFamilesOfChild(GedcomIndividual child) {
		return getFamilesOfChild(child.getId());
	}
	
	/**
	 * 
	 * 
	 * @param childNode
	 * @return
	 */
	public Set<GedcomFamily> getFamilesOfChild(GedcomNode childNode) {
		verifyNodeStructure(childNode, "INDIVIDUAL_RECORD");
		return getFamilesOfChild(childNode.followPath("INDIVIDUAL_RECORD", "INDI").getTagLineXRef());
	}
	
	/**
	 * 
	 * @param parent1Id
	 * @param parent2Id
	 * @return
	 */
	public GedcomFamily getFamilyOfParents(String parent1Id, String parent2Id) {
		Set<GedcomFamily> families1 = getFamiliesOfParent(parent1Id);
		Set<GedcomFamily> families2 = getFamiliesOfParent(parent2Id);
		

		//A comment in the guava docs: 
		//"I can use intersection as a Set directly, but copying it can be more 
		//efficient if I use it a lot."
		SetView<GedcomFamily> view = Sets.intersection(families1, families2);
		
		if (view.size() > 1) {
			throw new GedcomError("The parents " + parent1Id + " and " + parent2Id + 
					" have been found as parent in more than one family.");
		}
		
		//Returns the first family, or null if there is none
		return Iterables.getFirst(view, null);
	}
	
	/**
	 * 
	 * 
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public GedcomFamily getFamilyOfParents(GedcomIndividual parent1, GedcomIndividual parent2) {
		if (parent1 == null || parent2 == null) {
			return null;
		}
		
		return getFamilyOfParents(parent1.getId(), parent2.getId());
	}
	
	/**
	 * 
	 * 
	 * @param parent1Node
	 * @param parent2Node
	 * @return
	 */
	public GedcomFamily getFamilyOfParents(GedcomNode parent1Node, GedcomNode parent2Node) {
		verifyNodeStructure(parent1Node, "INDIVIDUAL_RECORD");
		verifyNodeStructure(parent2Node, "INDIVIDUAL_RECORD");
		return getFamilyOfParents(
				parent1Node.followPath("INDIVIDUAL_RECORD", "INDI").getTagLineXRef(), 
				parent2Node.followPath("INDIVIDUAL_RECORD", "INDI").getTagLineXRef());
	}
	
	/**
	 * 
	 * 
	 * @param node
	 * @param structureName
	 */
	private void verifyNodeStructure(GedcomNode node, String structureName) {
		if (node != null 
				&& node.getStoreStructure().getStructureName() != null 
				&& node.getStoreStructure().getStructureName().equals(structureName)) {
			return;
		} else {
			throw new GedcomToFamilytreeError("The given node " + node + 
					" is not a structure of the type " + structureName);
		}
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
		
		if (!individuals.containsKey(individualId)) {
			throw new GedcomToFamilytreeError("Failed to build family tree. Individual with ID " + 
					individualId + " does not exist.");
		}
		
		FamilyTree familyTree = new FamilyTree(treeTitle);
		
		//Start building the tree by adding the first child
		addChild(familyTree, individuals.get(individualId));
		
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
		List<String> famsLinks = child.getSpouseLinks();
		
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
			
			if (!families.containsKey(famXRef)) {
//				throw new GedcomToFamilytreeError("Individual " + childId + " is linked to family " + 
//						famXRef + ", but such a family has not been found. Family skipped.");
				System.err.println("Individual " + child.getId() + " is linked to family " + 
						famXRef + ", but such a family has not been found. Family skipped.");
				continue;
			}
			
			
			GedcomFamily family = families.get(famXRef);
						
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
			if (!individuals.containsKey(husbXRef)) {
				System.out.println("Can not create family with husband " + husbXRef + 
						", wife " + wifeXRef + " and children " + childXRefs + 
						". ID of husband not found.");
				return;
			}

			husband = individuals.get(husbXRef);
		}
		
		if (wifeXRef != null) {
			if (!individuals.containsKey(wifeXRef)) {
				System.out.println("Can not create family with husband " + husbXRef + 
						", wife " + wifeXRef + " and children " + childXRefs + 
						". ID of wife not found.");
				return;
			}

			wife = individuals.get(wifeXRef);
		}
		
		FamilyTreeNode newNode = null;
		
		//
		if (childOfParentsId.equals(husbXRef)) {
			newNode = new FamilyTreeNode(husbXRef + "-" + wifeXRef, husband, wife);
		} else {
			newNode = new FamilyTreeNode(husbXRef + "-" + wifeXRef, wife, husband);
		}
		
		treeNode.addChildNode(newNode);
				
		if (childXRefs != null && childXRefs.size() > 0) {
			//Add all the children of this family
			for (String childXRef : childXRefs) {
				if (individuals.containsKey(childXRef)) {
					addChild(newNode, individuals.get(childXRef));
				}
			}	
		}
		
	}
	

}
