package entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-22T17:31:42.470+0200")
@StaticMetamodel(Person.class)
public class Person_ {
	public static volatile SingularAttribute<Person, String> Email;
	public static volatile SingularAttribute<Person, String> Password;
	public static volatile SingularAttribute<Person, String> Nickname;
	public static volatile SingularAttribute<Person, Integer> Id;
	public static volatile ListAttribute<Person, Person> friends;
	public static volatile ListAttribute<Person, Datas> datas;
}
