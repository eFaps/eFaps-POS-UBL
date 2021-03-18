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

import org.efaps.ubl.documents.IAllowanceEntry;

public class AllowanceEntry
    implements IAllowanceEntry
{

    private final String reason;

    private final BigDecimal factor;

    private final BigDecimal amount;

    private final BigDecimal baseAmount;

    private AllowanceEntry(final Builder builder)
    {
        reason = builder.reason;
        factor = builder.factor;
        amount = builder.amount;
        baseAmount = builder.baseAmount;
    }

    @Override
    public String getReason()
    {
        return reason;
    }

    @Override
    public BigDecimal getFactor()
    {
        return factor;
    }

    @Override
    public BigDecimal getAmount()
    {
        return amount;
    }

    @Override
    public BigDecimal getBaseAmount()
    {
        return baseAmount;
    }

    /**
     * Creates builder to build {@link AllowanceEntry}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link AllowanceEntry}.
     */
    public static final class Builder
    {

        private String reason;
        private BigDecimal factor;
        private BigDecimal amount;
        private BigDecimal baseAmount;

        private Builder()
        {
        }

        public Builder withReason(final String reason)
        {
            this.reason = reason;
            return this;
        }

        public Builder withFactor(final BigDecimal factor)
        {
            this.factor = factor;
            return this;
        }

        public Builder withAmount(final BigDecimal amount)
        {
            this.amount = amount;
            return this;
        }

        public Builder withBaseAmount(final BigDecimal baseAmount)
        {
            this.baseAmount = baseAmount;
            return this;
        }

        public AllowanceEntry build()
        {
            return new AllowanceEntry(this);
        }
    }
}
