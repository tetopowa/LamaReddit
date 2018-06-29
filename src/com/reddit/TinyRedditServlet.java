package com.reddit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@SuppressWarnings("serial")
public class TinyRedditServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		
		
		MessageEndpoint me = new MessageEndpoint();
		// Create reddit msg 
		Random r=new Random();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		
		resp.getWriter().println("populating store...");
		
		int maxmessage=25;
		int maxuser=10;
		for (int i = 0; i < maxmessage; i++) {
			Entity e = new Entity("rmsg", "m" + i);
			e.setProperty("body", "Hello " + i );
			e.setProperty("owner","u"+r.nextInt(maxuser+1));
			//datastore.put(e);
			
			Entity index=new Entity("rmsgIndex","i"+i,e.getKey());
			ArrayList<String> voters = new ArrayList<String>();
			for (int j = 0; j < 10; j++) {
				voters.add("u"+r.nextInt(maxuser+1));
			}
			index.setProperty("voters", voters);
			//datastore.put(index);
		}
		resp.getWriter().println("populating store done !!");
		
		
		// Query
		/*String user = req.getParameter("user");
		if (user == null) {
			user = "u1";
		}
		
		resp.getWriter().println("getting index timeline for:" + user);

		Filter f = new FilterPredicate("voters", FilterOperator.EQUAL, user);

		Query q = new Query("rmsgIndex").setFilter(f);
		q.setKeysOnly();

		PreparedQuery pq = datastore.prepare(q);

		resp.getWriter().println("<h1>query</h1>");
		
		long t1 = System.currentTimeMillis();
		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(5));
		List<Key> pk = new ArrayList<Key>();
		for (Entity res : results) {
			pk.add(res.getParent());
		}
		
		Map<Key, Entity> hm = new HashMap<Key, Entity>();
		hm = datastore.get(pk);

		for (Entity ki : hm.values()) {
			resp.getWriter().println("<li>:"+ki.getProperty("body"));
		}

		resp.getWriter().println("<li> done in " + (System.currentTimeMillis() - t1));*/
		
		
		Query q = new Query("Message");

		 PreparedQuery pq= datastore.prepare(q);
		 List<Entity> results=pq.asList(FetchOptions.Builder.withLimit(5));
		
		 resp.getWriter().println("Message");
		 for (Entity re : results) {
			 resp.getWriter().println("<li>:"+re);
			 
			 Filter f = new FilterPredicate("Parent", FilterOperator.EQUAL, re.getKey());

			com.google.appengine.api.datastore.Query qu = new com.google.appengine.api.datastore.Query("MessageIndex").setFilter(f);

			PreparedQuery pq2 = datastore.prepare(qu);
			
			List<Entity> results2 = pq2.asList(FetchOptions.Builder.withLimit(5));
			
			resp.getWriter().println("MessageIndex");
			for (Entity ki : results2) {
				resp.getWriter().println("<li>:"+ki.getProperty("likeVoters"));
			}
		}

		resp.getWriter().println("finished");
		
	}
}
