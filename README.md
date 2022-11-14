# books


## commit messages
### - rules
- 제목과 본문을 한 줄 띄워 분리하기
- 제목은 영문 기준 50자 이내로
- 제목 첫 글자를 대문자로
- 제목 끝에 . 금지
- 제목은 명령조로
- Github - 제목(이나 본문)에 이슈 번호 붙이기
- 본문은 영문 기준 72자마다 줄 바꾸기
- 본문은 어떻게보다 무엇을, 왜 기준에 맞춰 작성하기

### - message structure
```
type: Subject

body

footer
```

### - type
- feat : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 수정
- style : 코드 formatting, 세미콜론(;) 누락, 코드 변경이 없는 경우
- refactor : 코드 리팩터링
- test : 테스트 코드, 리팩터링 테스트 코드 추가(프로덕션 코드 변경 X)
- chore : 빌드 업무 수정, 패키지 매니저 수정(프로덕션 코드 변경 X)
- design : CSS 등 사용자 UI 디자인 변경
- comment : 필요한 주석 추가 및 변경
- rename : 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우
- remove : 파일을 삭제하는 작업만 수행한 경우
- !BREAKING CHANGE : 커다란 API 변경의 경우
- !HOTFIX : 급하게 치명적인 버그를 고쳐야 하는 경우
