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

package org.eel.kitchen.jsonschema.other;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.*;

public final class SelfValidationTest
{
    private JsonNode draftv3;
    private JsonNode googleAPI;
    private JsonSchema schema;
    private final JsonSchemaFactory factory
        = new JsonSchemaFactory.Builder().build();

    @BeforeClass
    public void setUp()
        throws IOException, JsonSchemaException
    {
        draftv3 = JsonLoader.fromResource("/schema-draftv3.json");
        googleAPI = JsonLoader.fromResource("/other/google-json-api.json");

        final SchemaContainer container = factory.registerSchema(draftv3);
        schema = factory.createSchema(container);
    }

    @Test
    public void testSchemaValidatesItself()
    {

        final ValidationReport report = schema.validate(draftv3);

        assertTrue(report.isSuccess());
    }

    @Test
    public void testGoogleSchemas()
    {
        final Map<String, JsonNode> schemas
            = JacksonUtils.nodeToMap(googleAPI.get("schemas"));

        ValidationReport report;
        String name;
        JsonNode node;

        for (final Map.Entry<String, JsonNode> entry: schemas.entrySet()) {
            name = entry.getKey();
            node = entry.getValue();
            report = schema.validate(node);
            assertTrue(report.isSuccess(), name);
        }
    }
}
