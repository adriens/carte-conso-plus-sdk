/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adriens.cate.conso.plus.sdk;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author salad74
 */
@XmlRootElement(name = "partners")
@XmlAccessorType(XmlAccessType.FIELD)
public class Partners {

    @XmlElement(name = "partner")
    private List<Partner> partners = null;

    public Partners() {
        partners = new ArrayList<Partner>();
    }

    public List<Partner> getPartners() {
        return partners;
    }

    public void setPartners(List<Partner> partners) {
        this.partners = partners;
    }

    public static void main(String[] args) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Partners.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //We had written this file in marshalling example
        Partners parts = (Partners) jaxbUnmarshaller.unmarshal(Partners.class.getResourceAsStream("/partners.xml"));

        for (Partner part : parts.getPartners()) {
            System.out.println(part.getName());
            System.out.println(part.getWwwHomePage());
            System.out.println("-------------------------------------------------");
        }
    }
}
