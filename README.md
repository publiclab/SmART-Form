# About

We spend 90% of our time indoors and the air in most of our homes contains formaldehyde. Formaldehyde exposure can worsen health. This app works with color-changing badges to allow users to measure their indoor formaldehyde exposure. The app provides information on recommended formaldehyde levels and tips on reducing exposure if the concentration is high. Users will also be given the option to upload their results and complete a survey to create a national database of formaldehyde levels. The survey will ask about housing characteristics and also include the option to report health symptoms.

For more information on the the proccess of development and validation of this system please see our paper in _Building and Environment_ ["Smartphone app for residential testing of formaldehyde (SmART-Form)"](https://www.sciencedirect.com/science/article/pii/S0360132318307248?via%3Dihub)

Creation of this app was funded by National Science Foundation (NSF) grant 1645048. This app was created by the authors and has not been reviewed by NSF.

# Documentation

The following documentation is adapted from Zhang, Shapiro, Gehrke, Castner, Liu, Guo, Prasad, Zhang, Haines, Kormos, Frey, Qin, and Dannemiller “[Smartphone app for residential testing of formaldehyde (SmART-Form)](https://www.sciencedirect.com/science/article/pii/S0360132318307248?via%3Dihub)” in Building and Environment Volume 148, 15 January 2019, Pages 567-578. https://doi.org/10.1016/j.buildenv.2018.11.029 All bracketed citation numbers refer back to this article. 

## Layout

To facilitate organizing multiple tests, the main layout was designed with a master-detail two-panel structure: table view and detail view. The table view contains all existing tests with titles and dates, and the detail view displays the whole process of a badge test, including user input parameters, images taken by the camera, and access to data upload surveys.

![Screenshots from the app](https://ars.els-cdn.com/content/image/1-s2.0-S0360132318307248-gr6.jpg "Screenshots from the app")

## Test Management

The object-oriented design structure was focused on the badge test, and for each test, we included the unique ID, title, date, images as inputs, and result as output. For memory efficiency, the test objects were stored in JSON format and images were referred by path in the internal storage.

## Image Processing

In our app, we experimented with taking images with different parameters on different phones, and selected a set of the most common parameters each for Android and IOS systems to minimize variation. Our mathematic model uses the calibration patch on the badge to account for environmental variation, and we expect this to partially address differences in camera parameters. 

For the mathmatical model we used and additional information please see [this suplementry infomration page](https://ars.els-cdn.com/content/image/1-s2.0-S0360132318307248-mmc1.docx) (the formulas didn't render well on here on GH). 

![chart](https://ars.els-cdn.com/content/image/1-s2.0-S0360132318307248-gr2.jpg)

Briefly, color change is measured by the color change ratio of lightness by comparing the color changing area to the calibration (non-changing) patch. Lightness is a relative value (unitless) and is computed from standard RGB images with fixed value ranges. Normally the lightness of the actual environment is measured through the magnitude of the analogue electrical pulses of the light sensor chips (with units W∙m^−2∙sr^−1 where sr refers to steradian). However the digitization process of the smartphone built-in camera quantifies these pulses into the unitless RGB values. Therefore the lightness values represent the magnitude of the lighting condition in a relative sense (up to a scale). The “color change ratio” in our model takes advantage of the fact that the reaction and calibration patch undergo the same light illumination and by dividing these values, their units can be cancelled.

## User Warnings

We identified key conditions that could lead to erroneous readings throughout app development ([Table 1](https://www.sciencedirect.com/science/article/pii/S0360132318307248#tbl1)). For each condition, we analyzed images taken under both suitable (no error) and unsuitable (may cause an error) conditions. The parameter associated with each error, such as lightness or saturation, was chosen based on the value with the most substantial difference between conditions. Boundaries for warnings were selected to be about halfway between extreme points measured in suitable and unsuitable conditions.

We noted that high relative humidity conditions above about 75–80% will interfere with the color change of the badge and can potentially cause erroneous readings in the app. Fortunately, under these conditions the badge also develops a blue/purple tint that is detectable in the image. We incubated badges at various relative humidity conditions to determine when an interfering blue color appeared on the badge. The lowest saturation value detected in suitable conditions was 0.80 and the highest saturation value detected in unsuitable conditions was 0.52. Therefore, we selected 0.6 as the boundary for activation of this warning. We also subjected badges to long-term, high-temperature storage conditions to produce contamination and quantified detection with the app.

For exposure time, we selected a 72 h period to balance detection capabilities with user time and consistency in the color-changing area of the badge. The badge can theoretically be read at different times that allow for sufficient color change (at least about 12 h), but those parameters were not validated here. Taking an image in a very short amount of time (for instance, 5 min) will result in an artificially high value by dividing by a very small time value due to the algorithm used, and thus we also wanted to prevent this error. We also placed a limit on the reported values so that high and low values will be reported as >120 ppb and <20 ppb, respectively. This range represents the calibration range above the method detection limit. Values above 120 ppb may have also experienced saturation on the badge, but this needs further evaluation to confirm.

## Algorithm

The mathematical model is based on the color change ratio (illumination or lightness) between the calibration patch and the chemical badge. Fig. 4 highlights the areas in green and red, respectively. These two blocks of bitmap were cropped and calculated for the mean value of the color intensity I, and the ratio = I(red)/I(green). We confirmed a linear relationship between exposure (formaldehyde concentration in ppb∙hr) and the color change ratio.

![badge](https://ars.els-cdn.com/content/image/1-s2.0-S0360132318307248-gr4.jpg)

The calibration patch noted in green and the color-changing area is noted in red. Each block contains 50 × 50 pixels in the 250 × 250 cropped badge image. 

## User Interface

![UI](https://ars.els-cdn.com/content/image/1-s2.0-S0360132318307248-gr5.jpg) 

Image by Kevin Nguyen @lightandluck

## Calibration

![graph](https://ars.els-cdn.com/content/image/1-s2.0-S0360132318307248-gr7_lrg.jpg)

Calibration of the app. A. The best-fit line to the data was y = −36301x + 36671 (R2 = 0.8811 and P < 0.0001) and is shown in small blue dots. Standard deviation lines (dashed outer lines) are shown in green around data. The standard deviation of the data was equivalent to 10.9 ppb at 72 h of exposure. B. Co-exposure to acetaldehyde or a VOC mixture did not interfere with measurement (P = 0.93, P = 0.07, respectively).

## Challenges

Uncontrolled lighting conditions present the greatest challenge for use of this system. The user warning about ambient lighting conditions is critical to successful use of the app. Shadows, overexposure, multiple light scattering, and low light are situations that could affect the reading of the badge. Previous work has considered these challenges in applications of surface material and optical property measurement. Normally, measuring the albedo of a surface requires a lab-based reflectance measurement [28,29], with a single light source and an object with known shape (i.e. a perfect sphere) in a dark room. In a natural or indoor environment, surface albedo measurement requires physical-model based disturbance correction (e.g. atmosphere and light scattering) with in situ information such as scene geometry (walls, tables, etc. in the room) or humidity/weather conditions [[30], [31], [32]]. In our system, we know that the badge is a flat surface, while the environment and its lighting are unpredictable. We designed the badge to include a calibration patch, where we assumed that this portion in the smartphone image encodes variants of the environment. Using this, we are able to approximate the albedo computation through calculating the ratio of lightness of the reaction and calibration areas. We also integrated warnings to the user within the app to account for some of these conditions, but potential still remains for introduced error, which was observed in our field test when taking images of the badge with a different orientation to a light source. The mathematical light reflectance model we used in our algorithm is a simple linear color change ratio between the calibration patch and the reaction patch. This is based on the assumption that the indoor environment contains only homogeneous and ambient light. Although the calibration patch is able to capture most of the environment lighting, this model is not able to account for complex and often non-linear lighting environments and non-standard image-taking practices, such as non-orthogonalized view, concentrated light sources, and inhomogenous shadows. In this work, our capability is limited to a standard and commercial product for designing a more capable calibration patch accounting for more complex lighting environment, which could be attempted in future work. Future enhancements of the system can focus on further improving mathematical models used for color calculation, and improving and systematizing conditions under which images are taken.

We also noted that the formaldehyde levels measured in our field test were, on average, slightly higher compared to those found in some other studies but still within a similar range [26,27,[33], [34], [35], [36], [37]]. It is unclear whether this is due to systemically higher formaldehyde levels in this particular community with environmental concerns, or if other co-contaminants not considered in the chamber study may inflate values. A future side-by-side field test with the DNPH measurement method would help to identify the reason for these levels and also potentially indicate additional improvements for the system.

The standard deviation of our data was 10.9 ppb if the badge has been exposed for 72 h and the image is taken with a standard orientation to the light source. For example, this means that a reading of 35 ppb is 68% likely to have a true value between 24 and 46 ppb, and a reading of 85 ppb is 68% likely to have a true value between 74 and 96 ppb. It is always desirable to obtain a more precise reading. However, the accuracy and precision available here is most likely acceptable to citizen scientists or concerned citizens who want to quickly determine the general range of their formaldehyde exposure with an inexpensive and easily-accessible method. This device is best used as a screening tool to determine if their exposure is high or low, and can inform decisions about further testing or potential remediation.

Colorimetric badges have well-established limitations that will also impact the use of this system. This includes dependence on temperature, relative humidity, and pressure/air flow in the ambient environment [38,39]. Another general limitation is the interpretation of color change intensity by the human eye [38], which we are able to overcome with the use of the smartphone app. These well-established limitations should be weighed against the benefits of using this system for measurement, including ease of use and low cost.

# Contact:

Karen Dannemiller, PhD   (dannemiller dot 70 at osu)

Rongjun Qin, PhD (qin dot 324 at osu dot edu)

Jessica Castner, PhD, RN, FAEN (jpcastner15 at gmail)

Nick Shapiro, PhD (nickshapiro at ucla)

Gretchen Gehrke, PhD (gretchen at publiclab)
