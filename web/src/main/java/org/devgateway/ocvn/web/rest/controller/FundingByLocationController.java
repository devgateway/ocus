/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.ocvn.web.rest.controller;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 
 * @author mpostelnicu
 *
 */
@RestController
public class FundingByLocationController extends GenericOcvnController {
	
	public class CustomProjectionOperation implements AggregationOperation {
	    private DBObject operation;

	    public CustomProjectionOperation (DBObject operation) {
	        this.operation = operation;
	    }

	    @Override
	    public DBObject toDBObject(AggregationOperationContext context) {
	        return context.getMappedObject(operation);
	    }
	}
	

	@RequestMapping("/api/plannedFundingByLocation/{year:^\\d{4}$}")
	public List<DBObject> plannedFundingByLocation(@PathVariable Integer year) {

		DBObject vars=new BasicDBObject();
		vars.put("numberOfLocations", new BasicDBObject("$size","$planning.locations"));
		vars.put("planningBudget","$planning.budget.amount.amount");
		DBObject in=new BasicDBObject("$divide",Arrays.asList("$$planningBudget","$$numberOfLocations"));			
		
		DBObject let = new BasicDBObject();
		let.put("vars", vars);
		let.put("in",in);
				
		DBObject dividedTotal=new BasicDBObject("$let",let);
		
		DBObject project=new BasicDBObject();
		project.put("planning.locations", 1);
		project.put("planning.budget.amount.amount",1);
		project.put("dividedTotal",dividedTotal);
		
		Aggregation agg = newAggregation(
				match(where("planning").exists(true)
						.and("planning.locations").exists(true).ne(null)
						.and("planning.bidPlanProjectDateApprove").gte(getStartDate(year)).lte(getEndDate(year))),
				new CustomProjectionOperation(new BasicDBObject("$project",project)),
				unwind("$planning.locations"),
				group("planning.locations").sum("$dividedTotal").as("totalPlannedAmount"));
	
		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> tagCount = results.getMappedResults();
		return tagCount;

	}

}