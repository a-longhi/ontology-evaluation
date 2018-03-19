/**
 * Copyright 2018 Andrej Tibaut
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.  
 */
package ontology.metrics;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DITOnto (Depth of subsumption hierarchy): Length (number of links between
 * concepts) of the longest path from Thing (owl:Thing) to a leaf concept.
 * Formula: DITOnto=Max(PathLength(CThing,LeafCi)), where PathLength is the
 * function that calculates length between the i-th leaf concept LeafCi and the
 * CThing (owl:Thing). The maximum path length (Max) is then selected as result.
 * Inspired by:
 * https://stackoverflow.com/questions/40626898/getting-all-possible-paths-in-a-tree-structure
 * 
 * 
 * @author Andrej Tibaut
 */
public class WMCOnto2 {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public WMCOnto2(OntModel ontologyModel) {
		logger.info("*********************************************");

		logger.info("WMCOnto2 - Weigth method per class");

		int allPathsLength = 0;
		List<List<OntClass>> allPathsThing2Leaf = getAllPathsFromThing2Leaf(ontologyModel);
		for (List<OntClass> path : allPathsThing2Leaf) {
			allPathsLength += path.size();
		}

		// find all concepts in the ontology
		final List<OntClass> leafConcepts = findLeafConcepts(ontologyModel);

		logger.info("Number of all leafs: " + leafConcepts.size());
		logger.info("Number of all leaf paths: " + allPathsThing2Leaf.size());
		logger.info("Sum of all legths of all paths between Thing and leaf: " + allPathsLength);
		double WMCOnto2 = (double) allPathsThing2Leaf.size() / leafConcepts.size();
		logger.info("WMCOnto2 (Weigth method per class ):" + WMCOnto2);
		logger.info("*********************************************");

	}

	/**
	 * The method finds all concepts in the ontology.
	 * 
	 * @param iOntologyModel
	 * @return List of concepts in the ontology
	 * @author Andrej Tibaut
	 */
	public static List<OntClass> findLeafConcepts(final OntModel model) {
		final List<OntClass> results = new ArrayList<OntClass>();

		final ExtendedIterator<OntClass> concepts = model.listClasses();
		while (concepts.hasNext()) {
			OntClass aConcept = (OntClass) concepts.next();
			if (aConcept.listSubClasses(true).toList().size() == 0) {
				logger.debug("LEAF: " + aConcept.getLocalName());
				results.add(aConcept);
			}
		}

		return results;
	}

	/**
	 * The method finds all concepts in the ontology.
	 * 
	 * @param iOntologyModel
	 * @return List of concepts in the ontology
	 * @author Andrej Tibaut
	 */
	public static List<List<OntClass>> getAllPathsFromThing2Leaf(final OntModel iOntModel) {
		List<List<OntClass>> allPathsThing2Leaf = new ArrayList<>();

		ExtendedIterator<OntClass> rootConcepts = iOntModel.listHierarchyRootClasses();
		while (rootConcepts.hasNext()) {
			OntClass aConcept = (OntClass) rootConcepts.next();
			OntClass theThing = aConcept.getSuperClass();

			// in case the root concept doesn't have rdfs:subClassOf
			// rdf:resource="http://www.w3.org/2002/07/owl#Thing" (ontology pitfall)
			String name = ((theThing == null) || (theThing.getLocalName() == null) ? "N/A" : theThing.getLocalName());
			allPathsThing2Leaf = searchDepthFirstAllThing2LeafPaths(aConcept, name);
		}
		logger.debug("All paths Thing to leaf: " + allPathsThing2Leaf);

		return allPathsThing2Leaf;
	}

	public static List<List<OntClass>> searchDepthFirstAllThing2LeafPaths(OntClass iConcept, String s) {
		List<List<OntClass>> retLists = new ArrayList<>();

		if (iConcept.listSubClasses(true).toList().size() == 0) {
			logger.debug(s + " --> " + iConcept.getLocalName());
			List<OntClass> leafList = new LinkedList<>();
			leafList.add(iConcept);
			retLists.add(leafList);
		} else {
			ExtendedIterator<OntClass> isubConcepts = iConcept.listSubClasses(true);
			while (isubConcepts.hasNext()) {
				OntClass subConcept = isubConcepts.next();
				List<List<OntClass>> nodeLists = searchDepthFirstAllThing2LeafPaths(subConcept,
						s + " --> " + iConcept.getLocalName());
				for (List<OntClass> nodeList : nodeLists) {
					nodeList.add(0, iConcept);
					retLists.add(nodeList);
				}
			}

		}
		return retLists;
	}

}