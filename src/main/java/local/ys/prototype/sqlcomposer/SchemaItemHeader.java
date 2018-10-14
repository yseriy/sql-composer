package local.ys.prototype.sqlcomposer;

abstract class AbstractItemHeader {

    private final SchemaItem schemaItem;

    AbstractItemHeader(SchemaItem schemaItem) {
        this.schemaItem = schemaItem;
    }

    SchemaItem getSchemaItem() {
        return schemaItem;
    }
}

class CommonItemHeader extends AbstractItemHeader implements SchemaItemSQLStringBuilder.SchemaItemHeader {

    CommonItemHeader(SchemaItem schemaItem) {
        super(schemaItem);
    }

    @Override
    public String getSQLHeaderString() {
        return "(cond->'" + getSchemaItem().key + "') AS " + getSchemaItem().key;
    }
}

class DateTimeItemHeader extends AbstractItemHeader implements SchemaItemSQLStringBuilder.SchemaItemHeader {

    DateTimeItemHeader(SchemaItem schemaItem) {
        super(schemaItem);
    }

    @Override
    public String getSQLHeaderString() {
        return null;
    }
}
