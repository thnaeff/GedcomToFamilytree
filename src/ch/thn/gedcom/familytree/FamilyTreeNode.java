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


import ch.thn.gedcom.creator.structures.GedcomFamily;
import ch.thn.gedcom.creator.structures.GedcomIndividual;
import ch.thn.gedcom.familytree.sort.FamilytreeSorter;
import ch.thn.util.tree.onoff.core.AbstractGenericOnOffSetTreeNode;

/**
 * 
 * 
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilyTreeNode
	extends AbstractGenericOnOffSetTreeNode<GedcomIndividual[], FamilyTreeNode> {
	
	
	private static FamilytreeSorter sorter = new FamilytreeSorter();
	
	private GedcomFamily family = null;

	/**
	 * 
	 * 
	 * @param parent1 The parent which is the child of the parents of this family
	 * @param parent2 The partner of parent1
	 * @param family The family of the two parents
	 */
	public FamilyTreeNode(GedcomIndividual parent1, GedcomIndividual parent2, GedcomFamily family) {
		super(sorter, new GedcomIndividual[] {parent1, parent2});
		this.family = family;
	}
	
	/**
	 * 
	 * 
	 * @param value
	 * @param family
	 */
	private FamilyTreeNode(GedcomIndividual[] value, GedcomFamily family) {
		super(sorter, value);
		this.family = family;
	}

	@Override
	public FamilyTreeNode nodeFactory(GedcomIndividual[] value) {
		throw new UnsupportedOperationException("Node can not be created with just the values. Use the ");
	}

	@Override
	public FamilyTreeNode nodeFactory(FamilyTreeNode node) {
		return new FamilyTreeNode(node.getNodeValue(), node.getFamily());
	}

	@Override
	protected FamilyTreeNode internalGetThis() {
		return this;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomFamily getFamily() {
		return family;
	}
	
	
	
	
	@Override
	public String toString() {
		GedcomIndividual[] individuals = getNodeValue();
		return individuals[0] + ", " + individuals[1];
	}

}
