package com.example.parthpachchigar.hardwaredevice;

import android.util.Log;

/**
 * Created by plkr on 10/30/2017.
 */

public class compi extends Thread{
    int iPhi, iThe, iPsi;
    /* magnetic field readings corrected for hard iron effects and PCB orientation */
    int iBfx, iBfy, iBfz;
    /* hard iron estimate */
    int iVx;
    int iVy;
    int iVz;
    private Accelerometer acc;
    private Magnetometer mag;
    compi() {
        try

        {
            iVx = 419;
            iVy = 683;
            iVz = 528;
            acc = new Accelerometer();
            mag = new Magnetometer();
        }
        catch(Exception e)
        {
            Log.w("Error in compi",e.toString());
        }
    }
    /* tilt-compensated e-Compass code */
    public int iTrig(int ix, int iy) {
        int MINDELTATRIG = 1;
        int itmp; /* scratch */
        int ixsq; /* ix * ix */
        int isignx; /* storage for sign of x. algorithm assumes x >= 0 then corrects later */
        int ihypsq; /* (ix * ix) + (iy * iy) */
        int ir; /* result = ix / sqrt(ix*ix+iy*iy) range -1, 1 returned as signed int */
        int idelta; /* delta on candidate result dividing each stage by factor of 2 */
/* stack variables */
/* ix, iy: signed 16 bit integers representing sensor reading in range -32768 to 32767 */
/* function returns signed int as signed fraction (ie +32767=0.99997, -32768=-1.0000) */
/* algorithm solves for ir*ir*(ix*ix+iy*iy)=ix*ix */
/* correct for pathological case: ix==iy==0 */
        if ((ix == 0) && (iy == 0)) ix = iy = 1;
/* check for -32768 which is not handled correctly */
        if (ix == -32768) ix = -32767;
        if (iy == -32768) iy = -32767;
/* store the sign for later use. algorithm assumes x is positive for convenience */
        isignx = 1;
        if (ix < 0) {
            ix = (int) -ix;
            isignx = -1;
        }
/* for convenience in the boosting set iy to be positive as well as ix */
        iy = (int) Math.abs(iy);
/* to reduce quantization effects, boost ix and iy but keep below maximum signed 16 bit */
        while ((ix < 16384) && (iy < 16384)) {
            ix = (int) (ix + ix);
            iy = (int) (iy + iy);
        }
/* calculate ix*ix and the hypotenuse squared */
        ixsq = (int) (ix * ix); /* ixsq=ix*ix: 0 to 32767^2 = 1073676289 */
        ihypsq = (int) (ixsq + iy * iy); /* ihypsq=(ix*ix+iy*iy) 0 to 2*32767*32767=2147352578 */
/* set result r to zero and binary search step to 16384 = 0.5 */
        ir = 0;
        idelta = 16384; /* set as 2^14 = 0.5 */
/* loop over binary sub-division algorithm */
        do {
/* generate new candidate solution for ir and test if we are too high or too low */
/* itmp=(ir+delta)^2, range 0 to 32767*32767 = 2^30 = 1073676289 */
            itmp = (int) ((ir + idelta) * (ir + idelta));
/* itmp=(ir+delta)^2*(ix*ix+iy*iy), range 0 to 2^31 = 2147221516 */
            itmp = (itmp >> 15) * (ihypsq >> 15);
            if (itmp <= ixsq) ir += idelta;
            idelta = (int) (idelta >> 1); /* divide by 2 using right shift one bit */
        } while (idelta >= MINDELTATRIG); /* last loop is performed for idelta=MINDELTATRIG */
/* correct the sign before returning */
        return (int) (ir * isignx);
    }

