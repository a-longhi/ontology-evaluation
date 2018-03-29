Ontology evaluation project
===========================
The project implements metrics for evaluation of ontologies. 

# 1. OQuaRE metrics
The metrics support the OQuaRE [1] (Wiki [2]) ontology evaluation framework. Original definitions and formulas are improved and some of them corrected. This is the only known open source implementation of the evaluation framework.   


## 1.1. LCOMOnto (Lack of Cohesion in Methods)
Semantic and conceptual relatedness of classes. It can be used to measure the separation of responsibilities and independence of components of ontologies.
Formula: 

```
LCOMOnto=∑PathLength(CThing,LeafCi) / ∑PathLeafCj
```


where PathLength is the function that calculates length between the i-th leaf concept LeafCi and the CThing (owl:Thing) and PathLeafCi is the j-th path between CThing and a leaf.


## 1.2. WMCOnto2 (Weigth method per class) 
Arithmetic mean number of path length (number of links between concepts) from Thing (owl:Thing) to a leaf class.
Formula: 

```
WMCOnto2=∑PathLength(CThing,LeafCi) / ∑LeafCi
```

where PathLength is the function that calculates length between the i-th leaf concept LeafCi and the CThing (owl:Thing).

## 1.3. DITOnto (Depth of subsumption hierarchy)
Length (number of links between concepts) of the longest path from Thing (owl:Thing) to a leaf concept.
Formula: 

```
DITOnto=Max(PathLength(CThing,LeafCi))
```

where PathLength is the function that calculates length between the i-th leaf concept LeafCi and the CThing (owl:Thing). The maximum path length (Max) is then selected as result.



## 1.4. NACOnto (Number of Ancestor Concepts) 
Arithmetic mean number of direct ancestor concepts per leaf concept.
Formula: 

```
NACOnto=∑AncLeafCi / ∑LeafCj 
```

where AncLeafCi is the i-th direct ancestor of a leaf and LeafCj is j-th leaf concept.



## 1.5. NOCOnto (Number of Children Concepts)
Number of the direct subconcepts divided by the number of concepts minus the number of leaf concepts.
Formula: 

```
NOCOnto=∑Ci∑SubCj / (∑Ci - ∑LeafCk)
```

where Ci is the i-th concept and SubCj is its j-th direct subclass and LeafCk is k-th leaf concept.



## 1.6. CBOnto (Coupling between Objects)
Number of direct ancestors of all concepts divided by the number of concepts not counting concepts with owl:Thing as direct ancestor.
Formula: 

```
CBOnto=∑Ci∑AncCj / (∑Ci - ∑CTk)
```

where Ci is the i-th concept and AncCj is its j-th direct ancestor and CTk is  k-th concept with owl:Thing as direct parent.


## 1.7. RFCOnto (Response for a concept)
Number of direct properties (owl:ObjectProperty, owl:DatatypeProperty) and direct ancestor concepts that can be directly accessed from a concept divided by the number of all concepts. 
Formula: 

```
RFCOnto=(∑Ci∑ProCj + ∑Ci∑AncCk) / ∑Ci 
```

where Ci is the i-th concept and ProCj is its j-th property and AncCk is its k-th ancestor.



## 1.7. NOMOnto (Number of properties) 
Arithmetic mean number of properties (owl:ObjectProperty, owl:DatatypeProperty) per concept. 
Formula: 

```
NOMOnto=∑Ci∑ProCj  ∕ ∑Ci 
```

where Ci is the i-th concept and ProCj is its j-th property.

## 1.8. RROnto (Relationship Richness)
Number of subconcepts (rdfs:subClassOf) divided by the sum of subconcepts (rdfs:subClassOf) plus object and data properties (owl:ObjectProperty, owl:DatatypeProperty) of the concepts. 
Formula: 

```
RROnto=∑Ci∑SubCj  ∕ (∑Ci∑SubCj + ∑Ci∑ProCk) 
```

where Ci is the i-th concept and SubCj is its j-th subconcept and ProCk is its k-th property.



## 1.9. PROnto (Properties Richness) 
Sum of direct concept properties (owl:ObjectProperty, owl:DatatypeProperty)  divided by the sum of direct subconcepts (rdfs:subClassOf) plus number of direct concept properties. 
Formula: 

```
RROnto=∑Ci∑ProCj ∕ (∑Ci∑(SubCk + ∑Ci∑ProCj)
```

where Ci is the i-th concept and ProCj is its j-th property and SubCk is its k-th subconcept.


## 1.10. AROnto (Attribute Richness) 
Number of property restrictions (owl:Restrictions (owl:someValuesFrom, owl:allValuesFrom, owl:hasValue, owl:minCardinality, owl:maxCardinality)) nested inside of rdfs:subClassOf per concept in the ontology.
Formula: 

```
AROnto=∑Ci∑RestCj / ∑Ci
```

where Ci is the i-th concept and RestCj is its j-th restriction.
  
