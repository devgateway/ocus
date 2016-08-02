package org.devgateway.ocus.persistence.mongo.spring;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.*;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocus.persistence.mongo.USAItem;
import org.devgateway.ocus.persistence.mongo.spring.constants.USASpendingConstants;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TimeZone;

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

                    // also set the Tag
                    release.getTag().add(Tag.award);
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
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Tender tender = release.getTender();

                            if (tender == null) {
                                tender = new Tender();
                                tender.setId("tender-" + release.getOcid());
                                release.setTender(tender);
                            }

                            Organization procuringEntity = tender.getProcuringEntity();
                            if (procuringEntity == null) {
                                procuringEntity = new Organization();
                                tender.setProcuringEntity(procuringEntity);
                            }

                            Identifier identifier = procuringEntity.getIdentifier();
                            if (identifier == null) {
                                identifier = new Identifier();
                                procuringEntity.setIdentifier(identifier);
                            }
                            identifier.setLegalName(text);
                        }
                    }
                });

        forPattern("response/result/doc/extentcompeted")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Tender tender = release.getTender();

                            if (tender == null) {
                                tender = new Tender();
                                tender.setId("tender-" + release.getOcid());
                                release.setTender(tender);
                            }

                            // check OCE-45 for the mapping
                            tender.setProcurementMethod(
                                    USASpendingConstants.ExtentCompeted.extentCompetedMapping.get(text));
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
                            Tender tender = release.getTender();

                            if (tender == null) {
                                tender = new Tender();
                                tender.setId("tender-" + release.getOcid());
                                release.setTender(tender);
                            }

                            tender.setNumberOfTenderers(Integer.valueOf(text));
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
                            Tender tender = release.getTender();

                            if (tender == null) {
                                tender = new Tender();
                                tender.setId("tender-" + release.getOcid());
                                release.setTender(tender);
                            }

                            // check OCE-46
                            tender.setProcurementMethodRationale(text);
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

        forPattern("response/result/doc/streetaddress2")
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
                            address.setStreetAddress(address.getStreetAddress() + "\n" + text);
                        }
                    }
                });

        forPattern("response/result/doc/streetaddress3")
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
                            address.setStreetAddress(address.getStreetAddress() + "\n" + text);
                        }
                    }
                });

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

        forPattern("response/result/doc/principalnaicscode")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Award award = getFirstAward(release);
                            Item item = getFirstItemAward(award);

                            Classification classification = item.getClassification();
                            if (classification == null) {
                                classification = new Classification();
                                item.setClassification(classification);
                            }
                            classification.setId(text);
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

        forPattern("response/result/doc/descriptionofcontractrequirement")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);
                            Item item = getFirstItemContract(contract);

                            Classification classification = item.getClassification();
                            if (classification == null) {
                                classification = new Classification();
                                item.setClassification(classification);
                            }
                            classification.setDescription(text);
                        }
                    }
                });

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
                            dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
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
                            dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
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
                            dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                            period.setEndDate(dateFormat.parse(text));
                        }
                    }
                });

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


        forPattern("response/result/doc/PlaceofPerformanceCity")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);
                            USAItem item = (USAItem) getFirstItemContract(contract);

                            Address deliveryAddress = item.getDeliveryAddress();
                            if (deliveryAddress == null) {
                                deliveryAddress = new Address();
                                item.setDeliveryAddress(deliveryAddress);
                            }
                            deliveryAddress.setLocality(text);
                        }
                    }
                });

        forPattern("response/result/doc/placeofperformancecountrycode")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);
                            USAItem item = (USAItem) getFirstItemContract(contract);

                            Address deliveryAddress = item.getDeliveryAddress();
                            if (deliveryAddress == null) {
                                deliveryAddress = new Address();
                                item.setDeliveryAddress(deliveryAddress);
                            }
                            deliveryAddress.setCountryName(text);
                        }
                    }
                });

        forPattern("response/result/doc/placeofperformancezipcode")
                .addRule(new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) throws Exception {
                        if (!StringUtils.isEmpty(text)) {
                            // get the object from top of the stack, it should be a Release object
                            Release release = getDigester().peek();
                            Contract contract = getFirstContract(release);
                            USAItem item = (USAItem) getFirstItemContract(contract);

                            Address deliveryAddress = item.getDeliveryAddress();
                            if (deliveryAddress == null) {
                                deliveryAddress = new Address();
                                item.setDeliveryAddress(deliveryAddress);
                            }
                            deliveryAddress.setPostalCode(text);
                        }
                    }
                });
    }

    private Award getFirstAward(Release release) {
        Award award = null;
        Set<Award> awards = release.getAwards();

        if (awards.iterator().hasNext()) {
            award = awards.iterator().next();
        }

        if (award == null) {
            award = new Award();
            award.setId(release.getOcid() + "-award-" + awards.size());
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
            contract.setAwardID(getFirstAward(release).getId());
            contracts.add(contract);
        }

        return contract;
    }

    private Item getFirstItemAward(Award award) {
        Item item = null;
        Set<Item> items = award.getItems();

        if (items.iterator().hasNext()) {
            item = items.iterator().next();
        }

        if (item == null) {
            item = new USAItem();
            item.setId(Integer.toString(items.size()));
            items.add(item);
        }

        return item;
    }

    private Item getFirstItemContract(Contract contract) {
        Item item = null;
        Set<Item> items = contract.getItems();

        if (items.iterator().hasNext()) {
            item = items.iterator().next();
        }

        if (item == null) {
            item = new USAItem();
            item.setId(Integer.toString(items.size()));
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
            transaction.setId(Integer.toString(transactions.size()));
            transactions.add(transaction);
        }

        return transaction;
    }
}
