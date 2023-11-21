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

import fr.gouv.esante.pml.smt.utils.ADMSVocabulary;
import fr.gouv.esante.pml.smt.utils.ChargerMapping;
import fr.gouv.esante.pml.smt.utils.ChargerMapping2;
import fr.gouv.esante.pml.smt.utils.DCTVocabulary;
import fr.gouv.esante.pml.smt.utils.DublinCoreVocabulary;
import fr.gouv.esante.pml.smt.utils.PropertiesUtil;
import fr.gouv.esante.pml.smt.utils.SKOSVocabulary;

import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;



public class XLSXOWLTransformer2 {
	
	
	private static String xlsxCladimedFileName = PropertiesUtil.getProperties("xlsxCladimedFileName");
	  private static String owlCladimedFileName = PropertiesUtil.getProperties("owlCladimedFileName");

	  private static OWLOntologyManager man = null;
	  private static OWLOntology onto = null;
	  private static OWLDataFactory fact = null;
	  
	  private static OWLAnnotationProperty skosNotation  = null;
	  private static OWLAnnotationProperty rdfsLabel  = null;
	  private static OWLAnnotationProperty rdfsComment  = null;
	  private static OWLAnnotationProperty dcType  = null;
	  private static OWLAnnotationProperty dctCreated  = null;
	  private static OWLAnnotationProperty dctModified  = null;
	  private static OWLAnnotationProperty admsStatus  = null;
	  private static OWLAnnotationProperty owlDeprecated  = null;
	  private static OWLAnnotationProperty dctReplacedBy  = null;
	  private static OWLAnnotationProperty skosAltLabel  = null;

	
	public static void main(String[] args) throws Exception {
		
		ChargerMapping2.chargeExcelConceptToList(xlsxCladimedFileName);
		  
		final OutputStream fileoutputstream = new FileOutputStream(owlCladimedFileName);
		 man = OWLManager.createOWLOntologyManager();
		 onto = man.createOntology(IRI.create(PropertiesUtil.getProperties("terminologie_IRI")));
		 fact = onto.getOWLOntologyManager().getOWLDataFactory();
		
		 skosNotation =  fact.getOWLAnnotationProperty(SKOSVocabulary.NOTATION.getIRI());
		 rdfsLabel =  fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_LABEL.getIRI());
		 rdfsComment =  fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		 dcType =  fact.getOWLAnnotationProperty(DublinCoreVocabulary.TYPE.getIRI());   
		 
