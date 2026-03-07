# NewYearResolution
Network Programming Project (SWU)

### ✅ 프로젝트 소개

신년 목표 세우기 New Year Resolution 웹 서비스

2021년 크리스마스 전, 사람들에게 자신의 트리를 공유할 수 있게 하는 ‘내 트리를 꾸며줘!’ 웹 서비스가 있었다. 트리로 갈 수 있는 링크를 타고 들어가면 트리 주인을 제외한 다른 사람들이 메세지를 장식할 수 있도록 하고, 트리의 주인이 크리스마스 당일에 내용을 확인할 수 있는 서비스이다. 많은 사람들에게 좋은 경험을 주었고 올해도 시행될 산타파이브의 크리스마스 트리를 모티브로 삼아 웹 서비스를 개발한다.

<aside>
🌅 새해를 맞기 전 자신의 신년 목표를 세우고 공유할 수 있는 웹 서비스**로, 스스로 자신의 신년 목표를 쓰고, 다른 사람들의 목표를 보면서 동기부여를 받을 수 있도록 한다.

</aside>

- 서비스 이용자들에게 동기부여를 받을 수 있는 기회를 제공한다
- 꾸밈 요소를 제공하고 이를 이미지 출력하여 보관할 수 있도록 한다.

---

### ✅ 프로젝트 내용 및 진행 과정

**주요 기능**

- **개인의 신년 목표 생성**
    - 게시글 주인을 식별할 수 있는 User_id를 제공한다.
    - 닉네임, 게시글 제목과 내용을 작성할 수 있도록 한다.
- **신년 목표 출력 및 꾸밈 기능**
    - 배경 색상이나 글자 색상 같은 변경할 수 있도록 한다.
    - 자신의 게시글을 이미지로 출력할 수 있도록 한다.
- **신년 목표 게시판 기능**
    - 모든 게시글을 볼 수 있는 게시판을 생성한다.

---

### ✅ 과제  주제

1. **다양한 통신 방법 및 I/O를 이용한 개발**
    1. **ServerSocket 사용**
    2. **Thread 사용**
        
2. **프로토콜 분석을 통해 특정 프로토콜이 적용된 서비스 개발**
    1. **HTTP 프로토콜 분석**
        1. **하이퍼텍스트(HTML) 문서를 교환하기 위해 만들어진 protocol(통신 규약)**
        2. **Request(요청)/Response(응답)에 맞춰서 통신**
            
3. **DB 사용**
    1. **Mongo DB 사용**
    2. ~~지금 다시 고르라고하면 MySQL RDS..~~

<aside>
💡 네트워크 프로그래밍 과제로써 통신 framework 사용 금지

</aside>

---

## 기능 플로우 차트

```mermaid
flowchart TD
ST([메인 페이지 접속]) --> MN([게시판])
MN --> A([CREATE 버튼 클릭])
MN --> B([SEARCH 검색어 입력])
MN --> C([게시글 클릭])
A  --> AA([게시글 생성 페이지 이동]) 
AA --> AB([게시글 작성 AND 꾸미기])
AB --> AC([게시글 생성 버튼 클릭])
AC --> AD([게시글 비밀번호 입력 창])
AD --> AG([게시글 생성])
AG--> ST

C  --> CA([상세 페이지 이동])
CA --> CB([다운로드 버튼 클릭])
CA --> CC([삭제 버튼 클릭])
CB --> CD([비밀번호 입력])
CD --> CF{올바른 비밀번호인가?}
CF --> |NO|CF
CF --> |YES|CG([이미지 다운로드])

CC --> CE([비밀번호 입력])
CE --> CK{올바른 비밀번호인가?}
CK --> |NO|CJ([ALERT 비밀번호 오류])
CJ --> CK
CK --> |YES|CI([게시글 삭제])
CI --> MN

B  --> BA([검색 목록 불러오기])
BA --> MN([게시판])

```

## 서버 구현 플로우 차트

```mermaid
flowchart TD
START([Socket 열기]) --> WAITING([서버 대기])
WAITING --> REQUEST([Client 요청])
REQUEST --> THREAD([Client Thread 생성])
THREAD  --> READST{START LINE 읽기}
READST  --> |GET|GET([GET 요청인 경우])
READST  --> |POST|POST([POST 요청인 경우])
GET     --> THGET([GET Thread 생성])
POST    --> THPOT([POST Thread 생성])

THGET   --> GETIF{요청 페이지 확인}
GETIF   --> |NO| GETB([에러])
GETIF   --> |YES|GETA([요청 페이지 전송])
GETA    --> END([스레드 CLOSE])
GETB    --> END([스레드 CLOSE])

THPOT   --> POTIF([요청 확인])
POTIF   --> |SELECT| SELECT{param 값 유무 확인}
SELECT  --> |NULL| ALL([전체 게시글 출력])
SELECT  --> |NOT NULL| KEY{0번째 param key 값 확인}
KEY     --> |ID|ID([상세페이지로 이동])
KEY     --> |MESSAGE|MESSAGE([검색하기])
MESSAGE --> S_HOME([메인 홈 검색 결과 출력])
ALL     --> END([스레드 CLOSE])
ID      --> END([스레드 CLOSE])
S_HOME  --> END([스레드 CLOSE])

POTIF   --> |INSERT| INSERT([데이터 저장])
INSERT  --> MOVE([상세페이지로 이동])
MOVE    --> END([스레드 CLOSE])

POTIF   --> |DELETE| DELETE([DB에서 해당 데이터 찾아 삭제])
DELETE  --> HOME([홈 게시판으로 이동])
HOME    --> END([스레드 CLOSE])

```

## 상세보기
https://www.notion.so/nasong/New-Year-Plan-b829e3ae281046c2a131ae6999c6c989
