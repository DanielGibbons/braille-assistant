package org.example.brailleassistant.utils;

import java.util.HashMap;

// Look up tables for grade 1 braille translation formatting
public class FormattingLUTs {

    public HashMap<String, String> capitals;

    public String[] accents = {"\u0301", "\u030C", "\u0327", "\u0302",
                               "\u0308", "\u0300", "\u030A", "\u0303",
                               "\u0306", "\u0304"};


    public HashMap<String, String> superscripts;

    public HashMap<String, String> subscripts;

    public HashMap<String, String> ligatures;



    public FormattingLUTs() {

        capitals  = new HashMap<String, String>() {{

            put("\u0061", "\u0041"); // A
            put("\u0062", "\u0042"); // B
            put("\u0063", "\u0043"); // C
            put("\u0064", "\u0044"); // D
            put("\u0065", "\u0045"); // E
            put("\u0066", "\u0046"); // F
            put("\u0067", "\u0047"); // G
            put("\u0068", "\u0048"); // H
            put("\u0069", "\u0049"); // I
            put("\u006A", "\u004A"); // J
            put("\u006B", "\u004B"); // K
            put("\u006C", "\u004C"); // L
            put("\u006D", "\u004D"); // M
            put("\u006E", "\u004E"); // N
            put("\u006F", "\u004F"); // O
            put("\u0070", "\u0050"); // P
            put("\u0071", "\u0051"); // Q
            put("\u0072", "\u0052"); // R
            put("\u0073", "\u0053"); // S
            put("\u0074", "\u0054"); // T
            put("\u0075", "\u0055"); // U
            put("\u0076", "\u0056"); // V
            put("\u0077", "\u0057"); // W
            put("\u0078", "\u0058"); // X
            put("\u0079", "\u0059"); // Y
            put("\u007A", "\u005A"); // Z

            // greek
            // comes before every greek letter
            // if letter is capital - capital indicator (32) comes before each indicator

            put("\u03B1", "\u0391"); // ALPHA
            put("\u03B2", "\u0392"); // BETA
            put("\u03B3", "\u0393"); // GAMMA
            put("\u03B4", "\u0394"); // DELTA
            put("\u03B5", "\u0395"); // EPSILON
            put("\u03B6", "\u0396"); // ZETA
            put("\u03B7", "\u0397"); // ETA
            put("\u03B8", "\u0398"); // THETA
            put("\u03B9", "\u0399"); // IOTA
            put("\u03BA", "\u039A"); // KAPPA
            put("\u03BB", "\u039B"); // LAMBDA
            put("\u03BC", "\u039C"); // MU
            put("\u03BD", "\u039D"); // NU
            put("\u03BE", "\u039E"); // XI
            put("\u03BF", "\u039F"); // OMICRON
            put("\u03C0", "\u03A0"); // PI
            put("\u03C1", "\u03A1"); // RHO
            put("\u03C2", "\u03A2"); // SIGMA
            put("\u03C3", "\u03A3"); // TAU
            put("\u03C4", "\u03A4"); // UPSILON
            put("\u03C5", "\u03A5"); // PHI
            put("\u03C6", "\u03A6"); // CHI
            put("\u03C7", "\u03A7"); // PSI
            put("\u03C8", "\u03A8"); // OMEGA

        }};

        superscripts  = new HashMap<String, String>() {{

            put("0", "\u2070");
            put("1", "\u00B9");
            put("2", "\u00B2");
            put("3", "\u00B3");
            put("4", "\u2074");
            put("5", "\u2075");
            put("6", "\u2076");
            put("7", "\u2077");
            put("8", "\u2078");
            put("9", "\u2079");

            put("+", "\u207A");
            put("-", "\u207B");
            put("=", "\u207C");
            put("(", "\u207D");
            put(")", "\u207E");

            put("a", "\u1D43");
            put("b", "\u1D47");
            put("c", "\u1D9C");
            put("d", "\u1D48");
            put("e", "\u1D49");
            put("f", "\u1DA0");
            put("g", "\u1D4D");
            put("h", "\u02B0");
            put("i", "\u2071");
            put("j", "\u02B2");
            put("k", "\u1D4F");
            put("l", "\u02E1");
            put("m", "\u1D50");
            put("n", "\u207F");
            put("o", "\u1D52");
            put("p", "\u1D56");
            put("r", "\u02B3");
            put("s", "\u02E2");
            put("t", "\u1D57");
            put("u", "\u1D58");
            put("v", "\u1D5B");
            put("w", "\u02B7");
            put("x", "\u02E3");
            put("y", "\u02B8");

            put("A", "\u1D2C");
            put("B", "\u1D2E");
            put("D", "\u1D30");
            put("E", "\u1D31");
            put("G", "\u1D33");
            put("H", "\u0234");
            put("I", "\u2035");
            put("J", "\u0236");
            put("K", "\u1D37");
            put("L", "\u1D38");
            put("M", "\u1D39");
            put("N", "\u1D3A");
            put("O", "\u1D3C");
            put("P", "\u1D3E");
            put("R", "\u1D3F");
            put("T", "\u1D40");
            put("U", "\u1D41");
            put("V", "\u2C7D");
            put("W", "\u1D42");

            put("\u03B1", "\u1D45"); // alpha
            put("\u03B2", "\u1D5D"); // beta
            put("\u03B3", "\u1D5E"); // gamma
            put("\u03B4", "\u1D5F"); // delta
            put("\u03B5", "\u1D4B"); // epsilon
            put("\u03B8", "\u1DBF"); // theta
            put("\u03B9", "\u1DA5"); // iota
            put("\u03C6", "\u1DB2"); // phi
            put("\u03C7", "\u1D61"); // chi
            put("\u03C8", "\u1D60"); // psi


        }};

        subscripts  = new HashMap<String, String>() {{

            put("0", "\u2080");
            put("1", "\u2081");
            put("2", "\u2082");
            put("3", "\u2083");
            put("4", "\u2084");
            put("5", "\u2085");
            put("6", "\u2086");
            put("7", "\u2087");
            put("8", "\u2088");
            put("9", "\u2089");

            put("+", "\u208A");
            put("-", "\u208B");
            put("=", "\u208C");
            put("(", "\u208D");
            put(")", "\u208E");

            put("a", "\u2090");
            put("e", "\u2091");
            put("h", "\u2095");
            put("i", "\u1D62");
            put("j", "\u2C7C");
            put("k", "\u2096");
            put("l", "\u2097");
            put("m", "\u2098");
            put("n", "\u2099");
            put("o", "\u2092");
            put("p", "\u209A");
            put("r", "\u1D63");
            put("s", "\u209B");
            put("t", "\u209C");
            put("u", "\u1D64");
            put("v", "\u1D65");
            put("x", "\u2093");

            put("\u03B2", "\u1D66"); // beta
            put("\u03B3", "\u1D67"); // gamma
            put("\u03C1", "\u1D68"); // rho
            put("\u03C7", "\u1D6A"); // chi
            put("\u03C8", "\u1D69"); // psi

        }};

        ligatures  = new HashMap<String, String>() {{

            put("AA", "\uA732");
            put("aa", "\uA733");
            put("AE", "\u00C6");
            put("ae", "\u00E6");
            put("AO", "\uA734");
            put("ao", "\uA735");
            put("AU", "\uA736");
            put("au", "\uA737");
            put("AV", "\uA738");
            put("av", "\uA739");
            put("AY", "\uA73C");
            put("ay", "\uA73D");

            put("ff", "\uFB00");
            put("fi", "\uFB01");
            put("fl", "\uFB02");

            put("OE", "\u0152");
            put("oe", "\u0153");
            put("OO", "\uA74E");
            put("oo", "\uA74F");
            put("st", "\uFB06");
            put("TZ", "\uA728");
            put("tz", "\uA729");
            put("ue", "\u1D68");
            put("VY", "\uA760");
            put("vy", "\uA761");

        }};

    }
}
