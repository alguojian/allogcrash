###  拦截app崩溃日志，桌面提供查看入口，支持推送到钉钉群组

[![](https://jitpack.io/v/alguojian/allogcrash.svg)](https://jitpack.io/#alguojian/allogcrash)


#### Step 1.Add it in your root build.gradle at the end of repositories:
```
allprojects {
            repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
}
```

####  Step 2. Add the dependency
```

dependencies {

        debugImplementation 'com.github.alguojian.allogcrash:allogcrash_debug:3.0.7'
        releaseImplementation 'com.github.alguojian.allogcrash:allogcrash_release:3.0.7'


        debugImplementation 'com.android.support:design:28.0.0'
        debugImplementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50'
        
        //版本限制不超过3.0.0
        debugImplementation 'org.litepal.android:kotlin:3.0.0'
        debugImplementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
}
```

```
//application中初始化

 CrashHandler.initThis(this)
 
 ```

#### 因为崩溃信息保存在本地数据库litepal中，所以需要配置litepal，asstes目录中添加数据库配置文件
```
<?xml version="1.0" encoding="utf-8"?>
<litepal>

    <dbname value="crash" />

    <version value="1" />
    
    <list>
        <mapping class="com.alguojian.crash.CrashBean" />
        <mapping class="com.alguojian.crash.OtherNewsBean" />
    </list>

    <storage value="external" />

</litepal>
```

##### 如果项目中已经使用litepal那么初始化时使用以下防止多次初始化

```
  CrashHandler.initThis(this,true)
  
  然后直接在litepal.xml文件中插入以下两条配置
  
  <list>
      <mapping class="com.alguojian.crash.CrashBean" />
      <mapping class="com.alguojian.crash.OtherNewsBean" />
  </list>

```


##### 如果需要配置一些其他信息

```

val treeMap = TreeMap<String, String?>()
treeMap["用户手机号"] = "1111111111
CrashHandler.setOtherNews(treeMap);


//设置钉钉机器人链接
CrashHandler.setDingDingLink("https://oapi.dingtalk.com/robot/send?access_token=04c3473dd02444a631eaee0b30415d6c49b0f2ec25b6f755d56e15d606a322c0");


//推送最新一条bug到钉钉群组
CrashHandler.postCrashToDingding();

```