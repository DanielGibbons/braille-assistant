import numpy as np


class BrailleCellFinder(object):

    def __findEmptyRows(self, input_image):
        empty_rows = []
        for i in range(input_image.shape[0]):  # shape[0] = height of image (number of rows)
            if np.sum(input_image[i, :]) == 0:
                empty_rows.append(i)
        return empty_rows

    def __findEmptyColumns(self, input_image):
        empty_columns = []
        column_summation = np.sum(input_image, axis=0)
        for i in range(input_image.shape[1]): # shape[1] = width of image (number of columns)
            if column_summation[i] == 0:
                empty_columns.append(i)
        return empty_columns

    def __findBrailleLines(self, empty_rows):
        # Group consecutive empty rows and label
        empty_row_labels_list = []
        # get first empty row  and set consecutive empty rows to 0
        empty_row_label = [empty_rows[0], 0]
        for i in range(1, len(empty_rows)):  # iterate through empty rows from element 1 to last element
            if empty_rows[i] - empty_rows[i - 1] == 1:  # empty rows are consecutive
                empty_row_label[1] += 1
            else:  # empty rows aren't consecutive
                empty_row_labels_list.append(empty_row_label)  # add empty row label
                empty_row_label = [empty_rows[i], 0]  # initialise next empty row label
        # add last label if there is a sequence of empty rows at bottom of image
        if empty_row_label[1] > 0:
            empty_row_labels_list.append(empty_row_label)

        # Calculate mean number of consecutive empty rows
        empty_row_labels = np.asarray(empty_row_labels_list)
        mean_row_gap = np.mean(empty_row_labels[:, 1])

        # Identify the row labels corresponding to the gaps between braille lines
        row_labels_of_interest = []
        braille_lines = []
        for label in empty_row_labels:
            if label[1] > mean_row_gap:
                row_labels_of_interest.append(label)
        for i in range(0, len(row_labels_of_interest) - 1):
            braille_lines.append(
                [row_labels_of_interest[i][0] + row_labels_of_interest[i][1], row_labels_of_interest[i + 1][0]])
        return braille_lines

    def __findBrailleCellColumns(self, empty_columns):
        # Group consecutive empty columns and label
        empty_column_labels_list = []
        # get first empty column  and set consecutive empty columns to 0
        empty_column_label = [empty_columns[0], 0]
        for i in range(1, len(empty_columns)):  # iterate through empty columns from element 1 to last element
            if empty_columns[i] - empty_columns[i - 1] == 1:  # empty columns are consecutive
                empty_column_label[1] += 1
            else:  # empty columns aren't consecutive
                empty_column_labels_list.append(empty_column_label)  # add empty column label
                empty_column_label = [empty_columns[i], 0]  # initialise next empty column label
        # add last label if there is a sequence of empty columns at bottom of image
        if empty_column_label[1] > 0:
            empty_column_labels_list.append(empty_column_label)

        # Calculate mean number of consecutive empty columns
        empty_column_labels = np.asarray(empty_column_labels_list)
        mean_column_gap = np.mean(empty_column_labels[:, 1])

        # Identify the column labels corresponding to the gaps between braille cells
        column_labels_of_interest = []
        braille_cell_columns = []
        for label in empty_column_labels:
            if label[1] > mean_column_gap / 1.3:
                column_labels_of_interest.append(label)
        for i in range(0, len(column_labels_of_interest) - 1):
            braille_cell_columns.append([column_labels_of_interest[i][0] + column_labels_of_interest[i][1],
                                         column_labels_of_interest[i + 1][0]])
        return braille_cell_columns

    def __validateIdentifiedCells(self, braille_lines, braille_cell_columns):
        line_widths = []
        cell_widths = []

        if len(braille_lines) > 0:
            for line in braille_lines:
                line_widths.append(line[1]-line[0])
            mean_line_width = sum(line_widths) / len(line_widths)
            for width in line_widths:
                if (width < 0.85*mean_line_width) or (width > 1.15*mean_line_width):
                    return False

        if len(braille_cell_columns) > 0:
            for cell in braille_cell_columns:
                cell_widths.append(cell[1]-cell[0])
            mean_cell_width = sum(cell_widths) / len(cell_widths)
            for width in cell_widths:
                if (width < 0.8*mean_cell_width) or (width > 1.2*mean_cell_width):
                    return False

        return True



    def calculateBrailleCells(self, input_image):
        empty_rows = self.__findEmptyRows(input_image)
        empty_columns = self.__findEmptyColumns(input_image)
        braille_lines = self.__findBrailleLines(empty_rows)
        braille_cell_columns = self.__findBrailleCellColumns(empty_columns)
        validation = self.__validateIdentifiedCells(braille_lines, braille_cell_columns)
        print(braille_lines)
        print("\n \n \n")
        print(braille_cell_columns)
        return validation, braille_lines, braille_cell_columns


