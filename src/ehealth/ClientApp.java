package ehealth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;

public class ClientApp {
    ClientApp client = new ClientApp();

	public static void main(String[] args) throws IOException {
        File logXml = new File("././client-server-xml.log");
        FileWriter fileWriterXml = new FileWriter(logXml.getAbsoluteFile());
        BufferedWriter bufferedWriterXml = new BufferedWriter(fileWriterXml );

        File logJson = new File("././client-server-json.log");
        FileWriter fileWriterJson = new FileWriter(logJson.getAbsoluteFile());
        BufferedWriter bufferedWriterJson = new BufferedWriter(fileWriterJson );

		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		WebTarget service = client.target(getBaseURI());
		Response response;
        int first_person_id;
        int last_person_id;
        int new_person_id_xml;
        int new_person_id_json;

		/* Request 1
        GET request to obtain all the people in the list.
        Expected Input: -
        Expected Output: List of people in Xml Format. (String) */

        printAndSaveInLog(bufferedWriterXml, "Request #1: GET /person Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
        response = service.path("person").request().accept(MediaType.APPLICATION_XML).get();
        String peopleXml = response.readEntity(String.class);
        Document doc = convertStringToXmlDocument(peopleXml);
        NodeList nodes = doc.getElementsByTagName("idPerson");
        int elementCount = nodes.getLength();
        first_person_id = Integer.parseInt(doc.getElementsByTagName("idPerson").item(0).getTextContent());
        last_person_id = Integer.parseInt(doc.getElementsByTagName("idPerson").item(elementCount-1).getTextContent());

        printAndSaveInLog(bufferedWriterXml, "First Person Id: " + first_person_id);
        printAndSaveInLog(bufferedWriterXml, "Last Person Id: " + last_person_id);

        if (elementCount > 2) {
            printResponseAndSaveInLog(bufferedWriterXml, response);
            printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(peopleXml));
        }
        else {
            printAndSaveInLog(bufferedWriterXml, "ERROR");
        }

        /* Request 1
        GET request to obtain all the people in the list.
        Expected Input: -
        Expected Output: List of people in JSON Format. (String) */

        printAndSaveInLog(bufferedWriterJson, "Request #1: GET /person Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
		response = service.path("person").request().accept(MediaType.APPLICATION_JSON).get();
		String peopleJson = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterJson, response);
        JSONArray jsonArray = new JSONArray(peopleJson);

        printAndSaveInLog(bufferedWriterJson, "First Person Id: " + first_person_id);
        printAndSaveInLog(bufferedWriterJson, "Last Person Id: " + last_person_id);

        int peopleCount = jsonArray.length();
        jsonArray.getJSONObject(0).getInt("idPerson");
        if (peopleCount > 2) {
            printAndSaveInLog(bufferedWriterJson, beautifyJsonOutput(peopleJson));
        }
        else {
            printAndSaveInLog(bufferedWriterJson, "ERROR");
        }

        /* Request 2
        GET request to obtain a person from the list.
        Expected Input: PersonId (Integer)
        Expected Output: Person with their attributes in XML Format. (String) */

        printAndSaveInLog(bufferedWriterXml, "Request #2: GET /person/" + first_person_id + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
        response = service.path("person/" + first_person_id).request().accept(MediaType.APPLICATION_XML).get();
        String personXml = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterXml, response);
        printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(personXml));

        /* Request 2
        GET request to obtain a person from the list.
        Expected Input: PersonId (Integer)
        Expected Output: Person with their attributes in JSON Format. (String) */

        printAndSaveInLog(bufferedWriterJson, "Request #2: GET /person/" + first_person_id + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
		response = service.path("person/" + first_person_id).request().accept(MediaType.APPLICATION_JSON).get();
		String personJson = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterJson, response);
        printAndSaveInLog(bufferedWriterJson, beautifyJsonOutput(personJson));


         /* Request 3
        PUT request to edit a person in the list.
        Expected Input: PersonId (Integer)
        Expected Output: Edited Person with their attributes in XML Format. (String) */

