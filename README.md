elasticsearch-java-client-osgi
==============================

OSGI bundle for elasticsearch javaApi(client).

This is a java OSGI bundle for Elaticsearch java client.


==>This bundle creates "singleton" instance of elasticsearch "Transportclient".

==>To configure Elasticsearch addressName and cluster name, edit the values in ES.properties inside the bundle.
	
	
    Syntax:
    ======
    ELASSTICSEARCH_ADDRESS=IP:PORT
    ELASSTICSEARCH_CLUSTERNAME="CLUSTER_NAME"

	Example:
	=======
	ELASSTICSEARCH_ADDRESS=192.168.1.1:9300
	ELASSTICSEARCH_CLUSTERNAME=elasticsearch


**Package Exposed:**

       com.elasticsearch.java.client
            
            
**Methods Exposed:**
  
 **fetchFieldsFromES**
 
		It Fetchs documents es.
		Arguments:
	   		 1) FilteredQueryBuilder FiltertedQueryBulilder with query and filters
	   		 2) fields to be featched from documents(Projection)
			 3) skip value to skip documents
			 4) size no oF documents to be returned
			 5) sortBuildList  list of sort builders
		         6) indexName the index name
		         7) indexTypeName the index type name
		Returns:
			List of Maps with fields requested
		 

 **fetchDocumentsES**
 
		It Fetchs documents es.
				Arguments:
			   		 1) FilteredQueryBuilder FiltertedQueryBulilder with query and filters
					 2) skip value to skip documents
					 3) size no oF documents to be returned
					 4) sortBuildList  list of sort builders
					 5) indexName the index name
					 6) indexTypeName the index type name
				Returns:
					List of JSON strings


VERSIONS 
========

	Elasticsearch version --> 0.90.10
	Lucene version --> 4.0.0
	osgi.framework-->1.3.0
	slf4j-->1.7.2


