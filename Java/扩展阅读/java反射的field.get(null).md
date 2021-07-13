在java的反射中,通过字段获取对象,是通过 

Java代码  ![收藏代码](http://huangyunbin.iteye.com/images/icon_star.png)

1. **public** Object get(Object obj) 



字段不是静态字段的话,要传入反射类的对象.如果传null是会报 
java.lang.NullPointerException 

但是如果字段是静态字段的话,传入任何对象都是可以的,包括null 

下面是例子 : 



Java代码  ![收藏代码](http://huangyunbin.iteye.com/images/icon_star.png)

1. **package** com.huangyunbin; 
2.  
3. **import** java.lang.reflect.Field; 
4.  
5. **public** **class** FiledTest 
6. { 
7.   **public** **static** **void** main(String[] args) **throws** Exception 
8.   { 
9. ​    Field field=A.**class**.getDeclaredField("fild"); 
10. ​    **int** a= (Integer)field.get(**new** A()) ; 
11. //    int c= (Integer)field.get(null) ; //不是静态字段不能传null 
12. ​    System.out.println(a); 
13.  
14. ​    Field staticfield=A.**class**.getDeclaredField("staticFild"); 
15. ​    **int** b= (Integer)staticfield.get("") ; 
16. ​    **int** d= (Integer)staticfield.get(**null**) ; 
17. ​    System.out.println(b); 
18. ​    System.out.println(d); 
19.   } 
20.  
21. } 
22.  
23. **class** A { 
24.    **int** fild=3; 
25.   **static** **int** staticFild=4; 
26. } 



结果是 
3 
4 
4







1、field.get(null)

​    

​    public static void main(String[] args){
Field field=MainTest.class.getDeclaredField("name"); //static 
field.setAccessible(true);
Object o=field.get(null);
System.out.println("o="+o);  //输出string
}
public static String name="string";



1)如果field的name是一个static的变量，field.get(param)，param是任意的都可以，返回类中当前静态变量的值。

2)如果是非静态变量，field.get(obj)，obj必须是当前类的实例对象，返回实例对象obj的变量值。





2、field.set(null,string)

同上，field是一个静态变量时，会修改当前类中该变量的值为string；

field是非静态变量，则需要field(obj,string)，则修改obj这个实例对象中的field的值为string。









JDK API

- - - ```
      public Object get(Object obj)
                 throws IllegalArgumentException,
                        IllegalAccessException
      ```

      返回该所表示的字段的值`Field` ，指定的对象上。如果该对象具有原始类型，则该值将自动包装在对象中。

      基础字段的值如下获得： 

      如果基础字段是静态字段，则忽略`obj`参数; 它可能为null。 

      否则，底层字段是一个实例字段。  如果指定的`obj`参数为空，则该方法将抛出一个`NullPointerException`  。  如果指定的对象不是声明底层字段的类或接口的实例，则该方法将抛出一个`IllegalArgumentException`  。 

      如果这个`Field`对象正在执行Java语言访问控制，并且底层字段是无法访问的，则该方法将抛出一个`IllegalAccessException`  。 如果底层字段是静态的，那么声明该字段的类如果尚未被初始化，则会被初始化。 

      否则，将从底层实例或静态字段检索该值。  如果该字段具有原始类型，则该值在返回之前被包装在对象中，否则返回原样。 

      如果该字段隐藏在`obj`的类型中，则根据前述规则获取字段的值。 

      

- - - ```
      public void set(Object obj,
                      Object value)
               throws IllegalArgumentException,
                      IllegalAccessException
      ```

      将指定对象参数上的此`Field`对象表示的字段设置为指定的新值。如果基础字段具有原始类型，则新值将自动展开。

      操作进行如下： 

      如果基础字段是静态的，则忽略`obj`参数; 它可能为null。 

      否则底层字段是一个实例字段。  如果指定的对象参数为空，则该方法将抛出一个`NullPointerException` 。  如果指定的对象参数不是声明底层字段的类或接口的实例，则该方法将抛出一个`IllegalArgumentException`  。 

      如果这个`Field`对象正在执行Java语言访问控制，并且底层字段是无法访问的，则该方法将抛出一个`IllegalAccessException`  。 

      如果底层字段是final，该方法将抛出一个`IllegalAccessException`  ，除非`setAccessible(true)`已成功为该`Field`对象，该字段是非静态的。  以这种方式设置最终字段只有在反序列化或重建具有空白最终字段的类的实例时才有意义，才能使其可用于程序其他部分的访问。  在任何其他情况下使用可能会产生不可预测的影响，包括程序的其他部分继续使用此字段的原始值的情况。 

      如果底层字段是原始类型，则尝试将新值转换为原始类型的值的解包转换。  如果此尝试失败，该方法将抛出一个`IllegalArgumentException` 。 

      如果在可能展开后，新值不能通过标识或扩展转换转换为底层字段的类型，则该方法将抛出一个`IllegalArgumentException`  。 

      如果底层字段是静态的，那么声明该字段的类如果尚未被初始化，则会被初始化。 

      该字段设置为可能展开和扩展的新值。 

      如果字段隐藏在`obj`类型中，则该字段的值根据前面的规则设置。 







