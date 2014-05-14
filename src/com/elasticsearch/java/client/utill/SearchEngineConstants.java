package com.elasticsearch.java.client.utill;

/**
 * The Class SearchEngineConstants.
 * 
 * @author sidharthan.r
 */
public final class SearchEngineConstants {

	
	
	/**
	 * BATCH size to fetch results
	 */
	public static final int BATCHSIZE = 30000;

	
	/**
	 * Path of ES configuration file.
	 */
	public static final String PATH_CONF = "path.conf";

	/**
	 * Path of names.txt file.
	 */
	public static final String NAMES_TXT = "/com.elasticsearch.java.client/";

	/**
	 * Es client SNIFF property.
	 */
	public static final String SNIFF = "client.transport.sniff";
	/**
	 * IP constants.
	 */
	public static final String IP = "ip";
	/**
	 * ES.properties filename
	 */
	public static final String ES_PROPERTIES = "ES.properties";
	
	/**
	 * Elasticsearch IP and port.
	 */
	public static final String ES_ADDRESSS = "ELASSTICSEARCH_ADDRESS";
	/**
	 * Elasticsearch CLustername
	 */
	public static final String ES_CLUSTERNAME = "ELASSTICSEARCH_CLUSTERNAME";


	
	private SearchEngineConstants() {
		// Private Cons
	}
}
