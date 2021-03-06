package org.devgateway.toolkit.persistence.mongo.spring;

import org.apache.commons.io.IOUtils;
import org.devgateway.ocds.persistence.mongo.DefaultLocation;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.flags.FlagsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

@Configuration
public class MongoTemplateConfiguration {

    private final Logger logger = LoggerFactory.getLogger(MongoTemplateConfiguration.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createMandatoryImportIndexes() {
        //mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("planning.budget.projectID", Direction.ASC));
        //mongoTemplate.indexOps(Location.class).ensureIndex(new Index().on("description", Direction.ASC));
        mongoTemplate.indexOps(Organization.class).ensureIndex(new Index().on("identifier._id", Direction.ASC));
        mongoTemplate.indexOps(Organization.class)
                .ensureIndex(new Index().on("additionalIdentifiers._id", Direction.ASC));
        mongoTemplate.indexOps(Organization.class).ensureIndex(
                new Index().on("roles", Direction.ASC));
        mongoTemplate.indexOps(Organization.class).ensureIndex(new Index().on("name", Direction.ASC).unique());
        mongoTemplate.indexOps(DefaultLocation.class).ensureIndex(new Index().on("description", Direction.ASC));
        logger.info("Added mandatory Mongo indexes");
    }

    public void createCorruptionFlagsIndexes() {
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("flags.totalFlagged", Direction.ASC));

        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("flags.flaggedStats.type", Direction.ASC)
                .on("flags.flaggedStats.count", Direction.ASC)
        );

        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("flags.eligibleStats.type", Direction.ASC)
                .on("flags.eligibleStats.count", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I038_VALUE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I007_VALUE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I004_VALUE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I077_VALUE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I180_VALUE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I019_VALUE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I002_VALUE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I085_VALUE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on(FlagsConstants.I171_VALUE, Direction.ASC));
    }

    @PostConstruct
    public void mongoPostInit() {
        createMandatoryImportIndexes();
        createPostImportStructures();
    }

    public void createPostImportStructures() {

        createCorruptionFlagsIndexes();

        // initialize some extra indexes
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("ocid", Direction.ASC).unique());

        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("tender.procurementMethod", Direction.ASC));
        mongoTemplate.indexOps(Release.class)
                .ensureIndex(new Index().on("tender.procurementMethodRationale", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("tender.status", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("awards.status", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("awards.suppliers._id", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("awards.date", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("awards.value.amount", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("tender.value.amount", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("tender.numberOfTenderers", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().on("tender.submissionMethod", Direction.ASC));
        mongoTemplate.indexOps(Release.class)
                .ensureIndex(new Index().on(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE, Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index()
                .on(MongoConstants.FieldNames.TENDER_PERIOD_END_DATE, Direction.ASC));
        mongoTemplate.indexOps(Release.class)
                .ensureIndex(new Index().on("tender.items.classification._id", Direction.ASC));
        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().
                on("tender.items.deliveryLocation._id", Direction.ASC));

        mongoTemplate.indexOps(Release.class).ensureIndex(new Index().
                on("tender.items.deliveryLocation.geometry.coordinates", Direction.ASC));

        mongoTemplate.indexOps(Organization.class).ensureIndex(new TextIndexDefinitionBuilder().onField("name")
                .onField("id").onField("additionalIdentifiers._id").build());

        logger.info("Added extra Mongo indexes");

        ScriptOperations scriptOps = mongoTemplate.scriptOps();

        // add script to calculate the percentiles endpoint
        URL scriptFile = getClass().getResource("/tenderBidPeriodPercentilesMongo.js");
        try {
            String scriptText = IOUtils.toString(scriptFile);
            ExecutableMongoScript script = new ExecutableMongoScript(scriptText);
            scriptOps.register(new NamedMongoScript("tenderBidPeriodPercentiles", script));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add general mongo system helper methods
        URL systemScriptFile = getClass().getResource("/mongoSystemScripts.js");
        try {
            String systemScriptFileText = IOUtils.toString(systemScriptFile);
            ExecutableMongoScript script = new ExecutableMongoScript(systemScriptFileText);
            scriptOps.execute(script);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
