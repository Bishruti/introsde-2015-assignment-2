package ehealth.resources;

import ehealth.model.HealthMeasureHistory;
import ehealth.model.HealthProfile;
import ehealth.model.Person;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Stateless // Only used if the the application is deployed in a Java EE container
@LocalBean // Only used if the the application is deployed in a Java EE container
public class HealthMeasureHistoryResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int id;
    String measuretype;

    public HealthMeasureHistoryResource(UriInfo uriInfo, Request request,int id, String measuretype) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.measuretype = measuretype;
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<HealthMeasureHistory> getPersonHealthMeasureHistory() {
        List<HealthMeasureHistory> healthMeasureHistory = HealthMeasureHistory.getDetailsByPerson(id, measuretype);
        if (healthMeasureHistory == null)
            throw new RuntimeException("Get: Person with " + id + " not found");
        return healthMeasureHistory;
    }

    // For the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public HealthMeasureHistory getHealthMeasureHistoryHTML() {
        HealthMeasureHistory healthMeasureHistory = this.getHealthMeasureHistoryById(id);
        if (healthMeasureHistory == null)
            throw new RuntimeException("Get: Health Measure History with " + id + " not found");
        System.out.println("Returning Health Measure History... " + healthMeasureHistory.getMid());
        return healthMeasureHistory;
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    public HealthMeasureHistory newHealthMeasureHistory(HealthMeasureHistory healthMeasureHistory) throws IOException {
        System.out.println("Creating new Health Measure History...");
        Person p = Person.getPersonById(id);
        healthMeasureHistory.setPerson(p);
        /*List<HealthProfile> list =  HealthProfile.getHealthProfileByPerson(id);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.remove(i);
            }
        }*/
        HealthProfile hp = new HealthProfile();
        hp.setMeasure(measuretype);
        hp.setValue(healthMeasureHistory.getValue());
        hp.setPerson(p);
        HealthProfile.saveHealthProfile(hp);
        healthMeasureHistory.setMeasure(measuretype);
        healthMeasureHistory.setCreated(new Date());
        return HealthMeasureHistory.saveHealthMeasureHistory(healthMeasureHistory);
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putHealthMeasureHistory(HealthMeasureHistory healthMeasureHistory) {
        System.out.println("--> Updating Health Measure History... " +this.id);
        System.out.println("--> "+healthMeasureHistory.toString());
        Response res;
        HealthMeasureHistory existing = getHealthMeasureHistoryById(this.id);

        if (existing == null) {
            res = Response.noContent().build();
        } else {
            res = Response.created(uriInfo.getAbsolutePath()).build();
            healthMeasureHistory.setMid(this.id);
            HealthMeasureHistory.updateHealthMeasureHistory(healthMeasureHistory);
        }
        return res;
    }

    @DELETE
    public void deleteHealthMeasureHistory() {
        HealthMeasureHistory c = getHealthMeasureHistoryById(id);
        if (c == null)
            throw new RuntimeException("Delete: Health Measure History with " + id + " not found");
        HealthMeasureHistory.removeHealthMeasureHistory(c);
    }

    public HealthMeasureHistory getHealthMeasureHistoryById(int mid) {
        System.out.println("Reading Health Measure History from DB with id: "+mid);
        HealthMeasureHistory healthMeasureHistory = HealthMeasureHistory.getHealthMeasureHistoryById(mid);
        System.out.println("Health Measure History: "+healthMeasureHistory.toString());
        return healthMeasureHistory;
    }

    @Path("/{mid}")
    public HealthMeasureHistoryDetailResource getPersonHealthMeasureHistoryById(@PathParam("personId") int id, @PathParam("measuretype") String measuretype, @PathParam("mid") int mid) {
        return new HealthMeasureHistoryDetailResource(uriInfo, request, id, measuretype, mid);
    }
}