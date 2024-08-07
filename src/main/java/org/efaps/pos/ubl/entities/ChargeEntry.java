/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.efaps.pos.ubl.entities;

import java.math.BigDecimal;

import org.efaps.ubl.documents.interfaces.IChargeEntry;

public class ChargeEntry
    implements IChargeEntry
{

    private final String reason;

    private final BigDecimal factor;

    private final BigDecimal amount;

    private final BigDecimal baseAmount;

    private ChargeEntry(final Builder builder)
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
     * Creates builder to build {@link ChargeEntry}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link ChargeEntry}.
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

        public ChargeEntry build()
        {
            return new ChargeEntry(this);
        }
    }
}
