package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;

/**
 * @author idobre
 * @since 6/28/16
 */
public class USASpendingRulesModule extends AbstractRulesModule {
    private static final Logger LOGGER = Logger.getLogger(USASpendingRulesModule.class);

    @Override
    protected void configure() {
        forPattern("response/result/doc")
                .createObject().ofType("org.devgateway.ocds.persistence.mongo.Release")
                .then().setNext("saveRelease");

        forPattern("response/result/doc/unique_transaction_id").addRule(new Rule() {
            @Override
            public void body(String namespace, String name, String text) throws Exception {
                if (text != null) {
                    // get the object from top of the stack, it should be a Release object
                    Release release = getDigester().peek();
                    release.setOcid(MongoConstants.OCDS_PREFIX + text);
                }
            }
        });

        // Buyer
        forPattern("response/result/doc/fundingrequestingagencyid")
                .createObject().ofType("org.devgateway.ocds.persistence.mongo.Organization")
                .then().setBeanProperty().withName("name")
                .then().setNext("setBuyer");
        /*


        // solicitationid  - tender.id
        forPattern("response/result/doc/solicitationid").setBeanProperty().withName("id");

        // idvagencyid  - tender.id (framework ID)
        forPattern("response/result/doc/idvagencyid").setBeanProperty().withName("id");

        // solicitationprocedures  - tender.procurementMethod.details
        forPattern("response/result/doc/solicitationprocedures").setBeanProperty().withName("id");

        // typeofsetaside  - tender.procurementMethod.details
        forPattern("response/result/doc/typeofsetaside").setBeanProperty().withName("id");

        // tender.procuringEntity
        forPattern("response/result/doc/agencyid").setBeanProperty().withName("id");

        // contractingofficeagencyid  - tender.procuringEntity
        forPattern("response/result/doc/contractingofficeagencyid").setBeanProperty().withName("id");

        // extentcompeted - tender.procurementMethod
        forPattern("response/result/doc/extentcompeted").setBeanProperty().withName("id");

        // multipleorsingleawardidc  - tender.procurementMethod
        forPattern("response/result/doc/multipleorsingleawardidc").setBeanProperty().withName("id");

        // numberofoffersreceived  - tender.numberOfTenderers
        forPattern("response/result/doc/numberofoffersreceived").setBeanProperty().withName("id");

        // reasonnotcompeted  - tender.procurementMethod.rationale
        forPattern("response/result/doc/reasonnotcompeted").setBeanProperty().withName("id");





        // vendorname - supplier.name
        forPattern("response/result/doc/vendorname").setBeanProperty().withName("id");

        // vendordoingasbusinessname  - supplier.name (alternate)
        forPattern("response/result/doc/vendordoingasbusinessname").setBeanProperty().withName("id");

         // vendoralternatename  - supplier.name (alternate)
        forPattern("response/result/doc/vendoralternatename").setBeanProperty().withName("id");

        // dunsnumber - supplier.identifier.id
        forPattern("response/result/doc/dunsnumber").setBeanProperty().withName("id");

        // vendorlegalorganizationname  - supplier.identifier.legalName
        forPattern("response/result/doc/vendorlegalorganizationname").setBeanProperty().withName("id");

        // organizationaltype  - awards.supplier.additionalIdentifiers
        forPattern("response/result/doc/organizationaltype").setBeanProperty().withName("id");

        // faxno  - supplier.contactPoint.faxNumber
        forPattern("response/result/doc/faxno").setBeanProperty().withName("id");

        // phoneno  - supplier.contactPoint.telephone
        forPattern("response/result/doc/phoneno").setBeanProperty().withName("id");

        // divisionname - award:suppliers:contactPoint:name
        forPattern("response/result/doc/divisionname").setBeanProperty().withName("id");

        // city - award:suppliers:address:locality
        forPattern("response/result/doc/city").setBeanProperty().withName("id");

        // congressionaldistrict - award:suppliers:address:congressionalDistrict
        forPattern("response/result/doc/congressionaldistrict").setBeanProperty().withName("id");

        // vendorcountrycode  - supplier.address.countryName
        forPattern("response/result/doc/vendorcountrycode").setBeanProperty().withName("id");

        // zipcode  - supplier.address.postalCode
        forPattern("response/result/doc/zipcode").setBeanProperty().withName("id");

        // state - supplier.address.region
        forPattern("response/result/doc/state").setBeanProperty().withName("id");

        // streetaddress  - supplier.address.streetAddress
        forPattern("response/result/doc/streetaddress").setBeanProperty().withName("id");

        // streetaddress2  - supplier.address.streetAddress
        forPattern("response/result/doc/streetaddress2").setBeanProperty().withName("id");

        // streetaddress3  - supplier.address.streetAddress
        forPattern("response/result/doc/streetaddress3").setBeanProperty().withName("id");

        //award.amount
        forPattern("response/result/doc/baseandalloptionsvalue").setBeanProperty().withName("id");





        // piid  - contract.id
        forPattern("response/result/doc/piid").setBeanProperty().withName("id");

        // principalnaicscode - items.classification.id
        forPattern("response/result/doc/principalnaicscode").setBeanProperty().withName("id");

        // descriptionofcontractrequirement  - contracts:item:description
        forPattern("response/result/doc/descriptionofcontractrequirement").setBeanProperty().withName("id");

        // productorservicecode  - items.additionalClassifications
        forPattern("response/result/doc/productorservicecode").setBeanProperty().withName("id");

        // placeofmanufacture  - contracts:item:additionalClassifications
        forPattern("response/result/doc/placeofmanufacture").setBeanProperty().withName("id");

        // countryoforigin  - contracts:item:additionalClassifications
        forPattern("response/result/doc/countryoforigin").setBeanProperty().withName("id");

        // systemequipmentcode  - contracts:item:additionalClassifications
        forPattern("response/result/doc/systemequipmentcode").setBeanProperty().withName("id");

        // reasonformodification  - contract.amendment.rationale
        forPattern("response/result/doc/reasonformodification").setBeanProperty().withName("id");

        // signeddate  - contract.dateSigned
        forPattern("response/result/doc/signeddate").setBeanProperty().withName("id");

        // effectivedate  - contract.period.startDate
        forPattern("response/result/doc/effectivedate").setBeanProperty().withName("id");

        // currentcompletiondate - contract.period.endDate
        forPattern("response/result/doc/currentcompletiondate").setBeanProperty().withName("id");

        // ultimatecompletiondate  - contract.period.endDate (actual)
        forPattern("response/result/doc/ultimatecompletiondate").setBeanProperty().withName("id");

        // contract.amount
        forPattern("response/result/doc/baseandexercisedoptionsvalue").setBeanProperty().withName("id");





        // dollarsobligated - implementation.transaction.amount
        forPattern("response/result/doc/dollarsobligated").setBeanProperty().withName("id");



        // placeofperformancecity  - location.deliveryAddress.locality
        forPattern("response/result/doc/placeofperformancecity").setBeanProperty().withName("id");

        // placeofperformancecountrycode  - location.deliveryAddress.countryName
        forPattern("response/result/doc/placeofperformancecountrycode").setBeanProperty().withName("id");

        // placeofperformancezipcode  - location.deliveryAddress.postalCode
        forPattern("response/result/doc/placeofperformancezipcode").setBeanProperty().withName("id");

        */
    }
}
