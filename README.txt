Murad Berhanu 100996375
Mustapha Attah 100870703

Run the project on tomcat server
go to localhost:8080/COMP4601-SDA/rest/sda on browser to go to the main page

go to other links in assignment description to access other fetures:
-go to localhost:8080/COMP4601-SDA/rest/sda/reset to reset
-go to localhost:8080/COMP4601-SDA/rest/sda/{id} to search for a single document
-go to localhost:8080/COMP4601-SDA/rest/search/{tags} to do distributed
-go to localhost:8080/COMP4601-SDA/rest/sda/pageranks to display list of page ranks
-go to localhost:8080/COMP4601-SDA/rest/sda/boost to do boost on pageranks, then bo back to pageranks page to see updated values
-go to localhost:8080/COMP4601-SDA/rest/sda/noboost to do noboost on pageranks, then bo back to pageranks page to see updated values
-go to localhost:8080/COMP4601-SDA/rest/sda/documents to display all documents
-go to localhost:8080/COMP4601-SDA/rest/query/{tags} for documentcollection for distributed search

-go to (for example) -go to localhost:8080/COMP4601-SDA/rest/sd to get general error page

everything works in browser except delete single document (localhost:8080/COMP4601-SDA/rest/sda/{id}), we used Postman to test that

we were unable to accurately calculate lucene document scores, but boost and noboost still work with these values

to check different URLS to crawl (one of them works by default), first go to reset page and then in CrawlerController.java, the links are on line 89, 90, and 91. comment out/uncomment the link you want to try then go to localhost:8080/COMP4601-SDA/rest/sda to restart
