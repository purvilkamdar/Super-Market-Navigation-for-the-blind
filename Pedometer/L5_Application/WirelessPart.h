/*
 * WirelessPart.h
 *
 *  Created on: Apr 21, 2017
 *      Author: Purvil Kamdar
 */

#ifndef L5_APPLICATION_WIRELESSPART_H_
#define L5_APPLICATION_WIRELESSPART_H_
#include "tasks.hpp"
#include "wireless.h"
#include "sys_config.h"

class Wireless_Part: public scheduler_task {
public:
	Wireless_Part(uint8_t priority);
	bool init();
	bool run(void* p);
};

#endif /* L5_APPLICATION_WIRELESSPART_H_ */
