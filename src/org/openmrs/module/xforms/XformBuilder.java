package org.openmrs.module.xforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kxml2.kdom.*;
import java.io.*;
import org.xmlpull.v1.*;
import org.kxml2.io.*;
import java.util.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.*;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.Role;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.xforms.XformConstants;

//TODO This class is too big. May need breaking into smaller ones.

/**
 * Builds xforms from openmrs schema and template files.
 * This class also builds the XForm for creating new patients.
 * 
 * @author Daniel Kayiwa
 *
 */
public final class XformBuilder {
	
	/** Namespace for XForms. */
	public static final String NAMESPACE_XFORMS = "http://www.w3.org/2002/xforms";
	
	/** Namespace for XForm events. */
	public static final String NAMESPACE_XFORM_EVENTS = "http://www.w3.org/2001/xml-events";
	
	/** Namespace for XML schema. */
	public static final String NAMESPACE_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	/** Namespace for XML schema instance. */
	public static final String NAMESPACE_XML_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	
	/** Namespace for XHTML. */
	public static final String NAMESPACE_XHTML = "http://www.w3.org/1999/xhtml";
	
	/** Namespace for openmrs. */
	public static final String NAMESPACE_OPENMRS = "http://localhost:8080/openmrs/moduleServlet/formentry/forms/schema/4-109";
	
	/** Namespace prefix for XForms. */
	public static final String PREFIX_XFORMS = "xf";
	
	/** Namespace prefix for XForms events. */
	public static final String PREFIX_XFORM_EVENTS = "ev";
	
	/** Namespace prefix for XML schema. */
	public static final String PREFIX_XML_SCHEMA = "xsd";
	
	/** The second Namespace prefix for XML schema. */
	public static final String PREFIX_XML_SCHEMA2 = "xs";
	
	/** Namespace prefix for XML schema instance. */
	public static final String PREFIX_XML_INSTANCES = "xsi";
	
	/** Namespace prefix for openmrs. */
	public static final String PREFIX_OPENMRS= "openmrs";
	
	/** The character separator for namespace prefix. */
	public static final String NAMESPACE_PREFIX_SEPARATOR= ":";
	
	public static final String CONTROL_INPUT = "input";
	public static final String CONTROL_SELECT = "select";
	public static final String CONTROL_SELECT1 = "select1";
	public static final String CONTROL_SUBMIT = "submit";
	
	public static final String NODE_LABEL = "label";
	public static final String NODE_HINT = "hint";
	public static final String NODE_VALUE= "value";
	public static final String NODE_ITEM = "item";
	public static final String NODE_HTML = "html";
	public static final String NODE_SCHEMA = "schema";
	public static final String NODE_HEAD = "head";
	public static final String NODE_BODY = "body";
	public static final String NODE_SUBMISSION= "submission";
	public static final String NODE_INSTANCE = "instance";
	public static final String NODE_MODEL = "model";
	public static final String NODE_BIND = "bind";
	public static final String NODE_TITLE = "title";
	public static final String NODE_ENUMERATION = "enumeration";
	public static final String NODE_DATE = "date";
	public static final String NODE_TIME = "time";
	public static final String NODE_SIMPLETYPE = "simpleType";
	public static final String NODE_COMPLEXTYPE = "complexType";
	public static final String NODE_SEQUENCE = "sequence";
	public static final String NODE_RESTRICTION = "restriction";
	public static final String NODE_ATTRIBUTE = "attribute";
	public static final String NODE_FORM = "form";
	public static final String NODE_PATIENT = "patient";
	public static final String NODE_XFORMS_VALUE= "xforms_value";
	public static final String NODE_OBS= "obs";
	
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_ACTION = "action";
	public static final String ATTRIBUTE_METHOD = "method";
	public static final String ATTRIBUTE_NODESET = "nodeset";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_BIND = "bind";
	public static final String ATTRIBUTE_REF = "ref";
	public static final String ATTRIBUTE_APPEARANCE = "appearance";
	public static final String ATTRIBUTE_NILLABLE = "nillable";
	public static final String ATTRIBUTE_MAXOCCURS = "maxOccurs";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_FIXED = "fixed";
	public static final String ATTRIBUTE_OPENMRS_DATATYPE = "openmrs_datatype";
	public static final String ATTRIBUTE_OPENMRS_CONCEPT = "openmrs_concept";
	public static final String ATTRIBUTE_OPENMRS_ATTRIBUTE = "openmrs_attribute";
	public static final String ATTRIBUTE_OPENMRS_TABLE = "openmrs_table";
	public static final String ATTRIBUTE_SUBMISSION = "submission";
	public static final String ATTRIBUTE_MULTIPLE = "multiple";
	public static final String ATTRIBUTE_READONLY= "readonly";
	public static final String ATTRIBUTE_REQUIRED = "required";
	public static final String ATTRIBUTE_DESCRIPTION_TEMPLATE = "description-template";
	public static final String ATTRIBUTE_BASE = "base";	
	public static final String ATTRIBUTE_XSI_NILL = "xsi:nil";
	
	public static final String XPATH_VALUE_TRUE = "true()";
	public static final String VALUE_TRUE = "true";
	public static final String VALUE_FALSE = "false";
	public static final String NODE_SEPARATOR = "/";
	
	public static final String SUBMIT_ID = "submit";
	public static final String SUBMIT_LABEL= "Submit";
	public static final String SUBMISSION_METHOD = "post";
	
	public static final String HTML_TAG_TABLE = "table";
	public static final String HTML_TAG_TABLE_ROW = "tr";
	public static final String HTML_TAG_TABLE_CELL = "td";
	public static final String HTML_TAG_PARAGRAPH = "p";
	
	public static final String HTML_ATTRIBUTE_CELLSPACING = "cellspacing";
	public static final String HTML_ATTRIBUTE_CELLSPACING_VALUE = "10";
	public static final String HTML_ATTRIBUTE_CELLPADDING = "cellpadding";
	public static final String HTML_ATTRIBUTE_CELLPADDING_VALUE = "10";
	public static final String CONTROL_LABEL_PADDING = "     ";
	
	public static final String NODE_ENCOUNTER_LOCATION_ID = "encounter.location_id";
	public static final String NODE_ENCOUNTER_PROVIDER_ID = "encounter.provider_id";
	public static final String NODE_ENCOUNTER_ENCOUNTER_ID = "encounter.encounter_id";
	public static final String NODE_ENCOUNTER_ENCOUNTER_DATETIME = "encounter.encounter_datetime";
	public static final String NODE_PATIENT_PATIENT_ID = "patient.patient_id";
	public static final String NODE_PATIENT_FAMILY_NAME= "patient.family_name";
	public static final String NODE_PATIENT_MIDDLE_NAME = "patient.middle_name";
	public static final String NODE_PATIENT_GIVEN_NAME = "patient.given_name";
	
	public static final String NODE_PATIENT_ID = "patient_id";
	public static final String NODE_FAMILY_NAME = "family_name";
	public static final String NODE_MIDDLE_NAME = "middle_name";
	public static final String NODE_GIVEN_NAME = "given_name";
	public static final String NODE_GENDER = "gender";
	public static final String NODE_IDENTIFIER = "identifier";
	public static final String NODE_BIRTH_DATE = "birth_date";
	public static final String NODE_LOCATION_ID = "location_id";
	public static final String NODE_PROVIDER_ID = "provider_id";
	public static final String NODE_IDENTIFIER_TYPE_ID = "patient_identifier_type_id";
	
	public static final String DATA_TYPE_DATE = "xs:date";
	public static final String DATA_TYPE_INT = "xs:int";
	public static final String DATA_TYPE_TEXT = "xs:string";
	
	public static final String MULTIPLE_SELECT_VALUE_SEPARATOR = " ";
	
	/** The last five characters of an xml schema complex type name for a concept. e.g weight_kg_type where _type is appended to the concept weight_kg*/
	public static final String COMPLEX_TYPE_NAME_POSTFIX = "_type";
	
