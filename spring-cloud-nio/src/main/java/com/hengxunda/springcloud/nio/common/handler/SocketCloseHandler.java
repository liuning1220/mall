package com.hengxunda.springcloud.nio.common.handler;

import com.hengxunda.springcloud.common.exception.ServiceException;
import com.hengxunda.springcloud.common.security.jwt.AccountJWT;
import com.hengxunda.springcloud.nio.common.dto.BaseMessage;
import com.hengxunda.springcloud.nio.common.enums.MsgNoEnum;
import com.hengxunda.springcloud.nio.common.netty.RoomChannelContainer;
import com.hengxunda.springcloud.nio.handlers.user.LoginHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
@SpringBootConfiguration
public class SocketCloseHandler implements Handlebars {

    public static final int CLOSE_MSG_NO = -100;

    @PostConstruct
    public void postConstruct() {
        this.init();
    }

    @Override
    public void init() {
        MapperHandler.addHandler(this);
    }

    @Override
    public int msgNo() {
        return CLOSE_MSG_NO;
    }

    @Override
    public void process(ChannelHandlerContext ctx, BaseMessage request) {
        close(ctx.channel());
    }

    private void close(Channel channel) {
        String roomId = LoginHandler.UserUtils.getRoomId(channel);
        if (Objects.isNull(roomId)) {
            return;
        }
        RoomChannelContainer.removeChannel(channel);
        sendRoomNotification(channel);
    }

    private void sendRoomNotification(Channel channel) {
        AccountJWT accountJWT = LoginHandler.UserUtils.getUser(channel);
        if (Objects.nonNull(accountJWT)) {
            if (RoomChannelContainer.isOtherChannelInRoom(channel)) {
                return;
            }
            BaseMessage message = BaseMessage.getNotification();
            message.setMsgNo(MsgNoEnum.User.USER_LEAVE_BROADCAST.getCode());
            message.setBody(new Object());
            try {
                RoomChannelContainer.getGroupByRoom(channel).writeAndFlush(message);
            } catch (ServiceException e) {
                log.error("发送房间内广播离场消息失败", e);
            }
        }
    }

}
