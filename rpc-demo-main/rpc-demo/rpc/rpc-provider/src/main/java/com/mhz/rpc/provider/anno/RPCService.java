package com.mhz.rpc.provider.anno;

import java.lang.annotation.*;

/**
 * 对外暴露接口
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCService {
}
