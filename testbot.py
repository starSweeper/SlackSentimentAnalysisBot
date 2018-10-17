import os
import time
import re
import cv2
import random
import numpy as np
from slackclient import SlackClient
#from sklearn import svm


# instantiate Slack client
slack_key = ""  # removed for GitHub. Use a legacy token from https://api.slack.com/custom-integrations/legacy-tokens
slack_client = SlackClient(slack_key)

word_data = []
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
            if "thread_ts" not in event:
                print(event)
                pulled_messages.append("\n" + event['text'])
                print_to_message_file(event['channel'],re.sub("\n"," ",event['text']) + "\n")
                print(event['text'])
                bot_spy(event)


def bot_spy(event):
    channel = event['channel']
    timestamp = event['ts']

    if str("bot").lower() in event['text']:
        reply_to_message("My ears are burning",timestamp,channel)
        react_to_message("ear", channel, timestamp)
        react_to_message("fire", channel, timestamp)
    if str("amanda").lower() in event['text']:
        react_to_message("princess:skin-tone-5",channel,timestamp)
    #if "":
    #    reply_and_react("","",channel,timestamp)
    #if:
    #    reply_and_react("", "", channel, timestamp)
    #if:
    #    reply_and_react("", "", channel, timestamp)


def print_to_message_file(channel_name, message):
    if os.path.exists(channel_name + "messages.txt"):
        text_file = open(channel_name + "messages.txt", "a")
    else:
        text_file = open(channel_name + "messages.txt", "w")
    text_file.write(message)
    text_file.close()


def get_message_points(message_file, word_file):
    message_data = []
    global word_data
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
        message_data.append(get_message_work_related_count(slack_message))

    return message_data


def get_message_work_related_count(message):
    wr_points = 0
    swr_points = 0
    nwr_points = 0
    m_list = re.sub(r'[^A-Za-z0-9 ]+', "", message).split(' ')
    for m_word in m_list:
        for search_word in word_data:
            if search_word.word_string == m_word:
                wr_points += search_word.work_related_points
                swr_points += search_word.semi_work_related_points
                nwr_points += search_word.non_work_related_points

    return Messages(m_list, wr_points, swr_points, nwr_points)


def get_labels(label_file, message_list):
    labels = tuple(open(label_file, 'r'))
    pulled_labels = []

    for label, message in zip(labels, message_list):
        message.set_label(int(label))
        pulled_labels.append(int(label))

    return pulled_labels


def react_to_message(emoji, channel, timestamp):
    slack_client.api_call("reactions.add", name=emoji, channel=channel, timestamp=timestamp)


def reply_to_message(message, timestamp, channel):
    slack_client.api_call(
        "chat.postMessage",
        channel=channel,
        text=message,
        username='SSA Bot',
        thread_ts=timestamp,
        icon_emoji=':robot_face:'
    )

    slack_client.api_call("reactions.add", name="robot_face", channel=channel, timestamp=timestamp)


def reply_and_react(message, emoji, channel, timestamp):
    reply_to_message(message, timestamp, channel)
    react_to_message(emoji, channel, timestamp)


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

    return svm_data


def normalize(value):
    #return int(((value - 0) / (100 - 0)) * (1 - (-1)) + (-1)) #try swapping the commented lines....
    return int(value)


def listen(channel):
    if slack_client.rtm_connect():
        slack_client.api_call("channel.mark", channel=channel)
        while slack_client.server.connected is True:
            parse_bot_commands(slack_client.rtm_read())
            time.sleep(1)
    else:
        print("Connection Failed")


def get_conversation_history(channel):
    messages = slack_client.api_call("conversations.history", channel=channel)
    for msg in messages['messages']:
        print_to_message_file(channel, re.sub("\n", " ", msg['text']) + "\n")


def train_svm(training_data, labels, file_name='svm_data.dat'):
    svm = cv2.ml.SVM_create()

    svm.setKernel(cv2.ml.SVM_RBF)
    svm.setType(cv2.ml.SVM_C_SVC)
    svm.setC(10)
    svm.setGamma(15)

    svm.train(np.array(training_data, np.float32), cv2.ml.ROW_SAMPLE, np.array(labels, np.int32))
    svm.save(file_name)
    print("Training complete.\n")


def classify_message():
    svm = cv2.ml.SVM_load('svm_data.dat')


def test_svm(testing_data, labels, file_name='svm_data.dat'):
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
#listen("")
# prepare_training_data("messages.txt", "messageData.txt")
#get_conversation_history("")


#SVM
m_list = get_message_points("messages.txt", "messageData.txt")

l_list = get_labels("labels.txt", m_list)
svm_tt_data = prepare_svm_data(m_list)

random.shuffle(svm_tt_data)
random.shuffle(l_list)

train_data = svm_tt_data[:80]
test_data = svm_tt_data[20:]


train_svm(train_data, l_list[:80])
print("SVM was " + str(test_svm(test_data, l_list[20:])) + "% accurate")
