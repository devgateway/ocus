package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.ByRuleBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.*;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.springframework.security.access.method.P;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

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
                if (!StringUtils.isEmpty(text)) {
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

        // Tender section
        forPattern("response/result/doc/agencyid")
                .createObject().ofType("org.devgateway.ocds.persistence.mongo.Tender")
                .then().addRule(new Rule() {
            @Override
            public void body(String namespace, String name, String text) throws Exception {
                if (!StringUtils.isEmpty(text)) {
                    // get the object from top of the stack
                    Tender tender = getDigester().peek();
                    Organization procuringEntity = new Organization();
                    Identifier identifier = new Identifier();
                    identifier.setLegalName(text);
                    procuringEntity.setIdentifier(identifier);
                    tender.setProcuringEntity(procuringEntity);
                }
            }
        })
                .then().setNext("setTender");

        forPattern("response/result/doc/solicitationprocedures")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            // TODO - check OCE-45 and OCE-7 comments
                            release.getTender().setProcurementMethod(null);
                        }
                    }
                });

        forPattern("response/result/doc/typeofsetaside")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            // TODO - check OCE-45 and OCE-7 comments
                            release.getTender().setProcurementMethod(null);
                        }
                    }
                });

        // contractingofficeagencyid  - using only agencyid based on spreadsheet

        forPattern("response/result/doc/extentcompeted")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            // TODO - check OCE-45 and OCE-7 comments
                            release.getTender().setProcurementMethod(null);
                        }
                    }
                });

        forPattern("response/result/doc/multipleorsingleawardidc")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            // TODO - check OCE-45 and OCE-7 comments
                            release.getTender().setProcurementMethod(null);
                        }
                    }
                });

        forPattern("response/result/doc/numberofoffersreceived")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            release.getTender().setNumberOfTenderers(Integer.valueOf(text));
                        }
                    }
                });

        forPattern("response/result/doc/reasonnotcompeted")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            // TODO - check OCE-46 and OCE-7 comments
                            release.getTender().setProcurementMethod(null);
                        }
                    }
                });

        forPattern("response/result/doc/vendorname")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            supplier.setName(text);
                        }
                    }
                });

        // vendordoingasbusinessname  - supplier.name (alternate)
        // forPattern("response/result/doc/vendordoingasbusinessname").setBeanProperty().withName("id");

        // vendoralternatename  - supplier.name (alternate)
        // forPattern("response/result/doc/vendoralternatename").setBeanProperty().withName("id");

        forPattern("response/result/doc/dunsnumber")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            Identifier identifier = supplier.getIdentifier();
                            if (identifier == null) {
                                identifier = new Identifier();
                                supplier.setIdentifier(identifier);
                            }
                            identifier.setId(text);
                        }
                    }
                });

        forPattern("response/result/doc/vendorlegalorganizationname")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            Identifier identifier = supplier.getIdentifier();
                            if (identifier == null) {
                                identifier = new Identifier();
                                supplier.setIdentifier(identifier);
                            }
                            identifier.setLegalName(text);
                        }
                    }
                });

        // organizationaltype  - awards.supplier.additionalIdentifiers
        // forPattern("response/result/doc/organizationaltype").setBeanProperty().withName("id");

        forPattern("response/result/doc/faxno")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            ContactPoint contactPoint = supplier.getContactPoint();
                            if (contactPoint == null) {
                                contactPoint = new ContactPoint();
                                supplier.setContactPoint(contactPoint);
                            }
                            contactPoint.setFaxNumber(text);
                        }
                    }
                });

        forPattern("response/result/doc/phoneno")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            ContactPoint contactPoint = supplier.getContactPoint();
                            if (contactPoint == null) {
                                contactPoint = new ContactPoint();
                                supplier.setContactPoint(contactPoint);
                            }
                            contactPoint.setTelephone(text);
                        }
                    }
                });

        forPattern("response/result/doc/divisionname")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            ContactPoint contactPoint = supplier.getContactPoint();
                            if (contactPoint == null) {
                                contactPoint = new ContactPoint();
                                supplier.setContactPoint(contactPoint);
                            }
                            contactPoint.setName(text);
                        }
                    }
                });

        forPattern("response/result/doc/city")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            Address address = supplier.getAddress();
                            if (address == null) {
                                address = new Address();
                                supplier.setAddress(address);
                            }
                            address.setLocality(text);
                        }
                    }
                });

        forPattern("response/result/doc/vendorcountrycode")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            Address address = supplier.getAddress();
                            if (address == null) {
                                address = new Address();
                                supplier.setAddress(address);
                            }
                            address.setCountryName(text);
                        }
                    }
                });

        forPattern("response/result/doc/zipcode")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            Address address = supplier.getAddress();
                            if (address == null) {
                                address = new Address();
                                supplier.setAddress(address);
                            }
                            address.setPostalCode(text);
                        }
                    }
                });

        forPattern("response/result/doc/state")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            Address address = supplier.getAddress();
                            if (address == null) {
                                address = new Address();
                                supplier.setAddress(address);
                            }
                            address.setRegion(text);
                        }
                    }
                });

        forPattern("response/result/doc/streetaddress")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Organization supplier = getFirstSupplier(award);

                            Address address = supplier.getAddress();
                            if (address == null) {
                                address = new Address();
                                supplier.setAddress(address);
                            }
                            address.setStreetAddress(text);
                        }
                    }
                });

        // congressionaldistrict - award:suppliers:address:congressionalDistrict
        // forPattern("response/result/doc/congressionaldistrict").setBeanProperty().withName("id");

        forPattern("response/result/doc/baseandalloptionsvalue")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);

                            Amount amount = award.getValue();
                            if (amount == null) {
                                amount = new Amount();
                                award.setValue(amount);
                            }
                            amount.setAmount(new BigDecimal(text));
                            amount.setCurrency("USD");
                        }
                    }
                });

        forPattern("response/result/doc/piid")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);
                            contract.setId(text);
                        }
                    }
                });

        forPattern("response/result/doc/principalnaicscode")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);
                            Item item = getFirstItem(contract);

                            Classification classification = item.getClassification();
                            if (classification == null) {
                                classification = new Classification();
                                item.setClassification(classification);
                            }
                            classification.setId(text);
                        }
                    }
                });

        forPattern("response/result/doc/descriptionofcontractrequirement")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);
                            Item item = getFirstItem(contract);

                            Classification classification = item.getClassification();
                            if (classification == null) {
                                classification = new Classification();
                                item.setClassification(classification);
                            }
                            classification.setDescription(text);
                        }
                    }
                });

        // productorservicecode  - items.additionalClassifications
        // forPattern("response/result/doc/productorservicecode").setBeanProperty().withName("id");

        // placeofmanufacture  - contracts:item:additionalClassifications
        // forPattern("response/result/doc/placeofmanufacture").setBeanProperty().withName("id");

        // countryoforigin  - contracts:item:additionalClassifications
        // forPattern("response/result/doc/countryoforigin").setBeanProperty().withName("id");

        // systemequipmentcode  - contracts:item:additionalClassifications
        // forPattern("response/result/doc/systemequipmentcode").setBeanProperty().withName("id");

        forPattern("response/result/doc/reasonformodification")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);

                            Amendment amendment = contract.getAmendment();
                            if (amendment == null) {
                                amendment = new Amendment();
                                contract.setAmendment(amendment);
                            }

                            amendment.setRationale(text);
                        }
                    }
                });


        forPattern("response/result/doc/signeddate")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);

                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            contract.setDateSigned(dateFormat.parse(text));
                        }
                    }
                });

        forPattern("response/result/doc/effectivedate")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);

                            Period period = contract.getPeriod();
                            if (period == null) {
                                period = new Period();
                                contract.setPeriod(period);
                            }

                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            period.setStartDate(dateFormat.parse(text));
                        }
                    }
                });

        forPattern("response/result/doc/currentcompletiondate")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);

                            Period period = contract.getPeriod();
                            if (period == null) {
                                period = new Period();
                                contract.setPeriod(period);
                            }

                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            period.setEndDate(dateFormat.parse(text));
                        }
                    }
                });

        // ultimatecompletiondate  - contract.period.endDate (actual)
        // forPattern("response/result/doc/ultimatecompletiondate").setBeanProperty().withName("id");

        forPattern("response/result/doc/baseandexercisedoptionsvalue")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);

                            Amount amount = contract.getValue();
                            if (amount == null) {
                                amount = new Amount();
                                contract.setValue(amount);
                            }
                            amount.setCurrency("USD");
                            amount.setAmount(new BigDecimal(text));
                        }
                    }
                });

        forPattern("response/result/doc/dollarsobligated")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);

                            Implementation implementation = contract.getImplementation();
                            if (implementation == null) {
                                implementation = new Implementation();
                                contract.setImplementation(implementation);
                            }

                            Transaction transaction = getFirstTransaction(implementation);

                            Amount amount = transaction.getAmount();
                            if (amount == null) {
                                amount = new Amount();
                                transaction.setAmount(amount);
                            }
                            amount.setCurrency("USD");
                            amount.setAmount(new BigDecimal(text));
                        }
                    }
                });

        /*
        // placeofperformancecity  - location.deliveryAddress.locality
        forPattern("response/result/doc/placeofperformancecity").setBeanProperty().withName("id");

        // placeofperformancecountrycode  - location.deliveryAddress.countryName
        forPattern("response/result/doc/placeofperformancecountrycode").setBeanProperty().withName("id");

        // placeofperformancezipcode  - location.deliveryAddress.postalCode
        forPattern("response/result/doc/placeofperformancezipcode").setBeanProperty().withName("id");

        */
    }

    private Award getFirstAward(Release release) {
        Award award = null;
        Set<Award> awards = release.getAwards();

        if (awards.iterator().hasNext()) {
            award = awards.iterator().next();
        }

        if (award == null) {
            award = new Award();
            awards.add(award);
        }

        return award;
    }

    private Organization getFirstSupplier(Award award) {
        Organization supplier = null;
        Set<Organization> suppliers = award.getSuppliers();

        if (suppliers.iterator().hasNext()) {
            supplier = suppliers.iterator().next();
        }

        if (supplier == null) {
            supplier = new Organization();
            suppliers.add(supplier);
        }

        return supplier;
    }

    private Contract getFirstContract(Release release) {
        Contract contract = null;
        Set<Contract> contracts = release.getContracts();

        if (contracts.iterator().hasNext()) {
            contract = contracts.iterator().next();
        }

        if (contract == null) {
            contract = new Contract();
            contracts.add(contract);
        }

        return contract;
    }

    private Item getFirstItem(Contract contract) {
        Item item = null;
        Set<Item> items = contract.getItems();

        if (items.iterator().hasNext()) {
            item = items.iterator().next();
        }

        if (item == null) {
            item = new Item();
            items.add(item);
        }

        return item;
    }

    private Transaction getFirstTransaction(Implementation implementation) {
        Transaction transaction = null;
        Set<Transaction> transactions = implementation.getTransactions();

        if (transactions.iterator().hasNext()) {
            transaction = transactions.iterator().next();
        }

        if (transaction == null) {
            transaction = new Transaction();
            transactions.add(transaction);
        }

        return transaction;
    }
}
