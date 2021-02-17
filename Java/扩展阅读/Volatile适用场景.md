把代码块声明为 synchronized，有两个重要后果，通常是指该代码具有 **原子性（atomicity）**和 **可见性（visibility）**。

- **原子性**意味着个时刻，只有一个线程能够执行一段代码，这段代码通过一个monitor object保护。从而防止多个线程在更新共享状态时相互冲突。
- **可见性**则更为微妙，它必须确保释放锁之前对共享数据做出的更改对于随后获得该锁的另一个线程是可见的。 —— 如果没有同步机制提供的这种可见性保证，线程看到的共享变量可能是修改前的值或不一致的值，这将引发许多严重问题。



## volatile的使用条件 



Volatile 变量具有 `synchronized` 的可见性特性，但是**不具备原子性**。这就是说线程能够自动发现 volatile 变量的最新值。

Volatile 变量可用于提供线程安全，但是只能应用于非常有限的一组用例：多个变量之间或者某个变量的当前值与修改后值之间没有约束。因此，单独使用 volatile 还不足以实现计数器、互斥锁或任何具有与多个变量相关的不变式（Invariants）的类（例如 “start <=end”）。



出于简易性或可伸缩性的考虑，您可能倾向于使用 volatile 变量而不是锁。当使用 volatile 变量而非锁时，某些习惯用法（idiom）更加易于编码和阅读。此外，volatile 变量不会像锁那样造成线程阻塞，因此也很少造成可伸缩性问题。在某些情况下，如果读操作远远大于写操作，volatile 变量还可以提供优于锁的**性能**优势。



### **使用条件** 



您只能在有限的一些情形下使用 volatile 变量替代锁。要使 volatile 变量提供理想的线程安全，必须同时满足下面两个条件：

- 对变量的写操作不依赖于当前值。
- 该变量没有包含在具有其他变量的不变式中。

实际上，这些条件表明，可以被写入 volatile 变量的这些有效值**独立于任何程序的状态**，包括变量的当前状态。

第一个条件的限制使 volatile 变量**不能用作线程安全计数器**。虽然增量操作（`x++`）看上去类似一个单独操作，实际上它是一个由（读取－修改－写入）操作序列组成的组合操作，必须以原子方式执行，而 volatile 不能提供必须的原子特性。实现正确的操作需要使`x` 的值在操作期间保持不变，而 volatile 变量无法实现这点。（然而，如果只从单个线程写入，那么可以忽略第一个条件。）



### 反例 

大多数编程情形都会与这两个条件的其中之一冲突，使得 volatile 变量不能像 `synchronized` 那样普遍适用于实现线程安全。

【反例：volatile变量不能用于约束条件中】 下面是一个非线程安全的数值范围类。它包含了一个不变式 —— 下界总是小于或等于上界。

```java
@NotThreadSafe 



public class NumberRange {



    private int lower, upper;



 



    public int getLower() { return lower; }



    public int getUpper() { return upper; }



 



    public void setLower(int value) { 



        if (value > upper) 



            throw new IllegalArgumentException(...);



        lower = value;



    }



 



    public void setUpper(int value) { 



        if (value < lower) 



            throw new IllegalArgumentException(...);



        upper = value;



    }



}
```



将 `lower` 和 upper 字段定义为 volatile 类型不能够充分实现类的线程安全；而仍然需要使用同步——使 `setLower()` 和 `setUpper()` 操作原子化。

**否则，如果凑巧两个线程在同一时间使用不一致的值执行 \**\*\*`setLower`\*\* 和 `setUpper` 的话，则会使范围处于不一致的状态。\****例如，如果初始状态是`(0, 5)`，同一时间内，线程 A 调用`setLower(4)` 并且线程 B 调用`setUpper(3)`，显然这两个操作交叉存入的值是不符合条件的，那么两个线程都会通过用于保护不变式的检查，使得最后的范围值是`(4, 3)` —— 一个无效值。



## volatile的适用场景

### 模式 #1：状态标志 

也许实现 volatile 变量的规范使用仅仅是使用一个布尔状态标志，用于指示发生了一个重要的一次性事件，例如完成初始化或请求停机。



```java
volatile boolean shutdownRequested;



 



...



 



public void shutdown() { 



    shutdownRequested = true; 



}



 



public void doWork() { 



    while (!shutdownRequested) { 



        // do stuff



    }



}
```





线程1执行doWork()的过程中，可能有另外的线程2调用了shutdown，所以boolean变量必须是volatile。

而如果使用 `synchronized` 块编写循环要比使用 volatile 状态标志编写麻烦很多。由于 volatile 简化了编码，并且状态标志并不依赖于程序内任何其他状态，因此此处非常适合使用 volatile。



这种类型的状态标记的一个公共特性是：**通常只有一种状态转换**；`shutdownRequested` 标志从`false` 转换为`true`，然后程序停止。这种模式可以扩展到来回转换的状态标志，但是只有在转换周期不被察觉的情况下才能扩展（从`false` 到`true`，再转换到`false`）。此外，还需要某些原子状态转换机制，例如原子变量。



