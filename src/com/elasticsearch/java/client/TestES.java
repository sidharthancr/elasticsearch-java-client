package com.elasticsearch.java.client;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class TestES extends GenericSearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	TestES es= new TestES();
	FilteredQueryBuilder builder=QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.matchAllFilter());
	List<String> fields= new ArrayList<String>();
	fields.add("name");
	System.out.println(es.fetchFieldsFromES(builder,fields,0,0,null,"appviewx","ro"));

	}

}
