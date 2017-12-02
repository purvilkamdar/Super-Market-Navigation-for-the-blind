/*
 * Kalmanfilter.h
 *
 *  Created on: Oct 28, 2017
 *      Author: plkr
 */

#ifndef KALMANFILTER_H_
#define KALMANFILTER_H_

class Kalmanfilter {
public:
    int R; //noise power desirable
    int Q; //noise power estimated
    int A;
    int B;
    int C;
    float cov;
    float x;
    Kalmanfilter(int R1 = 1, int Q1 = 1, int A1 = 1, int B1 = 0, int C1 = 1);
    float filter(int z, int u=0);
    float lastMeasurement();
    void setMeasurement(int noise);
    void setProcessNoise(int noise);
};

#endif /* KALMANFILTER_H_ */
