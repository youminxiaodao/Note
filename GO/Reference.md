

## [Go's Declaration Syntax](https://blog.go-zh.org/gos-declaration-syntax)

2010/07/07

#### Introduction

​    Newcomers to Go wonder why the declaration syntax is different from  the tradition established in the C family. In this post we'll compare  the two approaches and explain why Go's declarations look as they do.  

#### C syntax

​    First, let's talk about C syntax. C took an unusual and clever  approach to declaration syntax. Instead of describing the types with  special syntax, one writes an expression involving the item being  declared, and states what type that expression will have. Thus  

```
int x;
```

​    declares x to be an int: the expression 'x' will have type int. In  general, to figure out how to write the type of a new variable, write an expression involving that variable that evaluates to a basic type, then put the basic type on the left and the expression on the right.  

​    Thus, the declarations  

```
int *p;
int a[3];
```

​    state that p is a pointer to int because '*p' has type int, and that a is an array of ints because a[3] (ignoring the particular index  value, which is punned to be the size of the array) has type int.  

​    What about functions? Originally, C's function declarations wrote the types of the arguments outside the parens, like this:  

```
int main(argc, argv)
    int argc;
    char *argv[];
{ /* ... */ }
```

​    Again, we see that main is a function because the expression main(argc, argv) returns an int. In modern notation we'd write  

```
int main(int argc, char *argv[]) { /* ... */ }
```

​    but the basic structure is the same.  

​    This is a clever syntactic idea that works well for simple types but can get confusing fast. The famous example is declaring a function  pointer. Follow the rules and you get this:  

```
int (*fp)(int a, int b);
```

​    Here, fp is a pointer to a function because if you write the  expression (*fp)(a, b) you'll call a function that returns int. What if  one of fp's arguments is itself a function?  

```
int (*fp)(int (*ff)(int x, int y), int b)
```

​    That's starting to get hard to read.  

​    Of course, we can leave out the name of the parameters when we declare a function, so main can be declared  

```
int main(int, char *[])
```

​    Recall that argv is declared like this,  

```
char *argv[]
```

​    so you drop the name from the middle of its declaration to construct its type. It's not obvious, though, that you declare something of type  char *[] by putting its name in the middle.  

​    And look what happens to fp's declaration if you don't name the parameters:  

```
int (*fp)(int (*)(int, int), int)
```

​    Not only is it not obvious where to put the name inside  

```
int (*)(int, int)
```

​    it's not exactly clear that it's a function pointer declaration at all. And what if the return type is a function pointer?  

```
int (*(*fp)(int (*)(int, int), int))(int, int)
```

​    It's hard even to see that this declaration is about fp.  

​    You can construct more elaborate examples but these should  illustrate some of the difficulties that C's declaration syntax can  introduce.  

​    There's one more point that needs to be made, though. Because type  and declaration syntax are the same, it can be difficult to parse  expressions with types in the middle. This is why, for instance, C casts always parenthesize the type, as in  

```
(int)M_PI
```

#### Go syntax

​    Languages outside the C family usually use a distinct type syntax in declarations. Although it's a separate point, the name usually comes  first, often followed by a colon. Thus our examples above become  something like (in a fictional but illustrative language)  

```
x: int
p: pointer to int
a: array[3] of int
```

​    These declarations are clear, if verbose - you just read them left  to right. Go takes its cue from here, but in the interests of brevity it drops the colon and removes some of the keywords:  

```
x int
p *int
a [3]int
```

