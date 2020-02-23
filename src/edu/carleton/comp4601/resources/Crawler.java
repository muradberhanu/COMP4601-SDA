package edu.carleton.comp4601.resources;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

import com.mongodb.BasicDBObject;
import com.sun.org.apache.xml.internal.resolver.readers.SAXParserHandler;
import edu.carleton.comp4601.resources.GraphClass;
import edu.uci.ics.crawler4j.parser.HtmlContentHandler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.bson.Document;
import org.jgrapht.graph.*;
import org.jsoup.Jsoup;
import org.apache.tika.Tika;

import com.mongodb.client.MongoCollection;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.select.Elements;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class Crawler extends WebCrawler{
    GraphClass graph;
    public DirectedMultigraph<String, DefaultEdge> g;
    MongoCollection<Document> coll;
    //MongoCollection<Document> tikaColl;
    private long crawlTime;
    Directory memoryIndex;

    public Crawler(GraphClass graph, MongoCollection<Document> coll, Directory memoryIndex /*MongoCollection<Document> tikaColl*/) {
        this.graph = graph;
        g = graph.getGraph();
        this.coll = coll;
        this.memoryIndex = memoryIndex;
        //this.tikaColl = tikaColl;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        String parent_url = page.getWebURL().getParentUrl();
        Document doc = new Document();
        Integer docid = new Integer(page.getWebURL().getDocid());

        g.addVertex(page.getWebURL().getURL());
        if(parent_url!=null) {
            g.addVertex(parent_url);
            g.addEdge(page.getWebURL().getURL(), parent_url);
        }

        doc.append("id", docid.toString());
        doc.append("url", page.getWebURL().getURL());
        ArrayList<String> links = new ArrayList<String>();
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData parseData = (HtmlParseData) page.getParseData();
            String html = parseData.getHtml();
            Set<WebURL> outgoingUrls = parseData.getOutgoingUrls();
            for (WebURL outgoingUrl : outgoingUrls) {
                links.add(outgoingUrl.getURL());
            }

            org.jsoup.nodes.Document document = Jsoup.parse(parseData.getHtml());
            doc.append("links", document.select("a[href]").toString());
            doc.append("images", document.select("img[src~=(?i)\\.(png|jpe?g|gif|png)]").toString());
            Elements text = document.getElementsByTag("p");
            text.addAll(document.select("title"));
            text.addAll(document.select("h1"));
            text.addAll(document.select("h2"));
            text.addAll(document.select("h3"));
            text.addAll(document.select("h4"));
            doc.append("title", document.select("title").toString());
            doc.append("text", text.toString());
            doc.append("crawlTime", new Long(crawlTime).toString());
        }
        Metadata metadata = new Metadata();
        try {

            parseTika(page, doc, metadata);
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            memoryIndex = new RAMDirectory();
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(memoryIndex, indexWriterConfig);
            org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
            luceneDoc.add(new TextField("DocID", doc.get("id").toString(), Field.Store.YES));
            luceneDoc.add(new TextField("URL", doc.get("url").toString(), Field.Store.YES));
            //luceneDoc.add(new TextField("IndexedBy", , Field.Store.YES));
            luceneDoc.add(new TextField("Date", doc.get("crawlTime").toString(), Field.Store.YES));
            if(page.getContentType().equals("text/html")) {
                luceneDoc.add(new TextField("Content", doc.get("text").toString() + doc.get("images").toString(), Field.Store.YES));
                doc.append("content", doc.get("text").toString() + doc.get("images").toString());
            }
//            else if(){
//
//            }
            for(String name: metadata.names()){
                luceneDoc.add(new TextField(name, metadata.get(name), Field.Store.YES));
            }
            indexWriter.addDocument(luceneDoc);
            indexWriter.close();

            //Term term = new Term("DocID", "1");
            //Query query = new TermQuery(term);
            List<org.apache.lucene.document.Document> results = searchIndex("DocID", doc.get("id").toString(), doc);

            System.out.print("");
        } catch (IOException | org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
        }
        if(coll.count(doc)==0) {
            coll.insertOne(doc);
        }


    }

    @Override
    public boolean shouldVisit(Page page, WebURL url) {
        crawlTime = System.currentTimeMillis();
        String urlString = url.getURL().toLowerCase();
        return urlString.startsWith("https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/") ||
        		urlString.startsWith("https://sikaman.dyndns.org/courses/4601/");
    }

    public void parseTika(Page page , Document document, Metadata metadata) throws TikaException, SAXException, IOException, ParseException {
        Tika tika = new Tika();
        //Metadata metadata = new Metadata();
        InputStream input = null;
        input = TikaInputStream.get(page.getWebURL().getURL().getBytes(), metadata);
        //org.xml.sax.ContentHandler textHandler = new BodyContentHandler(-1);
        ContentHandler handler = new BodyContentHandler();
        ParseContext context = new ParseContext();
        Parser parser = new AutoDetectParser();
        parser.parse(input, handler, metadata, context);
        //System.out.println("MimeType type =" + page.getContentType());
		Map<String,Object> mongoMetadata = new HashMap<String, Object>();
		for (int i = 0; i < metadata.names().length; i++) {
			String item = metadata.names()[i];
			if(metadata.isMultiValued(item))
				mongoMetadata.put(item, Arrays.asList(metadata.getValues(item)));
			else
				mongoMetadata.put(item, metadata.get(item));
		}
        //org.bson.Document document = new org.bson.Document();//("links",page..toString());
        document.append("metadata", new Document(mongoMetadata));
//        document.append("text", handler.getBodyText());
//        document.append("title", metadata.get(DublinCore.TITLE));
//        System.out.println("metadata:"+handler.getMetaTags());
//        System.out.println("text:"+handler.getBodyText());
//        System.out.println("title:"+metadata.get(DublinCore.TITLE));

    }
    public List<org.apache.lucene.document.Document> searchIndex(String inField, String queryString, Document doc) throws org.apache.lucene.queryparser.classic.ParseException, IOException {
        Query query = new QueryParser(inField, new StandardAnalyzer())
                .parse(queryString);
        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 100);
        List<org.apache.lucene.document.Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }
        doc.append("score", Float.toString(topDocs.scoreDocs[0].score));
        return documents;
    }

}