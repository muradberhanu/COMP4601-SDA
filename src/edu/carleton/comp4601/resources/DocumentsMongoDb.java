package edu.carleton.comp4601.resources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;


public class DocumentsMongoDb {

	String ID = "DocId";
	static MongoClient mongoClient = new MongoClient("localhost", 27017);
	static MongoDatabase db = mongoClient.getDatabase("crawler");
	public static MongoCollection<Document> coll = db.getCollection("crawledSites");
	static DocumentsMongoDb instance;
	
	public DocumentsMongoDb() {
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		MongoDatabase db = mongoClient.getDatabase("crawler");
		MongoCollection<Document> coll = db.getCollection("crawledSites");
	}
	
	private ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document> getDocuments() {
		FindIterable<Document> cursor = coll.find();
		ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document> map = new ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document>();
		MongoCursor<Document> c = cursor.iterator();
		while (c.hasNext()) {
			Document object = c.next();
			if (object.get(ID) != null) {
				Map<String, String> documentMap = new ConcurrentHashMap<String, String>();
				documentMap.put("id", object.getString("id"));
				documentMap.put("score", object.getString("score"));
				documentMap.put("name", object.getString("title"));
				documentMap.put("content", object.getString("content"));
				documentMap.put("url", object.getString("url"));
				map.put((Integer) object.get(ID), new edu.carleton.comp4601.dao.Document(documentMap));
			}
		}
		return map;
	}
	
	public edu.carleton.comp4601.dao.Document find(int id) {
		FindIterable<Document> cursor = coll.find(new BasicDBObject(ID, id));
		MongoCursor<Document> c = cursor.iterator();
		if (c.hasNext()) {
			Document object = c.next();
			Map<String, String> documentMap = new ConcurrentHashMap<String, String>();
			documentMap.put("id", object.getString("id"));
			documentMap.put("score", object.getString("score"));
			documentMap.put("name", object.getString("title"));
			documentMap.put("content", object.getString("content"));
			documentMap.put("url", object.getString("url"));
			return new edu.carleton.comp4601.dao.Document(documentMap);
		} else
			return null;
	}
	
	public static DocumentsMongoDb getInstance() {
		if (instance == null) {
			try {
				if(coll.count()==0)
				CrawlerController.startCrawl(coll);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			instance = new DocumentsMongoDb();
		}
		return instance;
	}
	
	public boolean close(int id) {
		DeleteResult result = coll.deleteOne(new BasicDBObject(ID, id));
		return result != null;
	}
	
	public long size() {
		return coll.count();
	}
	
//	static String getAllDocuments(MongoCollection<Document> col) {
//        System.out.println("Fetching all documents from the collection");
//        String graphString = null;
//        // Performing a read operation on the collection.
//        FindIterable<Document> fi = col.find();
//        MongoCursor<Document> cursor = fi.iterator();
//        try {
//            while(cursor.hasNext()) {
////            	if (cursor.next().containsKey("graph")){
////            		graphString = cursor.next().get("graph").toString();
////				}
//                System.out.println(cursor.next().toJson());
//            }
//        } finally {
//            cursor.close();
//            return graphString;
//        }
//    }
	
}
