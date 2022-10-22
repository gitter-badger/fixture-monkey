---
title: "LabMonkey"
weight: 1
---
Fixure Monkey 0.4.x 에서는 `FixtureMonkey` 클래스 대신 `LabMonkey`를 사용합니다.
`FixtureMonkey`에서는 0.3.x 기능을 사용합니다.

## 생성 방법
### 기본 옵션을 사용하는 LabMonkey 생성
```java
LabMonkey labMonkey = LabMonkey.create();
```

### 옵션을 추가하여 생성하는 LabMonkey 생성
```java
LabMonkey labMonkey = LabMonkey.labMonkeyBuilder()
	+ 옵션들...
    .build();
```

## 옵션
### addContainerType
### addExceptGenerateClass
### addExceptGenerateClasses
### addExceptGeneratePackage
### addExceptGeneratePackages
### arbitraryValidator
### defaultArbitraryContainerInfo
### defaultArbitraryContainerMaxSize
### defaultDecomposedContainerValueFactory
### defaultNotNull
### defaultNullInjectGenerator
### defaultObjectPropertyGenerator
### defaultPropertyGenerator
### defaultPropertyNameResolver
### javaArbitraryResolver
### javaTimeArbitraryResolver
### javaTimeTypeArbitraryGenerator
### javaTypeArbitraryGenerator
### manipulatorOptimizer
### monkeyExpressionFactory
### nullableContainer
### nullableElement
### objectIntrospector
### plugin
### pushArbitraryContainerInfoGenerator
### pushArbitraryCustomizer
### pushAssignableTypeArbitraryCustomizer
### pushExactTypeArbitraryCustomizer
### pushArbitraryIntrospector
### pushAssignableTypeArbitraryIntrospector
### pushExactTypeArbitraryIntrospector
### pushAssignableTypeContainerPropertyGenerator
### pushExactTypeContainerPropertyGenerator
### pushAssignableTypeNullInjectGenerator
### pushAssignableTypeObjectPropertyGenerator
### pushAssignableTypePropertyNameResolver
### pushContainerIntrospector
### pushContainerPropertyGenerator
### pushObjectPropertyGenerator
### pushExactTypeObjectPropertyGenerator
### pushPropertyNameResolver
### pushExactTypePropertyNameResolver
### pushExceptGenerateType
### pushNullInjectGenerator
### pushExactTypeNullInjectGenerator
### register
### registerAssignableType
### registerExactType
### registerGroup
### useExpressionStrictMode
