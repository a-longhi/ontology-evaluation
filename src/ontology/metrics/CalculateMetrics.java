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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ANOnto -
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class CalculateMetrics {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	private static String[] metrics = { "LCOMOnto", "WMCOnto2", "DITOnto", "NACOnto", "NOCOnto", "CBOnto", "RFCOnto",
			"NOMOnto", "RROnto", "PROnto", "AROnto", "INROnto", "CROnto", "ANOnto", "TMOnto2" };

	public CalculateMetrics() {

	}

	public static void main(String[] args) throws IOException {
		System.out.println("STARTED...");
		if (args.length > 0) {
			String ontFile = args[0];
			OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
			ontologyModel.read(ontFile, "RDF/XML-ABBREV");
			String nameSpace = ontologyModel.getNsPrefixURI("");
			ExtendedIterator<Ontology> iterator = ontologyModel.listOntologies();
			// in case we want to access ontology metadata (imports etc.)
			Ontology ontology = iterator.next();
			// System.out.println(ontology.getURI());

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
		System.out.println("...FINISHED");

	}
}