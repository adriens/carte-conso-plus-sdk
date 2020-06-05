/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adriens.cate.conso.plus.sdk;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author meilie
 */
public class CarteConsoCrawlerTest {
    
    public CarteConsoCrawlerTest() {
    }
    
    /**
     * Test of getSoldeValeur method, of class CarteConsoCrawler.
     */
    @Test
    public void testGetSoldeValeur() {
        System.out.println("testGetSoldeValeur");
        CarteConsoCrawler instance = new CarteConsoCrawler();
        int expResult = 1000;
        instance.setSoldeValeur(expResult);
        int result = instance.getSoldeValeur();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of fillup method, of class CarteConsoCrawler.
     */
    @Test
    public void testFillupSoldeValeur() throws Exception {
        System.out.println("testFillupSoldeValeur");
        CarteConsoCrawler instance = new CarteConsoCrawler();
        instance.setSoldeDescription("Avec 803 points, vous pouvez bénéficier d'un bon d'achat de 1000 F. Il vous restera 3 points.");
        instance.fillUpSoldeValeur();
        int expResult = 1000;
        int result = instance.getSoldeValeur();
        System.out.println("soldeDescription: "+instance.getSoldeDescription()+", result: "+result);
        assertEquals(expResult, result);
    }
    
}
