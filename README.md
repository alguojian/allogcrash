# allogcrash 手机app闪退信息，推送到钉钉

```
//初始化
CrashHandler.getInstance().init(this);

//设置一些信息
TreeMap<String, String> treeMap = new TreeMap<>();
treeMap.put("用户手机号","1111111111");
CrashHandler.getInstance().setOtherNews(treeMap);

//设置钉钉机器人链接
CrashHandler.getInstance().setDingDingLink("https://oapi.dingtalk.com/robot/send?access_token=04c3473dd02444a631eaee0b30415d6c49b0f2ec25b6f755d56e15d606a322c0");

```