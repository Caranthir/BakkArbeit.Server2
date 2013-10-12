package entity;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity implementation class for Entity: Person
 *
 */
@Entity
@NamedQuery(name="findPersonByName", query="SELECT p FROM Person p WHERE p.Nickname= ?1")
@XmlRootElement
public class Person implements Serializable {


	private String Email;
	private String Password;
	private String Nickname;   
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int Id;
	private static final long serialVersionUID = 1L;

	@OneToMany 
	@JoinTable(name = "friends", joinColumns = @JoinColumn(name="person_A_id", referencedColumnName="id"), inverseJoinColumns =@JoinColumn(name="person_B_id", referencedColumnName="id")) 
	private List<Person> friends = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "dataToPerson", joinColumns = { 
			@JoinColumn(name = "Person_ID", referencedColumnName="id") }, 
			inverseJoinColumns = { @JoinColumn(name = "Datas_ID", referencedColumnName="id") })
	private List<Datas> datas = new ArrayList<>();

	public Person() {
		super();
	}   
	public String getEmail() {
		return this.Email;
	}

	public List<Person> getFriends() {
		return friends;
	}
	public void setFriends(List<Person> friends) {
		this.friends = friends;
	}
	public List<Datas> getDatas() {
		return datas;
	}
	public void addDatas(Datas data) {
		datas.add(data);
	}
	public void setEmail(String Email) {
		this.Email = Email;
	}   
	public String getPassword() {
		return this.Password;
	}

	public void setPassword(String Password) {
		this.Password = Password;
	}   
	public String getNickname() {
		return this.Nickname;
	}

	public void setNickname(String Nickname) {
		this.Nickname = Nickname;
	}   
	public int getId() {
		return this.Id;
	}

	public void setId(int Id) {
		this.Id = Id;
	}

}
