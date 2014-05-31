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


import ch.thn.gedcom.creator.structures.GedcomIndividual;
import ch.thn.gedcom.familytree.sort.FamilytreeBirthDateSorter;
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
	

	/**
	 * 
	 * 
	 * @param parent1 The parent which is the child of the parents of this family
	 * @param parent2 The partner of parent1
	 */
	public FamilyTreeNode(GedcomIndividual parent1, GedcomIndividual parent2) {
		super(new FamilytreeBirthDateSorter(), new GedcomIndividual[] {parent1, parent2});
	}
	
	/**
	 * @param value
	 */
	public FamilyTreeNode(GedcomIndividual[] value) {
		super(new FamilytreeBirthDateSorter(), value);
	}

	@Override
	public FamilyTreeNode nodeFactory(GedcomIndividual[] value) {
		return new FamilyTreeNode(value);
	}

	@Override
	public FamilyTreeNode nodeFactory(FamilyTreeNode node) {
		return new FamilyTreeNode(node.getNodeValue());
	}

	@Override
	protected FamilyTreeNode internalGetThis() {
		return this;
	}
	
	@Override
	public String toString() {
		GedcomIndividual[] individuals = getNodeValue();
		return individuals[0] + ", " + individuals[1];
	}

}
