package AST;

import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gustavo
 */
public class Term {
    public final ArrayList<Factor> factors;
    public final ArrayList<String> opers;
    
    public Term(ArrayList<Factor> factors, ArrayList<String> opers) {
        this.factors = factors;
        this.opers = opers;
    }
}
