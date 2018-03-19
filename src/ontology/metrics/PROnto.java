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

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PROnto (Properties Richness): Number of usage of direct object and data
 * properties (owl:ObjectProperty, owl:DatatypeProperty) divided by the sum of
 * subconcepts (rdfs:subClassOf) plus object and data properties
 * (owl:ObjectProperty, owl:DatatypeProperty) of the concepts. Formula:
 * RROnto=∑|ProCij| ∕ ∑(|SubCk| + ∑|ProCij|); where ProCij is the i-th property
 * of the j-th concept and SubCk is the k-th subconcept in the ontology.
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
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

		// get number of usage of direct object properties in the ontology
		int nuop = 0;
		// int nuop = getNumberOfUsageOfDirectObjectProperties(ontologyModel);

		// get number of usage of direct object properties in the ontology
		int nudp = 0;
		// int nudp = getNumberOfUsageOfDirectDataProperties(ontologyModel);

		// get number of usage of direct data properties in the ontology
		int nop = getNumberOfObjectProperties(ontologyModel);

		// get number of usage of direct data properties in the ontology
		int ndp = getNumberOfDataProperties(ontologyModel);

		List<Resource> up = getNumberOfUsagesOfPropertiesInConcepts(ontologyModel);

		double pronto = (double) (nuop + nudp) / (nop + nop + ndp);

		logger.info("Number of all concepts: " + nc);
		logger.info("Number of all subconcepts: " + nsc);
		logger.info("Number of object properties: " + nop);
		logger.info("Number of data properties: " + ndp);

		logger.info("Number of all usages of object and data properties: " + up.size());
		// logger.info("Number of all usages of data properties: " + nudp);

		logger.info("PROnto: " + pronto);
		logger.info("*********************************************");
		// getStatements(ontologyModel);
		// getUndefinedProperties(ontologyModel);
	}

	/**
	 * The method finds all concepts in the ontology.
	 * 
	 * @param iOntologyModel
	 * @return List of concepts in the ontology
	 * @author Andrej Tibaut
	 */
	public static List<Individual> findAllIndividuals(final OntModel iOntologyModel) {
		return iOntologyModel.listIndividuals().toList();
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

	public void getProperties(final OntModel iOntologyModel) {

		ExtendedIterator<ObjectProperty> op = iOntologyModel.listObjectProperties();

		while (op.hasNext()) {
			ObjectProperty p = op.next();
			System.out.println("OP: " + p);
			ResIterator iter = iOntologyModel.listResourcesWithProperty(p);
			if (iter.hasNext()) {
				System.out.println("The database contains vcards for:");
				while (iter.hasNext()) {
					System.out.println("  " + iter.nextResource().getRequiredProperty(p).getString());
				}
			} else {
				System.out.println("No vcards were found in the database");
			}
		}

	}

	public void getStatements(final OntModel iOntologyModel) {

		StmtIterator iter = iOntologyModel.listStatements((Resource) null, (Property) null, (RDFNode) null);

		while (iter.hasNext()) {
			Statement p = iter.next();
			System.out.println("STAT: " + p);
		}

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

		for (Individual aIndividual : individuals) {
			System.out.println("IND: " + aIndividual.getURI());
			StmtIterator iter = iOntologyModel.listStatements(aIndividual, (Property) null, (RDFNode) null);
			// StmtIterator iter = iOntologyModel.listStatements((Resource) null, (Property)
			// null, (RDFNode) null);
			while (iter.hasNext()) {
				Statement s = iter.nextStatement();
				System.out.println(" -->STMT: " + s);

				if (s.getPredicate() instanceof Property) {
					Property r = s.getPredicate();
					if (r.isURIResource()) {
						System.out.println("  -->PROP: " + r.toString());
						// retList.addAll(nodeList);
					}
				}

			}
		}
		// logger.debug("All used properties: " + retList);

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
			System.out.println("CLAZ: " + aConcept.getURI());
			StmtIterator iter = iOntologyModel.listStatements(aConcept, (Property) null, (RDFNode) null);
			// StmtIterator iter = iOntologyModel.listStatements((Resource) null, (Property)
			// null, (RDFNode) null);
			while (iter.hasNext()) {
				Statement s = iter.nextStatement();
				System.out.println(" -->STMT: " + s);

				if (s.getObject() instanceof Resource) {
					Resource r = (Resource) s.getObject();
					if (!r.isURIResource()) {
						// System.out.println(" -->ID: " + r.toString());
						List<Resource> nodeList = getUsedPropertyResources(iOntologyModel, r);
						retList.addAll(nodeList);
					}
				} else { // is a literal
					// Axion, for example
					// [http://www.co-ode.org/ontologies/pizza/pizza.owl#Mushroom,
					// http://www.w3.org/2004/02/skos/core#prefLabel, "Mushroom"@en] where literal
					// is "Mushroom@en"
					System.out.println(" -->LITERAL: " + s.getObject());
					// System.out.println(" -->LITERAL: " + s.getLiteral().getLexicalForm());
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
			// System.out.println(" -->LITERAL: " + iResource.getURI());
			if (iResource.canAs(OntProperty.class)) {
				OntProperty op = iResource.as(OntProperty.class);
				System.out.println("       -->PROP: " + op.toString());
				retList.add(op);
			}
		} else {
			// System.out.println(" -->RDFNODE: " + iResource);
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
		// logger.debug("All used properties in concepts: " + retList);

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

	/**
	 * The method finds all direct properties for the given list of concepts
	 * 
	 * @param iConcepts
	 *            list of all ontology concepts
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfUsageOfDirectObjectProperties(final OntModel iOntologyModel) {
		int np = 0; // number of properties

		ExtendedIterator<ObjectProperty> s = iOntologyModel.listObjectProperties();
		while (s.hasNext()) {
			ObjectProperty op = s.next();
			ExtendedIterator<? extends OntClass> lc = op.listDeclaringClasses();
			if (lc.hasNext()) {
				List<? extends OntClass> lp = lc.toList();

				np += lp.size();
				logger.debug(" Object property " + op.getLocalName() + " is used in " + lp);
				// for (OntClass oc = lp.hasNext()) {
				// }

			}
			lc.close();
		}

		return np;
	}

	/**
	 * The method finds all use of direct data properties
	 * 
	 * @param iConcepts
	 *            list of all ontology concepts
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfUsageOfDirectDataProperties(final OntModel iOntologyModel) {
		int np = 0; // number of properties

		// ResIterator ri = iOntologyModel.listResourcesWithProperty(RDF.predicate,
		// OWL2.Axiom);

		ExtendedIterator<DatatypeProperty> s = iOntologyModel.listDatatypeProperties();
		while (s.hasNext()) {
			DatatypeProperty ldp = (DatatypeProperty) s.next();
			ExtendedIterator<? extends OntClass> lc = ldp.listDeclaringClasses(true);
			if (lc.hasNext()) {
				List<? extends OntClass> lp = lc.toList();
				np += lp.size();
				logger.debug(" Data property " + ldp.getLocalName() + " is used in " + lp.toString());
			}
			lc.close();
		}

		return np;
	}

	@Deprecated
	public static int getNumSubclasses(final Resource klass) {
		return klass.getModel().listSubjectsWithProperty(RDFS.subClassOf, klass).toList().size();
	}

}