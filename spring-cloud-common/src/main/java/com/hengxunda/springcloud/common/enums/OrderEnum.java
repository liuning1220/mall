package com.hengxunda.springcloud.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

public interface OrderEnum {

    @Getter
    @AllArgsConstructor
    enum Status {

        CANCEL(0, "已取消"),

        NOT_PAY(1, "未支付"),

        PAY_SUCCESS(2, "支付成功"),

        PAY_FAIL(3, "支付失败");

        private final int code;

        private final String msg;

        public static Status acquireByCode(final int code) {
            return Arrays.stream(values()).filter(v -> v.code == code).findFirst().orElse(null);
        }

        public int code() {
            return code;
        }

        public String msg() {
            return msg;
        }
    }

}
