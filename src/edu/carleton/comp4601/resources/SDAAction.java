package edu.carleton.comp4601.resources;

import edu.carleton.comp4601.dao.Document;
import edu.carleton.comp4601.dao.DocumentCollection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.ArrayList;

public class SDAAction {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    String id;
    DocumentCollection documents;

    public SDAAction(UriInfo uriInfo, Request request, String id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
//        this.documents = documents.;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Document getDocument(){
        for (Document doc : documents.getDocuments()){
            if (doc.getId().equals(id)){ //Warning: 'equals()' between objects of inconvertible types 'Integer' and 'String'
                return doc;
            }
        }
        throw new RuntimeException("Document not found");
    }



}