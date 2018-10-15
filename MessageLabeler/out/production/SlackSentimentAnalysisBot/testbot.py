import os
import time
import re
import cv2
import numpy as np
from slackclient import SlackClient
#from sklearn import svm


# instantiate Slack client
slack_key = ""  # removed for GitHub. Use a legacy token from https://api.slack.com/custom-integrations/legacy-tokens
slack_client = SlackClient(slack_key)

starterbot_id = None  # do we need this?

pulled_messages = []


class Messages:
    label = -1

    def __init__(self, m, wr, swr, nwr):
        self.message_words = m
        self.work_related_points = wr
        self.semi_work_related_points = swr
        self.non_work_related_points = nwr


    def set_label(self, new_label):
        label = new_label


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

            # For demo purposes only ;P
            slack_client.api_call("reactions.add", name="d", channel=event['channel'], timestamp=event['ts'])
            slack_client.api_call("reactions.add", name="a", channel=event['channel'], timestamp=event['ts'])
            slack_client.api_call("reactions.add", name="b", channel=event['channel'], timestamp=event['ts'])


def print_to_message_file(channel_name, message):
    if os.path.exists(channel_name + "messages.txt"):
        text_file = open(channel_name + "messages.txt", "a")
    else:
        text_file = open(channel_name + "messages.txt", "w")
    text_file.write(message)
    text_file.close()


def get_message_points(message_file, word_file):
    message_data = []
    word_data = []
    word_lines = tuple(open(word_file, 'r'))
    message_lines = tuple(open(message_file, 'r'))

    # Go through each word in word file and keep track of how many points each word is worth in each catagory
    for each_word in word_lines:
        line_data = each_word.split(' ')

        word_wr_points = int(line_data[1])
        word_swr_points = int(line_data[2])
        word_nwr_points = int(line_data[3])

        word_data.append(Words(line_data[0], word_wr_points, word_swr_points, word_nwr_points))

    # Go through each message in message file and keep track of how many points it earned based off words in the message
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

    return message_data


def get_labels(label_file, message_list):
    labels = tuple(open(label_file, 'r'))

    for label, message in labels, message_list:
        message.setLabel(label)


def send_a_message(message, channel):
    message_info = slack_client.api_call(
        "chat.postMessage",
        channel=channel,
        text=message,
        username='SSA Bot',
        icon_emoji=':robot_face:'
    )

    slack_client.api_call("reactions.add", name="robot_face", channel=channel, timestamp=message_info['ts'])


def prepare_svm_data(message_list):
    svm_data = []
    for msg in message_list:
        svm_data.append([normalize(msg.work_related_points), normalize(msg.semi_work_related_points), normalize(msg.non_work_related_points)])


def normalize(value):
    return ((value - 0) / (100 - 0)) * (1 - (-1)) + (-1)

def listen(channel):
    if slack_client.rtm_connect():
        slack_client.api_call("channel.mark", channel=channel)
        while slack_client.server.connected is True:
            parse_bot_commands(slack_client.rtm_read())
            time.sleep(1)
    else:
        print("Connection Failed")


def train_svm(training_data, labels, file_name='svm_data.dat'):
    svm = cv2.ml.SVM_create()

    svm.setKernel(cv2.ml.SVM_RBF)
    svm.setType(cv2.ml.SVM_C_SVC)
    svm.setC(10)
    svm.setGamma(115)

    svm.train(np.array(training_data, np.float32), cv2.ml.ROW_SAMPLE, np.array(labels, np.int32))
    svm.save(file_name)
    print("Training complete.\n")


def test_svm(file_name, testing_data, labels,):
    svm = cv2.ml.SVM_load(file_name)
    result = []
    for i in testing_data:
        temp_list = [i]
        result.append(int(svm.predict(np.asarray(temp_list, np.float32))[1]))

    mask = []
    print("Labels:  " + str(labels))
    print("Results: " + str(result))

    for i in range(len(labels)):
        if labels[i] != result[i]:
            mask.append(0)
        else:
            mask.append(1)

    print("Mask:    " + str(mask))
    correct = np.count_nonzero(mask)
    return correct * 100.0 / len(result)

# testing, enter a channel ID between the quotes (find in URL)
# send_a_message("I'm so excited to start judging you and your conversations!!", "")
# listen("")
# prepare_training_data("messages.txt", "messageData.txt")
