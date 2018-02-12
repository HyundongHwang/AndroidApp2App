<!-- TOC -->

- [AndroidApp2App](#androidapp2app)
    - [`MyAppA`](#myappa)
    - [`MyAppB`](#myappb)
- [데모](#데모)
- [Activity 로 Intent 전달](#activity-로-intent-전달)
- [Service 로 Intent 전달](#service-로-intent-전달)
- [Messenger 로 Message 전달](#messenger-로-message-전달)
- [결론](#결론)

<!-- /TOC -->

# AndroidApp2App
안드로이드 앱 o앱 통신을 즉 IPC를 테스트 해 보기 위한 샘플프로젝트 이다. <br>
더 다양한 방식이 있을 수 있지만 여기서는 3가지 방식만 테스트 해 보았다. <br>

- Activity 로 Intent 전달
- Service 로 Intent 전달
- Messenger 로 Message 전달

AIDL을 이용한 통신도 테스트 하려고 했지만, 인터페이스 구현체를 jar로 공유하는 등 여러가지로 손이 많이 가서 이건 제외하기로 했다. <br>

<br>
<br>
<br>

## `MyAppA` 
호스트에 해당하는 앱으로 메시지를 수신하고 프로세싱해서 리턴하는 역할이다. <br> 
Activity 통신에서는 잠깐 화면이 보이는데 다른 시나리오에서는 화면이 아예 보이지 않는다. 

<br>
<br>
<br>

## `MyAppB` 
게스트에 해당하는 앱으로 메시지를 송신하고 결과를 기다렸다가 화면에 출력하는 역할을 한다. <br>
송신자의 pkgName, clsName을 함께 송신하여 결과메시지를 수신할수 있도록 하였다. 

<br>
<br>
<br>

# 데모
데모는 `deploy/MyAppA-release.apk`, `deploy/MyAppB-release.apk` 를 설치받아서 바로 실행해 볼수 있다.

![](https://s10.postimg.org/ll1jpmvmx/Kakao_Talk_20180212_134410278.png)

<br>
<br>
<br>

# Activity 로 Intent 전달

<!--
@startuml

entity MyAppA.MainActivity as a
entity MyAppB.MainActivity as b

hnote over b
    click "send msg by activity" 
endnote

b -> a : startActivityForResult

hnote over a
    onCreate or onNewIntent
        process msg ...
        setResult + newMsg
endnote

a -> b : finish

hnote over b
    onResult : 
        display newMsg
endnote

@enduml
-->
![](http://www.plantuml.com/plantuml/png/RP7DRi8m3CVlUGgB4qXR7s27YGvf4eUYQUTTqjOh4ObJnG6KjvyJIYQq-MJ__VLpikMeGpmxgnHID351bzfvRfjE6sg7Q2vPqmpw8V_s9zyhTIGV4Vm5WqGWDbWpd635I2Cudg2NogLb1J9lp0rAzV3y2XfUeB3SXxxxy8byjd7zHM0eolgrIn-dwQ0TFe5BkJ0JxIml4a-RpGE6BXYUTW5rreBu1xpkoxIIopO7Fo1pGMoQfkgCSI6eKhQ59L-5eVBSmZjyPIAyTWMhNglpjRrS-st8yF6VP_DqMoirTV5eUBOwPSRVKLlnvEj-0000)

`myappb\MainActivity.java`

```java
public class MainActivity extends AppCompatActivity {

    private void _send_msg_by_activity() {
        String msg = _newMsg();
        Intent msgIntent = new Intent();
        msgIntent.setComponent(new ComponentName("com.hhd2002.myappa", "com.hhd2002.myappa.MainActivity"));
        msgIntent.putExtra("msg", msg);
        msgIntent.putExtra("pkg", getPackageName());
        msgIntent.putExtra("cls", MainActivity.class.getName());
        _writeLog(String.format("B->A : %s", msg));
        startActivityForResult(msgIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == 2000) {
            String msg = data.getStringExtra("msg");
            _writeLog(String.format("A->B : %s", msg));
        }
    }
}
```

`myappa\MainActivity.java`

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (this.getIntent() != null) {
            boolean skipUi = _processIntent(this.getIntent());

            if (skipUi)
                return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        _processIntent(this.getIntent());
    }

    private boolean _processIntent(Intent intent) {
        String msg = intent.getStringExtra("msg");

        if (msg != null) {
            String newMsg = String.format("%s world", msg);
            Intent resIntent = new Intent();
            resIntent.putExtra("msg", newMsg);
            this.setResult(2000, resIntent);
            finish();
        }

        return false;
    }
}
```

<br>
<br>
<br>

# Service 로 Intent 전달

<!--
@startuml

entity MyAppA.MyService as a
entity MyAppB.MainActivity as b

hnote over b
    click "send msg by service" 
endnote

b -> a : startService

hnote over a
    onStartCommand
        process msg
endnote

a -> b : startActivity + newMsg

hnote over b
    onNewIntent
        display newMsg
endnote

@enduml
-->

![](http://www.plantuml.com/plantuml/png/PP4zRiCm38LtdOB8v01j3j0XY9kfWuC2MRjGCk4AiIX3L1tezf4Ssu5HJlpJUn-XaqGCyS_rIX576nCqgHw6-j2aAuNH6W8KmCtmyz2WvTf4EvPUdckbVjb70ZzIo1NaCBqrDzW9SGjEEj09vAcuWopNbdsbDBmV0U439exPilfXi1opP-NIzwsxeACtSAbO5enpBtDBklr-uusJj-ThaJjxvv3RgLLY2DwGI7ds8i12e1U2vQeANWpb10ehnxDS5bSMfdlp3qVFE1UwVqrYAqjhPUWnRLbEESj_yG00)

`myappb\MainActivity.java`

```java
public class MainActivity extends AppCompatActivity {
    
    private void _send_msg_by_service() {
        Random random = new Random();
        String msg = String.format("hello %d", random.nextInt(100));

        Intent msgIntent = new Intent();
        msgIntent.setComponent(new ComponentName("com.hhd2002.myappa", "com.hhd2002.myappa.MyService"));
        msgIntent.putExtra("msg", msg);
        msgIntent.putExtra("pkg", getPackageName());
        msgIntent.putExtra("cls", MainActivity.class.getName());
        startService(msgIntent);

        _writeLog(String.format("B->A : %s", msg));
    }
```

`myappa\MainActivity.java`

```java
public class MyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return Service.START_STICKY;
        else
            _processIntent(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void _processIntent(Intent intent) {
        String msg = intent.getStringExtra("msg");
        String pkg = intent.getStringExtra("pkg");
        String cls = intent.getStringExtra("cls");

        Intent newIntent = new Intent();
        newIntent.setComponent(new ComponentName(pkg, cls));

        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP );

        String newMsg = String.format("%s world", msg);
        newIntent.putExtra("msg", newMsg);
        startActivity(newIntent);
    }
}
```

`\MyAppB\...\AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    <application
        <service android:name=".MyService">
            <intent-filter>
                <action android:name="com.hhd2002.myappa.MyService" />
```


<br>
<br>
<br>

# Messenger 로 Message 전달

<!--
@startuml

entity MyAppA.MyMessengerService as a
entity MyAppB.MainActivity as b

hnote over b
    click "send msg by activity" 

    if ServiceConnection == null
        create ServiceConnection
        onServiceConnected :
            create Messenger w/ binderService
endnote

hnote over a
    onBind :
        create Messenger
        return Messenger's binder        
endnote

b -> a : Messenger.send msg

hnote over a
    Messenger.handleMessage :
    process msg
endnote

a -> b : startActivity + newMsg

hnote over b
    onNewIntent
        display newMsg
endnote

@enduml
-->

![](http://www.plantuml.com/plantuml/png/RL8nRiCm3Dpr2Y9BXwBf7Y15d4uPtAKle2pM4QfGXYJRyEzBkxOSfzKawOvtF56dc32azkQK8aus3L0EHTCK-t8eAKRYci8dXSvM11W1DwpplaJBHPLiDzu9hfMwiay4lgCW9v1LELjzmqwq3DnY3LgOSyaEr4InNp2Rl7jc4jGp78_0hNCJOT89XAByXvW9dhSO6JXay4uX1uF-5RHbaoDAEZEslqc1wbVyBCmxmKUn30HARU0LU8gpoK9ONJIyl072OINlbsVwfuMLT4KsZiOprZJtr0HVoSLKcmrmDD1YC8qvJ-ePcFhomKJFEJ-elt2IEUT4niR6uR0KPVMJxEJh_000)

`\myappb\MainActivity.java`

```java
public class MainActivity extends AppCompatActivity {

    private void _send_msg_by_messenger_service() {
        if (_messenger == null) {
            Intent msgIntent = new Intent();
            msgIntent.setComponent(new ComponentName("com.hhd2002.myappa", "com.hhd2002.myappa.MyMessengerService"));

            ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    _writeLog("onServiceConnected !!! ");
                    _messenger = new Messenger(service);
                    _send_msg_by_messenger();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    _writeLog("onServiceDisconnected !!! ");
                }
            };

            bindService(msgIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            _send_msg_by_messenger();
        }
    }

    private void _send_msg_by_messenger() {
        Message newMsg = new Message();
        String msg = _newMsg();
        newMsg.getData().putString("msg", msg);
        newMsg.getData().putString("pkg", getPackageName());
        newMsg.getData().putString("cls", MainActivity.class.getName());

        try {
            _messenger.send(newMsg);
            _writeLog(String.format("B->A : %s", msg));
        } catch (Exception e) {
            e.printStackTrace();
            _messenger = null;
            _send_msg_by_messenger_service();
        }
    }
```

`\myappa\MyMessengerService.java`

```java
public class MyMessengerService extends Service {

    private Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String msgStr = msg.getData().getString("msg");
            String pkg = msg.getData().getString("pkg");
            String cls = msg.getData().getString("cls");

            Intent newIntent = new Intent();
            newIntent.setComponent(new ComponentName(pkg, cls));

            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP );

            String newMsg = String.format("%s world", msgStr);
            newIntent.putExtra("msg", newMsg);
            startActivity(newIntent);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder = new Messenger(_handler).getBinder();
        return binder;
    }
}

```

`\MyAppB\...\AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    <application
        <service
            android:name=".MyMessengerService"
            android:process=":remote">
            <intent-filter>
                <action android:name="com.hhd2002.myappa.MyMessengerService" />
```

<br>
<br>
<br>

# 결론
GUI 가 연동동작하는 시나리오는 `Activity 로 Intent 전달` 이 가장 적당하다. 구현도 가장 쉽다. <br>
통신 속도가 중요하다면 `Messenger 로 Message 전달` 가 가장 빠르다. <br> `Activity 로 Intent 전달` <<< `Service 로 Intent 전달` < `Messenger 로 Message 전달`

