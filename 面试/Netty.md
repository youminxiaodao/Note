## 如何防止Netty服务端意外退出

有以下两种方式:

1.程序监听NioSocketChannel的关闭事件，并同步阻塞调用方法，如下代码:

f.channel().closeFuture().sync();

程序会处于阻塞状态，后续的shutdownGraceFully方法不会被执行，程序不再退出.当前方法被阻塞在CloseFuture中，等待Channel关闭.

2.添加监听，在链路关闭时再释放线程池和连接句柄，如下代码:

```java
public static void main(String[] args) throws InterruptedException {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                    }
                });
        ChannelFuture future = b.bind(18080).sync();
        future.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                System.out.println(channelFuture.channel().toString() + " 链路关闭!");
            }
        });

    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        /*bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();*/
    }
}
```