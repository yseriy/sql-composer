package local.ys.prototype.sqlcomposer;

import java.util.List;
import java.util.function.Function;

public class SchemaItemSQLStringBuilder {

    interface SchemaItemFilter {

        String getSQLConditionString();

        boolean isNotEmptyFilter();
    }

    interface SchemaItemHeader {

        String getSQLHeaderString();
    }

    public String getSQLHeadersString(List<SchemaItem> schema) {
        String separator = ", ";
        StringBuilder stringBuilder = new StringBuilder();

        schema.stream()
                .filter((schemaItem -> schemaItem.isShow))
                .map(this::createSchemaItemHeader)
                .map(SchemaItemHeader::getSQLHeaderString)
                .forEach((headerString) -> stringBuilder.append(headerString).append(separator));

        return removeLastSeparator(stringBuilder, separator);
    }

    private SchemaItemHeader createSchemaItemHeader(SchemaItem schemaItem) {
        if (schemaItem.type.equalsIgnoreCase("datetime")) {
            return new DateTimeItemHeader(schemaItem);
        } else {
            return new CommonItemHeader(schemaItem);
        }
    }

    public String getSQLConditionString(List<SchemaItem> schema, Function<String, String> getFilterString) {
        String separator = " AND ";
        StringBuilder stringBuilder = new StringBuilder();

        schema.stream()
                .filter((schemaItem) -> schemaItem.isFilter)
                .map((schemaItem) -> createSchemaItemFilter(schemaItem, getFilterString))
                .filter(SchemaItemFilter::isNotEmptyFilter)
                .map(SchemaItemFilter::getSQLConditionString)
                .forEach((conditionString) -> stringBuilder.append(conditionString).append(separator));

        return removeLastSeparator(stringBuilder, separator);
    }

    private SchemaItemFilter createSchemaItemFilter(SchemaItem schemaItem, Function<String, String> getFilterString) {
        if (schemaItem.type.equalsIgnoreCase("datetime")) {
            return new DateTimeItemFilter(schemaItem, getFilterString.apply(schemaItem.key));
        } else {
            return new CommonItemFilter(schemaItem, getFilterString.apply(schemaItem.key));
        }
    }

    private String removeLastSeparator(StringBuilder stringBuilder, String separator) {
        int indexOfLastSeparator = stringBuilder.lastIndexOf(separator);
        int indexOfLastSymbol = stringBuilder.length() - 1;
        return stringBuilder.delete(indexOfLastSeparator, indexOfLastSymbol).toString();
    }
}
