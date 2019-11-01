package org.apache.dubbo.demo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * Created by Administrator on 2019/7/9.
 */
@SPI("ext110")
public interface ExtService {

    @Adaptive("ext")
    String sayHi(URL url, String hi);

    @Adaptive
    String sayHi2(URL url, String hi);
}
