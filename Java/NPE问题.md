最近在看[Java_manual.pdf](https://links.jianshu.com/go?to=https%3A%2F%2F102.alibaba.com%2FdownloadFile.do%3Ffile%3D1528269849853%2FJava_manual.pdf%3F_blank)，其中有一点值得反思：“防止 NPE，是程序员的基本修养。”
 NPE(Null Pointer Exception)一直是我们最头疼的问题，也是最容易忽视的地方，先总结几条不同场景的解决方案，望后续补充。
 1.RPC调用某个规定好的接口，如获取某一范围内查询结果，返回结果我们约定好是List<String>。
 若没有结果，我们是返回null还是Collections.emptyList() ？虽然NPE问题大部分是调用者问题，但是我们可以从源头解决，比如我们不允许null值，必须返回对应类型（当然实际业务可能不一样）。



```dart
public List<String> getLists(Map<String, Object> condition) {
    List<String> records = getRecords(condition);
    if (records != null && records.size() > 0) {
      return records;
    }
    return Collections.emptyList();
  }
```

2.foreach遍历循环时候要做集合null值判断，以前我们这么写



```dart
    if (records != null && records.size() > 0) {
      for (String record : records) {
        //some codes
      }
    }
    
    //next step
```

我们可以直接使用org.springframework.util.CollectionUtils;



```dart
if (!CollectionUtils.isEmpty(records)) {
      for (String record : records) {
        //some codes
      }
    }

    //next step
```

3.与2对应的jdk8提供了一些“工具”类java.util.Objects对对象进行非空判断：



```rust
    if (Objects.nonNull(str)) { // 等价于 str != null
      // some codes
    }

    // next step
```



```csharp
if (Objects.equals(a, b)) { // 等价于 (a == b) || (a != null && a.equals(b));
      // some codes
    }

    // next step
```



```dart
 // 等价于 String str = a == null ? null : a.toString();
String str = Objects.toString(a, null);
```

4.还有一些pojo里面的类型尽量使用包装类（之前[《java中包装类与基本类型的运用对比》](https://www.jianshu.com/p/0b4cc1da6ada?_blank)提到过），这会导致null值出现，根据不同场景进行处理，但是不建议在pojo里面处理，要保证pojo的完整干净。



```tsx
// 反例：
public void setCreateTime(Date createTime) {
    if (Objects.isNull(createTime)) {
      this.createTime = new Date();
    } else {
      this.createTime = createTime;
    }
  }
```

5.spring注解@NonNull @Nullable
 如果可以传入NULL值，则标记为@Nullable，如果不可以，则标注为@NonNull。如果违反了这些协定，IntelliJ IDEA 将出现警告。



```csharp
private List<Person> query(@NonNull PersonQueryBean queryBean) {
     // to do some codes...
    Sort sort = queryBean.getSort();
    ....
}
```







## **1. 前言** 

对于 **Java** 开发者来说，`null` 是一个令人头疼的类型，一不小心就会发生 **NPE** （空指针） 问题。也是 **Java** 语言为人诟病的一个重要原因之一。在我们消除可恶的 **NPE** 问题之前我们要回顾一下 Java 中 null 的概念。

## **2. Java 中的 null** 

翻译自 **Oracle Java 文档[1]**

> Java 语言中有两种类型，一种是 **基本类型** ，另一种是 **引用类型**。还有一种没有名字的特殊类型，即表达式 `null` 。由于 `null` 类型没有名称，所以不可能声明为 `null` 类型的变量或者转换为 `null` 类型。`null` 引用是`null` 类型表达式唯一可能的值。`null` 引用可以转换为任意引用类型。事实上，程序员可以忽略`null`类型，可以认为`null`仅仅是一个可以成为任何引用类型的特殊符号。

从上面的描述我们可以了解到,**其实 null 仅仅是一个关键字标识量，既不是一种类型也不算对象，无法直接声明 null 和被转换为 null，仅仅只能被引用，null 可以转换为任何引用类型。当一个 Java 引用类型对象被引用为 null 时代表当前对象不引用对象，并没有为其分配内存。** 这也是我们在没有引用的对象上调用方法出现空指针的根本原因。大多数情况下 **Java** 开发者使用 `null` 是为了表示某种不存在的意思。

## **3. NPE 问题的解决** 

很多时候我们对数据是否存在有自己的期望，但是这种期望并不能直接被我们掌控，一个返回值为 null 所表达的意思并不明确过于模糊，往往通过是否判断为 `null` 来规避空指针问题。于是 Google 工程师在他们的 `Guava` 工具类库中设计了 `Optional<T>` 来解决 `null` 不可控的问题。让你在不得不使用 `null` 的时候，可以更加简便明确的使用 `null` 并帮助你避免直接使用 `null` 带来的问题。**Java 8** 将此设计吸收。我们可以直接使用 **Java** 提供的 `Optional` 来解决空指针问题。接下来我们来研究一下 **Java 8** 中的 `Optional` 。

## **4. Java 8 中的 Optional** 

Java 8 中的 `Optional` 是一个可选值的包装类。它的意义不仅仅帮我们简化了 **NPE** 问题的处理，同时也是 **Java** 函数式编程的一个重要辅助。我们接下来将对其 **API** 进行讲解以帮助你在实际开发中使用他们。

### **4.1 Optional 声明**

`Optional` 只能通过静态方法来声明。它提供了三个静态方法：

**empty()** 返回一个值为 `null` 的 `Optional` 实例

```javascript
  Optional<Object> empty = Optional.empty();
```

**of(T)** 返回一个值不为 `null` 的 `Optional` 实例

```javascript
 Optional<String> nonNull = Optional.of("Felordcn");
```

**ofNullable()** 返回一个值可能为 `null` 的 `Optional` 实例

```javascript
 // value 值来自其它不确定的来源
 String value = SomeApi.source();
 // 可能为 null
 Optional<String> nullable = Optional.ofNullable(value);
 // 也可能不为 null
 Optional<String>  hasValue = Optional.ofNullable(value);
```

### **4.2 其它方法**

**isPresent()** 如果值存在则返回 `true`，否则返回 `false` 。如果 `Optional` 值不确定，可使用该方法进行安全校验

```javascript
 Optional<String> nonNull = Optional.of("Felordcn");
  // true
  boolean present =nonNull.isPresent();
```

**get()** 获取 `Optional` 中的值，如果为空会抛出 `NoSuchElementException` 异常

```javascript
 Optional<String> nonNull = Optional.of("Felordcn");
  // Felordcn
  String str = nonNull.get();
```

**ifPresent(Consumer)** 如果值存在则该值被消费函数 `Consumer` 消费 , 否则不做任何事情。`isPresent()` 加强版

```javascript
      //  非空打印出字符串
      nullable.ifPresent(System.out::println);

     //等同于
      if (nullable.isPresent()) {
                 System.out.println(nonNull);
      }
```

**filter(Predicate)** 如果值满足断言函数 `Predicate` 则返回该 `Optional`，否则返回 `Optional.empty()`

```javascript
 Optional<String> nonNull = Optional.of("Felordcn");
 Optional<String> felord = nonNull.filter(s -> s.startsWith("Felord"));
 // str = "Felordcn"
 String str = felord.get();
```

**map(Function)** 获取元素某个属性的 `Optional` 。如果该属性为 `null` 返回 `Optional.empty()` ，否则返回对应值的 `Optional`

```javascript
 Optional<User> userOpt = Optional.ofNullable(user);
//  username 为空 则为 空  Optional
 Optional<String> usernameOpt = userOpt.map(User::getUsername);
```

**flatMap(Function)** 有时候我们会返回 `Optional<Optional<T>>` 非常不便于处理，我们需要将元素展开，可使用该方法处理，参考 **Stream Api** 中的相关方法

**orElse(other)** 如果 `Optional` 的值存在，返回 `Optional`， 否则指定一个 `Optional`

**orElseGet(Supplier)** 如果 `Optional` 的值存在，返回 `Optional`， 否则指定一个执行 `Supplier` 函数来获取值

**orElseThrow(Supplier<? extends Throwable>)** 如果 `Optional` 的值存在，返回 `Optional`， 否则抛出一个指定 `Supplier` 函数提供的异常

### **4.3 Java 9 中的新 API**

**or(Supplier)** `orElseGet` 的改进类型。不单单返回具体的值，而可以函数式的返回 `Optional`

**stream()** 将 `Optional` 和 `Stream` 打通

**ifPresentOrElse(Consumer)** `ifPresent` 方法提供了有值后的消费逻辑而没有值的逻辑没有提供入口。新方法 `ifPresentOrElse` 弥补了这一缺陷

## **5. Optional 的使用误区** 

`Optional` 很香但是也不能滥用。一个危险的举动就是将 `Optional` 作为入参传递给方法。因为入参是不可控的，你无法保证入参中的 `Optional` 是否为 `null`。这恰恰违背了 `Optional` 的本意。所以尽量在表达式中使用 `Optional` 或者在返回值中使用，而不是在方法的参数中使用 `Optional` 。

## **6. 总结** 

今天对 `Optional` 进行讲解。从 `Optional` 的设计本意到其常用的方法。我们也对 `Optional` 在 **Java 9** 中的新 API 进行了介绍。另外 `Optional` 也不是万能的，合理的使用才能发挥其优势。希望今天的文章对你有用。