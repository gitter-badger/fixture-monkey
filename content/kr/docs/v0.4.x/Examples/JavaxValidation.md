---
title: "Javax.validation 어노테이션 적용"
weight: 1
---
## 1. 의존성 추가
```groovy
testImplementation("com.navercorp.fixturemonkey:fixture-monkey-javax-validation:0.4.1")
```

```xml
<dependency>
  <groupId>com.navercorp.fixturemonkey</groupId>
  <artifactId>fixture-monkey-javax-validation</artifactId>
  <version>0.4.1</version>
  <scope>test</scope>
</dependency>
```

## 2. 플러그인 추가
`LabMonkeyBuilder` 의 옵션 `plugin`을 추가합니다.

```java
LabMonkey labMonkey = LabMonkey.labMonkeyBuilder()
    .plugin(new JavaxValidationPlugin())
    .build();
```