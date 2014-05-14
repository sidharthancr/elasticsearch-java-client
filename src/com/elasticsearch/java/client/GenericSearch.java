package com.elasticsearch.java.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticsearch.java.client.utill.SearchEngineConstants;




/**
 * The Class GenericSearch.
 *
 * @author sidharthan.r
 * @version 1.0.0
 * @since 1.0.0
 */

public abstract class GenericSearch {

    /** The logger. */
    private Logger logger = LoggerFactory.getLogger(GenericSearch.class);

    /** The s esclient. */
    private static Client sESCLIENT;

     /**
      * This method returns ESclient instance.
      *
      * @return the es client
      * ESClient instance
      */
	public final Client getEsClient() {

		if (sESCLIENT == null) {
			sESCLIENT = getES();
		}
		return sESCLIENT;
	}

	/**
	 * Sets the es client.
	 *
	 * @param esClient the new es client
	 */
	public final void setEsClient(final Client esClient) {

		GenericSearch.sESCLIENT = esClient;
	}

	/**
	 * Gets the es.
	 *
	 * @return the es
	 */
	private Client getES() {
		try {
			logger.info("Getting EsClient instance in getEsClient() method");
			final Map<String, String> esProperties =getESHostValue();
			String esHosts= esProperties.get(SearchEngineConstants.ES_ADDRESSS);
			logger.info("EsHostss value in Properties:" + esHosts);
			// Setting cluster name of ES Server
			final Builder settings = ImmutableSettings.settingsBuilder().put("cluster.name", esProperties.get(SearchEngineConstants.ES_CLUSTERNAME));
			settings.put(SearchEngineConstants.PATH_CONF, SearchEngineConstants.NAMES_TXT);
			settings.put(SearchEngineConstants.SNIFF, true);
			final TransportClient transportClient = new TransportClient(settings);
			if (!esHosts.trim().isEmpty()) {
				final String[] hosts = esHosts.split(",");
				for (int i = 0; i < hosts.length; i++) {
					int port;
					String ip;
					final String aESHost = hosts[i];
					if (aESHost != null) {
						final String[] hostInfo = aESHost.split(":");
						if (hostInfo.length > 1) {
							ip = hostInfo[0];
							port = Integer.parseInt(hostInfo[1]);
							transportClient.addTransportAddress(new InetSocketTransportAddress(ip, port));
						}
					}
				}
			}

			return transportClient;
		} catch (Exception e) {
			logger.error("Error in TransportClient", e);
			return null;
		}

	}

	/**
	 * Gets the eS host value.
	 *
	 * @return the eS host value
	 */
	private Map<String, String> getESHostValue() {
		Map<String, String> esProperties= new HashMap<String,String>();
		try {
			Properties prop = new Properties();
		    InputStream in = GenericSearch.class
		            .getResourceAsStream(SearchEngineConstants.ES_PROPERTIES);
		    prop.load(in);
			esProperties.put(SearchEngineConstants.ES_ADDRESSS,prop.getProperty(SearchEngineConstants.ES_ADDRESSS));
			esProperties.put(SearchEngineConstants.ES_CLUSTERNAME,prop.getProperty(SearchEngineConstants.ES_CLUSTERNAME));
	        in.close();
	        return esProperties;
		} catch (Exception e) {
			logger.error("Error while accessgind ES propertyFile",e);
		} 
		return null;
	}

