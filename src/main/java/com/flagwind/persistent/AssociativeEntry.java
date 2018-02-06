package com.flagwind.persistent;


import com.flagwind.persistent.annotation.Associative;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class AssociativeEntry {


    private String name;
    private String source;
    private String extras;
    private Object value;



    public AssociativeEntry(String name, String source, Object value, String extras) {
        this.name = name;
        this.source = source;
        this.extras = extras;
        this.value = value;
    }

    public AssociativeEntry(Associative associative, Object value) {
        this.name = associative.name();
        this.source = associative.source();
        this.extras = associative.extras();
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getAssociateValue() {
        AssociativeProvider provider = DiscoveryFactory.instance().resolve(this.source);
        if (StringUtils.isEmpty(extras)) {
            return provider.associate(this.value);
        } else {
            return provider.associate(Arrays.asList(this.value, this.extras).toArray());
        }
    }

    public void excute(ExtensibleObject extensibleObject) {
        extensibleObject.set(this.name, this.getAssociateValue());
    }
}
