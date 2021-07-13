# CentOS升级Vim7到Vim8

## 安装python3

```bash
sudo yum install -y git gcc-c++ ncurses-devel python-devel cmake wget make
sudo yum install -y python36 python36-devel
```



## 克隆vim项目

```bash
// 克隆项目到本地
// 假定是在~目录下克隆，实际可变更位置。
cd ~
git clone https://github.com/vim/vim.git
```

## 编译并配置Vim

```bash
// 进入项目
cd vim

// 配置参数
./configure --prefix=/usr/local/vim  --enable-pythoninterp=yes --enable-python3interp=yes --with-python-command=python --with-python3-command=python36

// 编译文件，可能需要使用sudo权限
sudo make
sudo make install

// 编译成功后，vim/src/目录下，会有 vim 文件，后面会用到
ls ~/vim/src -al
```

## 复制Vim到系统配置，并修改Profile

```bash
// 复制前可以备份一下
sudo cp /usr/bin/vim /usr/bin/vim.backup

// 复制前面编译的vim到系统配置
cd ~/vim/src
sudo cp vim /usr/bin

// 修改系统的Profile, 如果sudo权限不够，则可以尝试使用root用户，我是用的root用户。
su  // 输入root密码，如果还没有设置，则输入 `sudo passwd` 设置root密码。
echo "PATH=\$PATH:/usr/local/vim/bin" >> /etc/profile

// 运行刚修改的配置。
source /etc/profile

// 最后，check version
vim --version
// 同时，也能看到它支持Python3了。(7.4是不支持的。)
```