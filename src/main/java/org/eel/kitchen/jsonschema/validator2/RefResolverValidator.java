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
import org.eel.kitchen.jsonschema.JsonSchemaException;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.ref.SchemaRegistry;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

final class RefResolverValidator
    implements Validator
{
    private final SchemaBuildingBlocks buildingBlocks;
    private final SchemaContext context;
    private final Set<JsonRef> refs;

    private boolean isRef;

    RefResolverValidator(final SchemaBuildingBlocks buildingBlocks,
        final SchemaContext context, final Set<JsonRef> refs)
    {
        this.buildingBlocks = buildingBlocks;
        this.context = context;
        this.refs = refs;
    }

    @Override
    public boolean validate(final ValidationReport report,
        final JsonNode instance)
    {
        SchemaContainer container = context.getContainer();
        SchemaNode schemaNode = context.getNode();

        isRef = nodeIsRef(schemaNode);

        if (!isRef)
            return true;

        final SchemaRegistry registry = buildingBlocks.getRegistry();

        JsonRef ref;

        try {
            ref = JsonRef.fromNode(schemaNode.getNode(), "$ref");
            ref = container.getLocator().resolve(ref);
        } catch (JsonSchemaException e) {
            report.addMessage(e.getMessage());
            return false;
        }

        if (!refs.add(ref)) {
            report.addMessage("ref loop detected!");
            report.addMessage("ref " + ref + " loops on himself");
        }

        if (!container.contains(ref)) {
            try {
                container = registry.get(ref.getRootAsURI());
                context.setContainer(container);
            } catch (JsonSchemaException e) {
                report.addMessage(e.getMessage());
                return false;
            }
        }

        try {
            schemaNode = container.lookupFragment(ref.getFragment());
            context.setNode(schemaNode);
            return true;
        } catch (JsonSchemaException e) {
            report.addMessage(e.getMessage());
            return false;
        }
    }

    @Override
    public Validator next()
    {
        return isRef ? this
            : new SyntaxCheckerValidator(buildingBlocks, context);
    }

    private static boolean nodeIsRef(final SchemaNode schemaNode)
    {
        final JsonNode node = schemaNode.getNode();
        final JsonNode refNode = node.path("$ref");

        if (!node.isTextual())
            return false;

        try {
            new URI(refNode.textValue());
            return true;
        } catch (URISyntaxException ignored) {
            return false;
        }
    }
}
