package com.navercorp.fixturemonkey.test;

import static org.assertj.core.api.BDDAssertions.then;

import net.jqwik.api.Property;

import com.navercorp.fixturemonkey.LabMonkey;
import com.navercorp.fixturemonkey.test.IntrospectorGetterTestSpecs.StringClass;
import com.navercorp.fixturemonkey.test.IntrospectorGetterTestSpecs.StringGetter;
import com.navercorp.fixturemonkey.test.IntrospectorGetterTestSpecs.StringGetterAnnotation;
import com.navercorp.fixturemonkey.test.IntrospectorGetterTestSpecs.StringGetterSetter;
import com.navercorp.fixturemonkey.test.IntrospectorGetterTestSpecs.StringGetterSetterAnnotation;
import com.navercorp.fixturemonkey.test.IntrospectorGetterTestSpecs.StringSetter;
import com.navercorp.fixturemonkey.test.IntrospectorGetterTestSpecs.StringSetterAnnotation;

public class IntrospectorGetterTest {
	private static final LabMonkey SUT = LabMonkey.labMonkey();

	@Property
	void generationTest1() {
		StringClass actual = SUT.giveMeBuilder(StringClass.class)
			.sample();

		then(actual);
	}

	@Property
	void generationTest2() {
		StringGetter actual = SUT.giveMeBuilder(StringGetter.class)
			.sample();

		then(actual);
	}

	@Property
	void generationTest3() {
		StringSetter actual = SUT.giveMeBuilder(StringSetter.class)
			.sample();

		then(actual);
	}

	@Property
	void generationTest4() {
		StringGetterSetter actual = SUT.giveMeBuilder(StringGetterSetter.class)
			.sample();

		then(actual);
	}

	@Property
	void generationTest5() {
		StringGetterAnnotation actual = SUT.giveMeBuilder(StringGetterAnnotation.class)
			.sample();

		then(actual);
	}

	@Property
	void generationTest6() {
		StringSetterAnnotation actual = SUT.giveMeBuilder(StringSetterAnnotation.class)
			.sample();

		then(actual);
	}

	@Property
	void generationTest7() {
		StringGetterSetterAnnotation actual = SUT.giveMeBuilder(StringGetterSetterAnnotation.class)
			.sample();

		then(actual);
	}
}
