package com.reddit;

import java.util.ArrayList;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class MessageIndex {
	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	Key id;

	@Persistent
	ArrayList<String> likeVoters;
	
	@Persistent
	ArrayList<String> unlikeVoters;

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public ArrayList<String> getLikeVoters() {
		return likeVoters;
	}

	public void setLikeVoters(ArrayList<String> likeVoters) {
		this.likeVoters = likeVoters;
	}

	public ArrayList<String> getUnlikeVoters() {
		return unlikeVoters;
	}

	public void setUnlikeVoters(ArrayList<String> unlikeVoters) {
		this.unlikeVoters = unlikeVoters;
	}
}
