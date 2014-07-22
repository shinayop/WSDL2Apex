package com.salesforce.ide.wsdl2apex.core;
/*
 * Copyright (c) 2013, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * BindingMessage
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Jan 18, 2006
 */
public class BindingMessage extends WsdlNode {

    private QName name;
    private Definitions definitions;
    private String type;
    private ArrayList<SoapHeader> headers = new ArrayList<SoapHeader>();
    private SoapBody body;

    public BindingMessage(Definitions definitions, String type) {
        this.definitions = definitions;
        this.type = type;
    }

    public QName getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Iterator<SoapHeader> getHeaders() {
        return headers.iterator();
    }

    public SoapBody getBody() {
        return body;
    }

    void read(WsdlParser parser) throws WsdlParseException {
        String na = parser.getAttributeValue(null, NAME);
        if (na != null) {
          name = new QName(definitions.getTargetNamespace(), na);
        }

        int eventType = parser.getEventType();
        while (true) {
            if (eventType == XmlInputStream.START_TAG) {
                String n = parser.getName();
                String ns = parser.getNamespace();
                if (n != null && ns != null) {
                    parse(n, ns, parser);
                }
            } else if (eventType == XmlInputStream.END_TAG) {
                String name = parser.getName();
                String namespace = parser.getNamespace();
                if (type.equals(name) && WSDL_NS.equals(namespace)) {
                    return;
                }
            }
            eventType = parser.next();
        }
    }

    private void parse(String name, String namespace, WsdlParser parser) throws WsdlParseException {

        if (WSDL_SOAP_NS.equals(namespace)) {

            if (HEADER.equals(name)) {
                SoapHeader header = new SoapHeader();
                header.read(parser);
                headers.add(header);
            } else if (BODY.equals(name)) {
                if (body != null) throw new WsdlParseException("can not have more than one soap:body");
                body = new SoapBody();
                body.read(parser);
            } else if (FAULT.equals(name)) {
                //todo:
            }
        }
    }
}
