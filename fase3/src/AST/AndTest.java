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
public class AndTest {
    public final ArrayList<NotTest> notTests;

    public AndTest(ArrayList<NotTest> notTests) {
        this.notTests = notTests;
    }
}