		 dctCreated = fact.getOWLAnnotationProperty(DCTVocabulary.created.getIRI());
		 dctModified = fact.getOWLAnnotationProperty(DCTVocabulary.modified.getIRI());
		 admsStatus = fact.getOWLAnnotationProperty(ADMSVocabulary.status.getIRI());
		 owlDeprecated =  fact.getOWLAnnotationProperty(fact.getOWLDeprecated());
		 dctReplacedBy =  fact.getOWLAnnotationProperty(DCTVocabulary.isReplacedBy.getIRI());
		 skosAltLabel =  fact.getOWLAnnotationProperty(SKOSVocabulary.ALTLABEL.getIRI());

		 
		    OWLClass owlClass = null;
		    
		    
		    createPrincipalNoeud();
		    createConceptRetiresNoeud();
		    createConceptRetires2021Noeud();
		    

		    
		    for(String id: ChargerMapping2.listConcepts.keySet()) {
		    	final String about = PropertiesUtil.getProperties("terminologie_URI") + id;
		        owlClass = fact.getOWLClass(IRI.create(about));
		        OWLAxiom declare = fact.getOWLDeclarationAxiom(owlClass);
		        man.applyChange(new AddAxiom(onto, declare));
		        
		        
		        
		        //balise subClass
		        if("active".equals(ChargerMapping2.listConcepts.get(id).get(5))) {
		        
		                 String aboutSubClass = null;
		        
		                 if("parent".equals(ChargerMapping2.listConcepts.get(id).get(0)))
		                 { 
		                     aboutSubClass = PropertiesUtil.getProperties("URI_parent") ;
		                 }else {
		        	
		        	          aboutSubClass = 
		        	        		  PropertiesUtil.getProperties("terminologie_URI") + 
		        	        		  ChargerMapping2.listConcepts.get(id).get(0);
		                 }
		       
		       
		               OWLClass subClass = fact.getOWLClass(IRI.create(aboutSubClass));
		               OWLAxiom axiom = fact.getOWLSubClassOfAxiom(owlClass, subClass);
		               man.applyChange(new AddAxiom(onto, axiom));
		        
		        }
		        
                else {
		        	
		        	
		        	String aboutSubClass = null;
			        aboutSubClass = "https://data.esante.gouv.fr/assoc_cladimed/cladimed/Concept_retirés_2021"  ;
			        OWLClass subClass = fact.getOWLClass(IRI.create(aboutSubClass));
			        
			        OWLAxiom axiom = fact.getOWLSubClassOfAxiom(owlClass, subClass);
			        man.applyChange(new AddAxiom(onto, axiom));
	
		        }
		        
		        
		        addLateralAxioms(skosNotation, id, owlClass);
		        //addLateralAxioms(fact.getRDFSLabel(), ChargerMappingExcel.listConcepts.get(id).get(1), owlClass, "fr");
		        addLateralAxioms(rdfsLabel, ChargerMapping2.listConcepts.get(id).get(1), owlClass, "fr");
		       
		        
		       if (  !ChargerMapping2.listConcepts.get(id).get(2).isEmpty()) 
		         addLateralAxioms(rdfsComment, ChargerMapping2.listConcepts.get(id).get(2), owlClass, "fr");
		       
		       addLateralAxioms(dcType, ChargerMapping2.listConcepts.get(id).get(4), owlClass);
		       
		       
		       addLateralAxioms(admsStatus, ChargerMapping2.listConcepts.get(id).get(5), owlClass);
		       
		       if("inactive".equals(ChargerMapping2.listConcepts.get(id).get(5))) {
		    	   
		    	   addBooleanAxioms(owlDeprecated, "true", owlClass);
		    	   
		    	   if(!"".equals(ChargerMapping2.listConcepts.get(id).get(7))) {
		    	       
		    		   
		    		   String  listeCodes =ChargerMapping2.listConcepts.get(id).get(7).replace("OU", ",");
		    		   
		    		   String[] codes = listeCodes.split(",");
		    		   
		    		   for(int j=0; j<codes.length; j++)
		    		   {
		    			   addURIAxioms(dctReplacedBy, codes[j].replace(" ", ""), owlClass);
		    			   
		    		   }
		    		   
		    		addLateralAxioms(rdfsComment, "CLADIMED 2021 : code supprimé et remplacé par "+ChargerMapping2.listConcepts.get(id).get(7), owlClass, "fr");

		    			   
		    		     
		    	     
		    	   }else {
		    		   
			    		addLateralAxioms(rdfsComment, "CLADIMED 2021 : code supprimé", owlClass, "fr");

		    	   }
		    	   
		       }
		    	   
		    	   

		       addDatelAxioms(dctCreated, ChargerMapping2.listConcepts.get(id).get(3), owlClass);
		       
		       if(ChargerMapping2.listConcepts.get(id).get(6)!= "")
		         addDatelAxioms(dctModified, ChargerMapping2.listConcepts.get(id).get(6), owlClass);
		       
		       if (  !ChargerMapping2.listConcepts.get(id).get(8).isEmpty()) { 
			         addLateralAxioms(skosAltLabel, ChargerMapping2.listConcepts.get(id).get(8), owlClass, "fr");
		             addLateralAxioms(rdfsComment, "CLADIMED 2021 : lablel modifé", owlClass, "fr");
		             
		       }
		       
		       
		        
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
  
  public static void addDatelAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	  
	   // final OWLAnnotation annotation =
	    //		fact.getOWLAnnotation(prop, fact.getOWLLiteral(val, OWL2Datatype.XSD_DATE_TIME));

	   final OWLAnnotation annotation =
	    		fact.getOWLAnnotation(prop, fact.getOWLLiteral(val, OWL2Datatype.XSD_DATE_TIME));

	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addBooleanAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val,OWL2Datatype.XSD_BOOLEAN));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addURIAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {

	    IRI iri_creator = IRI.create(PropertiesUtil.getProperties("terminologie_URI")+val);
		   
	    OWLAnnotationProperty prop_creator =fact.getOWLAnnotationProperty(prop.getIRI());
	    
	    OWLAnnotation annotation = fact.getOWLAnnotation(prop_creator, iri_creator);
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
  
  
  public static void createConceptRetires2021Noeud() {
	  
	  
	    
	   final String classRacine = "https://data.esante.gouv.fr/assoc_cladimed/cladimed/Concept_retirés_2021" ;
	   OWLClass noeudRacine = fact.getOWLClass(IRI.create(classRacine));
	   
	   String aboutSubClass = null;
      aboutSubClass = "https://data.esante.gouv.fr/assoc_cladimed/cladimed/Concept_retirés"  ;
      OWLClass subClass = fact.getOWLClass(IRI.create(aboutSubClass));
      
      OWLAxiom axiom = fact.getOWLSubClassOfAxiom(noeudRacine, subClass);
      man.applyChange(new AddAxiom(onto, axiom));
    
	  addLateralAxioms(skosNotation, "Concept retirés 2021", noeudRacine);
     
	  
}
 
 
  public static void createConceptRetiresNoeud() {
	  
	  
	    
	   final String classRacine = "https://data.esante.gouv.fr/assoc_cladimed/cladimed/Concept_retirés" ;
	   OWLClass noeudRacine = fact.getOWLClass(IRI.create(classRacine));
	    addLateralAxioms(skosNotation, "Concept retirés", noeudRacine);
     
	  
}

}
