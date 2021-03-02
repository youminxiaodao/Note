## BIO

每个线程对应一个连接

弊端：线程太多；调度，资源，是阻塞的



NIO

1个线程处理N和连接，以及读写

弊端：并发比较大的时候，每循环会有O(n)次系统调用，可能只有一个client发送了数据



多路复用器

实现：select，poll，epoll，kqueue

epoll是同步IO模型

select、poll的弊端：每次重复传递数据；每次调用要触发内核遍历





linux下可以使用strace -ff -o out java xxx来追踪系统调用

out是生成的文件名前缀

同理strace -ff -o out cmd适用于其它命令进行的系统调用.

