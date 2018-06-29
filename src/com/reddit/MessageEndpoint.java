package com.reddit;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.persistence.EntityNotFoundException;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

@Api(name = "messageendpoint", namespace = @ApiNamespace(ownerDomain = "mycompany.com", ownerName = "mycompany.com", packagePath = "services"))
public class MessageEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listMessage")
	public CollectionResponse<Message> listMessage(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Message> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Message.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Message>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Message obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Message> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getMessage")
	public Message getMessage(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Message message = null;
		try {
			message = mgr.getObjectById(Message.class, id);
		} finally {
			mgr.close();
		}
		return message;
	}

	@ApiMethod(name = "getAllMessage", path="getAllMessage")
	public List<Entity> getAllMessage() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("Message");
		PreparedQuery pq = ds.prepare(q);

		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(100));

		return results;
	}
	
	@ApiMethod(name = "getBestMessage", path="getBestMessage")
	public List<Entity> getBestMessage() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("Message").addSort("likeVotes", SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);

		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(5));

		return results;
	}

	@ApiMethod(name = "likeMessage", path="likeMessage")
	public Entity likeMessage(@Named("userID") String userID, @Named("idMessage") String idMessage,@Named("typeLike") Boolean typeLike) throws com.google.appengine.api.datastore.EntityNotFoundException {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key keyMessage = KeyFactory.createKey("Message", idMessage);
		Entity message,messageIndex;
		message = ds.get(keyMessage);
		Key keyIndexMessage = KeyFactory.createKey(keyMessage,"MessageIndex","i"+idMessage);
		messageIndex = ds.get(keyIndexMessage);

		ArrayList<String> likeMessageIndex;
		if(messageIndex.getProperty("voters") == null){
			likeMessageIndex = new ArrayList<String>();
		} else {
			likeMessageIndex = ((ArrayList<String>)messageIndex.getProperty("voters"));
		}
		if(!likeMessageIndex.contains(userID)){
			likeMessageIndex.add(userID);
			long votes = ((long)message.getProperty("likeVotes"));
			if(typeLike){
				votes++;
			} else {
				votes--;
			}
			message.setProperty("likeVotes", votes);
			messageIndex.setProperty("voters",likeMessageIndex);
			ds.put(messageIndex);
			ds.put(message);
		} else {
			message = null;
		}
		return message;
	}

	@ApiMethod(name="getUserMessage", path="getUserMessage")
	public List<Entity> getUserMessage(@Named("userID") String userID){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		List<Entity> ret;
		Filter pf = new FilterPredicate("ownerID", FilterOperator.EQUAL, userID);
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("Message").setFilter(pf);
		PreparedQuery pq = ds.prepare(q);
		ret = pq.asList(FetchOptions.Builder.withLimit(10));
		return ret;
	}
	
	@ApiMethod(name="getUserLikedMessage", path="getUserLikedMessage")
	public Collection<Entity> getUserLikedMessage(@Named("userID") String userID){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Map<Key, Entity> map;
		List<Entity> ret;
		Filter pf = new FilterPredicate("voters", FilterOperator.EQUAL, userID);
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("MessageIndex").setFilter(pf);
		q.setKeysOnly();
		
		PreparedQuery pq = ds.prepare(q);

		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(100));
		List<Key> pk = new ArrayList<Key>();
		for (Entity r : results) {
			pk.add(r.getParent());
		}
		
		map = ds.get(pk);
		return map.values();
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param message the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertMessage")
	public Entity insertMessage(@Named("userName") String userName, @Named("userID") String userID, @Named("title") String title, @Named("body") String body, @Named("imgUrl") String imgUrl) throws ParseException {

		Random r = new Random();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Entity message = new Entity("Message", "m"+r.nextInt());

		message.setProperty("ownerName", userName);
		message.setProperty("ownerID", userID);
		message.setProperty("body", body);
		message.setProperty("title", title);
		message.setProperty("imgUrl", imgUrl);
		message.setProperty("childIndex", "i"+message.getKey().getName());
		message.setProperty("likeVotes", 0);

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		message.setProperty("pubDate", dateFormat.format(cal.getTime()));
		ds.put(message);

		Entity messageIndex = new Entity("MessageIndex","i"+message.getKey().getName().toString(), message.getKey());

		ArrayList<String> voters= new ArrayList<String>();

		messageIndex.setProperty("voters", voters);
		ds.put(messageIndex);

		return message;

	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param message the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateMessage")
	public Message updateMessage(Message message) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsMessage(message)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(message);
		} finally {
			mgr.close();
		}
		return message;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeMessage")
	public void removeMessage(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Message message = mgr.getObjectById(Message.class, id);
			mgr.deletePersistent(message);
		} finally {
			mgr.close();
		}
	}

	private boolean containsMessage(Message message) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Message.class, message.getId());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
