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

    private StringBuffer msgBuffer = new StringBuffer();

    private long releaseCount;

    private long startTime;

    @Override
    protected Release processRelease(Release release) {
        releaseCount++;

        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        if (releaseCount % 1000 == 0) {
            logMessage("Import Speed " + releaseCount * 1000 / (System.currentTimeMillis() - startTime)
                    + " rows per second.");
            startTime = System.currentTimeMillis();
            releaseCount = 0;
        }

        // LOGGER.error(">>>>> release: " + release);
        // if (Integer.parseInt(release.getId()) % 1000 == 0) {
        //     LOGGER.error(">>>>> " + release.getOcid());
        // }
        return release;
    }

    @Override
    protected AbstractRulesModule getAbstractRulesModule() {
        return new USASpendingRulesModule();
    }

    @Override
    public void logMessage(String message) {
        LOGGER.info(message);
        msgBuffer.append(message).append("\r\n");
    }

    @Override
    public StringBuffer getMsgBuffer() {
        return msgBuffer;
    }

    public void newMsgBuffer() {
        msgBuffer = new StringBuffer();
    }
}
