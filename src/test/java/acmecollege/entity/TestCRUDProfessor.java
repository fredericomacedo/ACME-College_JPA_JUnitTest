/**
 * 
 */
package acmecollege.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

/**
 * 
 * @author Your Name
 * @version Current Date
 */
@TestMethodOrder(MethodOrderer.MethodName.class)

public class TestCRUDProfessor extends JUnitBase {
	private EntityManager em;
	private EntityTransaction et;	
	private static Professor professor;
	
	@BeforeAll
	static void setupAllInit() {
		professor = new Professor();
		professor.setFirstName("John Doe");
	}
	
	@BeforeEach
	void setup() {
		em = getEntityManager();
		et = em.getTransaction();
	}
	
	@AfterEach
	void tearDown() {
		em.close();
	}
	

	@AfterAll
	static void tearDownAll( ) {
	    
	    deleteAllData();
	    
	}

	@Test
	@Order(1)
	void test01_Empty() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Professor> root = query.from(Professor.class);
		query.select(builder.count(root));
		TypedQuery<Long> tq = em.createQuery(query);
		long result = tq.getSingleResult();
		assertThat(result, is(comparesEqualTo(0L)));
	}
	
	@Test
	@Order(2)
	void test02_Create() {
		et.begin();
		professor = new Professor();
		professor.setFirstName("Jane");
		professor.setLastName("Doe");
		professor.setDepartment("Tech");
		em.persist(professor);
		et.commit();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Professor> root = query.from(Professor.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id"))); 
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", professor.getId());
		long result = tq.getSingleResult();
		assertThat(result, is(greaterThanOrEqualTo(1L))); 
	}
	
	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		Professor professor2 = new Professor();
		assertThrows(PersistenceException.class, () -> em.persist(professor2));
		et.commit();
	}
	
	@Test
	@Order(4)
	void test04_Read() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
	    Root<Professor> root = query.from(Professor.class);
	    query.select(root);
	    TypedQuery<Professor> tq = em.createQuery(query);
	    List<Professor> professors = tq.getResultList();
	    assertThat(professors, contains(equalTo(professor)));
	}

	@Test
	@Order(5)
	void test05_ReadDependencies() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
	    Root<Professor> root = query.from(Professor.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
	    TypedQuery<Professor> tq = em.createQuery(query);
	    tq.setParameter("id", professor.getId());
	    Professor returnedProfessor = tq.getSingleResult();
	    assertThat(returnedProfessor, equalTo(professor));
	    assertThat(returnedProfessor.getFirstName(), equalTo(professor.getFirstName()));
	    assertThat(returnedProfessor.getLastName(), equalTo(professor.getLastName()));
	}
	
	@Test
	@Order(6)
	void test06_Update() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
	    Root<Professor> root = query.from(Professor.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
	    TypedQuery<Professor> tq = em.createQuery(query);
	    tq.setParameter("id", professor.getId());
	    Professor returnedProfessor = tq.getSingleResult();

	    String newFirstName = "Updated First Name";
	    String newLastName = "Updated Last Name";

	    et.begin();
	    returnedProfessor.setFirstName(newFirstName);
	    returnedProfessor.setLastName(newLastName);
	    em.merge(returnedProfessor);
	    et.commit();

	    returnedProfessor = tq.getSingleResult();

	    assertThat(returnedProfessor.getFirstName(), equalTo(newFirstName));
	    assertThat(returnedProfessor.getLastName(), equalTo(newLastName));
	}
}
