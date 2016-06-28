package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.binder.AbstractRulesModule;

/**
 * @author idobre
 * @since 6/28/16
 */
public class USASpendingRulesModule extends AbstractRulesModule {
    @Override
    protected void configure() {
        forPattern("test/release")
                .createObject().ofType("org.devgateway.ocds.persistence.mongo.Release")
                .then().setNext("saveRelease");

        forPattern("test/release/id").setBeanProperty().withName("id");

        forPattern("test/release/buyer").createObject().ofType("org.devgateway.ocds.persistence.mongo.Organization")
                .then().setNext("setBuyer");

        forPattern("test/release/buyer/name").setBeanProperty().withName("name");

        forPattern("test/release/language").setBeanProperty().withName("language");
    }
}
