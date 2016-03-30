package edu.kit.ipd.sdq.kamp.core;

import java.io.IOException;
import java.util.List;

import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelWriter {

	public static final int COLUMN_PREFIX = 0;
//	public static final int COLUMN_BASICACTIVITY = 1;
//	public static final int COLUMN_ARCHITECTUREELEMENT = 2;
	public static final int COLUMN_ACTIVITYTYPE = 1;
	public static final int COLUMN_DESCRIPTION = 2;
	public static final int COLUMN_ELEMENTNAME = 3;
	public static final int COLUMN_AFFECTEDARCHITECTUREELEMENTS = 4;
	
	private int currentRow;
	private WritableSheet sheet;

	public void saveActivitiesToExcelFile(String filename, List<Activity> activityList, String prefix) {
    	
    	WritableWorkbook workbook = null;
    	
    	this.currentRow = 0;
    	
    	try {
			workbook = Workbook.createWorkbook(new java.io.File(filename));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 

    	if (workbook!=null) {
        	String username = System.getProperty("user.name");
    		this.sheet = workbook.createSheet("Arbeitsplan" + ((username!=null) ? " " + username : ""), 0); 
    		
    		addLabelToSheet(this.sheet, COLUMN_ACTIVITYTYPE, this.currentRow, "Aktivitätstyp");
    		addLabelToSheet(this.sheet, COLUMN_DESCRIPTION, this.currentRow, "Beschreibung der Aktivität");
    		addLabelToSheet(this.sheet, COLUMN_ELEMENTNAME, this.currentRow, "Betroffene Entwicklungsartefakte");
    		addLabelToSheet(this.sheet, COLUMN_AFFECTEDARCHITECTUREELEMENTS, this.currentRow, "Betroffene Architekturelemente");
    		this.currentRow++;
    		
    		saveActivitiesToExcelFile(activityList, "");
    		
    		try {
				workbook.write();
	    		workbook.close(); 
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (WriteException e) {
				throw new RuntimeException(e);
			}
    	}
    	
	}
    
    
    private void addLabelToSheet(WritableSheet sheet, int column, int row, String text) {
    	Label label = new Label(column, row, text);
    	
    	try {
			sheet.addCell(label);
		} catch (RowsExceededException e) {
			throw new RuntimeException(e);
		} catch (WriteException e) {
			throw new RuntimeException(e);
		} 
    }
    
    private void addNumberToSheet(WritableSheet sheet, int column, int row, double value) {
    	Number number = new Number(column, row, value);
    	try {
			sheet.addCell(number);
		} catch (RowsExceededException e) {
			throw new RuntimeException(e);
		} catch (WriteException e) {
			throw new RuntimeException(e);
		} 
    }
    
   
    
    private void saveActivitiesToExcelFile(List<Activity> activityList, String prefix) {
    	for (Activity activity : activityList) {

			
			if (activity.getElementType()==ActivityElementType.BASICCOMPONENT) {
				this.currentRow++;
			}
			
			addLabelToSheet(this.sheet, COLUMN_PREFIX, this.currentRow, prefix+"");
//			addLabelToSheet(this.sheet, COLUMN_BASICACTIVITY, this.currentRow, translateBasicActivity(activity.getBasicActivity())+"");
//			addLabelToSheet(this.sheet, COLUMN_ARCHITECTUREELEMENT, this.currentRow, translateElementType(activity.getElementType())+"");
			addLabelToSheet(this.sheet, COLUMN_ACTIVITYTYPE, this.currentRow, translateActivityType(activity.getType())+"");
			addLabelToSheet(this.sheet, COLUMN_DESCRIPTION, this.currentRow, activity.getDescription()+"");
			addLabelToSheet(this.sheet, COLUMN_ELEMENTNAME, this.currentRow, activity.getElementName()+"");
			addLabelToSheet(this.sheet, COLUMN_AFFECTEDARCHITECTUREELEMENTS, this.currentRow, 
					(activity.getAffectingArchitectureElement()!=null) ? activity.getAffectingArchitectureElement() : "");
			this.currentRow++;

			if (!activity.getSubactivities().isEmpty()) {
				saveActivitiesToExcelFile(activity.getSubactivities(), prefix + "=");
			}
			if (!activity.getFollowupActivities().isEmpty()) {
				saveActivitiesToExcelFile(activity.getFollowupActivities(), prefix+"=>");
			}
		}
	}
    
    private String translateBasicActivity(BasicActivity basicActivity) {
    	switch(basicActivity) {
			case ADD:
				return "Hinzufügen von";
			case CHECKANDUPDATE:
				return "Prüfen und Aktualisieren von";
			case EXECUTE:
				return "Durchführen von";
			case MODIFY:
				return "Modifizieren/Bearbeiten von";
			case REMOVE:
				return "Entfernen von";
			default:
				return "";
    	}
    }
    
    private String translateElementType(ActivityElementType elementType) {
    	switch(elementType) {
			case ASSEMBLYCONNECTOR:
				return "Assemblierungskonnektor";
			case BASICCOMPONENT:
				return "Komponente";
			case BUILDCONFIGURATION:
				return "Baukonfiguration";
			case COLLECTIONDATATYPE:
				return "Kollektions-Datentyp";
			case COMPOSITECOMPONENT:
				return "Komposit-Komponente";
			case COMPOSITEDATATYPE:
				return "Komposit-Datentyp";
			case DATATYPE:
				return "Datentyp";
			case DEPLOYMENTCONFIGURATION:
				return "Inbetriebnahmekonfiguration";
			case INTERFACE:
				return "Schnittstelle";
			case INTERFACESIGNATURE:
				return "Schnittstellen-Operation";
			case PROVIDEDOPERATION:
				return "Angebotene Operation";
			case PROVIDEDROLE:
				return "Schnittstellenangebot";
			case RELEASECONFIGURATION:
				return "Bereitstellungskonfiguration";
			case REQUIREDOPERATION:
				return "Nachgefragte Operation";
			case REQUIREDROLE:
				return "Schnittstellennachfrage";
			case SOURCECODEFILES:
				return "Quelltextdateien";
			case METADATAFILES:
				return "Metadatendateien";
			case TESTCASE:
				return "Testfälle";
			default:
				return elementType.toString();
    	}
    }
    
    private String translateActivityType(ActivityType activityType) {
    	switch(activityType) {
		case ARCHITECTUREMODELDIFF:
			return "Architektur-Bezogene Aktivität";
		case BUILDCONFIGURATION:
			return "Baukonfiguration";
		case BUILDEXECUTION:
			return "Baudurchführung";
		case DEPLOYMENTCONFIGURATION:
			return "Inbetriebnahmekonfiguration";
		case DEPLOYMENTEXECUTION:
			return "Inbetriebnahmedurchführung";
		case IMPLEMENTATION_METADATA:
			return "Metadatenbearbeitung";
		case IMPLEMENTATION_SOURCECODE:
			return "Quelltextbearbeitung";
		case INTERNALMODIFICATIONMARK:
			return "Modifikationskennzeichnung";
		case RELEASECONFIGURATION:
			return "Bereitstellungskonfiguration";
		case RELEASEEXECUTION:
			return "Bereitstellung";
		case TESTDEVELOPMENT:
			return "Testentwicklung";
		case TESTEXECUTION:
			return "Testdurchführung";
		case TESTUPDATE:
			return "Testaktualisierung";
		default:
			return "<sonstiges>";
    	}
    }
    
}
