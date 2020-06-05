/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adriens.cate.conso.plus.sdk;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salad74
 */
public class CarteConsoCrawler {

    /**
     * @return the adresse
     */
    public String getAdresse() {
        return adresse;
    }

    /**
     * @param adresse the adresse to set
     */
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public static final String URL_ROOT = "http://www.consoplus.nc/";
    public static final String URL_LOGIN = URL_ROOT + "compte.asp";
    
    final static Logger logger = LoggerFactory.getLogger(CarteConsoCrawler.class);

    private String noCompte;
    private String password;

    private String nom;
    private String prenom;
    private String email;
    private int solde;
    private int soldeValeur;
    private String soldeDescription;
    private String adresse;
    private String telephone;
    private String mobilis;

    private HtmlPage accountPage;

    // Constructors
    public CarteConsoCrawler() {
    }

    public CarteConsoCrawler(String aCompte, String aPasswd) throws IOException {
        super();
        setNoCompte(aCompte);
        setPassword(aPasswd);
        try {
            logger.info("Getting datas back from website for compte <" + getNoCompte() + "> ...");
            fillup();
            logger.info("Sucessfully retrieved datas from website.");
        } catch (IOException ex) {
            logger.error("Unable to fetch datas : " + ex.getMessage());
            throw ex;
        }
    }

    public static String getMagazineUrl(int numMagazine){
        String out = "https://www.flipsnack.com/KICOM/conso-n-" + numMagazine + "/full-view.html";
        return out;
    }
    public static String getMagazineUrl(){
        return "https://www.flipsnack.com/KICOM/";
    }

    // Some static cleanup methods
    public static final int extractPoints(String pointText) {
        ///html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[8]/span
        String tmp = pointText;
        tmp = tmp.replace(" points", "");
        return Integer.parseInt(tmp);
    }

    public static String extractNom(String rawNomInput) {
        String out = rawNomInput;
        out = out.replace("- Nom : ", "");
        return out;
    }

    public static String extractPrenom(String rawPrenomInput) {
        String out = rawPrenomInput;
        out = out.replace("- Prénom : ", "");
        return out;
    }

    public static String extractAdresse(String rawAdressInput) {
        String out = rawAdressInput;
        out = out.replace("- Adresse : ", "");
        return out;
    }

    public static String extractEmail(String rawEmailInput) {
        String out = rawEmailInput;
        out = out.replace("- Email : ", "");
        return out;
    }

    public static String extractTelephone(String rawTelephoneInput) {
        String out = rawTelephoneInput;
        out = out.replace("- Tél : ", "");
        return out;
    }

    public static String extractMobilis(String rawMobilisInput) {
        String out = rawMobilisInput;
        out = out.replace("- Mobilis : ", "");
        return out;
    }

    // Pure htmlunit WebClient mthode
    private static WebClient buildWebClient() {
        // Disable verbose logs
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        return webClient;
    }
    
    public static MagazineConsoPlus getLatestMagazine() throws Exception{
        return getConsoPlusMagazines().get(0);
    }

