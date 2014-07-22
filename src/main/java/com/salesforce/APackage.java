package com.salesforce.ide.wsdl2apex.core;

import com.google.common.collect.Maps;

import java.util.HashMap;

public class APackage extends ABase {

    private final String packageName;
    private final HashMap<String, AClass> classes = Maps.newLinkedHashMap();

    public APackage(String packageName, Definitions definitions, ApexTypeMapper typeMapper) {
        super(definitions, typeMapper);
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void load(Schema schema) throws CalloutException, ConnectionException {
        for(ComplexType ct: schema.getComplexTypes()) { 
            AClass aclass = new ComplexTypeClass(ct, definitions, typeMapper);
            addClass(aclass);
        }
    }

    void addClass(AClass ac) throws CalloutException {
        if (classes.containsKey(ac.getName())) {
            throw new CalloutException("Class name '" + ac.getName() + "' already in use. " +
                    "Please edit WSDL to remove repeated names");
        }
        classes.put(ac.getName(), ac);
    }

    public String getCode() throws CalloutException {
        AWriter writer = new AWriter();
        write(writer);
        return writer.toString();
    }

    /** This creates and adds a synchronous binding/stub to the package */
    public void load(Binding binding) throws CalloutException, ConnectionException {
        AClass aclass = new SyncBindingClass(this, binding, definitions, typeMapper);
        addClass(aclass);
   }

    /** This creates and adds an asynchronous binding/stub to the package */
    public void loadAsync(Packages packages, Binding binding) throws CalloutException, ConnectionException {
        addClass(new AsyncBindingClass(packages, this, binding, definitions, typeMapper));
    }
    
    public void write(AWriter writer) throws CalloutException {
        writer.writeComment("Generated by wsdl2apex");
        writer.writeLine();
        writer.writeLine("public class ", packageName, " {");

        for (AClass aclass : classes.values()) {
            aclass.write(writer);
        }
        writer.writeLine("}");
    }
}
