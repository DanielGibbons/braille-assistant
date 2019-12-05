package org.example.brailleassistant.processors;

import org.example.brailleassistant.utils.BraillePipeline;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CellParser extends IProcessor {

    private GradeOneLUTs gradeOneLUTs;
    private FormattingLUTs formattingLUTs;

    public CellParser(String processorId) {
        super(processorId);
        initialiseCellParser();
    }

    @Override
    public void Execute(BraillePipeline braillePipeline) {

        String[][] grade1Braille = grade1BrailleParser(braillePipeline.getBrailleCellValues());
        String[][] fontFormattedBraille = fontFormatter(grade1Braille);

        braillePipeline.setBrailleCellTranslation(characterFormatter(fontFormattedBraille));
    }

    private void initialiseCellParser() {
        gradeOneLUTs = new GradeOneLUTs();
        formattingLUTs = new FormattingLUTs();
    }

    // 1st stage of parser - apply rules of Grade 1 UEB
    private String[][] grade1BrailleParser(short[][] brailleCellValues) {

        BrailleInformation parserInformation = new BrailleInformation();

        String[][] parsedBraille = new String[brailleCellValues.length][brailleCellValues[0].length];

        Boolean redoRequired;

        // For each braille line
        for (int brailleLine = 0; brailleLine < brailleCellValues.length; brailleLine++) {

            // clear info
            parserInformation.clearAll();

            // for each braille cell
            for (int brailleCell = 0; brailleCell < brailleCellValues[0].length; brailleCell++) {

                redoRequired = true;

                int brailleCellValue = brailleCellValues[brailleLine][brailleCell];

                while (redoRequired) {

                    redoRequired = false;

                    // space
                    if (brailleCellValues[brailleLine][brailleCell] == 0) {
                        parserInformation.clearAll();
                        parsedBraille[brailleLine][brailleCell] = " ";
                        continue;
                    }

                    // Grade 1 Indicator
                    if (brailleCellValues[brailleLine][brailleCell] == 48) {
                        parserInformation.clearAll();
                        parsedBraille[brailleLine][brailleCell] = "G1";
                        continue;
                    }

                    // ? or '
                    if (brailleCellValues[brailleLine][brailleCell] == 38) {
                        parserInformation.clearAll();
                        if (brailleCell != brailleCellValues[0].length - 1) {
                            if (brailleCellValues[brailleLine][brailleCell + 1] == 0) {
                                parsedBraille[brailleLine][brailleCell] = "?";
                            } else {
                                parsedBraille[brailleLine][brailleCell] = "'";
                            }
                        }
                        continue;
                    }

                    // no prefix in parser
                    if (parserInformation.mPrefixState == CellParser.BraillePrefixState_t.None) {

                        int prefix = Arrays.binarySearch(gradeOneLUTs.prefixes, brailleCellValue);

                        if (prefix < 0) {

                            if (parserInformation.mNumberMode) {

                                String lutTranslation = gradeOneLUTs.numbers.get(brailleCellValue);

                                if (lutTranslation == null) {

                                    lutTranslation = gradeOneLUTs.letters.get(brailleCellValue);

                                    if (lutTranslation == null) {

                                        lutTranslation = gradeOneLUTs.punctuation.get(brailleCellValue);

                                        if (parserInformation.mNumberModePunctuation.contains(lutTranslation)) { // null, full stop, comma, simple numeric fraction line

                                            if(lutTranslation == null) {

                                                lutTranslation = gradeOneLUTs.numberMode.get(brailleCellValue);

                                                if (Objects.equals(lutTranslation, "G1")) {

                                                    parserInformation.mNumberMode = false;
                                                    continue;

                                                }

                                            }

                                        } else {

                                            parserInformation.mNumberMode = false;

                                        }

                                    }

                                }

                                parsedBraille[brailleLine][brailleCell] = lutTranslation;

                            } else {

                                String lutTranslation = gradeOneLUTs.punctuation.get(brailleCellValue);

                                if (lutTranslation == null) {
                                    lutTranslation = gradeOneLUTs.letters.get(brailleCellValue);
                                }

                                parsedBraille[brailleLine][brailleCell] = lutTranslation;

                            }

                        } else {

                            parserInformation.mNumberMode = false;
                            parserInformation.mPrefixState = CellParser.BraillePrefixState_t.Stage1;
                            parserInformation.mPrefixStage1Value = gradeOneLUTs.prefixes[prefix];

                        }

                    } else if (parserInformation.mPrefixState == CellParser.BraillePrefixState_t.Stage1) {

                        String lutTranslation = "ERROR";

                        if (parserInformation.mPrefixStage1Value == 8) {

                            lutTranslation = gradeOneLUTs.prefix_8.get(brailleCellValue);
                            if (Objects.equals(lutTranslation, "DAGGER_INDICATOR")) {
                                parserInformation.mPrefixState = CellParser.BraillePrefixState_t.Stage2;
                                parserInformation.mPrefixStage2Value = brailleCellValue;
                                continue;
                            }

                        } else if (parserInformation.mPrefixStage1Value == 16) {

                            lutTranslation = gradeOneLUTs.prefix_16.get(brailleCellValue);
                            if (Objects.equals(lutTranslation, "EM_DASH_INDICATOR")) {
                                parserInformation.mPrefixState = CellParser.BraillePrefixState_t.Stage2;
                                parserInformation.mPrefixStage2Value = brailleCellValue;
                                continue;
                            } else if (lutTranslation == null) { // numeric space
                                parserInformation.clearPrefix();
                                parsedBraille[brailleLine][brailleCell] = " ";
                                parserInformation.mNumberMode = true;
                                redoRequired = true;
                                continue;
                            }

                        } else if (parserInformation.mPrefixStage1Value == 24) {

                            lutTranslation = gradeOneLUTs.prefix_24.get(brailleCellValue);

                        } else if (parserInformation.mPrefixStage1Value == 32) {

                            lutTranslation = gradeOneLUTs.prefix_32.get(brailleCellValue);
                            if (lutTranslation == null) { // previous '32' cell represented a capital letter
                                parserInformation.clearPrefix();
                                parsedBraille[brailleLine][brailleCell-1] = "<cl>"; // CAPITAL_LETTER
                                redoRequired = true;
                                continue;
                            } else if (Objects.equals(lutTranslation, "<cw>")) { // CAPITAL_WORD
                                parserInformation.mPrefixState = CellParser.BraillePrefixState_t.Stage2;
                                parserInformation.mPrefixStage2Value = brailleCellValue;
                                continue;
                            }

                        } else if (parserInformation.mPrefixStage1Value == 40) {

                            lutTranslation = gradeOneLUTs.prefix_40.get(brailleCellValue);

                        } else if (parserInformation.mPrefixStage1Value == 43) {

                            lutTranslation = gradeOneLUTs.prefix_43.get(brailleCellValue);

                            if (Objects.equals(lutTranslation, "SQUARE_TRIANGLE_INDICATOR")) {
                                parserInformation.mPrefixState = CellParser.BraillePrefixState_t.Stage2;
                                parserInformation.mPrefixStage2Value = brailleCellValue;
                                continue;
                            }

                        } else if (parserInformation.mPrefixStage1Value == 51) {

                            lutTranslation = gradeOneLUTs.prefix_51.get(brailleCellValue);

                        } else if (parserInformation.mPrefixStage1Value == 54) {

                            lutTranslation = gradeOneLUTs.prefix_54.get(brailleCellValue);
                            if (lutTranslation == null) {
                                parserInformation.clearPrefix();
                                parsedBraille[brailleLine][brailleCell-1] = "\u2032"; // FEET (PRIME)
                                redoRequired = true;
                                continue;
                            }

                        } else if (parserInformation.mPrefixStage1Value == 56) {

                            lutTranslation = gradeOneLUTs.prefix_56.get(brailleCellValue);

                        } else if (parserInformation.mPrefixStage1Value == 60) {

                            lutTranslation = gradeOneLUTs.prefix_60.get(brailleCellValue);
                            if (lutTranslation == null) {
                                parserInformation.clearPrefix();
                                parserInformation.mNumberMode = true;
                                redoRequired = true;
                                continue;
                            }

                        }

                        parserInformation.clearPrefix();
                        parsedBraille[brailleLine][brailleCell] = lutTranslation;

                    } else if (parserInformation.mPrefixState == CellParser.BraillePrefixState_t.Stage2) {

                        String lutTranslation = "ERROR";

                        if (parserInformation.mPrefixStage1Value == 8 && parserInformation.mPrefixStage2Value == 32) {

                            lutTranslation = gradeOneLUTs.prefix_8_32.get(brailleCellValue);

                        } else if (parserInformation.mPrefixStage1Value == 16 && parserInformation.mPrefixStage2Value == 32) {

                            lutTranslation = gradeOneLUTs.prefix_16_32.get(brailleCellValue);

                        } else if (parserInformation.mPrefixStage1Value == 32 && parserInformation.mPrefixStage2Value == 32) {

                            lutTranslation = gradeOneLUTs.prefix_32_32.get(brailleCellValue);

                            if (lutTranslation == null) {
                                parserInformation.clearPrefix();
                                parsedBraille[brailleLine][brailleCell-1] = "<cw>"; // CAPITAL_WORD
                                redoRequired = true;
                                continue;
                            }

                        } else if (parserInformation.mPrefixStage1Value == 43 && parserInformation.mPrefixStage2Value == 60) {

                            lutTranslation = gradeOneLUTs.prefix_43_60.get(brailleCellValue);

                        }

                        parserInformation.clearPrefix();
                        parsedBraille[brailleLine][brailleCell] = lutTranslation;

                    }

                }

            }

        }

        return parsedBraille;

    }

    private String[][] grade2BrailleParser(String[][] grade1BrailleParserOutput) {

        return grade1BrailleParserOutput;

    }

    // apply capitalisation
    private String[][] fontFormatter(String[][] gradeParserOutput) {

        CapitalInformation capitalInformation = new CapitalInformation();

        String[][] parsedBraille = new String[gradeParserOutput.length][gradeParserOutput[0].length];


        for (int brailleLine = 0; brailleLine < gradeParserOutput.length; brailleLine++) {

            for (int brailleCell = 0; brailleCell < gradeParserOutput[0].length; brailleCell++) {

                String str = gradeParserOutput[brailleLine][brailleCell];

                if (Objects.equals(str, "<cl>") || Objects.equals(str, "<cw>") || Objects.equals(str, "<c>") || Objects.equals(str, "</c>")) { // capital indicator

                    if (Objects.equals(str, "</c>")) { // Apostrophe or capital terminator

                        capitalInformation.clearCapitalData();

                    } else {

                        capitalInformation.mCapitalMode = true;

                        switch (str) {
                            case "<cl>":
                                capitalInformation.mCapitalDuration = CellParser.Duration_t.Symbol;
                                break;
                            case "<cw>":
                                capitalInformation.mCapitalDuration = CellParser.Duration_t.Word;
                                break;
                            case "<c>":
                                capitalInformation.mCapitalDuration = CellParser.Duration_t.Passage;
                                break;
                        }
                    }

                } else {

                    if (Objects.equals(gradeParserOutput[brailleLine][brailleCell], " ")) {

                        if (capitalInformation.mCapitalMode && capitalInformation.mCapitalDuration == CellParser.Duration_t.Word) {
                            capitalInformation.clearCapitalData();
                        }

                        parsedBraille[brailleLine][brailleCell] = gradeParserOutput[brailleLine][brailleCell];

                    } else {

                        Integer integerValue;
                        Boolean accentLigatureScriptDetected = false;

                        try {

                            integerValue = Integer.valueOf(str);

                            if (integerValue >= 201 && integerValue <= 213) { // accent, ligature, subscript or superscript detected
                                accentLigatureScriptDetected = true;
                            }

                        } catch (NumberFormatException e) {

                            e.printStackTrace();

                        }

                        if (accentLigatureScriptDetected) { // check for accents ligatures subscripts or superscripts

                            parsedBraille[brailleLine][brailleCell] = gradeParserOutput[brailleLine][brailleCell];


                        } else { // look for greek and english letters

                            if (capitalInformation.mCapitalMode) {

                                String lutTranslation = formattingLUTs.capitals.get(gradeParserOutput[brailleLine][brailleCell]);

                                if (lutTranslation == null) {

                                    if (capitalInformation.mCapitalDuration == CellParser.Duration_t.Symbol) {
                                        capitalInformation.clearCapitalData();
                                    }

                                    parsedBraille[brailleLine][brailleCell] = gradeParserOutput[brailleLine][brailleCell];

                                } else {

                                    parsedBraille[brailleLine][brailleCell] = lutTranslation;

                                    if (capitalInformation.mCapitalDuration == CellParser.Duration_t.Symbol) {
                                        capitalInformation.clearCapitalData();
                                    }
                                }


                            } else {

                                parsedBraille[brailleLine][brailleCell] = gradeParserOutput[brailleLine][brailleCell];

                            }

                        }
                    }

                }


            }
        }

        return parsedBraille;

    }

    // apply subscripts, superscripts, accents and ligatures
    private String[][] characterFormatter(String[][] fontFormatterOutput) {

        CharacterInformation characterInformation = new CharacterInformation();

        String[][] parsedBraille = new String[fontFormatterOutput.length][fontFormatterOutput[0].length];

        for (int brailleLine = 0; brailleLine < fontFormatterOutput.length; brailleLine++) {

            characterInformation.clearActiveIndicator();

            for (int brailleCell = 0; brailleCell < fontFormatterOutput[0].length; brailleCell++) {

                if (Objects.equals(fontFormatterOutput[brailleLine][brailleCell], "NS")) {
                    characterInformation.clearActiveIndicator();
                    parsedBraille[brailleLine][brailleCell] = " ";
                    continue;
                }

                if (characterInformation.mActiveIndicator == 200) { // No active indicator

                    String str = fontFormatterOutput[brailleLine][brailleCell];
                    Integer integerValue;

                    try {
                        integerValue = Integer.valueOf(str);

                        if (integerValue >= 201 && integerValue <=213) {

                            characterInformation.mActiveIndicator = integerValue;

                            if (integerValue == 211) { // ligature
                                characterInformation.mLigaturePosition = brailleCell;
                            }

                        } else {

                            parsedBraille[brailleLine][brailleCell] = str;

                        }

                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                        parsedBraille[brailleLine][brailleCell] = str;

                    }


                } else {

                    if (characterInformation.mActiveIndicator <= 210) { // accent loaded

                        parsedBraille[brailleLine][brailleCell] = fontFormatterOutput[brailleLine][brailleCell] + formattingLUTs.accents[characterInformation.mActiveIndicator - 201];
                        characterInformation.clearActiveIndicator();

                    } else if (characterInformation.mActiveIndicator == 212) { // subscript

                        parsedBraille[brailleLine][brailleCell] = formattingLUTs.subscripts.get(fontFormatterOutput[brailleLine][brailleCell]);
                        characterInformation.clearActiveIndicator();

                    } else if (characterInformation.mActiveIndicator == 213) { // superscript

                        parsedBraille[brailleLine][brailleCell] = formattingLUTs.superscripts.get(fontFormatterOutput[brailleLine][brailleCell]);
                        characterInformation.clearActiveIndicator();

                    } else { // ligature

                        String firstElementOfLigature = parsedBraille[brailleLine][characterInformation.mLigaturePosition - 1];
                        parsedBraille[brailleLine][characterInformation.mLigaturePosition - 1] = "";
                        String concat = firstElementOfLigature.concat(fontFormatterOutput[brailleLine][brailleCell]);
                        parsedBraille[brailleLine][brailleCell] = formattingLUTs.ligatures.get(concat);
                        characterInformation.clearActiveIndicator();

                    }

                }

            }

        }

        return parsedBraille;

    }


    // Helper Classes
    enum BraillePrefixState_t {
        None,
        Stage1,
        Stage2
    }

    enum Duration_t {
        None,
        Symbol,
        Word,
        Passage
    }

    // STAGE 1 PARSER - Converting braille to English
    class BrailleInformation {

        public Boolean mNumberMode;
        public CellParser.BraillePrefixState_t mPrefixState;
        public int mPrefixStage1Value;
        public int mPrefixStage2Value;

        final private List mNumberModePunctuation = Arrays.asList(null, "\u002E", "\u002C", "\u2044");


        public BrailleInformation() {
            this.mNumberMode = false;
            this.mPrefixState = CellParser.BraillePrefixState_t.None;
            this.mPrefixStage1Value = 0;
            this.mPrefixStage2Value = 0;
        }

        public void clearPrefix() {
            this.mPrefixState = CellParser.BraillePrefixState_t.None;
            this.mPrefixStage1Value = 0;
            this.mPrefixStage2Value = 0;
        }

        public void clearAll() {
            this.mNumberMode = false;
            this.clearPrefix();
        }

    }


    // STAGE 2 PARSER - Capitals and font weighting (bold, italic, script, underline)
    class CapitalInformation {

        public Boolean mCapitalMode;
        public CellParser.Duration_t mCapitalDuration;


        public CapitalInformation() {
            this.mCapitalMode = false;
            this.mCapitalDuration = CellParser.Duration_t.None;
        }

        public void clearCapitalData() {
            this.mCapitalMode = false;
            this.mCapitalDuration = CellParser.Duration_t.None;
        }

    }

    class CharacterInformation {

        public int mActiveIndicator;
        public int mLigaturePosition;

        public CharacterInformation() {

            this.mActiveIndicator = 200;
            this.mLigaturePosition = 0; // only useful if active indicator is ligature
        }

        public void clearActiveIndicator() {

            this.mActiveIndicator = 200;
            this.mLigaturePosition = 0; // only useful if active indicator is ligature

        }

    }
}
