package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

/**
 * Value OCDS Entity http://standard.open-contracting.org/latest/en/schema/reference/#value
 *
 * We don't use Value class because of this bug:
 *  http://stackoverflow.com/questions/32343853/
 *          spring-mongodb-driver-not-supporting-hierarchical-structure-of-java-domain-objec
 *      (hope this will be fixed in future versions of spring data mongodb)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "amount",
        "currency"
})
public class Amount {
    /**
     * Amount as a number.
     *
     */
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * The currency in 3-letter ISO 4217 format.
     *
     */
    @JsonProperty("currency")
    private String currency;

    /**
     * Amount as a number.
     *
     * @return
     *     The amount
     */
    @JsonProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Amount as a number.
     *
     * @param amount
     *     The amount
     */
    @JsonProperty("amount")
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * The currency in 3-letter ISO 4217 format.
     *
     * @return
     *     The currency
     */
    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    /**
     * The currency in 3-letter ISO 4217 format.
     *
     * @param currency
     *     The currency
     */
    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(amount).
                append(currency).
                toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Amount)) {
            return false;
        }
        Amount rhs = ((Amount) other);
        return new EqualsBuilder().
                append(amount, rhs.amount).
                append(currency, rhs.currency).
                isEquals();
    }
}