    /* function to calculate ir = iy / ix with iy <= ix, and ix, iy both > 0 */
    static int iDivide(int iy, int ix)
    {
        int MINDELTADIV = 1; /* final step size for iDivide */
        int itmp; /* scratch */
        int ir; /* result = iy / ix range 0., 1. returned in range 0 to 32767 */
        int idelta; /* delta on candidate result dividing each stage by factor of 2 */
/* set result r to zero and binary search step to 16384 = 0.5 */
        ir = 0;
        idelta = 16384; /* set as 2^14 = 0.5 */
/* to reduce quantization effects, boost ix and iy to the maximum signed 16 bit value */
        while ((ix < 16384) && (iy < 16384))
        {
            ix = (int)(ix + ix);
            iy = (int)(iy + iy);
        }
/* loop over binary sub-division algorithm solving for ir*ix = iy */
        do
        {
/* generate new candidate solution for ir and test if we are too high or too low */
            itmp = (int)(ir + idelta); /* itmp=ir+delta, the candidate solution */
            itmp = (int)((itmp * ix) >> 15);
            if (itmp <= iy) ir += idelta;
            idelta = (int)(idelta >> 1); /* divide by 2 using right shift one bit */
        } while (idelta >= MINDELTADIV); /* last loop is performed for idelta=MINDELTADIV */
        return (ir);
    }
    public int iHundredAtanDeg(int iy, int ix)
    {
         /* fifth order of polynomial approximation giving 0.05 deg max error */
        int K1 = 5701;
        int K2 = -1645;
        int K3 = 446;

        int iAngle; /* angle in degrees times 100 */
        int iRatio; /* ratio of iy / ix or vice versa */
        int iTmp; /* temporary variable */
/* check for pathological cases */
        if ((ix == 0) && (iy == 0)) return (0);
        if ((ix == 0) && (iy != 0)) return (9000);
/* check for non-pathological cases */
        if (iy <= ix)
            iRatio = iDivide(iy, ix); /* return a fraction in range 0. to 32767 = 0. to 1. */
        else
            iRatio = iDivide(ix, iy); /* return a fraction in range 0. to 32767 = 0. to 1. */
/* first, third and fifth order polynomial approximation */
        iAngle = (int) K1 * (int) iRatio;
        iTmp = ((int) iRatio >> 5) * ((int) iRatio >> 5) * ((int) iRatio >> 5);
        iAngle += (iTmp >> 15) * (int) K2;
        iTmp = (iTmp >> 20) * ((int) iRatio >> 5) * ((int) iRatio >> 5);
        iAngle += (iTmp >> 15) * (int) K3;
        iAngle = iAngle >> 15;
/* check if above 45 degrees */
        if (iy > ix) iAngle = (int)(9000 - iAngle);
/* for tidiness, limit result to range 0 to 9000 equals 0.0 to 90.0 degrees */
        if (iAngle < 0) iAngle = 0;
        if (iAngle > 9000) iAngle = 9000;
        return ((int) iAngle);
    }
    public int iHundredAtan2Deg(int iy, int ix) {
        int iResult; /* angle in degrees times 100 */
/* check for -32768 which is not handled correctly */
        if (ix == -32768) ix = -32767;
        if (iy == -32768) iy = -32767;
/* check for quadrants */
        if ((ix >= 0) && (iy >= 0)) /* range 0 to 90 degrees */
            iResult = iHundredAtanDeg(iy, ix);
        else if ((ix <= 0) && (iy >= 0)) /* range 90 to 180 degrees */
            iResult = (int) (18000 - (int) iHundredAtanDeg(iy, (int) - ix));
        else if ((ix <= 0) && (iy <= 0)) /* range -180 to -90 degrees */
            iResult = (int) ((int) -18000 + iHundredAtanDeg((int) -iy, (int) -ix));
        else /* ix >=0 and iy <= 0 giving range -90 to 0 degrees */
            iResult = (int) (-iHundredAtanDeg((int) - iy, ix));
        return (iResult);
    }
    @Override
    public void run() {
        while (true) {
            int iBpx, iBpy, iBpz;
            int iGpx, iGpy, iGpz;
            int[] temp_acc = new int[3];
            int[] temp_mag = new int[3];
            try {
                temp_acc = acc.getRawValues();
                temp_mag = mag.getRawValues();
            } catch (Exception e) {
                Log.w("Error in compi", e.toString());
            }
            iBpx = temp_mag[0];
            iBpy = temp_mag[1];
            iBpz = temp_mag[2];
            iGpx = temp_acc[0];
            iGpy = temp_acc[1];
            iGpz = temp_acc[2];
            //Log.w("Magnetometer:",String.valueOf(temp_mag));
            //Log.w("Accelerometer:",String.valueOf(temp_acc));
/* stack variables */
/* iBpx, iBpy, iBpz: the three components of the magnetometer sensor */
/* iGpx, iGpy, iGpz: the three components of the accelerometer sensor */
/* local variables */
            int iSin, iCos; /* sine and cosine */
/* subtract the hard iron offset */
            iBpx -= iVx; /* see Eq 16 */
            iBpy -= iVy; /* see Eq 16 */
            iBpz -= iVz; /* see Eq 16 */

/* calculate current roll angle Phi */
            iPhi = iHundredAtan2Deg(iGpy, iGpz);/* Eq 13 */
/* calculate sin and cosine of roll angle Phi */
            iSin = iTrig(iGpy, iGpz); /* Eq 13: sin = opposite / hypotenuse */
            iCos = iTrig(iGpz, iGpy); /* Eq 13: cos = adjacent / hypotenuse */
/* de-rotate by roll angle Phi */
            iBfy = (int) ((iBpy * iCos - iBpz * iSin) >> 15);/* Eq 19 y component */
            iBpz = (int) ((iBpy * iSin + iBpz * iCos) >> 15);/* Bpy*sin(Phi)+Bpz*cos(Phi)*/
            iGpz = (int) ((iGpy * iSin + iGpz * iCos) >> 15);/* Eq 15 denominator */
/* calculate current pitch angle Theta */
            iThe = iHundredAtan2Deg((int) -iGpx, iGpz);/* Eq 15 */
/* restrict pitch angle to range -90 to 90 degrees */
            if (iThe > 9000) iThe = (int) (18000 - iThe);
            if (iThe < -9000) iThe = (int) (-18000 - iThe);
/* calculate sin and cosine of pitch angle Theta */
            iSin = (int) -iTrig(iGpx, iGpz); /* Eq 15: sin = opposite / hypotenuse */
            iCos = iTrig(iGpz, iGpx); /* Eq 15: cos = adjacent / hypotenuse */
/* correct cosine if pitch not in range -90 to 90 degrees */
            if (iCos < 0) iCos = (int) -iCos;
/* de-rotate by pitch angle Theta */
            iBfx = (int) ((iBpx * iCos + iBpz * iSin) >> 15); /* Eq 19: x component */
            iBfz = (int) ((-iBpx * iSin + iBpz * iCos) >> 15);/* Eq 19: z component */
/* calculate current yaw = e-compass angle Psi */
            iPsi = iHundredAtan2Deg((int) -iBfy, iBfx); /* Eq 22 */
            Log.w("Value:", String.valueOf(iPsi));
            try {
                Thread.sleep(100);
            }
            catch (Exception e)
            {

            }
        }
    }