	/**
	 * Query builder.
	 *
	 * @param indexName the index name
	 * @param indexType the index type
	 * @param queryBuilder the query builder
	 * @param fields the fields
	 * @param skipValue the skip value
	 * @param limit the limit
	 * @param orderByList the order by list
	 * @return the search response
	 */
	public final SearchResponse queryBuilder(final String indexName, final String indexType, final FilteredQueryBuilder queryBuilder,
			final List<String> fields, final int skipValue, final int limit, final List<SortBuilder> orderByList) {
		SearchRequestBuilder query;
		getEsClient();
		logger.info("Entertin into queryBuilder in generic search with parameters :" + " indexName:" + indexName
				+ " indexType:" + indexType + " fields:" + fields + " skipValue:" + skipValue + " limit:" + limit
				+ " orderByList:" + orderByList);

		try {
			if (indexName != null) {
				query = sESCLIENT.prepareSearch(indexName);
				if (indexType != null) {
					query = query.setTypes(indexType);
				}
			} 
			else {
				query = sESCLIENT.prepareSearch();
			}

			if (queryBuilder != null) {
				query.setQuery(queryBuilder);
			}
			if (fields != null) {
				final String[] fieldS = fields.toArray(new String[fields.size()]);
				query.addFields(fieldS);
			}
			if (orderByList != null && orderByList.size() > 0) {
				for (SortBuilder orderBy : orderByList) {
					query.addSort(orderBy);
				}
			}
			if (skipValue != 0) {
				query.setFrom(skipValue);
			}
			if (limit != 0) {
				query.setSize(limit);
			}
			else {
				query.setSize(SearchEngineConstants.BATCHSIZE);
			}
			if (logger.isDebugEnabled()) {
				logger.info("Query:" + query);
			}
		
			final SearchResponse searchResponse = query.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setScroll(new TimeValue(SearchEngineConstants.BATCHSIZE)).execute().actionGet();
			logger.info("End of queryBuilder in generic search");
			return searchResponse;
		} 
		catch (Exception e) {
			logger.error("Es_Error", e);
		}
		return null;

	}
	
	
	/**
	 * Fetchfields from es.
	 *
	 * @param FilteredQueryBuilder the builder
	 * @param fields to be featched the fields
	 * @param from the from
	 * @param size the size
	 * @param sortBuildList the sort build list
	 * @param indexName the index name
	 * @param indexTypeName the index type name
	 * @return the listOfMap
	 */
	public final List<Map<String, Object>> fetchFieldsFromES(final FilteredQueryBuilder builder, final List<String> fields, final int from, final int size,
			final List<SortBuilder> sortBuildList,final String indexName,final String indexTypeName) {
		SearchResponse response = queryBuilder(indexName,indexTypeName, builder,
				fields, from, size, sortBuildList);
		final List<Map<String,Object>> listOfMap= new ArrayList<Map<String,Object>>();
		final Map<String,Object> keyValuePair= new HashMap<String,Object>();
		Object tempString;
		if (response != null) {
			while (true) {
				for (SearchHit hit : response.getHits()) {
					final Map<String, SearchHitField> fieldsMap = hit.getFields();
					if (fieldsMap != null) {
						for(String field:fields)
						{
							if (fieldsMap.get(field) != null) {
								tempString = fieldsMap.get(field).getValue();
								keyValuePair.clear();
								keyValuePair.put(field, tempString);
							}
						}
						listOfMap.add(keyValuePair);
					}
				}
				if (response.getHits().getHits().length == 0) {
					break;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("If matched documents are more than 30000 then it will fetch docmument in batches size(30000): "
							+ builder);
				}
				response = getEsClient().prepareSearchScroll(response.getScrollId())
						.setScroll(new TimeValue(SearchEngineConstants.BATCHSIZE)).execute().actionGet();
			}

		}
		return listOfMap;
	}


	/**
	 * Fetch documents es.
	 *
	 * @param FilteredQueryBuilder the builder
	 * @param from the from
	 * @param size the size
	 * @param sortBuildList the sort build list
	 * @param indexName the index name
	 * @param indexTypeName the index type name
	 * @return the Array of documents
	 */
	public final List<String> fetchDocumentsES(final FilteredQueryBuilder builder, final int from, final int size,
			final List<SortBuilder> sortBuildList,final String indexName,final String indexTypeName) {
		SearchResponse response = queryBuilder(indexName,indexTypeName, builder,
				null, from, size, sortBuildList);
		List<String> listOfDocuments= new ArrayList<String>();
		if (response != null) {
			while (true) {
				for (SearchHit hit : response.getHits()) {
					listOfDocuments.add(hit.sourceAsString());					
				}
				if (response.getHits().getHits().length == 0) {
					break;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("If matched documents are more than 30000 then it will fetch docmument in batches size(30000): "
							+ builder);
				}
				response = getEsClient().prepareSearchScroll(response.getScrollId())
						.setScroll(new TimeValue(SearchEngineConstants.BATCHSIZE)).execute().actionGet();
			}

		}
		return listOfDocuments;
	}
}
