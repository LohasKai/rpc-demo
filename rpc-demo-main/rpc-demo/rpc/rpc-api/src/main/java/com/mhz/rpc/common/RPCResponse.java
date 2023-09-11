package com.mhz.rpc.common;

import lombok.Data;

@Data
public class RPCResponse {

    /**
     * 相应Id
     */
    private String requestId;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 返回的结果
     */
    private Object result;
}
