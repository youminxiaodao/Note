## ThreadPoolExecutor的submit方法和execute方法区别



## Java创建对象的方式

new、反射、工厂模式



## 反射创建对象的方式

Class.forName("类名");

类名.class;

对象名.getClass();



## 3种让线程等待和唤醒的方法

1.使用Object中的wait()方法让线程等待，使用Object中的notify()方法唤醒线程;

2.使用JUC包中的Condition的await()方法让线程等待，使用signal()方法唤醒线程;

3.LockSupport类可以阻塞当前线程以及唤醒指定被阻塞的线程.

## Object类中的wait和notify方法实现线程等待和唤醒

wait和notify不能在没有synchronized的代码块中调用.两个代码块都会报错:IlleagalMonitorStateException

先执行notify，再执行wait，现象？notify代码块正常，但是wait会卡主

### Condition接口中的await和signal方法实现线程的等待和唤醒

await和signal不能在没有lock.lock()的代码块中调用.两个代码块都会报错:IlleagalMonitorStateException

先执行await，再执行signal，现象？signal代码块正常，但是await会卡主

## 传统的synchronized和Lock实现等待唤醒通知的约束

1.线程先要获得并持有锁，必须在锁块(synchronized或lock)中

2.必须要先等待后唤醒，线程才能被唤醒

## LockSupport类中的park等待和unpark唤醒

LockSupport是用来创建锁和其它同步类的基本线程阻塞原语。LockSupport是一个线程阻塞工具类，所有的方法都是静态方法，可以让线程在任意位置阻塞，阻塞之后也有对应的唤醒方法。归根结底，LockSupport调用的是Unsafe中的native方法。LockSupport类使用了一种名为Permit(许可证)的概念来做到阻塞和唤醒线程的功能，每个线程都有一个许可(permit)。permit只有两个值1和0，默认是0。可以把许可看成是一种(0,1)信号量(Semaphore)，但是与Semaphore不同的是，许可的累加上限是1.

LockSupport提供park()和unpark()系列方法实现阻塞线程和解除线程阻塞的过程。LockSupport和每个使用它的线程都有一个许可，permit相当于1,0的开关，默认是0，调用一次unpark就加1变成1，调用一次park会消费permit，也就是将1变成0，同时park立即返回。如再次调用park会变成阻塞(因为permit为0了会阻塞在这里，一直到permit变为1)，这时调用unpark会把permit置为1.每个线程都都一个相关的permit，permit最多只有一个，重复调用unpark也不会累积凭证。

当调用park方法时，如果有凭证，则会直接消耗掉这个凭证然后正常退出；如果无凭证，就必须阻塞等待凭证可用；

而unpark则相反，它会增加一个凭证，但凭证最多只能有1个，累加无效.

像Synchronized、Lock出现的必须在锁代码块中、且唤醒必须在阻塞后调用的限制，在LockSupport中不存在.

## LockSupport为什么可以先唤醒线程后阻塞线程

因为unpark获得了一个凭证，之后再调用park方法，就可以名正言顺的凭证消费，故不会阻塞。

## LockSupport为什么唤醒两次后阻塞两次，但最终结果还会阻塞线程？

因为凭证的数量最多为1，连续调用两次unpark和调用一次unpark效果一样，只会增加一个凭证；而调用两次park却需要消费两个凭证，证不够，不能放行.

## 什么是伪共享

## 什么是内存屏障

## 如何打破双亲委派机制

重写loadClass方法可以打破双亲委派机制

## 问题

### 缓存一致性协议失效

1.如果一个变量存储长度大于一个缓存行就会导致缓存行失效，可以使用总线锁

2.CPU并不支持缓存一致性协议

### 什么情况下会产生总线风暴？

大量volatile+CAS

### Synchronized在一个方法加锁，另一个方法解锁，如何实现(跨方法)

通过Unsafe类的monitorEnter和monitorExit

![image-20210323204657808](C:\Develop\Git\Note\images\image-20210323204657808.png)

### 如何手动加内存屏障？

通过Unsafe类下的系列方法

![image-20210323204814971](C:\Develop\Git\Note\images\image-20210323204814971.png)



### 为什么通过对象的getClass方法可以获取到Class?

对象头中存储着MetaData元数据指针

### 锁的粗化

### 锁的消除

### 锁的升级

### 锁为什么不能降级

### 自适应自旋

### 设置自旋次数

### 如果Node节点在条件队列中，那锁是共享模式还是独占模式？

独占模式

## Atomic系列源码

AtomicBoolean:将bool值转换成int，再调用unsafe.compareAndSwapInt

AtomicInteger:调用unsafe.getAndAddInt及unsafe.getAndSetInt(只在AtomicInterger.getAndSet中调用)

​	JDK8新增

```
getAndUpdate(IntUnaryOperator)
updateAndGet(IntUnaryOperator updateFunction)
getAndAccumulate(int x, IntBinaryOperator accumulatorFunction)
accumulateAndGet(int x,IntBinaryOperator accumulatorFunction)
```

AtomicIntegerArray:

AtomicIntegerFieldUpdater

AtomicLong:VM_SUPPORTS_LONG_CAS

AtomicMarkableReference与AtomicStampedReference在源码上的区别，只是第二个参数的区别，即bool和int的区别，其余无差

AtomicReference

AtomicReferenceArray

AtomicReferenceFieldUpdater

DoubleAccumulator





## Lock系列源码

## AQS源码

## Tomcat源码

## Netty源码

## BlockingQueue、Queue系列源码

Condition只能在独占模式下使用

## JDK7 HashMap引起的死锁

## JDK7和JDK8中Hashmap的区别

## JDK7和JDK8中ConcurrentHashMap的区别



## 分段锁

## filter、拦截器、AOP的区别

## Springboot启动jar的原理、springboot启动war的原理

1.ioc容器带动了内嵌tomcat的启动

2.外置的tomcat带动了我们的ioc容器启动





## Java中反射的种类

https://blog.csdn.net/guorui_java/article/details/114433677



## class.newInstance和constructor.newInstance的区别

私有构造器只能使用constructor.newInstance进行创建对象

