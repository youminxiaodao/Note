## JDK7(Entry数组+单链表)

### 初始化

new HashMap<>();

**初始化**时，默认的是容量是16，加载因子是 0.75，扩容阈值(threshold)为默认容量16

![image-20210404095635534](C:\Develop\Git\Note\images\image-20210404095635534.png)

![image-20210404095304041](C:\Develop\Git\Note\images\image-20210404095304041.png)

![image-20210404095334919](C:\Develop\Git\Note\images\image-20210404095334919.png)

![image-20210404095453457](C:\Develop\Git\Note\images\image-20210404095453457.png)

### 添加元素

第一次添加元素时，table=EMPTY_TABLE，这时候需要调用inflateTable(threshold)初始化Entry数组

![image-20210404095523387](C:\Develop\Git\Note\images\image-20210404095523387.png)



![image-20210404095821983](C:\Develop\Git\Note\images\image-20210404095821983.png)

![image-20210404101409025](C:\Develop\Git\Note\images\image-20210404101409025.png)

使用roundUnToPowerOf2(toSize)  (toSize:16)算出一个比toSize大的2的幂次方数

原因:得到2的幂次方数，是为了处理hash冲突时，可以把算出的数组下表值限定在数组的大小范围内(length-1)&h

如现在length-1为15，二进制表示即1111，这样与任何一个数进行&运算，得到的结果都是0000~1111即0~15

重新计算扩容阈值threshold，16*0.75=12

初始化一个容量为16的Entry数组，table=Entry[capacity]

![image-20210404101832675](C:\Develop\Git\Note\images\image-20210404101832675.png)



![image-20210404102428780](C:\Develop\Git\Note\images\image-20210404102428780.png)

hash(key):使用hashcode方法获取hash值，再使用无符号右移、异或运算，使之在indexFor中计算数组下标时，使高位与结果有关，提高散列性

indexFor:获取该key在数组中的下标

![image-20210404103024218](C:\Develop\Git\Note\images\image-20210404103024218.png)

遍历当前数组位置，若当前位置存储一个相同的key，则进行值覆盖，并返回旧value值

![image-20210404103225672](C:\Develop\Git\Note\images\image-20210404103225672.png)

若当前位置没有存储新key，则变更记录加1，并生成一个Entry节点，把这个节点加入这个位置上的单链表

![image-20210404103349824](C:\Develop\Git\Note\images\image-20210404103349824.png)



![image-20210404103551352](C:\Develop\Git\Note\images\image-20210404103551352.png)

若当前数组大小小于扩容阈值(12)，则直接新增元素(createEntry)

先获取数组上这个位置上的Entry节点，创建一个新节点，指向旧节点(头插法)，然后存入数组

![image-20210404103903017](C:\Develop\Git\Note\images\image-20210404103903017.png)

若当前数组大小大于扩容阈值(12)，需要进行扩容

![image-20210404104002318](C:\Develop\Git\Note\images\image-20210404104002318.png)

扩容后的数组大小是原数组的2倍

![image-20210404104058399](C:\Develop\Git\Note\images\image-20210404104058399.png)

使用transfer将旧数组上的节点重新计算数组下标，存入新数组

新下标:新数组下标，要么是原下标，要么加上原数组大小即加16

原因:原数组下标是使用(length-1)&h进行计算，leng-1得到的2进制是1111，h的二级制是x0000~x1111，其中x可能是0，也可能是1，在原数组中进行下标计算，x位无影响；但是新数组是原数组的2倍，这时候length-1得到的2进制是11111,与h进行&运算，如果x是0，得到的下标与原数组一致，若为1，则新下标在原下标基础上加16

![image-20210404104250268](C:\Develop\Git\Note\images\image-20210404104250268.png)

### 获取元素

![image-20210404105134989](C:\Develop\Git\Note\images\image-20210404105134989.png)

计算查询key的hash值，获取到数组下标，用下标获取数组中该下标上的单链表

遍历该链表，如找到则返回value，否则返回null

![image-20210404105202489](C:\Develop\Git\Note\images\image-20210404105202489.png)



### 删除元素

![image-20210404105415271](C:\Develop\Git\Note\images\image-20210404105415271.png)



![image-20210404105434439](C:\Develop\Git\Note\images\image-20210404105434439.png)

### 获取hashmap大小

![image-20210404110939822](C:\Develop\Git\Note\images\image-20210404110939822.png)



## JDK8(Node数组+单链表+红黑树)

### 初始化

new HashMap<>();

![image-20210404111214712](C:\Develop\Git\Note\images\image-20210404111214712.png)

![image-20210404111538996](C:\Develop\Git\Note\images\image-20210404111538996.png)

### 新增元素

![image-20210404111615282](C:\Develop\Git\Note\images\image-20210404111615282.png)

![image-20210404111643317](C:\Develop\Git\Note\images\image-20210404111643317.png)

![image-20210404111946446](C:\Develop\Git\Note\images\image-20210404111946446.png)

#### 扩容/初始化

第一次进行初始化操作，设置扩容因子为newThr即12，新建一个大小为newCap(16)的Node数组，由于第一次出书画，oldTab(table)为空，因此直接返回这个新建的Node数组(newTab)

![image-20210404112354459](C:\Develop\Git\Note\images\image-20210404112354459.png)

![image-20210404112430617](C:\Develop\Git\Note\images\image-20210404112430617.png)

初始化完后，新建一个Node节点，并存入数组i位置，返回空

![image-20210404112748232](C:\Develop\Git\Note\images\image-20210404112748232.png)

![image-20210404112826380](C:\Develop\Git\Note\images\image-20210404112826380.png)

#### hash冲突

##### 存在旧key

若有找到相同的key，用新value替换旧value，直接return旧value

![image-20210404113545264](C:\Develop\Git\Note\images\image-20210404113545264.png)

##### 不存在旧key，且单链表节点数小于8

![image-20210404114707798](C:\Develop\Git\Note\images\image-20210404114707798.png)

##### 不存在旧key，且单链表节点数为8

![image-20210404114912275](C:\Develop\Git\Note\images\image-20210404114912275.png)

###### 数组容量小于64

不进行树化，直接进行扩容，从本质上解决容量不足问题

![image-20210404115338526](C:\Develop\Git\Note\images\image-20210404115338526.png)

![image-20210404122605863](C:\Develop\Git\Note\images\image-20210404122605863.png)

![image-20210404121409299](C:\Develop\Git\Note\images\image-20210404121409299.png)

###### 数组容量大于64

![image-20210404123132545](C:\Develop\Git\Note\images\image-20210404123132545.png)

创建新TreeNode节点，用来转换单链表成双向链表

![image-20210404123105627](C:\Develop\Git\Note\images\image-20210404123105627.png)

![image-20210404123159399](C:\Develop\Git\Note\images\image-20210404123159399.png)

![image-20210404123225787](C:\Develop\Git\Note\images\image-20210404123225787.png)

![image-20210404123248589](C:\Develop\Git\Note\images\image-20210404123248589.png)



