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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/sda")
public class SearchableDocumentArchive {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private DocumentCollection documents; //placeholder, will change after merge to get from crawled storage
    private String name;

    public SearchableDocumentArchive(){
        name = "COMP4601 Searchable Document Archive: Murad Berhanu and Mustapha Attah";
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
    @GET
    @Path("documents")
    @Produces(MediaType.TEXT_HTML)
    public List<Document> getAllDocumentsHTML(){
        List<Document> lod = new ArrayList<Document>();
        lod.addAll(documents.getDocuments()); //placeholder, will change after merge to get from crawled storage
        return lod;
    }

    //XML representation
    @GET
    @Path("documents")
    @Produces(MediaType.TEXT_XML)
    public List<Document> getAllDocumentsXML(){
        List<Document> lod = new ArrayList<Document>();
        lod.addAll(documents.getDocuments()); //placeholder, will change after merge to get from crawled storage
        return lod;
    }

    //Get a specific document
    //HTML representation
    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public SDAAction getDocumentHTML(@PathParam("id") String id){
        return new SDAAction(uriInfo, request, id);
    }

    //XML representation
    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_XML)
    public SDAAction getDocumentXML(@PathParam("id") String id){
        return new SDAAction(uriInfo, request, id);
    }

    @DELETE
    @Path("{id}")
    public Response deleteDocument (@PathParam("id") int id){
        if (!(documents.getDocuments().get(id) == null)){  // delete condition to be changed after merge
            return Response.status(200, "DELETE SUCCESSFUL").build();
        }
        return Response.status(404, "DOCUMENT NOT FOUND").build();
    }


    /*
    TODO
    */
    @GET
    @Path("search/{tags}")
    @Produces(MediaType.TEXT_HTML)
    public String searchDaTin(@PathParam("tags") String tags) throws SearchException, IOException, ClassNotFoundException {

        ArrayList<Document> docs = new ArrayList<Document>();
        SearchResult searchResult = SearchServiceManager.getInstance().search(tags);

        //perform local search for doc with local storage class

        try {
            searchResult.await(SDAConstants.TIMEOUT, TimeUnit.SECONDS);

        } catch (InterruptedException e){
            e.printStackTrace();
            Response.status(500, "").build();
            return "No Document Found";
        } finally {
            SearchServiceManager.getInstance().reset();
        }

        docs.addAll(searchResult.getDocs());

        return documentsAsString(docs, tags);
    }



    public String documentsAsString (ArrayList<Document> documents, String tags){
        //TODO

        return null;
    }


}