## 1.11. INROnto (Relationships per concept)
Arithmetic mean number of subconcepts (rdfs:subClassOf) per concept. 
Formula: 

```
INROnto=∑Ci∑SubCj  / ∑Ci
```

where Ci is the i-th concept and SubCj is its j-th subconcept.
Remark: if the value is greater than 1 multiple inheritance is contained in the ontology, which means that the ontology in place is not normalized.


## 1.12. CROnto (Concept Richness)
Arithmetic mean number of direct individuals per concept (excluding individuals of its subconcepts) 
Formula: 

```
CROnto=∑Ci∑IndCj / ∑Cj
```

where Ci is the i-th concept and IndCi is its j-th individual.


## 1.13. ANOnto (Annotation Richness)
Arithmetic mean number of annotation properties (existing in OWL: owl:versionInfo, rdfs:comment, rdfs:label, rdfs:seeAlso, rdfs:isDefinedBy) per concept (owl:Class). 
Formula: 

```
ANOnto=∑Ci∑ApCj / ∑Ci
```

where Ci is the i-th concept and ApCj is its j-th annotation property.

## 1.14. TMOnto2 (Tangledness)
Mean number of direct ancestors (super-classes) of concepts with more than 1 direct ancestor (multiple ancestorage). 
Formula: 

```
TMOnto2=∑Ci∑AncCj / ∑Ci
```

where Ci is the i-th concept with more than one direct ancestor and AncCj is its j-th direct ancestor.


## 1.14. TMOnto2 (Tangledness) 
Mean number of direct ancestors (super-classes) of concepts with more than 1 direct ancestor (multiple ancestorage). 
Formula: 

```
TMOnto2=∑Ci∑AncCj / ∑Ci
```

where Ci is the i-th concept with more than one direct ancestor and AncCj is its j-th direct ancestor.

## 1.15. EXOnto (Externes) [NEW]
The metric measures the number of external namespaces that the maintained ontology consumes. The namespace is considered external if any of its resources is used at least one time in the maintained ontology. Standard namespaces (XSD, RDF, RDFS, OWL) are not counted.
 
Formula: 

```
CPOnto=∑ENSi
```

where ENSi is the i-th external namespace (ontology).

## 1.16. CPOnto (Composability) [NEW]
The metric describes the composure of the ontology on the scale from a monolithic self-sufficient ontology to a highly composed and interconnected ontology. It is calculated as the usage of resources in axioms from external namespaces divided by the usage of all resources (base namespace and external namespaces). 
 
Formula: 

```
CPOnto=∑ResEi ∕ (∑ResEi + ∑ResBj)
```

where ResEi is the i-th external resource and ResBj is the j-th resource from the base namespace.

## 1.17. AGOnto (Aggregability) [NEW]
The metric relates to the aggregation size of the sets of the external ontologies. It calculates the percentage of the used resources for the maximal and minimal set according to the total size of all external sets in the maintained ontology. The maximal percentage is then divided by the size of the minimal percentage to get the multiplier.

 
Formula: 

```
AGOnto = Max(ENSi/NE, …, ENSn/NE) ∕ Min(ENSi/NE, …, ENSn/NE)
```

where ENSi is the size of the i-th external namespace (i between 1 and number of external namespaces), NE is total sum of used external resources in the maintained ontology.


# 2. Usage


The project contains Java code (see the folder `src/ontology/metrics`) and initial ontology data (see the ontology `cidoc_crm_v6.2-draft-2015August.rdf` in the folder `resources`) to start with.
The project was created with the `Eclipse` and therefore can be downloaded (or pulled) and imported into Eclipse. The best way is to import it as a `Maven` project (the `pom.xml` is in the root of the project).  The class with the `Main` method is the `ontology.metrics.CalculateMetrics` to which the ontology `resources/cidoc_crm_v6.2-draft-2015August.rdf` [3] can be passed as a command line argument (args).
To run the project with the `Maven` use the following commands:
* clean (to delete `target` folder etc.) and compile

```
mvn clean compile    
```
* run it

```
mvn exec:java -Dexec.mainClass=ontology.metrics.CalculateMetrics -Dexec.args="resources/cidoc_crm_v6.2-draft-2015August.rdf"
```

By default all metrics are calculated. If you want to calculate individual metrics (e.g. CPOnto - Composability), modify the code accordingly: 

```
private static String[] metrics = { "CPOnto" }; 
```

# 3. Test ontologies

There are some test ontologies in the /resource folder.


References
----------

[1] A. Duque-Ramos, J.T. Fernández-Breis, R. Stevens, N. Aussenac-Gilles, OQuaRE: A square-based approach for evaluating the quality of ontologies, J. Res. Pract. Inf. Technol. 43 (2011) 159–176.

[2] OQUARE Wiki. http://miuras.inf.um.es/oquarewiki/. Accessed 15 Dec 2017

[3] CIDOC CRM, reference ontology for the interchange of cultural heritage information (ISO 21127:2014), http://www.cidoc-crm.org/. Accessed 15 Dec 2017
