/*
 * Accelerometer.cpp
 *
 *  Created on: Oct 28, 2017
 *      Author: plkr
 */

#include <periodic_scheduler/Accelerometer.h>
#include <iostream>
#include <stdio.h>
#include "math.h"

Accelerometer::Accelerometer(int update_Rate)
{
	pin = new GPIO(P2_6);
	pin->setAsOutput();
	pin->setHigh();
    windowSize = (int)(round(2 / (update_Rate / 1000)));
    //this->accNorm = new int[windowSize]; // amplitude of the acceleration
    varAcc   = 0.0; // variance of the acceleration on the window L
    minAcc   = 1.0;  // minimum of the acceleration on the window L
    maxAcc   = 0xff800000; // maximum of the acceleration on the window L
    threshold = 0xff800000; // threshold to detect a step
    sensitivity = 6.0;  // sensitivity to detect a step
    stepCount = -1;           // number of steps

    /*for (int i=0;i<50;i++)
    	this->stepArr.push_back(0);*/

    //this->stepArr; // steps in 2 seconds
    updateRate = update_Rate; //Update rate in ms
    //Kalmanfilter filter();
    //filter = new Kalmanfilter();

}

float Accelerometer::max(std::vector<float> args)
{
    float temp = args[0];
    for(int i=0; i<(int)args.size();i++)
    {
        if(args[i]>temp)
        {
            temp=args[i];
        }
    }
    return temp;
}

float Accelerometer::min(std::vector<float> args)
{
    float temp = args[0];
    for (int i=0; i<(int)args.size(); i++)
    {
        if(args[i]<temp)
        {
            temp = args[i];
        }
    }
    return temp;
}




float Accelerometer::computeNorm(int x, int y, int z)
{
	float norm;
	norm=sqrt(((x*x) + (y*y) + (z*z)));
    float filteredNorm =  this->filter.filter(norm);
    return filteredNorm/9.80665;

}

void Accelerometer::stepDetection()
{
    this->computeAccelerationVariance();
    this->maxAcc = max(this->accNorm);
    this->minAcc = min(this->accNorm);

    printf("\n Max Acc: %f",this->maxAcc);
    printf("\n Min Acc: %f", this->minAcc);
    this->threshold = (this->minAcc + this->maxAcc)/2;


    float diff = this->maxAcc - this->minAcc;
    printf("Before fabs: %f",diff);
    diff = fabsf(diff);
    printf("\n After fabs: %f",diff);

    if ((diff>=this->sensitivity) && (this->accNorm[this->accNorm.size()-1] >= this->threshold)
        && (this->accNorm[this->accNorm.size()-2] < this->threshold) /*&& (this->stepArr[this->stepArr.size()-1] == 0)*/)
    {
    	//printf("Increase in Step Count");
    	this->pin->setLow();
        this->stepCount++;
        this->stepArr.push_back(1);
        this->stepArr.erase(this->stepArr.begin());
        LD.setNumber((char)this->stepCount);
        this->pin->setHigh();
    }

    else{
    	//printf("Decrease in step count");
        this->stepArr.push_back(0);
        this->stepArr.erase(this->stepArr.begin());
    }

}

void Accelerometer::computeAccelerationVariance()
{
    float mean = 0.0;
    float mean2 = 0.0;

    for(int i =0; i<(int)this->accNorm.size(); i++)
    {
        mean += this->accNorm[i];
        mean2 += this->accNorm[i] + this->accNorm[i];
    }
    this->varAcc = ((mean*mean) - mean)/this->accNorm.size();

    if((this->varAcc - 0.5) > 0.0)
    {
        this->varAcc -= 0.5;
    }

    if(this->varAcc != NULL)
    {
        this->filter.setMeasurement(this->varAcc);
        this->sensitivity = 2.0 *(sqrt(this->varAcc)/(9.80665*9.80665));
    }
    else
    {
        this->sensitivity = 1.0 / 30.0;
    }

}

void Accelerometer::processMeasurement(int x1, int y, int z)
{

    float norm = this->computeNorm(x1, y, z);
    this->accNorm.push_back(norm);
    if(this->accNorm.size()>40)
    	this->accNorm.erase(this->accNorm.begin());
    this->stepDetection();
}
