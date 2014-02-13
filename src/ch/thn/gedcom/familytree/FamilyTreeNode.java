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


import java.util.Comparator;
import java.util.LinkedList;

import ch.thn.gedcom.creator.GedcomCreatorFamily;
import ch.thn.gedcom.familytree.printer.FamilyTreePrintBuilder;
import ch.thn.gedcom.familytree.printer.FamilytreePrinter;
import ch.thn.util.tree.TreeNodeException;
import ch.thn.util.tree.printable.GenericPrintableTreeNode;
import ch.thn.util.tree.printable.TreePrinter;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilyTreeNode 
	extends GenericPrintableTreeNode<String, GedcomToFamilytreeIndividual[], FamilyTreeNode> {
	
	/**
	 * 
	 * 
	 * @param key
	 * @param value
	 */
	protected FamilyTreeNode(String key, GedcomToFamilytreeIndividual[] value) {
		super(key, value);
	}
	
	/**
	 * 
	 * 
	 * @param parent
	 * @param key
	 * @param value
	 */
	protected FamilyTreeNode(FamilyTreeNode parent, String key, GedcomToFamilytreeIndividual[] value) {
		super(parent, key, value);
	}
	
	/**
	 * 
	 * 
	 * @param key
	 * @param parent1 The parent which is the child of the parents of this family
	 * @param parent2 The partner of parent1
	 */
	public FamilyTreeNode(String key, GedcomToFamilytreeIndividual parent1, 
			GedcomToFamilytreeIndividual parent2) {
		super(key, new GedcomToFamilytreeIndividual[] {parent1, parent2});
	}
	
	@Override
	protected FamilyTreeNode nodeFactory(String key,
			GedcomToFamilytreeIndividual[] value) {
		return new FamilyTreeNode(key, value);
	}


	@Override
	protected FamilyTreeNode nodeFactory(FamilyTreeNode parent, String key,
			GedcomToFamilytreeIndividual[] value) {
		return  new FamilyTreeNode(parent, key, value);
	}


	@Override
	protected FamilyTreeNode getThis() {
		return this;
	}
	
	@Override
	protected LinkedList<FamilyTreeNode> getChildNodes() {
		return super.getChildNodes();
	}
	
	@Override
	protected FamilyTreeNode addChildNode(FamilyTreeNode childNode)
			throws TreeNodeException {
		return super.addChildNode(childNode);
	}
	
	@Override
	protected void sortChildNodesByValue(
			Comparator<FamilyTreeNode> childValueSorter) {
		super.sortChildNodesByValue(childValueSorter);
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomToFamilytreeIndividual[] getIndividuals() {
		return super.getNodeValue();
	}
	
	@Override
	public boolean printNode(
			TreePrinter<String, GedcomToFamilytreeIndividual[], ?, ?, ?> printer) {
		FamilyTreePrintBuilder printBuilder = null;
		
		if (printer instanceof FamilytreePrinter) {
			printBuilder = ((FamilytreePrinter)printer).getPrintBuilder();
		} else {
			throw new GedcomToFamilytreeError("Can not determine if family should be " +
					"printed or not if a printer other than a " + 
					FamilytreePrinter.class.getSimpleName() + " is used.");
		}
		
		if (printBuilder.printDivorced()) {
			//Print all families
			return true;
		}
		
		GedcomToFamilytreeIndividual[] parents = getNodeValue();

		if (!printFamily(printBuilder, parents[0], parents[1])) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param printBuilder
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	private boolean printFamily(FamilyTreePrintBuilder printBuilder, 
			GedcomToFamilytreeIndividual parent1, GedcomToFamilytreeIndividual parent2) {
		
		//Print always if no family exists
		if (parent1 == null || parent2 == null) {
			return true;
		}
		
		GedcomCreatorFamily family = printBuilder.getFamily(parent1, parent2);
				
		//If divorced and there are no children...
		if (family.getDivorced() && family.getNumberOfChildren() == 0) {
			FamilyTreeNode node = (FamilyTreeNode) getParentNode().getChildNode(0);
			FamilyTreeNode lastNode = node;
			
			boolean hasPrintable = false;
			int familiesCount = 0;
			
			//Look for a family with the same parent1 which is not divorced or 
			//which is divorced but has children
			while (node != null) {
				GedcomToFamilytreeIndividual[] parents = node.getNodeValue();
				
				//Family with same parent1?
				if (parent1.getId().equals(parents[0].getId())) {
					familiesCount++;
					
					GedcomCreatorFamily nodeFamily = printBuilder.getFamily(parents[0], parents[1]);
					
					//Not divorced families and families with children will be printed anyways
					if (!nodeFamily.getDivorced() || family.getNumberOfChildren() > 0) {
						hasPrintable = true;
						break;
					}
				}
				
				lastNode = node;
				
				node = (FamilyTreeNode) node.getNextNode();
			}
			
			if (hasPrintable) {
				//There is another family with the same parent1 but they are not 
				//divorced or there are ones with children and will be printed anyways, 
				//so do not print this divorced family without children
				return false;
			} else {
				//There is no family which will be printed anyways
				
				if (familiesCount == 1) {
					//There is only one (this) family with the same parent1 -> print it
					return true;
				} else {
					//There is more than one family with the same parent1 -> only print the last one
					if (this.equals(lastNode)) {
						return true;
					} else {
						return false;
					}
				}
			}
			
		}
		
		return true;
	}
	

}
