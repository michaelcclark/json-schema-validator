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

package org.eel.kitchen.jsonschema.validator2;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import org.eel.kitchen.jsonschema.JsonSchemaException;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Initial validator -- each schema validator MUST instantiate one of these
 * first
 *
 * <p>Its role is to gather whatever elements are obtained from a context,
 * whether it be initial or validator supplied, and instantiate a {@link
 * RefResolverValidator} from then on.</p>
 *
 * <p>Among other things, it means that this is the place where malformed
 * JSON input will be detected.</p>
 */
public final class SchemaValidator
    implements Validator
{
    private final SchemaBuildingBlocks buildingBlocks;
    private final JsonNode schema;
    private final String path;

    private Validator next;

    public SchemaValidator(final SchemaBuildingBlocks buildingBlocks,
        final JsonNode schema, final String path)
    {
        Preconditions.checkNotNull(buildingBlocks);
        Preconditions.checkNotNull(schema);
        Preconditions.checkNotNull(path);

        this.buildingBlocks = buildingBlocks;
        this.schema = schema;
        this.path = path;
    }

    public SchemaValidator(final SchemaBuildingBlocks buildingBlocks,
        final JsonNode schema)
    {
        this(buildingBlocks, schema, "#");
    }

    @Override
    public boolean validate(final ValidationReport report,
        final JsonNode instance)
    {
        if (!schema.isObject()) {
            report.addMessage("schema is not an object");
            return false;
        }

        final SchemaRegistry registry = buildingBlocks.getRegistry();

        final SchemaContainer container;
        final SchemaNode schemaNode;

        try {
            container = registry.register(schema);
            schemaNode = container.lookupFragment(path);
        } catch (JsonSchemaException e) {
            report.addMessage(e.getMessage());
            return false;
        }

        final SchemaContext context = new SchemaContext(container, schemaNode);
        final Set<JsonRef> refs = new LinkedHashSet<JsonRef>();

        next = new RefResolverValidator(buildingBlocks, context, refs);
        return true;
    }

    @Override
    public Validator next()
    {
        return next;
    }
}
