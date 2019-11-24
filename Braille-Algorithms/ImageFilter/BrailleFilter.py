import numpy as np
import cv2


class BrailleFilter(object):

    def __init__(self, filter_type, dot_colour):
        # Print all array values setting
        np.set_printoptions(threshold=np.inf)
        self.mFilterType = filter_type
        self.mDotColour = dot_colour
        self.mStructureElement3 = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3))
        self.mStructureElement6 = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (6, 6))
        self.mClaheKernel = cv2.createCLAHE(clipLimit=3, tileGridSize=(8, 8))

    def __applyLocalLightnessEqualization(self, input_image):
        # Convert image to LAB color model
        image_lab = cv2.cvtColor(input_image, cv2.COLOR_BGR2LAB)
        # Split the image into L, A, and B channels
        l_channel, a_channel, b_channel = cv2.split(image_lab)
        # Create CLAHE Kernel and apply to lightness channel
        adjusted_l_channel = self.mClaheKernel.apply(l_channel)
        # Merge the CLAHE enhanced L channel with the original A and B channel
        merged_channels = cv2.merge((adjusted_l_channel, a_channel, b_channel))
        # Convert image from LAB color model back to RGB color model
        lightness_equalized_image = cv2.cvtColor(merged_channels, cv2.COLOR_LAB2BGR)
        # Convert image to grayscale
        lightness_equalized_image = cv2.cvtColor(lightness_equalized_image, cv2.COLOR_BGR2GRAY)
        return lightness_equalized_image

    def __applyGlobalHistogramEqualization(self, input_image):
        # Calculate histogram of image and fined corresponding CDF
        hist, bins = np.histogram(input_image.flatten(), 256, [0, 256])
        cdf = hist.cumsum()
        cdf_m = np.ma.masked_equal(cdf, 0)
        cdf_m = (cdf_m - cdf_m.min()) * 255 / (cdf_m.max() - cdf_m.min())
        cdf = np.ma.filled(cdf_m, 0).astype('uint8')
        # Apply CDF transform to grayscaled input image
        global_equalized_image = cdf[input_image]
        # Apply median filter to remove high frequency noise
        blurred_global_equalized_image = cv2.medianBlur(global_equalized_image, 7)
        return blurred_global_equalized_image

    def __applyThresholding(self, input_image):
        # White areas indicate the top of each braille dot
        if self.mDotColour == "white":
            ret, white_image = cv2.threshold(input_image, 246, 255, cv2.THRESH_BINARY)
        elif self.mDotColour == "black":
            ret, white_image = cv2.threshold(input_image, 15, 255, cv2.THRESH_BINARY)
        else:
            exit()
        eroded_white_image = cv2.erode(white_image, self.mStructureElement3, iterations=2)
        dilated_white_image = cv2.dilate(eroded_white_image, self.mStructureElement6, iterations=1)
        if self.mDotColour == "black":
            dilated_white_image = cv2.bitwise_not(dilated_white_image)

        # Find connected components and extract the mean area
        cc_output = cv2.connectedComponentsWithStats(dilated_white_image, 4, cv2.CV_8U)
        stats = cc_output[2]
        stats = np.delete(stats, 0, 0)  # delete background component
        mean_area = np.mean(stats[:, cv2.CC_STAT_AREA])

        # Remove components that are < mean_area/1.5
        for stat in stats:
            if stat[cv2.CC_STAT_AREA] < mean_area / 2:
                x1 = stat[cv2.CC_STAT_LEFT]
                x2 = x1 + stat[cv2.CC_STAT_WIDTH]
                y1 = stat[cv2.CC_STAT_TOP]
                y2 = y1 + stat[cv2.CC_STAT_HEIGHT]
                cv2.rectangle(dilated_white_image, (x1, y1), (x2, y2), 0, cv2.FILLED)

        cc_output = cv2.connectedComponentsWithStats(dilated_white_image, 4, cv2.CV_8U)
        stats = cc_output[2]
        centroids = cc_output[3]
        braille_dot_stats = np.delete(stats, 0, 0)  # delete background component
        braille_dot_centres = np.delete(centroids, 0, 0)  # delete background component
        braille_dot_locations_list = []

        for stat, dot in zip(braille_dot_stats, braille_dot_centres):
            x1 = stat[cv2.CC_STAT_LEFT]
            x2 = x1 + stat[cv2.CC_STAT_WIDTH]
            y1 = stat[cv2.CC_STAT_TOP]
            y2 = y1 + stat[cv2.CC_STAT_HEIGHT]
            cv2.rectangle(dilated_white_image, (x1, y1), (x2, y2), 0, cv2.FILLED)
            cv2.circle(dilated_white_image, (int(dot[0]), int(dot[1])), 3, 255, cv2.FILLED)
            braille_dot_locations_list.append([int(dot[0]), int(dot[1])])

        # sort dot locations based on row
        braille_dot_locations = np.asarray(braille_dot_locations_list)
        if len(braille_dot_locations) > 1:
            braille_dot_locations = braille_dot_locations[braille_dot_locations[:, 1].argsort(kind='mergesort')]

        return dilated_white_image, braille_dot_locations

    def extractBrailleDots(self, input_image):
        if self.mFilterType == "Single-Sided":
            lightness_equalized_image = self.__applyLocalLightnessEqualization(input_image)
            global_equalized_image = self.__applyGlobalHistogramEqualization(lightness_equalized_image)
            output_image, braille_dot_locations = self.__applyThresholding(global_equalized_image)
            return output_image, braille_dot_locations
