package org.eel.kitchen.jsonschema.validator2;

import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;

public final class SchemaContext
{
    private SchemaContainer container;
    private SchemaNode node;

    SchemaContext(final SchemaContainer container, final SchemaNode node)
    {
        this.container = container;
        this.node = node;
    }

    SchemaContainer getContainer()
    {
        return container;
    }

    void setContainer(final SchemaContainer container)
    {
        this.container = container;
    }

    SchemaNode getNode()
    {
        return node;
    }

    void setNode(final SchemaNode node)
    {
        this.node = node;
    }
}
