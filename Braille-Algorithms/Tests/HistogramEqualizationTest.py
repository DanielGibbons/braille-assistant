import cv2
import numpy as np
from matplotlib import pyplot as plt


if __name__ == "__main__":

    # DEFINITIONS
    mStructureElement3 = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3))
    mStructureElement6 = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (6, 6))
    mClaheKernel = cv2.createCLAHE(clipLimit=3, tileGridSize=(8, 8))

    # LOCAL LIGHTNESS EQUALIZATION
    input_image = cv2.imread('../Resources/braille-paper-3.jpg')
    gray_image = cv2.cvtColor(input_image, cv2.COLOR_BGR2GRAY)
    # Convert image to LAB color model
    image_lab = cv2.cvtColor(input_image, cv2.COLOR_BGR2LAB)
    # Split the image into L, A, and B channels
    l_channel, a_channel, b_channel = cv2.split(image_lab)
    # Create CLAHE Kernel and apply to lightness channel
    adjusted_l_channel = mClaheKernel.apply(l_channel)
    # Merge the CLAHE enhanced L channel with the original A and B channel
    merged_channels = cv2.merge((adjusted_l_channel, a_channel, b_channel))
    # Convert image from LAB color model back to RGB color model
    lightness_equalized_image = cv2.cvtColor(merged_channels, cv2.COLOR_LAB2BGR)
    # Convert image to grayscale
    lightness_equalized_image = cv2.cvtColor(lightness_equalized_image, cv2.COLOR_BGR2GRAY)

    # GLOBAL EQUALIZATION
    hist, bins = np.histogram(lightness_equalized_image.flatten(), 256, [0, 256])
    cdf = hist.cumsum()
    cdf_m = np.ma.masked_equal(cdf, 0)
    cdf_m = (cdf_m - cdf_m.min()) * 255 / (cdf_m.max() - cdf_m.min())
    cdf = np.ma.filled(cdf_m, 0).astype('uint8')
    global_equalized_image = cdf[lightness_equalized_image]
    blur_global_equalized_image = cv2.medianBlur(global_equalized_image, 7)

    # THRESHOLDING AND NOISE REMOVAL
    ret, white_image = cv2.threshold(blur_global_equalized_image, 246, 255, cv2.THRESH_BINARY)
    eroded_white_image = cv2.erode(white_image, mStructureElement3, iterations=2)
    dilated_white_image = cv2.dilate(eroded_white_image, mStructureElement6, iterations=1)
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
        cv2.rectangle(dilated_white_image, (int(dot[0]) - 3, int(dot[1]) - 3), (int(dot[0]) + 3, int(dot[1]) + 3), 255, cv2.FILLED)
        cv2.circle(dilated_white_image, (int(dot[0]), int(dot[1])), 5, 255, cv2.FILLED)
        braille_dot_locations_list.append([int(dot[0]), int(dot[1])])

    # HISTOGRAM PLOTS
    fig = plt.figure(1)
    ax = fig.subplots()
    ax.set_title('Original Braille Image')
    ax.set_xlabel('Pixel Value')
    ax.set_ylabel('Number of Pixels')
    ax.hist(gray_image.flatten(), 256, [0, 256], color='r', histtype='bar', stacked=True)
    plt.show()

    fig = plt.figure(2)
    ax = fig.subplots()
    ax.set_title('Lightness Equalized Braille Image')
    ax.set_xlabel('Pixel Value')
    ax.set_ylabel('Number of Pixels')
    ax.hist(lightness_equalized_image.flatten(), 256, [0, 256], color='r', histtype='bar', stacked=True)
    plt.show()

    fig = plt.figure(3)
    ax = fig.subplots()
    ax.set_title('Global Equalized Braille Image')
    ax.set_xlabel('Pixel Value')
    ax.set_ylabel('Number of Pixels')
    ax.hist(blur_global_equalized_image.flatten(), 256, [0, 256], color='r', histtype='bar', stacked=True)
    plt.show()

    # # IMAGE PLOTS
    fig = plt.figure(2)

    img1 = fig.add_subplot(221, title='Original Braille Image')
    img1.imshow(input_image, cmap='gray')

    img2 = fig.add_subplot(222, title='Light Equalized Braille Image')
    img2.imshow(lightness_equalized_image, cmap='gray')

    img3 = fig.add_subplot(223, title='Globally Equalized Braille Image')
    img3.imshow(global_equalized_image, cmap='gray')

    img4 = fig.add_subplot(224, title='Filter Output Image')
    img4.imshow(dilated_white_image, cmap='gray')

    plt.tight_layout()
    plt.show()

    cv2.waitKey(0)
    cv2.destroyAllWindows()