​    There is no direct correspondence between the look of [3]int and how to use a in an expression. (We'll come back to pointers in the next  section.) You gain clarity at the cost of a separate syntax.  

​    Now consider functions. Let's transcribe the declaration for main as it would read in Go, although the real main function in Go takes no  arguments:  

```
func main(argc int, argv []string) int
```

​    Superficially that's not much different from C, other than the change from `char` arrays to strings, but it reads well from left to right:  

​    function main takes an int and a slice of strings and returns an int.  

​    Drop the parameter names and it's just as clear - they're always first so there's no confusion.  

```
func main(int, []string) int
```

​    One merit of this left-to-right style is how well it works as the  types become more complex. Here's a declaration of a function variable  (analogous to a function pointer in C):  

```
f func(func(int,int) int, int) int
```

​    Or if f returns a function:  

```
f func(func(int,int) int, int) func(int, int) int
```

​    It still reads clearly, from left to right, and it's always obvious which name is being declared - the name comes first.  

​    The distinction between type and expression syntax makes it easy to write and invoke closures in Go:  

```
sum := func(a, b int) int { return a+b } (3, 4)
```

#### Pointers

​    Pointers are the exception that proves the rule. Notice that in  arrays and slices, for instance, Go's type syntax puts the brackets on  the left of the type but the expression syntax puts them on the right of the expression:  

```
var a []int
x = a[1]
```

​    For familiarity, Go's pointers use the * notation from C, but we  could not bring ourselves to make a similar reversal for pointer types.  Thus pointers work like this  

```
var p *int
x = *p
```

​    We couldn't say  

```
var p *int
x = p*
```

​    because that postfix * would conflate with multiplication. We could have used the Pascal ^, for example:  

```
var p ^int
x = p^
```

​    and perhaps we should have (and chosen another operator for xor),  because the prefix asterisk on both types and expressions complicates  things in a number of ways. For instance, although one can write  

```
[]int("hi")
```

​    as a conversion, one must parenthesize the type if it starts with a *:  

```
(*int)(nil)
```

​    Had we been willing to give up * as pointer syntax, those parentheses would be unnecessary.  

​    So Go's pointer syntax is tied to the familiar C form, but those  ties mean that we cannot break completely from using parentheses to  disambiguate types and expressions in the grammar.  

​    Overall, though, we believe Go's type syntax is easier to understand than C's, especially when things get complicated.  

#### Notes

​    Go's declarations read left to right. It's been pointed out that C's read in a spiral! See [ The "Clockwise/Spiral Rule"](http://c-faq.com/decl/spiral.anderson.html) by David Anderson.  

Rob Pike 编写



### [Inside the Go Playground](https://blog.go-zh.org/playground)

2013/12/12

#### Introduction

​    In September 2010 we [introduced the Go Playground](https://blog.golang.org/introducing-go-playground),     a web service that compiles and executes arbitrary Go code and returns the     program output.  

​    If you're a Go programmer then you have probably already used the playground     by using the [Go Playground](https://play.golang.org) directly,     taking the [Go Tour](https://tour.golang.org),     or running [executable examples](https://golang.org/pkg/strings/#pkg-examples)     from the Go documentation.  

​    You may also have used it by clicking one of the "Run" buttons in a slide     deck on [talks.golang.org](https://talks.golang.org/) or a post on this     very blog     (such as the [recent article on Strings](https://blog.golang.org/strings)).  

​    In this article we will take a look at how the playground is implemented     and integrated with these services.     The implementation involves a variant operating system environment and runtime     and our description here assumes you have some familiarity with systems     programming using Go.  

#### Overview

  ![img](https://blog.go-zh.org/playground/overview.png)

​    The playground service has three parts:  

- A back end that runs on Google's servers. It receives RPC  requests, compiles the user program using the gc tool chain, executes  the user program, and returns the program output (or compilation errors) as the RPC response.
- A front end that runs on [Google App Engine](https://cloud.google.com/appengine/docs/go/). It receives HTTP requests from the client and makes corresponding RPC requests to the back end. It also does some caching.
- A JavaScript client that implements the user interface and makes HTTP requests to the front end.

#### The back end

​    The back end program itself is trivial, so we won't discuss its implementation     here. The interesting part is how we safely execute arbitrary user code in a     secure environment while still providing core functionality such as time, the     network, and the file system.  

​    To isolate user programs from Google's infrastructure, the back end runs     them under [Native Client](https://developers.google.com/native-client/)     (or "NaCl"), a technology developed by Google to permit the safe execution of     x86 programs inside web browsers. The back end uses a special version of the gc     tool chain that generates NaCl executables.  

​    (This special tool chain was merged into Go 1.3.     To learn more, read the [design document](https://golang.org/s/go13nacl).)  

​    NaCl limits the amount of CPU and RAM a program may consume, and it prevents     programs from accessing the network or file system.     This presents a problem, however.     Go's concurrency and networking support are among its key strengths,     and access to the file system is vital for many programs.     To demonstrate concurrency effectively we need time, and to demonstrate     networking and the file system we obviously need a network and a file system.  

​    Although all these things are supported today, the first version of the     playground, launched in 2010, had none of them.     The current time was fixed at 10 November 2009, `time.Sleep` had no effect,     and most functions of the `os` and `net` packages were stubbed out to     return an `EINVALID` error.  

​    A year ago we     [implemented fake time](https://groups.google.com/d/msg/golang-nuts/JBsCrDEVyVE/30MaQsiQcWoJ)     in the playground, so that programs that sleep would behave correctly.     A more recent update to the playground introduced a fake network stack and a     fake file system, making the playground's tool chain similar to a normal     Go tool chain.     These facilities are described in the following sections.  

#### Faking time

​    Playground programs are limited in the amount of CPU time and memory they can     use, but they are also restricted in how much real time they can use.     This is because each running program consumes resources on the back end     and any stateful infrastructure between it and the client.     Limiting the run time of each playground program makes our service more     predictable and defends us against denial of service attacks.  

​    But these restrictions become stifling when running code that uses time.     The [Go Concurrency Patterns](https://talks.golang.org/2012/concurrency.slide)     talk demonstrates concurrency with examples that use timing functions like     [`time.Sleep`](https://golang.org/pkg/time/#Sleep) and     [`time.After`](https://golang.org/pkg/time/#After).     When run under early versions of the playground, these programs' sleeps would     have no effect and their behavior would be strange (and sometimes wrong).  

​    By using a clever trick we can make a Go program *think* that it is sleeping,     when really the sleeps take no time at all.     To explain the trick we first need to understand how the scheduler manages     sleeping goroutines.  

​    When a goroutine calls `time.Sleep` (or similar) the scheduler adds a timer to     a heap of pending timers and puts the goroutine to sleep.     Meanwhile, a special timer goroutine manages that heap.     When the timer goroutine starts it tells the scheduler to wake     it when the next pending timer is ready to fire and then sleeps.     When it wakes up it checks which timers have expired, wakes the appropriate     goroutines, and goes back to sleep.  

​    The trick is to change the condition that wakes the timer goroutine.     Instead of waking it after a specific time period, we modify the scheduler to     wait for a deadlock; the state where all goroutines are blocked.  

​    The playground version of the runtime maintains its own internal clock. When     the modified scheduler detects a deadlock it checks whether any timers are     pending. If so, it advances the internal clock to the trigger time of the     earliest timer and then wakes the timer goroutine. Execution continues and the     program believes that time has passed, when in fact the sleep was nearly     instantaneous.  

​    These changes to the scheduler can be found in [`proc.c`](https://golang.org/cl/73110043) and [`time.goc`](https://golang.org/cl/73110043).  

​    Fake time fixes the issue of resource exhaustion on the back end, but what     about the program output? It would be odd to see a program that sleeps run to     completion correctly without taking any time.  

​    The following program prints the current time each second and then exits after     three seconds. Try running it.  

```
func main() {
    stop := time.After(3 * time.Second)
    tick := time.NewTicker(1 * time.Second)
    defer tick.Stop()
    for {
        select {
        case <-tick.C:
            fmt.Println(time.Now())
        case <-stop:
            return
        }
    }
}
```

​    How does this work? It is a collaboration between the back end, front end, and client.  

​    We capture the timing of each write to standard output and standard error and     provide it to the client. Then the client can "play back" the writes with the     correct timing, so that the output appears just as if the program were running     locally.  

​    The playground's `runtime` package provides a special     [`write` function](https://github.com/golang/go/blob/go1.3/src/pkg/runtime/sys_nacl_amd64p32.s#L54)     that includes a small "playback header" before each write.     The playback header comprises a magic string, the current time, and the     length of the write data. A write with a playback header has this structure:  

```
0 0 P B <8-byte time> <4-byte data length> <data>
```

​    The raw output of the program above looks like this:  

```
\x00\x00PB\x11\x74\xef\xed\xe6\xb3\x2a\x00\x00\x00\x00\x1e2009-11-10 23:00:01 +0000 UTC
\x00\x00PB\x11\x74\xef\xee\x22\x4d\xf4\x00\x00\x00\x00\x1e2009-11-10 23:00:02 +0000 UTC
\x00\x00PB\x11\x74\xef\xee\x5d\xe8\xbe\x00\x00\x00\x00\x1e2009-11-10 23:00:03 +0000 UTC
```

​    The front end parses this output as a series of events     and returns a list of events to the client as a JSON object:  

```
{
    "Errors": "",
    "Events": [
        {
            "Delay": 1000000000,
            "Message": "2009-11-10 23:00:01 +0000 UTC\n"
        },
        {
            "Delay": 1000000000,
            "Message": "2009-11-10 23:00:02 +0000 UTC\n"
        },
        {
            "Delay": 1000000000,
            "Message": "2009-11-10 23:00:03 +0000 UTC\n"
        }
    ]
}
```

​    The JavaScript client (running in the user's web browser) then plays back the     events using the provided delay intervals.     To the user it appears that the program is running in real time.  

#### Faking the file system

​    Programs built with the Go's NaCl tool chain cannot access the local machine's     file system. Instead, the `syscall` package's file-related functions     (`Open`, `Read`, `Write`, and so on) operate on an in-memory file system     that is implemented by the `syscall` package itself.     Since package `syscall` is the interface between the Go code and the operating     system kernel, user programs see the file system exactly the same way as they     would a real one.  

​    The following example program writes data to a file, and then copies     its contents to standard output. Try running it. (You can edit it, too!)  

```
func main() {
    const filename = "/tmp/file.txt"

    err := ioutil.WriteFile(filename, []byte("Hello, file system\n"), 0644)
    if err != nil {
        log.Fatal(err)
    }

    b, err := ioutil.ReadFile(filename)
    if err != nil {
        log.Fatal(err)
    }

    fmt.Printf("%s", b)
}
```

​    When a process starts, the file system is populated with some devices under     `/dev` and an empty `/tmp` directory. The program can manipulate the file     system as usual, but when the process exits any changes to the file system are     lost.  

​    There is also a provision to load a zip file into the file system at init time     (see [`unzip_nacl.go`](https://github.com/golang/go/blob/go1.3/src/pkg/syscall/unzip_nacl.go)).     So far we have only used the unzip facility to provide the data files required     to run the standard library tests, but we intend to provide playground programs     with a set of files that can be used in documentation examples, blog posts, and     the Go Tour.  

​    The implementation can be found in the     [`fs_nacl.go`](https://github.com/golang/go/blob/master/src/syscall/fs_nacl.go) and     [`fd_nacl.go`](https://github.com/golang/go/blob/master/src/syscall/fd_nacl.go) files     (which, by virtue of their `_nacl` suffix, are built into package `syscall` only     when `GOOS` is set to `nacl`).  

​    The file system itself is represented by the     [`fsys` struct](https://github.com/golang/go/blob/master/src/syscall/fs_nacl.go#L26),     of which a global instance (named `fs`) is created during init time.     The various file-related functions then operate on `fs` instead of making the     actual system call.     For instance, here is the [`syscall.Open`](https://github.com/golang/go/blob/master/src/syscall/fs_nacl.go#L473) function:  

```
func Open(path string, openmode int, perm uint32) (fd int, err error) {
    fs.mu.Lock()
    defer fs.mu.Unlock()
    f, err := fs.open(path, openmode, perm&0777|S_IFREG)
    if err != nil {
        return -1, err
    }
    return newFD(f), nil
}
```

​    File descriptors are tracked by a global slice named     [`files`](https://github.com/golang/go/blob/master/src/syscall/fd_nacl.go#L17).     Each file descriptor corresponds to a [`file`](https://github.com/golang/go/blob/master/src/syscall/fd_nacl.go#L23)     and each `file` provides a value that implements the [`fileImpl`](https://github.com/golang/go/blob/master/src/syscall/fd_nacl.go#L30) interface.     There are several implementations of the interface:  

- regular files and devices (such as `/dev/random`) are represented by [`fsysFile`](https://github.com/golang/go/blob/master/src/syscall/fs_nacl.go#L58),
- standard input, output, and error are instances of [`naclFile`](https://github.com/golang/go/blob/master/src/syscall/fd_nacl.go#L216), which uses system calls to interact with the actual files (these are a  playground program's only way to interact with the outside world),
- network sockets have their own implementation, discussed in the next section.

#### Faking the network

​    Like the file system, the playground's network stack is an in-process fake     implemented by the `syscall` package. It permits playground projects to use     the loopback interface (`127.0.0.1`). Requests to other hosts will fail.  

​    For an executable example, run the following program. It listens on a TCP port,     waits for an incoming connection, copies the data from that connection to     standard output, and exits. In another goroutine, it makes a connection to the     listening port, writes a string to the connection, and closes it.  

```
func main() {
    l, err := net.Listen("tcp", "127.0.0.1:4000")
    if err != nil {
        log.Fatal(err)
    }
    defer l.Close()

    go dial()

    c, err := l.Accept()
    if err != nil {
        log.Fatal(err)
    }
    defer c.Close()

    io.Copy(os.Stdout, c)
}

func dial() {
    c, err := net.Dial("tcp", "127.0.0.1:4000")
    if err != nil {
        log.Fatal(err)
    }
    defer c.Close()
    c.Write([]byte("Hello, network\n"))
}
```

​    The interface to the network is more complex than the one for files, so the     implementation of the fake network is larger and more complex than the fake     file system. It must simulate read and write timeouts, different address types     and protocols, and so on.  

​    The implementation can be found in [`net_nacl.go`](https://github.com/golang/go/blob/master/src/syscall/net_nacl.go).     A good place to start reading is [`netFile`](https://github.com/golang/go/blob/master/src/syscall/net_nacl.go#L461), the network socket implementation of the `fileImpl` interface.  

#### The front end

​    The playground front end is another simple program (shorter than 100 lines).     It receives HTTP requests from the client, makes RPC requests to the back end,     and does some caching.  

​    The front end serves an HTTP handler at `https://golang.org/compile`.     The handler expects a POST request with a `body` field     (the Go program to run) and an optional `version` field     (for most clients this should be `"2"`).  

​    When the front end receives a compilation request it first checks     [memcache](https://developers.google.com/appengine/docs/memcache/)     to see if it has cached the results of a previous compilation of that source.     If found, it returns the cached response.     The cache prevents popular programs such as those on the     [Go home page](https://golang.org/) from overloading the back ends.     If there is no cached response, the front end makes an RPC request to the back     end, stores the response in memcache, parses the playback events, and returns     a JSON object to the client as the HTTP response (as described above).  

#### The client

​    The various sites that use the playground each share some common JavaScript     code for setting up the user interface (the code and output boxes, the run     button, and so on) and communicating with the playground front end.  

​    This implementation is in the file     [`playground.js`](https://github.com/golang/tools/blob/master/godoc/static/playground.js)     in the `go.tools` repository, which can be imported from the     [`golang.org/x/tools/godoc/static`](https://godoc.org/golang.org/x/tools/godoc/static) package.     Some of it is clean and some is a bit crufty, as it is the result of     consolidating several divergent implementations of the client code.  

​    The [`playground`](https://github.com/golang/tools/blob/master/godoc/static/playground.js#L227)     function takes some HTML elements and turns them into an interactive     playground widget. You should use this function if you want to put the     playground on your own site (see 'Other clients' below).  

​    The [`Transport`](https://github.com/golang/tools/blob/master/godoc/static/playground.js#L6)     interface (not formally defined, this being JavaScript)     abstracts the user interface from the means of talking to the web front end.     [`HTTPTransport`](https://github.com/golang/tools/blob/master/godoc/static/playground.js#L43)     is an implementation of `Transport` that speaks the HTTP-based protocol     described earlier.     [`SocketTransport`](https://github.com/golang/tools/blob/master/godoc/static/playground.js#L115)     is another implementation that speaks WebSocket (see 'Playing offline' below).  

​    To comply with the [same-origin policy](https://en.wikipedia.org/wiki/Same-origin_policy),     the various web servers (godoc, for instance) proxy requests to     `/compile` through to the playground service at `https://golang.org/compile`.     The common [`golang.org/x/tools/playground`](https://godoc.org/golang.org/x/tools/playground)     package does this proxying.  

#### Playing offline

​    Both the [Go Tour](https://tour.golang.org) and the     [Present Tool](https://godoc.org/golang.org/x/tools/present) can be     run offline. This is great for people with limited internet connectivity     or presenters at conferences who cannot (and *should* not) rely on a working     internet connection.  

​    To run offline, the tools run their own version of the playground back end on     the local machine. The back end uses a regular Go tool chain with none of the     aforementioned modifications and uses a WebSocket to communicate with the     client.  

​    The WebSocket back end implementation can be found in the     [`golang.org/x/tools/playground/socket`](https://godoc.org/golang.org/x/tools/playground/socket) package.     The [Inside Present](https://talks.golang.org/2012/insidepresent.slide#1) talk discusses this code in detail.  

#### Other clients

​    The playground service is used by more than just the official Go project     ([Go by Example](https://gobyexample.com/) is one other instance)     and we are happy for you to use it on your own site. All we ask is that     you [contact us first](mailto:golang-dev@googlegroups.com),     use a unique user agent in your requests (so we can identify you), and that     your service is of benefit to the Go community.  

#### Conclusion

​    From godoc to the tour to this very blog, the playground has become an     essential part of our Go documentation story. With the recent additions     of the fake file system and network stack we are excited to expand     our learning materials to cover those areas.  

​    But, ultimately, the playground is just the tip of the iceberg.     With Native Client support scheduled for Go 1.3,     we look forward to seeing what the community can do with it.  

​    *This article is part 12 of the*     [Go Advent Calendar](https://blog.gopheracademy.com/go-advent-2013),     *a series of daily blog posts throughout December .*  

Andrew Gerrand 编写



### [Defer, Panic, and Recover](https://blog.go-zh.org/defer-panic-and-recover)

2010/08/04

​    Go has the usual mechanisms for control flow: if, for, switch, goto.  It also has the go statement to run code in a separate goroutine.   Here I'd like to discuss some of the less common ones: defer, panic, and recover.  

​    A **defer statement** pushes a function call onto a list. The  list of saved calls is executed after the surrounding function returns.  Defer is commonly used to simplify functions that perform various  clean-up actions.  

​    For example, let's look at a function that opens two files and copies the contents of one file to the other:  

```
func CopyFile(dstName, srcName string) (written int64, err error) {
    src, err := os.Open(srcName)
    if err != nil {
        return
    }

    dst, err := os.Create(dstName)
    if err != nil {
        return
    }

    written, err = io.Copy(dst, src)
    dst.Close()
    src.Close()
    return
}
```

​    This works, but there is a bug. If the call to os.Create fails, the  function will return without closing the source file. This can be easily remedied by putting a call to src.Close before the second return  statement, but if the function were more complex the problem might not  be so easily noticed and resolved. By introducing defer statements we  can ensure that the files are always closed:  

```
func CopyFile(dstName, srcName string) (written int64, err error) {
    src, err := os.Open(srcName)
    if err != nil {
        return
    }
    defer src.Close()

    dst, err := os.Create(dstName)
    if err != nil {
        return
    }
    defer dst.Close()

    return io.Copy(dst, src)
}
```

​    Defer statements allow us to think about closing each file right  after opening it, guaranteeing that, regardless of the number of return  statements in the function, the files *will* be closed.  

​    The behavior of defer statements is straightforward and predictable. There are three simple rules:  

​    \1. *A deferred function's arguments are evaluated when the defer statement is evaluated.*  

​    In this example, the expression "i" is evaluated when the Println  call is deferred. The deferred call will print "0" after the function  returns.  

```
func a() {
    i := 0
    defer fmt.Println(i)
    i++
    return
}
```

​    \2. *Deferred function calls are executed in Last In First Out order after the surrounding function returns.*  

​    This function prints "3210":  

```
func b() {
    for i := 0; i < 4; i++ {
        defer fmt.Print(i)
    }
}
```

​    \3. *Deferred functions may read and assign to the returning function's named return values.*  

​    In this example, a deferred function increments the return value i *after* the surrounding function returns. Thus, this function returns 2:  

```
func c() (i int) {
    defer func() { i++ }()
    return 1
}
```

​    This is convenient for modifying the error return value of a function; we will see an example of this shortly.  

​    **Panic** is a built-in function that stops the ordinary flow of control and begins *panicking*. When the function F calls panic, execution of F stops, any deferred  functions in F are executed normally, and then F returns to its caller.  To the caller, F then behaves like a call to panic. The process  continues up the stack until all functions in the current goroutine have returned, at which point the program crashes. Panics can be initiated  by invoking panic directly. They can also be caused by runtime errors,  such as out-of-bounds array accesses.  

​    **Recover** is a built-in function that regains control of a  panicking goroutine. Recover is only useful inside deferred functions.  During normal execution, a call to recover will return nil and have no  other effect. If the current goroutine is panicking, a call to recover  will capture the value given to panic and resume normal execution.  

​    Here's an example program that demonstrates the mechanics of panic and defer:  

```
package main

import "fmt"

func main() {
    f()
    fmt.Println("Returned normally from f.")
}

func f() {
    defer func() {
        if r := recover(); r != nil {
            fmt.Println("Recovered in f", r)
        }
    }()
    fmt.Println("Calling g.")
    g(0)
    fmt.Println("Returned normally from g.")
}

func g(i int) {
    if i > 3 {
        fmt.Println("Panicking!")
        panic(fmt.Sprintf("%v", i))
    }
    defer fmt.Println("Defer in g", i)
    fmt.Println("Printing in g", i)
    g(i + 1)
}
```

​    The function g takes the int i, and panics if i is greater than 3,  or else it calls itself with the argument i+1. The function f defers a  function that calls recover and prints the recovered value (if it is  non-nil). Try to picture what the output of this program might be before reading on.  

​    The program will output:  

```
Calling g.
Printing in g 0
Printing in g 1
Printing in g 2
Printing in g 3
Panicking!
Defer in g 3
Defer in g 2
Defer in g 1
Defer in g 0
Recovered in f 4
Returned normally from f.
```

​    If we remove the deferred function from f the panic is not recovered and reaches the top of the goroutine's call stack, terminating the  program. This modified program will output:  

```
Calling g.
Printing in g 0
Printing in g 1
Printing in g 2
Printing in g 3
Panicking!
Defer in g 3
Defer in g 2
Defer in g 1
Defer in g 0
panic: 4
 
panic PC=0x2a9cd8
[stack trace omitted]
```

​    For a real-world example of **panic** and **recover**, see the [json package](https://golang.org/pkg/encoding/json/) from the Go standard library. It decodes JSON-encoded data with a set  of recursive functions. When malformed JSON is encountered, the parser  calls panic to unwind the stack to the top-level function call, which  recovers from the panic and returns an appropriate error value (see the  'error' and 'unmarshal' methods of the decodeState type in [decode.go](https://golang.org/src/pkg/encoding/json/decode.go)).  

​    The convention in the Go libraries is that even when a package uses  panic internally, its external API still presents explicit error return  values.  

​    Other uses of **defer** (beyond the file.Close example given earlier) include releasing a mutex:  

```
mu.Lock()
defer mu.Unlock()
```

​    printing a footer:  

```
printHeader()
defer printFooter()
```

​    and more.  

​    In summary, the defer statement (with or without panic and recover)  provides an unusual and powerful mechanism for control flow.  It can be  used to model a number of features implemented by special-purpose  structures in other programming languages. Try it out.  

Andrew Gerrand 编写