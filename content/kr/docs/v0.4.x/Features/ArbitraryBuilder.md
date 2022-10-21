---
title: "ArbitraryBuilder"
weight: 2
---

`ArbitraryBuilder`는 Fixture Monkey에서 제공하는 객체 생성 방법입니다.

## 생성 방법
### 타입
```java
ArbitraryBuilder<Generate> generateBuilder = labMonkey.giveMeBuilder(Generate.class);
```

### 객체
```java
Generate generate = new Generate("test");

ArbitraryBuilder<Generate> generateBuilder = labMonkey.giveMeBuilder(Generate.class);
```


## 특징
* 타입에서 생성한 ArbitraryBuilder는 항상 랜덤한 값을 생성합니다.
* `fixed` 연산 혹은 객체에서 생성한 ArbitraryBuilder를 사용하면 고정된 값을 반환합니다.


## 연산
### 값을 변경하는 연산
#### set
`ArbitraryBuilder`에서 생성하는 객체의 필드 값을 변경합니다.

#### setPostCondition
{{< alert color="warning" title="Warning">}}
setPostCondition은 성능상 이슈가 있어 사용을 권장하지 않습니다.
{{< /alert >}}

`ArbitraryBuilder`에서 생성하는 객체의 필드 값에 조건을 설정합니다.


#### size
`ArbitraryBuilder`에서 생성하는 컨테이너 크기를 변경합니다.

**다른 모든 연산들보다 먼저 실행됩니다.**

#### fixed
`ArbitraryBuilder`에서 반환하는 객체 값을 고정합니다.

fixed를 실행한 `ArbitraryBuilder` 에서는 항상 같은 값을 반환합니다.

### 타입을 변경하는 연산
* map
* zip
