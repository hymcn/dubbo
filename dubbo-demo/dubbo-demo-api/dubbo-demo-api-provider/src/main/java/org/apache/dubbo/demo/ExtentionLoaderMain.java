package org.apache.dubbo.demo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by Administrator on 2019/7/9.
 */
public class ExtentionLoaderMain {

    private static final Logger log = LoggerFactory.getLogger(ExtentionLoaderMain.class);

    public static void main(String[] args) {
//        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
//        System.out.println(protocol.getClass());
//        System.out.println(ExtensionLoader.getExtensionLoader(Protocol.class).getDefaultExtensionName());


//        Ext1 ext = ExtensionLoader.getExtensionLoader(Ext1.class).getAdaptiveExtension();
//        log.info(ext.sayHi());
        URL url = URL.valueOf("test://abc.com/?ext=ext120");
        try {
            Class extClass = Class.forName("org.apache.dubbo.demo.support.Ext110");
            log.info(extClass.getSimpleName());
            ExtService ext1 = (ExtService) extClass.newInstance();
            String hi = ext1.sayHi(url, "Hi");
            log.info(hi);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        String defaultExtName = ExtensionLoader.getExtensionLoader(ExtService.class).getDefaultExtensionName();
        log.info(defaultExtName);
        ExtService ext1 = ExtensionLoader.getExtensionLoader(ExtService.class).getDefaultExtension();
        log.info(ext1.sayHi(url, "Hi"));

        log.info(ExtensionLoader.getExtensionLoader(ExtService.class).getLoadedExtensions().toString());
        ext1 = ExtensionLoader.getExtensionLoader(ExtService.class).getAdaptiveExtension();
        log.info(ext1.sayHi(url, "Hi"));

        Set<String> supportedExt = ExtensionLoader.getExtensionLoader(ExtService.class).getSupportedExtensions();
        log.info(supportedExt.toString());
    }
}
