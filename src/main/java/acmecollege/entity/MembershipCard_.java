package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2023-07-26T12:07:06.668-0400")
@StaticMetamodel(MembershipCard.class)
public class MembershipCard_ extends PojoBase_ {
	public static volatile SingularAttribute<MembershipCard, ClubMembership> clubMembership;
	public static volatile SingularAttribute<MembershipCard, Student> owner;
	public static volatile SingularAttribute<MembershipCard, Byte> signed;
}
