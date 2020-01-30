package de.unituebingen.validator.persistence.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public class Persistable {
	
	@Id @GeneratedValue
	private Long id;
	
	@Version
	private Long version;
		
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	
	public boolean isDeleted() {
		return false;
	}
	
	

}
