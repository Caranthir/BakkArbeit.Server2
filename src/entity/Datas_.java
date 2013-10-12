package entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-22T17:31:42.089+0200")
@StaticMetamodel(Datas.class)
public class Datas_ {
	public static volatile SingularAttribute<Datas, Integer> id;
	public static volatile SingularAttribute<Datas, String> Name;
	public static volatile SingularAttribute<Datas, String> Path;
	public static volatile SingularAttribute<Datas, String> Type;
	public static volatile ListAttribute<Datas, Person> persons;
}
