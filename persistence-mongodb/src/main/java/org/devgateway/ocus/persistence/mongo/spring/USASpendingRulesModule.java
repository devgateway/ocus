package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.binder.AbstractRulesModule;

/**
 * @author idobre
 * @since 6/28/16
 */
public class USASpendingRulesModule extends AbstractRulesModule {
    @Override
    protected void configure() {
        forPattern("response/result/doc")
                .createObject().ofType("org.devgateway.ocds.persistence.mongo.Release")
                .then().setNext("saveRelease");

        forPattern("response/result/doc/record_count").setBeanProperty().withName("id");
    }
}
