/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adriens.cate.conso.plus.sdk;

/**
 *
 * @author salad74
 */
public class Partner {

    private String name;
    private String logoUrl;
    private String wwwHomePage;

    /**
     * @return the name
     */
    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the logoUrl
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @param logoUrl the logoUrl to set
     */
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    /**
     * @return the wwwHomePage
     */
    public String getWwwHomePage() {
        return wwwHomePage;
    }

    /**
     * @param wwwHomePage the wwwHomePage to set
     */
    public void setWwwHomePage(String wwwHomePage) {
        this.wwwHomePage = wwwHomePage;
    }

}
