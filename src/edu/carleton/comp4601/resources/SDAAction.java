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

    
    public SDAAction(UriInfo uriInfo, Request request, String id, DocumentsMongoDb documentsMongoDb) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = new Integer(id);
        this.documentsMongoDb = documentsMongoDb;
    }
    
    public SDAAction(UriInfo uriInfo, Request request, String id, DocumentsMongoDb documentsMongoDb, DocumentCollection documents) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = new Integer(id);
        this.documentsMongoDb = documentsMongoDb;
        this.documents = documents;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public Document getDocument(){
    	Document d = documentsMongoDb.find(id);
		if (d == null) {
			throw new RuntimeException("No such document: " + id);
		}
		return d;
    }
    
    @DELETE
	public void deleteDocument() {
		if (!documentsMongoDb.deleteDocument(Integer.toString(id)))
			throw new RuntimeException("Account " + id + " not found");
		else {
			for(Document doc: documents.getDocuments()) {
				if(doc.getId() == id) {
					documents.getDocuments().remove(doc);
				}
			}
		}
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


}