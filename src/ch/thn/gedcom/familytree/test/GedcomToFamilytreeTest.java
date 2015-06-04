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
import ch.thn.gedcom.creator.GedcomCreatorStructureStorage;
import ch.thn.gedcom.creator.GedcomEnums.NameType;
import ch.thn.gedcom.creator.GedcomEnums.Sex;
import ch.thn.gedcom.creator.structures.GedcomFamily;
import ch.thn.gedcom.creator.structures.GedcomIndividual;
import ch.thn.gedcom.familytree.FamilyTreeNode;
import ch.thn.gedcom.familytree.GedcomToFamilyTree;
import ch.thn.gedcom.familytree.printer.FamilytreeCSVPrinter;
import ch.thn.gedcom.familytree.printer.FamilytreeHTMLPrinter;
import ch.thn.gedcom.familytree.printer.FamilytreeTextPrinter;
import ch.thn.gedcom.printer.GedcomStructureTreePrinter;
import ch.thn.gedcom.store.GedcomParseException;
import ch.thn.gedcom.store.GedcomStore;
import ch.thn.util.file.FileUtil;
import ch.thn.util.tree.printer.TreeNodeDebugPrinter;

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
			store.parse(store.getClass().getResource("/gedcomobjects_5.5.1.gedg").getPath());
		} catch (GedcomParseException e) {
			e.printStackTrace();
		}

		GedcomStructureTreePrinter treePrinter = new GedcomStructureTreePrinter();

		GedcomIndividual indi1 = new GedcomIndividual(store, "I1");
		indi1.setSex(Sex.MALE);
		indi1.addName("HusbandName1", new String[] {"Husband"});
		indi1.addName("MarriedName1", NameType.MARRIED, new String[] {"HusbandFirst", "HusbandSecond"});
		indi1.setBirth(true, GedcomFormatter.getGedcomDate(new Date(), true, true));
		indi1.setDeath(true, GedcomFormatter.getGedcomDate(new Date(), true, true));
		indi1.addAddress("Strasse", null, null, null, null, null, new String[] {"something@gmail.com"}, null, null);
		indi1.addSpouseFamilyLink("F1");
		indi1.addSpouseFamilyLink("F3");
		System.out.println(treePrinter.print(indi1.getTree()));

		GedcomIndividual indi2 = new GedcomIndividual(store, "I2");
		indi2.setSex(Sex.FEMALE);
		indi2.addName("WifeName1", new String[] {"Wife"});
		indi2.addAddress("Strasse", null, null, null, null, null, null, null, null);
		indi2.addSpouseFamilyLink("F1");
		//		System.out.println(indi2.getHeadNode().print(new GedcomStructureTreePrinter(true)));

		GedcomIndividual indi3 = new GedcomIndividual(store, "I3");
		indi3.setSex(Sex.MALE);
		indi3.addName("Name1", new String[] {"Child1"});
		indi3.addChildFamilyLink("F1");
		indi3.setBirth(true, "03 JAN 2000");
		//		System.out.println(indi3.getHeadNode().print(new GedcomStructureTreePrinter(true)));

		GedcomIndividual indi4 = new GedcomIndividual(store, "I4");
		indi4.setSex(Sex.FEMALE);
		indi4.addName("Name1", new String[] {"Child2"});
		indi4.addAddress("Strasse 4", null, null, null, null, null, null, null, null);
		indi4.addSpouseFamilyLink("F2");
		indi4.addChildFamilyLink("F1");
		indi4.setBirth(true, "01 JAN 2000");
		//		System.out.println(indi4.getHeadNode().print(new GedcomStructureTreePrinter(true)));

		GedcomIndividual indi5 = new GedcomIndividual(store, "I5");
		indi5.setSex(Sex.MALE);
		indi5.addName("Name2", new String[] {"Husband"});
		indi5.addSpouseFamilyLink("F2");
		//		System.out.println(indi5.getHeadNode().print(new GedcomStructureTreePrinter(true)));

		GedcomIndividual indi6 = new GedcomIndividual(store, "I6");
		indi6.setSex(Sex.MALE);
		indi6.addName("Name2", new String[] {"Child1"});
		indi6.addAddress("Strasse 6", null, null, null, null, null, null, null, null);
		indi6.addChildFamilyLink("F2");
		//		System.out.println(indi6.getHeadNode().print(new GedcomStructureTreePrinter(true)));

		GedcomIndividual indi7 = new GedcomIndividual(store, "I7");
		indi7.setSex(Sex.MALE);
		indi7.addName("Name3", new String[] {"Child3"});
		indi7.addChildFamilyLink("F3");
		//		System.out.println(indi7.getHeadNode().print(new GedcomStructureTreePrinter(true)));

		GedcomIndividual indi8 = new GedcomIndividual(store, "I8");
		indi8.setSex(Sex.MALE);
		indi8.addName("Name1", new String[] {"Child3"});
		indi8.addAddress("Strasse 8", null, null, null, null, null, null, null, null);
		indi8.addChildFamilyLink("F1");
		indi8.setBirth(true, "02 JAN 2000");
		//		System.out.println(indi8.getHeadNode().print(new GedcomStructureTreePrinter(true)));



		GedcomFamily fam1 = new GedcomFamily(store, "F1");
		fam1.setHusbandLink("I1");
		fam1.setWifeLink("I2");
		fam1.addChildLink("I3");
		fam1.addChildLink("I4");
		fam1.addChildLink("I8");
		fam1.setMarried(true, GedcomFormatter.getGedcomDate(new Date(), true, true));
		//		System.out.println(fam1.getHeadNode().print(new GedcomStructureTreePrinter(true)));

		GedcomFamily fam2 = new GedcomFamily(store, "F2");
		fam2.setHusbandLink("I5");
		fam2.setWifeLink("I4");
		fam2.addChildLink("I6");
		//		System.out.println(fam2.getHeadNode().print(new GedcomStructureTreePrinter(true)));

		GedcomFamily fam3 = new GedcomFamily(store, "F3");
		fam3.setHusbandLink("I1");
		fam3.setWifeLink("I4");
		fam3.addChildLink("I7");
		//		System.out.println(fam3.getHeadNode().print(new GedcomStructureTreePrinter(true)));


		GedcomCreatorStructureStorage storage = new GedcomCreatorStructureStorage();

		storage.addIndividual(indi1);
		storage.addIndividual(indi2);
		storage.addIndividual(indi3);
		storage.addIndividual(indi4);
		storage.addIndividual(indi5);
		storage.addIndividual(indi6);
		storage.addIndividual(indi7);
		storage.addIndividual(indi8);

		storage.addFamily(fam1);
		storage.addFamily(fam2);
		storage.addFamily(fam3);

		GedcomToFamilyTree toFamilyTree = new GedcomToFamilyTree(storage);

		toFamilyTree.buildFamilyTree("I1");
		//		toFamilyTree.buildFamilyTree("I1", "GedcomToFamilytree Test-Tree");

		TreeNodeDebugPrinter<FamilyTreeNode> debugPrinter = new TreeNodeDebugPrinter<>();
		System.out.println(debugPrinter.print(toFamilyTree.getFamilyTree()));

		System.out.println("---------------");

		FamilytreeTextPrinter textPrinter = new FamilytreeTextPrinter(true, true, true, true, true, true, true, true, true, true, true, true, true, true);
		System.out.println(textPrinter.print(toFamilyTree));


		FamilytreeCSVPrinter csvPrinter = new FamilytreeCSVPrinter(true, true, true, true, true, true, true, true, true, true, true, true, true, true);
		FileUtil.writeStringToFile("/home/thomas/Desktop/familienfest/familytreetest.csv", csvPrinter.print(toFamilyTree));

		FamilytreeHTMLPrinter htmlPrinter = new FamilytreeHTMLPrinter(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
		StringBuilder sb = new StringBuilder();
		htmlPrinter.appendSimpleHeader(sb, "Familytree test");
		sb.append(htmlPrinter.print(toFamilyTree));
		htmlPrinter.appendSimpleFooter(sb);
		FileUtil.writeStringToFile("/home/thomas/Desktop/familienfest/familytreetest.html", sb);



	}



}
