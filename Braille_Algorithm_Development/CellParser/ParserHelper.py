from enum import Enum


# STAGE 1 PARSER - Converting braille to English
class BraillePrefixState(Enum):

    none = 1
    stageOne = 2
    stageTwo = 3


class BrailleInformation(object):

    def __init__(self):
        self.numberMode = False
        self.prefixState = BraillePrefixState.none
        self.prefixStageOneValue = 0
        self.prefixStageTwoValue = 0

    def clearPrefix(self):
        self.prefixState = BraillePrefixState.none
        self.prefixStageOneValue = 0
        self.prefixStageTwoValue = 0

    def clearAll(self):
        self.clearPrefix()
        self.numberMode = False


# STAGE 2 PARSER - Capitals and font weighting (bold, italic, script, underline)
class FontInformation(object):

    def __init__(self):
        self.capitalMode = False
        self.capitalDuration = "none"
        self.activeTypeFont = "none"
        self.activeTypeFontDuration = "none"
        self.terminators = ["boldTerminator", "italicTerminator", "scriptTerminator", "underlineTerminator"]
        self.fonts = ["bold", "italic", "script", "underlined"]

    def clearCapitalData(self):
        self.capitalMode = False
        self.capitalDuration = "none"

    def clearActiveTypeFont(self):
        self.activeTypeFont = "none"
        self.activeTypeFontDuration = "none"


# STAGE 3 Parser - Ligatures, accents, subscripts, superscripts, numeric spaces
class FormattingInformation(object):

    def __init__(self):
        self.activeIndicator = "none"

    def clearActiveIndicator(self):
        self.activeIndicator = "none"

    def clearAll(self):
        self.clearActiveIndicator()







