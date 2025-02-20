package edu.carleton.comp4601.resources;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.DBCursor;
import edu.carleton.comp4601.resources.GraphClass;
import edu.carleton.comp4601.resources.Crawler;
import edu.carleton.comp4601.resources.GraphLayoutVisualizer;
import edu.carleton.comp4601.resources.Marshaller;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.bson.Document;
import org.jgrapht.graph.DefaultEdge;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.jgrapht.graph.Multigraph;

public class CrawlerController extends CrawlController{
	
	static GraphClass graph = new GraphClass();

    public CrawlerController(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer)
            throws Exception {
        super(config, pageFetcher, robotstxtServer);
        // TODO Auto-generated constructor stub
    }
    
    

     static String getAllDocuments(MongoCollection<Document> col) {
        System.out.println("Fetching all documents from the collection");
        String graphString = null;
        // Performing a read operation on the collection.
        FindIterable<Document> fi = col.find();
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
            return graphString;
        }
    }

    public static MongoCollection<Document> startCrawl(MongoCollection<Document> coll) throws Exception {
    	//if(coll.count() == 0) {
        File crawlStorage = new File("crawler4jstorage");
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorage.getAbsolutePath());
        config.setIncludeBinaryContentInCrawling(true);
        config.setPolitenessDelay(10);
        //config.setMaxDepthOfCrawling(1);
        config.setMaxPagesToFetch(2000);

        int numCrawlers = 1;

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer= new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        controller.addSeed("https://sikaman.dyndns.org/courses/4601/resources/N-0.html");
        controller.addSeed("https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/handouts/");
        controller.addSeed("https://www.crawler-test.com");

        CrawlController.WebCrawlerFactory<Crawler> factory = () -> new Crawler(graph, coll/*tikaColl*/);
        controller.start(factory, numCrawlers);
        
        //FindIterable<Document> iterable = db.getCollection("crawledSites").find();

        //System.out.println(graph.getGraph().toString());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~");
//        for(DefaultEdge e : graph.getGraph().edgeSet()){
//            System.out.println(graph.getGraph().getEdgeSource(e) + " --> " + graph.getGraph().getEdgeTarget(e));
//        }


        Document doc = new Document("graph", Marshaller.serializeObject(graph.getGraph()));
        //byte[] serializedGraph = Marshaller.serializeObject(graph);
        //doc.append("graph: ", serializedGraph);
//        if(coll.count(doc)==0) {
//            graphColl.insertOne(doc);
//        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!");
        getAllDocuments(coll);
        //getAllDocuments(tikaColl);
        //String graphString = getAllDocuments(graphColl);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!");
        //GraphLayoutVisualizer.visualizeGraph(graph.getGraph());

//		Multigraph<Vertex, DefaultEdge> graph2 = (Multigraph<Vertex, DefaultEdge>) Marshaller.deserializeObject(graphString.getBytes());
//		GraphLayoutVisualizer.visualizeGraph(graph2);

        //mongoClient.close();
    //}
        return coll;
    }
}