	private static String xformAction;
	
	private static Element currentRow = null;
	private static boolean singleColumnLayout = true;
		
	/**
	 * Builds an Xform from an openmrs schema and template xml.
	 * 
	 * @param schemaXml - the schema xml.
	 * @param templateXml - the template xml.
	 * @return - the built xform's xml.
	 */
	public static String getXform4mStrings(String schemaXml, String templateXml,String xformAction){
		return getXform4mDocuments(getDocument(new StringReader(schemaXml)),getDocument(new StringReader(templateXml)),xformAction);
	}
	
	/**
	 * Creates an xform from schema and template files.
	 * 
	 * @param schemaPathName - the complete path and name of the schema file.
	 * @param templatePathName - the complete path and name of the template file
	 * @return the built xform's xml. 
	 */
	public static String getXform4mFiles(String schemaPathName, String templatePathName,String xformAction){
		try{
			Document schemaDoc = getDocument(new FileReader(schemaPathName));
			Document templateDoc = getDocument(new FileReader(templatePathName));
			return getXform4mDocuments(schemaDoc,templateDoc,xformAction);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Converts xml to a documnt object.
	 * 
	 * @param xml - the xml to convert.
	 * @return - the Document object containing the xml.
	 */
	public static Document getDocument(String xml){
		return getDocument(new StringReader(xml));
	}
	
	/**
	 * Sets the value of a node in a document.
	 * 
	 * @param doc - the document.
	 * @param name - the name of the node whose value to set.
	 * @param value - the value to set.
	 * @return - true if the node with the name was found, else false.
	 */
	public static boolean setNodeValue(Document doc, String name, String value){
		Element node = getElement(doc.getRootElement(),name);
		if(node == null)
			return false;
		
		setNodeValue(node,value);
		return true;
	}
	
	/**
	 * Sets the text value of a node.
	 * 
	 * @param node - the node whose value to set.
	 * @param value - the value to set.
	 */
	private static void setNodeValue(Element node, String value){
		for(int i=0; i<node.getChildCount(); i++){
			if(node.isText(i)){
				node.removeChild(i);
				node.addChild(Element.TEXT, value);
				return;
			}
		}

		node.addChild(Element.TEXT, value);
	}
	
	/**
	 * Sets the value of an attribute of a node in a document.
	 * 
	 * @param doc - the document.
	 * @param nodeName - the name of the node.
	 * @param attributeName - the name of the attribute.
	 * @param value - the value to set.
	 * @return
	 */
	public static boolean setNodeAttributeValue(Document doc, String nodeName, String attributeName, String value){
		Element node = getElement(doc.getRootElement(),nodeName);
		if(node == null)
			return false;
		
		node.setAttribute(null, attributeName, value);
		return true;
	}
	
	/**
	 * Gets a child element of a parent node with a given name.
	 * 
	 * @param parent - the parent element
	 * @param name - the name of the child.
	 * @return - the child element.
	 */
	private static Element getElement(Element parent, String name){
		for(int i=0; i<parent.getChildCount(); i++){
			if(parent.getType(i) != Element.ELEMENT)
				continue;
			
			Element child = (Element)parent.getChild(i);
			if(child.getName().equalsIgnoreCase(name))
				return child;
			
			child = getElement(child,name);
			if(child != null)
				return child;
		}
		
		return null;
	}
	
	/**
	 * Builds an Xfrom from an openmrs schema and template document.
	 * 
	 * @param schemaDoc - the schema document.
	 * @param templateDoc - the template document.
	 * @return - the built xform's xml.
	 */
	public static String getXform4mDocuments(Document schemaDoc, Document templateDoc, String xformAction){
		XformBuilder.xformAction = xformAction;
		Element formNode = (Element)templateDoc.getRootElement();
		
		currentRow = null;
		singleColumnLayout = false;
		if(VALUE_TRUE.equalsIgnoreCase(Context.getAdministrationService().getGlobalProperty("xforms.singleColumnLayout")))
				singleColumnLayout = true;
		
		Document doc = new Document();
		doc.setEncoding(XformConstants.DEFAULT_CHARACTER_ENCODING);
		Element htmlNode = doc.createElement(NAMESPACE_XHTML, null);
		htmlNode.setName(NODE_HTML);
		htmlNode.setPrefix(null, NAMESPACE_XHTML);
		htmlNode.setPrefix(PREFIX_XFORMS, NAMESPACE_XFORMS);
		htmlNode.setPrefix(PREFIX_XML_SCHEMA, NAMESPACE_XML_SCHEMA);
		htmlNode.setPrefix(PREFIX_XML_SCHEMA2, NAMESPACE_XML_SCHEMA);
		htmlNode.setPrefix(PREFIX_XML_INSTANCES, NAMESPACE_XML_INSTANCE);
		doc.addChild(org.kxml2.kdom.Element.ELEMENT, htmlNode);
		
		Element headNode = doc.createElement(NAMESPACE_XHTML, null);
		headNode.setName(NODE_HEAD);
		htmlNode.addChild(org.kxml2.kdom.Element.ELEMENT, headNode);
		
		Element bodyNode = doc.createElement(NAMESPACE_XHTML, null);
		bodyNode.setName(NODE_BODY);
		htmlNode.addChild(org.kxml2.kdom.Element.ELEMENT, bodyNode);
		
		Element titleNode =  doc.createElement(NAMESPACE_XHTML, null);
		titleNode.setName(NODE_TITLE);
		titleNode.addChild(Element.TEXT,formNode.getAttributeValue(null, ATTRIBUTE_NAME));
		headNode.addChild(Element.ELEMENT,titleNode);
		
		Element modelNode =  doc.createElement(NAMESPACE_XFORMS, null);
		modelNode.setName(NODE_MODEL);
		headNode.addChild(Element.ELEMENT,modelNode);
		
		Element instanceNode =  doc.createElement(NAMESPACE_XFORMS, null);
		instanceNode.setName(NODE_INSTANCE);
		modelNode.addChild(Element.ELEMENT,instanceNode);
		
		instanceNode.addChild(Element.ELEMENT, formNode);
		
		Element submitNode =  doc.createElement(NAMESPACE_XFORMS, null);
		submitNode.setName(NODE_SUBMISSION);
		submitNode.setAttribute(null, ATTRIBUTE_ID, SUBMIT_ID);
		submitNode.setAttribute(null, ATTRIBUTE_ACTION, xformAction);
		submitNode.setAttribute(null, ATTRIBUTE_METHOD, SUBMISSION_METHOD);
		Element hint = submitNode.createElement(NAMESPACE_XFORMS, NODE_HINT);
		hint.setName(NODE_HINT);
		hint.addChild(Element.TEXT, "Click to submit");
		submitNode.addChild(Element.ELEMENT, hint);
		modelNode.addChild(Element.ELEMENT,submitNode);
		
		Document xformSchemaDoc = new Document();
		xformSchemaDoc.setEncoding(XformConstants.DEFAULT_CHARACTER_ENCODING);
		Element xformSchemaNode = doc.createElement(NAMESPACE_XML_SCHEMA, null);
		xformSchemaNode.setName(NODE_SCHEMA);
		xformSchemaNode.setPrefix(PREFIX_XML_SCHEMA2, NAMESPACE_XML_SCHEMA);
		xformSchemaNode.setPrefix(PREFIX_OPENMRS, NAMESPACE_OPENMRS);
		xformSchemaNode.setAttribute(null, "elementFormDefault", "qualified");
		xformSchemaNode.setAttribute(null, "attributeFormDefault", "unqualified");
		xformSchemaDoc.addChild(org.kxml2.kdom.Element.ELEMENT, xformSchemaNode);
		
		Hashtable bindings = new Hashtable();
		parseTemplate(modelNode,formNode,bindings,bodyNode);
		parseSchema(schemaDoc.getRootElement(),bodyNode,modelNode,xformSchemaNode,bindings);
		
		return fromDoc2String(doc);
	}
	
	/**
	 * Gets the label of an openmrs standard form node
	 * 
	 * @param name - the name of the node.
	 * @return - the label.
	 */
	private static String getDisplayText(String name){
		if(name.equalsIgnoreCase(NODE_ENCOUNTER_ENCOUNTER_DATETIME))
			return "ENCOUNTER DATE";
		else if(name.equalsIgnoreCase(NODE_ENCOUNTER_LOCATION_ID))
			return "LOCATION";
		else if(name.equalsIgnoreCase(NODE_ENCOUNTER_PROVIDER_ID))
			return "PROVIDER";
		else if(name.equalsIgnoreCase(NODE_PATIENT_PATIENT_ID))
			return "PATIENT ID";
		else if(name.equalsIgnoreCase(NODE_PATIENT_MIDDLE_NAME))
			return "MIDDLE NAME";
		else if(name.equalsIgnoreCase(NODE_PATIENT_GIVEN_NAME))
			return "GIVEN NAME";
		else if(name.equalsIgnoreCase(NODE_PATIENT_FAMILY_NAME))
			return "FAMILY NAME";
		else
			return name.replace('_', ' ');
	}
	
	/**
	 * Parses an openmrs template and builds the bindings in the model plus
	 * UI controls for openmrs table field questions.
	 * 
	 * @param modelElement - the model element to add bindings to.
	 * @param formElement
	 * @param bindings - a hash table to populate with the built bindings.
	 * @param bodyNode - the body node to add the UI control to.
	 */
	private static void parseTemplate(Element modelElement, Element formElement, Hashtable bindings,Element bodyNode){
		int numOfEntries = formElement.getChildCount();
		for (int i = 0; i < numOfEntries; i++) {
			if (formElement.isText(i))
				continue; //Ignore all text.
			
			Element child = formElement.getElement(i);
			if(child.getAttributeValue(null, ATTRIBUTE_OPENMRS_DATATYPE) == null && child.getAttributeValue(null, ATTRIBUTE_OPENMRS_CONCEPT) != null)
				continue; //These could be like options for multiple select, which take true or false value.
			
			String name =  child.getName();
			
			//If the node has an openmrs_concept attribute but is not called obs,
			//Or has the openmrs_attribite and openmrs_table attributes. 
			if((child.getAttributeValue(null, ATTRIBUTE_OPENMRS_CONCEPT) != null && !child.getName().equals(NODE_OBS)) ||
			  (child.getAttributeValue(null, ATTRIBUTE_OPENMRS_ATTRIBUTE) != null && child.getAttributeValue(null, ATTRIBUTE_OPENMRS_TABLE) != null)){
				
				Element bindNode = createBindNode(modelElement,child,bindings);
				
				if(isMultSelectNode(child))
					addMultipleSelectXformValueNode(child);
					
				if(isTableFieldNode(child))
					setTableFieldDataType(name,bindNode);
			}
			
			//if(child.getAttributeValue(null, ATTRIBUTE_OPENMRS_ATTRIBUTE) != null && child.getAttributeValue(null, ATTRIBUTE_OPENMRS_TABLE) != null){
			//Build UI controls for the openmrs fixed table fields. The rest of the controls are built from
			if(isTableFieldNode(child)){
				Element controlNode = buildTableFieldUIControlNode(child,bodyNode);
				
				if(name.equalsIgnoreCase(NODE_ENCOUNTER_LOCATION_ID))
					populateLocations(controlNode);
				
				if(name.equalsIgnoreCase(NODE_ENCOUNTER_PROVIDER_ID))
					populateProviders(controlNode);
			}
			
			parseTemplate(modelElement,child,bindings, bodyNode);
		}
	}
	
	/**
	 * Builds a UI control node for a table field.
	 * 
	 * @param node - the node whose UI control to build.
	 * @param bodyNode - the body node to add the UI control to.
	 * @return  - the created UI control node.
	 */
	private static Element buildTableFieldUIControlNode(Element node,Element bodyNode){
		
		Element controlNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
		String name = node.getName();
		
		//location and provider are not free text.
		if(name.equalsIgnoreCase(NODE_ENCOUNTER_LOCATION_ID) || name.equalsIgnoreCase(NODE_ENCOUNTER_PROVIDER_ID))
			controlNode.setName(CONTROL_SELECT1);
		else
			controlNode.setName(CONTROL_INPUT);
		
		controlNode.setAttribute(null, ATTRIBUTE_BIND, name);
		
		//create the label
		Element labelNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
		labelNode.setName(NODE_LABEL);
		labelNode.addChild(Element.TEXT, getDisplayText(name)+ "     ");
		controlNode.addChild(Element.ELEMENT, labelNode);
		
		addControl(bodyNode,controlNode);
		
		return controlNode;
	}
	
	/**
	 * Populates a UI control node with providers.
	 * 
	 * @param controlNode - the UI control node.
	 */
	private static void populateProviders(Element controlNode){
		List<User> providers = Context.getUserService().getUsersByRole(new Role("Provider"));
		for(User provider : providers){
			Element itemNode = /*bodyNode*/controlNode.createElement(NAMESPACE_XFORMS, null);
			itemNode.setName(NODE_ITEM);
			
			Element node = itemNode.createElement(NAMESPACE_XFORMS, null);
			node.setName(NODE_LABEL);
			node.addChild(Element.TEXT, provider.getPersonName().toString());
			itemNode.addChild(Element.ELEMENT, node);
			
			node = itemNode.createElement(NAMESPACE_XFORMS, null);
			node.setName(NODE_VALUE);
			node.addChild(Element.TEXT, provider.getUserId().toString());
			itemNode.addChild(Element.ELEMENT, node);
			
			controlNode.addChild(Element.ELEMENT, itemNode);
		}

	}
	
	/**
	 * Populates a UI control node with locations.
	 * 
	 * @param controlNode - the UI control node.
	 */
	private static void populateLocations(Element controlNode){
		List<Location> locations = Context.getEncounterService().getLocations();
		for(Location loc : locations){
			Element itemNode = /*bodyNode*/controlNode.createElement(NAMESPACE_XFORMS, null);
			itemNode.setName(NODE_ITEM);
			
			Element node = itemNode.createElement(NAMESPACE_XFORMS, null);
			node.setName(NODE_LABEL);
			node.addChild(Element.TEXT, loc.getName());
			itemNode.addChild(Element.ELEMENT, node);
			
			node = itemNode.createElement(NAMESPACE_XFORMS, null);
			node.setName(NODE_VALUE);
			node.addChild(Element.TEXT, loc.getLocationId().toString());
			itemNode.addChild(Element.ELEMENT, node);
			
			controlNode.addChild(Element.ELEMENT, itemNode);
		}
	}
	
	/**
	 * Creates a model binding node.
	 * 
	 * @param modelElement - the model node to add the binding to.
	 * @param node - the node whose binding to create.
	 * @param bindings - a hashtable of node bindings keyed by their names.
	 * @return - the created binding node.
	 */
	private static Element createBindNode(Element modelElement,Element node, Hashtable bindings){
		Element bindNode = modelElement.createElement(NAMESPACE_XFORMS, null);
		bindNode.setName(ATTRIBUTE_BIND);
		bindNode.setAttribute(null, ATTRIBUTE_ID, node.getName());
		bindNode.setAttribute(null, ATTRIBUTE_NODESET, getNodesetAttValue(node));
		modelElement.addChild(Element.ELEMENT, bindNode);
		
		//store the binding node with the key being its id attribute.
		bindings.put(node.getName(), bindNode);
		return bindNode;
	}
	
	/**
	 * Adds a node to hold the xforms value for a multiple select node.
	 * The value is a space delimited list of selected answers, which will later on be used to
	 * fill the true or false values as expected by openmrs multiple select questions.
	 * 
	 * @param child - the multiple select node to add the value node to.
	 */
	private static void addMultipleSelectXformValueNode( Element node){
		//Element xformsValueNode = modelElement.createElement(null, null);
		Element xformsValueNode = node.createElement(null, null);
		xformsValueNode.setName(NODE_XFORMS_VALUE);
		xformsValueNode.setAttribute(null, ATTRIBUTE_XSI_NILL, VALUE_TRUE);
		node.addChild(Element.ELEMENT, xformsValueNode);
	}
	
	/**
	 * Set data types for the openmrs fixed table fields.
	 * 
	 * @param name - the name of the question node.
	 * @param bindingNode - the binding node whose type attribute we are to set.
	 */
	private static void setTableFieldDataType(String name, Element bindNode){
		if(name.equalsIgnoreCase(NODE_ENCOUNTER_ENCOUNTER_DATETIME))
			bindNode.setAttribute(null, ATTRIBUTE_TYPE, DATA_TYPE_DATE);
		else if(name.equalsIgnoreCase(NODE_ENCOUNTER_LOCATION_ID))
			bindNode.setAttribute(null, ATTRIBUTE_TYPE, DATA_TYPE_INT);
		else if(name.equalsIgnoreCase(NODE_ENCOUNTER_PROVIDER_ID))
			bindNode.setAttribute(null, ATTRIBUTE_TYPE, DATA_TYPE_INT);
		else if(name.equalsIgnoreCase(NODE_PATIENT_PATIENT_ID))
			bindNode.setAttribute(null, ATTRIBUTE_TYPE, DATA_TYPE_INT);
		else
			bindNode.setAttribute(null, ATTRIBUTE_TYPE, DATA_TYPE_TEXT);

	}
	
	/**
	 * Check whether a node is an openmrs table field node
	 * These are the ones with the attributes: openmrs_table and openmrs_attribute
	 * e.g. patient_unique_number openmrs_table="PATIENT_IDENTIFIER" openmrs_attribute="IDENTIFIER"
	 *  
	 * @param node - the node to check.
	 * @return - true if it is, else false.
	 */
	private static boolean isTableFieldNode(Element node){
		return (node.getAttributeValue(null, ATTRIBUTE_OPENMRS_ATTRIBUTE) != null && node.getAttributeValue(null, ATTRIBUTE_OPENMRS_TABLE) != null);		

	}
	
	/**
	 * Checks whether a node is multiple select or not.
	 * 
	 * @param child - the node to check.
	 * @return - true if multiple select, else false.
	 */
	private static boolean isMultSelectNode(Element child){
		return (child.getAttributeValue(null, ATTRIBUTE_MULTIPLE) != null && child.getAttributeValue(null, ATTRIBUTE_MULTIPLE).equals("1"));
	}
	
	/**
	 * Parses the openmrs schema document and builds UI conrols from openmrs concepts.
	 * 
	 * @param rootNode - the schema document root node.
	 * @param bodyNode - the xform document body node.
	 * @param modelNode - the xform model node.
	 * @param xformSchemaNode - the root node of the xml schema data types.
	 * @param bindings - a hashtable of node bindings.
	 */
	private static void parseSchema(Element rootNode,Element bodyNode, Element modelNode, Element xformSchemaNode, Hashtable bindings){
		int numOfEntries = rootNode.getChildCount();
		for (int i = 0; i < numOfEntries; i++) {
			if (rootNode.isText(i))
				continue;
			
			Element child = rootNode.getElement(i);
			String name = child.getName();
			if(name.equalsIgnoreCase(NODE_COMPLEXTYPE) && isUserDefinedSchemaElement(child))
				parseComplexType(child,child.getAttributeValue(null, ATTRIBUTE_NAME),bodyNode,xformSchemaNode,bindings);
			else if(name.equalsIgnoreCase(NODE_SIMPLETYPE) && isUserDefinedSchemaElement(child))
				xformSchemaNode.addChild(0, Element.ELEMENT, child);
		}
		
		if(xformSchemaNode.getChildCount() > 0)
			modelNode.addChild(0, Element.ELEMENT, xformSchemaNode);
	}
		
	/**
	 * Parses a complex type node in an openmrs schema document.
	 * 
	 * @param complexTypeNode - the complex type node.
	 * @param name - the name of the complex type node.
	 * @param bodyNode - the xform body node.
	 * @param xformSchemaNode - the top node of the xml schema data types.
	 * @param bindings - a hashtable of node bindings.
	 */
	private static void parseComplexType(Element complexTypeNode,String name, Element bodyNode, Element xformSchemaNode, Hashtable bindings){
		name = getBindNodeName(name);
		if(name == null)
			return;
		
		Element labelNode = null, bindNode = (Element)bindings.get(name);
		
		for(int i=0; i<complexTypeNode.getChildCount(); i++){
			if(complexTypeNode.isText(i))
				continue; //ignore text.
			
			Element node = (Element)complexTypeNode.getChild(i);
			if(node.getName().equalsIgnoreCase(NODE_SEQUENCE))
				labelNode = parseSequenceNode(name,node,bodyNode,xformSchemaNode,bindNode);
			
			if(labelNode != null && isNodeWithConceptNameAndId(node))
				addLabelTextAndHint(labelNode,node);
		}
	}
	
	/**
	 * Gets the binding of a complex type node. 
	 * An example of such a node would be: complexType name="weight_kg_type"
	 * 
	 * @param name - the name of the complex type node.
	 * @param bindings - a hashtable of bingings.
	 * @return - the binding node.
	 */
	private static Element getBindNode(String name, Hashtable bindings){
		if(name == null)
			return null;
		
		//We are only dealing with names ending with _type. e.g. education_level_type
		//Openmrs appends the _type to the name when creating xml types for each concept
		if(name.indexOf(COMPLEX_TYPE_NAME_POSTFIX) == -1)
			return null;

		//remove the _type part. e.g from above the name is education_level
		name = name.substring(0, name.length() - COMPLEX_TYPE_NAME_POSTFIX.length());
		Element bindNode = (Element)bindings.get(name);
		return bindNode;
	}
	
	/**
	 * Gets the binding node name of a complex type node name. 
	 * An example of such a node would be weight_kg for: complexType name="weight_kg_type"
	 * 
	 * @param name - the name of the complex type node.
	 * @return - the binding node name.
	 */
	private static String getBindNodeName(String name){
		if(name == null)
			return null;
		
		//We are only dealing with names ending with _type. e.g. education_level_type
		//Openmrs appends the _type to the name when creating xml types for each concept
		if(name.indexOf(COMPLEX_TYPE_NAME_POSTFIX) == -1)
			return null;

		//remove the _type part. e.g from above the name is education_level
		name = name.substring(0, name.length() - COMPLEX_TYPE_NAME_POSTFIX.length());
		return name;
	}
	
	/**
	 * Adds text and hint to a label node.
	 * 
	 * @param labelNode - the label node.
	 * @param node - the node having the concept name and id.
	 */
	private static void addLabelTextAndHint(Element labelNode, Element node){
		labelNode.addChild(Element.TEXT , getConceptName(node.getAttributeValue(null, ATTRIBUTE_FIXED)) + CONTROL_LABEL_PADDING);
		
		String hint = getConceptDescription(node);
		if(hint != null && hint.length() > 0){
			Element hintNode = /*bodyNode*/labelNode.createElement(NAMESPACE_XFORMS, null);
			hintNode.setName(NODE_HINT);
			hintNode.addChild(Element.TEXT, getConceptDescription(node));
			labelNode.getParent().addChild(1,Element.ELEMENT, hintNode);
		}
	}
	
	/**
	 * Checks if this node has the concept name and id. An example of such a node would be as:
	 *  <xs:attribute name="openmrs_concept" type="xs:string" use="required" fixed="5089^WEIGHT (KG)^99DCT" /> 
	 *  where the concept name and id combination we are refering to is: 5089^WEIGHT (KG)^99DCT
	 * 
	 * @param node - the node to check.
	 * @return true if so, else false.
	 */
	private static boolean isNodeWithConceptNameAndId(Element node){
		return node.getName().equalsIgnoreCase(NODE_ATTRIBUTE) && node.getAttributeValue(null, ATTRIBUTE_NAME) != null && node.getAttributeValue(null, ATTRIBUTE_NAME).equalsIgnoreCase(ATTRIBUTE_OPENMRS_CONCEPT);
	}
	
	/**
	 * Gets the concept id from a name and id combination.
	 * 
	 * @param conceptName - the concept name.
	 * @return - the id
	 */
	private static Integer getConceptId(String conceptName){
		return Integer.parseInt(conceptName.substring(0, conceptName.indexOf("^")));
	}
	
	/**
	 * Gets the description of a concept.
	 * 
	 * @param node - the node having the concept.
	 * @return - the concept description.
	 */
	private static String getConceptDescription(Element node){
		String name = node.getAttributeValue(null, ATTRIBUTE_FIXED);
		Concept concept = Context.getConceptService().getConcept(getConceptId(name));
		ConceptName conceptName = concept.getName();
		return conceptName.getDescription();
	}
	
	/**
	 * Gets the name of a concept from the name and id combination value.
	 * 
	 * @param val - the name and id combination.
	 * @return - the cencept name.
	 */
	private static String getConceptName(String val){
		return val.substring(val.indexOf('^')+1, val.lastIndexOf('^'));
	}
	
	/**
	 * Parses a sequence node from an openmrs schema document.
	 * 
	 * @param name
	 * @param sequenceNode
	 * @param bodyNode
	 * @param xformSchemaNode
	 * @param bindingNode
	 * @return the created label node.
	 */
	private static Element parseSequenceNode(String name,Element sequenceNode,Element bodyNode, Element xformSchemaNode, Element bindingNode ){
		Element labelNode = null,controlNode = bodyNode.createElement(NAMESPACE_XFORMS, null);;
		
		for(int i=0; i<sequenceNode.getChildCount(); i++){
			if(sequenceNode.isText(i))
				continue; //ignore text.
			
			Element node = (Element)sequenceNode.getChild(i);
			String itemName = node.getAttributeValue(null, ATTRIBUTE_NAME);
		
			//Instead of the value node, multiple select questions have one node
			//for each possible select option.
			if(!itemName.equalsIgnoreCase(NODE_VALUE)){
				if(!(itemName.equalsIgnoreCase(NODE_DATE) || itemName.equalsIgnoreCase(NODE_TIME)) && node.getAttributeValue(null, ATTRIBUTE_OPENMRS_CONCEPT) == null)
					labelNode = parseMultiSelectNode(name,itemName, node,controlNode,bodyNode, labelNode,bindingNode);
				continue;
			}
			
			//We are interested in the element whose name attribute is equal to value, 
			//for single select lists.
			if(node.getAttributeValue(null, ATTRIBUTE_NILLABLE).equalsIgnoreCase("0"))
				bindingNode.setAttribute(null, ATTRIBUTE_REQUIRED, XPATH_VALUE_TRUE);
			
			labelNode = parseSequenceValueNode(name,node,labelNode,bodyNode,bindingNode);
		}
		
		return labelNode;
	}
	
	/**
	 * Builds an xform input type control from a sequence value node.
	 * 
	 * @param name - the name of the complex type node we are dealing with.
	 * @param node - the value node.
	 * @param type - the type attribute value.
	 * @param labelNode - the label node.
	 * @param bindingNode - the binding node.
	 * @param bodyNode - the body node.
	 * @return returns the created label node.
	 */
	private static Element buildSequenceInputControlNode(String name,Element node,String type,Element labelNode, Element bindingNode,Element bodyNode){
		type = getPrefixedDataType(type);
		bindingNode.setAttribute(null, ATTRIBUTE_TYPE, type);
		
		Element inputNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
		inputNode.setName(CONTROL_INPUT);
		inputNode.setAttribute(null, ATTRIBUTE_BIND, name);
		
		labelNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
		labelNode.setName(NODE_LABEL);
		inputNode.addChild(Element.ELEMENT, labelNode);
						
		addControl(bodyNode,inputNode);
		return labelNode;
	}
	
	/**
	 * Parses a sequence value node and builds the corresponding xforms UI control.
	 * 
	 * @param name - the name of the complex type node whose sequence we are parsing.
	 * @param node - the sequence value node.
	 * @param labelNode - the label node to build.
	 * @param bodyNode - the body node.
	 * @param bindingNode - the binding node.
	 * @return the created label node.
	 */
	private static Element parseSequenceValueNode(String name,Element node, Element labelNode, Element bodyNode,Element bindingNode){
		//return 
		String type = node.getAttributeValue(null, ATTRIBUTE_TYPE);
		if(type != null)
			labelNode = buildSequenceInputControlNode(name,node,type,labelNode,bindingNode,bodyNode);
		else{
			for(int j=0; j<node.getChildCount(); j++){
				if(node.isText(j))
					continue;
				
				Element simpleTypeNode = (Element)node.getChild(j);
				if(!simpleTypeNode.getName().equalsIgnoreCase(NODE_SIMPLETYPE))
					continue;
				
				return parseSimpleType(name,simpleTypeNode,bodyNode,bindingNode);
			}
		}
		
		return labelNode;
	}
	
	/**
	 * Checks if the data type has a namespace prefix, if it does not, it prepends it.
	 * 
	 * @param type - the data type.
	 */
	private static String getPrefixedDataType(String type){
		if(type == null)
			return null;
		
		if(type.indexOf(NAMESPACE_PREFIX_SEPARATOR) == -1)
			type = PREFIX_OPENMRS+NAMESPACE_PREFIX_SEPARATOR + type;
		else
			type = PREFIX_XML_SCHEMA+NAMESPACE_PREFIX_SEPARATOR + type.substring(type.indexOf(NAMESPACE_PREFIX_SEPARATOR)+1);
			//type = type.substring(type.indexOf(NAMESPACE_PREFIX_SEPARATOR)+1);
			
		return type;
	}
	
	/**
	 * Parses a simple type node in an openmrs schema document.
	 * 
	 * @param name
	 * @param simpleTypeNode
	 * @param bodyNode
	 * @param bindingNode
	 * @return
	 */
	private static Element parseSimpleType(String name,Element simpleTypeNode,Element bodyNode,Element bindingNode){
		for(int i=0; i<simpleTypeNode.getChildCount(); i++){
			if(simpleTypeNode.isText(i))
				continue; //ignore text.
			
			Element child = (Element)simpleTypeNode.getElement(i);
			if(child.getName().equalsIgnoreCase(NODE_RESTRICTION))
				return parseRestriction(name,(Element)simpleTypeNode.getParent(),child,bodyNode,bindingNode);
		}
		
		return null;
	}
	
	/**
	 * Gets the node having the concept name and id combination for a multiple select item node.
	 * Such a node would look like: 
	 * xs:attribute name="openmrs_concept" type="xs:string" use="required" fixed="215^JAUNDICE^99DCT"
	 * 
	 * @param node - the multiple select item node.
	 * @return the concept node.
	 */
	private static Element getMultiSelectItemConceptNode(Element node){
		Element retNode;
		for(int i=0; i<node.getChildCount(); i++){
			if(node.isText(i))
				continue; //ignore text.
			
			Element child = (Element)node.getChild(i);
			if(child.getName().equalsIgnoreCase(NODE_ATTRIBUTE))
				return child;
			
			retNode = getMultiSelectItemConceptNode(child);
			if(retNode != null)
				return retNode;
		}
		
		return null;
	}
	
	/**
	 * Parses a multi select node and builds its corresponding items.
	 * An example of such a node would be: xs:element name="jaundice" default="false" nillable="true"
	 * for a complex type whose name is eye_exam_findings_type
	 * 
	 * @param name - the name of the complex type node we are dealing with.
	 * @param itemName - the name attribute of the node we are dealing with.
	 * @param selectItemNode - the multiple select item node. 
	 * @param controlNode - the xform UI control
	 * @param bodyNode - the body node.
	 * @param labelNode - the label node.
	 * @param bindingNode - the binding node.
	 * @return the label node we have created.
	 */
	private static Element parseMultiSelectNode(String name,String itemName, Element selectItemNode,Element controlNode,Element bodyNode, Element labelNode, Element bindingNode){		
		
		//If this is the first time we are looping through, create the input control.
		//Otherwise just add the items one by one as we get called for each.
		if(controlNode.getChildCount() == 0){
			//controlNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
			controlNode.setName(CONTROL_SELECT);
			controlNode.setAttribute(null, ATTRIBUTE_BIND, name);
			controlNode.setAttribute(null, ATTRIBUTE_APPEARANCE, Context.getAdministrationService().getGlobalProperty("xforms.multiSelectAppearance"));
			
			labelNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
			labelNode.setName(NODE_LABEL);
			controlNode.addChild(Element.ELEMENT, labelNode);
			
			addControl(bodyNode,controlNode); //bodyNode.addChild(Element.ELEMENT, controlNode);
			
			bindingNode.setAttribute(null, ATTRIBUTE_TYPE, DATA_TYPE_TEXT);
		}
		
		buildMultipleSelectItemNode(itemName,selectItemNode,controlNode);
		
		return labelNode;

	}
	
	/**
	 * Builds a multiple select item node.
	 * 
	 * @param itemName - the name attribute of the multiple select item whose item node we are building.
	 * @param selectItemNode - the xml schema select item node.
	 * @param controlNode - the xforms control whose item we are building.
	 */
	private static void buildMultipleSelectItemNode(String itemName, Element selectItemNode,Element controlNode){
		Element node  = getMultiSelectItemConceptNode(selectItemNode);
		String value = node.getAttributeValue(null, ATTRIBUTE_FIXED);
		String label = getConceptName(value);
		
		Element itemLabelNode = /*bodyNode*/controlNode.createElement(NAMESPACE_XFORMS, null);
		itemLabelNode.setName(NODE_LABEL);	
		itemLabelNode.addChild(Element.TEXT, label);
		
		Element itemValNode = /*bodyNode*/controlNode.createElement(NAMESPACE_XFORMS, null);
		itemValNode.setName(NODE_VALUE);	
		itemValNode.addChild(Element.TEXT, itemName /*value*/ /*binding*/);
		
		Element itemNode = /*bodyNode*/controlNode.createElement(NAMESPACE_XFORMS, null);
		itemNode.setName(NODE_ITEM);
		itemNode.addChild(Element.ELEMENT, itemLabelNode);
		itemNode.addChild(Element.ELEMENT, itemValNode);
		
		controlNode.addChild(Element.ELEMENT, itemNode);
	}
	
	/**
	 * Adds a UI control to the document body.
	 * 
	 * @param bodyNode - the body node.
	 * @param controlNode - the UI control.
	 */
	private static void addControl(Element bodyNode, Element controlNode){
		Element tableNode = null;
		if(bodyNode.getChildCount() == 0){
			tableNode = bodyNode.createElement(NAMESPACE_XHTML, null);
			tableNode.setName(HTML_TAG_TABLE);
			tableNode.setAttribute(null, HTML_ATTRIBUTE_CELLSPACING, HTML_ATTRIBUTE_CELLSPACING_VALUE);
			//tableNode.setAttribute(null, HTML_ATTRIBUTE_CELLPADDING, HTML_ATTRIBUTE_CELLPADDING_VALUE);
			bodyNode.addChild(Element.ELEMENT, tableNode);
						
			Element paragraphNode = bodyNode.createElement(NAMESPACE_XHTML, null);
			paragraphNode.setName(HTML_TAG_PARAGRAPH);
			bodyNode.addChild(Element.ELEMENT, paragraphNode);
			
			Element submitNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
			submitNode.setName(CONTROL_SUBMIT);
			submitNode.setAttribute(null, ATTRIBUTE_SUBMISSION, SUBMIT_ID);
			paragraphNode.addChild(Element.ELEMENT, submitNode);
			
			Element labelNode = submitNode.createElement(NAMESPACE_XFORMS, null);
			labelNode.setName(NODE_LABEL);
			labelNode.addChild(Element.TEXT, SUBMIT_LABEL);
			submitNode.addChild(Element.ELEMENT, labelNode);
		}
		else
			tableNode = bodyNode.getElement(0);
		
		boolean newRowCreated = false;
		if(singleColumnLayout || (!singleColumnLayout && currentRow == null)){
			currentRow = tableNode.createElement(NAMESPACE_XHTML, null);
			currentRow.setName(HTML_TAG_TABLE_ROW);
			tableNode.addChild(Element.ELEMENT, currentRow);
			newRowCreated = true;
		}
		
		Element cell = currentRow.createElement(NAMESPACE_XHTML, null);
		cell.setName(HTML_TAG_TABLE_CELL);
		currentRow.addChild(Element.ELEMENT, cell);
		
		cell.addChild(Element.ELEMENT, controlNode);
		
		if(!newRowCreated)
			currentRow = null;
	}
	
	/**
	 * Parses a restriction which has the enumeration nodes.
	 * Such a node would look like: xs:restriction base="xs:string"
	 * It also sets the data type which is normally the base attribute.
	 * 
	 * @param name - the name of the complex type question node whose restriction we are parsing.
	 * @param valueNode - the node who name attribute is equal to value.
	 * @param restrictionNode - the restriction node.
	 * @param bodyNode - the xform body node.
	 * @param bindingNode - the binding node.
	 * @return the label node of the created control.
	 */
	private static Element parseRestriction(String name, Element valueNode,Element restrictionNode,Element bodyNode, Element bindingNode){
		//the base attribute of a restriction has the data type for this question.
		String type = restrictionNode.getAttributeValue(null, ATTRIBUTE_BASE);
		type = getPrefixedDataType(type);
		bindingNode.setAttribute(null, ATTRIBUTE_TYPE, type);
		
		String controlName = CONTROL_SELECT;
		String maxOccurs = valueNode.getAttributeValue(null, ATTRIBUTE_MAXOCCURS);
		if(maxOccurs != null && maxOccurs.equalsIgnoreCase("1"))
			controlName = CONTROL_SELECT1;
		
		Element controlNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
		controlNode.setName(controlName);
		controlNode.setAttribute(null, ATTRIBUTE_BIND, name);
		controlNode.setAttribute(null, ATTRIBUTE_APPEARANCE, Context.getAdministrationService().getGlobalProperty(XformConstants.GLOBAL_PROP_KEY_SINGLE_SELECT_APPEARANCE));
		addControl(bodyNode,controlNode); 
		
		Element labelNode = bodyNode.createElement(NAMESPACE_XFORMS, null);
		labelNode.setName(NODE_LABEL);
		controlNode.addChild(Element.ELEMENT, labelNode);
	
		addRestrictionEnumerations(restrictionNode,controlNode);
		
		return labelNode;
	}
	
	/**
	 * Adds enumerations items for a restriction node to an xform control.
	 * Such a node is a select or select1 kind, and the enumerations become
	 * the xform items.
	 * 
	 * @param restrictionNode - the restriction node.
	 * @param controlNode - the control node to add the enumerations to.
	 */
	private static void addRestrictionEnumerations(Element restrictionNode, Element controlNode){
		Element itemValNode = null;
		Element itemLabelNode = null;
		for(int i=0; i<restrictionNode.getChildCount(); i++){
			//element nodes have the values. e.g. <xs:enumeration value="1360^RE TREATMENT^99DCT" /> 
			if(restrictionNode.getType(i) == Element.ELEMENT){
				Element child = restrictionNode.getElement(i);
				if(child.getName().equalsIgnoreCase(NODE_ENUMERATION))
				{
					itemValNode = /*bodyNode*/controlNode.createElement(NAMESPACE_XFORMS, null);
					itemValNode.setName(NODE_VALUE);	
					itemValNode.addChild(Element.TEXT, child.getAttributeValue(null, NODE_VALUE));
				}
			}
			
			//Comments have the labels. e.g. <!--  RE TREATMENT --> 
			if(restrictionNode.getType(i) == Element.COMMENT){
				itemLabelNode = /*bodyNode*/controlNode.createElement(NAMESPACE_XFORMS, null);
				itemLabelNode.setName(NODE_LABEL);	
				itemLabelNode.addChild(Element.TEXT, restrictionNode.getChild(i));
			}
			
			//Check if both the labal and value are set. First loop sets value and second label.
			if(itemLabelNode != null && itemValNode != null){
				Element itemNode = /*bodyNode*/controlNode.createElement(NAMESPACE_XFORMS, null);
				itemNode.setName(NODE_ITEM);
				controlNode.addChild(Element.ELEMENT, itemNode);

				itemNode.addChild(Element.ELEMENT, itemLabelNode);
				itemNode.addChild(Element.ELEMENT, itemValNode);
				itemLabelNode = null;
				itemValNode = null;
			}
		}
	}
	
	/**
	 * Check if a given node as an openmrs value node.
	 * 
	 * @param node - the node to check.
	 * @return - true if it has, else false.
	 */
	private static boolean hasValueNode(Element node){
		for(int i=0; i<node.getChildCount(); i++){
			if(node.isText(i))
				continue;
			
			Element child = node.getElement(i);
			if(child.getName().equalsIgnoreCase(NODE_VALUE))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Gets the value of the nodeset attribute, for a given node, used for xform bindings.
	 * 
	 * @param node - the node.
	 * @return - the value of the nodeset attribite.
	 */
	private static String getNodesetAttValue(Element node){
		if(hasValueNode(node))
			return getNodePath(node) + "/value";
		else if(isMultSelectNode(node))
			return getNodePath(node) + "/xforms_value";
		else
			return getNodePath(node);
	}
	
	/**
	 * Gets the path of a node from the instance node.
	 * 
	 * @param node - the node whose path to get.
	 * @return - the complete path from the instance node.
	 */
	public static String getNodePath(Element node){
		String path = node.getName();
		Element parent = (Element)node.getParent();
		while(parent != null && !parent.getName().equalsIgnoreCase(NODE_INSTANCE)){
			path = parent.getName() + NODE_SEPARATOR + path;
			if(parent.getParent() != null && parent.getParent() instanceof Element)
				parent = (Element)parent.getParent();
			else
				parent = null;
		}
		return NODE_SEPARATOR + path;
	}
	
	/**
	 * Converts an xml document to a string.
	 * 
	 * @param doc - the document.
	 * @return the xml string in in the document.
	 */
	public static String fromDoc2String(Document doc){
		KXmlSerializer serializer = new KXmlSerializer();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		try{
			serializer.setOutput(dos,null);
			doc.write(serializer);
			serializer.flush();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		byte[] byteArr = bos.toByteArray();
		char[]charArray = new char[byteArr.length];
		for(int i=0; i<byteArr.length; i++)
			charArray[i] = (char)byteArr[i];
		
		return String.valueOf(charArray);
	}
	
	/**
	 * Gets a document from a stream reader.
	 * 
	 * @param reader - the reader.
	 * @return the document.
	 */
	public static Document getDocument(Reader reader){
		Document doc = new Document();
		
		try{
			KXmlParser parser = new KXmlParser();
			parser.setInput(reader);
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			
			doc.parse(parser);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return doc;
	}
	
	/**
	 * Sets the values of patient table fields in an xform.
	 * These are the ones with the attributes: openmrs_table and openmrs_attribute
	 * e.g. <patient_unique_number openmrs_table="PATIENT_IDENTIFIER" openmrs_attribute="IDENTIFIER" /> 
	 * 
	 * @param parentNode - the root node of the xform.
	 * @param patientId - the patient id.
	 * @param xformsService - the xforms service.
	 */
	public static void setPatientTableFieldValues(Element parentNode, Integer patientId, XformsService xformsService){
		int numOfEntries = parentNode.getChildCount();
		for (int i = 0; i < numOfEntries; i++) {
			if (parentNode.getType(i) != Element.ELEMENT)
				continue;
			
			Element child = (Element)parentNode.getChild(i);
			String tableName = child.getAttributeValue(null, ATTRIBUTE_OPENMRS_TABLE);
			String columnName = child.getAttributeValue(null, ATTRIBUTE_OPENMRS_ATTRIBUTE);
			if(tableName != null && columnName != null && isUserDefinedNode(child.getName())){
				Object value  = getPatientValue(xformsService,patientId,tableName,columnName);
				if(value != null)
					XformBuilder.setNodeValue(child, value.toString());
			}
			
			setPatientTableFieldValues(child, patientId, xformsService);
		}
	}
	
	/**
	 * Gets the value of a patient database table field.
	 * 
	 * @param xformsService - the xforms service.
	 * @param patientId - the patient id
	 * @param tableName - the name of the table.
	 * @param columnName - the name of column in the table.
	 * @return - the value
	 */
	private static Object getPatientValue(XformsService xformsService, Integer patientId, String tableName, String columnName){
		
		Object value = null;
		
		try{
			value = xformsService.getPatientValue(patientId, tableName, columnName);
		}
		catch(Exception e){
			System.out.println("No column called: "+columnName + " in table: " + tableName);
		}
		
		return value;
	}
	
	/**
	 * Checks if a node is a user defined one. That is not one of the standard openmrs nodes.
	 * 
	 * @param name - the name of the node.
	 * @return - true if it is a user define one, else false.
	 */
	public static boolean isUserDefinedNode(String name){
		return !(name.equalsIgnoreCase(NODE_ENCOUNTER_ENCOUNTER_DATETIME)||
				name.equalsIgnoreCase(NODE_ENCOUNTER_LOCATION_ID)||
				name.equalsIgnoreCase(NODE_ENCOUNTER_PROVIDER_ID)||
				name.equalsIgnoreCase(NODE_PATIENT_MIDDLE_NAME)||
				name.equalsIgnoreCase(NODE_PATIENT_GIVEN_NAME)||
				name.equalsIgnoreCase(NODE_PATIENT_PATIENT_ID)||
				name.equalsIgnoreCase(NODE_PATIENT_FAMILY_NAME));
	}
	
	/**
	 * Checks if a schema node is a user defined one.
	 * 
	 * @param node - the node to check.
	 * @return - true if it is a user defined one, else false.
	 */
	private static boolean isUserDefinedSchemaElement(Element node){
		
		if(!(node.getName().equalsIgnoreCase(NODE_SIMPLETYPE) || node.getName().equalsIgnoreCase(NODE_COMPLEXTYPE)))
			return false;
		
		String name = node.getAttributeValue(null, ATTRIBUTE_NAME);
		
		return !(name.equalsIgnoreCase("form") ||
				name.equalsIgnoreCase("_header_section") ||
				name.equalsIgnoreCase("_other_section") ||
				name.equalsIgnoreCase("_requiredString") ||
				name.equalsIgnoreCase("_infopath_boolean") ||
				name.equalsIgnoreCase("encounter_section") ||
				name.equalsIgnoreCase("obs_section") ||
				name.equalsIgnoreCase("patient_section"));
	}
	
	/**
	 * Builds an xform for creating a new patient.
	 * 
	 * @param xformAction - the url to post the xform data to.
	 * @return - the xml of the new patient xform.
	 */
	public static String getNewPatientXform(String xformAction){
		XformBuilder.xformAction = xformAction;
			
		Document doc = new Document();
		doc.setEncoding(XformConstants.DEFAULT_CHARACTER_ENCODING);
		Element htmlNode = doc.createElement(NAMESPACE_XHTML, null);
		htmlNode.setName(NODE_HTML);
		htmlNode.setPrefix(null, NAMESPACE_XHTML);
		htmlNode.setPrefix(PREFIX_XFORMS, NAMESPACE_XFORMS);
		htmlNode.setPrefix(PREFIX_XML_SCHEMA, NAMESPACE_XML_SCHEMA);
		htmlNode.setPrefix(PREFIX_XML_SCHEMA2, NAMESPACE_XML_SCHEMA);
		htmlNode.setPrefix(PREFIX_XML_INSTANCES, NAMESPACE_XML_INSTANCE);
		doc.addChild(org.kxml2.kdom.Element.ELEMENT, htmlNode);
		
		Element headNode = doc.createElement(NAMESPACE_XHTML, null);
		headNode.setName(NODE_HEAD);
		htmlNode.addChild(org.kxml2.kdom.Element.ELEMENT, headNode);
		
		Element bodyNode = doc.createElement(NAMESPACE_XHTML, null);
		bodyNode.setName(NODE_BODY);
		htmlNode.addChild(org.kxml2.kdom.Element.ELEMENT, bodyNode);
		
		Element titleNode =  doc.createElement(NAMESPACE_XHTML, null);
		titleNode.setName(NODE_TITLE);
		titleNode.addChild(Element.TEXT,"Patient");
		headNode.addChild(Element.ELEMENT,titleNode);
		
		Element modelNode =  doc.createElement(NAMESPACE_XFORMS, null);
		modelNode.setName(NODE_MODEL);
		headNode.addChild(Element.ELEMENT,modelNode);
		
		Element instanceNode =  doc.createElement(NAMESPACE_XFORMS, null);
		instanceNode.setName(NODE_INSTANCE);
		modelNode.addChild(Element.ELEMENT,instanceNode);
		
		Element formNode =  doc.createElement(null, null);
		formNode.setName(NODE_PATIENT);
		formNode.setAttribute(null, ATTRIBUTE_ID, "0");
		formNode.setAttribute(null, ATTRIBUTE_DESCRIPTION_TEMPLATE,"${/patient/family_name}$ ${/patient/middle_name}$ ${/patient/given_name}$");
		
		instanceNode.addChild(Element.ELEMENT, formNode);
		
		Element submitNode =  doc.createElement(NAMESPACE_XFORMS, null);
		submitNode.setName(NODE_SUBMISSION);
		submitNode.setAttribute(null, ATTRIBUTE_ID, SUBMIT_ID);
		submitNode.setAttribute(null, ATTRIBUTE_ACTION, xformAction);
		submitNode.setAttribute(null, ATTRIBUTE_METHOD, SUBMISSION_METHOD);
		modelNode.addChild(Element.ELEMENT,submitNode);
		
		addPatientNode(formNode,modelNode,bodyNode,NODE_FAMILY_NAME,DATA_TYPE_TEXT,"Family Name","The patient family name",true,false,CONTROL_INPUT,null,null);
		addPatientNode(formNode,modelNode,bodyNode,NODE_MIDDLE_NAME,DATA_TYPE_TEXT,"Middle Name","The patient middle name",false,false,CONTROL_INPUT,null,null);
		addPatientNode(formNode,modelNode,bodyNode,NODE_GIVEN_NAME,DATA_TYPE_TEXT,"Given Name","The patient given name",false,false,CONTROL_INPUT,null,null);
		addPatientNode(formNode,modelNode,bodyNode,NODE_BIRTH_DATE,DATA_TYPE_DATE,"Birth Date","The patient birth date",false,false,CONTROL_INPUT,null,null);
		addPatientNode(formNode,modelNode,bodyNode,NODE_IDENTIFIER,DATA_TYPE_TEXT,"Identifier","The patient identifier",true,false,CONTROL_INPUT,null,null);
		addPatientNode(formNode,modelNode,bodyNode,NODE_PATIENT_ID,DATA_TYPE_INT,"Patient ID","The patient ID",false,true,CONTROL_INPUT,null,null);
		
		addPatientNode(formNode,modelNode,bodyNode,NODE_GENDER,DATA_TYPE_TEXT,"Gender","The patient's sex",false,false,CONTROL_SELECT1,new String[]{"Male","Female"},new String[]{"M","F"});
		
		String[] items, itemValues; int i=0;
		List<Location> locations = Context.getEncounterService().getLocations();
		if(locations != null){
			items = new String[locations.size()];
			itemValues = new String[locations.size()];
			for(Location loc : locations){
				items[i] = loc.getName();
				itemValues[i++] = loc.getLocationId().toString();
			}
			addPatientNode(formNode,modelNode,bodyNode,NODE_LOCATION_ID,DATA_TYPE_INT,"Location","The patient's location",true,false,CONTROL_SELECT1,items,itemValues);
		}
		
		List<PatientIdentifierType> identifierTypes = Context.getPatientService().getPatientIdentifierTypes();
		if(identifierTypes != null){
			i=0;
			items = new String[identifierTypes.size()];
			itemValues = new String[identifierTypes.size()];
			for(PatientIdentifierType identifierType : identifierTypes){
				items[i] = identifierType.getName();
				itemValues[i++] = identifierType.getPatientIdentifierTypeId().toString();
			}
			addPatientNode(formNode,modelNode,bodyNode,"patient_identifier_type_id",DATA_TYPE_INT,"Identifier Type","The patient's identifier type",true,false,CONTROL_SELECT1,items,itemValues);
		}
		
		return XformBuilder.fromDoc2String(doc);
	}
	
	/**
	 * Adds a node to a new patient xform.
	 * 
	 * @param formNode
	 * @param modelNode
	 * @param bodyNode
	 * @param name
	 * @param type
	 * @param label
	 * @param hint
	 * @param required
	 * @param readonly
	 * @param controlType
	 * @param items
	 * @param itemValues
	 */
	private static void addPatientNode(Element formNode,Element modelNode,Element bodyNode,String name,String type,String label, String hint,boolean required, boolean readonly, String controlType, String[] items, String[] itemValues){
		//add the model node
		Element element = formNode.createElement(null, null);
		element.setName(name);
		formNode.addChild(Element.ELEMENT, element);
		
		//add the model binding
		element = modelNode.createElement(NAMESPACE_XFORMS, null);
		element.setName(NODE_BIND);
		element.setAttribute(null, ATTRIBUTE_ID, name);
		element.setAttribute(null, ATTRIBUTE_NODESET, "/"+NODE_PATIENT+"/"+name);
		element.setAttribute(null, ATTRIBUTE_TYPE, type);
		if(readonly)
			element.setAttribute(null, ATTRIBUTE_READONLY, XPATH_VALUE_TRUE);
		if(required)
			element.setAttribute(null, ATTRIBUTE_REQUIRED, XPATH_VALUE_TRUE);
		modelNode.addChild(Element.ELEMENT, element);
		
		//add the control
		element = bodyNode.createElement(NAMESPACE_XFORMS, null);
		element.setName(controlType);
		element.setAttribute(null, ATTRIBUTE_BIND, name);
		bodyNode.addChild(Element.ELEMENT, element);
		
		//add the label
		Element child = element.createElement(NAMESPACE_XFORMS, null);
		child.setName(NODE_LABEL);
		child.addChild(Element.TEXT, label);
		element.addChild(Element.ELEMENT, child);
		
		//add the hint
		child = element.createElement(NAMESPACE_XFORMS, null);
		child.setName(NODE_HINT);
		child.addChild(Element.TEXT, hint);
		element.addChild(Element.ELEMENT, child);
		
		//add control items
		if(items != null){
			for(int i=0; i<items.length; i++){
				child = element.createElement(NAMESPACE_XFORMS, null);
				child.setName(NODE_ITEM);
				element.addChild(Element.ELEMENT, child);
				
				Element elem = element.createElement(NAMESPACE_XFORMS, null);
				elem.setName(NODE_LABEL);
				elem.addChild(Element.TEXT, items[i]);
				child.addChild(Element.ELEMENT, elem);
				
				elem = element.createElement(NAMESPACE_XFORMS, null);
				elem.setName(NODE_VALUE);
				elem.addChild(Element.TEXT, itemValues[i]);
				child.addChild(Element.ELEMENT, elem);
			}
		}
	}
}