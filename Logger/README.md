项目: 日志框架
简介: 该框架是简易的日志打印框架,用于项目中的日志输出，查看分析等等

一、接入步骤如下：
1.implement 'com.sdk.log:logger:0.0.1-SNAPSHOT'

2.在项目的main目录下新建文件夹Resources,在改文件夹下新建一个app-config.cfg配置文件
如果不配置,则调用默认配置
Log.Writer=File
Log.Format=Def
Log.LogFile=fast.log
Log.Level=debug,info,warn,error

Writer表示 写日志的方式,上面默认是输出到文件
Format表示 日志的格式,sdk中默认格式 [level] [date] threadName=[name] msg=[msg]
logFile表示 写出类型是File时,文件名为fast.log
Level表示 日志过滤，只会打印出上面加入的日志，默认打印所有日志

上述Writer和Format如果需要自定义,则可改需要配置成类名，当然也可以在代码中添加
1、自定义Writer类
public class LogCatWriter implements Logger.LogWriter {
    private Logger _log;
    private String _tag;

    @Override
    public void write(int level, String msg) {
    }

    @Override
    public void attach(Logger log) {
    }
}
Log.Writer = LogCatWriter的全路径
或者在代码中添加
Logger logger = Logger.get()
logger.setWriter(LogCatWriter.class.getName)

Format参数同Writer,如果不需要配置则用默认值即可
Format的实现例如下：
public class LogFormatter implements LogFormatter {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Logger _log;

    @Override
    public void attach(Logger log) {
        _log = log;
    }

    @Override
    public String format(LogInfo info) {
        return "[" + _log.getLevelName(info.level()) + "]" + " " + format.format(info.date()) + " threadName=" + info.thread() + " msg=" + info.msg();
    }
}

Log.Format = LogFormatter的全路径
logger.setFormatter(LogFormatter.class.getName)

3.初始化
在Application中初始化,添加如下代码
LoggerManager.getInstance().init(Context);

二、如何使用
1.config定义

 · 获取OptionConfig
        OptionConfig config = Config.option();
        config.put(key,value)
        创建一个config,并且提前把参数添加进去

 · 获取Config(file or inoutStream)
        Config.from(file)或者Config.from(InputStream)

 · 获取Config (Builder)
       try {
           Config.Builder builder = ()->{

           };
           b.stream(name,inputStream);
           Config.define(builder);
       } catch (IOException e) {
           e.printStackTrace();
       }

2.创建Logger对象并使用
通过我们可以通过
Logger l = LoggerManager.get("Main")来获取
还可以通过
Logger l = LoggerManager.get("main",config);

区别是
方式1 使用的是默认的config配置，也就是会从上面我们的配置文件里面获取
方式2 会从我们的参数config中直接获取，如果没有找到会去默认配置里找
一般情况下我们使用默认配置就行，除非需要特殊配置
目前的默认配置是通过Log来打印日志

然后我们就可以通过
log.e(msg)
log.d(msg)
log.i(msg)
log.w(msg)
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
-keepnames class com.sdk.log.Logger
-keep class com.sdk.log.Config
-keep class com.sdk.log.LoggerManager