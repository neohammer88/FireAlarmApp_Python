/* Title : FireNot App
 * Version : 1.2
 * Language : Python
 * Programmer : Tom Rho
 * Date : 09/12/2017
 */

from pyfcm import FCMNotification
from time import sleep
import time
import sys
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(23, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

def action(pin):
    print('Smoke detected!')
    fire()
    #return

GPIO.add_event_detect(23, GPIO.RISING)
GPIO.add_event_callback(23, action)

def fire():
    push_service = FCMNotification(api_key="AAAA8uMT9dc:APA91bEgcvmK51JLmx-Mc26Tbc7GxDGrZVHez8F18ItU-AVuFECJyZati38DOb9h5E5RI_PrlHVnoBXYaATJh7JCnVxh9Frv08Qx7IYHWt3NY8y3pt1RpGtJzokJz2lbDthqEQgCE2Ts")

    alert_title = "Fire alarm"
    alert_body = "There's fire at home."

    ##### Single device
    registration_id_api1 = "fhEx3ixrpO0:APA91bGNGahMovNuRxLUoT6tuhTasVqmVD10eBSfE8tVp3xo9dAWvdbrmxxwXsR1RGUPprCt1lJp6yTtAXBuffcGynIqDdvKnbGk0AomEgtzkviE3ia_rY7gwRnxM1Lfc2OCX6wFbGM5"
    registration_id_api2 = "cQ7cMW_W6bw:APA91bFi9XHS6zxYnSGz9sdjVewPJWq8WVTLq0FAS87OOZ4V7BDufmhoL6bl_mGro_Q2ilEBnpgg92tiZpYLIx6WNN0TRsfibHbqm9SIOlEGR6LqcCV5sGE-jWEqWo8mKRWoYxpBMYyW"

    registration_ids = [registration_id_api1, registration_id_api2]
#    result = push_service.notify_single_device(registration_id = registration_id_api1,
#                                               message_title = alert_title, message_body = alert_body)

    result = push_service.notify_multiple_devices(registration_ids = registration_ids,
                                               message_title = alert_title, message_body = alert_body)

    print result
try:
    print('Listening for signal')
    while True:
        #print('Listening for signal')
        time.sleep(0.5)
except KeyboardInterrupt:
    GPIO.cleanup()
    sys.exit()
