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
package ch.thn.gedcom.familytree.test;

import java.util.Date;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.creator.GedcomCreatorFamily;
import ch.thn.gedcom.creator.GedcomCreatorIndividual;
import ch.thn.gedcom.creator.GedcomCreatorEnums.NameType;
import ch.thn.gedcom.creator.GedcomCreatorEnums.Sex;
import ch.thn.gedcom.familytree.GedcomToFamilytree;
import ch.thn.gedcom.printer.GedcomStructureTreePrinter;
import ch.thn.gedcom.store.GedcomParseException;
import ch.thn.gedcom.store.GedcomStore;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomToFamilytreeTest {
	
	
	public static void main(String[] args) {
		
		GedcomStore store = new GedcomStore();
		
		store.showParsingOutput(false);
		
		try {
			store.parse("/home/thomas/Projects/java/GedcomStore/gedcomobjects_5.5.1_test.gedg");
		} catch (GedcomParseException e) {
			e.printStackTrace();
		}
		
		GedcomStructureTreePrinter treePrinter = new GedcomStructureTreePrinter(true);
		
		GedcomCreatorIndividual indi1 = new GedcomCreatorIndividual(store, "I1");
		indi1.setSex(Sex.MALE);
		indi1.addName("HusbandName1", new String[] {"Husband"});
		indi1.addName("MarriedName1", NameType.MARRIED, new String[] {"Husband"});
		indi1.setBirth(true, GedcomFormatter.getGedcomDate(new Date(), true, true));
		indi1.setDeath(true, GedcomFormatter.getGedcomDate(new Date(), true, true));
		indi1.addAddress("Strasse", null, null, null, null, null, new String[] {"something@gmail.com"}, null, null);
		indi1.addSpouseLink("F1");
		indi1.addSpouseLink("F3");
		System.out.println(treePrinter.print(indi1.getTree()));
		
		GedcomCreatorIndividual indi2 = new GedcomCreatorIndividual(store, "I2");
		indi2.setSex(Sex.FEMALE);
		indi2.addName("WifeName1", new String[] {"Wife"});
		indi2.addAddress("Strasse", null, null, null, null, null, null, null, null);
		indi2.addSpouseLink("F1");
//		System.out.println(indi2.getHeadNode().print(new GedcomStructureTreePrinter(true)));
				
		GedcomCreatorIndividual indi3 = new GedcomCreatorIndividual(store, "I3");
		indi3.setSex(Sex.MALE);
		indi3.addName("Name1", new String[] {"Child 1"});
		indi3.addChildLink("F1");
//		System.out.println(indi3.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		GedcomCreatorIndividual indi4 = new GedcomCreatorIndividual(store, "I4");
		indi4.setSex(Sex.FEMALE);
		indi4.addName("Name1", new String[] {"Child 2"});
		indi4.addAddress("Strasse 4", null, null, null, null, null, null, null, null);
		indi4.addSpouseLink("F2");
		indi4.addChildLink("F1");
//		System.out.println(indi4.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		GedcomCreatorFamily fam1 = new GedcomCreatorFamily(store, "F1");
		fam1.setHusbandLink("I1");
		fam1.setWifeLink("I2");
		fam1.addChildLink("I3");
		fam1.addChildLink("I4");
		fam1.addChildLink("I8");
		fam1.setMarried(true, GedcomFormatter.getGedcomDate(new Date(), true, true));
//		System.out.println(fam1.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		
		GedcomCreatorIndividual indi5 = new GedcomCreatorIndividual(store, "I5");
		indi5.setSex(Sex.MALE);
		indi5.addName("Name2", new String[] {"Husband"});
		indi5.addSpouseLink("F2");
//		System.out.println(indi5.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		GedcomCreatorIndividual indi6 = new GedcomCreatorIndividual(store, "I6");
		indi6.setSex(Sex.MALE);
		indi6.addName("Name2", new String[] {"Child 1"});
		indi6.addAddress("Strasse 6", null, null, null, null, null, null, null, null);
		indi6.addChildLink("F2");
//		System.out.println(indi6.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		GedcomCreatorFamily fam2 = new GedcomCreatorFamily(store, "F2");
		fam2.setHusbandLink("I5");
		fam2.setWifeLink("I4");
		fam2.addChildLink("I6");
//		System.out.println(fam2.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		
		
		GedcomCreatorIndividual indi7 = new GedcomCreatorIndividual(store, "I7");
		indi7.setSex(Sex.MALE);
		indi7.addName("Name3", new String[] {"Child 3"});
		indi7.addChildLink("F3");
//		System.out.println(indi7.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		GedcomCreatorFamily fam3 = new GedcomCreatorFamily(store, "F3");
		fam3.setHusbandLink("I1");
		fam3.setWifeLink("I4");
		fam3.addChildLink("I7");
//		System.out.println(fam3.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		
		
		GedcomCreatorIndividual indi8 = new GedcomCreatorIndividual(store, "I8");
		indi8.setSex(Sex.MALE);
		indi8.addName("Name1", new String[] {"Child 3"});
		indi8.addAddress("Strasse 8", null, null, null, null, null, null, null, null);
		indi8.addChildLink("F1");
//		System.out.println(indi8.getHeadNode().print(new GedcomStructureTreePrinter(true)));
		
		
		GedcomToFamilytree toFamilyTree = new GedcomToFamilytree(store);
		
		toFamilyTree.addIndividual(indi1.getTree());
		toFamilyTree.addIndividual(indi2.getTree());
		toFamilyTree.addIndividual(indi3.getTree());
		toFamilyTree.addIndividual(indi4.getTree());
		toFamilyTree.addIndividual(indi5.getTree());
		toFamilyTree.addIndividual(indi6.getTree());
		toFamilyTree.addIndividual(indi7.getTree());
		toFamilyTree.addIndividual(indi8.getTree());
		
		toFamilyTree.addFamily(fam1.getTree());
		toFamilyTree.addFamily(fam2.getTree());
		toFamilyTree.addFamily(fam3.getTree());
		
		toFamilyTree.buildFamilyTree("I1");
//		FamilyTree familyTree = toFamilyTree.buildFamilyTree("GedcomToFamilytree Test-Tree", "I1");
		
		System.out.println(toFamilyTree.printTextFamilyTree(true, true, true, true, true, true, true, true, true, true, true, true, true));
		
//		System.out.println(toFamilyTree.printHtmlFamilyTree("Test GedcomToFamilyTree", true, true, true, true, true, true, true, true, true, true));
//		System.out.println(toFamilyTree.printHorizontalHtmlFamilyTree("Test Horizontal GedcomToFamilyTree", true, true, true, true, true, true, true, true, true, true, 200));
		
//		System.out.println(toFamilyTree.printCSVFamilyTree(true, true, true, true, true, true, true, true, true, true, true, true, true, true));

		
	}

}
