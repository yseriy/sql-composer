package local.ys.prototype.sqlcomposer;

class CommonItemHeader implements SchemaItemSQLStringBuilder.SchemaItemHeader {

    private final SchemaItem schemaItem;

    CommonItemHeader(SchemaItem schemaItem) {
        this.schemaItem = schemaItem;
    }

    @Override
    public String getSQLHeaderString() {
        return "(cond->'" + schemaItem.key + "') AS " + schemaItem.key;
    }
}

class DateTimeItemHeader implements SchemaItemSQLStringBuilder.SchemaItemHeader {

    private final SchemaItem schemaItem;

    DateTimeItemHeader(SchemaItem schemaItem) {
        this.schemaItem = schemaItem;
    }

    @Override
    public String getSQLHeaderString() {
        return null;
    }
}
