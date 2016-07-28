package org.devgateway.ocus.persistence.mongo;

import org.devgateway.ocds.persistence.mongo.Address;
import org.devgateway.ocds.persistence.mongo.Item;

/**
 * @author idobre
 * @since 7/22/16
 */
public class USAItem extends Item {
    /**
     *  a standard Address block which can be used to provide a postal address where services should be delivered.
     */
    private Address deliveryAddress;

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
