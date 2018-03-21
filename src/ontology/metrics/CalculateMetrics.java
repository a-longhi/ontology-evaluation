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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ANOnto -
 * 
 * @author Andrej Tibaut
 */
public class CalculateMetrics {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// private static String[] metrics = { "LCOMOnto", "WMCOnto2", "DITOnto",
	// "NACOnto", "NOCOnto", "CBOnto", "RFCOnto",
	// "NOMOnto", "RROnto", "PROnto", "AROnto", "INROnto", "CROnto", "ANOnto",
	// "TMOnto2" };

	private static String[] metrics = { "PROnto" };

	public CalculateMetrics() {

	}

	public static void main(String[] args) throws IOException {
		logger.info("STARTED...");
		if (args.length > 0) {
			String ontFile = args[0];
			OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
			ontologyModel.read(ontFile, "RDF/XML-ABBREV");
			String nameSpace = ontologyModel.getNsPrefixURI("");
			logger.info(nameSpace);
			ExtendedIterator<Ontology> iterator = ontologyModel.listOntologies();
			// in case we want to access ontology metadata (imports etc.)
			Ontology ontology = iterator.next();
			logger.info(ontology.getURI());

			for (String metric : metrics) {
				Class<?> klas = null;
				try {
					klas = Class.forName("ontology.metrics." + metric);
					Constructor<?> constructor = null;
					constructor = klas.getConstructor(OntModel.class);
					Object instance = constructor.newInstance(ontologyModel);
				} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("...FINISHED");

	}

	private static Model makeSampleModel() {
		String BASE = "http://example/";

		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("", BASE);
		Resource r1 = model.createResource(BASE + "r1");
		Resource r2 = model.createResource(BASE + "r2");
		Property p1 = model.createProperty(BASE + "p");
		Property p2 = model.createProperty(BASE + "p2");
		RDFNode v1 = model.createTypedLiteral("1", XSDDatatype.XSDinteger);
		RDFNode v2 = model.createTypedLiteral("2", XSDDatatype.XSDinteger);

		r1.addProperty(p1, v1).addProperty(p1, v2);
		r1.addProperty(p2, v1).addProperty(p2, v2);
		r2.addProperty(p1, v1).addProperty(p1, v2);

		return model;
	}

	// if (domainNode.asResource().getNameSpace().equals(NS_OWL) ||
	// domainNode.asResource().getNameSpace().equals(NS_XSD) ||
	// domainNode.asResource().getNameSpace().equals(NS_RDF)||
	// domainNode.asResource().getNameSpace().equals(NS_RDFS)) continue
	// Ontology o =
	// model.createOntology("http://github.com/quadrama/metadata/ontology.owl");
	// o.setRDFType(OWL2.Ontology);
	// model.setNsPrefixes(PrefixMapping.Standard);
	// model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
	// model.setNsPrefix("skos", SKOS.getURI());
	// DCTerms.date;

}