        printAndSaveInLog(bufferedWriterXml, "Request #3: PUT /person/" + first_person_id + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
        String inputPutPersonXml = "<person><firstname>Bilbo</firstname></person>";
        response = service.path(("person/" + first_person_id)).request().accept(MediaType.APPLICATION_XML).put(Entity.xml(inputPutPersonXml));
        response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterXml, response);
        String updatedPersonXml = service.path(("person/" + first_person_id)).request().accept(MediaType.APPLICATION_XML).get().readEntity(String.class);
        printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(updatedPersonXml));

        /* Request 3
        PUT request to edit a person in the list.
        Expected Input: PersonId (Integer)
        Expected Output: Edited Person with their attributes in JSON Format. (String) */

        printAndSaveInLog(bufferedWriterJson, "Request #3: PUT /person/" + first_person_id + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
        String inputPutPersonJson = "{\"firstname\":\"Frodo\"}";
        response = service.path("person/"+first_person_id).request().accept(MediaType.APPLICATION_JSON).put(Entity.json(inputPutPersonJson));
        response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterJson, response);
        String updatedPersonJson = service.path("person/" + first_person_id).request().accept(MediaType.APPLICATION_JSON).get().readEntity(String.class);
        printAndSaveInLog(bufferedWriterJson, beautifyJsonOutput(updatedPersonJson));

		/* Request 4
        POST request to add a new person in the list.
        Expected Input: -
        Expected Output: Newly created Person with their attributes in XML Format. (String) */

        printAndSaveInLog(bufferedWriterXml, "Request #4: POST /person Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
        String inputPostPersonXml = "<person>\n"+
                "<firstname>Chuck</firstname>\n"+
                "<lastname>Norris</lastname>\n"+
                "<birthdate>1945-01-01T00:00:00+02:00</birthdate>\n"+
                "<healthProfile>\n"+
                "   <measureType>\n"+
                "       <measure>height</measure>\n"+
                "       <value>172</value>\n"+
                "   </measureType>\n"+
                "   <measureType>\n"+
                "       <measure>weight</measure>\n"+
                "       <value>78.9</value>\n"+
                "   </measureType>\n"+
                "</healthProfile>\n"+
                "</person>";
        response = service.path("person").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(inputPostPersonXml));
        String postPersonXml = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterXml, response);
        printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(postPersonXml));
        Document personDoc = convertStringToXmlDocument(postPersonXml);
        new_person_id_xml = Integer.parseInt(personDoc.getElementsByTagName("idPerson").item(0).getTextContent());;
        printAndSaveInLog(bufferedWriterXml, "New Person Id is: " + new_person_id_xml);

        /* Request 4
        POST request to add a new person in the list.
        Expected Input: -
        Expected Output: Newly created Person with their attributes in JSON Format. (String) */

        printAndSaveInLog(bufferedWriterJson, "Request #4: POST /person Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
		String inputPostPersonJson = "{\n" +
                                        "\"firstname\":\"Hermione\",\n" +
                                        "\"lastname\":\"Grainger\",\n " +
                                        "\"birthdate\":\"1990-09-02T00:00:00+02:00\",\n" +
                                        "\"measureType\":[\n" +
                                            "{\n" +
                                                "\"measure\":\"height\",\n" +
                                                "\"value\":\"170\"\n"+
                                            "},\n" +
                                            "{\n" +
                                                "\"measure\":\"weight\",\n" +
                                                "\"value\":\"55\"\n" +
                                            "}\n" +
                                        "]\n" +
                                    "}";

		response = service.path("person").request().accept(MediaType.APPLICATION_JSON).post(Entity.json(inputPostPersonJson));
		String postPersonJson = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterJson, response);
        printAndSaveInLog(bufferedWriterJson, beautifyJsonOutput(postPersonJson));
        JSONObject postJsonObject = new JSONObject(postPersonJson);
        new_person_id_json = (int) postJsonObject.get("idPerson");
        printAndSaveInLog(bufferedWriterJson, "New Person Id is: " + new_person_id_json);

        /* Request 5
        DELETE request to delete a person from the list in XML format.
        Expected Input: personId (Integer)
        Expected Output: Response Message and Response Code. */

        printAndSaveInLog(bufferedWriterXml, "Request #5: DELETE /person/" + new_person_id_xml + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
        response = service.path("person/" + new_person_id_xml).request().accept(MediaType.APPLICATION_XML).delete();
        response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterXml, response);
        printAndSaveInLog(bufferedWriterXml, "GET Request to check if the person is deleted.");
        Response getDeletedXml;
        getDeletedXml = service.path("person/" + new_person_id_xml).request().accept(MediaType.APPLICATION_XML).get();
        printResponseAndSaveInLog(bufferedWriterXml, getDeletedXml);

         /* Request 5
        DELETE request to delete a person from the list in JSON format.
        Expected Input: personId (Integer)
        Expected Output: Response Message and Response Code */

        printAndSaveInLog(bufferedWriterJson, "Request #5: DELETE /person/" + new_person_id_json + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
        response = service.path("person/" + new_person_id_json).request().accept(MediaType.APPLICATION_JSON).delete();
        response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterJson, response);
        printAndSaveInLog(bufferedWriterJson, "GET Request to check if the person is deleted.");
        Response getDeletedJson;
        getDeletedJson = service.path("person/" + new_person_id_json).request().accept(MediaType.APPLICATION_JSON).get();
        printResponseAndSaveInLog(bufferedWriterJson, getDeletedJson);

        /* Request 9
        GET request to obtain all measure types in the list.
        Expected Input: -
        Expected Output: List of measure types in XML Format.  (String) */
        printAndSaveInLog(bufferedWriterXml, "Request #9: GET /measureTypes Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
        response = service.path("measureTypes").request().accept(MediaType.APPLICATION_XML).get();
        String measureTypesXml = response.readEntity(String.class);

        Document measureDoc = convertStringToXmlDocument(peopleXml);
        NodeList measureNodes = measureDoc.getElementsByTagName("measureType");
        int measureCount = measureNodes.getLength();

        if (measureCount > 2) {
            printResponseAndSaveInLog(bufferedWriterXml, response);
            printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(measureTypesXml));
        }
        else {
            printAndSaveInLog(bufferedWriterXml, "ERROR");
        }

        /* Request 9
        GET request to obtain all measure types in the list.
        Expected Input: -
        Expected Output: List of measure types in JSON Format.  (String) */

        printAndSaveInLog(bufferedWriterJson, "Request #9: GET /measureTypes Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
        response = service.path("measureTypes").request().accept(MediaType.APPLICATION_JSON).get();
        String measureTypesJson = response.readEntity(String.class);
        JSONObject measureTypeObject = new JSONObject(measureTypesJson);
        JSONArray measure_types  = measureTypeObject.getJSONArray("measureType");
        if (measure_types.length() > 2) {
            printResponseAndSaveInLog(bufferedWriterJson, response);
            printAndSaveInLog(bufferedWriterJson, beautifyJsonOutput(measureTypesJson));
        }
        else {
            printAndSaveInLog(bufferedWriterJson, "ERROR");
        }


        /* Request 6
        GET request to obtain all measure details about a measure of a person in the list.
        Expected Input: personId (Integer)
                        measureType (String)
        Expected Output: List of measure types in XML and JSON Format. (String) */

        String measure =  "";
        int measureId = 0;
        for(int index = 0; index < measure_types.length(); index++) {
            measure = measure_types.getString(index);

            /*Yeilds the list of measure details of the first person in XML Format*/

            printAndSaveInLog(bufferedWriterXml, "Request #6: GET /person/" + first_person_id + "/" + measure + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
            response = service.path("person/" + first_person_id + "/" + measure).request().accept(MediaType.APPLICATION_XML).get();
            String measureFirstPersonXml = response.readEntity(String.class);
            printResponseAndSaveInLog(bufferedWriterXml, response);
            printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(measureFirstPersonXml));

            /*Yeilds the list of measure details of the last person in XML Format*/

            printAndSaveInLog(bufferedWriterXml, "Request #6: GET /person/" + last_person_id + "/" + measure + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
            response = service.path("person/" + last_person_id + "/" + measure).request().accept(MediaType.APPLICATION_XML).get();
            String measurePersonLastXml = response.readEntity(String.class);
            printResponseAndSaveInLog(bufferedWriterXml, response);
            printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(measurePersonLastXml));

            /*Yeilds the list of measure details of the first person in JSON Format*/

            printAndSaveInLog(bufferedWriterJson, "Request #6: GET /person/" + first_person_id + "/" + measure + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
            response = service.path("person/" + first_person_id + "/" + measure).request().accept(MediaType.APPLICATION_JSON).get();
            String measureFirstPersonJson = response.readEntity(String.class);
            printResponseAndSaveInLog(bufferedWriterJson, response);
            printAndSaveInLog(bufferedWriterJson,beautifyJsonOutput(measureFirstPersonJson));
            JSONObject measureTObject = new JSONObject(measureFirstPersonJson.substring(1,measureFirstPersonJson.length()-1));
            measureId  = measureTObject.getInt("mid");

            /*Yeilds the list of measure details of the last person in JSON Format*/

            printAndSaveInLog(bufferedWriterJson, "Request #6: GET /person/" + last_person_id + "/" + measure + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
            response = service.path("person/" + last_person_id + "/" + measure).request().accept(MediaType.APPLICATION_JSON).get();
            String measureLastPersonJson = response.readEntity(String.class);
            printResponseAndSaveInLog(bufferedWriterJson, response);
            printAndSaveInLog(bufferedWriterJson, beautifyJsonOutput(measureLastPersonJson));
            break;
        }

        /* Request 7
        GET request to obtain measure details about a particular measure of a person in the list.
        Expected Input: personId (Integer)
                        measureType (String)
                        measureId (Integer)
        Expected Output: List of measure types in XML Format. (String) */

        printAndSaveInLog(bufferedWriterXml, "The acquired measure type is: " + measure);
        printAndSaveInLog(bufferedWriterXml, "The acquired measure id is: " + measureId);

        printAndSaveInLog(bufferedWriterXml, "Request #7: GET /person/" + first_person_id + "/" + measure + "/" + measureId + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
        response = service.path("person/" + first_person_id + "/" + measure + "/" + measureId).request().accept(MediaType.APPLICATION_XML).get();
        String measureIdPersonXml = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterXml, response);
        printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(measureIdPersonXml));

        /* Request 7
        GET request to obtain measure details about a particular measure of a person in the list.
        Expected Input: personId (Integer)
                        measureType (String)
                        measureId (Integer)
        Expected Output: List of measure types in JSON Format. (String) */

        printAndSaveInLog(bufferedWriterJson, "The acquired measure type is: " + measure);
        printAndSaveInLog(bufferedWriterJson, "The acquired measure id is: " + measureId);

        printAndSaveInLog(bufferedWriterJson, "Request #7: GET /person/" + first_person_id + "/" + measure + "/" + measureId + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
        response = service.path("person/" + first_person_id + "/" + measure + "/" + measureId).request().accept(MediaType.APPLICATION_JSON).get();
        String measureIdPersonJson = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterJson, response);
        printAndSaveInLog(bufferedWriterJson, beautifyJsonOutput(measureIdPersonJson));

        /* Request 8
        POST request to create measure details about a measure of a person in the list.
        Expected Input: personId (Integer)
                        measureType (String)
        Expected Output: Count of Measure before the POST request. (Integer)
                         List of newly created measure in JSON Format. (String)
                         Count of Measure after the POST request. (Integer) */

        printAndSaveInLog(bufferedWriterJson, "Request #8: POST /person/" + first_person_id + "/" + measure + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON");
        Response getPersonMeasure1 = service.path("person/" + first_person_id + "/" + measure).request().accept(MediaType.APPLICATION_JSON).get();
        String personMeasure1 = getPersonMeasure1.readEntity(String.class);
        JSONArray personalMeasureArray1 = new JSONArray(personMeasure1);
        printAndSaveInLog(bufferedWriterJson, "The number of counts of " + measure + "  is:" + personalMeasureArray1.length());

        String inputMeasureValueJson = "{\"value\":\"100\"}";
        response = service.path("person/" + first_person_id + "/" + measure).request().accept(MediaType.APPLICATION_JSON).post(Entity.json(inputMeasureValueJson));
        String measureValuePersonJson = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterJson, response);
        printAndSaveInLog(bufferedWriterJson, beautifyJsonOutput(measureValuePersonJson));

        Response getPersonMeasure2 = service.path("person/" + first_person_id + "/" + measure).request().accept(MediaType.APPLICATION_JSON).get();
        String personMeasure2 = getPersonMeasure2.readEntity(String.class);
        JSONArray personalMeasureArray2 = new JSONArray(personMeasure2);
        printAndSaveInLog(bufferedWriterJson, "The number of counts of " + measure + " after post request is:" + personalMeasureArray2.length());

        /* Request 8
        POST request to create measure details about a measure of a person in the list.
        Expected Input: personId (Integer)
                        measureType (String)
        Expected Output: Count of Measure before the POST request. (Integer)
                         List of newly created measure in XML Format. (String)
                         Count of Measure after the POST request. (Integer) */

        printAndSaveInLog(bufferedWriterXml, "Request #8: POST /person/" + first_person_id + "/" + measure + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML");
        Response getPersonMeasure3 = service.path("person/" + first_person_id + "/" + measure).request().accept(MediaType.APPLICATION_XML).get();
        String personMeasure3 = getPersonMeasure3.readEntity(String.class);
        Document document1 = convertStringToXmlDocument(personMeasure3);
        NodeList measureNodes1 = document1.getElementsByTagName("mid");
        int measureCount1 = measureNodes1.getLength();
        printAndSaveInLog(bufferedWriterXml, "The number of counts of " + measure + "  is:" + measureCount1);

        String inputMeasureValueXml = "<measureHistory>\n" +
                                            "<value>102</value>\n" +
                                      "</measureHistory>";
        response = service.path("person/" + first_person_id + "/" + measure).request().accept(MediaType.APPLICATION_XML).post(Entity.xml(inputMeasureValueXml));
        String measureValuePersonXml = response.readEntity(String.class);
        printResponseAndSaveInLog(bufferedWriterXml, response);
        printAndSaveInLog(bufferedWriterXml, beautifyXmlOutput(measureValuePersonXml));

        Response getPersonMeasure4 = service.path("person/" + first_person_id + "/" + measure).request().accept(MediaType.APPLICATION_XML).get();
        String personMeasure4 = getPersonMeasure4.readEntity(String.class);
        Document document2 = convertStringToXmlDocument(personMeasure4);
        NodeList measureNodes2 = document2.getElementsByTagName("mid");
        int measureCount2 = measureNodes2.getLength();
        printAndSaveInLog(bufferedWriterXml, "The number of counts of " + measure + "  is:" + measureCount2);

        bufferedWriterXml.close();
        bufferedWriterJson.close();
    }

	private static URI getBaseURI() {
		return UriBuilder.fromUri(
				"http://127.0.1.1:3100/ehealth/").build();
	}

	private static void printResponseAndSaveInLog(BufferedWriter bufferwriter, Response response) throws IOException {
		System.out.println("Result: " + response.getStatusInfo());
        bufferwriter.write("Result: " + response.getStatusInfo() + "\n");
		System.out.println("HTTP Status: " + response.getStatus());
        bufferwriter.write("HTTP Status: " + response.getStatus() + "\n");
	}

    private static void printAndSaveInLog(BufferedWriter bufferwriter, String outputResponse) throws IOException {
        System.out.println(outputResponse);
        bufferwriter.write(outputResponse + "\n");
    }

    public static Document convertStringToXmlDocument(String peopleXml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse( new InputSource( new StringReader( peopleXml ) ) );
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String beautifyJsonOutput(String rawJsonInput) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Object json = mapper.readValue(rawJsonInput, Object.class);
        String beautifiedJsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        return beautifiedJsonOutput;
    }

    public static String beautifyXmlOutput(String xmlInput) {
        try {
            Source rawXmlInput = new StreamSource(new StringReader(xmlInput));
            StringWriter stringWriter = new StringWriter();
            StreamResult beautifiedXmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(rawXmlInput, beautifiedXmlOutput);
            return beautifiedXmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}