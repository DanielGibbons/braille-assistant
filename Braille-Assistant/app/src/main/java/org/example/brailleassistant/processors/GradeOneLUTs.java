package org.example.brailleassistant.processors;

import java.util.HashMap;

// Look up tables for grade 1 braille translation
public class GradeOneLUTs {

    final public int[] prefixes = {8, 16, 24, 32, 40, 43, 51, 54, 56, 60};

    public HashMap<Integer, String> prefix_8;

    public HashMap<Integer, String> prefix_8_32;

    public HashMap<Integer, String> prefix_16;

    public HashMap<Integer, String> prefix_16_32;

    public HashMap<Integer, String> prefix_24;

    public HashMap<Integer, String> prefix_32;

    public HashMap<Integer, String> prefix_32_32;

    public HashMap<Integer, String> prefix_40;

    public HashMap<Integer, String> prefix_43;

    public HashMap<Integer, String> prefix_43_60;

    public HashMap<Integer, String> prefix_51;

    public HashMap<Integer, String> prefix_54;

    public HashMap<Integer, String> prefix_56;

    public HashMap<Integer, String> prefix_60;

    public HashMap<Integer, String> numberMode;

    public HashMap<Integer, String> letters;

    public HashMap<Integer, String> numbers;

    public HashMap<Integer, String> punctuation;

    //    ACCENT INTEGER ENCODING
    //    201 - acute
    //    202 - caron
    //    203 - cedilla
    //    204 - circumflex
    //    205 - diaeresis
    //    206 - grave
    //    207 - ring
    //    208 - tilde
    //    209 - breve
    //    210 - macron
    //    211 - ligature
    //    212 - subscript
    //    213 - superscript

