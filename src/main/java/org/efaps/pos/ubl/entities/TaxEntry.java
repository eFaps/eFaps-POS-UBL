/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl.entities;

import java.math.BigDecimal;

import org.efaps.ubl.documents.ITaxEntry;
import org.efaps.ubl.documents.TaxType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = TaxEntry.Builder.class)
public class TaxEntry
    implements ITaxEntry
{

    private BigDecimal amount;

    private BigDecimal taxableAmount;

    private BigDecimal percent;

    private String id;

    private String name;

    private String code;

    private String taxExemptionReasonCode;

    private TaxType taxType;

    private TaxEntry(final Builder builder)
    {
        amount = builder.amount;
        taxableAmount = builder.taxableAmount;
        percent = builder.percent;
        id = builder.id;
        name = builder.name;
        code = builder.code;
        taxExemptionReasonCode = builder.taxExemptionReasonCode;
        setTaxType(builder.taxType);
    }

    @Override
    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(final BigDecimal amount)
    {
        this.amount = amount;
    }

    @Override
    public BigDecimal getTaxableAmount()
    {
        return taxableAmount;
    }

    public void setTaxableAmount(final BigDecimal taxableAmount)
    {
        this.taxableAmount = taxableAmount;
    }

    @Override
    public BigDecimal getPercent()
    {
        return percent;
    }

    public void setPercent(final BigDecimal percent)
    {
        this.percent = percent;
    }

    @Override
    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public String getCode()
    {
        return code;
    }

    public void setCode(final String code)
    {
        this.code = code;
    }

    @Override
    public String getTaxExemptionReasonCode()
    {
        return taxExemptionReasonCode;
    }

    public void setTaxExemptionReasonCode(final String taxExemptionReasonCode)
    {
        this.taxExemptionReasonCode = taxExemptionReasonCode;
    }

    @Override
    public TaxType getTaxType()
    {
        return taxType;
    }

    public void setTaxType(final TaxType taxType)
    {
        this.taxType = taxType;
    }

    /**
     * Creates builder to build {@link TaxEntry}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link TaxEntry}.
     */
    public static final class Builder
    {

        private BigDecimal amount;
        private BigDecimal taxableAmount;
        private BigDecimal percent;
        private String id;
        private String name;
        private String code;
        private String taxExemptionReasonCode;
        private TaxType taxType;

        private Builder()
        {
        }

        public Builder withAmount(final BigDecimal amount)
        {
            this.amount = amount;
            return this;
        }

        public Builder withTaxableAmount(final BigDecimal taxableAmount)
        {
            this.taxableAmount = taxableAmount;
            return this;
        }

        public Builder withPercent(final BigDecimal percent)
        {
            this.percent = percent;
            return this;
        }

        public Builder withId(final String id)
        {
            this.id = id;
            return this;
        }

        public Builder withName(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder withCode(final String code)
        {
            this.code = code;
            return this;
        }

        public Builder withTaxExemptionReasonCode(final String taxExemptionReasonCode)
        {
            this.taxExemptionReasonCode = taxExemptionReasonCode;
            return this;
        }

        public Builder withTaxType(final TaxType taxType)
        {
            this.taxType = taxType;
            return this;
        }

        public TaxEntry build()
        {
            return new TaxEntry(this);
        }
    }

}
