/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adriens.cate.conso.plus.sdk;

/**
 *
 * @author 3004SAL
 */
public class MagazineConsoPlus {

    private String urlFullView;
    private String urlCoverImage;
    private String title;
    private int numMag;




    
    public static final String URL_MAGAZINES = "http://www.consoplus.nc/magazine.asp";
    
    
    public MagazineConsoPlus(){
        
    }
    
    public static int extractNumMagFromFlipsnackUrl(String flipsnackUrl) throws Exception{
        if(flipsnackUrl == null){
            throw new Exception("Cannot extract Magazine number from null url");
        }
        int out = 0;
        if(flipsnackUrl.contains("https://www.flipsnack.com/KICOM/le-magazine-conso-")){
            // https://www.flipsnack.com/KICOM/le-magazine-conso-juin-2019-n-178.html
            String tmp = flipsnackUrl;
            tmp = tmp.substring(flipsnackUrl.lastIndexOf("-") + 1, flipsnackUrl.length()-5);
            //tmp = tmp.replace(".html", "");
            out = Integer.parseInt(tmp);
            return out;
        }
        else if (flipsnackUrl.contains("https://www.flipsnack.com/KICOM/conso-n-")){
            // https://www.flipsnack.com/KICOM/conso-n-187/full-view.html
           
            String tmp = flipsnackUrl.replace("https://www.flipsnack.com/KICOM/conso-n-", "");
            tmp = tmp.replace("/full-view.html", "");
            tmp = tmp.replace(".html", "");
            if(tmp.contains("-")){
                tmp = tmp.substring(0, tmp.indexOf("-") - 1);
            }
            
            out = Integer.parseInt(tmp);
            return out;
        }
        else{
            throw new Exception("Unable to get magazine id from url: <" + flipsnackUrl + ">");
        }
        
        
        
    }
    /**
     * @return the numMag
     */
    public int getNumMag() {
        return numMag;
    }

    /**
     * @param numMag the numMag to set
     */
    public void setNumMag(int numMag) {
        this.numMag = numMag;
    }

    
    public String toString(){
        if(title == null){
            return "";
        }
        else{
            return getTitle();
        }
    }
    /**
     * @return the urlFullView
     */
    public String getUrlFullView() {
        return urlFullView;
    }

    /**
     * @param urlFullView the urlFullView to set
     */
    public void setUrlFullView(String urlFullView) {
        this.urlFullView = urlFullView;
    }

    /**
     * @return the urlCoverImage
     */
    public String getUrlCoverImage() {
        return urlCoverImage;
    }

    /**
     * @param urlCoverImage the urlCoverImage to set
     */
    public void setUrlCoverImage(String urlCoverImage) {
        this.urlCoverImage = urlCoverImage;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
