# AEIO 프로젝트 가이드

## 프로젝트 개요

얼굴 요가 클래스 플랫폼 REST API 서버.
Java 17, Spring Boot 3.2.5, JPA/MySQL, JWT, AWS S3, Prometheus/Grafana

---

## 디렉토리 구조

```
src/main/java/com/unithon/aeio/
├── domain/
│   ├── classes/       # 클래스(운동 프로그램) 도메인
│   ├── member/        # 회원 도메인
│   └── review/        # 리뷰 도메인
└── global/
    ├── config/        # Security, Swagger, S3, WebConfig
    ├── entity/        # BaseTimeEntity
    ├── error/         # 전역 예외 처리
    ├── result/        # 공통 응답 포맷 및 ResultCode
    └── security/      # JWT 필터, 커스텀 어노테이션
```

각 도메인은 아래 계층으로 구성됨:
```
{domain}/
├── controller/   # HTTP 요청 처리
├── service/      # 비즈니스 로직 (인터페이스 + Impl)
├── repository/   # JPA Repository
├── entity/       # JPA 엔티티
├── dto/          # Request / Response DTO
└── converter/    # 엔티티 ↔ DTO 변환
```

---

## 엔티티 관계

```
Member ──1:N──> Worry (고민 부위)
Member ──1:N──> MemberClass
Member ──1:N──> ClassLike

Classes ──1:N──> MemberClass
Classes ──1:N──> ClassLike

MemberClass ──1:N──> PracticeLog
MemberClass ──1:N──> Review

Review ──1:N──> ReviewPhoto
```

---

## API 작성 방식

### 공통 응답 포맷

**성공 응답** — `ResultResponse<T>` 사용
```java
@GetMapping("/members/information")
public ResultResponse<MemberResponse.MemberInfo> getMemberInfo(
        @LoginMember Member member) {
    return ResultResponse.of(MemberResultCode.GET_USER_INFO,
            memberService.getMemberInfo(member));
}
```

**ResultResponse 구조**
```json
{
  "status": 200,
  "code": "SM001",
  "message": "성공적으로 멤버 정보를 조회했습니다.",
  "data": { ... }
}
```

**에러 응답** — `ErrorResponse` (GlobalExceptionHandler에서 자동 생성)
```json
{
  "status": 400,
  "code": "EG001",
  "message": "올바르지 않은 입력입니다.",
  "data": [{ "field": "nickName", "message": "닉네임은 필수입니다." }]
}
```

### Result/Error Code Enum 패턴

```java
// 도메인별 성공 코드
@Getter
@RequiredArgsConstructor
public enum MemberResultCode implements ResultCode {
    CREATE_MEMBER(200, "SM001", "성공적으로 멤버 정보를 저장했습니다."),
    GET_USER_INFO(200, "SM003", "성공적으로 멤버 정보를 조회했습니다.");

    private final int status;
    private final String code;
    private final String message;
}

// 도메인별 에러 코드
@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(404, "EM000", "회원을 찾을 수 없습니다."),
    NICKNAME_ALREADY_EXIST(409, "EM001", "이미 존재하는 닉네임입니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

### 예외 처리

비즈니스 예외는 `BusinessException`으로 던짐:
```java
throw new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND);
```

`GlobalExceptionHandler`가 일괄 처리:
- `BusinessException` → 도메인 에러 코드 응답
- `MethodArgumentNotValidException` → 유효성 검사 실패 응답

### 인증 — @LoginMember 커스텀 어노테이션

컨트롤러 파라미터에 `@LoginMember`를 붙이면 현재 로그인한 `Member` 엔티티가 주입됨:
```java
public ResultResponse<...> someApi(@LoginMember Member member) { ... }
```

### 입력 검증

```java
// DTO 필드에 Jakarta Validation 어노테이션 사용
@NotBlank(message = "닉네임은 필수로 입력해야 합니다.")
@Size(max = 9, message = "닉네임은 최대 9자까지 설정할 수 있습니다.")
private String nickName;
```

컨트롤러에서 `@Valid` 또는 `@Validated`로 활성화.

### 페이징

```java
@GetMapping("/classes/likes/me")
public ResultResponse<ClassResponse.PagedLikeList> getMyLikedClasses(
        @LoginMember Member member,
        @PageableDefault(size = 10, sort = "createdAt",
                         direction = Sort.Direction.DESC) Pageable pageable) { ... }
```

### Swagger 문서화

```java
@Operation(summary = "클래스 구독 API", description = "사용자가 클래스를 구독합니다.")
@Parameters({ @Parameter(name = "classId", description = "클래스 ID") })
```

---

## 주요 설정

| 항목 | 값 |
|------|-----|
| 포트 | 8081 |
| DB | MySQL `localhost:3306/aeio` |
| JWT Access Token | 4주 |
| JWT Refresh Token | 8주 (HttpOnly 쿠키) |
| S3 버킷 | `aeio-photo2` |
| Batch Fetch Size | 500 (N+1 방지) |

---

## 보안 규칙

- `/login/**`, Swagger 경로만 permitAll, 나머지 전부 인증 필요
- Refresh Token은 쿠키(HttpOnly)로만 관리
- S3 URL은 허용된 호스트 + `photo/` 또는 `default/` 경로만 수락
- 리소스 수정/삭제 시 소유자 확인 필수

---

## 개발 패턴 요약

1. **새 도메인 추가** → `domain/{name}/` 하위에 controller, service, repository, entity, dto, converter 생성
2. **새 API 추가** → ResultCode Enum에 코드 추가 → Service 로직 → Controller에서 `ResultResponse.of()` 반환
3. **새 에러 추가** → ErrorCode Enum에 코드 추가 → `throw new BusinessException(...)` 사용
4. **이미지 처리** → S3 Presigned URL 발급, 트랜잭션 커밋 후 기존 이미지 삭제 (`TransactionSynchronization`)
5. **N+1 방지** → Lazy Loading 기본 + `findBy...In()` 일괄 조회 후 Map으로 매핑