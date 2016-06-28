package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.reader.XMLFileImport;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author idobre
 * @since 6/28/16
 */
public class USASpendingXMLImport extends XMLFileImport implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(USASpendingXMLImport.class);

    private final StringBuffer msgBuffer = new StringBuffer();

    public USASpendingXMLImport(ReleaseRepository releaseRepository, InputStream inputStream) {
        super(releaseRepository, inputStream);
    }

    public USASpendingXMLImport(ReleaseRepository releaseRepository, File file) throws IOException {
        super(releaseRepository, file);
    }

    @Override
    protected Release processRelease(Release release) {
        if (Integer.parseInt(release.getId()) % 1000 == 0) {
           LOGGER.error(">>>>> " + release.getId());
        }
        return release;
    }

    @Override
    protected AbstractRulesModule getAbstractRulesModule() {
        return new USASpendingRulesModule();
    }

    @Override
    public void logMessage(String message) {

    }

    @Override
    public StringBuffer getMsgBuffer() {
        return msgBuffer;
    }
}
