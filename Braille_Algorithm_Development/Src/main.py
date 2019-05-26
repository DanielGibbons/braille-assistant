import numpy as np
import cv2
from ImageFilter.BrailleFilter import BrailleFilter
from CellFinder.BrailleCellFinder import BrailleCellFinder
from CellCalculator.BrailleCellCalculator import BrailleCellCalculator
from CellParser.BrailleCellParser import BrailleCellParser


def showData():
    # Draw cells based on known braille lines and braille cell columns
    for row_line in braille_cell_lines:
        y1 = row_line[0]
        y2 = row_line[1]
        for column_line in braille_cell_columns:
            x1 = column_line[0]
            x2 = column_line[1]
            cv2.rectangle(extracted_braille_image, (x1, y1), (x2, y2), 255, 1)

    line_number = 0
    for braille_line in braille_cell_values:
        print("BRAILLE LINE - %d" % line_number, braille_line)
        line_number += 1
    print("\n")

    line_number = 0
    for braille_line in parsed_braille:
        print("BRAILLE LINE - %d" % line_number, braille_line)
        line_number += 1
    print("\n")

    # Show braille image and filtered braille image
    cv2.imshow('Image', braille_image)
    cv2.imshow('Extracted Image', extracted_braille_image)


if __name__ == '__main__':

    # Print all array values setting
    np.set_printoptions(threshold=np.inf)

    # Initialise custom braille reader objects
    filter = BrailleFilter("Single-Sided", "black")
    cellFinder = BrailleCellFinder()
    cellCalculator = BrailleCellCalculator()
    cellParser = BrailleCellParser()
    cellParser.setBrailleGrade("Grade-1")

    # Read input image and convert to grayscale
    braille_image = cv2.imread('../Resources/braille-paper-2.jpg')

    # Filter input image and acquire locations of all braille dots
    extracted_braille_image, braille_dot_locations = filter.extractBrailleDots(braille_image)

    # Calculate the row numbers and column numbers which bound each braille line and braille cell respectively
    validation, braille_cell_lines, braille_cell_columns = cellFinder.calculateBrailleCells(extracted_braille_image)

    # Calculate the value of each braille cell in each braille line
    # N.B - Currently only found the row locations (top, middle, bottom) of each braille dot in each braille line
    braille_line_dot_locations, braille_cell_values = cellCalculator.calculateBrailleCells(braille_cell_lines,
                                                                                           braille_cell_columns,
                                                                                           braille_dot_locations)
    parsed_braille = cellParser.parseBraille(braille_cell_values)
    parsed_braille = np.asarray(parsed_braille)

    showData()
    cv2.waitKey(0)
    cv2.destroyAllWindows()
