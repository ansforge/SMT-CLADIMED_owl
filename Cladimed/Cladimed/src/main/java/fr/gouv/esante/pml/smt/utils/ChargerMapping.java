package fr.gouv.esante.pml.smt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ChargerMapping {
	
	
	
	public static HashMap<String, List<String>> listConcepts = new HashMap<String, List<String>>();
	
	
	public static  void chargeExcelConceptToList(final String xlsFile) throws Exception {
		
		
	   List<String> listeNouveauteV15 =  getNouveauteV15(xlsFile);
		
		FileInputStream file = new FileInputStream(new File(xlsFile));
			
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		
		XSSFSheet sheet = workbook.getSheetAt(Integer.parseInt(PropertiesUtil.getProperties("classification_active")));
		
		Iterator<Row> rowIterator = sheet.iterator();
		
		rowIterator.next();
		
		 while (rowIterator.hasNext()) {
	    	 
	    	 Row row = rowIterator.next();
	    	 Cell c2 = row.getCell(5); //get Ref Code
		     Cell c3 = row.getCell(6); // get Libelle
		     
		     String refCode = c2.getStringCellValue();
		     String libelle = c3.getStringCellValue();
		     
		      //Calcul Ref Parent, Type
		      
		       List<String> data = getInfos(refCode);
		    	       
				List<String> listedonnees= new ArrayList<>();
				
				listedonnees.add(data.get(0));// code Parent
				
				listedonnees.add(libelle);
				
				
				
				if (listeNouveauteV15.contains(refCode)) {
					
					listedonnees.add(PropertiesUtil.getProperties("Nouveaute_V15_Comment"));
					//status : active
					//date creation 2021
					
				} else {
					listedonnees.add("");
					// status active
					//date creation 2020
					
				}
				
				listedonnees.add(data.get(1));// type
				
				
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
	
	
	

	
}
