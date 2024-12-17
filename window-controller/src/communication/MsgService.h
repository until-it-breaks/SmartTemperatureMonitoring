#ifndef __MSG_SERVICE__
#define __MSG_SERVICE__

#include <Arduino.h>

class Msg {
    public:
        Msg(String content) {
            this->content = content;
        }

        String getContent() {
            return this->content;
        }
    private:
        String content;
};

class MsgServiceClass {
    public:
        void init();
        bool isMsgAvailable();
        Msg* receiveMsg();
        void sendMsg(const String& msg);
        Msg* currentMsg;
        bool msgAvailable;
};

extern MsgServiceClass MsgService;

#endif