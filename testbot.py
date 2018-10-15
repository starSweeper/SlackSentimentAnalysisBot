import os
import time
import re
from slackclient import SlackClient
#from sklearn import svm


# instantiate Slack client
slack_key = "" #removed for GitHub. Use a legacy token from https://api.slack.com/custom-integrations/legacy-tokens
slack_client = SlackClient(slack_key)

starterbot_id = None #do we need this?

pulled_messages = []
word_data = []
message_data = []


class Messages:
    def __init__(self, m, wr, swr, nwr):
        self.message_words = m
        self.work_related_points = wr
        self.semi_work_related_points = swr
        self.non_work_related_points = nwr


class Words:
    def __init__(self, w, wr, swr, nwr):
        self.word_string = w
        self.work_related_points = wr
        self.semi_work_related_points = swr
        self.non_work_related_points = nwr


def parse_bot_commands(slack_events):
    global pulled_messages
    for event in slack_events:
        if event['type'] == 'message':
            pulled_messages.append("\n" + event['text'])
            print_to_message_file(event['channel'],re.sub("\n"," ",event['text']) + "\n")
            print(event['text'])

            #For demo purposes only ;P
            slack_client.api_call("reactions.add", name="d", channel=event['channel'], timestamp = event['ts'])
            slack_client.api_call("reactions.add", name="a", channel=event['channel'], timestamp=event['ts'])
            slack_client.api_call("reactions.add", name="b", channel=event['channel'], timestamp=event['ts'])
    #return pulled_messages


def print_to_message_file(channel_name, message):
    if os.path.exists(channel_name + "messages.txt"):
        text_file = open(channel_name + "messages.txt", "a")
    else:
        text_file = open(channel_name + "messages.txt", "w")
    text_file.write(message)
    text_file.close()


def prepare_training_data(messageFile, wordFile):
    global message_data
    global word_data
    word_lines = tuple(open(wordFile, 'r'))
    message_lines = tuple(open(messageFile, 'r'))

    for each_word in word_lines:
        line_data = each_word.split(' ')

        word_wr_points = int(line_data[1])
        word_swr_points = int(line_data[2])
        word_nwr_points = int(line_data[3])

        word_data.append(Words(line_data[0], word_wr_points, word_swr_points, word_nwr_points))

    for slack_message in message_lines:
        wr_points = 0
        swr_points = 0
        nwr_points = 0
        m_list = re.sub(r'[^A-Za-z0-9 ]+', "", slack_message).split(' ')
        for m_word in m_list:
            print(m_word)
            for search_word in word_data:
                if search_word.word_string == m_word:
                    wr_points += search_word.work_related_points
                    swr_points += search_word.semi_work_related_points
                    nwr_points += search_word.non_work_related_points

        message_data.append(Messages(m_list, wr_points, swr_points, nwr_points))


def send_a_message(message, channel):
    message_info = slack_client.api_call(
        "chat.postMessage",
        channel=channel,
        text=message,
        username='SSA Bot',
        icon_emoji=':robot_face:'
    )

    slack_client.api_call("reactions.add", name="robot_face", channel=channel, timestamp=message_info['ts'])


def listen(channel):
    if slack_client.rtm_connect():
        slack_client.api_call("channel.mark", channel=channel)
        while slack_client.server.connected is True:
            parse_bot_commands(slack_client.rtm_read())
            time.sleep(1)
    else:
        print("Connection Failed")


#testing, enter a channel ID between the quotes (find in URL)
#send_a_message("Hello, world", "")
#listen("")
#prepare_training_data("messages.txt", "messageData.txt")
