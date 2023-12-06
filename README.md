# see-realview-backend

> 이 저장소는 카카오맵을 기반으로 맛집 키워드를 검색하고 결과를 보여줍니다. 이후 맛집을 선택하면 해당 맛집에 대한 네이버 블로그 리뷰 중에서 해당 리뷰가 광고성 리뷰인지, 내돈내산 리뷰인지 판별한 결과를 미리 알 수 있도록 표시해주는 서비스 입니다. 맛집 검색 시에 활용하시면 됩니다.

</br>

## 변경 로그 요약
- 업데이트 예정

</br>

## 현재 진행 중인 사항
- [ ] API 설계
- [ ] 와이어프레임 구체화
- [ ] 음식점 검색 API 구현
- [ ] 지도 위치 검색 API 구현
- [ ] 포스트 분석 알고리즘 강화
- [ ] 한국어 자연어 전처리
- [ ] 포스트 북마크 기능 구현

</br>

## 설치 방법
> Require
> - JDK 17
> - Spring 3.1.4
> - GOOGLE_APPLICATION_CREDENTIALS, KAKAO_SEARCH_KEY, NAVER_SEARCH_ID, NAVER_SEARCH_SECRET, ACCESS_SECRET, REFRESH_SECRET, GMAIL_SENDER, GMAIL_PASSWORD 환경변수가 등록되어 있어야 합니다.
> - ACCESS_SECRET, REFRESH_SECRET은 256bits 이상의 길이어야 합니다.

1. clone project
```
$ git clone https://github.com/see-realview/see-realview-backend.git
$ cd see-realview-backend
$ cd see-realview
```

2. build and execute
```
$ ./gradlew build
$ cd build
$ cd libs
$ java -jar see-realview-0.0.1-SNAPSHOT.jar
```

</br>

## 기본 사용 예시
- 업데이트 예정

</br>

## 문제 해결 단계
- 업데이트 예정

</br>

## 심화 자료와 문서 링크
- 업데이트 예정

</br>

## 변경 로그 소개
- 2023.10.19 : 백엔드 프로젝트 초기 설정 [#7](https://github.com/see-realview/see-realview-backend/issues/7)
- 2023.11.24 : 블로그 포스트 분석 기능 구현 [#10](https://github.com/see-realview/see-realview-backend/issues/10)
- 2023.11.29 : 회원가입 이메일 인증 로직 구현 [#13](https://github.com/see-realview/see-realview-backend/issues/13)
- 2023.11.29 : 블로그 포스트의 텍스트 기반 리뷰 판별 모델 3종 비교 [#15](https://github.com/see-realview/see-realview-backend/issues/15)
- 2023.12.05 : redis 캐싱 버그 수정 [#19](https://github.com/see-realview/see-realview-backend/issues/19)

</br>

## 코드 유지 관리자
|      | **문석준**                 | **이창욱**                  | **이현빈**                    | **진예규**                       |
|:----:|:--------------------------:|:---------------------------:|:-----------------------------:|:--------------------------------:|
|E-Mail| seokjun0915@icloud.com     | ckddnr5527@gmail.com        | blackhblee@gmail.com          | jyg3485@naver.com                |
|GitHub| [seokwns](https://github.com/seokwns) | [ichanguk](https://github.com/ichanguk) | [blackhblee](https://github.com/blackhblee) | [teriyakki-jin](https://github.com/teriyakki-jin) |
|      | <img src="https://github.com/seokwns.png" width=100px> | <img src="https://github.com/ichanguk.png" width=100px> | <img src="https://github.com/blackhblee.png" width=100px> | <img src="https://github.com/teriyakki-jin.png" width=100px> |

</br>

## 라이선스 정보
see-realview is [MIT licensed](https://github.com/see-realview/see-realview-backend/blob/main/LICENSE).
