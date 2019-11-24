from CellParser.GradeOneLUTs import *
from CellParser.FormattingLUTs import *
from CellParser.ParserHelper import *


class BrailleCellParser(object):

    def __init__(self):
        self.mBrailleGrade = "Grade-1"

    def __grade1BrailleParser(self, braille_cells):
        parser_information: BrailleInformation = BrailleInformation()
        parsed_braille = []
        current_braille_line = 0

        for braille_line in braille_cells:

            parsed_braille.append([])
            parser_information.clearAll()

            for braille_cell in braille_line:

                redo_required = True

                while redo_required is True:

                    redo_required = False

                    if braille_cell == 0:
                        parser_information.clearAll()
                        parsed_braille[current_braille_line] += [" "]
                        continue

                    if braille_cell == 48:
                        parser_information.clearAll()
                        parsed_braille[current_braille_line] += ["grade1Indicator"]
                        continue

                    if parser_information.prefixState == BraillePrefixState.none:

                        prefix = prefixes.get(braille_cell, "NOT_FOUND")

                        if prefix == "NOT_FOUND":  # not a prefix and no prefix present
                            if parser_information.numberMode is True:
                                # check numbers
                                lut_translation = numbers.get(braille_cell, "NOT_FOUND")
                                if lut_translation == "NOT_FOUND":
                                    # check letters
                                    lut_translation = letters.get(braille_cell, "NOT_FOUND")  # letter(k-z)
                                    if lut_translation == "NOT_FOUND":
                                        # check basicPunctuation
                                        lut_translation = basicPunctuation.get(braille_cell, "NOT_FOUND")
                                        number_mode_punctuation = ["NOT_FOUND", u'\u002E',
                                                                   u'\u002C', u'\u2044']  # fullstop, comma, simple numeric fraction line
                                        if lut_translation not in number_mode_punctuation:
                                            parser_information.numberMode = False
                                        else:
                                            if lut_translation == "NOT_FOUND":
                                                lut_translation = numberMode.get(braille_cell, "NOT_FOUND")
                                                if lut_translation == "grade1Indicator":
                                                    parser_information.numberMode = False
                                                    continue
                                parsed_braille[current_braille_line] += [lut_translation]
                            else:
                                # letter or basicPunctuation
                                lut_translation = basicPunctuation.get(braille_cell, "NOT_FOUND")
                                if lut_translation == "NOT_FOUND":
                                    lut_translation = letters.get(braille_cell, "NOT_FOUND")
                                parsed_braille[current_braille_line] += [lut_translation]

                        else:  # cell is a prefix
                            parser_information.numberMode = False
                            parser_information.prefixState = BraillePrefixState.stageOne
                            parser_information.prefixStageOneValue = braille_cell

                    elif parser_information.prefixState == BraillePrefixState.stageOne:  # there has been 1 prefix

                        lut_translation = "ERROR"

                        if parser_information.prefixStageOneValue == 8:
                            lut_translation = prefix_8.get(braille_cell, "NOT_FOUND")
                            if lut_translation == "daggerIndicator":
                                parser_information.prefixState = BraillePrefixState.stageTwo
                                parser_information.prefixStageTwoValue = braille_cell
                                continue

                        elif parser_information.prefixStageOneValue == 16:
                            lut_translation = prefix_16.get(braille_cell, "NOT_FOUND")
                            if lut_translation == "emDashIndicator":
                                parser_information.prefixState = BraillePrefixState.stageTwo
                                parser_information.prefixStageTwoValue = braille_cell
                                continue
                            elif lut_translation == "NOT_FOUND":  # numericSpace
                                parser_information.clearPrefix()
                                parsed_braille[current_braille_line] += [" "]  # numericSpace
                                parser_information.numberMode = True
                                redo_required = True
                                continue

                        elif parser_information.prefixStageOneValue == 24:
                            lut_translation = prefix_24.get(braille_cell, "NOT_FOUND")

                        elif parser_information.prefixStageOneValue == 32:
                            lut_translation = prefix_32.get(braille_cell, "NOT_FOUND")
                            if lut_translation == "NOT_FOUND":  # previous '32' cell represented capital letter
                                parser_information.clearPrefix()
                                parsed_braille[current_braille_line] += ["capitalLetter"]
                                redo_required = True
                                continue
                            elif lut_translation == "capitalWord":
                                parser_information.prefixState = BraillePrefixState.stageTwo
                                parser_information.prefixStageTwoValue = braille_cell
                                continue

                        elif parser_information.prefixStageOneValue == 40:
                            lut_translation = prefix_40.get(braille_cell, "NOT_FOUND")

                        elif parser_information.prefixStageOneValue == 43:
                            lut_translation = prefix_43.get(braille_cell, "NOT_FOUND")
                            if lut_translation == "squareTriangleIndicator":
                                parser_information.prefixState = BraillePrefixState.stageTwo
                                parser_information.prefixStageTwoValue = braille_cell
                                continue

                        elif parser_information.prefixStageOneValue == 51:
                            lut_translation = prefix_51.get(braille_cell, "NOT_FOUND")

                        elif parser_information.prefixStageOneValue == 54:
                            lut_translation = prefix_54.get(braille_cell, "NOT_FOUND")
                            if lut_translation == "NOT_FOUND":
                                parser_information.clearPrefix()
                                parsed_braille[current_braille_line] += [u'\u2032']
                                redo_required = True
                                continue

                        elif parser_information.prefixStageOneValue == 56:
                            lut_translation = prefix_56.get(braille_cell, "NOT_FOUND")

                        elif parser_information.prefixStageOneValue == 60:
                            lut_translation = prefix_60.get(braille_cell, "NOT_FOUND")
                            if lut_translation == "NOT_FOUND":
                                parser_information.clearPrefix()
                                parser_information.numberMode = True
                                redo_required = True
                                continue

                        parser_information.clearPrefix()
                        parsed_braille[current_braille_line] += [lut_translation]

                    elif parser_information.prefixState == BraillePrefixState.stageTwo:  # there has been 2 prefixes

                        lut_translation = "ERROR"

                        if parser_information.prefixStageOneValue == 8 and parser_information.prefixStageTwoValue == 32:
                            lut_translation = prefix_8_32.get(braille_cell, "NOT_FOUND")

                        elif parser_information.prefixStageOneValue == 16 and parser_information.prefixStageTwoValue == 32:
                            lut_translation = prefix_16_32.get(braille_cell, "NOT_FOUND")

                        elif parser_information.prefixStageOneValue == 32 and parser_information.prefixStageTwoValue == 32:
                            lut_translation = prefix_32_32.get(braille_cell, "NOT_FOUND")
                            if lut_translation == "NOT_FOUND":  # previous '32' cell represented capital letter
                                parser_information.clearPrefix()
                                parsed_braille[current_braille_line] += ["capitalWord"]
                                redo_required = True
                                continue

                        elif parser_information.prefixStageOneValue == 43 and parser_information.prefixStageTwoValue == 60:
                            lut_translation = prefix_43_60.get(braille_cell, "NOT_FOUND")

                        parser_information.clearPrefix()
                        parsed_braille[current_braille_line] += [lut_translation]

            current_braille_line += 1

        return parsed_braille

    def __grade2BrailleParser(self, grade_1_braille_output):
        return grade_1_braille_output

    def __formatFont(self, grade_output):
        font_information: FontInformation = FontInformation()
        parsed_braille = []
        current_braille_line = 0

        for braille_line in grade_output:

            parsed_braille.append([])

            for braille_cell in braille_line:

                # Current assumption - 1 font on single section of text
                #                    - capital indicator will only be there if there isn't 1 active
                if braille_cell in capitalIndicators:

                    if braille_cell == "apostrophe_capitalTerminator":
                        if font_information.capitalDuration == "passage":  # passage capital to terminate
                            font_information.clearCapitalData()
                        else:  # no active capital passage
                            parsed_braille[current_braille_line] += [u'\u0027']  # apostrophe
                    else:
                        font_information.capitalMode = True
                        font_information.capitalDuration = capitalIndicators[braille_cell][1]

                elif braille_cell in fontIndicators:

                    if braille_cell in font_information.terminators:
                        parsed_braille[current_braille_line] += [
                            self.__getEndOfFontIndicator(font_information.activeTypeFont)]
                        font_information.clearActiveTypeFont()
                    else:
                        font_information.activeTypeFont = fontIndicators[braille_cell][0]
                        font_information.activeTypeFontDuration = fontIndicators[braille_cell][1]
                        parsed_braille[current_braille_line] += [
                            self.__getStartOfFontIndicator(font_information.activeTypeFont)]

                else:  # check current capital/active fonts

                    if braille_cell == " ":  # space

                        if font_information.capitalMode and font_information.capitalDuration == "word":
                            font_information.clearCapitalData()
                        if font_information.activeTypeFont != "none" and font_information.activeTypeFontDuration == "word":
                            parsed_braille[current_braille_line] += [
                                self.__getEndOfFontIndicator(font_information.activeTypeFont)]
                            font_information.clearActiveTypeFont()

                        parsed_braille[current_braille_line] += [braille_cell]

                    # Check for accent/ligatures/subsrcipts/superscripts and add these to parsed braille
                    elif braille_cell in accents or braille_cell in consecutiveIndicators:

                        parsed_braille[current_braille_line] += [braille_cell]

                    else:  # lut for greek and english letters

                        if font_information.capitalMode:

                            lut_translation = capitals.get(braille_cell, "NOT FOUND")

                            if lut_translation == "NOT FOUND":  # different symbol

                                if font_information.capitalDuration == "letter":
                                    font_information.clearCapitalData()
                                if font_information.activeTypeFontDuration == "letter":
                                    parsed_braille[current_braille_line] += [
                                        self.__getEndOfFontIndicator(font_information.activeTypeFont)]
                                    font_information.clearActiveTypeFont()

                                parsed_braille[current_braille_line] += [braille_cell]

                            else:  # found in LUT

                                parsed_braille[current_braille_line] += [lut_translation]

                                if font_information.capitalDuration == "letter":
                                    font_information.clearCapitalData()
                                if font_information.activeTypeFontDuration == "letter":
                                    parsed_braille[current_braille_line] += [
                                        self.__getEndOfFontIndicator(font_information.activeTypeFont)]
                                    font_information.clearActiveTypeFont()

                        else:  # not in capital mode

                            parsed_braille[current_braille_line] += [braille_cell]

                            if font_information.activeTypeFontDuration == "letter":
                                parsed_braille[current_braille_line] += [
                                    self.__getEndOfFontIndicator(font_information.activeTypeFont)]
                                font_information.clearActiveTypeFont()

            current_braille_line += 1

        return parsed_braille

    def __getStartOfFontIndicator(self, font_type):

        if font_type == "bold":
            return "<b>"
        elif font_type == "italic":
            return "<i>"
        elif font_type == "script":
            return "<s>"
        elif font_type == "underline":
            return "<u>"
        else:
            return 0

    def __getEndOfFontIndicator(self, font_type):

        if font_type == "bold":
            return "</b>"
        elif font_type == "italic":
            return "</i>"
        elif font_type == "script":
            return "</s>"
        elif font_type == "underline":
            return "</u>"
        else:
            return 0

    def __formatCharacters(self, grade_output):
        formatting_information: FormattingInformation = FormattingInformation()
        parsed_braille = []
        current_braille_line = 0

        for braille_line in grade_output:

            parsed_braille.append([])

            for braille_cell in braille_line:

                if braille_cell == "numericSpace":
                    formatting_information.clearActiveIndicator()
                    parsed_braille[current_braille_line] += [" "]
                    continue

                if formatting_information.activeIndicator == "none":

                    if braille_cell in consecutiveIndicators or braille_cell in accents:

                        formatting_information.activeIndicator = braille_cell

                    else:  # in 'otherIndicators' or regular character

                        parsed_braille[current_braille_line] += [braille_cell]

                else:  # activeIndicator is in 'consecutiveIndicators' dictionary

                    if formatting_information.activeIndicator in accents:

                        parsed_braille[current_braille_line] += [braille_cell + accents[formatting_information.activeIndicator]]
                        formatting_information.clearActiveIndicator()

                    elif formatting_information.activeIndicator in consecutiveIndicators:

                        if formatting_information.activeIndicator == "subscriptIndicator":
                            parsed_braille[current_braille_line] += [scripts[braille_cell][1]]
                            formatting_information.clearActiveIndicator()
                        elif formatting_information.activeIndicator == "superscriptIndicator":
                            parsed_braille[current_braille_line] += [scripts[braille_cell][0]]
                            formatting_information.clearActiveIndicator()
                        else:
                            ligature = parsed_braille[current_braille_line].pop() + braille_cell
                            parsed_braille[current_braille_line] += [ligatures[ligature]]
                            formatting_information.clearActiveIndicator()

            current_braille_line += 1

        return parsed_braille

    def setBrailleGrade(self, braille_grade):
        self.mBrailleGrade = braille_grade

    def parseBraille(self, braille_cells):
        if self.mBrailleGrade == "Grade-1":
            parsed_braille = self.__grade1BrailleParser(braille_cells)
            format_font_braille = self.__formatFont(parsed_braille)
            braille_output = self.__formatCharacters(format_font_braille)
            return braille_output
        elif self.mBrailleGrade == "Grade-2":
            parsed_grade_1_braille = self.__grade1BrailleParser(braille_cells)
            parsed_braille = self.__grade2BrailleParser(parsed_grade_1_braille)
            format_font_braille = self.__formatFont(parsed_braille)
            braille_output = self.__formatCharacters(format_font_braille)
            return braille_output
        else:
            return 0
