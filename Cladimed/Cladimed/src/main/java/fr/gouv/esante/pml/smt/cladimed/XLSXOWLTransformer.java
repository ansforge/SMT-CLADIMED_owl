package fr.gouv.esante.pml.smt.cladimed;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import fr.gouv.esante.pml.smt.utils.ChargerMapping;
import fr.gouv.esante.pml.smt.utils.DCTVocabulary;
import fr.gouv.esante.pml.smt.utils.DublinCoreVocabulary;
import fr.gouv.esante.pml.smt.utils.PropertiesUtil;
import fr.gouv.esante.pml.smt.utils.SKOSVocabulary;

import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;



public class XLSXOWLTransformer {
	
	
	private static String xlsxCladimedFileName = PropertiesUtil.getProperties("xlsxCladimedFileName");
	  private static String owlCladimedFileName = PropertiesUtil.getProperties("owlCladimedFileName");

	  private static OWLOntologyManager man = null;
	  private static OWLOntology onto = null;
	  private static OWLDataFactory fact = null;
	  
	  private static OWLAnnotationProperty skosNotation  = null;
	  private static OWLAnnotationProperty rdfsLabel  = null;
	  private static OWLAnnotationProperty rdfsComment  = null;
	  private static OWLAnnotationProperty dcType  = null;
	
	public static void main(String[] args) throws Exception {
		
		ChargerMapping.chargeExcelConceptToList(xlsxCladimedFileName);
		  
		final OutputStream fileoutputstream = new FileOutputStream(owlCladimedFileName);
		 man = OWLManager.createOWLOntologyManager();
		 onto = man.createOntology(IRI.create(PropertiesUtil.getProperties("terminologie_IRI")));
		 fact = onto.getOWLOntologyManager().getOWLDataFactory();
		
		 skosNotation =  fact.getOWLAnnotationProperty(SKOSVocabulary.NOTATION.getIRI());
		 rdfsLabel =  fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_LABEL.getIRI());
		 rdfsComment =  fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		 dcType =  fact.getOWLAnnotationProperty(DublinCoreVocabulary.TYPE.getIRI());   
		 
		    OWLClass owlClass = null;
		    
		    
		    createPrincipalNoeud();
		    

		    
		    for(String id: ChargerMapping.listConcepts.keySet()) {
		    	final String about = PropertiesUtil.getProperties("terminologie_URI") + id;
		        owlClass = fact.getOWLClass(IRI.create(about));
		        OWLAxiom declare = fact.getOWLDeclarationAxiom(owlClass);
		        man.applyChange(new AddAxiom(onto, declare));
		        
		        
		        
		        //balise subClass
		        String aboutSubClass = null;
		        
		        if("parent".equals(ChargerMapping.listConcepts.get(id).get(0)))
		        { 
		        aboutSubClass = PropertiesUtil.getProperties("URI_parent") ;
		        }else {
		        	
		        	aboutSubClass = PropertiesUtil.getProperties("terminologie_URI") + ChargerMapping.listConcepts.get(id).get(0);
		        }
		       
		       
		        OWLClass subClass = fact.getOWLClass(IRI.create(aboutSubClass));
		        OWLAxiom axiom = fact.getOWLSubClassOfAxiom(owlClass, subClass);
		        man.applyChange(new AddAxiom(onto, axiom));
		        
		        
		        addLateralAxioms(skosNotation, id, owlClass);
		        //addLateralAxioms(fact.getRDFSLabel(), ChargerMappingExcel.listConcepts.get(id).get(1), owlClass, "fr");
		        addLateralAxioms(rdfsLabel, ChargerMapping.listConcepts.get(id).get(1), owlClass, "fr");
		       if (!ChargerMapping.listConcepts.get(id).get(2).isEmpty()) 
		         addLateralAxioms(rdfsComment, ChargerMapping.listConcepts.get(id).get(2), owlClass, "fr");
		       
		       addLateralAxioms(dcType, ChargerMapping.listConcepts.get(id).get(3), owlClass);
		       
		       
		        
		    }
		    
		    
		    final RDFXMLDocumentFormat ontologyFormat = new RDFXMLDocumentFormat();
		    ontologyFormat.setPrefix("dct", "http://purl.org/dc/terms/");
		    
		    
		    IRI iri = IRI.create(PropertiesUtil.getProperties("terminologie_IRI"));
		    man.applyChange(new SetOntologyID(onto,  new OWLOntologyID(iri)));
		   
		    addPropertiesOntology();
		    
		    man.saveOntology(onto, ontologyFormat, fileoutputstream);
		    fileoutputstream.close();
		    System.out.println("Done.");
		
		

	}
	
	public static void addLateralAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addLateralAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass, String lang) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val, lang));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  
  
  public static void createPrincipalNoeud() {
	  
	   String noeud_parent = PropertiesUtil.getProperties("noeud_parent");
	   String noeud_parent_label=PropertiesUtil.getProperties("label_noeud_parent");
	   String noeud_parent_notation=PropertiesUtil.getProperties("notation_noeud_parent");
	    
	   final String aboutSubClass1 = PropertiesUtil.getProperties("URI_parent") ;
	   OWLClass subClass1 = fact.getOWLClass(IRI.create(aboutSubClass1));
       addLateralAxioms(skosNotation, noeud_parent_notation, subClass1);
       addLateralAxioms(rdfsLabel, noeud_parent_label, subClass1, "fr");
	  
  }
  
  
  private static void addPropertiesOntology() {
	  
	  
	  OWLAnnotation anno_version = fact.getOWLAnnotation(fact.getOWLVersionInfo(), 
	    		fact.getOWLLiteral(PropertiesUtil.getProperties("ontology_version")));
	    
	    
	    OWLAnnotation anno_label = fact.getOWLAnnotation(fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_LABEL.getIRI()), 
	    		fact.getOWLLiteral((PropertiesUtil.getProperties("ontology_label")), "fr"));
	    
	    IRI iri_creator = IRI.create(PropertiesUtil.getProperties("ontology_creator"));
	    OWLAnnotationProperty prop_creator =fact.getOWLAnnotationProperty(DCTVocabulary.creator.getIRI());
	    OWLAnnotation anno_creator = fact.getOWLAnnotation(prop_creator, iri_creator);
	    
	    
	    IRI iri_pub = IRI.create(PropertiesUtil.getProperties("ontology_publisher"));
	    OWLAnnotationProperty prop_pub =fact.getOWLAnnotationProperty(DCTVocabulary.publisher.getIRI());
	    OWLAnnotation anno_pub = fact.getOWLAnnotation(prop_pub, iri_pub );
	    
	    
	    OWLAnnotationProperty prop_label =fact.getOWLAnnotationProperty(DCTVocabulary.issued.getIRI());
	    OWLAnnotation anno_issued = fact.getOWLAnnotation(prop_label, fact.getOWLLiteral(PropertiesUtil.getProperties("ontology_issued"),
	    		OWL2Datatype.XSD_DATE_TIME));
	    
	    
	    man.applyChange(new AddOntologyAnnotation(onto, anno_version));
	    man.applyChange(new AddOntologyAnnotation(onto, anno_label));
	    man.applyChange(new AddOntologyAnnotation(onto, anno_creator));
	    man.applyChange(new AddOntologyAnnotation(onto, anno_pub));
	    man.applyChange(new AddOntologyAnnotation(onto, anno_issued));
  }
  

}
