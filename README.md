# GedcomToFamilytree

**Builds a family tree from GEDCOM structures which have been created as GedcomNodes. Individuals and familiy structures can be added to the tree builder which are then used to build the family tree, starting from any given individual.**

The basic steps to create a family tree are the following:
1. Create your individuals using the [GedcomStore](https://github.com/thnaeff/GedcomStore), the [GedcomCreator](https://github.com/thnaeff/GedcomCreator) or load your individuals from a CSV file with  [FamilyChartToGedcom](https://github.com/thnaeff/FamilyChartToGedcom)
2. Define the families (with the same tools listed above)
3. Add the individuals and the families to GedcomToFamilyTree (with `addIndividual` and `addFamily`)
4. Let GedcomToFamilyTree build the family tree, starting from the individual you want

See [the test code](https://github.com/thnaeff/GedcomToFamilytree/tree/master/src/ch/thn/gedcom/familytree/test) for examples.


# Dependencies
* [GedcomCreator](http://github.com/thnaeff/GedcomCreator)
* [GedcomStore](http://github.com/thnaeff/GedcomStore)
* My own utility library: [Util](http://github.com/thnaeff/Util)


