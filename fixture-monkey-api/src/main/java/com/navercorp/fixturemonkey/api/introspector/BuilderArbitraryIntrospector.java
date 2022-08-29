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

package com.navercorp.fixturemonkey.api.introspector;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.platform.commons.util.ReflectionUtils;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.Builders;
import net.jqwik.api.Builders.BuilderCombinator;

import com.navercorp.fixturemonkey.api.generator.ArbitraryGeneratorContext;
import com.navercorp.fixturemonkey.api.generator.ArbitraryProperty;
import com.navercorp.fixturemonkey.api.property.Property;
import com.navercorp.fixturemonkey.api.type.Types;

@API(since = "0.4.0", status = Status.EXPERIMENTAL)
public final class BuilderArbitraryIntrospector implements ArbitraryIntrospector {
	public static final BuilderArbitraryIntrospector INSTANCE = new BuilderArbitraryIntrospector();

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public ArbitraryIntrospectorResult introspect(ArbitraryGeneratorContext context) {
		Property property = context.getProperty();
		Class<?> type = Types.getActualType(property.getType());
		if (type.isInterface()) {
			return ArbitraryIntrospectorResult.EMPTY;
		}

		List<ArbitraryProperty> childrenProperties = context.getChildren();
		Map<String, Arbitrary<?>> childrenArbitraries = context.getChildrenArbitraryContexts()
			.getArbitrariesByResolvedName();
		String builderMethodName = "builder";
		Method builderMethod = ReflectionUtils.findMethod(type, builderMethodName)
			.orElseThrow(() -> new IllegalArgumentException("Class has no builder class. " + type.getName()));

		Class<?> builderType = ReflectionUtils.invokeMethod(builderMethod, null).getClass();

		String buildMethodName = "build";
		Method buildMethod = ReflectionUtils.findMethod(builderType, buildMethodName)
			.orElseThrow(() -> new IllegalArgumentException("build method could not found. " + buildMethodName)
			);

		BuilderCombinator<?> builderCombinator = Builders.withBuilder(
			() -> ReflectionUtils.invokeMethod(builderMethod, null)
		);
		for (ArbitraryProperty arbitraryProperty : childrenProperties) {
			String fieldName = arbitraryProperty.getProperty().getName();
			Method buildFieldMethod = ReflectionUtils.findMethods(
					builderType,
					m -> m.getName().equals(fieldName)
				)
				.stream()
				.filter(Objects::nonNull)
				.filter(m -> m.getParameterCount() == 1)
				.findFirst()
				.orElse(null);

			if (buildFieldMethod == null) {
				continue;
			}

			buildFieldMethod.setAccessible(true);

			String resolvePropertyName = arbitraryProperty.getResolvePropertyName();
			Arbitrary arbitrary = childrenArbitraries.get(resolvePropertyName);
			if (arbitrary != null) {

				builderCombinator = builderCombinator.use(arbitrary)
					.in((b, v) -> {
						try {
							if (v != null) {
								return ReflectionUtils.invokeMethod(buildFieldMethod, b, v);
							} else {
								return b;
							}
						} catch (IllegalArgumentException e) {
							throw e;
						}
					});
			}
		}

		return new ArbitraryIntrospectorResult(
			builderCombinator.build(a -> ReflectionUtils.invokeMethod(buildMethod, a))
		);
	}
}
