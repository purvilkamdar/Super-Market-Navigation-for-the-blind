
/*
 * Accelerometer.h
 *
 *  Created on: Oct 28, 2017
 *      Author: plkr
 */

#ifndef ACCELEROMETER_H_
#define ACCELEROMETER_H_

#include <math.h>
#include <iostream>
#include <vector>
#include "Kalmanfilter.h"
#include <stdlib.h>
#include "io.hpp"
#include "gpio.hpp"
class Accelerometer{
		GPIO *pin;
        int windowSize;
        std::vector<float> accNorm; // amplitude of the acceleration
        float varAcc; // variance of the acceleration on the window L
        float minAcc;  // minimum of the acceleration on the window L
        float maxAcc; // maximum of the acceleration on the window L
        float threshold; // threshold to detect a step
        float sensitivity;  // sensitivity to detect a step
        int stepCount;           // number of steps
        std::vector<int> stepArr; // steps in 2 seconds
        int updateRate; //Update rate in ms
        Kalmanfilter filter;
public:
    Accelerometer(int update_Rate);
    //virtual ~Accelerometer();
    void processMeasurement(int x, int y, int z);
    float computeNorm(int x, int y, int z);
    void stepDetection();
    void computeAccelerationVariance();
    float max(std::vector<float> args);
    float min(std::vector<float> args);
};

#endif /* ACCELEROMETER_H_ */
