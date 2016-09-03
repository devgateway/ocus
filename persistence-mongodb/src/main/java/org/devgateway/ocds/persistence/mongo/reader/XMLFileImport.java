package org.devgateway.ocds.persistence.mongo.reader;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.OcdsSchemaValidatorService;
import org.devgateway.toolkit.persistence.mongo.spring.MongoTemplateConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.scheduling.annotation.Async;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author idobre
 * @since 6/27/16
 */
public abstract class XMLFileImport implements XMLFile {
    private static final Logger LOGGER = Logger.getLogger(XMLFileImport.class);

    private static final int VALIDATION_BATCH = 5000;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoTemplateConfiguration mongoTemplateConfiguration;

    @Autowired
    private OcdsSchemaValidatorService validationService;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Async
    public void process(final InputStream inputStream, final Boolean purgeDatabase, final Boolean validateData)
            throws IOException, SAXException {
        clearAllCaches();

        if (purgeDatabase) {
            purgeDatabase();
        }

        DigesterLoader digesterLoader = DigesterLoader.newLoader(getAbstractRulesModule());
        Digester digester = digesterLoader.newDigester();

        // Push this object onto Digester's stack to handle object save operation (call saveRelease method)
        digester.push(this);
        digester.parse(inputStream);

        if (purgeDatabase) {
            postImportStage();
        }

        if (validateData) {
            validateData();
        }

        logMessage("<b>IMPORT PROCESS COMPLETED.</b>");
    }

    @Async
    public void process(final File file, final Boolean purgeDatabase, final Boolean validateData)
            throws IOException, SAXException {
        process(new FileInputStream(file), purgeDatabase, validateData);
    }

    /**
     * This function should be called on 'end' event when we have a complete Release object.
     *
     * @param obj
     */
    public void saveRelease(final Object obj) {
        if (obj instanceof Release) {
            Release release = processRelease((Release) obj);

            if (release.getId() == null) {
                releaseRepository.insert(release);
            } else {
                releaseRepository.save(release);
            }
        }
    }

    /**
     * Delete all data without dropping indexes
     */
    private void purgeDatabase() {
        logMessage("Purging database...");

        ScriptOperations scriptOps = mongoTemplate.scriptOps();
        ExecutableMongoScript echoScript = new ExecutableMongoScript("db.dropDatabase()");
        scriptOps.execute(echoScript);

        logMessage("Database purged.");

        // create indexes that affect import performance
        mongoTemplateConfiguration.createMandatoryImportIndexes();
    }

    public void validateData() {
        logMessage("<b>RUNNING SCHEMA VALIDATION.</b>");

        int pageNumber = 0;
        int processedCount = 0;

        Page<Release> page;
        do {
            page = releaseRepository.findAll(new PageRequest(pageNumber++, VALIDATION_BATCH));
            page.getContent().parallelStream().map(rel -> validationService.validate(rel))
                    .filter(r -> !r.getReport().isSuccess()).forEach(r -> logMessage(
                    "<font style='color:red'>OCDS Validation Failed: " + r.toString() + "</font>"));
            processedCount += page.getNumberOfElements();
            logMessage("Validated " + processedCount + " releases");
        } while (!page.isLast());

        logMessage("<b>SCHEMA VALIDATION COMPLETE.</b>");
    }

    /**
     * This is invoked if the database has been purged
     */
    private void postImportStage() {
        // post-init indexes
        mongoTemplateConfiguration.createPostImportStructures();
    }

    /**
     * Simple method that gets all cache names and invokes {@link Cache#clear()} on all
     */
    public void clearAllCaches() {
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(c -> cacheManager.getCache(c).clear());
        }
    }

    /**
     * Function used to post-process a release in case we need to append new information, like ocid.
     *
     * @param release
     * @return
     */
    protected abstract Release processRelease(final Release release);

    protected abstract AbstractRulesModule getAbstractRulesModule();
}