    public GradeOneLUTs() {

        prefix_8 = new HashMap<Integer, String>() {{

            put(47, "\u0026"); // ampersand
            put(35, "\u3008"); // opening angle bracket
            put(28, "\u3009"); // closing angle bracket
            put(9, "\u00A2"); // cent
            put(14, "\u0024"); // dollar
            put(17, "\u20AC"); // euro
            put(7, "\u00A3"); // pound
            put(61, "\u00A5"); // yen

            put(44, "209"); // breve
            put(36, "210"); // macron

            put(20, "\u007E"); // tilde
            put(34, "\u005E"); // math power
            put(1, "\u0040"); // @
            put(32, "DAGGER_INDICATOR");

            put(6, "<ss>"); // SCRIPT_SYMBOL
            put(2, "<sw>"); // SCRIPT_WORD
            put(54, "<s>"); // SCRIPT_PASSAGE
            put(4, "</s>"); // SCRIPT_TERMINATOR

        }};

        prefix_8_32 = new HashMap<Integer, String>() {{

            put(57, "\u2020"); // dagger
            put(59, "\u2021"); // double dagger

        }};

        prefix_16 = new HashMap<Integer, String>() {{

            put(20, "\u002A"); // asterisk
            put(35, "\u0028"); // opening round bracket
            put(28, "\u0029"); // closing round bracket
            put(22, "\u002B"); // addition sign
            put(38, "\u00D7"); // multiplication sign
            put(12, "\u00F7");  // division sign
            put(54, "\u003D"); // equals sign
            put(2, "\u3003"); // ditto
            put(32, "EM_DASH_INDICATOR");
            put(36, "\u2212"); // en dash or minus sign

        }};

        prefix_16_32 = new HashMap<Integer, String>() {{

            put(36, "\u2014"); // em dash

        }};

        prefix_24 = new HashMap<Integer, String>() {{

            put(45, "\u2640"); // female gender sign
            put(61, "\u2642"); // male gender sign
            put(38, "\u201C"); // opening double quotation
            put(52, "\u201D"); // closing double quotation
            put(9, "\u00A9"); // copyright
            put(23, "\u00AE"); // registered
            put(30, "\u2122"); // trademark

            // Accented letters
            //  if letter is capital capital indicator comes before accent indicator which in turn comes before the letter
            put(12, "201"); // acute
            put(44, "202"); // caron
            put(47, "203"); // cedilla
            put(41, "204"); // circumflex
            put(18, "205"); // diaeresis
            put(33, "206"); // grave
            put(43, "207"); // ring
            put(59, "208"); // tilde

            put(15, "\u2761"); // paragraph
            put(14, "\u00A7"); // section
            put(26, "\u00B0"); // degree

            put(6, "<bs>"); // BOLD_SYMBOL
            put(2, "<bw>"); // BOLD_WORD
            put(54, "<b>"); // BOLD_PASSAGE
            put(4, "</b>"); // BOLD_TERMINATOR

            put(22, "211"); // LIGATURE

        }};

        prefix_32 = new HashMap<Integer, String>() {{

            put(38, "\u2018"); // opening single quotation
            put(52, "\u2019"); // closing single quotation
            put(32, "<cw>"); // CAPITAL_WORD
            put(4, "</c>"); // CAPITAL_TERMINATOR

        }};

        prefix_32_32 = new HashMap<Integer, String>() {{

            put(32, "<c>"); // CAPITAL_PASSAGE

        }};

        prefix_40 = new HashMap<Integer, String>() {{

            put(35, "\u005B"); // opening square Bracket
            put(28, "\u005D"); // closing square bracket
            put(52, "\u0025"); // percentage
            put(36, "\u005F"); // underscore
            put(12, "\u2044"); // general fraction line

            put(6, "<is>"); // ITALIC_SYMBOL
            put(2, "<iw>"); // ITALIC_WORD
            put(54, "<i>"); // ITALIC_PASSAGE
            put(4, "</i>"); // ITALIC_TERMINATOR

            // greek letters
            // prefix comes before every greek letter
            // if letter is capital - capital indicator (32) comes before each greek prefix indicator
            put(1, "\u03B1"); // alpha
            put(3, "\u03B2"); // beta
            put(27, "\u03B3"); // gamma
            put(25, "\u03B4"); // delta
            put(17, "\u03B5"); // epsilon
            put(53, "\u03B6"); // zeta
            put(49, "\u03B7"); // eta
            put(57, "\u03B8"); // theta
            put(10, "\u03B9"); // iota
            put(5, "\u03BA"); // kappa
            put(7, "\u03BB");  // lambda
            put(13, "\u03BC"); // mu
            put(29, "\u03BD"); // nu
            put(45, "\u03BE"); // xi
            put(21, "\u03BF"); // omicron
            put(15, "\u03C0"); // pi
            put(23, "\u03C1"); // rho
            put(14, "\u03C3"); // sigma
            put(30, "\u03C4"); // tau
            put(37, "\u03C5"); // upsilon
            put(11, "\u03C6"); // phi
            put(47, "\u03C7"); // chi
            put(61, "\u03C8"); // psi
            put(58, "\u03C9"); // omega

        }};

        prefix_43 = new HashMap<Integer, String>() {{

            put(63, "\u25CF"); // circle
            put(60, "SQUARE_TRIANGLE_INDICATOR");

        }};

        prefix_43_60 = new HashMap<Integer, String>() {{

            put(25, "\u25A0"); // square
            put(9, "\u25B2"); // triangle

        }};

        prefix_51 = new HashMap<Integer, String>() {{

            put(21, "\u2192"); // right arrow
            put(42, "\u2190"); // left arrow
            put(41, "\u2193"); // down arrow
            put(44, "\u2191"); // up arrow
            put(14, "\u2197"); // up right arrow
            put(35, "\u2198"); // down right arrow
            put(28, "\u2199"); // down left arrow
            put(49, "\u2196"); // up left arrow

        }};

        // not here - feet
        prefix_54 = new HashMap<Integer, String>() {{

            put(54, "\u2033"); // inches

        }};

        prefix_56 = new HashMap<Integer, String>() {{

            put(35, "\u007B"); // opening curly bracket
            put(28, "\u007D"); // curly curly bracket
            put(50, "\u2022"); // bullet point
            put(57, "\u0023"); // #
            put(33, "\u2216"); // backward slash
            put(12, "\u2215"); // forward slash
            put(38, "\u00AB"); // opening guillemet
            put(52, "\u00BB"); // closing guillemet

            put(6, "<us>"); // UNDERLINE_SYMBOL
            put(2, "<uw>"); // UNDERLINE_WORD
            put(54, "<u>"); // UNDERLINE_PASSAGE
            put(4, "</u>"); // UNDERLINE_TERMINATOR

        }};

        prefix_60 = new HashMap<Integer, String>() {{

            put(35, "\u266D"); // flat
            put(33, "\u266E"); // natural
            put(41, "\u266F"); // sharp

        }};

        numberMode = new HashMap<Integer, String>() {{

            put(35, "G1");
            put(33, "NS");

        }};

        letters = new HashMap<Integer, String>() {{

            put(1, "\u0061"); // a
            put(3, "\u0062"); // b
            put(9, "\u0063"); // c
            put(25, "\u0064"); // d
            put(17, "\u0065"); // e
            put(11, "\u0066"); // f
            put(27, "\u0067"); // g
            put(19, "\u0068"); // h
            put(10, "\u0069"); // i
            put(26, "\u006A"); // j
            put(5, "\u006B"); // k
            put(7, "\u006C"); // l
            put(13, "\u006D"); // m
            put(29, "\u006E"); // n
            put(21, "\u006F"); // o
            put(15, "\u0070"); // p
            put(31, "\u0071"); // q
            put(23, "\u0072"); // r
            put(14, "\u0073"); // s
            put(30, "\u0074"); // t
            put(37, "\u0075"); // u
            put(39, "\u0076"); // v
            put(58, "\u0077"); // w
            put(45, "\u0078"); // x
            put(61, "\u0079"); // y
            put(53, "\u007A"); // z

        }};

        numbers = new HashMap<Integer, String>() {{

            put(26, "\u0030"); // 0
            put(1, "\u0031"); // 1
            put(3, "\u0032"); // 2
            put(9, "\u0033"); // 3
            put(25, "\u0034"); // 4
            put(17, "\u0035"); // 5
            put(11, "\u0036"); // 6
            put(27, "\u0037"); // 7
            put(19, "\u0038"); // 8
            put(10, "\u0039"); // 9

        }};

        punctuation = new HashMap<Integer, String>() {{

            put(4, "\u0027"); // ' (apostrophe)
            put(2, "\u002C"); // ,
            put(36, "\u002D"); // hyphen
            put(50, "\u002E"); // .
            put(22, "\u0021"); // !
            put(6, "\u003B"); // ;
            put(18, "\u003A"); // :
            put(38, "QUESTION_MARK_OPENING_NON_SPECIFIC_QUOTATION"); // QUESTION_MARK_OPENING_NON_SPECIFIC_QUOTATION
            put(52, "'"); // // CLOSING_NON_SPECIFIC_QUOTATION
            put(41, "\u221A"); // open square root
            put(44, "CLOSE_SQUARE_ROOT"); // CLOSE_SQUARE_ROOT
            put(34, "212"); // SUBSCRIPT
            put(20, "213"); // SUPERSCRIPT
            put(12, "\u2044"); // simple numeric fraction line
            put(55, "GFO"); // GENERAL_FRACTION_OPEN_INDICATOR
            put(62, "GFC"); // GENERAL_FRACTION_CLOSE_INDICATOR
            put(49, "ST"); // SHAPE_TERMINATOR

        }};

    }
}
