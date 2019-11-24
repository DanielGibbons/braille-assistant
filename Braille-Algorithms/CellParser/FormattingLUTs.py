# STAGE 2 PARSER LUTs

capitalIndicators = {

    "capitalLetter": ("capital", "letter"),
    "capitalWord": ("capital", "word"),
    "capitalPassage": ("capital", "passage"),
    "apostrophe_capitalTerminator": ("capital", "terminator")

}

fontIndicators = {

    "boldSymbol": ("bold", "letter"),
    "boldWord": ("bold", "word"),
    "boldPassage": ("bold", "passage"),
    "boldTerminator": ("bold", "terminator"),

    "italicSymbol": ("italic", "letter"),
    "italicWord": ("italic", "word"),
    "italicPassage": ("italic", "passage"),
    "italicTerminator": ("italic", "terminator"),

    "scriptSymbol": ("script", "letter"),
    "scriptWord": ("script", "word"),
    "scriptPassage": ("script", "passage"),
    "scriptTerminator": ("script", "terminator"),

    "underlinedSymbol": ("underline", "letter"),
    "underlinedWord": ("underline", "word"),
    "underlinedPassage": ("underline", "passage"),
    "underlinedTerminator": ("underline", "terminator")

}

capitals = {

    u'\u0061': u'\u0041',  # a
    u'\u0062': u'\u0042',  # b
    u'\u0063': u'\u0043',  # c
    u'\u0064': u'\u0044',  # d
    u'\u0065': u'\u0045',  # e
    u'\u0066': u'\u0046',  # f
    u'\u0067': u'\u0047',  # g
    u'\u0068': u'\u0048',  # h
    u'\u0069': u'\u0049',  # i
    u'\u006A': u'\u004A',  # j
    u'\u006B': u'\u004B',  # k
    u'\u006C': u'\u004C',  # l
    u'\u006D': u'\u004D',  # m
    u'\u006E': u'\u004E',  # n
    u'\u006F': u'\u004F',  # o
    u'\u0070': u'\u0050',  # p
    u'\u0071': u'\u0051',  # q
    u'\u0072': u'\u0052',  # r
    u'\u0073': u'\u0053',  # s
    u'\u0074': u'\u0054',  # t
    u'\u0075': u'\u0055',  # u
    u'\u0076': u'\u0056',  # v
    u'\u0077': u'\u0057',  # w
    u'\u0078': u'\u0058',  # x
    u'\u0079': u'\u0059',  # y
    u'\u007A': u'\u005A',  # z

    # greek
    # comes before every greek letter
    # if letter is capital - capital indicator (32) comes before each indicator
    u'\u03B1': u'\u0391',  # alpha
    u'\u03B2': u'\u0392',  # "beta"
    u'\u03B3': u'\u0393',  # "gamma"
    u'\u03B4': u'\u0394',  # "delta"
    u'\u03B5': u'\u0395',  # "epsilon"
    u'\u03B6': u'\u0396',  # "zeta"
    u'\u03B7': u'\u0397',  # "eta"
    u'\u03B8': u'\u0398',  # "theta"
    u'\u03B9': u'\u0399',  # "iota"
    u'\u03BA': u'\u039A',  # "kappa"
    u'\u03BB': u'\u039B',  # "lambda"
    u'\u03BC': u'\u039C',  # "mu"
    u'\u03BD': u'\u039D',  # "nu"
    u'\u03BE': u'\u039E',  # "xi"
    u'\u03BF': u'\u039F',  # "omicron"
    u'\u03C0': u'\u03A0',  # "pi"
    u'\u03C1': u'\u03A1',  # "rho"
    u'\u03C3': u'\u03A2',  # "sigma"
    u'\u03C4': u'\u03A3',  # "tau"
    u'\u03C5': u'\u03A4',  # "upsilon"
    u'\u03C6': u'\u03A5',  # "phi"
    u'\u03C7': u'\u03A6',  # "chi"
    u'\u03C8': u'\u03A7',  # "psi"
    u'\u03C9': u'\u03A8'  # "omega"
}


# STAGE 3 PARSER LUTs

consecutiveIndicators = {
    "subscriptIndicator",
    "superscriptIndicator",
    "ligatureIndicator"
}

accents = {
    # Accented letters
    # if letter is capital capital indicator comes before accent indicator which in turn comes before the letter
    "acute": u'\u0301',
    "caron": u'\u030C',
    "cedilla": u'\u0327',
    "circumflex": u'\u0302',
    "diaeresis": u'\u0308',
    "grave": u'\u0300',
    "ring": u'\u030A',
    "tilde": u'\u0303',
    "breve": u'\u0306',
    "macron": u'\u0304'

}

