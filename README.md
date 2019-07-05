# docker-k8s
## 搭建简单的Spring-boot框架
我是使用的intelliJ来搭建的Springboot框架，首先下载intelliJ以及Java最新的JDK
<br>新建一个spring项目,点击坐上角file --->选择new --->点击project...
<br>点击左边Spring Initializr ---> 右上角新建jdk,选中我们刚安装好的JDK,点击next 如下图所示：
<br>![Alt text](http://images2015.cnblogs.com/blog/1124684/201704/1124684-20170401171602149-1644892820.png)
<br>看需求修改下图中的信息后点击next（可以直接使用默认）
<br>![Alt text](http://images2015.cnblogs.com/blog/1124684/201704/1124684-20170401172307477-1233963453.png)
<br>点击左边的Web ---> 选中中间列的Web ---> 点击next 如下图所示：
<br>![Alt text](http://images2015.cnblogs.com/blog/1124684/201704/1124684-20170401174429680-340954316.png)
<br>输入项目名称和保存路径 --- >点击finish
<br>![Alt text](http://images2015.cnblogs.com/blog/1124684/201704/1124684-20170401174651352-2074169693.png)
<br>可以将下面这三个无关的文件删除，干净结构
<br>![Alt text](http://images2015.cnblogs.com/blog/1124684/201704/1124684-20170401175723695-1916884097.png)
<br>创建的项目如下图所示：
<br>![Alt text](http://images2015.cnblogs.com/blog/1124684/201704/1124684-20170401175812945-76013544.png)
<br>创建HelloController类，代码如下所示：
```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class HelloController {
    @RequestMapping("/")
    @ResponseBody
    public String index(){
        return "Hello World!";
    }
    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String say(){
        return "hi";
    }
}
```
执行DemoApplication中main方法后访问http://localhost:8080/hello 就可以访问到我们的 “hi”
<br>
## 打jar包准备做镜像
在项目上鼠标右键 --> Open Module Settings
<br>![Alt text](https://images2015.cnblogs.com/blog/998529/201609/998529-20160929164617969-1066189780.png)
<br>Artifacts --> + --> JAR --> From modules with dependencies...
<br>![Alt text](https://images2015.cnblogs.com/blog/998529/201609/998529-20160929165026906-589079232.png)
<br>Main Class是你这个项目(脚本)的主方法,就是要运行的类,选一个
<br>设置 META-INF/MANIFEST.MF，设置完后就可以点OK了
<br>最后一步, Build Artifacts... --> demo2.jar --> Build
<br>build完之后就可以再out文件夹下找到打好的jar包
<br>
## jar包上传服务器
我是使用的scp来往Ubuntu server上上传文件的
<br>首先server端安装openssh-server
<br>```$ sudo apt-get install openssh-server```  
client上安装openssh-client
<br>```$ sudo apt-get install openssh-client```
<br>要从客户端把jar包上传到server上，执行以下命令
<br>```$ scp /home/reinhardt/demo/demo2.jar root@10.58.14.251:~/```
<br>输入server密码，传输成功

## 安装docker
### 卸载旧的或者已经安装Docker版本
旧的doceker或者已经安装过的一般叫docker或者docker-engine。 
卸载它们。
<br>
出现下面这样说明，已经卸载完了，系统里面没有了
<br>
```command
root@10.58.14.251:~#$ apt-get remove docker docker-engine docker.io
Reading package lists... Done
Building dependency tree   
Reading state information... Done
E: Unable to locate package docker
E: Unable to locate package docker-engine
E: Unable to locate package docker.io
E: Couldn't find any package by glob 'docker.io'
E: Couldn't find any package by regex 'docker.io'
root@10.58.14.251:~#
```
### 正式安装Docker
#### 使用 APT 安装
由于 apt 源使用 HTTPS 以确保软件下载过程中不被篡改。因此，我们首先需要添加使用 HTTPS 传输的软件包以及 CA 证书。
<br>```$ sudo apt-get update```
<br>
```
$ sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
```
为了确认所下载软件包的合法性，需要添加软件源的 GPG 密钥。
<br>```$ curl -fsSL https://mirrors.ustc.edu.cn/docker-ce/linux/ubuntu/gpg | sudo apt-key add -```
<br>然后，我们需要向 source.list 中添加 Docker 软件源
<br>
```
$ sudo add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) \
    stable"
```
更新 apt 软件包缓存，并安装 docker-ce：
<br>```$ sudo apt-get update```
```$ sudo apt-get install docker-ce```
### 测试 Docker 是否安装正确
```
$ docker run hello-world

Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
d1725b59e92d: Pull complete
Digest: sha256:0add3ace90ecb4adbf7777e9aacf18357296e799f81cabc9fde470971e499788
Status: Downloaded newer image for hello-world:latest

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (amd64)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://hub.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/get-started/
```
若能正常输出以上信息，则说明安装成功。

## docker部署springboot
编写一个简单的Dockerfile
```$ vim Dockerfile```
<br>如下所示：
```
FROM java:8-alpine
ADD demo2.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```
其中FROM后是一个已经包含了Java8 JDK的镜像，直接从docker hub里拉取
<br>ADD第一个参数为 JAR 包的名称， 第二个人参数自定义名称，打包后的名称
<br>EXPOSE是项目运行时的端口
<br>ENTRYPOINT是运行 JAR 包的指令， 如 java -jar app.jar 为 ["java","-jar","/app.jar"]
注意这里的 app.jar 和 ADD 的第二个参数对应
<br>通过dockerfile 文件生成本工程的镜像：
<br>```docker build -t gentle . ```
<br>此行命令最后有一个点，表示使用当前上下文中的 Dockerfile 文件
<br>docker images 查看已有镜像，docker build -t spring .  构建镜像，-t ：给镜像取名为 spring 。
<br>输入docker images得到如下输出：
```
root@reinhardt:~# docker images
REPOSITORY       TAG                 IMAGE ID            CREATED             SIZE
spring           latest              66ef80fc645d        16 hours ago        170MB
```
通过镜像启动容器，命令如下：
<br>```docker run -d -p 8080:8080 --name spring_test spring```
<br>-d 后台运行、
<br>最后一个 gentle 是引用的镜像的名字、
<br>--name jy_gentle 给容器取名为 jy_gentle （取名参数前面是两短横线）、
<br>-p 8089:8089 端口映射，注意是小写 p 
<br>前一个 8089 是对外浏览器上访问的端口，后一个 8089 是容器内工程本身的端口，两者可不一样
<br>docker ps -a ：查看当前服务器上所有容器。
<br>服务器开启后，通过浏览器访问 'http://10.58.14.251:8080/hello' 即可看到”hi“，说明docker运行spring成功

## 安装minikube
使用如下命令从官方下载最新的minicube到Ubuntu server上
```
curl -Lo minikube https://storage.googleapis.com/minikube/releases/v0.24.1/minikube-linux-amd64
```
遇到问题下载不了，Ubuntu server所在环境连接不到googleapis.com 所以改换策略，从本机虚拟机上下载需要文件，编成可执行文件上传服务器
```
curl -Lo minikube https://storage.googleapis.com/minikube/releases/v0.24.1/minikube-linux-amd64
chmod +x minikube
sudo mv minikube /home/reinhardt/demo/
scp /home/reinhardt/demo/minikube root@10.58.14.251:~/
```
在服务器上执行
```
mv minikube /usr/local/bin/
```
完成，接下来输入
```
minikube -h
```
报错
```
permission denied  
```
查阅资料发现权限不够，猜测因为是外来可执行文件所以不给执行
<br>输入命令
```
sudo chmod -R 777  /usr/local/bin/
```
问题得到解决
<br>下面使用非虚拟机命令启动minikube
<br>因为是在linux下所以可以没有虚拟机启动
```
sudo minikube start --vm-driver=none
```
然后minikube启动报错
```
 Error updating cluster:  Error updating localkube from uri: Error creating localkube asset from url: Error opening file asset: /root/.minikube/cache/localkube/localkube-v1.8.0: open /root/.minikube/cache/localkube/localkube-v1.8.0: no such file or directory
 ```
查阅资料是日志问题，输入
```
minikube config set WantReportErrorPrompt false
```
问题解决
<br>出现如下，表示minikube启动成功：
```
Starting local Kubernetes v1.8.0 cluster...
Starting VM...
Getting VM IP address...
Moving files into cluster...
Downloading localkube binary
 148.25 MB / 148.25 MB [============================================] 100.00% 0s
 0 B / 65 B [----------------------------------------------------------]   0.00%
 65 B / 65 B [======================================================] 100.00% 0sSetting up certs...
Connecting to cluster...
Setting up kubeconfig...
Starting cluster components...
Kubectl is now configured to use the cluster.
===================
WARNING: IT IS RECOMMENDED NOT TO RUN THE NONE DRIVER ON PERSONAL WORKSTATIONS
    The 'none' driver will run an insecure kubernetes apiserver as root that may leave the host vulnerable to CSRF attacks

When using the none driver, the kubectl config and credentials generated will be root owned and will appear in the root home directory.
You will need to move the files to the appropriate location and then set the correct permissions.  An example of this is below:

    sudo mv /root/.kube $HOME/.kube # this will write over any previous configuration
    sudo chown -R $USER $HOME/.kube
    sudo chgrp -R $USER $HOME/.kube

    sudo mv /root/.minikube $HOME/.minikube # this will write over any previous configuration
    sudo chown -R $USER $HOME/.minikube
    sudo chgrp -R $USER $HOME/.minikube

This can also be done automatically by setting the env var CHANGE_MINIKUBE_NONE_USER=true
Loading cached images from config file.
```

## 安装kubectl
同理，连接不上，使用本地虚拟机上传
```
curl -Lo kubectl https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
chmod +x kubectl
sudo mv kubrctl /home/reinhardt/demo/
scp /home/reinhardt/demo/kubectl root@10.58.14.251:~/
```
在服务器上执行
```
mv kubectl /usr/local/bin/
```
完成
<br>接下来输入
```
kubectl version
```
可以查看到自己的client版本是v1.15.0，然后server版本是v1.8.0
<br>这里问题就来了
如果版本相差太大
kubectl基本无法使用，
<br>输入
```
kubectl get pods
```
提示server和client通信有问题，就是因为版本问题
deployment也会提示有问题
<br>所以修改上文下载最新版kubectl，改为下载v1.8.0版本，重新安装一遍即可
```
curl -Lo kubectl https://storage.googleapis.com/kubernetes-release/release/v1.8.0/bin/linux/amd64/kubectl
```

## 将image通过minikube部署
通过image启动一个容器
```
sudo kubectl run spring-test --image=spring --port=8080
```
其中，spring-test是要定义的容器名称 spring:latest表明要用spring镜像 --port=8080表明容器对外暴露8080端口
<br>通过
```
kubectl get pods
```
查看当前pods，发现spring拉取失败，但是spring已经是本地镜像了（参考上文已经打包好spring镜像），所以加上--image-pull-policy=IfNotPresent，优先拉取本地镜像
```
sudo kubectl run spring-test --image=spring --port=8080 --image-pull-policy=IfNotPresent
```
稍等一分钟左右，如果你的服务一直是containerCreating状态，没有变化，那就是创建实例又出现问题，如下方法查看log
```
sudo minikube logs
```
日志中出现 failed pulling image… 服务在拉取自身需要的gcr.io/google_containers/pause-amd64:3.0镜像时失败了，如下报错。
```
Jan 05 03:52:58 minikube localkube[3624]: E0105 03:52:58.952990    3624 kuberuntime_manager.go:632] createPodSandbox for pod "nginx666-864b85987c-kvdpb_default(b0cc687d-f1cb-11e7-ba05-080027e170dd)" failed: rpc error: code = Unknown desc = failed pulling image "gcr.io/google_containers/pause-amd64:3.0": Error response from daemon: Get https://gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
```
解决方法：用本地镜像替代，原理就是使用阿里云的镜像下载到本地，然后命名为minikube使用的gcr.io的同名镜像，替代远端镜像即可
<br>下载阿里云镜像
```
docker pull registry.cn-hangzhou.aliyuncs.com/google-containers/pause-amd64:3.0
```
本地命名为 gcr.io/google_containers/pause-amd64:3.0
```
docker tag registry.cn-hangzhou.aliyuncs.com/google-containers/pause-amd64:3.0 gcr.io/google_containers/pause-amd64:3.0
```
再次查看pods，发现此时status已经变成running，服务启动成功
```
sudo kubectl get pods

NAMESPACE     NAME                             READY     STATUS             RESTARTS   AGE
default       spring-test-77867567f5-48czx     1/1       Running            2          16h
```

下面开始暴露端口
<br>三种暴露端口的方式，NodePort、LoadBalancer 和 Ingress
<br>这里先用nodeport做例子
<br>首先
```
sudo kubectl expose deployment kube-nginx999 --type=NodePort
```
用nodeport方式暴露端口，然后
```
kubectl get service
```
查看当前运行状态以及端口号
```
NAME            TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)           AGE
kube-nginx999   NodePort       10.99.254.255    <none>        80:30180/TCP      17h
kubernetes      ClusterIP      10.96.0.1        <none>        443/TCP           17h
spring-test     NodePort       10.97.184.136    <none>        8080:30230/TCP    17h
```
使用curl访问
```
curl http://127.0.0.1:port
```
得到”Hello World“

<br>但是外网访问10.58.14.251:30230的时候并不能访问，查阅资料是转发规则问题，输入
```
iptables -P FORWARD ACCEPT
```
问题解决

Loadbalancer以及Ingress方式暂时还没有尝试










