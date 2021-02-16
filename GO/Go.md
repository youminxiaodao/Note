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