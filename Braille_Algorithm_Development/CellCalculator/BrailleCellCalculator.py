class BrailleCellCalculator(object):

    def __calculateInternalBrailleCellRows(self, braille_cell_lines):
        # Calculate braille row positions in each braille line
        internal_braille_cell_rows = []
        current_braille_line = 0
        for braille_line in braille_cell_lines:
            top = braille_line[0]
            bottom = braille_line[1]
            row_height = (bottom - top) / 3
            intermediate_line_1 = int(top + row_height)
            intermediate_line_2 = int(top + (2 * row_height))
            internal_braille_cell_rows.append([[top, intermediate_line_1],
                                 [intermediate_line_1 + 1, intermediate_line_2],
                                 [intermediate_line_2 + 1, bottom]])
        return internal_braille_cell_rows

    def __classifyDotsIntoBrailleRows(self, braille_cell_lines, internal_braille_cell_rows, braille_dot_locations):
        # Find rows of braille dots inside each braille line
        braille_line_dot_locations = []
        for braille_line in range(len(braille_cell_lines)):
            braille_line_dot_locations.append([])
            braille_line_dot_locations[braille_line].append([])
            braille_line_dot_locations[braille_line].append([])
            braille_line_dot_locations[braille_line].append([])

        # Iterate through all braille dots
        current_braille_line = 0
        for dot_location in braille_dot_locations:
            braille_dot_row = dot_location[1]  # get pixel row value
            # IF: Within bounds of braille line
            if braille_dot_row in range(braille_cell_lines[current_braille_line][0], braille_cell_lines[current_braille_line][1]):
                # IF: Braille dot rows are similar
                if braille_dot_row in range(internal_braille_cell_rows[current_braille_line][0][0],
                                            internal_braille_cell_rows[current_braille_line][0][1]):
                    braille_line_dot_locations[current_braille_line][0].append(dot_location[0])
                elif braille_dot_row in range(internal_braille_cell_rows[current_braille_line][1][0],
                                              internal_braille_cell_rows[current_braille_line][1][1]):
                    braille_line_dot_locations[current_braille_line][1].append(dot_location[0])
                elif braille_dot_row in range(internal_braille_cell_rows[current_braille_line][2][0],
                                              internal_braille_cell_rows[current_braille_line][2][1]):
                    braille_line_dot_locations[current_braille_line][2].append(dot_location[0])
            # IF: Assigned all braille dots to a row in a braille line
            elif current_braille_line == len(braille_cell_lines) - 1:
                break
            # IF: Braille dot row is within range of next braille line
            elif braille_dot_row in range(braille_cell_lines[current_braille_line + 1][0],
                                          braille_cell_lines[current_braille_line + 1][1]):
                current_braille_line += 1
                if braille_dot_row in range(internal_braille_cell_rows[current_braille_line][0][0],
                                            internal_braille_cell_rows[current_braille_line][0][1]):
                    braille_line_dot_locations[current_braille_line][0].append(dot_location[0])
                elif braille_dot_row in range(internal_braille_cell_rows[current_braille_line][1][0],
                                              internal_braille_cell_rows[current_braille_line][1][1]):
                    braille_line_dot_locations[current_braille_line][1].append(dot_location[0])
                elif braille_dot_row in range(internal_braille_cell_rows[current_braille_line][2][0],
                                              internal_braille_cell_rows[current_braille_line][2][1]):
                    braille_line_dot_locations[current_braille_line][2].append(dot_location[0])

        # Sort rows in ascending column order
        for braille_line in braille_line_dot_locations:
            for braille_row in braille_line:
                braille_row.sort()

        return braille_line_dot_locations

    def __extractBrailleCells(self, braille_cell_lines, internal_braille_cell_rows, braille_cell_columns,
                              braille_line_dot_locations):
        # Braille Cell Formation
        #   0 #   # 3
        #   1 #   # 4
        #   2 #   # 5

        braille_cells = []
        for braille_line in range(len(braille_line_dot_locations)):
            braille_cells.append([])
            for braille_cell in range(len(braille_cell_columns)):
                braille_cells[braille_line].append([])

        current_braille_line = 0
        for braille_line in braille_line_dot_locations:
            for braille_row in range(3):  # scan through each row in each braille line
                starting_dot = 0
                # Each braille cell column
                for braille_cell in range(len(braille_cell_columns)):
                    # get bounds
                    lower_bound = braille_cell_columns[braille_cell][0]
                    upper_bound = braille_cell_columns[braille_cell][1]
                    intermediate_bound = (lower_bound + upper_bound) / 2
                    # loop through current braille row in braille line
                    for braille_dot in range(starting_dot, len(braille_line[braille_row])):
                        if braille_line[braille_row][braille_dot] in range(lower_bound, upper_bound + 1):
                            if braille_line[braille_row][braille_dot] <= intermediate_bound:
                                braille_cells[current_braille_line][braille_cell].append(braille_row)
                            else:  # braille_line[braille_row[braille_dot] > intermediate_bound
                                braille_cells[current_braille_line][braille_cell].append(braille_row + 3)
                        elif braille_line[braille_row][braille_dot] > upper_bound:
                            break
                        starting_dot += 1
            current_braille_line += 1
        return braille_cells

    def __calculateBrailleCellValues(self, braille_cells):
        braille_cell_values = []
        for braille_line in range(len(braille_cells)):
            braille_cell_values.append([])
            for braille_cell in range(len(braille_cells[braille_line])):
                braille_cell_values[braille_line].append([])

        current_braille_line = 0
        current_braille_cell = 0
        for braille_line in braille_cells:
            current_braille_cell = 0
            for braille_cell in braille_line:
                braille_cell_value = 0
                for dot_location in braille_cell:
                    braille_cell_value = braille_cell_value | (2 ** dot_location)
                braille_cell_values[current_braille_line][current_braille_cell] = braille_cell_value
                current_braille_cell += 1
            current_braille_line += 1

        return braille_cell_values

    def calculateBrailleCells(self, braille_cell_lines, braille_cell_columns, braille_dot_locations):
        internal_braille_cell_rows = self.__calculateInternalBrailleCellRows(braille_cell_lines)
        braille_line_dot_locations = self.__classifyDotsIntoBrailleRows(braille_cell_lines,
                                                                        internal_braille_cell_rows,
                                                                        braille_dot_locations)
        braille_cells = self.__extractBrailleCells(braille_cell_lines, internal_braille_cell_rows, braille_cell_columns,
                                                   braille_line_dot_locations)
        braille_cell_values = self.__calculateBrailleCellValues(braille_cells)

        return braille_line_dot_locations, braille_cell_values