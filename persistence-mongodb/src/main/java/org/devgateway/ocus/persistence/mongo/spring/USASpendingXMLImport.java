package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.reader.XMLFileImport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * @author idobre
 * @since 6/28/16
 */
@Service
@Transactional
public class USASpendingXMLImport extends XMLFileImport implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(USASpendingXMLImport.class);

    private final StringBuffer msgBuffer = new StringBuffer();

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
