package entity;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity implementation class for Entity: Datas
 *
 */
@Entity
@XmlRootElement
public class Datas implements Serializable {

	   
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String Name;
	private String Path;
	private String Type;
	private static final long serialVersionUID = 1L;
	private List<Person> persons = new ArrayList<Person>();

	public List<Person> getPersons() {
		return persons;
	}
	public void addPerson(Person person) {
		persons.add(person);
	}
	public Datas() {
		super();
	}   
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}   
	public String getName() {
		return this.Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}   
	public String getPath() {
		return this.Path;
	}

	public void setPath(String Path) {
		this.Path = Path;
	}   
	public String getType() {
		return this.Type;
	}

	public void setType(String Type) {
		this.Type = Type;
	}
   
}
