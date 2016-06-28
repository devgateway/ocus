package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.reader.XMLFileImport;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author idobre
 * @since 6/28/16
 */
public class USASpendingXMLImport extends XMLFileImport {
    public USASpendingXMLImport(ReleaseRepository releaseRepository, InputStream inputStream) {
        super(releaseRepository, inputStream);
    }

    public USASpendingXMLImport(ReleaseRepository releaseRepository, File file) throws IOException {
        super(releaseRepository, file);
    }

    @Override
    protected Release processRelease(Release release) {
        return release;
    }

    @Override
    protected AbstractRulesModule getAbstractRulesModule() {
        return new USASpendingRulesModule();
    }
}
