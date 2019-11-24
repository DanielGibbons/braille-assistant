import cv2
from ImageFilter.BrailleFilter import BrailleFilter
from CellFinder.BrailleCellFinder import BrailleCellFinder


def showData():
    # Draw cells based on known braille lines and braille cell columns
    if validation:
        for row_line in braille_cell_lines:
            y1 = row_line[0]
            y2 = row_line[1]
            for column_line in braille_cell_columns:
                x1 = column_line[0]
                x2 = column_line[1]
                cv2.rectangle(extracted_braille_image, (x1, y1), (x2, y2), 255, 1)
                cv2.rectangle(braille_image, (x1 - 3, y1 - 3), (x2 + 3, y2 + 3), (0, 0, 255), 1)

    # Show braille image and filtered braille image
    cv2.imshow('Image', braille_image)
    cv2.imshow('Extracted Image', extracted_braille_image)


if __name__ == '__main__':

    camera = cv2.VideoCapture(1)
    filter = BrailleFilter("Single-Sided", "black")
    cellFinder = BrailleCellFinder()

    while True:

        #  open image and convert to grayscale
        ret, braille_image = camera.read()
        counter = 0

        if ret:

            # Filter input image and acquire locations of all braille dots
            extracted_braille_image, braille_dot_locations = filter.extractBrailleDots(braille_image)

            # Calculate the row numbers and column numbers which bound each braille line and braille cell respectively
            validation, braille_cell_lines, braille_cell_columns = cellFinder.calculateBrailleCells(extracted_braille_image)

            # Draw images to screen
            showData()

            # Wait 20ms
            cv2.waitKey(70)

        else:

            print("Error")
            camera.release()
            cv2.waitKey(0)
            cv2.destroyAllWindows()
