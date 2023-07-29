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
 * @author Fredeirco Lucio Macedo
 * @version July 28, 2023
 */
@TestMethodOrder(MethodOrderer.MethodName.class)

public class TestCRUDStudent extends JUnitBase {
	private EntityManager em;
	private EntityTransaction et;	
	private static Student student;
	
	@BeforeAll
	static void setupAllInit() {

	}
	
	@AfterAll
	static void tearDownAll( ) {
	   
		
	    deleteAllData(); 
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

	@Test
	@Order(1)
	void test01_Empty() {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(s) from Student s
		Root<Student> root = query.from(Student.class);
		query.select(builder.count(root));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		// Get the result as row count
		long result = tq.getSingleResult();

		assertThat(result, is(comparesEqualTo(0L)));

	}
	
	@Test
	@Order(2)
	void test02_Create() {
		// Begin transaction
		et.begin();
		
		// Create a new Student
		student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		
		
		// Persist the student entity
		em.persist(student);
		
		// Commit the transaction
		et.commit();
		
		// Create CriteriaBuilder, CriteriaQuery and Root
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Student> root = query.from(Student.class);
		
		// Build the query
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id"))); 
		
		// Create TypedQuery, set the parameter and get the result
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", student.getId());
		long result = tq.getSingleResult();
		
		// Check if the student was created successfully
		assertThat(result, is(greaterThanOrEqualTo(1L))); 
	}
	
	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		
		// Create a new Student without setting the name
		Student student2 = new Student();
		
		// We expect a failure because name is a required field
		assertThrows(PersistenceException.class, () -> em.persist(student2));
		
		et.commit();
	}
	
	@Test
	@Order(4)
	void test04_Read() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    
	    // Create query for Student
	    CriteriaQuery<Student> query = builder.createQuery(Student.class);
	    
	    // Select student from Student
	    Root<Student> root = query.from(Student.class);
	    query.select(root);
	    
	    // Create query
	    TypedQuery<Student> tq = em.createQuery(query);
	    
	    // Get the result as a list of Students
	    List<Student> students = tq.getResultList();

	    // Check if the list contains the student we added
	    assertThat(students, contains(equalTo(student)));
	}

	@Test
	@Order(5)
	void test05_ReadDependencies() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    
	    // Create query for Student
	    CriteriaQuery<Student> query = builder.createQuery(Student.class);
	    
	    // Select student from Student
	    Root<Student> root = query.from(Student.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));
	    
	    // Create query and set the parameter
	    TypedQuery<Student> tq = em.createQuery(query);
	    tq.setParameter("id", student.getId());
	    
	    // Get the result as row count
	    Student returnedStudent = tq.getSingleResult();

	    // Check if the returned Student is the same as the one we added
	    assertThat(returnedStudent, equalTo(student));
	    
	    // You can also assert that other attributes of the Student are as expected
	    assertThat(returnedStudent.getFirstName(), equalTo(student.getFirstName()));
	    assertThat(returnedStudent.getLastName(), equalTo(student.getLastName()));
	   
	}
	
	@Test
	@Order(6)
	void test06_Update() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    
	    // Create query for Student
	    CriteriaQuery<Student> query = builder.createQuery(Student.class);
	    
	    // Select student from Student student
	    Root<Student> root = query.from(Student.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));
	    
	    // Create query and set the parameter
	    TypedQuery<Student> tq = em.createQuery(query);
	    tq.setParameter("id", student.getId());
	    
	    // Get the result as row count
	    Student returnedStudent = tq.getSingleResult();

	    String newFirstName = "Updated First Name";
	    String newLastName = "Upadated Last Name";

	    et.begin();
	    returnedStudent.setFirstName(newFirstName);
	    returnedStudent.setLastName(newLastName);
	    em.merge(returnedStudent);
	    et.commit();

	    returnedStudent = tq.getSingleResult();

	    assertThat(returnedStudent.getFirstName(), equalTo(newFirstName));
	    assertThat(returnedStudent.getLastName(), equalTo(newLastName));
	}
	
	@Test
	@Order(7)
	void test07_Delete() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();

	    // Create query for Student
	    CriteriaQuery<Student> query = builder.createQuery(Student.class);

	    // Select student from Student student
	    Root<Student> root = query.from(Student.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));

	    // Create query and set the parameter
	    TypedQuery<Student> tq = em.createQuery(query);
	    tq.setParameter("id", student.getId());

	    // Get the result as row count
	    Student returnedStudent = tq.getSingleResult();

	    // Begin the transaction
	    et.begin();
	    // Remove the student
	    em.remove(returnedStudent);
	    // Commit the transaction
	    et.commit();

	    // Trying to retrieve the student again, expecting a NoResultException since it has been removed
	    Exception exception = assertThrows(javax.persistence.NoResultException.class, () -> {
	        tq.getSingleResult();
	    });

	    // Verify the exception is of type NoResultException
	    assertThat(exception.getClass(), equalTo(javax.persistence.NoResultException.class));
	}


	
}
