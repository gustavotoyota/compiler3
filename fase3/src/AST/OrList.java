/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AST;

import java.util.ArrayList;

/**
 *
 * @author Gustavo
 */
public class OrList {
    public final ArrayList<OrTest> orTests;

    public OrList(ArrayList<OrTest> orTests) {
        this.orTests = orTests;
    } 
}
