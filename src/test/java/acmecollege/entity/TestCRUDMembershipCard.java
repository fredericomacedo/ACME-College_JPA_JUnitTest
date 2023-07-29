
package acmecollege.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
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
public class TestCRUDMembershipCard extends JUnitBase {
	private EntityManager em;
	private EntityTransaction et;	
	private static MembershipCard membershipCard;
	private static Student student;
	private static ClubMembership clubMembership;
	private static StudentClub clubNonAcademic;
	@BeforeAll
	static void setupAllInit() {
		student = new Student();
		student.setFullName("John", "Doe");
		// Initialize other fields of student as required
		
		DurationAndStatus ds = new DurationAndStatus();
		ds.setDurationAndStatus(LocalDateTime.of(2022, 8, 28, 0, 0), LocalDateTime.of(2023, 8, 27, 0, 0) , "+");

		
		clubNonAcademic = new NonAcademicStudentClub();
		clubNonAcademic.setName("Student Hiking Club");
		
		clubMembership = new ClubMembership();
		clubMembership.setDurationAndStatus(ds);
		clubMembership.setStudentClub(clubNonAcademic);
		// Initialize other fields of clubMembership as required

		membershipCard = new MembershipCard();
		membershipCard.setOwner(student);
		membershipCard.setClubMembership(clubMembership);
		// Initialize other fields of membershipCard as required
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
		Root<MembershipCard> root = query.from(MembershipCard.class);
		query.select(builder.count(root));
		TypedQuery<Long> tq = em.createQuery(query);
		long result = tq.getSingleResult();
		assertThat(result, is(comparesEqualTo(0L)));
	}
	
	@Test
	@Order(2)
	void test02_Create() {
		et.begin();
		em.persist(clubNonAcademic);
		em.persist(student);
		em.persist(clubMembership);
		em.persist(membershipCard);
		et.commit();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<MembershipCard> root = query.from(MembershipCard.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(MembershipCard_.id), builder.parameter(Integer.class, "id"))); 
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", membershipCard.getId());
		long result = tq.getSingleResult();
		assertThat(result, is(greaterThanOrEqualTo(1L))); 
	}
	
	@Test
	@Order(3)
	void test03_CreateInvalid() {
		et.begin();
		MembershipCard membershipCard2 = new MembershipCard();
		assertThrows(PersistenceException.class, () -> em.persist(membershipCard2));
		et.commit();
	}
	
	@Test
	@Order(4)
	void test04_Read() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<MembershipCard> query = builder.createQuery(MembershipCard.class);
	    Root<MembershipCard> root = query.from(MembershipCard.class);
	    query.select(root);
	    TypedQuery<MembershipCard> tq = em.createQuery(query);
	    List<MembershipCard> membershipCards = tq.getResultList();
	    assertThat(membershipCards, contains(equalTo(membershipCard)));
	}

	@Test
	@Order(5)
	void test05_ReadDependencies() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<MembershipCard> query = builder.createQuery(MembershipCard.class);
	    Root<MembershipCard> root = query.from(MembershipCard.class);
	    query.select(root);
	    query.where(builder.equal(root.get(MembershipCard_.id), builder.parameter(Integer.class, "id")));
	    TypedQuery<MembershipCard> tq = em.createQuery(query);
	    tq.setParameter("id", membershipCard.getId());
	    MembershipCard returnedMembershipCard = tq.getSingleResult();
	    assertThat(returnedMembershipCard, equalTo(membershipCard));
	    assertThat(returnedMembershipCard.getOwner(), equalTo(student));
	    assertThat(returnedMembershipCard.getClubMembership(), equalTo(clubMembership));
	}
	
	@Test
	@Order(6)
	void test06_Update() {
	    CriteriaBuilder builder = em.getCriteriaBuilder();
	    CriteriaQuery<MembershipCard> query = builder.createQuery(MembershipCard.class);
	    Root<MembershipCard> root = query.from(MembershipCard.class);
	    query.select(root);
	    query.where(builder.equal(root.get(MembershipCard_.id), builder.parameter(Integer.class, "id")));
	    TypedQuery<MembershipCard> tq = em.createQuery(query);
	    tq.setParameter("id", membershipCard.getId());
	    MembershipCard returnedMembershipCard = tq.getSingleResult();

	    Student newStudent = new Student();
	    newStudent.setFullName("Jane",  "Doe");
	    // Initialize other fields of newStudent as required
	    
	    DurationAndStatus ds = new DurationAndStatus();
		ds.setDurationAndStatus(LocalDateTime.of(2022, 8, 28, 0, 0), LocalDateTime.of(2023, 8, 27, 0, 0) , "+");

	    et.begin();
	    em.persist(newStudent);
	    //em.persist(newClubMembership);
	    returnedMembershipCard.setOwner(newStudent);
	    et.commit(); 
	    returnedMembershipCard = tq.getSingleResult();

	    assertThat(returnedMembershipCard.getOwner(), equalTo(newStudent));
	    //assertThat(returnedMembershipCard.getClubMembership(), equalTo(newClubMembership));
	}
}
