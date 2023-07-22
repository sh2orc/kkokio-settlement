# 꼬끼오톡 1/N 프로젝트
꼬끼오 1/N 정산 프로젝트는 꼬끼오톡 플랫폼에서 다수의 사용자에 대한 정산을 처리하기 위한 솔루션을 제공하는 프로젝트입니다. 이 프로젝트는 카카오 플랫폼의 거래 정보를 기반으로 정산을 수행하고, 생성된 1/N 정산은 상태를 관리하여 정산 처리를 지원합니다.

해결하는 과제인 정산은 크게 2가지로 나뉘며 연관 관계를 맺고 관리 됩니다. 1/N 정산의 정보를 갖는 상위 정산과 유저들에게 할당된 하위 엔티티의 유저 정산입니다. 이 둘의 엔티니는 연관관계를 가지고 있으며 유저별 정산이 모두 완료 되어야 1/N 정산이 최종적으로 완료 상태가 됩니다.

유저에게 할당된 정산금액이 상환(송금)이 완료되지 않으면, 유저 정산의 상태는 __진행 중__ 상태로 유지되며, 나머지 잔여 금액(unpaidAmount)이 상환 처리가 되지 않으면 상위 1/N 정산은 미정산으로 유지됩니다. 정산이 완료되지 않은 유저 정산들에 대해서는 스케줄러에서 일정 주기마다 체크하여 알림을 처리(Mock)하도록 하였습니다.

이 프로젝트의 기술 스택은은 다음과 같습니다.

## 기술 스택
본 프로젝트는 Spring boot 3.0.8와 MariaDB 기반으로 작성이 되어있습니다. REST API를 기반 백엔드 서비스를 제공하여야 하므로 Spring Web과 기본 임베디드 컨테이너인 톰캣 기반에서 구현되어있습니다. (필요 시 build.gradle 수정을 통해 Undertow로 전환 가능 제공할 수 있습니다.). DAL(Data Access Layer)는 Spring Data JPA 기반으로 활용하고 있습니다. 단, 직접 DTO로 직접 프로젝션을 이용하는 경우 QueryDSL을 이용하고 있으며 *QueryRepository와 같은 네이밍 규칙을 같습니다. Spring Security를 이용하여  URI들에 대한 인증과 인가를 처리합니다. 

- Java JDK 17
- Spring Boot 3.0.8
- Spring Web (+ tomcat)
- Spring Data JPA
- Spring Security
- Junit 5
- Lombok
- Gradle 7
- MariaDB 10.x


## 주요 기능
1/N 정산 프로젝트를 구성하는 주요 기능은 한 명의 유저가 다수의 유저들에게 정산을 요청하여 1/N 정산이 생성 되어 진행됩니다. 정산의 진행 과정에서 다음과 같은 단위 기능들을 제공하고 있습니다.

- 1/N 정산 생성 : 한 명의 유저가 (자신 포함 가능) 다수 유저들에게 1/N 정산 생성
- 1/N 정산 처리 : 1/N 정산과 연관 관계의 유저 정산들에 대한 전체 라이프 사이클 관리
- 유저 정산 처리 : 유저에게 할당된 정산을 처리하며, 정산은 N 회에 걸쳐 진행 가능합니다. 다만 유저에게 할당된 잔여 정산 금액을 초과할 수 없습니다.
- 정산 조회
  - 유저가 다른 유저들에게 요청한 정산 내역 조회
  - 유저가 다른 유저에게로부터 요청 받은 정산 내역 조회
- 알림 기능: 정산 상태 변경이나 알림 대상자에게 알림을 전송하는 기능을 제공합니다.
- 인증 기능 : Spring Security 기반의 HTTP Header 토큰 값(X-USER-ID) 인증
- 로그 기능 : API 요청과 응답에 대한 전체 로그 출력
- 표준 메시지 포멧 : ApiResponse, ApiException 등 API 처리 결과 포멧 공통화

## 요구사항 분석
### 기능 요구사항
- 사용자는 다수의 사람들에게 금액을 지정하여 정산 요청
- 요청받은 사용자는 정산하기 버튼을 통해서 요청한 요청자에게 금액을 송금
- 요청한 모든 인원이 정산을 완료한 경우에는 해당 요청은 정산 완료 처리
- 요청한 일부 인원 중 미정산된 경우에는 리마인드 알림
- 요청자는 자신이 요청한 정산하기 전체 리스트의 정보 조회
- 요청받은사람은 자신이 요청 받은 정산하기 전체 리스트의 정보 조회

### 선택조건
- 요청받은 사용자의 친구 여부는 관련없는 것으로 생각할 수 있다.
- 요청받은 사용자의 잔액은 충분히 많은 것으로 생각할 수 있다.
- 꼬끼오톡의 대화방의 개념은 생략할 수 있다.
- 정산하기 금액은 반드시 1/N이 아니라, 다른 금액으로 개별 요청할 수 있다.

### 기술적 명세서
- 개발 언어와 프레임워크 : Java, Spring Boot
- TDD 필수: 각 기능 및 제약사항에 대해 단위 테스트 작성
- REST API로 구현
- 다수 인스턴스가 실행되어도 문제 없도록 동시성 이슈를 함께 고려 필요
- API 의 HTTP Method(GET/POST/PUT/DELETE)는 자유롭게 사용 가능
- 개인정보가 있을 경우 데이터는 암호화 되어야 한다.
- 에러 응답, 에러코드는 자유롭게 정의 가능
- 회원 정보의 가입/탈퇴는 생력하며, 이미 회원정보가 존재한다고 가정한다.
- 실제 금액 이체, 실제 알림 발송 등의 외부 의존송이 필요한 기능은 Interface 혹은 Mock으로 구현한다. (실제 구현을 하지 않아도 된다)
- 인증(Authentication)과 인가(Authorization)
  - 로그인 사용자의 식별 값은 숫자 형태이며 "X-USER-ID"라는 HTTP Header로 전달
  - 사용자가 속한 대화방의 식별값은 문자 형태이며, "X-ROOM-ID"라는 HTTP Header로 전달

## 프로젝트 기여자
- 이태영 sh2orc@gmail.com

## 라이선스
This project is licensed under the [GNU General Public License v3.0](LICENSE) - see the [LICENSE](LICENSE.md) file for details.