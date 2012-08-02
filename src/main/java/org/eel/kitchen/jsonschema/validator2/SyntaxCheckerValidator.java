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
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;

import java.util.ArrayList;
import java.util.List;

final class SyntaxCheckerValidator
    implements Validator
{
    private final SchemaBuildingBlocks buildingBlocks;
    private final SchemaContext context;
    private final List<String> messages = new ArrayList<String>();

    SyntaxCheckerValidator(final SchemaBuildingBlocks buildingBlocks,
        final SchemaContext context)
    {
        this.buildingBlocks = buildingBlocks;
        this.context = context;
    }

    @Override
    public boolean validate(final ValidationReport report,
        final JsonNode instance)
    {
        final SyntaxChecker checker = buildingBlocks.getSyntaxChecker();
        checker.checkSyntax(messages, context.getNode().getNode());
        report.addMessages(messages);
        /*
         * Continuation is quite simple: we should only ever continue if the
         * schema is considered valid -- in other words,
         * syntax validation returned no (error) messages.
         */
        return messages.isEmpty();
    }

    @Override
    public Validator next()
    {
        // TODO
        return null;
    }
}
