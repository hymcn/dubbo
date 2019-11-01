package org.apache.dubbo.demo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by Administrator on 2019/7/9.
 *
 *  ExtentionLoader dubbo实现的一个动态扩展加载的机制
 *  用于类的加载、适配和实例化
 *
 *  通过SPI标注的接口可以动态的完成加载和适配
 *  SPI标注的代表的一类实现特定功能的服务接口，服务接口可能会有多种实现方式，这就涉及到系统中使用哪一种实现方式。
 *
 *  1.实现SPI会指定默认的实现方式(getDefaultExtensionName)
 *  2.同时可以根据URL的参数动态的适配(getAdaptiveExtension),对应的注解@Adaptive
 */
public class ExtentionLoaderMain {

    private static final Logger log = LoggerFactory.getLogger(ExtentionLoaderMain.class);

    public static void main(String[] args) {
        URL url = URL.valueOf("test://abc.com/?ext=ext120");

        String defaultExtName = ExtensionLoader.getExtensionLoader(ExtService.class).getDefaultExtensionName();
        log.info(defaultExtName);
        ExtService ext1 = ExtensionLoader.getExtensionLoader(ExtService.class).getDefaultExtension();
        log.info(ext1.sayHi(url, "Hi"));
        log.info(ExtensionLoader.getExtensionLoader(ExtService.class).getLoadedExtensions().toString());
        ext1 = ExtensionLoader.getExtensionLoader(ExtService.class).getAdaptiveExtension();
        // Adaptive指定了ext
        log.info(ext1.sayHi(url, "Hi"));
        // Adaptive没有指定，没有指定按照getDefaultExtension执行
        log.info(ext1.sayHi2(url, "Hi"));
        Set<String> supportedExt = ExtensionLoader.getExtensionLoader(ExtService.class).getSupportedExtensions();
        log.info(supportedExt.toString());
    }
}
