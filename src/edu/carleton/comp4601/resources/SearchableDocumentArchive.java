package edu.carleton.comp4601.resources;

//import com.sun.tools.corba.se.idl.PragmaEntry;
import edu.carleton.comp4601.dao.Document;
import edu.carleton.comp4601.dao.DocumentCollection;
import edu.carleton.comp4601.utility.SDAConstants;
import edu.carleton.comp4601.utility.SearchException;
import edu.carleton.comp4601.utility.SearchResult;
import edu.carleton.comp4601.utility.SearchServiceManager;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.mongodb.BasicDBObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Path("/sda")
public class SearchableDocumentArchive {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private DocumentCollection documents = new DocumentCollection(); //placeholder, will change after merge to get from crawled storage
    private String name;
    public static DocumentsMongoDb documentsMongoDb;

    public SearchableDocumentArchive(){
        name = "COMP4601 Searchable Document Archive: Murad Berhanu and Mustapha Attah";
        documentsMongoDb = DocumentsMongoDb.getInstance();
        ArrayList<edu.carleton.comp4601.dao.Document> documentsList = new ArrayList<edu.carleton.comp4601.dao.Document>();
        ConcurrentHashMap<String, edu.carleton.comp4601.dao.Document> documentMap = documentsMongoDb.getDocuments();
		for (edu.carleton.comp4601.dao.Document e : documentMap.values()) {
		    documentsList.add(e);
		}
		if(documentMap!=null) {
			documents.setDocuments(documentsList);
		}
        //documentsMongoDb.coll.db.
    }

