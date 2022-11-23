# MP3Player

## 개 요

- Kotlin과 SQLite를 사용하여 MP3Player를 만들어보았다.


## 개발환경

| 구 분 | 내 용 |
| --- | --- |
| OS | Windows 10 home |
| Language | Kotlin |
| Editor | Android Studio Dolphin |
| DB | SQLite |
| Github | https://github.com/fkrbtjs/MP3Player-kotlin-project |

## 개발기간

2022.11.11(금) ~ 2022.11.14(월)


## 기능 요약 및 설명

- lottie animation을 사용하여 opening event 추가
- Appbar와 viewPager사용하여 화면구성

https://user-images.githubusercontent.com/115532120/203591038-0f18c618-56d4-4c94-b8bd-4ebf6356083c.mp4

- like버튼 클릭여부를 SQLite에 저장하여 PlayList 와 FaoverList에 표현
- Fragment의 생명주기에 따라 onResume이 될때마다 notifyDataSetChanged()

https://user-images.githubusercontent.com/115532120/203591805-4a89642c-66ce-406c-b23d-f5629552f43d.mp4

- searchview에 setOnQueryTextListener 사용하여 입력값에 따라 SQLite에 저장되어있는 데이터 호출

https://user-images.githubusercontent.com/115532120/203592482-ecf4c52a-b738-4029-9952-7a0b723dd07b.mp4

- 데이터베이스에 노래데이터가 없을경우 contentResolver를 통해 공유데이터에서 음원을 가져온다
- coroutine 사용하여 음원재생
- 셔플기능과 반복기능 추가

https://user-images.githubusercontent.com/115532120/203593638-bb9b4876-7166-4645-8855-9050c605c3ae.mp4
