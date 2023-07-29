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
public class TestCRUDCourse extends JUnitBase {
	private EntityManager em;
	private EntityTransaction et;	
	private static Course course;
	
	@BeforeAll
	static void setupAllInit() {
		course = new Course();
		course.setCourseCode("CS101");
		course.setCourseTitle("Introduction to Computer Science");
		course.setYear(2023);
		course.setSemester("Fall");
		course.setCreditUnits(3);
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
		Root<Course> root = query.from(Course.class);
		query.select(builder.count(root));
		TypedQuery<Long> tq = em.createQuery(query);
		long result = tq.getSingleResult();
		assertThat(result, is(comparesEqualTo(0L)));
	}
	
	@Test
	@Order(2)
	void test02_Create() {
		et.begin();
		course = new Course();
		course.setCourseCode("CS102");
		course.setCourseTitle("Data Structures");
		course.setYear(2023);
		course.setSemester("Spring");
		course.setCreditUnits(3);
		em.persist(course);
		et.commit();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Course> root = query.from(Course.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id"))); 
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", course.getId());
		long result = tq.getSingleResult();
		assertThat(result, is(greaterThanOrEqualTo(1L))); 
	}
	
	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		Course course2 = new Course();
		assertThrows(PersistenceException.class, () -> em.persist(course2));
		et.commit();
	}
	
	@Test
	@Order(4)
	void test04_Read() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Course> query = builder.createQuery(Course.class);
	    Root<Course> root = query.from(Course.class);
	    query.select(root);
	    TypedQuery<Course> tq = em.createQuery(query);
	    List<Course> courses = tq.getResultList();
	    assertThat(courses, contains(equalTo(course)));
	}

	@Test
	@Order(5)
	void test05_ReadDependencies() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Course> query = builder.createQuery(Course.class);
	    Root<Course> root = query.from(Course.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id")));
	    TypedQuery<Course> tq = em.createQuery(query);
	    tq.setParameter("id", course.getId());
	    Course returnedCourse = tq.getSingleResult();
	    assertThat(returnedCourse, equalTo(course));
	    assertThat(returnedCourse.getCourseCode(), equalTo(course.getCourseCode()));
	    assertThat(returnedCourse.getCourseTitle(), equalTo(course.getCourseTitle()));
	}
	
	@Test
	@Order(6)
	void test06_Update() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<Course> query = builder.createQuery(Course.class);
	    Root<Course> root = query.from(Course.class);
	    query.select(root);
	    query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id")));
	    TypedQuery<Course> tq = em.createQuery(query);
	    tq.setParameter("id", course.getId());
	    Course returnedCourse = tq.getSingleResult();

	    String newCourseCode = "CS103";
	    String newCourseTitle = "Algorithms";

	    et.begin();
	    returnedCourse.setCourseCode(newCourseCode);
	    returnedCourse.setCourseTitle(newCourseTitle);
	    em.merge(returnedCourse);
	    et.commit();

	    returnedCourse = tq.getSingleResult();

	    assertThat(returnedCourse.getCourseCode(), equalTo(newCourseCode));
	    assertThat(returnedCourse.getCourseTitle(), equalTo(newCourseTitle));
	}
}
