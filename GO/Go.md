## 远程包

像Git或Mercurial这样的版本控制系统，可根据导入路径的描述来获取包源代码。`go` 工具可通过此特性来从远程代码库自动获取包。例如，本文档中描述的例子也可存放到Google Code上的Mercurial仓库 `code.google.com/p/go.example` 中，若你在包的导入路径中包含了代码仓库的URL，`go get` 就会自动地获取、 构建并安装它：

```
$ go get github.com/golang/example/hello
$ $GOPATH/bin/hello
Hello, Go examples!
```

若指定的包不在工作空间中，`go get` 就会将会将它放到 `GOPATH` 指定的第一个工作空间内。（若该包已存在，`go get` 就会跳过远程获取， 其行为与 `go install` 相同）

## Go 的基本类型有  

```
bool

string

int  int8  int16  int32  int64
uint uint8 uint16 uint32 uint64 uintptr

byte // uint8 的别名

rune // int32 的别名
    // 表示一个 Unicode 码点

float32 float64

complex64 complex128
```

`int`, `uint` 和 `uintptr` 在 32 位系统上通常为 32 位宽，在 64 位系统上则为 64 位宽。     当你需要一个整数值时应使用 `int` 类型，除非你有特殊的理由使用固定大小或无符号的整数类型。  



## 常量

​    常量的声明与变量类似，只不过是使用 `const` 关键字。  

​    常量可以是字符、字符串、布尔值或数值。  

​    常量不能用 `:=` 语法声明。  



## 结构体指针

​    结构体字段可以通过结构体指针来访问。  

​    如果我们有一个指向结构体的指针 `p`，那么可以通过 `(*p).X` 来访问其字段 `X`。不过这么写太啰嗦了，所以语言也允许我们使用隐式间接引用，直接写 `p.X` 就可以

	package main
	
	import "fmt"
	
	type Vertex struct {
		X int
		Y int
	}
	
	func main() {
		v := Vertex{1, 2}
		p := &v
		p.X = 1e9
		fmt.Println(v)
	}
	

```go

```

## 切片就像数组的引用

​    切片并不存储任何数据，它只是描述了底层数组中的一段。  

​    更改切片的元素会修改其底层数组中对应的元素。  

​    与它共享底层数组的切片都会观测到这些修改。  



## 切片的长度与容量

​    切片拥有 **长度** 和 **容量**。  

​    切片的长度就是它所包含的元素个数。  

​    切片的容量是从它的第一个元素开始数，到其底层数组元素末尾的个数。  

​    切片 `s` 的长度和容量可通过表达式 `len(s)` 和 `cap(s)` 来获取。  

​    你可以通过重新切片来扩展一个切片，给它提供足够的容量。



## 方法即函数

​    记住：方法只是个带接收者参数的函数。  

​	你也可以为非结构体类型声明方法。

​    在此例中，我们看到了一个带 `Abs` 方法的数值类型 `MyFloat`。  

​    你只能为在同一包内定义的类型的接收者声明方法，而不能为其它包内定义的类型（包括 `int` 之类的内建类型）的接收者声明方法。  

​    （译注：就是接收者的类型定义和方法声明必须在同一包内；不能为内建类型声明方法。）  	

```go
package main

import (
	"fmt"
	"math"
)

type MyFloat float64

func (f MyFloat) Abs() float64 {
	if f < 0 {
		return float64(-f)
	}
	return float64(f)
}

func main() {
	f := MyFloat(-math.Sqrt2)
	fmt.Println(f.Abs())
}

```

## 底层值为 nil 的接口值

​    即便接口内的具体值为 nil，方法仍然会被 nil 接收者调用。  

​	在一些语言中，这会触发一个空指针异常，但在 Go 中通常会写一些方法来优雅地处理它（如本例中的 `M` 方法）。  

​    **注意:** 保存了 nil 具体值的接口其自身并不为 nil。  

```go
package main

import "fmt"

type I interface {
	M()
}

type T struct {
	S string
}

func (t *T) M() {
	if t == nil {
		fmt.Println("<nil>")
		return
	}
	fmt.Println(t.S)
}

func main() {
	var i I

	var t *T
	i = t
	describe(i)
	i.M()

	i = &T{"hello"}
	describe(i)
	i.M()
}

func describe(i I) {
	fmt.Printf("(%v, %T)\n", i, i)
}

```

## nil 接口值

​    nil 接口值既不保存值也不保存具体类型。  

​    为 nil 接口调用方法会产生运行时错误，因为接口的元组内并未包含能够指明该调用哪个 **具体** 方法的类型。  



## 空接口

​    指定了零个方法的接口值被称为 *空接口：*  

```
interface{}
```

​    空接口可保存任何类型的值。（因为每个类型都至少实现了零个方法。）  

​    空接口被用来处理未知类型的值。例如，`fmt.Print` 可接受类型为 `interface{}` 的任意数量的参数。  

```go
package main

import "fmt"

func main() {
	var i interface{}
	describe(i)

	i = 42
	describe(i)

	i = "hello"
	describe(i)
}

func describe(i interface{}) {
	fmt.Printf("(%v, %T)\n", i, i)
}

```

## 类型断言

​    **类型断言** 提供了访问接口值底层具体值的方式。  

```
t := i.(T)
```

​    该语句断言接口值 `i` 保存了具体类型 `T`，并将其底层类型为 `T` 的值赋予变量 `t`。  

​    若 `i` 并未保存 `T` 类型的值，该语句就会触发一个恐慌。  

