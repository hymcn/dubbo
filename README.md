# Apache Dubbo NOTEBOOK

### Dubbo核心要义

- 1.服务扩展问题

一个服务的实现方式往往不止一种，对于中间件（框架，也可以这么称呼）应用而言，一般而言会兼顾多种实现方式，给最终用户
更多的选择权，同时也给用户扩展的机会。

如何才能保证系统无侵入的按照用户想要的方式方式切换或者扩展某一服务的具体实现方式。

SPI（Service Provider Interface）


- 2. RPC

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

参考连接：http://www.elecfans.com/d/906796.html