# GedcomToFamilytree

**Builds a family tree from GEDCOM structures which have been created as GedcomNodes. Individuals and family structures can be added to the tree builder which are then used to build the family tree, starting from any given individual.**

---


[![License](http://img.shields.io/badge/License-Apache v2.0-802879.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Java Version](http://img.shields.io/badge/Java-1.6%2B-2E6CB8.svg)](https://java.com)
[![Apache Maven ready](http://img.shields.io/badge/Apache Maven ready-3.3.9%2B-FF6804.svg)](https://maven.apache.org/)


---


The basic steps to create a family tree are the following:

1. Create your individuals using the [GedcomStore](https://github.com/thnaeff/GedcomStore), the [GedcomCreator](https://github.com/thnaeff/GedcomCreator) or load your individuals from a CSV file with  [FamilyChartToGedcom](https://github.com/thnaeff/FamilyChartToGedcom)
2. Define the families (with the same tools listed above)
3. Add the individuals and the families to GedcomToFamilyTree (with `addIndividual` and `addFamily`)
4. Let GedcomToFamilyTree build the family tree, starting from the individual you want

See [the test code](https://github.com/thnaeff/GedcomToFamilytree/tree/master/src/ch/thn/gedcom/familytree/test) for examples.


---


<img src="http://maven.apache.org/images/maven-logo-black-on-white.png" alt="Built with Maven" width="150">

This project can be built with Maven

Maven command:
```
$ mvn clean install
```

pom.xml entry in your project:
```
<dependency>
	<groupId>ch.thn.gedcom</groupId>
	<artifactId>gedcomtofamilytree</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

