// package com.flagwind.persistent;

// import com.flagwind.persistent.annotation.Associative;
// import java.util.Arrays;

// import org.apache.commons.lang3.StringUtils;

// public class AssociativeEntry {

//     private String name;
//     private String source;
//     private String extras;


//     public AssociativeEntry(String name, String source, String extras) {
//         this.name = name;
//         this.source = source;
//         this.extras = extras;
//     }

//     public AssociativeEntry(String name, String source) {
//         this.name = name;
//         this.source = source;
//     }

//     public AssociativeEntry(Associative associative) {
//         this.name = associative.name();
//         this.source = associative.source();
//         this.extras = associative.extras();
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public String getSource() {
//         return source;
//     }

//     public void setSource(String source) {
//         this.source = source;
//     }

//     public String getExtras() {
//         return extras;
//     }

//     public void setExtras(String extras) {
//         this.extras = extras;
//     }

//     public Object getAssociateValue(Object value) {
//         AssociativeProvider provider = DiscoveryFactory.instance().resolve(this.source);
//         if (StringUtils.isEmpty(extras)) {
//             return provider.associate(value);
//         } else {
//             return provider.associate(Arrays.asList(value, this.extras).toArray());
//         }
//     }


//     public void excute(ExtensibleObject extensibleObject,Object value) {
//         extensibleObject.set(this.name, this.getAssociateValue(value));
//     }
// }
