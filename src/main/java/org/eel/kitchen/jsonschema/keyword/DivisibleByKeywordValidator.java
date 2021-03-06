/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.math.BigDecimal;

/**
 * Validator for the {@code disallow} keyword
 *
 * <p>Note: in draft v4, the keyword will be renamed to {@code mod},
 * but will work all the same.</p>
 */
public final class DivisibleByKeywordValidator
    extends NumericKeywordValidator
{
    public DivisibleByKeywordValidator(final JsonNode schema)
    {
        super("divisibleBy", schema);
    }

    @Override
    protected void validateLong(final ValidationReport report,
        final long instanceValue)
    {
        if (instanceValue % longValue != 0L)
            report.addMessage("instance is not a multiple of divisibleBy");
    }

    @Override
    protected void validateDecimal(final ValidationReport report,
        final BigDecimal instanceValue)
    {
        final BigDecimal remainder = instanceValue.remainder(decimalValue);

        /*
         * We cannot use equality! As far as BigDecimal goes,
         * "0" and "0.0" are NOT equal. But .compareTo() returns the correct
         * result.
         */
        if (remainder.compareTo(BigDecimal.ZERO) != 0)
            report.addMessage("instance is not a multiple of divisibleBy");
    }
}
