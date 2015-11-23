package ehealth.model;

import ehealth.dao.LifeCoachDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

// The persistent class for the "HealthProfile" database table.

@Entity // Indicates that this class is an entity to persist in DB
@Table(name = "HealthProfile")
@NamedQueries({
        @NamedQuery(name = "HealthProfile.findAll", query = "SELECT l FROM HealthProfile l"),
        @NamedQuery(name = "HealthProfile.getHealthProfileByPerson", query = "SELECT h FROM HealthProfile h WHERE h.person.idPerson = :idperson")
})

@XmlRootElement(name = "measureType")
@XmlType(propOrder = {"measure", "value" })
@XmlAccessorType(XmlAccessType.FIELD)

public class HealthProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id // Defines this attributed as the one that identifies the entity
    @GeneratedValue(generator="sqlite_healthprofile")
    @TableGenerator(name="sqlite_healthprofile")

    @XmlTransient
    @Column(name = "idHealthProfile") // Id of HealthProfile
    private int idHealthProfile;

    @Column(name = "value") // Value of HealthProfile
    private String value;

    @Column(name = "measure") // Measure of HealthProfile
    private String measure;

    // Creating relationship with Person
    @XmlTransient
    @ManyToOne
    @JoinColumn(name="idPerson",referencedColumnName="idPerson")
    private Person person;

    public HealthProfile() {
    }

    //Getters
    @XmlTransient
    public int getIdHealthProfile() {
        return this.idHealthProfile;
    }

    public void setIdHealthProfile(int idHealthProfile) {
        this.idHealthProfile = idHealthProfile;
    }

    public String getValue() {
        return this.value;
    }

    //Setters
    public void setValue(String value) {
        this.value = value;
    }

    public String getMeasure() {
        return this.measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    // we make this transient for JAXB to avoid and infinite loop on serialization
    @XmlTransient
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    // Database operations

    public static HealthProfile getHealthProfileByPersonId(int idPerson) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        HealthProfile p = em.find(HealthProfile.class, idPerson);
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static List<HealthProfile> getHealthProfileByPerson(int idPerson) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        List<HealthProfile> list = new ArrayList<HealthProfile>();
        list = em.createNamedQuery("HealthProfile.getHealthProfileByPerson", HealthProfile.class)
                .setParameter("idperson", idPerson)
                .getResultList();
        LifeCoachDao.instance.closeConnections(em);
        return list;
    }

    public static HealthProfile getHealthProfileById(int idHealthProfile) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        HealthProfile p = em.find(HealthProfile.class, idHealthProfile);
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static HealthProfile getHealthProfileByMeasure(String measure) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        HealthProfile p = em.find(HealthProfile.class, measure);
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static List<HealthProfile> getAll() {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        List<HealthProfile> list = em.createNamedQuery("HealthProfile.findAll", HealthProfile.class).getResultList();
        LifeCoachDao.instance.closeConnections(em);
        return list;
    }

    public static HealthProfile saveHealthProfile(HealthProfile p) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static HealthProfile updateHealthProfile(HealthProfile p) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static void removeHealthProfile(HealthProfile p) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        em.remove(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
    }
}
