/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.message.utils;

import com.creditcloud.message.entity.Message;
import com.creditcloud.message.entity.MessageBody;

/**
 *
 * @author rooseek
 */
public class DTOUtils {

    /**
     * handle MessageBody
     *
     * @param body
     * @return
     */
    public static com.creditcloud.model.MessageBody getMessageBody(MessageBody body) {
        com.creditcloud.model.MessageBody result = null;
        if (body != null) {
            result = new com.creditcloud.model.MessageBody(body.getTitle(),
                                                           body.getContent(),
                                                           body.getSentTime(),
                                                           body.getRealm(),
                                                           body.getType());
        }

        return result;
    }

    public static MessageBody convertMessageBody(com.creditcloud.model.MessageBody body) {
        MessageBody result = null;
        if (body != null) {
            result = new MessageBody(body.getTitle(),
                                     body.getContent(),
                                     body.getSentTime(),
                                     body.getRealm(),
                                     body.getType());
        }

        return result;
    }

    /**
     * handle Message
     *
     * @param message
     * @return
     */
    public static com.creditcloud.model.Message getMessage(Message message) {
        com.creditcloud.model.Message result = null;
        if (message != null) {
            result = new com.creditcloud.model.Message(message.getId(),
                                                       getMessageBody(message.getBody()),
                                                       message.getSender(),
                                                       message.getReceiver(),
                                                       message.getStatus());
        }
        return result;
    }

    public static Message convertMessage(com.creditcloud.model.Message message) {
        Message result = null;
        if (message != null) {
            result = new Message(convertMessageBody(message.getBody()),
                                 message.getSender(),
                                 message.getReceiver(),
                                 message.getStatus());
            result.setId((message.getId()));
        }
        return result;
    }
}
