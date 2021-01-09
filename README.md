# 基于钛极OS(TiJOS)的阿里云IoT接入案例

阿里云物联网套件是阿里云专门为物联网领域的开发人员推出的，其目的是帮助开发者搭建安全性能强大的数据通道，方便终端（如传感器、执行器、嵌入式设备或智能家电等等）和云端的双向通信。全球多节点部署让海量设备全球范围都可以安全低延时接入阿里云IoT Hub，安全上提供多重防护保障设备云端安全，性能上能够支撑亿级设备长连接，百万消息并发。物联网套件还提供了一站式托管服务，数据从采集到计算到存储，用户无需购买服务器部署分布式架构，用户通过规则引擎只需在web上配置规则即可实现采集+计算+存储等全栈服务。

总而言之，基于物联网套件提供的服务，物联网开发者可以快速搭建稳定可靠的物联网平台。

源码请参考 <https://github.com/TiJOSApp/tijos-mqtt-aliyun-iot>



## 开通阿里云物联网平台

在使用前我们需要先开通阿里云的物联网平台，进入阿里云官网注册完账户后进入管理控制台，在产品与服务中选择物联网平台（使用物联网平台需要激活，请参考官方教程自行激活功能）。

### 创建产品（设备模型）

在物联网平台中创建产品,  如果选择标准品类，在创建成功后将自动应用标准的物模型模板，节点类型选择直连设备， 数据格式请选择ICA标准数据格式， 认证方式选择设备密钥。

创建成功后， 可以在产品管理中看到相应的产品，点击产品可以看到产品的详细信息。

![image-20201209151653956](.\img\image-20201209151653956.png)

创建成功后， 平台会提示添加设备或前往定义物模型， 可选择前往定义物模型，查看平台标准模板所创建的物模型,包含各种设备属性，事件及服务，可根据实际需要对物模型进行编辑等操作。 

![image-20201209152158998](.\img\image-20201209152158998.png)



### 添加设备

产品物模型定义完成后， 即可在该产品下添加对应的设备， 在左边菜单中选择设备后， 在右边点击添加设备按钮即可进行设备添加

![image-20201209152454962](.\img\image-20201209152454962.png)

输入设备信息,其中设备名称可使用设备的唯一ID， 对于4G产品一般选择模组的IMEI号作为设备名称，方便在程序中自动获取。

![image-20201209152527887](.\img\image-20201209152527887.png)

完成添加后，即可在设备详情中查看DeviceSecret查看设备认证信息并保存，这些信息将用于代码中。

![image-20201209152730765](E:\work\ti-github\tijos-document-center\docs\tijos-samples\cloud_samples\ali-iot\img\image-20201209152730765.png)

通过以上步骤即可完成产品和设备的定义。 



### 设备接入

钛极OS是钛云物联开发的物联网操作系统 ，可运行于单片机、物联网模组等低资源设备中， 支持用户通过Java语言进行进行硬件功能开发，并提供了各种云端接入组件包， 并内置支持阿里云物联网平台接入。 

#### 准备工作

1. 准备一台内置钛极OS(TiJOS)的设备， 建议使用支持4G的TiGW200边缘计算网关
2. 安装Ecclipse及TiStudio开发环境， 具体请参考TiGW200开发指南文档或访问<钛极OS文档中心>[http://doc.tijos.net]
3. 将TiGW200进入开发模式并连接电脑USB口
4. 在设备详情中获取到设备的密钥认证信息， 包括产品ID，设备名称，设备密钥

#### 应用开发

在Ecclipse中新建TiJOS Application应用，腾讯云平台接入通过钛极OS(TiJOS)内置的AliYunIoT类及IDataModelEventListener事件进行支持，用户可参考相关文档和例程接合实际应用进行开发，并通过编译下载到TiGW200设备中进行测试。 

具体可参考[阿里云IoT平台客户端 - 文档中心 (tijos.net)](http://doc.tijos.net/docstore/tijos-development-guide/tijos.framework.networkcenter.alibaba/)

#### 代码编译下载

从GitHub下载已完成的代码，通过Eclipse导入到Workspace中, 在Eclipse中可以看到工程基于TiJOS Framework开发，所有源码和API都是Java代码，TiJOS Framework对各种外设传感器及网络做了抽象封装，通过API可方便快捷的操作外设硬件。

在完成代码修改后，通过选中工程右键弹出菜单点击Run as --> TiJOS Application实时下载至硬件中运行。 



## 代码说明

#### 启动4G网络

在程序启动时， 先启动4G网络

```java
启动4G网络 超时30秒
TiLTE.getInstance().startup(30);
```

#### 连接阿里云物联网平台

```java
//平台网址
String serverUrl = "mqtt://a1QJjmusiPI.iot-as-mqtt.cn-shanghai.aliyuncs.com:443"; /* 阿里云平台上海站点的域名后缀 */
//设备认证信息
String ProductKey = "a1QJjmusiPI";
String DeviceName = "gwdemo1";
String DeviceSecret = "71fab7d64714233e8757181ab6c9e28b";

//接入平台
aliiot.start(serverUrl, ProductKey, DeviceName, DeviceSecret, new AliListener());

```

#### 设备属性上报

```java
// 物模型中的属性上报
JSONObject properties = new JSONObject();
properties.put("LightStatus", 0);

int mid = aliiot.propertyPost(properties.toString());
```

#### 设备事件上报

```java
// 物模型中的事件上报
JSONObject errorEvent = new JSONObject();
errorEvent.put("ErrorCode", 0);

String serviceId = "Error";
mid = aliiot.eventPost(serviceId, errorEvent.toString());
```



#### 云端指令处理

云端指令通过事件回调来处理，用户需要程序中实现IDataModelEventListener事件接口

```java
class AliListener implements IDataModelEventListener {
......
    //当有属性控制指令下发时
	@Override
	public void onPropertySetArrived(String product, String deviceName, long msgId, String params) {
		System.out.println("onPropertySetArrived ");

		// 控制指令解析
		JSONObject commands = new JSONObject(params);
		// 命令执行

		//

		// 控制指令回复
		JSONObject reply = new JSONObject();

		try {
			AliYunIoT.getInstance().propertySetReply(msgId, 200, reply.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    //当有异步服务指令下发时
	@Override
	public void onAsyncServiceInvokeArrived(String product, String deviceName, long msgId, String serviceId,
			String params) {
		System.out.println("onAsyncServiceInvokeArrived ");

		// 解析服务参数
		JSONObject parameters = new JSONObject(params);

		// 执行服务

		// 返回结果
		int code = 200; // 200 - 成功
		JSONObject reply = new JSONObject();

		try {
			AliYunIoT.getInstance().asynServiceReply(msgId, serviceId, code, reply.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
......    
    
}
```



## 总结

本案例实现了最基本的网络接入和收发数据，而在实际产品设计中可通过阿里云的策略对数据进行转发，同时基于阿里云的云服务优势对数据做处理分析，以及大数据运算和存储等。