    @GET
    public String printName(){
        return name;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXML(){
        return "<?xml version=\"1.0\"?>" + "<sda> " + name + " </sda>";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/TestService")
    public String info(){
        return "This is the testservice";
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHTML(){
        return "<html> " +
                "<title>" + name + "</title>" +
                "<body><h1>" + name + "</body></h1>" +
                "</html> ";
    }


    // retrieves all documents
    //HTML representation
//    @GET
//    @Path("documents")
//    @Produces(MediaType.TEXT_HTML)
//    public List<Document> getAllDocumentsHTML(){
//        List<Document> lod = new ArrayList<Document>();
//        lod.addAll(documents.getDocuments()); //placeholder, will change after merge to get from crawled storage
//        return lod;
//    }

    @GET
	@Path("documents")
	@Produces(MediaType.TEXT_HTML)
	public String getDocumentsHTML() {
		List<Document> lod = new ArrayList<Document>();
		lod.addAll(documentsMongoDb.getDocuments().values());
		String htmlString = "";
		for(Document doc: documents.getDocuments()) {
			htmlString += "URL: "+doc.getUrl()
	    			+"<br> Name: "+doc.getName()
	    			+"<br> ID: "+doc.getId()
	    			+"<br> Score: "+doc.getScore()
	    			+"<br> Content: "+doc.getContent();
	    	if(!documentsMongoDb.getMongoDocument(doc.getId()).getString("links").isEmpty()) {
	    		htmlString+="<br> links: "+documentsMongoDb.getMongoDocument(doc.getId()).getString("links");
	    	}
	    	if(!documentsMongoDb.getMongoDocument(doc.getId()).getString("images").isEmpty()) {
	    		htmlString+="<br> images: "+documentsMongoDb.getMongoDocument(doc.getId()).getString("images");
	    	}
	    	if(!documentsMongoDb.getMongoDocument(doc.getId()).get("metadata").toString().equals("")) {
	    		htmlString+="<br> metadata: "+documentsMongoDb.getMongoDocument(doc.getId()).get("metadata").toString();
	    	}
	    	htmlString+= "<br><br><br><hr>";
		}
		return htmlString;
	}
    
    @GET
	@Path("documents")
	@Produces(MediaType.TEXT_XML)
	public DocumentCollection getDocuments() {
		return documents;
	}
    
    @GET
	@Path("boost")
	@Produces(MediaType.TEXT_PLAIN)
	public String doBoost() {
    	documentsMongoDb.boost();
    	ArrayList<edu.carleton.comp4601.dao.Document> documentsList = new ArrayList<edu.carleton.comp4601.dao.Document>();
        ConcurrentHashMap<String, edu.carleton.comp4601.dao.Document> documentMap = documentsMongoDb.getDocuments();
		for (edu.carleton.comp4601.dao.Document e : documentMap.values()) {
		    documentsList.add(e);
		}
		if(documentMap!=null) {
			documents.setDocuments(documentsList);
		}
		return "boost completed";
	}
    
    @GET
	@Path("noboost")
	@Produces(MediaType.TEXT_PLAIN)
	public String doNoBoost() {
    	documentsMongoDb.noBoost();
    	ArrayList<edu.carleton.comp4601.dao.Document> documentsList = new ArrayList<edu.carleton.comp4601.dao.Document>();
        ConcurrentHashMap<String, edu.carleton.comp4601.dao.Document> documentMap = documentsMongoDb.getDocuments();
		for (edu.carleton.comp4601.dao.Document e : documentMap.values()) {
		    documentsList.add(e);
		}
		if(documentMap!=null) {
			documents.setDocuments(documentsList);
		}
		return "noboost completed";
	}

    //Get a specific document
    //HTML representation
    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public String getDocumentHTML(@PathParam("id") String id){
    	SDAAction action = new SDAAction(uriInfo, request, id, documentsMongoDb);
    	String documentHTML = "URL: "+action.getDocument().getUrl()
    			+"<br> Name: "+action.getDocument().getName()
    			+"<br> ID: "+action.getDocument().getId()
    			+"<br> Score: "+action.getDocument().getScore()
    			+"<br> Content: "+action.getDocument().getContent();
    	if(!documentsMongoDb.getMongoDocument(Integer.parseInt(id)).getString("links").isEmpty()) {
    		documentHTML+="<br> links: "+documentsMongoDb.getMongoDocument(Integer.parseInt(id)).getString("links");
    	}
    	if(!documentsMongoDb.getMongoDocument(Integer.parseInt(id)).getString("images").isEmpty()) {
    		documentHTML+="<br> images: "+documentsMongoDb.getMongoDocument(Integer.parseInt(id)).getString("images");
    	}
    	if(!documentsMongoDb.getMongoDocument(Integer.parseInt(id)).get("metadata").toString().equals("")) {
    		documentHTML+="<br> metadata: "+documentsMongoDb.getMongoDocument(Integer.parseInt(id)).get("metadata").toString();
    	}
    	//CrawlerController.getAllDocuments(documentsMongoDb.coll);
        return documentHTML;
    }

    //XML representation
    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_XML)
    public Document getDocumentXML(@PathParam("id") String id){
    	SDAAction action = new SDAAction(uriInfo, request, id, documentsMongoDb);
        return action.getDocument();
    }

//    @DELETE
//    @Path("{id}")
//    public Response deleteDocument (@PathParam("id") String id){
//    	SDAAction action = new SDAAction(uriInfo, request, id, documentsMongoDb);
//        return action.deleteDocument();
//        if (!(documents.getDocuments().get(id) == null)){  // delete condition to be changed after merge
//            return Response.status(200, "DELETE SUCCESSFUL").build();
//        }
//        return Response.status(404, "DOCUMENT NOT FOUND").build();
//    }

    @GET
    @Path("reset")
    @Produces(MediaType.TEXT_HTML)
    public String reset() {
    	BasicDBObject document = new BasicDBObject();
    	documentsMongoDb.coll.deleteMany(document);
    	documents.setDocuments(null);
    	documentsMongoDb = null;
    	return "successful reset";
    	
    }
    
    @GET
	@Path("pagerank")
	@Produces(MediaType.TEXT_HTML)
	public String getPageranks() {
    	String htmlString = "<table style=\"border: 1px solid black;\">";
    	for(Document doc: documents.getDocuments()) {
    		htmlString += "<tr><td style=\"border: 1px solid black;\">" + doc.getUrl() + "</td><td style=\"border: 1px solid black;\">" + doc.getScore().toString() + "</td></tr>";
    	}
    	htmlString += "</table>";
    	return htmlString;
	}
    
    @GET
	@Path("pagerank")
	@Produces(MediaType.TEXT_XML)
	public String getPageranksXml() {
    	String htmlString = "<table style=\"border: 1px solid black;\">";
    	for(Document doc: documents.getDocuments()) {
    		htmlString += "<tr><td style=\"border: 1px solid black;\">" + doc.getUrl() + "</td><td style=\"border: 1px solid black;\">" + doc.getScore().toString() + "</td></tr>";
    	}
    	htmlString += "</table>";
    	return htmlString;
	}

    /*
    TODO
//    */
    @GET
    @Path("search/{tags}")
    @Produces(MediaType.TEXT_HTML)
    public String searchTagsHtml(@PathParam("tags") String tags) throws SearchException, IOException, ClassNotFoundException {

    	ArrayList<Document> docs = new ArrayList<Document>();
        SearchServiceManager.getInstance().start();
        SearchResult searchResult = SearchServiceManager.getInstance().search(tags);

        String[] splitTags = tags.split("\\+");
        for (Document doc: documents.getDocuments()) {
        	boolean check = false;
        	for(String tag: splitTags) {
        		check = false;
	        	if(doc.getContent().contains(tag)) {
	        		check = true;
	        	}
        	}
        	if(check == true) {
        		docs.add(doc);
        	}
        }

        try {
            searchResult.await(SDAConstants.TIMEOUT, TimeUnit.SECONDS);

        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            SearchServiceManager.getInstance().reset();
        }

        docs.addAll(searchResult.getDocs());

        return documentsAsString(docs, tags);
    }
    @GET
    @Path("search/{tags}")
    @Produces(MediaType.TEXT_XML)
    public ArrayList<Document> searchTagsXml(@PathParam("tags") String tags) throws SearchException, IOException, ClassNotFoundException {

        ArrayList<Document> docs = new ArrayList<Document>();
        SearchServiceManager.getInstance().start();
        SearchResult searchResult = SearchServiceManager.getInstance().search(tags);

        String[] splitTags = tags.split("\\+");
        for (Document doc: documents.getDocuments()) {
        	boolean check = false;
        	for(String tag: splitTags) {
        		check = false;
	        	if(doc.getContent().contains(tag)) {
	        		check = true;
	        	}
        	}
        	if(check == true) {
        		docs.add(doc);
        	}
        }

        try {
            searchResult.await(SDAConstants.TIMEOUT, TimeUnit.SECONDS);

        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            SearchServiceManager.getInstance().reset();
        }

        docs.addAll(searchResult.getDocs());

        return docs;
    }
    
    @GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		long count = documentsMongoDb.size();
		return String.valueOf(count);
	}



    public String documentsAsString (ArrayList<Document> documents, String tags){
        //TODO
    	String htmlString = "<h1>"+tags+"</h1><table style=\"border: 1px solid black;\">";
    	for(Document doc: documents) {
    		htmlString += "<tr><td style=\"border: 1px solid black;\">" + "<a href=" + doc.getUrl() +">" + doc.getUrl() + "</a>"+ "</td><td style=\"border: 1px solid black;\">" + doc.getScore().toString() + "</td></tr>";
    	}
    	htmlString += "</table>";
        return htmlString;
    }


}