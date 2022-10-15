---
title: "Java"
weight: 2
---

## 불변 객체
### JacksonArbitraryIntrospector
1. 의존성 추가
`testImplementation("com.navercorp.fixturemonkey:fixture-monkey-jackson:0.4.0")`

2. 옵션 변경
`LabMonkeyBuilder` 의 옵션 중 `objectIntrospector`를 변경합니다.

```java
LabMonkey labMonkey = LabMonkey.labMonkeyBuilder()
    .objectIntrospector(JacksonArbitraryIntrospector.INSTANCE)
    .build();
```

### `ConstructorPropertiesIntrospector`


## 가변 객체
