/*
 * Kalmanfilter.cpp
 *
 *  Created on: Oct 28, 2017
 *      Author: plkr
 */

#include <periodic_scheduler/Kalmanfilter.h>
#include <stdlib.h>

Kalmanfilter::Kalmanfilter(int R1, int Q1, int A1, int B1, int C1)
{
    R = R1; //noise power desirable
    Q = Q1; //noise power estimated

    A = A1;
    B = B1;
    C = C1;

    cov = NULL;
    x = NULL;
}
float Kalmanfilter::filter(int z, int u)
{
    if(this->x != NULL)
    {
        this->x = (1/this->C) * z;
        this->cov = (1 / this->C) * this->Q * (1 / this->C);
    }
    else
    {

        //Compute prediction
        const float predX = (this->A * this->x) + (this->B * u);
        const float predCov = ((this->A * this->cov) * this->A) + this->R;

        //Kalman gain
        const float K = predCov * this->C * (1 / ((this->C * predCov * this->C) + this->Q));

        //Correction
        this->x = predX + K * (z - (this->C * predX));
        this->cov = predCov - (K * this->C * predCov);
      }

  return this->x;
}

float Kalmanfilter::lastMeasurement()
{
    return this->x;
}

void Kalmanfilter::setMeasurement(int noise)
{
    this->Q = noise;
}

void Kalmanfilter::setProcessNoise(int noise)
{
    this->R = noise;
}