​    为了 **判断** 一个接口值是否保存了一个特定的类型，类型断言可返回两个值：其底层值以及一个报告断言是否成功的布尔值。  

```
t, ok := i.(T)
```

​    若 `i` 保存了一个 `T`，那么 `t` 将会是其底层值，而 `ok` 为 `true`。  

​    否则，`ok` 将为 `false` 而 `t` 将为 `T` 类型的零值，程序并不会产生恐慌。  

​    请注意这种语法和读取一个映射时的相同之处。  

```go
package main

import "fmt"

func main() {
	var i interface{} = "hello"

	s := i.(string)
	fmt.Println(s)

	s, ok := i.(string)
	fmt.Println(s, ok)

	f, ok := i.(float64)
	fmt.Println(f, ok)

	f = i.(float64) // 报错(panic)
	fmt.Println(f)
}
```

## 类型选择

​    **类型选择** 是一种按顺序从几个类型断言中选择分支的结构。  

​    类型选择与一般的 switch 语句相似，不过类型选择中的 case 为类型（而非值），     它们针对给定接口值所存储的值的类型进行比较。  

```
switch v := i.(type) {
case T:
    // v 的类型为 T
case S:
    // v 的类型为 S
default:
    // 没有匹配，v 与 i 的类型相同
}
```

​    类型选择中的声明与类型断言 `i.(T)` 的语法相同，只是具体类型 `T` 被替换成了关键字 `type`。  

​    此选择语句判断接口值 `i` 保存的值类型是 `T` 还是 `S`。在 `T` 或 `S` 的情况下，变量 `v` 会分别按 `T` 或 `S` 类型保存 `i` 拥有的值。在默认（即没有匹配）的情况下，变量 `v` 与 `i` 的接口类型和值相同。  

```go
package main

import "fmt"

func do(i interface{}) {
	switch v := i.(type) {
	case int:
		fmt.Printf("Twice %v is %v\n", v, v*2)
	case string:
		fmt.Printf("%q is %v bytes long\n", v, len(v))
	default:
		fmt.Printf("I don't know about type %T!\n", v)
	}
}

func main() {
	do(21)
	do("hello")
	do(true)
}
```



## Stringer

​    [`fmt`](https://go-zh.org/pkg/fmt/) 包中定义的 [`Stringer`](https://go-zh.org/pkg/fmt/#Stringer) 是最普遍的接口之一。  

```
type Stringer interface {
    String() string
}
```

​    `Stringer` 是一个可以用字符串描述自己的类型。`fmt` 包（还有很多包）都通过此接口来打印值。  

​	PS:Person Struct implements interface Stringer, fmt.Println will auto invoke implements method String(), like toString() in Java

```go
package main

import "fmt"

type Person struct {
	Name string
	Age  int
}

func (p Person) String() string {
	return fmt.Sprintf("%v (%v years)", p.Name, p.Age)
}

func main() {
	a := Person{"Arthur Dent", 42}
	z := Person{"Zaphod Beeblebrox", 9001}
	fmt.Println(a, z)
}
```



## 信道

​    信道是带有类型的管道，你可以通过它用信道操作符 `<-` 来发送或者接收值。  

```
ch <- v    // 将 v 发送至信道 ch。
v := <-ch  // 从 ch 接收值并赋予 v。
```

​    （“箭头”就是数据流的方向。）  

​    和映射与切片一样，信道在使用前必须创建：  

```
ch := make(chan int)
```

​    默认情况下，发送和接收操作在另一端准备好之前都会阻塞。这使得 Go 程可以在没有显式的锁或竞态变量的情况下进行同步。  

​    以下示例对切片中的数进行求和，将任务分配给两个 Go 程。一旦两个 Go 程完成了它们的计算，它就能算出最终的结果。  

```go
package main

import "fmt"

func sum(s []int, c chan int) {
	sum := 0
	for _, v := range s {
		sum += v
	}
	c <- sum // 将和送入 c
}

func main() {
	s := []int{7, 2, 8, -9, 4, 0}

	c := make(chan int)
	go sum(s[:len(s)/2], c)
	go sum(s[len(s)/2:], c)
	x, y := <-c, <-c // 从 c 中接收

	fmt.Println(x, y, x+y)
}
```



## 带缓冲的信道

​    信道可以是 *带缓冲的*。将缓冲长度作为第二个参数提供给 `make` 来初始化一个带缓冲的信道：  

```
ch := make(chan int, 100)
```

​    仅当信道的缓冲区填满后，向其发送数据时才会阻塞。当缓冲区为空时，接受方会阻塞。  



```go
package main

import "fmt"

func main() {
	ch := make(chan int, 2)
	ch <- 1
	ch <- 2
	fmt.Println(<-ch)
	fmt.Println(<-ch)
}

```



## range 和 close

​    发送者可通过 `close` 关闭一个信道来表示没有需要发送的值了。接收者可以通过为接收表达式分配第二个参数来测试信道是否被关闭：若没有值可以接收且信道已被关闭，那么在执行完  

```
v, ok := <-ch
```

​    之后 `ok` 会被设置为 `false`。  

​    循环 `for i := range c` 会不断从信道接收值，直到它被关闭。  

​    *注意：* 只有发送者才能关闭信道，而接收者不能。向一个已经关闭的信道发送数据会引发程序恐慌（panic）。  

​    *还要注意：* 信道与文件不同，通常情况下无需关闭它们。只有在必须告诉接收者不再有需要发送的值时才有必要关闭，例如终止一个 `range` 循环。  