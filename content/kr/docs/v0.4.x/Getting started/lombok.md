---
title: "Lombok을 사용하는 Java"
weight: 1
---

## @Value

### 1. 의존성 추가
`testImplementation("com.navercorp.fixturemonkey:fixture-monkey-jackson:0.4.0")` 추가

### 2. 옵션 변경
`LabMonkeyBuilder` 의 옵션 중 `objectIntrospector`를 변경합니다.

```java
LabMonkey labMonkey = LabMonkey.labMonkeyBuilder()
    .objectIntrospector(JacksonArbitraryIntrospector.INSTANCE)
    .build();
```

## @Builder
### 1. 옵션 변경
`LabMonkeyBuilder` 의 옵션 중 `objectIntrospector`를 변경합니다.
```java
LabMonkey labMonkey = LabMonkey.labMonkeyBuilder()
    .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
    .build();
```
