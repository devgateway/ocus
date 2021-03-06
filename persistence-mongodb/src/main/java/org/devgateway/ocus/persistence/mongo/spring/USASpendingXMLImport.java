package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Classification;
import org.devgateway.ocds.persistence.mongo.Item;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.reader.XMLFileImport;
import org.devgateway.ocds.persistence.mongo.repository.ClassificationRepository;
import org.devgateway.ocds.persistence.mongo.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    private StringBuffer msgBuffer = new StringBuffer();

    private long releaseCount;

    private long startTime;

    @Override
    protected Release processRelease(final Release release) {
        releaseCount++;

        Organization supplier = null;
        Organization procuringEntity = null;
        Item item = null;
        // get first award
        Award award = release.getAwards().stream().findFirst().get();
        if (award != null) {
            // get first supplier
            supplier = award.getSuppliers().stream().findFirst().get();
            // get first item
            item = award.getItems().stream().findFirst().get();
        }
        if (release.getTender() != null && release.getTender().getProcuringEntity() != null) {
            procuringEntity = release.getTender().getProcuringEntity();
        }

        if (supplier != null) {
            saveOrganization(supplier, Organization.OrganizationType.supplier);
        }
        if (procuringEntity != null) {
            saveOrganization(procuringEntity, Organization.OrganizationType.procuringEntity);
        }
        if (item != null && item.getClassification() != null) {
            saveItemClassification(item.getClassification());
        }

        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        if (releaseCount % 1000 == 0) {
            logMessage("Import Speed " + releaseCount * 1000 / (System.currentTimeMillis() - startTime)
                    + " rows per second.");
            startTime = System.currentTimeMillis();
            releaseCount = 0;
        }

        return release;
    }

    /**
     * Save the suppliers in a different mongo collection
     *
     * @param organization
     * @param type
     */
    private void saveOrganization(final Organization organization, final Organization.OrganizationType type) {
        Organization findOrganization = organizationRepository.findByIdOrNameAndTypes(organization.getName(), type);
        // Organization findOrganization = organizationRepository.findByIdOrName(organization.getName());
        if (findOrganization == null) {
            organizationRepository.save(organization);
        }
    }

    /**
     * Save Item Classification in a different mongo collection
     *
     * @param classification
     */
    private void saveItemClassification(final Classification classification) {
        Classification findclassification = classificationRepository.findOne(classification.getId());
        if (findclassification == null) {
            classificationRepository.save(classification);
        }
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
