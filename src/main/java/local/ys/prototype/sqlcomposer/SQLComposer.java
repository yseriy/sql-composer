package local.ys.prototype.sqlcomposer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SQLComposer {

    private static final Pattern DATE_RANGE_REGEXP = Pattern.compile("^FROM_(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d)_TO_(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d)$");
    private final StringBuilder stringBuilder = new StringBuilder();
    private final StringBuilder dataRangeStringBuffer = new StringBuilder();
    private final StringBuilder inStringBuffer = new StringBuilder();

    private static class ItemFilter {
        private final SchemaItem schemaItem;
        private final String[] filters;

        ItemFilter(SchemaItem schemaItem) {
            this(schemaItem, null);
        }

        ItemFilter(SchemaItem schemaItem, String[] filters) {
            this.schemaItem = schemaItem;
            this.filters = filters;
        }

        boolean isNotEmptyFilter() {
            return filters != null;
        }

        boolean isDateTimeItem() {
            return schemaItem.type.equalsIgnoreCase("datetime");
        }
    }

    public String formatSQL(List<SchemaItem> schema) {
        Stream<SchemaItem> schemaItemStream = schema.stream().filter((item) -> item.isFilter);
        ItemFilter[] nonEmptyItemFilters = schemaItemStream.map(this::createItemFilter)
                .filter(ItemFilter::isNotEmptyFilter).toArray(ItemFilter[]::new);

        return composeSQLString(nonEmptyItemFilters);
    }

    private ItemFilter createItemFilter(SchemaItem item) {
        String filter = getFilter(item.key);

        if (isFilterEmpty(filter)) {
            return new ItemFilter(item);
        }

        if (item.withMultiSelect) {
            return new ItemFilter(item, filter.split(","));
        }

        return new ItemFilter(item, new String[]{filter});
    }

    private String getFilter(String key) {
        return null;
    }

    private boolean isFilterEmpty(String filter) {
        return filter == null || filter.isEmpty();
    }

    private String composeSQLString(ItemFilter[] itemFilters) {
        stringBuilder.setLength(0);
        String prefix = "";

        for (ItemFilter itemFilter : itemFilters) {
            stringBuilder.append(prefix);
            prefix = " AND ";

            if (itemFilter.isDateTimeItem()) {
                stringBuilder.append(sqlDatetimeFilter(itemFilter));
            } else {
                stringBuilder.append(sqlFilter(itemFilter));
            }
        }

        return stringBuilder.toString();
    }

    private String sqlFilter(ItemFilter itemFilter) {
        inStringBuffer.setLength(0);
        String inPrefix = "IN (";

        for (String filter : itemFilter.filters) {
            inStringBuffer.append(inPrefix);
            inPrefix = ", ";
            inStringBuffer.append(filter);
        }

        inStringBuffer.append(")");
        return inStringBuffer.toString();
    }

    private String sqlDatetimeFilter(ItemFilter itemFilter) {
        dataRangeStringBuffer.setLength(0);
        inStringBuffer.setLength(0);

        String dataRangePrefix = "";
        String inPrefix = "IN (";

        for (String filter : itemFilter.filters) {
            Matcher matcher = DATE_RANGE_REGEXP.matcher(filter);

            if (matcher.find()) {
                dataRangeStringBuffer.append(dataRangePrefix);
                dataRangePrefix = " AND ";
                dataRangeStringBuffer.append(itemFilter.schemaItem.key);
                dataRangeStringBuffer.append(" >= ");
                dataRangeStringBuffer.append(matcher.group(1));
                dataRangeStringBuffer.append(" AND ");
                dataRangeStringBuffer.append(itemFilter.schemaItem.key);
                dataRangeStringBuffer.append(" <= ");
                dataRangeStringBuffer.append(matcher.group(2));
            } else {
                inStringBuffer.append(inPrefix);
                inPrefix = ",";
                inStringBuffer.append(filter);
            }
        }

        if (inStringBuffer.length() != 0) {
            inStringBuffer.append(")");
        }

        if (dataRangeStringBuffer.length() != 0 && inStringBuffer.length() != 0) {
            dataRangeStringBuffer.append(" AND ");
        }

        return dataRangeStringBuffer.append(inStringBuffer).toString();
    }
}