    public static ArrayList<MagazineConsoPlus> getConsoPlusMagazines() throws Exception {
        WebClient webClient = buildWebClient();
        HtmlPage htmlPage = webClient.getPage(MagazineConsoPlus.URL_MAGAZINES);
        List<HtmlElement> magsDivisions = htmlPage.getByXPath("//span[@class='col_mag']//a");
        logger.info("Found <" + magsDivisions.size() + "> magazines");
        logger.info("==========================================================================");
        
        ArrayList<MagazineConsoPlus> out = new ArrayList<>();
        
        
        for (int i = 0; i < magsDivisions.size() ; i++){
            MagazineConsoPlus aMag = new MagazineConsoPlus();
    
            String fullUrl = magsDivisions.get(i).getAttribute("href");
            aMag.setUrlFullView(fullUrl);
            //logger.info(magsDivisions.get(i).asXml());
            logger.info("Full URL: <" + fullUrl + ">");
            
            String urlCoverImage = URL_ROOT + magsDivisions.get(i).getFirstByXPath("string(./img/@src)");
            aMag.setUrlCoverImage(urlCoverImage);
            logger.info("Cover URL: <" + urlCoverImage + ">");
            
            String title = magsDivisions.get(i).getFirstByXPath("string(./img/@title)");
            aMag.setTitle(title);
            logger.info("Title: <" + title + ">");
            
            int numMagazine = MagazineConsoPlus.extractNumMagFromFlipsnackUrl(fullUrl);
            aMag.setNumMag(numMagazine);
            logger.info("Magazine number: <" + numMagazine  + ">");
            logger.info("==========================================================================");
            out.add(aMag);
            i++;
        }
        return out;
    }
    
    
    // The method that does the magic : login then crawle the website to get datas
    public void fillup() throws IOException {
        WebClient webClient = buildWebClient();
        HtmlPage htmlPage = webClient.getPage(URL_LOGIN);
        logger.info("Provided login : " + getNoCompte());
        logger.debug("Provided passwd : " + getPassword());

        HtmlForm htmlForm = htmlPage.getFormByName("form1");
        HtmlTextInput loginInput = (HtmlTextInput) htmlForm.getInputByName("login");
        loginInput.setValueAttribute(this.getNoCompte());
        logger.debug("Login as Text : " + loginInput.asText());
        logger.debug("Retrieved login : " + loginInput.getTextContent());

        HtmlPasswordInput passwordInput = (HtmlPasswordInput) htmlForm.getInputByName("password");
        passwordInput.setValueAttribute(this.getPassword());
        logger.debug("passwd as Text : " + passwordInput.asText());

        HtmlSubmitInput button = htmlForm.getFirstByXPath("//*[@id=\"button\"]");
        this.accountPage = (HtmlPage) button.click();
        // solde
        String tmpSolde = ((HtmlElement) (this.accountPage.getFirstByXPath("/html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[8]/span"))).asText();
        int lSolde = CarteConsoCrawler.extractPoints(tmpSolde);
        setSolde(lSolde);
        logger.info("Found points : <" + getSolde() + ">");

        // details solde
        ///html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[9]
        String details = ((HtmlElement) (this.accountPage.getFirstByXPath("/html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[9]"))).asText();
        details = details.replace("\n", "").replace("\r", "");
        setSoldeDescription(details);
        logger.info("Details : <" + getSoldeDescription() + ">");
        
        // valeur solde
        fillUpSoldeValeur();
        
        //nom
        // /html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[2]
        String nom = ((HtmlElement) (this.accountPage.getFirstByXPath("/html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[2]"))).asText();
        nom = CarteConsoCrawler.extractNom(nom);
        setNom(nom);
        logger.info("Nom : <" + getNom() + ">");

        // prenom
        String prenom = ((HtmlElement) (this.accountPage.getFirstByXPath("/html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[3]"))).asText();
        prenom = CarteConsoCrawler.extractPrenom(prenom);
        setPrenom(prenom);
        logger.info("Prenom : <" + getPrenom() + ">");

        // Adresse
        String adresse = ((HtmlElement) (this.accountPage.getFirstByXPath("/html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[4]"))).asText();
        adresse = CarteConsoCrawler.extractAdresse(adresse);
        setAdresse(adresse);
        logger.info("Adresse : <" + getAdresse() + ">");

        // email
        String email = ((HtmlElement) (this.accountPage.getFirstByXPath("/html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[7]"))).asText();
        email = CarteConsoCrawler.extractEmail(email);
        setEmail(email);
        logger.info("eMail : <" + getEmail() + ">");

        // telephone fixe
        String telephone = ((HtmlElement) (this.accountPage.getFirstByXPath("/html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[5]"))).asText();
        telephone = CarteConsoCrawler.extractTelephone(telephone);
        setTelephone(telephone);
        logger.info("Telephone : <" + getTelephone() + ">");

        // mobilis
        String mobilis = ((HtmlElement) (this.accountPage.getFirstByXPath("/html/body/div/div[2]/div/section[1]/div/div/article/div[2]/p[6]"))).asText();
        mobilis = CarteConsoCrawler.extractMobilis(mobilis);
        setMobilis(mobilis);
        logger.info("Telephone : <" + getMobilis() + ">");

    }
    
    void fillUpSoldeValeur() {
        // valeur solde
        String regExp = "(\\D*\\d*\\D*)(\\d*)";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(this.soldeDescription);
        String strSoldeValue = "0";
        if (matcher.find()) strSoldeValue = matcher.group(2);
        setSoldeValeur(Integer.parseInt(strSoldeValue));
        logger.info("Found money : <" + getSoldeValeur() + ">");
    }

    // Getters and setters
    /**
     * @return the noCompte
     */
    public String getNoCompte() {
        return noCompte;
    }

    /**
     * @param noCompte the noCompte to set
     */
    public void setNoCompte(String noCompte) {
        this.noCompte = noCompte;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the solde
     */
    public int getSolde() {
        return solde;
    }

    /**
     * @param solde the solde to set
     */
    public void setSolde(int solde) {
        this.solde = solde;
    }
    
    /**
     * @return the money value of the solde
     */
    public int getSoldeValeur() {
        return soldeValeur;
    }

    /**
     * @param soldeValeur the money value of the solde to set
     */
    public void setSoldeValeur(int soldeValeur) {
        this.soldeValeur = soldeValeur;
    }

    /**
     * @return the soldeDescription
     */
    public String getSoldeDescription() {
        return soldeDescription;
    }

    /**
     * @return the telephone
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * @param telephone the telephone to set
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return the mobilis
     */
    public String getMobilis() {
        return mobilis;
    }

    /**
     * @param mobilis the mobilis to set
     */
    public void setMobilis(String mobilis) {
        this.mobilis = mobilis;
    }

    /**
     * @param soldeDescription the soldeDescription to set
     */
    public void setSoldeDescription(String soldeDescription) {
        this.soldeDescription = soldeDescription;
    }

    public String toString() {
        return "Solde de <" + getNoCompte() + "> : <" + getSolde() + ">";
    }

    public static List<Partner> getPartners() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Partners.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Partners parts = (Partners) jaxbUnmarshaller.unmarshal(Partners.class.getResourceAsStream("/partners.xml"));
        return parts.getPartners();
    }

    // Yet a dummy main to show how to deal with the class
    public static void main(String[] args) {
        try {
            
            ArrayList<MagazineConsoPlus> mags = CarteConsoCrawler.getConsoPlusMagazines();
            System.out.println("Latest mag: <" + mags.get(0).toString() + ">");
            /*String login = "YOUR_LOGIN";
            String passwd = "YOUR_PASSWORD";
            CarteConsoCrawler wrap = new CarteConsoCrawler(login, passwd);
            logger.info(wrap.toString());
            logger.info("Bye.");
            logger.info("Found <" + CarteConsoCrawler.getPartners().size() + "> partners.");
            */
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
