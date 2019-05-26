import cv2
import numpy as np
import math
from ImageFilter.BrailleFilter import BrailleFilter


def rotate_image(mat, angle):

    height, width = mat.shape[:2]
    image_center = (width / 2, height / 2)

    rotation_mat = cv2.getRotationMatrix2D(image_center, angle, 1)

    radians = math.radians(angle)
    sin = math.sin(radians)
    cos = math.cos(radians)
    bound_w = int((height * abs(sin)) + (width * abs(cos)))
    bound_h = int((height * abs(cos)) + (width * abs(sin)))

    rotation_mat[0, 2] += ((bound_w / 2) - image_center[0])
    rotation_mat[1, 2] += ((bound_h / 2) - image_center[1])

    rotated_mat = cv2.warpAffine(mat, rotation_mat, (bound_w, bound_h))

    return rotated_mat


def find_rotation(x1, y1, x2, y2):

    if y1 == y2 or x1 == x2 or abs(x2 - x1) > abs(y2 - y1):
        return 0

    # sort vertices so y2 > y1
    if y1 > y2:
        temp_x = x1
        temp_y = y1
        x1 = x2
        y1 = y2
        x2 = temp_x
        y2 = temp_y

    angle_radians = math.radians(90) - math.atan(abs(y2-y1) / abs(x2-x1))

    angle_degrees = math.degrees(angle_radians)

    if x2 < x1:
        angle_degrees = -angle_degrees

    return angle_degrees


def find_distance_squared(point1, point2):
    return ((point2[0] - point1[0])**2) + ((point2[1] - point1[1])**2)


def find_nearest_neighbour(point, points):
    nearest_neighbour = []
    distance = 9999999999
    for p in points:
        if p[0] != point[0] and p[1] != point[1]:
            latest_distance = find_distance_squared(point, p)
            if latest_distance <= distance:
                distance = latest_distance
                nearest_neighbour = p

    return nearest_neighbour, distance


def calculate_required_image_rotation(braille_dots):

    rotation_data = []

    for dot in braille_dots:
        nearest_neighbour, distance = find_nearest_neighbour(dot, braille_dot_locations)
        rotation = find_rotation(dot[0], dot[1], nearest_neighbour[0], nearest_neighbour[1])
        if abs(rotation) < 22.5 and rotation != 0:
            cv2.line(extracted_braille_image, (dot[0], dot[1]), (nearest_neighbour[0], nearest_neighbour[1]), 128, 1)
            cv2.circle(extracted_braille_image, (dot[0], dot[1]), 6, 128, 1)
            if nearest_neighbour[1] > dot[1]:
                cv2.circle(extracted_braille_image, (nearest_neighbour[0], nearest_neighbour[1]), 5, 128, 1)
                cv2.putText(extracted_braille_image, str(round(rotation, 1)), (nearest_neighbour[0] + 4, nearest_neighbour[1] + 25),  cv2.FONT_HERSHEY_PLAIN, 1, 128, 2, False)

            else:
                cv2.circle(extracted_braille_image, (dot[0], dot[1]), 5, 128, 1)
                cv2.putText(extracted_braille_image, str(round(rotation, 1)), (dot[0] + 4, dot[1] + 25),  cv2.FONT_HERSHEY_PLAIN, 1, 128, 2, False)
            rotation_data.append([rotation, distance])

    rotation_array = np.asarray(rotation_data)
    averages = np.mean(rotation_array, axis=0)
    mean_distance = averages[1]
    average_rotation = 0
    number_of_rotations = 0
    for data in rotation_data:
        if data[1] < mean_distance * 1.25 or data[1] > mean_distance * 0.75:
            average_rotation += data[0]
            number_of_rotations += 1

    average_rotation = average_rotation / number_of_rotations

    return -average_rotation


if __name__ == '__main__':

    # Print all array values setting
    np.set_printoptions(threshold=np.inf)

    image = cv2.imread("../Resources/braille-paper-4-book-rotate.jpg")

    # Initialise custom braille reader objects
    filter = BrailleFilter("Single-Sided", "black")

    # Filter input image and acquire locations of all braille dots
    extracted_braille_image, braille_dot_locations = filter.extractBrailleDots(image)

    rotation = calculate_required_image_rotation(braille_dot_locations)

    print(rotation)

    corrected_image = rotate_image(extracted_braille_image, rotation)

    cv2.imshow("Original", image)
    cv2.imshow("Rotated Image", corrected_image)
    cv2.imshow("Filtered", extracted_braille_image)

    cv2.waitKey(0)

    cv2.destroyAllWindows()
