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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import ch.thn.gedcom.data.GedcomNode;
import ch.thn.gedcom.familytree.printer.FamilytreeCSVPrinter;
import ch.thn.gedcom.familytree.printer.FamilytreeHTMLPrinter;
import ch.thn.gedcom.familytree.printer.FamilytreeTextPrinter;
import ch.thn.gedcom.store.GedcomStore;
import ch.thn.util.tree.TreeNodeException;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomToFamilytree {
	
	public static final String indiXRefPrefix = "I";
	public static final String famXRefPrefix = "F";
	
	
	/** All the families with their ID */
	private HashMap<String, GedcomNode> individuals = null;
	/** All the individuals with their ID */
	private HashMap<String, GedcomNode> families = null;
	/** All individual IDs and their families of which they are a parent of */
	private HashMap<String, HashMap<String, GedcomNode>> familiesAsParent = null;
	/** All individual IDs and their families of which they are a child of */
	private HashMap<String, HashMap<String, GedcomNode>> familiesAsChild = null;
	
	private GedcomStore store = null;
	
	private FamilyTree familyTree = null;
	
	private FamilytreeCSVPrinter csvPrinter = null;
	private FamilytreeTextPrinter textPrinter = null;
	private FamilytreeHTMLPrinter htmlPrinter = null;
	
	
	/**
	 * 
	 * 
	 * @param store
	 */
	public GedcomToFamilytree(GedcomStore store) {
		this.store = store;
		
		individuals = new HashMap<>();
		families = new HashMap<>();
		familiesAsParent = new HashMap<String, HashMap<String,GedcomNode>>();
		familiesAsChild = new HashMap<String, HashMap<String,GedcomNode>>();
		
	}
	
	/**
	 * Build the family tree using the added individuals and families. The starting 
	 * individual of the tree will be the one which has the given individual ID
	 * 
	 * @param individualId
	 * @return
	 * @throws GedcomToFamilytreeError If there is no individual with the given ID
	 */
	public FamilyTree buildFamilyTree(String individualId) {
		return buildFamilyTree(null, individualId);
	}
	
	/**
	 * Build the family tree using the added individuals and families. The starting 
	 * individual of the tree will be the one which has the given individual ID
	 * 
	 * @param treeTitle
	 * @param individualId
	 * @return
	 * @throws GedcomToFamilytreeError If there is no individual with the given ID
	 */
	public FamilyTree buildFamilyTree(String treeTitle, String individualId) {
		
		if (!individuals.containsKey(individualId)) {
			throw new GedcomToFamilytreeError("Failed to build family tree. Individual with ID " + 
					individualId + " not found.");
		}
		
		FamilyTree familyTree = new FamilyTree(treeTitle);
		
		//Start building the tree by adding the first child
		addChild(familyTree, individualId);
		
		this.familyTree = familyTree;
		
		return familyTree;
	}
	
	/**
	 * Sort the whole tree
	 * 
	 * @param comparator
	 */
	public void sortTree(Comparator<FamilyTreeNode> comparator) {
		sortChildren(familyTree.getChildNodes(), comparator);
	}
	
	/**
	 * Sorts the children and all their children.
	 * 
	 * @param children
	 * @param childValueSorter
	 */
	private void sortChildren(LinkedList<FamilyTreeNode> children, 
			Comparator<FamilyTreeNode> childValueSorter) {
		for (FamilyTreeNode child : children) {
			//Sort the child
			child.sortChildNodesByValue(childValueSorter);
			//And all its children
			sortChildren(child.getChildNodes(), childValueSorter);
		}
	}
	
	/**
	 * Add a child for building the family tree
	 * 
	 * @param treeNode
	 * @param childId
	 */
	private void addChild(FamilyTreeNode treeNode, String childId) {
		GedcomNode startNode = individuals.get(childId);
		
		if (startNode == null) {
			throw new GedcomToFamilytreeError("Failed to add child " + childId + ". " + 
					"No such individual found.");
		}
		
		GedcomNode indi = searchForNode(startNode, "INDI");
		
		//Get all the links to this individuals families
		LinkedList<GedcomNode> famsLinks = indi.getChildLines("SPOUSE_TO_FAMILY_LINK");
		
		//No family for this individual
		if (famsLinks == null || famsLinks.size() == 0) {
			addOneIndividualFamily(treeNode, childId, indi);
			return;
		}
		
		
		boolean familyAdded = false;
		
		//Process the family (or families) for this individual
		for (GedcomNode famsLink : famsLinks) {
			String famXRef = famsLink.getChildLine("FAMS", 0).getTagLineXRef();
			
			if (famXRef == null || famXRef.length() == 0) {
				//An empty FAMS link
				continue;
			}
			
			if (!families.containsKey(famXRef)) {
//				throw new GedcomToFamilytreeError("Individual " + childId + " is linked to family " + 
//						famXRef + ", but such a family has not been found. Family skipped.");
				System.err.println("Individual " + childId + " is linked to family " + 
						famXRef + ", but such a family has not been found. Family skipped.");
				continue;
			}
			
			
			GedcomNode family = searchForNode(families.get(famXRef), "FAM");
						
			String husbXRef = null;
			String wifeXRef = null;
			
			if (family.hasChildLine("HUSB")) {
				husbXRef = family.getChildLine("HUSB", 0).getTagLineXRef();
			}
			
			if (family.hasChildLine("WIFE")) {
				wifeXRef = family.getChildLine("WIFE", 0).getTagLineXRef();
			}
						
			LinkedList<GedcomNode> childrenLinks = family.getChildLines("CHIL");
			
			LinkedList<String> childXRefs = new LinkedList<String>();
			
			if (childrenLinks != null) {
				for (GedcomNode childLink : childrenLinks) {
					childXRefs.add(childLink.getTagLineXRef());
				}
			}
						
			addFamily(treeNode, husbXRef, wifeXRef, childId, childXRefs);
			
			familyAdded = true;
		}
		
		if (!familyAdded) {
			addOneIndividualFamily(treeNode, childId, indi);
		}
	}
	
	/**
	 * 
	 * 
	 * @param treeNode
	 * @param childId
	 * @param indi
	 */
	private void addOneIndividualFamily(FamilyTreeNode treeNode, String childId, GedcomNode indi) {
		String husbXRef = null;
		String wifeXRef = null;
		
		GedcomNode sex = indi.getChildLine("SEX", 0);
		
		if (sex != null && sex.getTagLineValue().equals("F")) {
			wifeXRef = indi.getTagLineXRef();
		} else {
			//Assume a male individual if set is not set and it is not a female
			husbXRef = indi.getTagLineXRef();
		}
		
		addFamily(treeNode, husbXRef, wifeXRef, childId, null);
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
			String childOfParentsId, LinkedList<String> childXRefs) {
		
		GedcomNode husband = null;
		GedcomNode wife = null;
		
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
		
		GedcomToFamilytreeIndividual husbIndi = null;
		GedcomToFamilytreeIndividual wifeIndi = null;
		
		if (husband != null) {
			husbIndi = new GedcomToFamilytreeIndividual(store, husband);
		}
		
		if (wife != null) {
			wifeIndi = new GedcomToFamilytreeIndividual(store, wife);
		}
		
		
		if (childOfParentsId.equals(husbXRef)) {
			newNode = new FamilyTreeNode(husbXRef + "-" + wifeXRef, husbIndi, wifeIndi);
		} else {
			newNode = new FamilyTreeNode(husbXRef + "-" + wifeXRef, wifeIndi, husbIndi);
		}
		
		try {
			treeNode.addChildNode(newNode);
		} catch (TreeNodeException e) {
			e.printStackTrace();
			//Ignore exceptions since a new node is created there should never be 
			//this exception
		}
				
		if (childXRefs != null && childXRefs.size() > 0) {
			//Add all the children of this family
			for (String childXRef : childXRefs) {
				addChild(newNode, childXRef);
			}	
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param individual
	 * @return
	 */
	public boolean addIndividual(GedcomNode individual) {
		GedcomNode indi = searchForNode(individual, "INDI");
		
		if (indi == null) {
			return false;
		}
		
		String xref = indi.getTagLineXRef();
		
		individuals.put(xref, individual);
		
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param individuals
	 */
	public void addIndividuals(Collection<GedcomNode> individuals) {
		for (GedcomNode node : individuals) {
			addIndividual(node);
		}
	}
	
	/**
	 * 
	 * 
	 * @param family
	 * @return
	 */
	public boolean addFamily(GedcomNode family) {
		GedcomNode fam = searchForNode(family, "FAM");
		
		if (fam == null) {
			return false;
		}
		
		String famXRef = fam.getTagLineXRef();
				
		families.put(famXRef, family);
		
		
		if (family.getNumberOfChildLines("HUSB") > 0) {
			//There can only be one HUSB line
			String husbXRef = family.getChildLine("HUSB", 0).getTagLineXRef();
			
			if (!familiesAsParent.containsKey(husbXRef)) {
				familiesAsParent.put(husbXRef, new HashMap<String, GedcomNode>());
			}
			
			familiesAsParent.get(husbXRef).put(famXRef, family);
		}
		
		if (family.getNumberOfChildLines("WIFE") > 0) {
			//There can only be one WIVE line
			String wiveXRef = family.getChildLine("WIFE", 0).getTagLineXRef();
			
			if (!familiesAsParent.containsKey(wiveXRef)) {
				familiesAsParent.put(wiveXRef, new HashMap<String, GedcomNode>());
			}
			
			familiesAsParent.get(wiveXRef).put(famXRef, family);
		}
		
		//Children
		for (int i = 0; i < family.getNumberOfChildLines("CHIL"); i++) {
			String chilXRef = family.getChildLine("CHIL", i).getTagLineXRef();
			
			if (!familiesAsChild.containsKey(chilXRef)) {
				familiesAsChild.put(chilXRef, new HashMap<String, GedcomNode>());
			}
			
			familiesAsChild.get(chilXRef).put(famXRef, family);
		}
		
		
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param families
	 */
	public void addFamilies(Collection<GedcomNode> families) {
		for (GedcomNode node : families) {
			addFamily(node);
		}
	}
	
	/**
	 * 
	 * 
	 * @param individual
	 * @return
	 */
	public boolean hasIndividual(GedcomNode individual) {
		GedcomNode indi = searchForNode(individual, "INDI");
		
		if (indi == null) {
			return false;
		}
		
		String xref = indi.getTagLineXRef();
		
		return individuals.containsKey(xref);
	}
	
	/**
	 * 
	 * 
	 * @param family
	 * @return
	 */
	public boolean hasFamily(GedcomNode family) {
		GedcomNode fam = searchForNode(family, "FAM");
		
		if (fam == null) {
			return false;
		}
		
		String xref = fam.getTagLineXRef();
		
		return families.containsKey(xref);
	}
	
	/**
	 * 
	 * 
	 * @param individualXRef
	 * @return
	 */
	public GedcomNode getIndividual(String individualXRef) {
		return individuals.get(individualXRef);
	}
	
	/**
	 * 
	 * 
	 * @param familyXRef
	 * @return
	 */
	public GedcomNode getFamily(String familyXRef) {
		return families.get(familyXRef);
	}
	
	/**
	 * Returns the families of which the given individual is a parent of
	 * 
	 * @param individualXRef
	 * @return
	 */
	public HashMap<String, GedcomNode> getFamiliesOfParent(String individualXRef) {
		return familiesAsParent.get(individualXRef);
	}
	
	/**
	 * Returns the families of which the given individual is a child of
	 * 
	 * @param individualXRef
	 * @return
	 */
	public HashMap<String, GedcomNode> getFamiliesOfChild(String individualXRef) {
		return familiesAsChild.get(individualXRef);
	}
	
	/**
	 * Searches for the family of which both given individuals are a parent of
	 * 
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public String getFamilyOfParents(String parent1, String parent2) {
		HashMap<String, GedcomNode> families1 = familiesAsParent.get(parent1);
		HashMap<String, GedcomNode> families2 = familiesAsParent.get(parent2);

		for (String fam1XRef : families1.keySet()) {
			if (families2.containsKey(fam1XRef)) {
				return fam1XRef;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomStore getStore() {
		return store;
	}

	/**
	 * Searches for the first occurrence of the line with the given tag or structure name
	 * 
	 * @param startNode
	 * @param tagOrStructureName
	 * @return
	 */
	private GedcomNode searchForNode(GedcomNode startNode, String tagOrStructureName) {
		
		if (startNode.getNodeLine().isStructureLine()) {
			if (startNode.getNodeLine().getAsStructureLine().getStructureName().equals(tagOrStructureName)) {
				return startNode;
			}
		} else {
			if (startNode.getNodeLine().getAsTagLine().getTag().equals(tagOrStructureName)) {
				return startNode;
			}
		}
		
		if (!startNode.hasChildLine(tagOrStructureName)) {
			
			LinkedList<GedcomNode> children = startNode.getChildLines();
			for (GedcomNode node : children) {
				GedcomNode ret = searchForNode(node, tagOrStructureName);
				if (ret != null) {
					return ret;
				}
			}
			
			//Nothing found
			return null;
			
		} else {
			return startNode.getChildLine(tagOrStructureName, 0);
		}
		
	}
	
	
	
	/**
	 * 
	 * 
	 * @param showGender
	 * @param showRelationship
	 * @param showEmail
	 * @param showAddress
	 * @param showAge
	 * @param showBirthDate
	 * @param showDeathDate
	 * @param showMarriedName
	 * @param printDivorced
	 * @return
	 */
	public StringBuilder printTextFamilyTree(boolean showGender, boolean showRelationship, 
			boolean showEmail, boolean showAddress, boolean showAge, boolean showBirthDate, 
			boolean showDeathDate, boolean showMarriedName, boolean printDivorced) {
		
		if (textPrinter == null) {
			textPrinter = new FamilytreeTextPrinter(this, showGender, 
					showRelationship, showEmail, showAddress, showAge, showBirthDate, 
					showDeathDate, showMarriedName, printDivorced);
		}
		
		return textPrinter.print(familyTree);
	}
	
	/**
	 * 
	 * 
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
	 * @return
	 */
	public StringBuilder printCSVFamilyTree(boolean alignValuesRight, boolean showGender, 
			boolean showRelationship, 
			boolean showEmail, boolean showAddress, boolean showAge, boolean showBirthDate, 
			boolean showDeathDate, boolean showMarriedName, boolean printDivorced) {
		
		if (csvPrinter == null) {
			csvPrinter = new FamilytreeCSVPrinter(this, alignValuesRight, showGender, 
					showRelationship, showEmail, showAddress, showAge, showBirthDate, 
					showDeathDate, showMarriedName, printDivorced);
		}
		
		return csvPrinter.print(familyTree);
	}
	
	/**
	 * 
	 * 
	 * @param treeTitle
	 * @param useColors
	 * @param showGender
	 * @param showRelationship
	 * @param showEmail
	 * @param showAddress
	 * @param showAge
	 * @param showBirthDate
	 * @param showDeathDate
	 * @param showMarriedName
	 * @param printDivorced
	 * @return
	 */
	public StringBuilder printHtmlFamilyTree(String treeTitle, 
			boolean useColors, boolean showGender, boolean showRelationship, 
			boolean showEmail, boolean showAddress, boolean showAge, 
			boolean showBirthDate, boolean showDeathDate, boolean showMarriedName, 
			boolean printDivorced) {
		
		StringBuilder sb = new StringBuilder();
		
		if (htmlPrinter == null) {
			htmlPrinter = new FamilytreeHTMLPrinter(this, 
					useColors, showGender, showRelationship, showEmail, showAddress, 
					showAge, showBirthDate, showDeathDate, showMarriedName, printDivorced);
		}
		
		htmlPrinter.appendSimpleHeader(sb, treeTitle);
		
		sb.append(htmlPrinter.print(familyTree));
		
		htmlPrinter.appendSimpleFooter(sb);
		
		return sb;
	}
	
	
	/**
	 * 
	 * 
	 * @param targetFile
	 * @param showGender
	 * @param showRelationship
	 * @param showEmail
	 * @param showAddress
	 * @param showAge
	 * @param showBirthDate
	 * @param showDeathDate
	 * @param showMarriedName
	 * @param printDivorced
	 * @throws IOException
	 */
	public void writeTextFamilyTree(String targetFile, boolean showGender, boolean showRelationship, 
			boolean showEmail, boolean showAddress, boolean showAge, boolean showBirthDate, 
			boolean showDeathDate, boolean showMarriedName, boolean printDivorced) throws IOException {
		System.out.println("Writing family tree (TXT) to " + targetFile);
		
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8"));
		
		//Writes all at once as string
		output.write(printTextFamilyTree(showGender, showRelationship, showEmail, 
				showAddress, showAge, showBirthDate, showDeathDate, showMarriedName, 
				printDivorced).toString());
		
		output.close();
		System.out.println("Family tree (TXT) written to " + targetFile);
	}
	
	/**
	 * 
	 * 
	 * @param targetFile
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
	 * @throws IOException
	 */
	public void writeCSVFamilyTree(String targetFile, boolean alignValuesRight, 
			boolean showGender, boolean showRelationship, 
			boolean showEmail, boolean showAddress, boolean showAge, boolean showBirthDate, 
			boolean showDeathDate, boolean showMarriedName, boolean printDivorced) throws IOException {
		System.out.println("Writing family tree (CSV) to " + targetFile);
		
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8"));
		
		//Writes all at once as string
		output.write(printCSVFamilyTree(alignValuesRight, showGender, showRelationship, showEmail, 
				showAddress, showAge, showBirthDate, showDeathDate, showMarriedName, 
				printDivorced).toString());
		
		output.close();
		System.out.println("Family tree (CSV) written to " + targetFile);
	}
	
	/**
	 * 
	 * 
	 * @param targetFile
	 * @param treeTitle
	 * @param useColors
	 * @param showGender
	 * @param showRelationship
	 * @param showEmail
	 * @param showAddress
	 * @param showAge
	 * @param showBirthDate
	 * @param showDeathDate
	 * @param showMarriedName
	 * @param printDivorced
	 * @throws IOException
	 */
	public void writeHTMLFamilyTree(String targetFile, String treeTitle, 
			boolean useColors, boolean showGender, boolean showRelationship, 
			boolean showEmail, boolean showAddress, boolean showAge, 
			boolean showBirthDate, boolean showDeathDate, boolean showMarriedName, 
			boolean printDivorced) throws IOException {
		System.out.println("Writing family tree (HTML) to " + targetFile);
		
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8"));
		
		//Writes all at once as string
		output.write(printHtmlFamilyTree(treeTitle, useColors, showGender, showRelationship, 
				showEmail, showAddress, showAge, showBirthDate, showDeathDate, showMarriedName, 
				printDivorced).toString());
		
		output.close();
		System.out.println("Family tree (HTML) written to " + targetFile);
	}
	
	
}
