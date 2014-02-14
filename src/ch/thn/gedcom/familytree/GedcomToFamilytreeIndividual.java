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

import ch.thn.gedcom.creator.GedcomCreatorIndividual;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.gedcom.store.GedcomStore;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomToFamilytreeIndividual extends GedcomCreatorIndividual {

	/**
	 * 
	 * 
	 * @param store
	 * @param node
	 */
	public GedcomToFamilytreeIndividual(GedcomStore store, GedcomNode node) {
		super(store, node);
	}
	
	
	@Override
	public String toString() {
		return "ID: " + super.getId();
	}

}
