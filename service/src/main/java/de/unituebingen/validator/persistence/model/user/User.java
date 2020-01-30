package de.unituebingen.validator.persistence.model.user;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.unituebingen.validator.persistence.model.Persistable;

@Entity(name="SERVICE_USER")
@NamedQueries ({
	@NamedQuery(name=User.SELECT_BY_LOGIN, query = "SELECT u FROM SERVICE_USER u WHERE u.login = ?1"),
	@NamedQuery(name = User.COUNT_ALL_WITH_LOGIN_PATTERN, query = "SELECT Count(s) from SERVICE_USER s WHERE UPPER(s.login) LIKE UPPER(?1)")
})

public class User extends Persistable{
	
	public static final String SELECT_BY_LOGIN = "User.selectByLogin";
	public static final String COUNT_ALL_WITH_LOGIN_PATTERN = "User.countAllWithLoginPattern";
		
	@Column(nullable = false, unique = true)
	private String login;
	
	private String passwordDigest;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModified;

	@PrePersist
	private void prePersist() {
		Date date = new Date();
		this.created = date;
		this.lastModified = date;
	}
	
	@PreUpdate
	private void preUpdate() {
		this.lastModified = new Date();
	}
	
	
	// Generated Setters and Getters
	
	public String getPasswordDigest() {
		return passwordDigest;
	}

	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	



	
	
	
	
	

	
	
	

}