Returns the value of the field represented by this Field, on the specified object. The value is automatically wrapped in an object if it has a primitive type.
The underlying field's value is obtained as follows:
If the underlying field is a static field, the obj argument is ignored; it may be null.
Otherwise, the underlying field is an instance field. If the specified obj argument is null, the method throws a NullPointerException. If the specified object is not an instance of the class or interface declaring the underlying field, the method throws an IllegalArgumentException.
If this Field object is enforcing Java language access control, and the underlying field is inaccessible, the method throws an IllegalAccessException. If the underlying field is static, the class that declared the field is initialized if it has not already been initialized.
Otherwise, the value is retrieved from the underlying instance or static field. If the field has a primitive type, the value is wrapped in an object before being returned, otherwise it is returned as is.
If the field is hidden in the type of obj, the field's value is obtained according to the preceding rules.
Params:
obj – object from which the represented field's value is to be extracted
Returns:
the value of the represented field in object obj; primitive values are wrapped in an appropriate object before being returned
Throws:
IllegalAccessException – if this Field object is enforcing Java language access control and the underlying field is inaccessible.
IllegalArgumentException – if the specified object is not an instance of the class or interface declaring the underlying field (or a subclass or implementor thereof).
NullPointerException – if the specified object is null and the field is an instance field.
ExceptionInInitializerError – if the initialization provoked by this method fails.

```java
@CallerSensitive
public Object get(Object obj)
    throws IllegalArgumentException, IllegalAccessException
{
    if (!override) {
        if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
            Class<?> caller = Reflection.getCallerClass();
            checkAccess(caller, clazz, obj, modifiers);
        }
    }
    return getFieldAccessor(obj).get(obj);
}
```



Sets the field represented by this Field object on the specified object argument to the specified new value. The new value is automatically unwrapped if the underlying field has a primitive type.
The operation proceeds as follows:
If the underlying field is static, the obj argument is ignored; it may be null.
Otherwise the underlying field is an instance field. If the specified object argument is null, the method throws a NullPointerException. If the specified object argument is not an instance of the class or interface declaring the underlying field, the method throws an IllegalArgumentException.
If this Field object is enforcing Java language access control, and the underlying field is inaccessible, the method throws an IllegalAccessException.
If the underlying field is final, the method throws an IllegalAccessException unless setAccessible(true) has succeeded for this Field object and the field is non-static. Setting a final field in this way is meaningful only during deserialization or reconstruction of instances of classes with blank final fields, before they are made available for access by other parts of a program. Use in any other context may have unpredictable effects, including cases in which other parts of a program continue to use the original value of this field.
If the underlying field is of a primitive type, an unwrapping conversion is attempted to convert the new value to a value of a primitive type. If this attempt fails, the method throws an IllegalArgumentException.
If, after possible unwrapping, the new value cannot be converted to the type of the underlying field by an identity or widening conversion, the method throws an IllegalArgumentException.
If the underlying field is static, the class that declared the field is initialized if it has not already been initialized.
The field is set to the possibly unwrapped and widened new value.
If the field is hidden in the type of obj, the field's value is set according to the preceding rules.
Params:
obj – the object whose field should be modified
value – the new value for the field of obj being modified
Throws:
IllegalAccessException – if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
IllegalArgumentException – if the specified object is not an instance of the class or interface declaring the underlying field (or a subclass or implementor thereof), or if an unwrapping conversion fails.
NullPointerException – if the specified object is null and the field is an instance field.
ExceptionInInitializerError – if the initialization provoked by this method fails.

```java
@CallerSensitive
public void set(Object obj, Object value)
    throws IllegalArgumentException, IllegalAccessException
{
    if (!override) {
        if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
            Class<?> caller = Reflection.getCallerClass();
            checkAccess(caller, clazz, obj, modifiers);
        }
    }
    getFieldAccessor(obj).set(obj, value);
}
```