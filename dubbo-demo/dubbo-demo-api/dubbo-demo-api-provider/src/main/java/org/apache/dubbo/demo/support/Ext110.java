package org.apache.dubbo.demo.support;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.demo.ExtService;

/**
 * Created by Administrator on 2019/7/9.
 */
public class Ext110 implements ExtService {

    @Override
    public String sayHi(URL url, String hi) {
        return hi+ ", Ext110";
    }

    @Override
    public String sayHi2(URL url, String hi) {
        return hi+ ", Ext110";
    }
}
