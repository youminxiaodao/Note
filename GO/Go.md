## 远程包

像Git或Mercurial这样的版本控制系统，可根据导入路径的描述来获取包源代码。`go` 工具可通过此特性来从远程代码库自动获取包。例如，本文档中描述的例子也可存放到Google Code上的Mercurial仓库 `code.google.com/p/go.example` 中，若你在包的导入路径中包含了代码仓库的URL，`go get` 就会自动地获取、 构建并安装它：

```
$ go get github.com/golang/example/hello
$ $GOPATH/bin/hello
Hello, Go examples!
```

若指定的包不在工作空间中，`go get` 就会将会将它放到 `GOPATH` 指定的第一个工作空间内。（若该包已存在，`go get` 就会跳过远程获取， 其行为与 `go install` 相同）