# Apache Dubbo NOTEBOOK

### Dubbo核心要义

- 1.服务扩展问题

一个服务的实现方式往往不止一种，对于中间件（框架，也可以这么称呼）应用而言，一般而言会兼顾多种实现方式，给最终用户
更多的选择权，同时也给用户扩展的机会。

如何才能保证系统无侵入的按照用户想要的方式方式切换或者扩展某一服务的具体实现方式。

SPI（Service Provider Interface）

ExtentionLoader dubbo实现的一个动态扩展加载的机制
用于类的加载、适配和实例化

通过SPI标注的接口可以动态的完成加载和适配
SPI标注的代表的一类实现特定功能的服务接口，服务接口可能会有多种实现方式，这就涉及到系统中使用哪一种实现方式。

1.实现SPI会指定默认的实现方式(getDefaultExtensionName)
2.同时可以根据URL的参数动态的适配(getAdaptiveExtension),对应的注解@Adaptive

ExtentionLoader机制在系统中多处使用，就Protocol，ProxyFactory做下简要说明
Protocol默认为dubbo协议，export 和 refer没有指定适配参数，默认dubbo

@SPI("dubbo")
public interface Protocol {
    @Adaptive
    <T> Exporter<T> export(Invoker<T> invoker) throws RpcException;
    @Adaptive
    <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException;
    void destroy();
}


ProxyFactory默认实现为javassist，具体方法通过{PROXY_KEY}也就是url中的proxy进行适配。
@SPI("javassist")
public interface ProxyFactory {
    @Adaptive({PROXY_KEY})
    <T> T getProxy(Invoker<T> invoker) throws RpcException;
    @Adaptive({PROXY_KEY})
    <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException;
    @Adaptive({PROXY_KEY})
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;
}


- 2.RPC

RPC(Remote Procedure Call)
本地对象调用远程对象的方法，RPC现实调用远端方法和调用本地方法一样的感觉。

dubbo-rpc模块为相关RPC实现

RPC涉及到两端，Provider远程服务的提供者，Consumer本地调用方。Consumer与Provider通过网络通信。
为了更好的理解RPC，首先回顾下本地方法调用(Local Procedure Call)

我们实现一个简单的服务，回声服务，将输入内容原样返回

interface IEchoService{
    Object echo(Object o);
}

class EchoService implements{
    Object echo(Object o){
        return o;
    }
}

正常的本地方法调用逻辑，实例化服务对象，调用对应的方法。
IEchoService echoService = new EchoService();
echoService.echo("hello world");

为什么需要远程调用？再对比两端逻辑的时候我忽然想到这样一个问题，没有RPC会有什么不好？什么情况下RPC

首先RPC是一种远端服务提供方式，RPC的实现是我们调用远端的服务成为可能，不再拘泥于HTTP接口、REST等方式。
同时RPC将调用过程进行拆解，使得在服务调用过程的添加一些额外的调用逻辑成为可能，由于调用过程通过
网络进行通信，构建分布式服务的时候也更加容易。再微服务大行其道的今天，RPC扮演着重要的角色。


RPC技术简单说就是为了解决远程调用服务的一种技术，使得调用者像调用本地服务一样方便透明。

![pIYBAFy1XvyAc-tKAAByuWV5HaI964](http://file.elecfans.com/web1/M00/8E/C7/pIYBAFy1XvyAc-tKAAByuWV5HaI964.png)

1）客户端client发起服务调用请求。
2）client stub 可以理解成一个代理，会将调用方法、参数按照一定格式进行封装，通过服务提供的地址，发起网络请求。
3）消息通过网络传输到服务端。
4）server stub接受来自socket的消息
5）server stub将消息进行解包、告诉服务端调用的哪个服务，参数是什么
6）结果返回给server stub
7）sever stub把结果进行打包交给socket
8）socket通过网络传输消息
9）client slub 从socket拿到消息
10）client stub解包消息将结果返回给client。
一个RPC框架就是把步骤2到9都封装起来。
[参考连接](http://www.elecfans.com/d/906796.html)


RPC的Echo服务实现

服务提供端包含服务接口和具体实现，同时将服务暴露给外界使用，需要提供暴露的服务列表，因为是远端提供服务，需要提供相关服务提供者的ip和暴露端口。

interface IEchoService{
    Object echo(Object o);
}

class EchoService implements{
    Object echo(Object o){
        return o;
    }
}

这里需要监听服务端口，处理网络请求，将相应的请求（IEchoService.echo）映射到相应的实现处理，将调用结果，写会。

调用端（消费端）仅包含相关服务接口，同时需要知道相关服务的暴露地址。

interface IEchoService{
    Object echo(Object o);
}

调用端没有服务的具体实现，通过创建相应的代理对象，利用反射将调用方法的参数通过网络传输到服务端，完成方法调用。
IEchoService stub = proxy(IEchoService)
stub.echo()

- 抽象（Invoker）

dubbo的实现了满眼都是Invoker，它是触发者，调用者

public interface Invoker<T> extends Node {

    /**
     * 调用哪个服务
     *
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * 执行调用 invocation中封装了方法和参数信息
     *
     * @param invocation
     * @return result
     * @throws RpcException
     */
    Result invoke(Invocation invocation) throws RpcException;

    /**
     * Node中属性，远程调用关联的URL，URL在dubbo中扮演着重要角色，每一个方法调用相关的信息都是通过泛化的URL来表示
     *
     * @return url.
     */
    URL getUrl();
}


所以接下来我们需要做的就是构建两端的invoker，

先从服务端说起
