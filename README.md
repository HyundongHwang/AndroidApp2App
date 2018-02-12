<!-- TOC -->

- [AndroidApp2App](#androidapp2app)
- [데모](#데모)

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

![](https://s10.postimg.org/ll1jpmvmx/Kakao_Talk_20180212_134410278.png | width=100)