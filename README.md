项目: 日志框架
简介: 该框架是简易的日志打印框架,用于项目中的日志输出，查看分析等等

一、接入步骤如下：
1.implement project(':Logger')

2.在Application中初始化
定义一个通用配置
LogConfiguration config = new LogConfiguration.Builder()
                                              .identify(tag) //唯一id 可以是插件id
                                              .logLevel(LogLevel.ERROR | LogLevel.VERBOSE)  /如果运行所有输出,则可以用LogLevel.ALL
                                              .addInterceptor(new Interceptor(){  //拦截器  如果没需要可不加
                                                  @Override
                                                  public LogInfo intercept(Chain chain) {
                                                      RealInterceptorChain realInterceptor = (RealInterceptorChain) chain;
                                                      LogInfo info = realInterceptor.getLogInfo();
                                                      //info的拦截操作
                                                      return realInterceptor.proceed(info);
                                                  }
                                              })
                                              .build();
sLog.instance().init(ctx,config);

同时sdk中嵌入了上传的接口,如果日志写入到文件中，接入时可调用
 sLog.instance().setUploader(new Uploader() {
        @Override
        public void upload(File[] list, UploadResultListener listener) {
            for (File f : list) {
                listener.onSuccess(f);
            }
            //或者listener.onSuccess(list);
        }
    }).init(appContext, configuration);
如果不需要上报则可不设置，按上面方式初始化。

3.当然了,LogConfiguration.Builder还有其他api
  .threadFormatter(new DefaultThreadFormatter())  //线程的formatter 一般可以不设置
  .printer(new AndroidPrinter()) // 输出的类,系统默认AndroidPrinter，如无区别可以不设置
  .formatter(new LogcatFormatter()) //输出格式的类，系统默认LogcatFormatter，一般可不设置
  .dir(path) //一般如果是输出到文件比如printer传入FilePrinter,则表示本地缓存文件的位置
  .encryptEnable(boolean) //是否允许加密
  .fileName() //指定文件名
  .encoder(LoggerEncoder) //指定加密算法 目前只针对file读写有效

二、如何使用

1.创建Logger对象并使用
通过我们可以通过
Logger l = LoggerFactory.get("Main")来获取 默认使用上面配置的config,不设置则会报错
还可以通过
Logger l = LoggerManager.get("main",config);

如果你只想修改默认配置的printer则可以用如下方法
config = LogConfiguration.ofPrinter(printer);
或者只需要修改formatter
config = LogConfiguration.ofFormatter(formatter);
等等其他api,可查看代码

还可以通过先创建出Builder出来
Builder builder = LogConfiguration.of();
然后修改相应的配置
builder.build();

然后我们就可以通过
log.e(msg)
log.d(msg)
log.i(msg)
log.w(msg)
log.v(msg)
来愉快的打印日志啦！

3.日志打印开关
  1.全局开关
    LoggerManager.instance().openStaticLog()  打开日志
    LoggerManager.instance().closeStaticLog() 关闭日志

  2.单项开关
  Logger log = LoggerManager.get();
  log.open() or log.close()

只有当两个开关通知打开的时候才会打印日志，默认都打开

三、添加混淆规则
-keepnames class com.sdk.sLog.LoggerFactory
-keep class com.sdk.sLog.sLog