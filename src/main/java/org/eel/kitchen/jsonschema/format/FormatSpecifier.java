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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.List;

/**
 * Base class for a format specifier
 *
 * <p>The {@code format} keyword always takes a string as an argument, and this
 * string is called a "specifier". The draft defines specifiers for recognizing
 * URIs, phone numbers, different date formats, and so on -- and even CSS 2.1
 * colors and styles(not supported).</p>
 *
 * <p>One important thing to remember is that a specifier will only validate a
 * given subset of JSON instance types (for instance, {@code uri} only validates
 * string instances). In the event that the instane type is not of the
 * validated types, validation <i>succeeds</i>.</p>
 *
 * <p>The spec allows for custom specifiers to be added. This implementation,
 * however, does not support it.</p>
 *
 */
public abstract class FormatSpecifier
{
    /**
     * JSON instance types which this specifier can validate
     */
    private final EnumSet<NodeType> typeSet;

    /**
     * Protected constructor
     *
     * <p>Its arguments are the node types recognized by the specifier. Only
     * one specifier recognizes more than one type: {@code utc-millisec} (it
     * can validate both numbers and integers).
     * </p>
     *
     * @param first first type
     * @param other other types, if any
     */
    protected FormatSpecifier(final NodeType first, final NodeType... other)
    {
        typeSet = EnumSet.of(first, other);
    }

    /**
     * Main validation function
     *
     * <p>This function only checks whether the value is of a type recognized
     * by this specifier. If so, it call {@link #checkValue(List, JsonNode)}.
     * </p>
     *
     * @param messages the list of messages to fill
     * @param value the value to validate
     */
    public final void validate(final List<String> messages,
        final JsonNode value)
    {
        if (!typeSet.contains(NodeType.getNodeType(value)))
            return;

        checkValue(messages, value);
    }

    /**
     * Abstract method implemented by all specifiers
     *
     * <p>It is only called if the value type is one expected by the
     * specifier, see {@link #validate(List, JsonNode)}.</p>
     *
     * @param messages the list of messages to fill
     * @param value the value to validate
     */
    abstract void checkValue(final List<String> messages, final JsonNode value);
}
