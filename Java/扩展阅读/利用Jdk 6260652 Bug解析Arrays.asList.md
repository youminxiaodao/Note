在java.util.ArrayList源码中：

c.toArray might (incorrectly) not return Object[] (see 6260652) 产生疑惑：

附上Java Bug 网址:[ Java Bug Database](https://bugs.java.com/bugdatabase/)

```
,可以根据关键词或bug id 查询详细信息

这个Bug的描述中可以看出：
原因：Arrays内部实现的ArrayList的toArray()方法的行为与规范不一致。
代码测试：
```

```
import java.util.*;

public class Test{
     
    public static void demo1(){
        System.out.println("this is demo1");
        List<String> list=new ArrayList<>();
        list.add("张三");
        list.add("王五");
        Object[] arr=list.toArray();
        System.out.println(arr.getClass().getCanonicalName());
        arr[0]=new Object();
        Test.printArr(arr);
        
        /*
        正常编译、执行：
        this is demo1
        java.lang.Object[]
        java.lang.Object@15db9742  王五
        */
        
    }
    
    
    public static void demo2(){
        System.out.println("this is demo2");
        List<String> list = Arrays.asList("张三", "王五");
        
        Object[] arr=list.toArray();
        System.out.println(arr.getClass().getCanonicalName());
        arr[0]=new Object();
        Test.printArr(arr);
        
        /*
        正常编译
        执行输出：
        this is demo2
        java.lang.String[]
        Exception in thread "main" java.lang.ArrayStoreException: java.lang.Object
        at Test.demo2(Test.java:31)
        at Test.main(Test.java:55)
        */
        
    }
    
    public static void demo3() {
        System.out.println("this is demo3");
        Object[] arr = new String[]{"张三", "王五"};
        System.out.println(arr.getClass().getCanonicalName());
        arr[0] = 7;
        Test.printArr(arr);
        
        /*
        正常编译
        执行输出：
        this is demo3
        java.lang.String[]
        Exception in thread "main" java.lang.ArrayStoreException: java.lang.Integer
        at Test.demo3(Test.java:48)
        at Test.main(Test.java:71)
        */
    }
    
    public static void printArr(Object[] arr) {
        for (Object o : arr) {
            System.out.print(o + "  ");
        }
        System.out.println();
    }
    
    public static void main(String[]args){
        
        //Test.demo1();
        //Test.demo2();
        Test.demo3();
    }
}
```

```
输出截图：



分析过程详解：
第一步：
看ArrayList带Collection对象的构造函数源码（java.util.ArrayList）：
```



```
public ArrayList(Collection<? extends E> c) {
    elementData = c.toArray();
    size = elementData.length;
    // c.toArray might (incorrectly) not return Object[] (see 6260652)
    if (elementData.getClass() != Object[].class)
        elementData = Arrays.copyOf(elementData, size, Object[].class);
 }
```



```
看java.util.ArrayList，中toArray()源码：
```



```
public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    /**
     * 返回 ArrayList 元素组成的数组
     * @param a 需要存储 list 中元素的数组
     * 若 a.length >= list.size，则将 list 中的元素按顺序存入 a 中，然后 a[list.size] = null, a[list.size + 1] 及其后的元素依旧是 a 的元素
     * 否则，将返回包含list 所有元素且数组长度等于 list 中元素个数的数组
     * 注意：若 a 中本来存储有元素，则 a 会被 list 的元素覆盖，且 a[list.size] = null
     * @return
     * @throws ArrayStoreException 当 a.getClass() != list 中存储元素的类型时
     * @throws NullPointerException 当 a 为 null 时
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // 若数组a的大小 < ArrayList的元素个数,则新建一个T[]数组，
        // 数组大小是"ArrayList的元素个数",并将“ArrayList”全部拷贝到新数组中
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        // 若数组a的大小 >= ArrayList的元素个数,则将ArrayList的全部元素都拷贝到数组a中。
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }
```



可以看出，由于ArrayList中elementData类型为Object[],所以调用copyOf()返回值类型为Object[]。

**第二步：**

看 Arrays.asList()源码：

```
public static <T> List<T> asList(T... a) {
       return new ArrayList<>(a);
      }
```

仔细阅读官方文档，你会发现对 asList 方法的描述中有这样一句话：

> 返回一个由指定数组生成的**固定大小**的 List。

注意：参数类型是 T ，根据官方文档的描述，T 是数组元素的 class。

任何类型的对象都有一个 class 属性，这个属性代表了这个类型本身。原生数据类型，比如 int，short，long等，是没有这个属性的，具有 class 属性的是它们所对应的包装类 Integer，Short，Long。

asList 方法的参数必须是对象或者对象数组，而原生数据类型不是对象。当传入一个原生数据类型数组时，asList 的真正得到的参数就不是数组中的元素，而是数组对象本身。（解决方案：使用包装类数组。）

继续分析：

此时的ArrayList并非我们常用的java.util.ArrayList，而是Arrays的内部类。它继承自AbstractList，自然实现了Collection接口，代码如下：



```
private static class ArrayList<E> extends AbstractList<E>
        implements RandomAccess, java.io.Serializable
 {
        private static final long serialVersionUID = -2764017481108945198L;
        private final E[] a;

        ArrayList(E[] array) {
            if (array==null)
                throw new NullPointerException();
            a = array;
        }

        public int size() {
            return a.length;
        }
        。。。。。。
 }
```



可以发现，**这里的a不是 Object[]，而是E[]**。

a称为该ArrayList的backed array。同时构造函数也是直接用array给a赋值。这就是问题的所在。

另外，这个内部类里面并没有add,remove方法，它继承的AbstractList类里面有这些方法：



```
ublic abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
        。。。。。。。

    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }
 
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }
    。。。。。。
 
```



abstractList这个抽象类所定义的add和remove方法，仅仅是抛出了一个异常！

如果是想将一个数组转化成一个列表并做增加删除操作的话，建议代码如下：



```
public class Test {
   public static void main(String[] args) {
      String[] myArray = { "张三", "李四", "赵六" };
      List<String> myList = new ArrayList<String>(Arrays.asList(myArray));
      myList.add("王五");
   }
};  
```



demo2（测试代码中的）：



```
public static void demo2(){
        System.out.println("this is demo2");
        List<String> list = Arrays.asList("张三", "王五");
        
        Object[] arr=list.toArray();
        System.out.println(arr.getClass().getCanonicalName());
        arr[0]=new Object();
        Test.printArr(arr);
        
        /*
        正常编译
        执行输出：
        this is demo2
        java.lang.String[]
        Exception in thread "main" java.lang.ArrayStoreException: java.lang.Object
        at Test.demo2(Test.java:31)
        at Test.main(Test.java:55)
        */
        
    }
```



上面的抛出异常分析：

asList方法直接将String[]数组作为参数传递给ArrayList的构造方法，然后将String[]直接赋值给内部的a，所以a的真实类型是String[]，根据JLS规范String[]的clone方法返回的也是String[]类型。最终，toArray()方法返回的真实类型是String[]，此时，操作*arr[0]=new Object();*是向数组中添加Object对象，就会报异常的问题了。

Jdk 6260652 Bug 问题是在2005年提出的,现在已经解决了，使用toArray(T[] a)避免Exception的发生，所以可能会导致类型不匹配的错误。

 小总结：

Arrays.asList()的使用方法：

该方法是将数组转化为list。有以下几点需要注意：

1.该方法不适用于基本数据类型（byte,short,int,long,float,double,boolean）

解决方案：使用包装类数组，例子如下：



```
public class Test {
   public static void main(String[] args) {
      Integer[] myArray = { 1, 2, 3 };
      List myList = Arrays.asList(myArray);
      System.out.println(myList.size());
   }
}
```



2.该方法将数组与列表链接起来，当更新其中之一时，另一个自动更新

3.不支持add和remove方法

将数组转化为一个List对象，一般会想到Arrays.asList()方法，这个方法会返回一个ArrayList类型的对象。但是用这个对象对列表进行添加删除更新操作，就会报UnsupportedOperationException异常。

原因：**这个ArrayList类并非java.util.ArrayList类，而是Arrays类的静态内部类！**

说明：asList的返回对象是一个Arrays内部类,并没有实现集合的修改方法。Arrays.asList体现的是适配器模式，只是转换接口，后台的数据仍是数组。

```
String[] str = new String[]{"张三","王五"};
List list = Arrays.asList(str);
```

第一种情况：list.add("赵四"); //运行时异常

第二种情况:str[0] = "大二哈"; //list.get(0)也随着修改。

此类包含用来操作数组（比如排序和搜索）的各种方法。此类还包含一个允许将数组作为列表来查看的静态工厂。 除非特别注明，否则如果指定数组引用为 null，则此类中的方法都会抛出 NullPointerException