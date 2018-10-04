import os
import time
import re
from slackclient import SlackClient


# instantiate Slack client
slack_client = SlackClient('xoxb-329154375397-449717336870-ba49ankfQ51O5knv3gcsOTLn')
# starterbot's user ID in Slack: value is assigned after the bot starts up

starterbot_id = None



k = []
def parse_bot_commands(slack_events):
    global k
    for event in slack_events:
        if event['type'] == 'message':
            k = k.append(event['text'])
            print(event['text'])
    #return k

if slack_client.rtm_connect():
    while slack_client.server.connected is True:
       parse_bot_commands(slack_client.rtm_read())
       time.sleep(1)
    else:
        print("Connection Failed")
