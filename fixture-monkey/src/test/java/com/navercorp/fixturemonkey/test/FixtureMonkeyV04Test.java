/*
 * Fixture Monkey
 *
 * Copyright (c) 2021-present NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.fixturemonkey.test;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenNoException;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.domains.Domain;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.ArbitraryBuilders;
import com.navercorp.fixturemonkey.LabMonkey;
import com.navercorp.fixturemonkey.api.type.TypeReference;
import com.navercorp.fixturemonkey.customizer.ExpressionSpec;
import com.navercorp.fixturemonkey.resolver.OldManipulatorOptimizer;
import com.navercorp.fixturemonkey.test.ComplexManipulatorTestSpecs.IntValue;
import com.navercorp.fixturemonkey.test.ComplexManipulatorTestSpecs.NestedStringList;
import com.navercorp.fixturemonkey.test.ComplexManipulatorTestSpecs.StringAndInt;
import com.navercorp.fixturemonkey.test.ComplexManipulatorTestSpecs.StringValue;
import com.navercorp.fixturemonkey.test.FixtureMonkeyV04TestSpecs.ComplexObject;
import com.navercorp.fixturemonkey.test.FixtureMonkeyV04TestSpecs.ListWithAnnotation;
import com.navercorp.fixturemonkey.test.FixtureMonkeyV04TestSpecs.SimpleObject;
import com.navercorp.fixturemonkey.test.SimpleManipulatorTestSpecs.IntegerList;
import com.navercorp.fixturemonkey.test.SimpleManipulatorTestSpecs.ListListString;
import com.navercorp.fixturemonkey.test.SimpleManipulatorTestSpecs.MapKeyIntegerValueInteger;
import com.navercorp.fixturemonkey.test.SimpleManipulatorTestSpecs.NestedStringValueList;
import com.navercorp.fixturemonkey.test.SimpleManipulatorTestSpecs.StringList;
import com.navercorp.fixturemonkey.test.SimpleManipulatorTestSpecs.TwoString;
import com.navercorp.fixturemonkey.test.FixtureMonkeyV04TestSpecs.StringPair;

class FixtureMonkeyV04Test {
	private static final LabMonkey SUT = LabMonkey.labMonkeyBuilder()
		.manipulatorOptimizer(new OldManipulatorOptimizer())
		.build();

	@Property
	void sampleWithType() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class).sample();

		// then
		then(actual.getList()).isNotNull();
		then(actual.getMap()).isNotNull();
		then(actual.getMapEntry()).isNotNull();
	}

	@Property
	void sampleWithTypeReference() {
		TypeReference<ComplexObject> type = new TypeReference<ComplexObject>() {
		};

		// when
		ComplexObject actual = SUT.giveMeBuilder(type).sample();

		// then
		then(actual.getList()).isNotNull();
		then(actual.getMap()).isNotNull();
		then(actual.getMapEntry()).isNotNull();
	}

	@Property
	void set() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("str", "str")
			.sample();

		// then
		then(actual.getStr()).isEqualTo("str");
	}

	@Property
	void setDecomposedValue() {
		// given
		SimpleObject expected = new SimpleObject();
		expected.setInstant(Instant.now());
		expected.setOptionalString(Optional.of("test"));

		// when
		SimpleObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("object", expected)
			.set("object.str", "str")
			.sample()
			.getObject();

		// then
		then(actual.getInstant()).isEqualTo(expected.getInstant());
		then(actual.getOptionalString()).isEqualTo(expected.getOptionalString());
		then(actual.getStr()).isEqualTo("str");
	}

	@Property
	void setArbitrary() {
		// given
		SimpleObject expected = new SimpleObject();
		expected.setInstant(Instant.now());
		expected.setOptionalString(Optional.of("test"));

		// when
		SimpleObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("object", Arbitraries.just(expected))
			.set("object.str", "str")
			.sample()
			.getObject();

		// then
		then(actual.getInstant()).isEqualTo(expected.getInstant());
		then(actual.getOptionalString()).isEqualTo(expected.getOptionalString());
		then(actual.getStr()).isEqualTo("str");
	}

	@Property
	void setOptional() {
		// given
		Optional<String> optional = Optional.of("test");

		// when
		ArbitraryBuilder<SimpleObject> optionalString = SUT.giveMeBuilder(SimpleObject.class)
			.set("optionalString", optional);
		Optional<String> actual = optionalString
			.sample()
			.getOptionalString();

		then(actual).isEqualTo(optional);
	}

	@Property
	void setDecomposedList() {
		// given
		List<String> expected = new ArrayList<>();
		expected.add("a");
		expected.add("b");
		expected.add("c");
		expected.add("d");
		expected.add("e");

		// when
		List<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("strList", expected)
			.sample()
			.getStrList();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setDecomposedSet() {
		// given
		Set<String> expected = new HashSet<>();
		expected.add("a");
		expected.add("b");
		expected.add("c");
		expected.add("d");
		expected.add("e");

		// when
		Set<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("strSet", expected)
			.sample()
			.getStrSet();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setDecomposedMap() {
		// given
		Map<String, SimpleObject> expected = new HashMap<>();
		expected.put("a", new SimpleObject());
		expected.put("b", new SimpleObject());
		expected.put("c", new SimpleObject());
		expected.put("d", new SimpleObject());
		expected.put("e", new SimpleObject());

		// when
		Map<String, SimpleObject> actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("map", expected)
			.sample()
			.getMap();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setDecomposedMapEntry() {
		// given
		Map.Entry<String, SimpleObject> expected = new SimpleEntry<>("a", new SimpleObject());

		// when
		Map.Entry<String, SimpleObject> actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("mapEntry", expected)
			.sample()
			.getMapEntry();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setDecomposedOptional() {
		// given
		Optional<String> expected = Optional.of("test");

		// when
		Optional<String> actual = SUT.giveMeBuilder(SimpleObject.class)
			.set("optionalString", expected)
			.sample()
			.getOptionalString();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setDecomposedOptionalEmpty() {
		// given
		Optional<String> expected = Optional.empty();

		// when
		Optional<String> actual = SUT.giveMeBuilder(SimpleObject.class)
			.set("optionalString", expected)
			.sample()
			.getOptionalString();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setDecomposedOptionalInt() {
		// given
		OptionalInt expected = OptionalInt.of(0);

		// when
		OptionalInt actual = SUT.giveMeBuilder(SimpleObject.class)
			.set("optionalInt", expected)
			.sample()
			.getOptionalInt();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setDecomposedOptionalLong() {
		// given
		OptionalLong expected = OptionalLong.of(0L);

		// when
		OptionalLong actual = SUT.giveMeBuilder(SimpleObject.class)
			.set("optionalLong", expected)
			.sample()
			.getOptionalLong();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setDecomposedOptionalDouble() {
		// given
		OptionalDouble expected = OptionalDouble.of(0.d);

		// when
		OptionalDouble actual = SUT.giveMeBuilder(SimpleObject.class)
			.set("optionalDouble", expected)
			.sample()
			.getOptionalDouble();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setAllFields() {
		// when
		StringPair actual = SUT.giveMeBuilder(StringPair.class)
			.set("*", "str")
			.sample();

		then(actual.getValue1()).isEqualTo("str");
		then(actual.getValue2()).isEqualTo("str");
	}

	@Property
	void sizeZero() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.size("strList", 0)
			.sample();

		// then
		then(actual.getStrList()).hasSize(0);
	}

	@Property
	void size() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.size("strList", 10)
			.sample();

		// then
		then(actual.getStrList()).hasSize(10);
	}

	@Property
	void sizeArray() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.size("strArray", 10)
			.sample();

		// then
		then(actual.getStrArray()).hasSize(10);
	}

	@Property
	void sizeMinMax() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.size("strList", 3, 8)
			.sample();

		// then
		then(actual.getStrList()).hasSizeBetween(3, 8);
	}

	@Property
	void sizeMinIsBiggerThanMax() {
		// when
		thenThrownBy(() ->
			SUT.giveMeBuilder(ComplexObject.class)
				.size("strList", 5, 1)
				.sample()
		).isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("should be min > max");
	}

	@Property
	void minSize() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.minSize("strList", 10)
			.sample();

		// then
		then(actual.getStrList()).hasSizeGreaterThanOrEqualTo(10);
	}

	@Property
	void maxSize() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.maxSize("strList", 10)
			.sample();

		// then
		then(actual.getStrList()).hasSizeLessThanOrEqualTo(10);
	}

	@Property
	void maxSizeZero() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.maxSize("strList", 0)
			.sample();

		// then
		then(actual.getStrList()).hasSizeLessThanOrEqualTo(0);
	}

	@Property
	void notFixedSampleReturnsDiff() {
		// when
		ArbitraryBuilder<ComplexObject> fixedArbitraryBuilder = SUT.giveMeBuilder(ComplexObject.class);

		// then
		ComplexObject sample1 = fixedArbitraryBuilder.sample();
		ComplexObject sample2 = fixedArbitraryBuilder.sample();
		then(sample1).isNotEqualTo(sample2);
	}

	@Property
	void fixedSampleReturnsSame() {
		// when
		ArbitraryBuilder<ComplexObject> fixedArbitraryBuilder = SUT.giveMeBuilder(ComplexObject.class)
			.fixed();

		// then
		ComplexObject sample1 = fixedArbitraryBuilder.sample();
		ComplexObject sample2 = fixedArbitraryBuilder.sample();
		then(sample1).isEqualTo(sample2);
	}

	@Property
	void arbitraryFixedSampleReturnsSame() {
		// when
		ArbitraryBuilder<ComplexObject> fixedArbitraryBuilder = SUT.giveMeBuilder(ComplexObject.class)
			.set("str", Arbitraries.of("value1", "value2"))
			.fixed();

		// then
		ComplexObject sample1 = fixedArbitraryBuilder.sample();
		ComplexObject sample2 = fixedArbitraryBuilder.sample();
		then(sample1).isEqualTo(sample2);
	}

	@Property
	void apply() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("str", "set")
			.apply((it, builder) ->
				builder.size("strList", 1)
					.set("strList[0]", it.getStr())
			)
			.sample()
			.getStrList()
			.get(0);

		then(actual).isEqualTo("set");
	}

	@Property
	void applyWithoutAnyManipulators() {
		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.apply((it, builder) ->
				builder.size("strList", 1)
					.set("strList[0]", it.getStr())
			)
			.sample();

		// then
		String actualStr = actual.getStr();
		List<String> actualStrList = actual.getStrList();
		then(actualStrList).hasSize(1);
		then(actualStrList.get(0)).isEqualTo(actualStr);
	}

	@Property
	void applyNotAffectedManipulatorsAfterApply() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("str", "set")
			.apply((it, builder) ->
				builder.size("strList", 1)
					.set("strList[0]", it.getStr())
			)
			.set("str", "afterApply")
			.sample()
			.getStrList()
			.get(0);

		then(actual).isEqualTo("set");
	}

	@Property
	void acceptIfAlwaysTrue() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.acceptIf(
				it -> true,
				builder -> builder.set("str", "set")
			)
			.sample()
			.getStr();

		then(actual).isEqualTo("set");
	}

	@Property
	void acceptIf() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("str", "set")
			.acceptIf(
				it -> "set".equals(it.getStr()),
				builder -> builder
					.size("strList", 1)
					.set("strList[0]", "set")
			)
			.sample()
			.getStrList()
			.get(0);

		then(actual).isEqualTo("set");
	}

	@Property
	void setRootJavaType() {
		// given
		String expected = "test";

		// when
		String actual = SUT.giveMeBuilder(String.class)
			.set(expected)
			.sample();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setRootComplexType() {
		ComplexObject expected = new ComplexObject();
		expected.setStr("test");

		// when
		ComplexObject actual = SUT.giveMeBuilder(ComplexObject.class)
			.set(expected)
			.sample();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setWithLimit() {
		// when
		List<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.size("strList", 3)
			.set("strList[*]", "test", 1)
			.sample()
			.getStrList();

		// then
		then(actual).anyMatch("test"::equals);
		then(actual).anyMatch(it -> !"test".equals(it));
	}

	@Property
	void setNull() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNull("str")
			.sample()
			.getStr();

		// then
		then(actual).isNull();
	}

	@Property
	void setNullList() {
		// when
		List<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNull("strList")
			.sample()
			.getStrList();

		// then
		then(actual).isNull();
	}

	@Property
	void setNullMap() {
		// when
		Map<String, SimpleObject> actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNull("map")
			.sample()
			.getMap();

		// then
		then(actual).isNull();
	}

	@Property
	void setNullMapEntry() {
		// when
		Map.Entry<String, SimpleObject> actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNull("mapEntry")
			.sample()
			.getMapEntry();

		// then
		then(actual).isNull();
	}

	@Property
	void setNotNullString() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNotNull("str")
			.sample()
			.getStr();

		// then
		then(actual).isNotNull();
	}

	@Property
	void setNotNullList() {
		// when
		List<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNotNull("strList")
			.sample()
			.getStrList();

		// then
		then(actual).isNotNull();
	}

	@Property
	void setNotNullMap() {
		// when
		Map<String, SimpleObject> actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNotNull("map")
			.sample()
			.getMap();

		// then
		then(actual).isNotNull();
	}

	@Property
	void setNotNullMapEntry() {
		// when
		Map.Entry<String, SimpleObject> actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNotNull("mapEntry")
			.sample()
			.getMapEntry();

		// then
		then(actual).isNotNull();
	}

	@Property
	void setPostCondition() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.setPostCondition("str", String.class, it -> it.length() > 5)
			.sample()
			.getStr();

		// then
		then(actual).hasSizeGreaterThan(5);
	}

	@Property
	void setPostConditionRoot() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.setPostCondition(it -> it.getStr() != null && it.getStr().length() > 5)
			.sample()
			.getStr();

		// then
		then(actual).hasSizeGreaterThan(5);
	}

	@Property
	void setPostConditionWrongTypeThrows() {
		thenThrownBy(
			() -> SUT.giveMeBuilder(ComplexObject.class)
				.setPostCondition("str", Integer.class, it -> it > 5)
				.sample()
		).isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Wrong type filter is applied.");
	}

	@Property
	void mapWhenNull() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNull("str")
			.map(ComplexObject::getStr)
			.sample();

		then(actual).isNull();
	}

	@Property
	void mapWhenNotNull() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.setNotNull("str")
			.map(ComplexObject::getStr)
			.sample();

		then(actual).isNotNull();
	}

	@Property
	void mapToFixedValue() {
		// when
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.map(it -> "test")
			.sample();

		then(actual).isEqualTo("test");
	}

	@Property
	void mapKeyIsNotNull() {
		// when
		Set<String> actual = SUT.giveMeBuilder(new TypeReference<Map<String, String>>() {
			})
			.sample()
			.keySet();

		then(actual).allMatch(Objects::nonNull);
	}

	@Property(tries = 50)
	void sampleAfterMapTwiceReturnsDiff() {
		ArbitraryBuilder<String> arbitraryBuilder = SUT.giveMeBuilder(ComplexObject.class)
			.set("str", Arbitraries.strings().ascii().filter(it -> !it.isEmpty()))
			.map(ComplexObject::getStr);

		// when
		String actual = arbitraryBuilder.sample();

		// then
		String notExpected = arbitraryBuilder.sample();
		then(actual).isNotEqualTo(notExpected);
	}

	@Property
	void giveMeBuilderWithValue() {
		SimpleObject expected = new SimpleObject();
		expected.setStr("test");
		expected.setOptionalInt(OptionalInt.of(-1));

		SimpleObject actual = SUT.giveMeBuilder(expected)
			.sample();

		then(actual).isEqualTo(expected);
	}

	@Property
	void giveMeZipList() {
		// given
		List<ArbitraryBuilder<?>> list = new ArrayList<>();
		list.add(SUT.giveMeBuilder(StringValue.class));
		list.add(SUT.giveMeBuilder(IntValue.class));

		// when
		StringAndInt actual = ArbitraryBuilders.zip(
			list,
			(l) -> {
				StringAndInt result = new StringAndInt();
				result.setValue1((StringValue)l.get(0));
				result.setValue2((IntValue)l.get(1));
				return result;
			}
		).sample();

		then(actual.getValue1()).isNotNull();
		then(actual.getValue2()).isNotNull();
	}

	@Property
	void giveMeZipEmptyListThrows() {
		// given
		List<ArbitraryBuilder<?>> list = new ArrayList<>();

		thenThrownBy(
			() -> ArbitraryBuilders.zip(
				list,
				(l) -> new StringAndInt()
			).sample()
		).isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("zip should be used in more than two ArbitraryBuilders, given size");
	}

	@Property
	void zipThree() {
		// given
		ArbitraryBuilder<StringValue> s1 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s1");
		ArbitraryBuilder<StringValue> s2 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s2");
		ArbitraryBuilder<StringValue> s3 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s3");

		// when
		NestedStringList actual = ArbitraryBuilders.zip(s1, s2, s3, (a1, a2, a3) -> {
			List<StringValue> list = new ArrayList<>();
			list.add(a1);
			list.add(a2);
			list.add(a3);

			NestedStringList result = new NestedStringList();
			result.setValues(list);
			return result;
		}).sample();

		then(actual.getValues()).hasSize(3);
		then(actual.getValues().get(0).getValue()).isEqualTo("s1");
		then(actual.getValues().get(1).getValue()).isEqualTo("s2");
		then(actual.getValues().get(2).getValue()).isEqualTo("s3");
	}

	@Property
	void giveMeZipWithThree() {
		// given
		ArbitraryBuilder<StringValue> s1 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s1");
		ArbitraryBuilder<StringValue> s2 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s2");
		ArbitraryBuilder<StringValue> s3 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s3");

		// when
		NestedStringList actual = s1.zipWith(s2, s3, (a1, a2, a3) -> {
			List<StringValue> list = new ArrayList<>();
			list.add(a1);
			list.add(a2);
			list.add(a3);

			NestedStringList result = new NestedStringList();
			result.setValues(list);
			return result;
		}).sample();

		then(actual.getValues()).hasSize(3);
		then(actual.getValues().get(0).getValue()).isEqualTo("s1");
		then(actual.getValues().get(1).getValue()).isEqualTo("s2");
		then(actual.getValues().get(2).getValue()).isEqualTo("s3");
	}

	@Property
	void zipFour() {
		// given
		ArbitraryBuilder<StringValue> s1 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s1");
		ArbitraryBuilder<StringValue> s2 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s2");
		ArbitraryBuilder<StringValue> s3 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s3");
		ArbitraryBuilder<StringValue> s4 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s4");

		// when
		NestedStringList actual = ArbitraryBuilders.zip(s1, s2, s3, s4, (a1, a2, a3, a4) -> {
			List<StringValue> list = new ArrayList<>();
			list.add(a1);
			list.add(a2);
			list.add(a3);
			list.add(a4);

			NestedStringList result = new NestedStringList();
			result.setValues(list);
			return result;
		}).sample();

		then(actual.getValues()).hasSize(4);
		then(actual.getValues().get(0).getValue()).isEqualTo("s1");
		then(actual.getValues().get(1).getValue()).isEqualTo("s2");
		then(actual.getValues().get(2).getValue()).isEqualTo("s3");
		then(actual.getValues().get(3).getValue()).isEqualTo("s4");
	}

	@Property
	void giveMeZipWithFour() {
		// given
		ArbitraryBuilder<StringValue> s1 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s1");
		ArbitraryBuilder<StringValue> s2 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s2");
		ArbitraryBuilder<StringValue> s3 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s3");
		ArbitraryBuilder<StringValue> s4 = SUT.giveMeBuilder(StringValue.class)
			.set("value", "s4");

		// when
		NestedStringList actual = s1.zipWith(s2, s3, s4, (a1, a2, a3, a4) -> {
			List<StringValue> list = new ArrayList<>();
			list.add(a1);
			list.add(a2);
			list.add(a3);
			list.add(a4);

			NestedStringList result = new NestedStringList();
			result.setValues(list);
			return result;
		}).sample();

		then(actual.getValues()).hasSize(4);
		then(actual.getValues().get(0).getValue()).isEqualTo("s1");
		then(actual.getValues().get(1).getValue()).isEqualTo("s2");
		then(actual.getValues().get(2).getValue()).isEqualTo("s3");
		then(actual.getValues().get(3).getValue()).isEqualTo("s4");
	}

	@Property
	void giveMeZipWith() {
		// given
		ArbitraryBuilder<String> stringArbitraryBuilder = SUT.giveMeBuilder(String.class);

		// when
		String actual = SUT.giveMeBuilder(Integer.class)
			.zipWith(stringArbitraryBuilder, (integer, string) -> integer + "" + string)
			.sample();

		then(actual).isNotNull();
	}

	@Property
	void giveMeZipTwoElement() {
		// given
		ArbitraryBuilder<String> stringArbitraryBuilder = SUT.giveMeBuilder(String.class);
		ArbitraryBuilder<Integer> integerArbitraryBuilder = SUT.giveMeBuilder(Integer.class);

		// when
		String actual = ArbitraryBuilders.zip(
			stringArbitraryBuilder,
			integerArbitraryBuilder,
			(integer, string) -> integer + "" + string
		).sample();

		then(actual).isNotNull();
	}

	@Property
	void giveMeZipReturnsNew() {
		// given
		ArbitraryBuilder<String> stringArbitraryBuilder = SUT.giveMeBuilder(String.class);
		ArbitraryBuilder<Integer> integerArbitraryBuilder = SUT.giveMeBuilder(Integer.class);

		// when
		Arbitrary<String> zippedArbitraryBuilder = ArbitraryBuilders.zip(
			stringArbitraryBuilder,
			integerArbitraryBuilder,
			(integer, string) -> integer + "" + string
		).build();

		// then
		String result1 = zippedArbitraryBuilder.sample();
		String result2 = zippedArbitraryBuilder.sample();
		then(result1).isNotEqualTo(result2);
	}

	@Property
	void setNullFixedReturnsNull() {
		SimpleObject actual = SUT.giveMeBuilder(SimpleObject.class)
			.setNull("$")
			.fixed()
			.sample();

		then(actual).isNull();
	}

	@Property
	void setListElement() {
		String expected = "test";

		String actual = SUT.giveMeBuilder(new TypeReference<List<String>>() {
			})
			.size("$", 1)
			.set("$[0]", expected)
			.sample()
			.get(0);

		then(actual).isEqualTo(expected);
	}

	@Property
	void sizeSmallerRemains() {
		String expected = "test";
		List<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.size("strList", 2)
			.set("strList[0]", expected)
			.set("strList[1]", expected)
			.size("strList", 1)
			.sample()
			.getStrList();

		then(actual).hasSize(1);
		then(actual.get(0)).isEqualTo(expected);
	}

	@Property
	void applySetElementNull() {
		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.apply((obj, builder) -> builder.size("strList", 1)
				.setNull("strList[0]")
			)
			.sample()
			.getStrList()
			.get(0);

		then(actual).isNull();
	}

	@Property
	void setAndSetNull() {
		String actual = SUT.giveMeBuilder(SimpleObject.class)
			.set("str", "test")
			.setNull("str")
			.sample()
			.getStr();

		then(actual).isNull();
	}

	@Property
	void setAfterBuildNotAffected() {
		// given
		ArbitraryBuilder<SimpleObject> builder = SUT.giveMeBuilder(SimpleObject.class);
		Arbitrary<SimpleObject> buildArbitrary = builder.build();

		// when
		ArbitraryBuilder<SimpleObject> actual = builder.set("str", "set");

		// then
		SimpleObject actualSample = actual.sample();
		SimpleObject buildSample = buildArbitrary.sample();
		then(actualSample).isNotEqualTo(buildSample);
		then(actualSample.getStr()).isEqualTo("set");
	}

	@Property
	void applySampleTwiceReturnsDiff() {
		ArbitraryBuilder<SimpleObject> builder = SUT.giveMeBuilder(SimpleObject.class)
			.apply((obj, b) -> {
			});

		SimpleObject actual = builder.sample();

		SimpleObject expected = builder.sample();
		then(actual).isNotEqualTo(expected);
	}

	@Property
	void setPrimitiveToReference() {
		int integer = SUT.giveMeBuilder(SimpleObject.class)
			.set("integer", Integer.valueOf("1234"))
			.sample()
			.getInteger();

		then(integer).isEqualTo(1234);
	}

	@Property
	void setReferenceToPrimitive() {
		int integer = SUT.giveMeBuilder(SimpleObject.class)
			.set("wrapperInteger", 1234)
			.sample()
			.getWrapperInteger();

		then(integer).isEqualTo(1234);
	}

	@Property
	void copyValidOnly() {
		thenNoException()
			.isThrownBy(() -> SUT.giveMeBuilder(ListWithAnnotation.class)
				.size("values", 0)
				.validOnly(false)
				.copy()
				.sample());
	}

	@Property
	void giveMePrimitiveArrayToBuilder() {
		int[] actual = SUT.giveMeBuilder(ComplexObject.class)
			.fixed()
			.sample()
			.getIntArray();

		then(actual).isNotNull();
	}

	@Property
	void setFieldWhichObjectIsFixedNull() {
		String expected = "test";

		String actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("object", Arbitraries.just(null))
			.set("object.str", expected)
			.sample()
			.getObject()
			.getStr();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setFieldWhichRootIsFixedNull() {
		String expected = "test";

		String actual = SUT.giveMeBuilder(SimpleObject.class)
			.set("$", Arbitraries.just(null))
			.set("str", expected)
			.sample()
			.getStr();

		then(actual).isEqualTo(expected);
	}

	@Property
	void setLazyValue() {
		ArbitraryBuilder<String> variable = SUT.giveMeBuilder(String.class);
		ArbitraryBuilder<String> builder = SUT.giveMeBuilder(String.class)
			.setLazy("$", () -> variable.sample());
		variable.set("test");

		String actual = builder.sample();

		then(actual).isEqualTo("test");
	}

	@Property
	void setLazyValueWithLimit() {
		// when
		ArbitraryBuilder<String> variable = SUT.giveMeBuilder(String.class);
		ArbitraryBuilder<ComplexObject> builder = SUT.giveMeBuilder(ComplexObject.class)
			.size("strList", 3)
			.setLazy("strList[*]", () -> variable.sample(), 1);
		variable.set("test");

		List<String> actual = builder.sample().getStrList();

		// then
		then(actual).anyMatch("test"::equals);
		then(actual).anyMatch(it -> !"test".equals(it));
	}

	@Property(tries = 1)
	void setLazyValueSampleGivesDifferentValue() {
		ArbitraryBuilder<String> variable = SUT.giveMeBuilder(String.class);
		ArbitraryBuilder<String> builder = SUT.giveMeBuilder(String.class)
			.setLazy("$", () -> variable.sample());
		String expected = builder.sample();

		String actual = builder.sample();

		then(actual).isNotEqualTo(expected);
	}

	@Property
	void setArbitraryBuilder() {
		SimpleObject actual = SUT.giveMeBuilder(SimpleObject.class)
			.set("str", SUT.giveMeBuilder(String.class).set("$", "test"))
			.sample();

		then(actual.getStr()).isEqualTo("test");
	}

	@Property
	void giveMeListTypeApply() {
		SimpleObject actual = SUT.giveMeBuilder(new TypeReference<List<SimpleObject>>() {
			})
			.size("$", 1)
			.apply((it, builder) -> builder.set("$[0].str", it.get(0).getInteger() + ""))
			.sample()
			.get(0);

		then(actual.getStr()).isEqualTo(actual.getInteger() + "");
	}

	@Property
	void setIterable() {
		String expected = "test";

		Iterable<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("strIterable", Collections.singletonList(expected))
			.sample()
			.getStrIterable();

		then(actual.iterator().next()).isEqualTo(expected);
	}

	@Property
	void setIterator() {
		String expected = "test";

		Iterator<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("strIterator", Stream.of(expected).iterator())
			.sample()
			.getStrIterator();

		then(actual.next()).isEqualTo(expected);
	}

	@Property
	void setStream() {
		String expected = "test";

		Stream<String> actual = SUT.giveMeBuilder(ComplexObject.class)
			.set("strStream", Stream.of(expected))
			.sample()
			.getStrStream();

		then(actual.collect(Collectors.toList()).get(0)).isEqualTo(expected);
	}

	@Property
	void giveMeSpecSet() {
		// when
		SimpleManipulatorTestSpecs.IntValue actual = SUT.giveMeBuilder(SimpleManipulatorTestSpecs.IntValue.class)
			.spec(new ExpressionSpec().set("value", -1))
			.sample();

		then(actual.getValue()).isEqualTo(-1);
	}

	@Property
	void giveMeSpecSetArbitrary() {
		// when
		SimpleManipulatorTestSpecs.IntValue actual = SUT.giveMeBuilder(SimpleManipulatorTestSpecs.IntValue.class)
			.spec(new ExpressionSpec().set("value", Arbitraries.just(1)))
			.sample();

		then(actual.getValue()).isEqualTo(1);
	}

	@Property
	void giveMeListSize() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec().size("values", 1, 1))
			.sample();

		then(actual.getValues()).hasSize(1);
	}

	@Property
	void giveMeSetNull() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec().setNull("values"))
			.sample();

		then(actual.getValues()).isNull();
	}

	@Property
	void giveMeSizeAfterSetNullReturnsNull() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec()
				.setNull("values")
				.size("values", 1, 1)
			)
			.sample();

		then(actual.getValues()).isNull();
	}

	@Property
	void giveMeSetAfterSetNullReturnsNotNull() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec()
				.setNull("values")
				.size("values", 1, 1)
				.set("values[0]", 0)
			)
			.sample();

		then(actual.getValues()).isNotNull();
		then(actual.getValues()).hasSize(1);
		then(actual.getValues().get(0)).isEqualTo(0);
	}

	@Property
	void giveMeSetNotNullAfterSetNullReturnsNotNull() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec()
				.setNull("values")
				.setNotNull("values")
			)
			.sample();

		then(actual.getValues()).isNotNull();
	}

	@Property
	void giveMeSetNullAfterSetNotNullReturnsNull() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec()
				.setNotNull("values")
				.setNull("values")
			)
			.sample();

		then(actual.getValues()).isNull();
	}

	@Property
	void giveMeSetNullAfterSetReturnsNull() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec()
				.size("values", 1, 1)
				.set("values[0]", 0)
				.setNull("values")
			)
			.sample();

		then(actual.getValues()).isNull();
	}

	@Property
	void giveMeSpecPostCondition() {
		// when
		SimpleManipulatorTestSpecs.IntValue actual = SUT.giveMeBuilder(SimpleManipulatorTestSpecs.IntValue.class)
			.spec(new ExpressionSpec().setPostCondition(
				"value",
				Integer.class,
				value -> value >= 0 && value <= 100
			))
			.sample();

		then(actual.getValue()).isBetween(0, 100);
	}

	@Property
	void giveMeSpecPostConditionType() {
		// when
		SimpleManipulatorTestSpecs.IntValue actual = SUT.giveMeBuilder(SimpleManipulatorTestSpecs.IntValue.class)
			.spec(new ExpressionSpec().setPostCondition(
				"value",
				Integer.class,
				value -> value >= 0 && value <= 100
			))
			.sample();

		then(actual.getValue()).isBetween(0, 100);
	}

	@Property
	void giveMePostConditionIndex() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec()
				.setPostCondition("values[0]", Integer.class, value -> value >= 0 && value <= 100)
				.size("values", 1, 1)
			)
			.sample();

		then(actual.getValues()).hasSize(1);
		then(actual.getValues().get(0)).isBetween(0, 100);
	}

	@Property
	@Domain(SimpleManipulatorTestSpecs.class)
	void giveMeObjectToBuilderSet(@ForAll SimpleManipulatorTestSpecs.IntValue expected) {
		SimpleManipulatorTestSpecs.IntValue actual = SUT.giveMeBuilder(expected)
			.set("value", 1)
			.sample();

		then(actual.getValue()).isEqualTo(1);
	}

	@Property
	@Domain(SimpleManipulatorTestSpecs.class)
	void giveMeObjectToBuilderSetWithExpressionGenerator(@ForAll SimpleManipulatorTestSpecs.IntValue expected) {
		SimpleManipulatorTestSpecs.IntValue actual = SUT.giveMeBuilder(expected)
			.set((resolver) -> "value", 1)
			.sample();

		then(actual.getValue()).isEqualTo(1);
	}

	@Property
	void giveMeObjectToBuilderSetIndex() {
		// given
		IntegerList expected = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec().size("values", 2, 2))
			.sample();

		// when
		IntegerList actual = SUT.giveMeBuilder(expected)
			.set("values[1]", 1)
			.sample();

		then(actual.getValues().get(1)).isEqualTo(1);
	}

	@Property
	void giveMeListSpecMaxSize() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.spec(new ExpressionSpec()
				.list("values",
					it -> it.ofMaxSize(2)
				)
			)
			.sample();

		then(actual.getValues().size()).isLessThanOrEqualTo(2);
	}

	@Property
	void giveMeListSpecSizeBetween() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.spec(new ExpressionSpec()
				.list("values",
					it -> it.ofSizeBetween(1, 3)
				)
			)
			.sample();

		then(actual.getValues().size()).isBetween(1, 3);
	}

	@Property
	void giveMeSetAllName() {
		// when
		TwoString actual = SUT.giveMeBuilder(TwoString.class)
			.set("*", "set")
			.sample();

		then(actual.getValue1()).isEqualTo("set");
		then(actual.getValue2()).isEqualTo("set");
	}

	@Property
	void giveMeListExactSize() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.size("values", 3)
			.sample();

		then(actual.getValues().size()).isEqualTo(3);
	}

	@Property
	void giveMeListExactSizeWithExpressionGenerator() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.size((resolver) -> "values", 3)
			.sample();

		then(actual.getValues().size()).isEqualTo(3);
	}

	@Property
	void giveMeSizeMap() {
		// when
		MapKeyIntegerValueInteger actual = SUT.giveMeBuilder(MapKeyIntegerValueInteger.class)
			.size("values", 2, 2)
			.sample();

		then(actual.getValues()).hasSize(2);
	}

	@Property
	void giveMeSizeMapWithExpressionGenerator() {
		// when
		MapKeyIntegerValueInteger actual = SUT.giveMeBuilder(MapKeyIntegerValueInteger.class)
			.size((resolver) -> "values", 2, 2)
			.sample();

		then(actual.getValues()).hasSize(2);
	}

	@Property
	void giveMeSetRightOrder() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.spec(new ExpressionSpec()
				.list("values",
					it -> it
						.ofSize(3)
						.setElement(0, "field1")
						.setElement(1, "field2")
						.setElement(2, "field3")
				)
			)
			.sample();

		// then
		List<String> values = actual.getValues();
		then(values.size()).isEqualTo(3);
		then(values.get(0)).isEqualTo("field1");
		then(values.get(1)).isEqualTo("field2");
		then(values.get(2)).isEqualTo("field3");
	}

	@Property
	void giveMePostConditionRightOrder() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.spec(new ExpressionSpec()
				.list("values",
					(it) -> it.ofSize(2)
						.setElementPostCondition(0, String.class, s -> s.length() > 5)
						.setElementPostCondition(1, String.class, s -> s.length() > 10)
				))
			.sample();

		// then
		List<String> values = actual.getValues();
		then(values.size()).isEqualTo(2);
		then(values.get(0).length()).isGreaterThan(5);
		then(values.get(1).length()).isGreaterThan(10);
	}

	@Property
	void giveMeListSpecMinSize() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.spec(new ExpressionSpec()
				.list("values",
					it -> it.ofMinSize(1)
				)
			)
			.sample();

		then(actual.getValues().size()).isGreaterThanOrEqualTo(1);
	}

	@Property
	void giveMeSpecAny() {
		// given
		ExpressionSpec specOne = new ExpressionSpec()
			.list("values", it -> it
				.ofSize(1)
				.setElement(0, 1)
			);
		ExpressionSpec specTwo = new ExpressionSpec()
			.list("values", it -> it
				.ofSize(2)
				.setElement(0, 1)
				.setElement(1, 2)
			);

		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.specAny(specOne, specTwo)
			.sample();

		// then
		IntegerList expectedOne = new IntegerList();
		expectedOne.setValues(new ArrayList<>());
		expectedOne.getValues().add(1);

		IntegerList expectedTwo = new IntegerList();
		expectedTwo.setValues(new ArrayList<>());
		expectedTwo.getValues().add(1);
		expectedTwo.getValues().add(2);

		then(actual).isIn(expectedOne, expectedTwo);
	}

	@Property
	void giveMeSpecAnyWithEmpty() {
		thenNoException().isThrownBy(
			() -> SUT.giveMeBuilder(StringList.class)
				.specAny()
				.sample()
		);
	}

	@Property
	void giveMeSpecAnyWithNull() {
		thenNoException().isThrownBy(
			() -> SUT.giveMeBuilder(StringList.class)
				.specAny((ExpressionSpec[])null)
				.sample()
		);
	}

	@Property(tries = 2)
	void giveMeSpecAnyReturnsDiff() {
		// given
		Arbitrary<ComplexManipulatorTestSpecs.StringValue> complex = ComplexManipulatorTestSpecs.SUT.giveMeBuilder(
				ComplexManipulatorTestSpecs.StringValue.class)
			.specAny(
				new ExpressionSpec().set("value", "test1"),
				new ExpressionSpec().set("value", "test2"),
				new ExpressionSpec().set("value", "test3"),
				new ExpressionSpec().set("value", "test4"),
				new ExpressionSpec().set("value", "test5"),
				new ExpressionSpec().set("value", "test6"),
				new ExpressionSpec().set("value", "test7"),
				new ExpressionSpec().set("value", "test8"),
				new ExpressionSpec().set("value", "test9"),
				new ExpressionSpec().set("value", "test10")
			)
			.build();

		// when
		List<ComplexManipulatorTestSpecs.StringValue> sampled = complex.list().ofSize(100).sample();

		// then
		List<ComplexManipulatorTestSpecs.StringValue> distinct = sampled.stream().distinct().collect(toList());
		then(distinct.size()).isNotEqualTo(sampled.size());
	}

	@Property
	void giveMeSpecAnyFirstWithMetadataManipulatorReturnsGivenOrder() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.specAny(new ExpressionSpec().size("values", 2))
			.size("values", 1)
			.sample();

		then(actual.getValues()).hasSize(1);
	}

	@Property
	void giveMeSpecAnyLastWithMetadataManipulatorReturnsGivenOrder() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.size("values", 1)
			.specAny(new ExpressionSpec().size("values", 2))
			.sample();

		then(actual.getValues()).hasSize(2);
	}

	@Property
	void giveMeBuilderSetNull() {
		// when
		SimpleManipulatorTestSpecs.StringValue actual = SUT.giveMeBuilder(SimpleManipulatorTestSpecs.StringValue.class)
			.set("value", null)
			.sample();

		then(actual.getValue()).isNull();
	}

	@Property
	void giveMeMinSize() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.minSize("values", 2)
			.sample();

		then(actual.getValues().size()).isGreaterThanOrEqualTo(2);
	}

	@Property
	void giveMeMinSizeWithExpressionGenerator() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.minSize((resolver) -> "values", 2)
			.sample();

		then(actual.getValues().size()).isGreaterThanOrEqualTo(2);
	}

	@Property
	void giveMeMaxSize() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.maxSize("values", 10)
			.sample();

		then(actual.getValues().size()).isLessThanOrEqualTo(10);
	}

	@Property
	void giveMeMaxSizeWithExpressionGenerator() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.maxSize((resolver) -> "values", 10)
			.sample();

		then(actual.getValues().size()).isLessThanOrEqualTo(10);
	}

	@Property(tries = 10)
	void giveMeSizeMinMaxBiggerThanDefault() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.size("values", 100, 150)
			.sample();

		then(actual.getValues().size()).isBetween(100, 150);
	}

	@Property(tries = 10)
	void giveMeSizeMinBiggerThanDefaultMax() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.minSize("values", 100)
			.sample();

		then(actual.getValues().size()).isBetween(100, 110);
	}

	@Property
	void giveMeSizeMaxSizeIsZero() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.maxSize("values", 0)
			.sample();

		then(actual.getValues()).isEmpty();
	}

	@Property
	void giveMeSizeMaxSizeBeforeMinSizeThenMinSizeWorks() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.maxSize("values", 15)
			.minSize("values", 14)
			.sample();

		then(actual.getValues()).hasSizeGreaterThanOrEqualTo(14);
	}

	@Property
	void giveMeSizeMinSizeBeforeMaxSizeThenMaxSizeWorks() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.minSize("values", 14)
			.maxSize("values", 15)
			.sample();

		then(actual.getValues()).hasSizeLessThanOrEqualTo(15);
	}

	@Property
	void giveMePostConditionLimitIndex() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.size("values", 2, 2)
			.setPostCondition("values[*]", String.class, it -> it.length() > 0)
			.setPostCondition("values[*]", String.class, it -> it.length() > 5, 1)
			.sample();

		then(actual.getValues()).anyMatch(it -> it.length() > 5);
	}

	@Property
	void giveMePostConditionLimitIndexWithExpressionGenerator() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.size("values", 2, 2)
			.setPostCondition((resolver) -> "values[*]", String.class, it -> it.length() > 0)
			.setPostCondition((resolver) -> "values[*]", String.class, it -> it.length() > 5, 1)
			.sample();

		then(actual.getValues()).anyMatch(it -> it.length() > 5);
	}

	@Property
	void giveMePostConditionLimitIndexNotOverwriteIfLimitIsZeroReturnsNotPostCondition() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.setPostCondition("values[*]", String.class, it -> it.length() > 5)
			.setPostCondition("values[*]", String.class, it -> it.length() == 0, 0)
			.sample();

		then(actual.getValues()).allMatch(it -> it.length() > 5);
	}

	@Property
	void giveMeSpecListSetSize() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec().list("values", it -> it.ofSize(1)))
			.sample();

		then(actual.getValues()).hasSize(1);
	}

	@Property
	void giveMeSpecListSetElement() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofSize(1);
				it.setElement(0, 1);
			}))
			.sample();

		then(actual.getValues()).hasSize(1);
		then(actual.getValues().get(0)).isEqualTo(1);
	}

	@Property
	void giveMeSpecListAnySet() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofSize(3);
				it.any(1);
			}))
			.sample();

		then(actual.getValues()).anyMatch(it -> it == 1);
	}

	@Property
	void giveMeSpecListAnyWithoutSize() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.spec(new ExpressionSpec().list("values", it -> it.any("set")))
			.sample();

		then(actual.getValues()).anyMatch(it -> it.equals("set"));
	}

	@Property
	void giveMeSpecListAnyWithoutMaxSize() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofMinSize(5);
				it.any("set");
			}))
			.sample();

		then(actual.getValues()).anyMatch(it -> it.equals("set"));
	}

	@Property
	void giveMeSpecListAnyWithoutMinSize() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofMaxSize(2);
				it.any("set");
			}))
			.sample();

		then(actual.getValues()).anyMatch(it -> it.equals("set"));
	}

	@Property
	void giveMeSpecListAllSet() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofSize(3);
				it.all(1);
			}))
			.sample();

		then(actual.getValues()).allMatch(it -> it == 1);
	}

	@Property
	void giveMeSpecListPostConditionElement() {
		// when
		IntegerList actual = SUT.giveMeBuilder(IntegerList.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofSize(1);
				it.setElementPostCondition(0, Integer.class, postConditioned -> postConditioned > 1);
			}))
			.sample();

		then(actual.getValues()).hasSize(1);
		then(actual.getValues().get(0)).isGreaterThan(1);
	}

	@Property
	void giveMeSpecListPostConditionElementField() {
		// when
		NestedStringValueList actual = SUT.giveMeBuilder(NestedStringValueList.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofSize(1);
				it.setElementFieldPostCondition(0, "value", String.class,
					postConditioned -> postConditioned.length() > 5);
			}))
			.sample();

		then(actual.getValues()).allMatch(it -> it.getValue().length() > 5);
	}

	@Property
	void giveMeSpecListListElementSet() {
		// when
		ListListString actual = SUT.giveMeBuilder(ListListString.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofSize(1);
				it.listElement(0, nestedIt -> {
					nestedIt.ofSize(1);
					nestedIt.setElement(0, "set");
				});
			}))
			.sample();

		then(actual.getValues()).hasSize(1);
		then(actual.getValues().get(0)).hasSize(1);
		then(actual.getValues().get(0).get(0)).isEqualTo("set");
	}

	@Property
	void giveMeSpecListListElementPostCondition() {
		// when
		ListListString actual = SUT.giveMeBuilder(ListListString.class)
			.spec(new ExpressionSpec().list("values", it -> {
				it.ofSize(1);
				it.listElement(0, nestedIt -> {
					nestedIt.ofSize(1);
					nestedIt.setElementPostCondition(0, String.class, postConditioned -> postConditioned.length() > 5);
				});
			}))
			.sample();

		then(actual.getValues()).hasSize(1);
		then(actual.getValues().get(0)).hasSize(1);
		then(actual.getValues().get(0).get(0).length()).isGreaterThan(5);
	}

	@Property
	void giveMeSetLimitReturnsNotSet() {
		// when
		SimpleManipulatorTestSpecs.IntValue actual = SUT.giveMeBuilder(SimpleManipulatorTestSpecs.IntValue.class)
			.set("value", 1, 0)
			.sample();

		then(actual.getValue()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Property
	void giveMeSpecSetWithLimit() {
		// when
		SimpleManipulatorTestSpecs.IntValue actual = SUT.giveMeBuilder(SimpleManipulatorTestSpecs.IntValue.class)
			.spec(new ExpressionSpec()
				.set("value", 1, 0))
			.sample();

		then(actual.getValue()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Property
	void giveMeSpecSetIndexWithLimitReturns() {
		// when
		StringList actual = SUT.giveMeBuilder(StringList.class)
			.size("values", 2)
			.spec(new ExpressionSpec()
				.set("values[*]", "set"))
			.size("values", 3)
			.sample();

		then(actual.getValues()).anyMatch(it -> !it.equals("set"));
	}
}
