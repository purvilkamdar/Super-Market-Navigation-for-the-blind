/*
 * WirelessPart.cpp
 *
 *  Created on: Apr 21, 2017
 *      Author: Purvil Kamdar
 */

#include <WirelessPart.h>

Wireless_Part::Wireless_Part(uint8_t priority):scheduler_task("Wireless",2048,priority)
{
	// TODO Auto-generated constructor stub

}

bool Wireless_Part::init()
{
	mesh_set_node_address(2);
	return true;
}

bool Wireless_Part::run(void* p)
{
	int dest=1;
	//const char *data='h';
	wireless_send(dest,mesh_pkt_ack,NULL,1,1);
	return true;
}

