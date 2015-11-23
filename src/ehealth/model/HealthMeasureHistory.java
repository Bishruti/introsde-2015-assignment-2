package ehealth.model;

import ehealth.dao.LifeCoachDao;
import ehealth.model.Person;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

// The persistent class for the "HealthMeasureHistory" database table.
@Entity // Indicates that this class is an entity to persist in DB
@Table(name="HealthMeasureHistory")
//Queries to obtain HealthMeasureHistory by using various attributes.
@NamedQueries({
	@NamedQuery(name="HealthMeasureHistory.findAll", query="SELECT h FROM HealthMeasureHistory h"),
	@NamedQuery(name="HealthMeasureHistory.getTypes", query="SELECT DISTINCT h.measure FROM HealthMeasureHistory h"),
	@NamedQuery(name="HealthMeasureHistory.getHistoryByPersonIdAndMeasureType", query="SELECT h FROM HealthMeasureHistory h WHERE h.person.idPerson = :idperson AND h.measure = :measure"),
	@NamedQuery(name="HealthMeasureHistory.getHistoryByPersonIdAndMeasureId", query="SELECT h FROM HealthMeasureHistory h WHERE h.person.idPerson = :idperson AND h.measure= :measure AND h.mid = :measureid"),
})

@XmlRootElement(name = "measureHistory")
@XmlType(propOrder = {"mid", "value", "created" })
@XmlAccessorType(XmlAccessType.FIELD)
public class HealthMeasureHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id // Defines this attributed as the one that identifies the entity
	@GeneratedValue(generator="sqlite_healthmeasurehistory")
	@TableGenerator(name="sqlite_healthmeasurehistory")
	@Column(name="mid") // Id of HealthMeasureHistory
	private int mid;

	@Temporal(TemporalType.DATE)
	@Column(name="created") // HealthMeasureHistory created date
	private Date created;

	@XmlTransient
	@Column(name="measure") // Measure Name of Measure
	private String measure;

	@Column(name="value") // Value of Measure
	private String value;

    // Creating relationship with Person
    @XmlTransient
	@ManyToOne
	@JoinColumn(name = "idPerson", referencedColumnName = "idPerson")
	private Person person;

	public HealthMeasureHistory() {
	}

    //Getters
	public int getMid() {
		return this.mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public Date getCreated() {
		return this.created;
	}

    //Setters
	public void setCreated(Date created) {
		this.created = created;
	}

	public String getMeasure() {
		return this.measure;
	}

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	@XmlTransient
	public Person getPerson() {
	    return person;
	}

	public void setPerson(Person param) {
	    this.person = param;
	}

	// Database Operations
	public static HealthMeasureHistory getHealthMeasureHistoryById(int id) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		HealthMeasureHistory p = em.find(HealthMeasureHistory.class, id);
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}

	public static HealthMeasureHistory getHealthMeasureHistoryByType(String id) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		HealthMeasureHistory p = em.find(HealthMeasureHistory.class, id);
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}

	public static List<HealthMeasureHistory> getDetailsByPerson(int idPerson, String measureName) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<HealthMeasureHistory> list = new ArrayList<HealthMeasureHistory>();
		list = em.createNamedQuery("HealthMeasureHistory.getHistoryByPersonIdAndMeasureType", HealthMeasureHistory.class)
				.setParameter("idperson", idPerson)
				.setParameter("measure", measureName)
				.getResultList();
		LifeCoachDao.instance.closeConnections(em);
		return list;
	}

	public static List<HealthMeasureHistory> getMeasurebyMid(int idPerson, String measureName, int mid) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<HealthMeasureHistory> list = new ArrayList<HealthMeasureHistory>();
		list = em.createNamedQuery("HealthMeasureHistory.getHistoryByPersonIdAndMeasureId", HealthMeasureHistory.class)
				.setParameter("idperson", idPerson)
				.setParameter("measure", measureName)
				.setParameter("measureid", mid)
				.getResultList();
		LifeCoachDao.instance.closeConnections(em);
		return list;
	}

	public static List<String> getTypes() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<String> list = new ArrayList<String>();
		list = em.createNamedQuery("HealthMeasureHistory.getTypes", String.class)
				.getResultList();
		LifeCoachDao.instance.closeConnections(em);
		return list;
	}

	
	public static List<HealthMeasureHistory> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<HealthMeasureHistory> list = em.createNamedQuery("HealthMeasureHistory.findAll", HealthMeasureHistory.class).getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	public static HealthMeasureHistory saveHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static HealthMeasureHistory updateHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p=em.merge(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static void removeHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    p=em.merge(p);
	    em.remove(p);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	}
}


