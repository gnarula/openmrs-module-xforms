package org.openmrs.module.xforms.download;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.xforms.XformConstants;
import org.openmrs.module.xforms.XformsService;
import org.openmrs.module.xforms.model.PatientData;
import org.openmrs.module.xforms.model.PatientTableField;
import org.openmrs.module.xforms.model.PatientTableFieldBuilder;
import org.openmrs.module.xforms.util.XformsUtil;


/**
 * Manages patient downloads.
 * 
 * @author Daniel
 *
 */
public class PatientDownloadManager {

	private static Log log = LogFactory.getLog(PatientDownloadManager.class);

	
	public static void downloadPatients(String cohortId, OutputStream os, String serializerKey) throws Exception{
		
        if(cohortId == null)
            cohortId = Context.getAdministrationService().getGlobalProperty(XformConstants.GLOBAL_PROP_KEY_PATIENT_DOWNLOAD_COHORT);

        if(serializerKey == null)
        	serializerKey = XformConstants.GLOBAL_PROP_KEY_PATIENT_SERIALIZER;
        
		XformsService xformsService = (XformsService)Context.getService(XformsService.class);
        XformsUtil.invokeSerializationMethod("serialize",os,serializerKey , XformConstants.DEFAULT_PATIENT_SERIALIZER, getPatientData(cohortId,xformsService));
 	}
	
	public static void downloadPatients(String name, String identifier, OutputStream os,String serializerKey) throws Exception{
		if(serializerKey == null)
        	serializerKey = XformConstants.GLOBAL_PROP_KEY_PATIENT_SERIALIZER;
		
		XformsService xformsService = (XformsService)Context.getService(XformsService.class);
        XformsUtil.invokeSerializationMethod("serialize",os, serializerKey, XformConstants.DEFAULT_PATIENT_SERIALIZER, getPatientData(name,identifier,xformsService));
 	}
	
	private static PatientData getPatientData(String sCohortId,XformsService xformsService){
		//Context.openSession(); //This prevents the bluetooth server from failing with the form field lazy load exception.
		PatientData patientData  = new PatientData();
		
		Integer cohortId = getCohortId(sCohortId);
		if(cohortId != null){
			Cohort cohort = Context.getCohortService().getCohort(cohortId);
			Set<Integer> patientIds = cohort.getMemberIds();
			if(patientIds != null && patientIds.size() > 0){
				patientData.setPatients(getPantients(patientIds));
				List<PatientTableField> fields = PatientTableFieldBuilder.getPatientTableFields(xformsService);
				if(fields != null && fields.size() > 0){
					patientData.setFields(fields);
					patientData.setFieldValues(PatientTableFieldBuilder.getPatientTableFieldValues(new ArrayList(patientIds), fields, xformsService));
				}
			}
			
			List<Patient> patients = patientData.getPatients();
			if(patients != null && patients.size() > 0){
				for(Patient patient : patients)
					patientData.addMedicalHistory(xformsService.getPatientMedicalHistory(patient.getPatientId()));
			}
		}
		
		return patientData;
	}
	
	private static PatientData getPatientData(String name, String identifier,XformsService xformsService){
		//Context.openSession(); //This prevents the bluetooth server from failing with the form field lazy load exception.
		PatientData patientData  = new PatientData();
		
		if(name != null && name.trim().length() == 0)
			name = null;
		if(identifier != null && identifier.trim().length() == 0)
			identifier = null;
		
		List<Patient> patients = Context.getPatientService().getPatients(name, identifier, null);
		patientData.setPatients(patients);
		if(patients != null){
			for(Patient patient : patients){
				List<PatientTableField> fields = PatientTableFieldBuilder.getPatientTableFields(xformsService);
				if(fields != null && fields.size() > 0){
					patientData.setFields(fields);
					patientData.setFieldValues(PatientTableFieldBuilder.getPatientTableFieldValues(getPatientIds(patients), fields, xformsService));
					patientData.addMedicalHistory(xformsService.getPatientMedicalHistory(patient.getPatientId()));
				}
			}
		}
		
		return patientData;
	}
	
	private static List<Integer> getPatientIds(List<Patient> patients){
		List<Integer> patientIds = new ArrayList<Integer>();
		for(Patient patient : patients)
			patientIds.add(patient.getPatientId());

		return patientIds;
	}
	
	private static Integer getCohortId(String cohortId){
		if(cohortId == null || cohortId.trim().length() == 0)
			return null;
		try{
			return Integer.parseInt(cohortId);
		}catch(Exception e){
            log.error(e.getMessage(),e);
		}
		
		return null;
	}
	
	private static List<Patient> getPantients(Collection<Integer> patientIds){
		List<Patient> patients = new ArrayList<Patient>();
		
		PatientService patientService = Context.getPatientService();
		for(Integer patientId : patientIds)
			patients.add(patientService.getPatient(patientId));
		
		return patients;
	}
    
	public static void downloadCohorts(OutputStream os, String serializerKey) throws Exception{
		if(serializerKey == null)
        	serializerKey = XformConstants.GLOBAL_PROP_KEY_COHORT_SERIALIZER;
		
        XformsUtil.invokeSerializationMethod("serialize",os, serializerKey, XformConstants.DEFAULT_COHORT_SERIALIZER, Context.getCohortService().getCohorts());
     }
}
