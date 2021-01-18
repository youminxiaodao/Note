# new Integer(10)和new Integer(10)是否相等

今天看网上的面试题看到了一个这样的问题，new Integer(10)和new Integer(10)是否相等，new Integer（100）和new Integer（100）是否相等，当时没怎么过脑子就给了答案---false，false，但是当自己在程序里运行一下发现并没用那么简单。

```java
public static void main(String[] args) {



        Integer integer1 = 1;



        Integer integer2 = 1;



        System.out.println(integer1 == integer2);



        System.out.println(integer1.getClass()); //打印变量类型为int



        



        Integer integer3 = 100;



        Integer integer4 = 100;



        System.out.println(integer3 == integer4);



    }
```

java中“==”比较的是两个引用而非引用的值，所以大家普遍认为两个输出都会是false，然而运行结果则为true，false。

真正的结果是这样的：

![img](https://img-blog.csdn.net/20180314214645463?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L2h1MTU5MDMzMTQ4NTA=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

后来在网上找了好久，翻了些博客。发现这个问题的本质其实jdk1.5的时候引进了自动拆包，装包这个功能。上面这种情况就是自动打包出现的。

为什么自动装包就会出现这种结果呢，那要先说一下Integer的缓存机制，

```java
/**



     * Returns an {@code Integer} instance representing the specified



     * {@code int} value.  If a new {@code Integer} instance is not



     * required, this method should generally be used in preference to



     * the constructor {@link #Integer(int)}, as this method is likely



     * to yield significantly better space and time performance by



     * caching frequently requested values.



     *



     * This method will always cache values in the range -128 to 127,



     * inclusive, and may cache other values outside of this range.



     *



     * @param  i an {@code int} value.



     * @return an {@code Integer} instance representing {@code i}.



     * @since  1.5



     */



    @HotSpotIntrinsicCandidate



    public static Integer valueOf(int i) {



        if (i >= IntegerCache.low && i <= IntegerCache.high)



            return IntegerCache.cache[i + (-IntegerCache.low)];



        return new Integer(i);



    }
```

我们可以看到Integer在进行valueOf方法的时候是先判断了一下传进来的值得大小，如果在IntegerCache.low和nteger.high之间，就从IntegerCache中取值直接返回，否则new一个新的Integer对象返回。IntegerCache又是什么呢，往下看：

```html
/**



     * Cache to support the object identity semantics of autoboxing for values between



     * -128 and 127 (inclusive) as required by JLS.



     *



     * The cache is initialized on first usage.  The size of the cache



     * may be controlled by the {@code -XX:AutoBoxCacheMax=<size>} option.



     * During VM initialization, java.lang.Integer.IntegerCache.high property



     * may be set and saved in the private system properties in the



     * jdk.internal.misc.VM class.



     */



 



    private static class IntegerCache {



        static final int low = -128;



        static final int high;



        static final Integer cache[];



 



        static {



            // high value may be configured by property



            int h = 127;



            String integerCacheHighPropValue =



                VM.getSavedProperty("java.lang.Integer.IntegerCache.high");



            if (integerCacheHighPropValue != null) {



                try {



                    int i = parseInt(integerCacheHighPropValue);



                    i = Math.max(i, 127);



                    // Maximum array size is Integer.MAX_VALUE



                    h = Math.min(i, Integer.MAX_VALUE - (-low) -1);



                } catch( NumberFormatException nfe) {



                    // If the property cannot be parsed into an int, ignore it.



                }



            }



            high = h;



 



            cache = new Integer[(high - low) + 1];



            int j = low;



            for(int k = 0; k < cache.length; k++)



                cache[k] = new Integer(j++);







            // range [-128, 127] must be interned (JLS7 5.1.7)



            assert IntegerCache.high >= 127;



        }



 



        private IntegerCache() {}



    }
```

分析上述源码可知，IntegerCache的low为-128，而high可以通过"-XX:AutoBoxCacheMax=<size>"指定，默认的high为127。当设置high之后，high的实际值取默认值和设置值中最大值，同时小于或等于0X7FFFFF7E。然后将low到high之间的数值存放到cache中。由于该类及方法都是静态的，因此在首次使用时会执行该操作，当需要对low到high之间的数字进行装包操作时，则直接返回IntegerCache.cache中现成的引用。所以上述对基本类型1进行装包时返回同一个引用，故结果输出true

当然，Java中除了对Integer有缓存机制外，其中还有ByteCache，ShortCache，LongCache，CharacterCache分别对其对应的类型进行缓存，其中Byte，Short，Long的缓存范围都为-128——127，Character为0——127。特别要注意的是这几个缓存中，只有Integer的缓存上限（high）可以设置，其他的都不能进行设置，为固定范围。



再说一下非自动装包的情况下答案是怎么样的：

```java
public static void main(String[] args) {



        Integer integer1 = new Integer(1);



        Integer integer2 = new Integer(1);



        System.out.println(integer1 == integer2);



        System.out.println(integer1.equals(integer2));



 



 



        Integer integer3 = new Integer(100);



        Integer integer4 = new Integer(100);



        System.out.println(integer3 == integer4);



        System.out.println(integer3.equals(integer4));



    }
false



true



false



true
```

这是为什么，因为我们用new Integer（）的时候，java并没有调用valueOf（）方法，也就是并没有进行自动装包，而只是单纯的new了两个Integer对象。所以==判断为false，equals判断为true