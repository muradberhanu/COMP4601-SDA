package edu.carleton.comp4601.resources;

import edu.carleton.comp4601.dao.Document;
import edu.carleton.comp4601.dao.DocumentCollection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;

public class SDAAction {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    int id;
    DocumentCollection documents;
    DocumentsMongoDb documentsMongoDb;
    
//    static MongoDatabase db;
//	static MongoCollection<org.bson.Document> coll;
//    //MongoCollection<Document> tikaColl = db.getCollection("tikaCrawling");
//	static MongoCollection<org.bson.Document> graphColl;

    
    public SDAAction(UriInfo uriInfo, Request request, String id, DocumentsMongoDb documentsMongoDb) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = new Integer(id);
        this.documentsMongoDb = documentsMongoDb;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public Document getDocument(){
    	Document d = documentsMongoDb.find(id);
		if (d == null) {
			throw new RuntimeException("No such document: " + id);
		}
		return d;
        //throw new RuntimeException("Document not found");
    }
    
    @DELETE
	public void deleteDocument() {
		if (!documentsMongoDb.deleteDocument(Integer.toString(id)))
			throw new RuntimeException("Account " + id + " not found");
	}
    
    @GET
    @Produces(MediaType.TEXT_XML)
    public Document getAllDocuments(){
    	Document d = documentsMongoDb.find(id);
		if (d == null) {
			throw new RuntimeException("No such document: " + id);
		}
		return d;
        //throw new RuntimeException("Document not found");
    }
    
//    @GET
//    @Produces(MediaType.TEXT_HTML)
//    public Document getDocumentHtml(){
//    	Document d = documentsMongoDb.find(id);
//		if (d == null) {
//			throw new RuntimeException("No such document: " + id);
//		}
//		return d;
//        //throw new RuntimeException("Document not found");
//    }
    
//    public void initializeMongoDocuments() {
//    	try {
//    		CrawlerController controller = null;
//			controller.startCrawl();
//			this.db = controller.db;
//			this.coll = controller.coll;
//			this.graphColl = controller.graphColl;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }



}