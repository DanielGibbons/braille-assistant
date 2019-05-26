prefixes = {
    8: "ampersand_angleBracket_currency_accent_tilde_mathPower_@_scriptIndicator_dagger_prefix",
    16: "asterisk_roundBracket_basicMathSign_ditto_dash_prefix",
    24: "genderSign_doubleQuotation_IP_accent_paragraphSection_degree_boldIndicator_ligaturedIndicator_prefix",
    32: "capital_singleQuotation_prefix",
    40: "squareBracket_percentage_greekLetter_underscore_italicIndicator_generalFractionline_prefix",
    43: "shapeIndicator_prefix",
    51: "arrow_Prefix",
    54: "feet_inches_prefix",
    56: "curlyBracket_bulletPoint_hashTag_slash_guillemet_underlineIndicator_prefix",
    60: "number_musicSymbol_prefix",
}

prefix_8 = {
    47: u'\u0026',  # "ampersand"
    35: u'\u3008',  # "openingAngleBracket"
    28: u'\u3009',  # "closingAngleBracket"
    9: u'\u00A2',  # "cent"
    14: u'\u0024',  # "dollar"
    17: u'\u20AC',  # "euro"
    7: u'\u00A3',  # "pound"
    61: u'\u00A5',  # "yen"

    # Accented letters
    # if letter is capital capital indicator comes before accent indicator which in turn comes before the letter
    44: "breve",
    36: "macron",

    20: u'\u007E',  # "tilde" - used for approximately
    34: u'\u005E',  # "mathPower"8, 1
    1: u'\u0040',  # "@"
    32: "daggerIndicator",

    6: "scriptSymbol",
    2: "scriptWord",
    54: "scriptPassage",
    4: "scriptTerminator",
}

prefix_8_32 = {
    57: u'\u2020',  # "dagger"
    59: u'\u2021',  # "doubleDagger"
}

prefix_16 = {
    20: u'\u002A',  # "asterisk"
    35: u'\u0028',  # "openingRoundBracket"
    28: u'\u0029',  # "closingRoundBracket"
    22: u'\u002B',  # "additionSign"
    38: u'\u00D7',  # "multiplicationSign"
    12: u'\u00F7',  # "divisionSign"
    54: u'\u003D',  # "equalsSign"
    2: u'\u3003',  # "ditto"
    32: "emDashIndicator",
    36: u'\u2212',  # "enDash_minusSign"
}

prefix_16_32 = {
    36: u'\u2014',  # "emDash"
}

prefix_24 = {
    45: u'\u2640',  # "femaleGenderSign",
    61: u'\u2642',  # "maleGenderSign",
    38: u'\u201C',  # "openingDoubleQuotation"
    52: u'\u201D',  # "closingDoubleQuotation"
    9: u'\u00A9',  # "copyright"
    23: u'\u00AE',  # "registered"
    30: u'\u2122',  # "trademark"

    # Accented letters
    # if letter is capital capital indicator comes before accent indicator which in turn comes before the letter
    12: "acute",
    44: "caron",
    47: "cedilla",
    41: "circumflex",
    18: "diaeresis",
    33: "grave",
    43: "ring",
    59: "tilde",

    15: u'\u2761',  # "paragraph"
    14: u'\u00A7',  # "section"
    26: u'\u00B0',  # "degree"

    6: "boldSymbol",
    2: "boldWord",
    54: "boldPassage",
    4: "boldTerminator",

    22: "ligatureIndicator",
}

# could have signified a capital letter if subsequent cell is not one of these
prefix_32 = {
    38: u'\u2018',  # "openingSingleQuotation",
    52: u'\u2019',  # "closingSingleQuotation",
    32: "capitalWord",
    4: "capitalTerminator"
}

prefix_32_32 = {
    32: "capitalPassage"
}

prefix_40 = {
    35: u'\u005B',  # "openingSquareBracket"
    28: u'\u005D',  # "closingSquareBracket"
    52: u'\u0025',  # "percentage"
    36: u'\u005F',  # "underscore"
    12: u'\u2044',  # "generalFractionLine"

    6: "italicSymbol",
    2: "italicWord",
    54: "italicPassage",
    4: "italicTerminator",

    # greek
    # comes before every greek letter
    # if letter is capital - capital indicator (32) comes before each indicator
    1: u'\u03B1',  # alpha
    3: u'\u03B2',  # "beta"
    27: u'\u03B3',  # "gamma"
    25: u'\u03B4',  # "delta"
    17: u'\u03B5',  # "epsilon"
    53: u'\u03B6',  # "zeta"
    49: u'\u03B7',  # "eta"
    57: u'\u03B8',  # "theta"
    10: u'\u03B9',  # "iota"
    5: u'\u03BA',  # "kappa"
    7: u'\u03BB',  # "lambda"
    13: u'\u03BC',  # "mu"
    29: u'\u03BD',  # "nu"
    45: u'\u03BE',  # "xi"
    21: u'\u03BF',  # "omicron"
    15: u'\u03C0',  # "pi"
    23: u'\u03C1',  # "rho"
    14: u'\u03C3',  # "sigma"
    30: u'\u03C4',  # "tau"
    37: u'\u03C5',  # "upsilon"
    11: u'\u03C6',  # "phi"
    47: u'\u03C7',  # "chi"
    61: u'\u03C8',  # "psi"
    58: u'\u03C9',  # "omega"
}

