package org.grape.galaxy.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class LastAccessInfo implements Serializable {

	private static final long serialVersionUID = -6109011573644430143L;

	@PrimaryKey
	@Persistent
	private String userId;
	
	@Persistent(columns = @Column(length = 64))
	private String email;
	@Persistent
	private Date lastAccessTime;
	
	@Persistent
	private boolean kicked = false;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public boolean isKicked() {
		return kicked;
	}

	public void setKicked(boolean kicked) {
		this.kicked = kicked;
	}
}
