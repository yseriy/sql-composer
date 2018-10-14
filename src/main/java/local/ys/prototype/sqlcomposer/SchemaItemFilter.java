package local.ys.prototype.sqlcomposer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class AbstractItemFilter {

    private final SchemaItem schemaItem;
    private final String[] filters;

    AbstractItemFilter(SchemaItem schemaItem, String filter) {
        this.schemaItem = schemaItem;

        if (filter == null || filter.isEmpty()) {
            this.filters = new String[0];
        } else if (schemaItem.withMultiSelect) {
            this.filters = filter.split(",");
        } else {
            this.filters = new String[]{filter};
        }
    }

    boolean isEmptyFilter() {
        return filters.length == 0;
    }

    SchemaItem getSchemaItem() {
        return schemaItem;
    }

    String[] getFilters() {
        return filters;
    }
}

class CommonItemFilter extends AbstractItemFilter implements SchemaItemSQLStringBuilder.SchemaItemFilter {

    private final StringBuilder stringBuilder;

    CommonItemFilter(SchemaItem schemaItem, String filter) {
        super(schemaItem, filter);
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public String getSQLConditionString() {
        if (isEmptyFilter()) {
            return "";
        }

        String inPrefix = "IN (";

        for (String filter : getFilters()) {
            stringBuilder.append(inPrefix);
            inPrefix = ", ";
            stringBuilder.append(filter);
        }

        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public boolean isNotEmptyFilter() {
        return !isEmptyFilter();
    }
}

class DateTimeItemFilter extends AbstractItemFilter implements SchemaItemSQLStringBuilder.SchemaItemFilter {

    private static final String DATE_RANGE_REGEXP =
            "^FROM_(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d)" +
                    "_TO_(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d)$";
    private static final Pattern DATE_RANGE_PATTERN = Pattern.compile(DATE_RANGE_REGEXP);
    private static final int FROM_DATE = 1;
    private static final int TO_DATE = 2;

    private final StringBuilder dateRangeStringBuilder;
    private final StringBuilder inStringBuilder;

    DateTimeItemFilter(SchemaItem schemaItem, String filter) {
        super(schemaItem, filter);
        this.dateRangeStringBuilder = new StringBuilder();
        this.inStringBuilder = new StringBuilder();
    }

    @Override
    public String getSQLConditionString() {
        String dataRangePrefix = "";
        String inPrefix = "IN (";

        for (String filter : getFilters()) {
            Matcher matcher = DATE_RANGE_PATTERN.matcher(filter);

            if (matcher.find()) {
                dateRangeStringBuilder.append(dataRangePrefix);
                dataRangePrefix = " OR ";
                dateRangeStringBuilder.append(getSchemaItem().key).append(" >= ").append(matcher.group(FROM_DATE));
                dateRangeStringBuilder.append(" AND ");
                dateRangeStringBuilder.append(getSchemaItem().key).append(" <= ").append(matcher.group(TO_DATE));
            } else {
                inStringBuilder.append(inPrefix);
                inPrefix = ", ";
                inStringBuilder.append(filter);
            }
        }

        if (inStringBuilder.length() != 0) {
            inStringBuilder.append(")");
        }

        if (dateRangeStringBuilder.length() != 0 && inStringBuilder.length() != 0) {
            dateRangeStringBuilder.append(" OR ");
        }

        return dateRangeStringBuilder.append(inStringBuilder).toString();
    }

    @Override
    public boolean isNotEmptyFilter() {
        return !isEmptyFilter();
    }
}
