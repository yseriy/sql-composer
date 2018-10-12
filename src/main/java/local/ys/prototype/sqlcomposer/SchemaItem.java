package local.ys.prototype.sqlcomposer;

import java.util.Objects;

public class SchemaItem implements Comparable<SchemaItem> {

    public Integer schema_id;
    public String key;
    public String title;
    public String type;
    public String sqlType;
    public boolean isFilter;
    public boolean withSelect;
    public boolean withMultiSelect;
    public int orderBy;
    public boolean isShow;

    @Override
    public int compareTo(SchemaItem o) {
        if (o == null)
            return 1;
        return Integer.compare(this.orderBy, o.orderBy);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.schema_id);
        hash = 47 * hash + Objects.hashCode(this.key);
        hash = 47 * hash + Objects.hashCode(this.title);
        hash = 47 * hash + Objects.hashCode(this.type);
        hash = 47 * hash + (this.isFilter ? 1 : 0);
        hash = 47 * hash + (this.withSelect ? 1 : 0);
        hash = 47 * hash + (this.withMultiSelect ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SchemaItem other = (SchemaItem) obj;
        if (!Objects.equals(this.schema_id, other.schema_id))
            return false;
        if (!Objects.equals(this.key, other.key))
            return false;
        if (!Objects.equals(this.title, other.title))
            return false;
        if (!Objects.equals(this.type, other.type))
            return false;
        if (this.isFilter != other.isFilter)
            return false;
        if (this.withSelect != other.withSelect)
            return false;
        return this.withMultiSelect == other.withMultiSelect;
    }

}
