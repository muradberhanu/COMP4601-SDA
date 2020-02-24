package edu.carleton.comp4601.resources;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

import edu.carleton.comp4601.dao.DocumentCollection;


public class DocumentsMongoDb {

	static String ID = "id";
	static MongoClient mongoClient = new MongoClient("localhost", 27017);
	static MongoDatabase db = mongoClient.getDatabase("crawler");
	public static MongoCollection<Document> coll = db.getCollection("crawledSites");
	static DocumentsMongoDb instance;
	static DocumentCollection documents;
	
	public DocumentsMongoDb() {
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		MongoDatabase db = mongoClient.getDatabase("crawler");
		MongoCollection<Document> coll = db.getCollection("crawledSites");
	}
	
	public ConcurrentHashMap<String, edu.carleton.comp4601.dao.Document> getDocuments() {
		
		long collectionSize = coll.count();
		if(collectionSize==0) {
			BasicDBObject document = new BasicDBObject();
	    	coll.deleteMany(document);
			try {
				coll = CrawlerController.startCrawl(coll);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FindIterable<Document> cursor = coll.find();
		ConcurrentHashMap<String, edu.carleton.comp4601.dao.Document> map = new ConcurrentHashMap<String, edu.carleton.comp4601.dao.Document>();
		MongoCursor<Document> c = cursor.iterator();
		while (c.hasNext()) {
			Document object = c.next();
			if (object.get(ID) != null) {
				Map<String, Object> documentMap = new ConcurrentHashMap<String, Object>();
				documentMap.put("id", Integer.parseInt(object.getString("id")));
				documentMap.put("score", Float.parseFloat(object.getString("score")));
				documentMap.put("name", object.getString("title"));
				documentMap.put("content", object.getString("content"));
				documentMap.put("url", object.getString("url"));
				map.put(object.getString(ID), new edu.carleton.comp4601.dao.Document(documentMap));
			}
		}
		return map;
	}
	
	public edu.carleton.comp4601.dao.Document find(int id) {
		FindIterable<Document> cursor = coll.find(new BasicDBObject(ID, Integer.toString(id)));
		MongoCursor<Document> c = cursor.iterator();
		if (c.hasNext()) {
			Document object = c.next();
			Map<String, Object> documentMap = new ConcurrentHashMap<String, Object>();
			documentMap.put("id", Integer.parseInt(object.getString("id")));
			documentMap.put("score", Float.parseFloat(object.getString("score")));
			documentMap.put("name", object.getString("title"));
			documentMap.put("content", object.getString("content"));
			documentMap.put("url", object.getString("url"));
			return new edu.carleton.comp4601.dao.Document(documentMap);
		} else
			return null;
	}
	
	public Document getMongoDocument(int id) {
		FindIterable<Document> cursor = coll.find(new BasicDBObject(ID, Integer.toString(id)));
		MongoCursor<Document> c = cursor.iterator();
		if (c.hasNext()) {
			Document object = c.next();
			return object;
		} else
			return null;
	}
	
	public static DocumentsMongoDb getInstance() {
		//if (coll.count()==0) {
			try {
				//if(coll.count()==0)
//				BasicDBObject document = new BasicDBObject();
//		    	coll.deleteMany(document);
//				CrawlerController.startCrawl(coll);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			instance = new DocumentsMongoDb();
		//}
		return instance;
	}
	
	public boolean deleteDocument(String id) {
		DeleteResult result = coll.deleteOne(new BasicDBObject(ID, id));
		return result != null;
	}
	
	public long size() {
		
		FindIterable<Document> fi = coll.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
//            	if (cursor.next().containsKey("graph")){
//            		graphString = cursor.next().get("graph").toString();
//				}
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
		return coll.count();
	}
	
	static ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document> getDocuments(MongoCollection<Document> col) {
		FindIterable<Document> cursor = coll.find();
		ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document> map = new ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document>();
		MongoCursor<Document> c = cursor.iterator();
		while (c.hasNext()) {
			Document object = c.next();
			if (object.get(ID) != null) {
				Map<String, Object> documentMap = new ConcurrentHashMap<String, Object>();
				documentMap.put("id", Integer.parseInt(object.getString("id")));
				documentMap.put("score", Float.parseFloat(object.getString("score")));
				documentMap.put("name", object.getString("title"));
				documentMap.put("content", object.getString("content"));
				documentMap.put("url", object.getString("url"));
				edu.carleton.comp4601.dao.Document document = new edu.carleton.comp4601.dao.Document(documentMap);
				map.put((Integer) object.get(ID), document);
			}
		}
		return map;
    }
	
	static void boost() {
		FindIterable<Document> cursor = coll.find();
		ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document> map = new ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document>();
		MongoCursor<Document> c = cursor.iterator();
		while (c.hasNext()) {
			Document object = c.next();
			String score = object.getString("score");
			float newScore = Float.parseFloat(score) + Float.parseFloat(score) ;
			coll.updateOne(Filters.eq("id", object.getString("id")), new Document("$set", new Document("score", Float.toString(newScore))));
		}
    }
	
	static void noBoost() {
		FindIterable<Document> cursor = coll.find();
		ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document> map = new ConcurrentHashMap<Integer, edu.carleton.comp4601.dao.Document>();
		MongoCursor<Document> c = cursor.iterator();
		while (c.hasNext()) {
			Document object = c.next();
			String score = object.getString("score");
			float newScore = Float.parseFloat(score) + 1 ;
			coll.updateOne(Filters.eq("id", object.getString("id")), new Document("$set", new Document("score", Float.toString(newScore))));
		}
    }
	
}