    public void unknown_function()
    {
        int tmpAngle; /* temporary angle*100 deg: range -36000 to 36000 */
        int iLPPsi = 0; /* low pass filtered angle*100 deg: range -18000 to 18000 */

        int ANGLE_LPF = 32768/20; /* low pass filter: set to 32768 / N for N samples averaging */
/* implement a modulo arithmetic exponential low pass filter on the yaw angle */
/* compute the change in angle modulo 360 degrees */
        tmpAngle = (int)iPsi - (int)iLPPsi;
        if (tmpAngle > 18000) tmpAngle -= 36000;
        if (tmpAngle < -18000) tmpAngle += 36000;
/* calculate the new low pass filtered angle */
        tmpAngle = (int)iLPPsi + ((ANGLE_LPF * tmpAngle) >> 15);
/* check that the angle remains in -180 to 180 deg bounds */
        if (tmpAngle > 18000) tmpAngle -= 36000;
        if (tmpAngle < -18000) tmpAngle += 36000;
/* store the correctly bounded low pass filtered angle */
        iLPPsi = (int)tmpAngle;

        if (tmpAngle > 9000) tmpAngle = (int) (18000 - tmpAngle);
        if (tmpAngle < -9000) tmpAngle = (int) (-18000 - tmpAngle);
    }
}