### 模式 #2：一次性安全发布（one-time safe publication） 

在缺乏同步的情况下，可能会遇到某个对象引用的更新值（由另一个线程写入）和该对象状态的旧值同时存在。

这就是造成著名的双重检查锁定（double-checked-locking）问题的根源，其中对象引用在没有同步的情况下进行读操作，产生的问题是您可能会看到一个更新的引用，但是仍然会通过该引用看到不完全构造的对象。参见：[【设计模式】5. 单例模式（以及多线程、无序写入、volatile对单例的影响）](http://http//blog.csdn.net/vking_wang/article/details/8708520#t11)





```java
//注意volatile！！！！！！！！！！！！！！！！！  



private volatile static Singleton instace;   



  



public static Singleton getInstance(){   



    //第一次null检查     



    if(instance == null){            



        synchronized(Singleton.class) {    //1     



            //第二次null检查       



            if(instance == null){          //2  



                instance = new Singleton();//3  



            }  



        }           



    }  



    return instance;        
```



如果不用volatile，则因为内存模型允许所谓的“无序写入”，可能导致失败。***\*——某个线程可能会获得一个未完全初始化的实例。\****

考察上述代码中的 //3 行。此行代码创建了一个 Singleton 对象并初始化变量 instance 来引用此对象。这行代码的问题是：**在Singleton 构造函数体执行之前，变量instance 可能成为非 null 的！**
什么？这一说法可能让您始料未及，但事实确实如此。

在解释这个现象如何发生前，请先暂时接受这一事实，我们先来考察一下双重检查锁定是如何被破坏的。假设上述代码执行以下事件序列：

1.   线程 1 进入 getInstance() 方法。
2.   由于 instance 为 null，线程 1 在 //1 处进入synchronized 块。
3.   线程 1 前进到 //3 处，但在构造函数执行之前，使实例成为非null。
4.   线程 1 被线程 2 预占。
5.   线程 2 检查实例是否为 null。因为实例不为 null，线程 2 将instance 引用返回，返回一个构造完整但**部分初始化**了的Singleton 对象。
6.   线程 2 被线程 1 预占。
7.   线程 1 通过运行 Singleton 对象的构造函数并将引用返回给它，来完成对该对象的初始化。



### 模式 #3：独立观察（independent observation） 

安全使用 volatile 的另一种简单模式是：定期 “发布” 观察结果供程序内部使用。【例如】假设有一种环境传感器能够感觉环境温度。一个后台线程可能会每隔几秒读取一次该传感器，并更新包含当前文档的 volatile 变量。然后，其他线程可以读取这个变量，从而随时能够看到最新的温度值。

使用该模式的另一种应用程序就是收集程序的统计信息。【例】如下代码展示了身份验证机制如何记忆最近一次登录的用户的名字。将反复使用`lastUser` 引用来发布值，以供程序的其他部分使用。



```java
public class UserManager {



    public volatile String lastUser; //发布的信息



 



    public boolean authenticate(String user, String password) {



        boolean valid = passwordIsValid(user, password);



        if (valid) {



            User u = new User();



            activeUsers.add(u);



            lastUser = user;



        }



        return valid;



    }



} 
```





### 模式 #4：“volatile bean” 模式



volatile bean 模式的基本原理是：很多框架为易变数据的持有者（例如 `HttpSession`）提供了容器，但是放入这些容器中的对象必须是线程安全的。

在 volatile bean 模式中，JavaBean 的所有数据成员都是 volatile 类型的，并且 getter 和 setter 方法必须非常普通——即不包含约束！

```java
@ThreadSafe



public class Person {



    private volatile String firstName;



    private volatile String lastName;



    private volatile int age;



 



    public String getFirstName() { return firstName; }



    public String getLastName() { return lastName; }



    public int getAge() { return age; }



 



    public void setFirstName(String firstName) { 



        this.firstName = firstName;



    }



 



    public void setLastName(String lastName) { 



        this.lastName = lastName;



    }



 



    public void setAge(int age) { 



        this.age = age;



    }



}
```

### 模式 #5：开销较低的“读－写锁”策略

如果读操作远远超过写操作，您可以结合使用**内部锁**和 **volatile 变量**来减少公共代码路径的开销。

如下显示的线程安全的计数器，使用 `synchronized` 确保增量操作是原子的，并使用 `volatile` 保证当前结果的可见性。如果更新不频繁的话，该方法可实现更好的性能，因为读路径的开销仅仅涉及 volatile 读操作，这通常要优于一个无竞争的锁获取的开销。



```java
@ThreadSafe



public class CheesyCounter {



    // Employs the cheap read-write lock trick



    // All mutative operations MUST be done with the 'this' lock held



    @GuardedBy("this") private volatile int value;



 



    //读操作，没有synchronized，提高性能



    public int getValue() { 



        return value; 



    } 



 



    //写操作，必须synchronized。因为x++不是原子操作



    public synchronized int increment() {



        return value++;



    }
```


使用锁进行所有变化的操作，使用 volatile 进行只读操作。
其中，锁一次只允许一个线程访问值，volatile 允许多个线程执行读操作