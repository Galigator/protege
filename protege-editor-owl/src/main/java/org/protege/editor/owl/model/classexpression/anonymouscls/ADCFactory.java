package org.protege.editor.owl.model.classexpression.anonymouscls;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.parser.OWLParseException;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jan 8, 2009<br><br>
 */
public class ADCFactory implements OWLObjectVisitor {

    private final Logger logger = LoggerFactory.getLogger(ADCFactory.class);

    private AnonymousDefinedClassManager adcManager;

    private Set<OWLClassExpression> descrs = new HashSet<>();


    public ADCFactory(AnonymousDefinedClassManager adcManager) {
        this.adcManager = adcManager;
    }


    public List<OWLOntologyChange> getADCsForOntology(OWLOntology ont){
        List<OWLOntologyChange> changes = new ArrayList<>();
        descrs.clear();
        for (OWLClassAxiom ax : ont.getGeneralClassAxioms()){
            ax.accept(this);
        }

        for (OWLAnnotation annotation : ont.getAnnotations()){ // get annotations on ontology
            annotation.accept(this);
        }

        for (OWLClassExpression descr : descrs){
            OWLEntityCreationSet<OWLClass> chSet = adcManager.createAnonymousClass(ont, descr);
            changes.addAll(chSet.getOntologyChanges());
        }

        return changes;
    }


    public void visit(OWLSubClassOfAxiom ax) {
        if (ax.getSubClass().isAnonymous()){
            descrs.add(ax.getSubClass());
        }
    }


    public void visit(OWLEquivalentClassesAxiom ax) {
        for (OWLClassExpression descr : ax.getClassExpressions()){
            if (descr.isAnonymous()){
                descrs.add(descr);
            }
        }
    }


    public void visit(OWLAnnotation annotation) {
        if (annotation.getProperty().getIRI().equals(adcManager.getURI())){
            annotation.getValue().accept(this);
        }
    }

    public void visit(OWLLiteral node) {
        try{
            OWLClassExpression descr = parseOWLClassExpression(node.getLiteral());
            descrs.add(descr);
        }
        catch(OWLParseException e){
            logger.error("An error occurred whilst parsing a literal in the ADCFactory", e);
        }     
    }


    private OWLClassExpression parseOWLClassExpression(String s) throws OWLParseException {
        throw new OWLParseException("Retrieving ADCs from annotations not currently implemented");
    }
}
