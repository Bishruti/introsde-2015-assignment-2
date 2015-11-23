package ehealth.resources;

import ehealth.model.HealthMeasureHistory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Stateless // Only used if the the application is deployed in a Java EE container
@LocalBean // Only used if the the application is deployed in a Java EE container
public class HealthMeasureHistoryDetailResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int id;
    int mid;
    String measuretype;

    public HealthMeasureHistoryDetailResource(UriInfo uriInfo, Request request,int id, String measuretype, int mid) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.measuretype = measuretype;
        this.mid = mid;
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<HealthMeasureHistory> getPersonHealthMeasureHistoryById() {
        List<HealthMeasureHistory> healthMeasureHistory = HealthMeasureHistory.getMeasurebyMid(id, measuretype, mid);
        if (healthMeasureHistory == null)
            throw new RuntimeException("Get: Person with " + id + " not found");
        return healthMeasureHistory;
    }
}