scripts = {
     #      spr        sub
    '0': ('\u2070', '\u2080'),  # 0
    '1': ('\u00B9', '\u2081'),  # 1
    '2': ('\u00B2', '\u2082'),  # 2
    '3': ('\u00B3', '\u2083'),  # 3
    '4': ('\u2074', '\u2084'),  # 4
    '5': ('\u2075', '\u2085'),  # 5
    '6': ('\u2076', '\u2086'),  # 6
    '7': ('\u2077', '\u2087'),  # 7
    '8': ('\u2078', '\u2088'),  # 8
    '9': ('\u2079', '\u2089'),  # 9

    '+': ('\u207A', '\u208A'),  # +
    '-': ('\u207B', '\u208B'),  # -
    '=': ('\u207C', '\u208C'),  # =
    '(': ('\u207D', '\u208D'),  # (
    ')': ('\u207E', '\u208E'),  # )

    'a': ('\u1d43', '\u2090'),  # a
    'b': ('\u1d47', '?'),  # b
    'c': ('\u1d9c', '?'),  # c
    'd': ('\u1d48', '?'),  # d
    'e': ('\u1d49', '\u2091'),  # e
    'f': ('\u1da0', '?'),  # f
    'g': ('\u1d4d', '?'),  # g
    'h': ('\u02b0', '\u2095'),  # h
    'i': ('\u2071', '\u1d62'),  # i
    'j': ('\u02b2', '\u2c7c'),  # j
    'k': ('\u1d4f', '\u2096'),  # k
    'l': ('\u02e1', '\u2097'),  # l
    'm': ('\u1d50', '\u2098'),  # m
    'n': ('\u207f', '\u2099'),  # n
    'o': ('\u1d52', '\u2092'),  # o
    'p': ('\u1d56', '\u209a'),  # p
    'q': ('?', '?'),  # q
    'r': ('\u02b3', '\u1d63'),  # r
    's': ('\u02e2', '\u209b'),  # s
    't': ('\u1d57', '\u209c'),  # t
    'u': ('\u1d58', '\u1d64'),  # u
    'v': ('\u1d5b', '\u1d65'),  # v
    'w': ('\u02b7', '?'),  # w
    'x': ('\u02e3', '\u2093'),  # x
    'y': ('\u02b8', '?'),  # y
    'z': ('?', '?'),  # z

    'A': ('\u1d2c', '?'),  # A
    'B': ('\u1d2e', '?'),  # B
    'C': ('?', '?'),  # C
    'D': ('\u1d30', '?'),  # D
    'E': ('\u1d31', '?'),  # E
    'F': ('?', '?'),  # F
    'G': ('\u1d33', '?'),  # G
    'H': ('\u1d34', '?'),  # H
    'I': ('\u1d35', '?'),  # I
    'J': ('\u1d36', '?'),  # J
    'K': ('\u1d37', '?'),  # K
    'L': ('\u1d38', '?'),  # L
    'M': ('\u1d39', '?'),  # M
    'N': ('\u1d3a', '?'),  # N
    'O': ('\u1d3c', '?'),  # O
    'P': ('\u1d3e', '?'),  # P
    'Q': ('?', '?'),  # Q
    'R': ('\u1d3f', '?'),  # R
    'S': ('?', '?'),  # S
    'T': ('\u1d40', '?'),  # T
    'U': ('\u1d41', '?'),  # U
    'V': ('\u2c7d', '?'),  # V
    'W': ('\u1d42', '?'),  # W
    'X': ('?', '?'),  # X
    'Y': ('?', '?'),  # Y
    'Z': ('?', '?'),  # Z

    u'\u03B1': ('\u1d45', '?'),  # alpha
    u'\u03B2': ('\u1d5d', '\u1d66'),  # beta
    u'\u03B3': ('\u1d5e', '\u1d67'),  # gamma
    u'\u03B4': ('\u1d5f', '?'),  # delta
    u'\u03B5': ('\u1d4b', '?'),  # epsilon
    u'\u03B8': ('\u1dbf', '?'),  # theta
    u'\u03B9': ('\u1da5', '?'),  # iota
    u'\u03C1': ('?', '\u1d68'),  # rho
    u'\u03C6': ('\u1db2', '?'),  # phi
    u'\u03C7': ('\u1d61', '\u1d6a'),  # chi
    u'\u03C8': ('\u1d60', '\u1d69')  # psi

}

ligatures = {
    "AA": '\uA732',
    "aa": '\uA733',
    "AE": '\u00C6',
    "ae": '\u00E6',
    "AO": '\uA734',
    "ao": '\uA735',
    "AU": '\uA736',
    "au": '\uA737',
    "AV": '\uA738',
    "av": '\uA739',
    "AY": '\uA73C',
    "ay": '\uA73D',

    "ff": '\uFB00',
    "fi": '\uFB01',
    "fl": '\uFB02',
    "OE": '\u0152',
    "oe": '\u0153',
    "OO": '\uA74E',
    "oo": '\uA74F',
    "st": '\uFB06',
    "TZ": '\uA728',
    "tz": '\uA729',
    "ue": '\u1D68',
    "VY": '\uA760',
    "vy": '\uA761'

}

other = {
    "apostrophe_capitalTerminator",
    "questionMark_openingNonSpecificQuotation"
}
