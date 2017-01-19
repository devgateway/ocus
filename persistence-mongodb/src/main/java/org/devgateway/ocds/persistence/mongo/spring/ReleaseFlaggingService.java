/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.spring;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.processors.release.ReleaseFlagI019Processor;
import org.devgateway.ocds.persistence.mongo.flags.processors.release.ReleaseFlagI038Processor;
import org.devgateway.ocds.persistence.mongo.flags.processors.release.ReleaseFlagI007Processor;
import org.devgateway.ocds.persistence.mongo.repository.FlaggedReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author mpostelnicu
 *
 */
@Service
public class ReleaseFlaggingService {

    @Autowired
    private FlaggedReleaseRepository releaseRepository;

    @Autowired
    private ReleaseFlagI038Processor releaseFlagI038Processor;

    @Autowired
    private ReleaseFlagI007Processor releaseFlagI007Processor;

    @Autowired
    private ReleaseFlagI019Processor releaseFlagI019Processor;


    public static final int FLAGGING_BATCH_SIZE = 5000;

    private final Collection<AbstractFlaggedReleaseFlagProcessor> releaseFlagProcessors =
            Collections.unmodifiableList(Arrays.asList(
                    releaseFlagI038Processor,
                    releaseFlagI007Processor,
                    releaseFlagI019Processor
            ));

    private void processAndSaveFlagsForRelease(FlaggedRelease release) {
        releaseFlagProcessors.forEach(processor -> processor.process(release));
        releaseRepository.save(release);
    }

    public void processAndSaveFlagsForAllReleases(Consumer<String> logMessage) {

        logMessage.accept("<b>RUNNING SCHEMA VALIDATION.</b>");

        int pageNumber = 0;
        int processedCount = 0;

        Page<FlaggedRelease> page;
        do {
            page = releaseRepository.findAll(new PageRequest(pageNumber++, FLAGGING_BATCH_SIZE));
            page.getContent().parallelStream().forEach(this::processAndSaveFlagsForRelease);
            processedCount += page.getNumberOfElements();
            logMessage.accept("Flagged " + processedCount + " releases");
        } while (!page.isLast());

        logMessage.accept("<b>CORRUPTION FLAGGING COMPLETE.</b>");
    }
}
