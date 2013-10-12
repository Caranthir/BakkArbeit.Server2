package controller;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;

import sun.misc.BASE64Decoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import entity.Datas;
import entity.Person;

@Path("/person")
public class PersonResource {

	private EntityManagerFactory factory = Persistence.createEntityManagerFactory("bakkArbeit.Server");
	private EntityManager em = factory.createEntityManager();

	private Person person = new Person();

	// The @Context annotation allows us to have certain contextual objects
	// injected into this class.
	// UriInfo object allows us to get URI information (no kidding).
	@Context
	UriInfo uriInfo;

	// Another "injected" object. This allows us to use the information that's
	// part of any incoming request.
	// We could, for example, get header information, or the requestor's address.
	@Context
	Request request;

	// Basic "is the service running" test
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String respondAsReady() {
		return "Demo service is ready!";
	}

	@GET
	@Path("sample")
	@Produces(MediaType.APPLICATION_JSON)
	public Person getSamplePerson() {
		person.setNickname("denem");
		person.setId(5);
		System.out.println("Returning sample person: " + person.getNickname() + " " + person.getId());

		return person;
	}

	// Use data from the client source to create a new Person object, returned in JSON format. 
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Person postPerson(
			MultivaluedMap<String, String> personParams
			) {

		String nickName = personParams.getFirst("nickname");
		String pass = personParams.getFirst("password");
		String email = personParams.getFirst("email");

		if(getPerson(nickName)!=null){
			System.out.println("User already exists " + nickName + " " + pass + "  " + email);
			return null;
		}

		System.out.println("Storing posted " + nickName + " " + pass + "  " + email);

		person.setNickname(nickName);
		person.setPassword(pass);
		person.setEmail(email);

		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();

		System.out.println("person info: " + person.getNickname() + " " + person.getPassword() + " " + person.getEmail());

		return person;

	}

	@GET
	@Path("searchfriend")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String searchFriend(@QueryParam("Name") String friendname){
		if(getPerson(friendname)!=null){
			return "OK";
		}else
			return "FALSE";
	}

	@POST
	@Path("addfriend")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String addFriend(String friendname, @Context HttpHeaders headers){
		Person p= getPerson(headers);
		p.getFriends().add(getPerson(friendname));

		em.getTransaction().begin();
		em.persist(p);
		em.getTransaction().commit();

		return "OK";
	}

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@Context HttpHeaders headers) {

		Person p = getPerson(headers);

		//final List<String> contentType = headers.getRequestHeader(HttpHeaders.CONTENT_TYPE);
		System.out.println(p.getNickname()); 
		System.out.println(fileDetail.getFileName());

		String uploadedFileLocation = "c://uploaded/" +p.getNickname()+ "/" + fileDetail.getFileName();

		File file = new File("c://uploaded//" + p.getNickname());

		if (!file.exists()) {
			if (file.mkdirs()) {
				System.out.println("Multiple directories are created!");
			} else {
				System.out.println("Failed to create multiple directories!");
			}
		}


		Datas d= new Datas();
		d.addPerson(p);
		d.setPath(uploadedFileLocation);
		d.setName(fileDetail.getFileName());
		d.setType(d.getName().substring(d.getName().length()-3,d.getName().length()));
		p.addDatas(d);

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		em.getTransaction().begin();
		em.persist(d);
		em.persist(p);
		em.getTransaction().commit();

		String output = "File uploaded to : " + uploadedFileLocation;

		return "OK";
	}
	/**
	 * some parts copied from http://stackoverflow.com/questions/12239868/whats-the-correct-way-to-send-a-file-from-rest-web-service-to-client
	 * @param key
	 * @param response
	 * @param headers
	 * @return
	 * @throws IOException
	 */

	@GET
	@Path("/{key}")
	public Response download(@PathParam("key") int key,
			@Context HttpServletResponse response,
			@Context HttpHeaders headers) throws IOException {
		Person p = getPerson(headers);
		Datas d=null;
		for(Datas i: p.getDatas()){
			if(i.getId()==key){
				//System.out.println("olduu");
				d=i;
				break;
			}
		}
		if(d==null){
			return null;
		}
		File file = new File(d.getPath());

		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "attachment; filename="
				+ file.getName());
		response.setHeader("Content-Type", d.getType());
		ServletOutputStream outStream = response.getOutputStream();
		byte[] bbuf = new byte[(int) file.length() + 1024];
		DataInputStream in = new DataInputStream(
				new FileInputStream(file));
		int length = 0;

		while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			outStream.write(bbuf, 0, length);
		}

		in.close();
		outStream.flush();

		return Response.ok().build();
	}

	@GET
	@Path("dataslist")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Datas> getDatasList(@Context HttpHeaders headers){
		return getPerson(headers).getDatas();	
	}



	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private Person getPerson(String nickname){
		Query q = em.createNamedQuery("findPersonByName");
		q.setParameter(1, nickname);
		Person p;
		try{
			p = (Person) q.getSingleResult();
		}catch(NoResultException ex){
			return null;
		}
		//Return null to continue request processing
		return p;
	}

	@GET
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public Person getPerson(@Context HttpHeaders headers){


		final String AUTHENTICATION_SCHEME = "Basic";
		//Get request headers

		//Fetch authorization header
		final List<String> authorization = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);

		//If no authorization information present; block access
		if(authorization == null || authorization.isEmpty())
		{
			System.out.println("authorization null");
			return null;
		}

		//Get encoded username and password
		final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

		//Decode username and password
		String usernameAndPassword;
		//usernameAndPassword = StringUtils.newStringUtf8(Base64.decodeBase64(encodedUserPassword));
		//usernameAndPassword = StringUtils.newStringUtf8(Base64.decodeBase64(encodedUserPassword));

		//Split username and password tokens
		final StringTokenizer tokenizer = new StringTokenizer(encodedUserPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();

		//Query q = em.createQuery("SELECT p FROM Person p WHERE p.Nickname='" + username+ "'");

		Query q = em.createNamedQuery("findPersonByName");
		q.setParameter(1, username);
		Person p;
		try{
			p = (Person) q.getSingleResult();
		}catch(NoResultException ex){
			return null;
		}
		if(p==null){
			return null;
		}
		if(!p.getPassword().equals(password)){
			return null;
		}

		//Verifying Username and password
		System.out.println(username);
		System.out.println(password);


		//Return null to continue request processing
		return p;
	}


}
