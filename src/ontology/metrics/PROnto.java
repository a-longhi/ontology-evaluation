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
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PROnto (Properties Richness): Sum of usages of object and data properties in
 * axioms for concepts (also including owl:intersectionOf, owl:unionOf) and
 * individuals divided by the sum of all direct subconcepts (rdfs:subClassOf)
 * plus number of properties defined as owl:ObjProperty and owl:DataPropery in
 * the ontology. Formula: PROnto=(∑Ci∑ProCj + ∑Ij∑ProCk) ∕ (∑Ci∑(SubCl +
 * ∑Ci∑ProCl), where Ci is the i-th concept and ProCj is its j-th property, Ij
 * is the j-th individual and ProcCk is its k-th property, SubCl is its l-th
 * subconcept, and ProcCl is the l-th object or data property defined for the
 * concept Ci.
 * 
 * 
 * @author Andrej Tibaut
 * 
 */
public class PROnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public PROnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("PROnto - Properties Richness");

		// find all concepts in the ontology
		final List<OntClass> allConcepts = ontologyModel.listNamedClasses().toList();
		int nc = allConcepts.size();

		// get number of all subconcepts in the ontology
		int nsc = getNumberOfSubconcepts(allConcepts);

		// get number of usage of direct data properties in the ontology
		int nop = getNumberOfObjectProperties(ontologyModel);

		// get number of usage of direct data properties in the ontology
		int ndp = getNumberOfDataProperties(ontologyModel);

		List<Resource> up = getNumberOfUsagesOfPropertiesInConcepts(ontologyModel);
		List<Resource> ui = getNumberOfUsagesOfPropertiesInIndividuals(ontologyModel);

		double pronto = (double) (up.size() + ui.size()) / (nsc + nop + ndp);

		logger.info("Number of all concepts: " + nc);
		logger.info("Number of all subconcepts: " + nsc);
		logger.info("Number of object properties: " + nop);
		logger.info("Number of data properties: " + ndp);

		logger.info("Number of all usages object and data properties in concepts: " + up.size());
		logger.info("Number of all usages of object and data properties in individuals: " + ui.size());

		logger.info("PROnto: " + pronto);
		logger.info("*********************************************");
	}

	/**
	 * The method searches direct subconcepts for each concept
	 * 
	 * @param iOntologyModel
	 * @return Number of direct subconcepts in the ontology
	 * @author Andrej Tibaut
	 */
	public int getNumberOfSubconcepts(final List<OntClass> iConcepts) {
		int nsc = 0; // number of subconcepts

		for (OntClass aConcept : iConcepts) {
			ExtendedIterator<OntClass> iter = aConcept.listSubClasses(true);
			if (iter.hasNext())
				nsc += iter.toList().size();
		}

		return nsc;
	}

	/**
	 * The method searches all uses of object and data properties in individuals
	 * 
	 * @param iOntologyModel
	 * @return list of resources
	 * @author Andrej Tibaut
	 */
	public static List<Resource> getNumberOfUsagesOfPropertiesInIndividuals(final OntModel iOntologyModel) {
		List<Resource> retList = new ArrayList<>();

		List<Individual> individuals = iOntologyModel.listIndividuals().toList();
		String ns = iOntologyModel.getNsPrefixURI("");

		for (Individual aIndividual : individuals) {
			logger.debug("Individual: " + aIndividual.getURI());
			StmtIterator iter = iOntologyModel.listStatements(aIndividual, (Property) null, (RDFNode) null);
			while (iter.hasNext()) {
				Statement s = iter.nextStatement();
				logger.debug("Ind.Statement: " + s);

				if (s.getPredicate() instanceof Property) {
					Property r = s.getPredicate();
					if (r.isURIResource() && r.getNameSpace().startsWith(ns)) {
						logger.debug(">Ind.Property: " + r.toString());
						retList.add(r);
					}
				}

			}
		}

		return retList;

	}

	/**
	 * The method searches all uses of object and data properties in concept (class)
	 * axioms
	 * 
	 * @param iOntologyModel
	 * @return list of resources
	 * @author Andrej Tibaut
	 */
	public static List<Resource> getNumberOfUsagesOfPropertiesInConcepts(final OntModel iOntologyModel) {
		List<Resource> retList = new ArrayList<>();

		List<OntClass> concepts = iOntologyModel.listNamedClasses().toList();

		for (OntClass aConcept : concepts) {
			logger.debug("Class: " + aConcept.getURI());
			StmtIterator iter = iOntologyModel.listStatements(aConcept, (Property) null, (RDFNode) null);
			while (iter.hasNext()) {
				Statement s = iter.nextStatement();
				logger.debug(">Class.Statement: " + s);

				if (s.getObject() instanceof Resource) {
					Resource r = (Resource) s.getObject();
					if (!r.isURIResource()) {
						List<Resource> nodeList = getUsedPropertyResources(iOntologyModel, r);
						retList.addAll(nodeList);
					}
				} else { // is a literal
					// Axion, for example
					// [http://www.co-ode.org/ontologies/pizza/pizza.owl#Mushroom,
					// http://www.w3.org/2004/02/skos/core#prefLabel, "Mushroom"@en] where literal
					// is "Mushroom@en"
					logger.debug(">Class.Literal: " + s.getObject());
				}

			}
		}
		logger.debug("All used properties: " + retList);

		return retList;

	}

	/**
	 * The method searches direct subconcepts for each concept
	 * 
	 * @param iOntologyModel
	 * @return Number of direct subconcepts in the ontology
	 * @author Andrej Tibaut
	 */
	public static List<Resource> getUsedPropertyResources(final OntModel iOntologyModel, Resource iResource) {
		List<Resource> retList = new ArrayList<>();

		if (iResource.isURIResource()) {
			if (iResource.canAs(OntProperty.class)) {
				OntProperty op = iResource.as(OntProperty.class);
				logger.debug(">>Property: " + op.toString());
				retList.add(op);
			}
		} else {
			StmtIterator it = iOntologyModel.listStatements(iResource, (Property) null, (RDFNode) null);
			while (it.hasNext()) {
				Statement st = it.nextStatement();
				if (st.getObject() instanceof Resource) {
					Resource r = (Resource) st.getObject();
					List<Resource> nodeList = getUsedPropertyResources(iOntologyModel, r);
					retList.addAll(nodeList);
				}

			}
		}

		return retList;

	}

	/**
	 * The method finds all object properties
	 * 
	 * @param iOntologyModel
	 *            ontology model
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfObjectProperties(final OntModel iOntologyModel) {

		return iOntologyModel.listObjectProperties().toList().size();
	}

	/**
	 * The method finds all data properties
	 * 
	 * @param iOntologyModel
	 *            ontology model
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfDataProperties(final OntModel iOntologyModel) {

		return iOntologyModel.listDatatypeProperties().toList().size();
	}

	@Deprecated
	public static int getNumSubclasses(final Resource klass) {
		return klass.getModel().listSubjectsWithProperty(RDFS.subClassOf, klass).toList().size();
	}

}