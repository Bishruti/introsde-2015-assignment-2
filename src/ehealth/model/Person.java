package ehealth.model;

import ehealth.dao.LifeCoachDao;

import java.io.Serializable;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Entity  // Indicates that this class is an entity to persist in DB
@Table(name="Person")
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
@XmlRootElement
@XmlType(propOrder = { "idPerson", "firstname", "lastname", "birthdate", "measureType"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id // Defines this attributed as the one that identifies the entity
    @GeneratedValue(generator="sqlite_person")
    @TableGenerator(name="sqlite_person")

    @Column(name="idPerson") // Id of the Person
    private int idPerson;
    @Column(name="firstname") // Firstname of the Person
    private String firstname;
    @Column(name="lastname") // Lastname of the Person
    private String lastname;
    @Temporal(TemporalType.DATE) // Defines the precision of the date attribute
    @Column(name="birthdate")
    private Date birthdate;

    // Creating relationship with HealthProfile
    @XmlElementWrapper(name = "healthProfile")
    @OneToMany(mappedBy="person",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private List<HealthProfile> measureType;

    public List<HealthProfile> getMeasureType () {
        return measureType;
    }

    public void setMeasureType(List<HealthProfile> measureType) {
        this.measureType = measureType;
    }

    //Creating relationship with HealthMeasureHistory
    @XmlTransient
    @OneToMany(mappedBy="person",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private List<HealthMeasureHistory> measure;
    public List<HealthMeasureHistory> getMeasure() {
        return measure;
    }
    
    // Getters
    @XmlTransient
    public int getIdPerson(){
        return idPerson;
    }
    public String getLastname(){
        return lastname;
    }
    public String getFirstname(){
        return firstname;
    }
    public String getBirthdate(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        // Get the date today using Calendar object.
        return df.format(birthdate);
    }
    
    // Setters
    public void setIdPerson(int idPerson){
        this.idPerson = idPerson;
    }
    public void setLastname(String lastname){
        this.lastname = lastname;
    }
    public void setFirstname(String firstname){
        this.firstname = firstname;
    }
    public void setBirthdate(String birthdate) throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = format.parse(birthdate);
        this.birthdate = date;
    }


    // Database Operations
    public static Person getPersonById(int personId) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        Person p = em.find(Person.class, personId);
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static List<Person> getAll() {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        List<Person> list = em.createNamedQuery("Person.findAll", Person.class)
            .getResultList();
        LifeCoachDao.instance.closeConnections(em);
        return list;
    }

    public static Person savePerson(Person p) {
        appendHealthProfile(p);
        appendHealthMeasureHistory(p);
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static Person updatePerson(Person p) {
        EntityManager em = LifeCoachDao.instance.createEntityManager(); 
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static void removePerson(Person p) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        em.remove(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
    }

    private static Person appendHealthProfile(Person p) {
        for (HealthProfile healthProfile : p.measureType) {
            healthProfile.setPerson(p);
        }
        return p;
    }

    private static Person appendHealthMeasureHistory(Person p) {
        java.util.Date date = new java.util.Date();
        p.measure = new ArrayList<HealthMeasureHistory>(p.measureType.size());

        for (HealthProfile healthProfile : p.measureType) {
            HealthMeasureHistory healthMeasureHistory = new HealthMeasureHistory();
            healthMeasureHistory.setPerson(p);
            healthMeasureHistory.setValue(healthProfile.getValue());
            healthMeasureHistory.setMeasure(healthProfile.getMeasure());
            healthMeasureHistory.setCreated(date);
            p.measure.add(healthMeasureHistory);
        }
        return p;
    }
}