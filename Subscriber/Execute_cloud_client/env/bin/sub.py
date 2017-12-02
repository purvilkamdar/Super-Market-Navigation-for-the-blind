#!/usr/bin/env python

# Copyright 2016 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""This application demonstrates how to perform basic operations on
subscriptions with the Cloud Pub/Sub API.

For more information, see the README.md under /pubsub and the documentation
at https://cloud.google.com/pubsub/docs.
"""

import argparse
import time
from google.cloud import pubsub_v1
from google.cloud.pubsub_v1.subscriber.policy.thread import Policy
import grpc
import json
import ast
import requests


class UnavailableHackPolicy(Policy):
    def on_exception(self, exception):
        """
        There is issue on grpc channel that launch an UNAVAILABLE exception now and then. Until
        that issue is fixed we need to protect our consumer thread from broke.
        https://github.com/GoogleCloudPlatform/google-cloud-python/issues/2683
        """
        unavailable = grpc.StatusCode.UNAVAILABLE
        if getattr(exception, 'code', lambda: None)() in [unavailable]:
            print("OrbitalHack! - {}".format(exception))
            return
        return super(UnavailableHackPolicy, self).on_exception(exception)


def list_subscriptions(project, topic_name):
    """Lists all subscriptions for a given topic."""
    subscriber = pubsub_v1.SubscriberClient()
    topic_path = subscriber.topic_path(project, topic_name)

    for subscription in subscriber.list_subscriptions(topic_path):
        print(subscription.name)


def receive_messages(project, subscription_name):
    """Receives messages from a pull subscription."""
    subscriber = pubsub_v1.SubscriberClient(policy_class=UnavailableHackPolicy)
    # subscriber = pubsub_v1.SubscriberClient()
    subscription_path = subscriber.subscription_path(
        project, subscription_name)

    def callback(message):
        try:
            string = ('Received message: {}'.format(message))
            print(str(string))
            dic = {}
            temp_string1 = string.split('attributes: ')
            if 'lat' in temp_string1[1]:
                temp_string = str(temp_string1[1]).split(',')
                # print ("list="+str(temp_string))
                key = str(temp_string[0]).split(':')
                value = str(key[1]).split('u')
                final_value = str(value[1]).replace('"', '')

                final_value = final_value.strip('\'')
                dic['lat'] = (final_value)
                # print("dict="+str(dic))

                key1 = str(temp_string[1]).split(':')
                value1 = str(key1[1]).split('u')
                key2 = str(value1[1]).split('}')
                final_value2 = key2[0]
                final_value2 = final_value2.strip('\'')
                dic['lon'] = final_value2

                message.ack()
                res = requests.post(url="http://localhost:3000/co-ordinates", json=dic)
                print("Sent the co-ordinates")

            elif 'compass' in temp_string1[1]:
                print ("compass in the string")
                key1 = str(temp_string1[1]).split(':')
                value1 = str(key1[1]).split('u')
                final_value1 = str(value1[1]).replace('"', '')
                final_value1 = final_value1.split('}')
                final_value2 = str(final_value1[0]).strip('\'')
                dic['compass'] = final_value2
                print (str(dic))
                message.ack()
                res = requests.post(url="http://localhost:3000/compass", json=dic)
                print("Sent the compass values")

            elif 'left' in temp_string1[1]:
                print ("Sensor values")
                temp_string = str(temp_string1[1]).split(',')
                # print ("list="+str(temp_string))
                key = str(temp_string[0]).split(':')
                value = str(key[1]).split('u')
                final_value = str(value[1]).replace('"', '')

                final_value = final_value.strip('\'')
                print ("left:"+final_value)
                dic['left'] = (final_value)

                key = str(temp_string[1]).split(':')
                value = str(key[1]).split('u')
                final_value = str(value[1]).replace('"', '')

                final_value = final_value.strip('\'')
                print ("center:"+final_value)
                dic['center'] = final_value


                key1 = str(temp_string[2]).split(':')
                value1 = str(key1[1]).split('u')
                key2 = str(value1[1]).split('}')
                final_value2 = key2[0]
                final_value2 = final_value2.strip('\'')
                print("right:"+final_value2)
                dic['right'] = final_value2

                message.ack()
                print(str(dic))
                res = requests.post(url="http://localhost:3000/sensor", json=dic)
                print("Sent the sensor values")


            # print("dictionary="+str(dic))

            """
            key=str(temp_string[0][0]).split('{')
            key=str(key[1])

            val=str(temp_string[0][1]).split('}')
            val=str(val[0])
            dic={}
            key=key.split('u\'')
            val=val.split('u\'')
            key=str(key[1]).split('\'')
            val=str(val[1]).split('\'')
            dic[str(key[0])]=str(val[0])

            temp_string[1] = str(temp_string[1]).split(':')
            key = str(temp_string[1][0]).split('{')
            key = str(key[1])
            val = str(temp_string[1][1]).split('}')
            val = str(val[0])
            key = key.split('u\'')
            val = val.split('u\'')
            key = str(key[1]).split('\'')
            val = str(val[1]).split('\'')
            dic[str(key[0])] = str(val[0])

            json_object=json.dumps(dic)
            print("json="+str(json_object))
            """


        except Exception as e:
            print("Exception in payload" + str(e))

    subscriber.subscribe(subscription_path, callback=callback)

    # The subscriber is non-blocking, so we must keep the main thread from
    # exiting to allow it to process messages in the background.
    print('Listening for messages on {}'.format(subscription_path))
    while True:
        time.sleep(1)




if __name__ == '__main__':
    receive_messages('supermarketnavigation','testing')