Vibration Monitoring App for Android SmartPhones
This is an Android app that leverages the Accelerometer sensor that is built into modern SmartPhones. This sensor monitors acceleration along X, Y and Z axes. The App shows a plot of acceleration with time for the selected axis. It allows to select the axis for consideration.

A time domain plot of acceleration gives limited information. It can help detect motion in particular direction and measure amount of motion, velocity, etc. However when there is repeated motion or vibration we see a definite pattern in the time plot. More details can be obtained using Fourier Analysis using a technique called Discrete Fourier Transform (DFT). DFT converts the signal from time to frequency domain providing the frequencies in which the energy of the signal is distributed.

The app provides a frequency plot for the DFT using an algorithm called Fast Fourier Transform (FFT). The FFT is an algorithm for obtaining the DFT but uses optimizations like recursion which makes it much faster to obtain the DFT.

As a practical example, you can keep the SmartPhone on a motor casing with Z axis selected. As the motor runs, the whole body undergoes vibration. Assume the motor runs at 600rpm (10Hz). So if you have placed the SmartPhone correctly you should see the frequency peaking near 10Hz. If there is misalignment or imbalance there will be some other frequencies which will produce noise.

In industrial machines, accelerometers are mounted appropriately on the machine to monitor axial and radial vibrations and using frequency analysis - frequencies of interest are identified which can show signatures of imbalance, misalignment or even cracks.

This is a general purpose app that uses the SmartPhone sensor. It can be applied in different domains to perform vibration analysis. Lot depends on how you place the phone on the machine - as close as possible to the source of vibration.

Would love to hear if you found this beneficial. Write to me at: dattarajrao@yahoo.com 

Charting library used: AChartEngine 

FFT code inspired from example by: University of Princeton 

(../project_images/fftchart.png?raw=true "Example FFT chart")
(../project_images/vibanalyzer_promo3.jpg?raw=true "Example of usage of app")

