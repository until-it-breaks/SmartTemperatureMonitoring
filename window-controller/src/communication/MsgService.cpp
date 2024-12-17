#include "MsgService.h"

String content;
MsgServiceClass MsgService;

bool MsgServiceClass::isMsgAvailable() {
    return this->msgAvailable;
}

Msg* MsgServiceClass::receiveMsg() {
    if (this->msgAvailable) {
        Msg* msg = this->currentMsg;
        this->msgAvailable = false;
        this->currentMsg = nullptr;
        content = "";
        return msg;
    } else {
        return nullptr;
    }
}

void MsgServiceClass::init() {
    Serial.begin(9600);
    content.reserve(256);
    content = "";
    this->currentMsg = nullptr;
    this->msgAvailable = false;
}

void MsgServiceClass::sendMsg(const String& msg) {
    Serial.println(msg);
}

/* A function that is periodically called each iteration of the super loop */
void serialEvent() {
    while (Serial.available()) {
        char ch = (char) Serial.read();
        if (ch == '\n') {
            MsgService.currentMsg = new Msg(content);
            MsgService.msgAvailable = true;
        } else {
            content += ch;
        }
    }
}