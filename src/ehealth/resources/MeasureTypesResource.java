package ehealth.resources;

import ehealth.model.HealthMeasureHistory;
import ehealth.model.MeasureTypes;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless // Works only inside a Java EE application
@LocalBean // Works only inside a Java EE application
@Path("/measureTypes")
public class MeasureTypesResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // will work only inside a Java EE application
    @PersistenceUnit(unitName="introsde-jpa")
    EntityManager entityManager;

    // will work only inside a Java EE application
    @PersistenceContext(unitName = "introsde-jpa",type= PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;

    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public MeasureTypes getMeasureTypes() {
        System.out.println("Getting list of Measure Type...");
        MeasureTypes measureTypes = new MeasureTypes();
        measureTypes.setMeasureType(HealthMeasureHistory.getTypes());
        return measureTypes;
    }
}