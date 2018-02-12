<!-- TOC -->

- [AndroidApp2App](#androidapp2app)
- [데모](#데모)
- [Activity 로 Intent 전달](#activity-로-intent-전달)
- [Service 로 Intent 전달](#service-로-intent-전달)
- [Messenger 로 Message 전달](#messenger-로-message-전달)

<!-- /TOC -->

# AndroidApp2App
안드로이드 앱 o앱 통신을 즉 IPC를 테스트 해 보기 위한 샘플프로젝트 이다. <br>
더 다양한 방식이 있을 수 있지만 여기서는 3가지 방식만 테스트 해 보았다. <br>

- Activity 로 Intent 전달
- Service 로 Intent 전달
- Messenger 로 Message 전달

AIDL을 이용한 통신도 테스트 하려고 했지만, 인터페이스 구현체를 jar로 공유하는 등 여러가지로 손이 많이 가서 이건 제외하기로 했다.

# 데모
데모는 `deploy/MyAppA-release.apk`, `deploy/MyAppB-release.apk` 를 설치받아서 바로 실행해 볼수 있다.

![](https://s10.postimg.org/ll1jpmvmx/Kakao_Talk_20180212_134410278.png)

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