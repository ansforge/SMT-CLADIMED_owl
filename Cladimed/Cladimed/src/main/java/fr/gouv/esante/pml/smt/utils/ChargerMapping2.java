package fr.gouv.esante.pml.smt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ChargerMapping2 {
	
	
	
	public static HashMap<String, List<String>> listConcepts = new HashMap<String, List<String>>();
	
	
	public static  void chargeExcelConceptToList(final String xlsFile) throws Exception {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");	
		
	    List<String> listeNouveauteV15 =  getNouveauteV15(xlsFile);
	    
	    Map<String, String> listeModifV15 = getModifV15(xlsFile);
		
		FileInputStream file = new FileInputStream(new File(xlsFile));
			
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		
		XSSFSheet sheet = workbook.getSheetAt(Integer.parseInt(PropertiesUtil.getProperties("classification_active")));
		
		XSSFSheet sheetAbandon = workbook.getSheetAt(Integer.parseInt(PropertiesUtil.getProperties("Abandon_V15")));
		
		Iterator<Row> rowIterator = sheet.iterator();
		
		rowIterator.next();
		
		
        Iterator<Row> rowIteratorAbandon = sheetAbandon.iterator();
		
        rowIteratorAbandon.next();
		
		
		
		
		 while (rowIterator.hasNext()) {
	    	 
	    	 Row row = rowIterator.next();
	    	 Cell c2 = row.getCell(5); //get Ref Code
		     Cell c3 = row.getCell(6); // get Libelle
		     
		     String refCode = c2.getStringCellValue();
		     String libelle = c3.getStringCellValue();
		     
		      //Calcul Ref Parent, Type
		      
		       List<String> data = getInfos(refCode);
		    	       
				List<String> listedonnees= new ArrayList<>();
				
				listedonnees.add(0,data.get(0));// code Parent
				
				listedonnees.add(1,libelle);
				
				
				
				if (listeNouveauteV15.contains(refCode)) {
					
					 listedonnees.add(2,PropertiesUtil.getProperties("Nouveaute_V15_Comment"));
					 listedonnees.add(3, formatter.format(returnDate("01/01/2021")));
					//status : active
					//date creation 2021
					
				} else {
					 listedonnees.add(2, "");
					 listedonnees.add(3, formatter.format(returnDate("01/01/2020")));
					// status active
					//date creation 2020
					
				}
				
				listedonnees.add(4, data.get(1));// type
				
				listedonnees.add(5, "active");// status
				
				listedonnees.add(6, "");// date Modification
				
				listedonnees.add(7, "");// nouveau code replacedBy
				
				
				if(listeModifV15.containsKey(refCode)) {
					
					 listedonnees.add(6, formatter.format(returnDate("01/01/2021"))); //date Modif
					 listedonnees.add(8, listeModifV15.get(refCode));// alt Label
				}else {
					listedonnees.add(8, "");// altLAbel
				}
				
				
				listConcepts.put(refCode, listedonnees);
				
				
	   }
		 
		 
		 
	  //Abandon 
		 
     while (rowIteratorAbandon.hasNext()) {
	    	 
	    	 Row row = rowIteratorAbandon.next();
	    	 Cell c2 = row.getCell(5); //get Ref Code
		     Cell c3 = row.getCell(7); // get Libelle
		     Cell c4 = row.getCell(8); // get nouveau Code
		     
		     Cell c5 = row.getCell(6); // get nouveau Code
		     
		     String refCode = c2.getStringCellValue();
		     String libelle = c3.getStringCellValue();
		     
		     String replacedByCode = "";
		     
		     if(c4 != null && c4.getCellType() != Cell.CELL_TYPE_BLANK)
		       replacedByCode = c4.getStringCellValue();
		     else
		     {
		    	 // Traitment
		     }
		     
		      //Calcul Ref Parent, Type
		      
		       List<String> data = getInfos(refCode);
		    	       
				List<String> listedonnees= new ArrayList<>();
				
				listedonnees.add(0,data.get(0));// code Parent
				
				listedonnees.add(1,libelle);

				listedonnees.add(2, "");
				
				listedonnees.add(3, formatter.format(returnDate("01/01/2020"))); // date Creation

				listedonnees.add(4, data.get(1));// type
				
				listedonnees.add(5, "inactive");// status
				
				listedonnees.add(6, formatter.format(returnDate("01/01/2021")));// date Modification
				
				listedonnees.add(7, replacedByCode);// nouveau Code
				
				listedonnees.add(8, "");// altLAbel
				
				
				listConcepts.put(refCode, listedonnees);
				
				
	   }
		 
		 

		 file.close();
		
	}
	
	
	private static List<String> getNouveauteV15(final String xlsFile) throws IOException {
		
        FileInputStream file = new FileInputStream(new File(xlsFile));
		
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		
		XSSFSheet sheet = workbook.getSheetAt(Integer.parseInt(PropertiesUtil.getProperties("Nouveaute_V15")));
		
		Iterator<Row> rowIterator = sheet.iterator();
		
		rowIterator.next();
		
		 List<String> listeNouveauteV15 = new ArrayList<>();
		
		 while (rowIterator.hasNext()) {
	    	 
	    	 Row row = rowIterator.next();
	    	 Cell c2 = row.getCell(5); //get Ref Code
	    	 listeNouveauteV15 .add(c2.getStringCellValue());
				
	   }
		 
		
		 file.close();

		
		
		
		return listeNouveauteV15;
	}
	
	
	
private static  Map<String, String> getModifV15(final String xlsFile) throws IOException {
		
        FileInputStream file = new FileInputStream(new File(xlsFile));
		
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		
		XSSFSheet sheet = workbook.getSheetAt(Integer.parseInt(PropertiesUtil.getProperties("Modification_V15")));
		
		Iterator<Row> rowIterator = sheet.iterator();
		
		rowIterator.next();
		
		 Map<String, String> listeModifV15 = new HashMap<String, String>();
		
		 while (rowIterator.hasNext()) {
	    	 
	    	 Row row = rowIterator.next();
	    	 Cell c1 = row.getCell(5); //get Ref Code
	    	 Cell c2 = row.getCell(7); //get Alt Label
	    	 
	    	 listeModifV15.put(c1.getStringCellValue(), c2.getStringCellValue());
	   }
		 
		
		 file.close();

		
		
		
		return listeModifV15;
	}
	
	


	private static List<String> getInfos(String codeRef){
		
		List<String> data = new ArrayList<>();
		
		String codeParent="";
		
		String[] refElmt = codeRef.split("");
		
		for(int i=0; i< refElmt.length;i++)
		{
			
			
			 
		
		  if(refElmt.length==1) {codeParent =  "parent"; data.add(codeParent); data.add("Famille");}
		  //PropertiesUtil.getProperties("noeud_parent");}
		  if(refElmt.length==3) {codeParent = refElmt[0]; data.add(codeParent); data.add("Sous famille");}
		  if(refElmt.length==4) {codeParent = refElmt[0].concat(refElmt[1].concat(refElmt[2])); data.add(codeParent); data.add("Gamme");}
		  if(refElmt.length==5) {codeParent = refElmt[0].concat(refElmt[1].concat(refElmt[2].concat(refElmt[3]))); data.add(codeParent); data.add("Sous-gamme");}
		  if(refElmt.length==7) {codeParent = refElmt[0].concat(refElmt[1].concat(refElmt[2].concat(refElmt[3].concat(refElmt[4])))); data.add(codeParent); data.add("Composant");}


		
		}
		
		return data;
		
	}
	
	 private static Date returnDate(String dateInString) throws ParseException {
	  	 
		   SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	       Date date = formatter.parse(dateInString);
	           
		   return date;

	       
	    }
	
	
	

	
}
