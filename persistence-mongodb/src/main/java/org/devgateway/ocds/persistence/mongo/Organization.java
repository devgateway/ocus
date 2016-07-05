package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Organization
 * <p>
 * An organization.
 *
 * http://standard.open-contracting.org/latest/en/schema/reference/#organization
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "identifier",
        "additionalIdentifiers",
        "name",
        "address",
        "contactPoint"
})
@Document
public class Organization implements Identifiable {
    @ExcelExport
    @Id
    private String id;

    @ExcelExport
    @JsonProperty("identifier")
    private Identifier identifier;
    /**
     * A list of additional / supplemental identifiers for the organization, using the
     * [organization identifier guidance]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/#organization-identifiers).
     *  This could be used to provide an internally used identifier for
     *  this organization in addition to the primary legal entity identifier.
     *
     */
    @JsonProperty("additionalIdentifiers")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    private Set<Identifier> additionalIdentifiers = new LinkedHashSet<Identifier>();

    /**
     * The common name of the organization. The ID property provides an space for the formal legal name,
     * and so this may either repeat that value, or could provide the common name by which this organization is known.
     * This field could also include details of the department or sub-unit involved in this contracting process.
     *
     */
    @ExcelExport
    @JsonProperty("name")
    private String name;

    /**
     * An address. This may be the legally registered address of the organization, or may be a correspondence address
     * for this particular contracting process.
     *
     */
    @ExcelExport
    @JsonProperty("address")
    private Address address;

    /**
     * An person, contact point or department to contact in relation to this contracting process.
     *
     */
    @ExcelExport
    @JsonProperty("contactPoint")
    private ContactPoint contactPoint;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The identifier
     */
    @JsonProperty("identifier")
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     *
     * @param identifier
     *     The identifier
     */
    @JsonProperty("identifier")
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     * A list of additional / supplemental identifiers for the organization, using the
     * [organization identifier guidance]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/#organization-identifiers).
     *  This could be used to provide an internally used identifier for
     *  this organization in addition to the primary legal entity identifier.
     *
     * @return
     *     The additionalIdentifiers
     */
    @JsonProperty("additionalIdentifiers")
    public Set<Identifier> getAdditionalIdentifiers() {
        return additionalIdentifiers;
    }

    /**
     * A list of additional / supplemental identifiers for the organization, using the
     * [organization identifier guidance]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/#organization-identifiers).
     *  This could be used to provide an internally used identifier for
     *  this organization in addition to the primary legal entity identifier.
     *
     * @param additionalIdentifiers
     *     The additionalIdentifiers
     */
    @JsonProperty("additionalIdentifiers")
    public void setAdditionalIdentifiers(Set<Identifier> additionalIdentifiers) {
        this.additionalIdentifiers = additionalIdentifiers;
    }

    /**
     * The common name of the organization. The ID property provides an space for the formal legal name,
     * and so this may either repeat that value, or could provide the common name by which this organization is known.
     * This field could also include details of the department or sub-unit involved in this contracting process.
     *
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * The common name of the organization. The ID property provides an space for the formal legal name,
     * and so this may either repeat that value, or could provide the common name by which this organization is known.
     * This field could also include details of the department or sub-unit involved in this contracting process.
     *
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * An address. This may be the legally registered address of the organization,
     * or may be a correspondence address for this particular contracting process.
     *
     * @return
     *     The address
     */
    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    /**
     * An address. This may be the legally registered address of the organization,
     * or may be a correspondence address for this particular contracting process.
     *
     * @param address
     *     The address
     */
    @JsonProperty("address")
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * An person, contact point or department to contact in relation to this contracting process.
     *
     * @return
     *     The contactPoint
     */
    @JsonProperty("contactPoint")
    public ContactPoint getContactPoint() {
        return contactPoint;
    }

    /**
     * An person, contact point or department to contact in relation to this contracting process.
     *
     * @param contactPoint
     *     The contactPoint
     */
    @JsonProperty("contactPoint")
    public void setContactPoint(ContactPoint contactPoint) {
        this.contactPoint = contactPoint;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(identifier).
                append(additionalIdentifiers).
                append(name).
                append(address).
                append(contactPoint).
                toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Organization)) {
            return false;
        }
        Organization rhs = ((Organization) other);
        return new EqualsBuilder().
                append(identifier, rhs.identifier).
                append(additionalIdentifiers, rhs.additionalIdentifiers).
                append(name, rhs.name).
                append(address, rhs.address).
                append(contactPoint, rhs.contactPoint).
                isEquals();
    }

}
