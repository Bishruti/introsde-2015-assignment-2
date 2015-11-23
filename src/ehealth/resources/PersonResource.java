package ehealth.resources;

import ehealth.model.Person;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Stateless // Used only if the the application is deployed in a Java EE container
@LocalBean // Used only if the the application is deployed in a Java EE container
public class PersonResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int id;

    EntityManager entityManager; // Used only if the application is deployed in a Java EE container

    public PersonResource(UriInfo uriInfo, Request request,int id, EntityManager em) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.entityManager = em;
    }

    public PersonResource(UriInfo uriInfo, Request request,int id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    // Application Integration
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getPerson() {
        Person person = this.getPersonById(id);
        if (person == null) {
            return Response.status(404).build();
        }
        else {
            return Response.ok(person).build();
        }
    }

    // For browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public Person getPersonHTML() {
        Person person = this.getPersonById(id);
        if (person == null)
            throw new RuntimeException("Get: Person with " + id + " not found");
        System.out.println("Returning person... " + person.getIdPerson());
        return person;
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putPerson(Person person) {
        System.out.println("--> Updating Person... " +this.id);
        System.out.println("--> "+person.toString());
        Response res;
        Person existing = getPersonById(this.id);

        if (existing == null) {
            res = Response.noContent().build();
        } else {
            String updatedFirstName = person.getFirstname();
            String updatedLastName = person.getLastname();
            res = Response.created(uriInfo.getAbsolutePath()).build();
            if (updatedFirstName != null) {
                existing.setFirstname(updatedFirstName);
            }
            if (updatedLastName != null) {
                existing.setLastname(updatedLastName);
            }
            /*if (person.getBirthdate() != null) {
                try {
                    existing.setBirthdate(person.getBirthdate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }*/
            Person.updatePerson(existing);
        }
        return res;
    }

    @DELETE
    public void deletePerson() {
        Person c = getPersonById(id);
        if (c == null)
            throw new RuntimeException("Delete: Person with " + id
                    + " not found");
        Person.removePerson(c);
    }

    public Person getPersonById(int personId) {
        System.out.println("Reading person from DB with id: "+personId);
        Person person = Person.getPersonById(personId);
        return person;
    }

    @Path("/{measuretype}")
    public HealthMeasureHistoryResource getPersonHealthMeasureHistory(@PathParam("personId") int id, @PathParam("measuretype") String measuretype) {
        return new HealthMeasureHistoryResource(uriInfo, request, id, measuretype);
    }
}