prefix_43 = {
    63: u'\u25CF',  # "circle"
    60: "squareTriangleIndicator"
}

prefix_43_60 = {
    25: u'\u25A0',  # "square"
    9: u'\u25B2',  # "triangle"
}

# Arrows
prefix_51 = {
    21: u'\u2192',  # "rightArrow"
    42: u'\u2190',  # "leftArrow"
    41: u'\u2193',  # "downArrow"
    44: u'\u2191',  # "upArrow"
    14: u'\u2197',  # "upRightArrow"
    35: u'\u2198',  # "downRightArrow"
    28: u'\u2199',  # "downLeftArrow"
    49: u'\u2196',  # "upLeftArrow"
}

# not here - feet
prefix_54 = {
    54: u'\u2033'  # " (inches)
}

prefix_56 = {
    35: u'\u007B',  # "openingCurlyBracket"
    28: u'\u007D',  # "closingCurlyBracket"
    50: u'\u2022',  # "bulletPoint"
    57: u'\u0023',  # "hashTag"
    33: u'\u2216',  # "backwardSlash"
    12: u'\u2215',  # "forwardSlash"
    38: u'\u00AB',  # "openingGuillemet"
    52: u'\u00BB',  # "closingGuillement"

    6: "underlineSymbol",
    2: "underlineWord",
    54: "underlinePassage",
    4: "underlineTerminator",
}

# UEB Music
prefix_60 = {
    35: u'\u266D',  # "flat"
    33: u'\u266E',  # "natural"
    41: u'\u266F',  # "sharp"
}

numberMode = {
    48: "grade1Indicator",  # used if number is followed by a lowercase letter from "a-j" (even if there is a "." or "," after number
    16: "numericSpace"
}

letters = {
    1: u'\u0061',  # a
    3: u'\u0062',  # b
    9: u'\u0063',  # c
    25: u'\u0064',  # d
    17: u'\u0065',  # e
    11: u'\u0066',  # f
    27: u'\u0067',  # g
    19: u'\u0068',  # h
    10: u'\u0069',  # i
    26: u'\u006A',  # j
    5: u'\u006B',  # k
    7: u'\u006C',  # l
    13: u'\u006D',  # m
    29: u'\u006E',  # n
    21: u'\u006F',  # o
    15: u'\u0070',  # p
    31: u'\u0071',  # q
    23: u'\u0072',  # r
    14: u'\u0073',  # s
    30: u'\u0074',  # t
    37: u'\u0075',  # u
    39: u'\u0076',  # v
    58: u'\u0077',  # w
    45: u'\u0078',  # x
    61: u'\u0079',  # y
    53: u'\u007A'  # z
}

numbers = {
    26: u'\u0030',
    1: u'\u0031',
    3: u'\u0032',
    9: u'\u0033',
    25: u'\u0034',
    17: u'\u0035',
    11: u'\u0036',
    27: u'\u0037',
    19: u'\u0038',
    10: u'\u0039'
}

basicPunctuation = {
    4: "apostrophe_capitalTerminator",  # capitalTerminator only required when following a capital prefix (word/passage)
    2: u'\u002C',  # comma
    36: u'\u002D',  # hyphen
    50: u'\u002E',  # full stop - 3 in a row is an elipsis
    22: u'\u0021',  # exclamation mark
    6: u'\u003B',  # semi-colon
    18: u'\u003A',  # colon
    38: "questionMark_openingNonSpecificQuotation",
    52: "'",  #
    41: u'\u221A',  # "openSquareRoot",
    44: "closeSquareRoot",
    34: "subscriptIndicator",
    20: "superscriptIndicator",
    12: u'\u2044',  # "simpleNumericFractionLine"
    55: "generalFractionOpenIndicator",
    62: "generalFractionCloseIndicator",
    49: "shapeTerminator"  # not required if shape is followed by space (only followed by letter, number or punctuation)
}
