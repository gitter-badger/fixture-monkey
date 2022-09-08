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

package com.navercorp.fixturemonkey.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.generator.ArbitraryProperty;
import com.navercorp.fixturemonkey.api.option.GenerateOptions;
import com.navercorp.fixturemonkey.api.property.ElementProperty;
import com.navercorp.fixturemonkey.api.property.Property;
import com.navercorp.fixturemonkey.arbitrary.ArbitraryExpression;

@API(since = "0.4.0", status = Status.EXPERIMENTAL)
public final class TraverseContext {
	private final List<ArbitraryProperty> arbitraryProperties;
	private final GenerateOptions generateOptions;

	public TraverseContext(List<ArbitraryProperty> arbitraryProperties, GenerateOptions generateOptions) {
		this.arbitraryProperties = arbitraryProperties;
		this.generateOptions = generateOptions;
	}

	public boolean isMatch(Property property, String expression) {
		StringBuilder sb = new StringBuilder();
		for (ArbitraryProperty arbitraryProperty : arbitraryProperties) {
			if (arbitraryProperty.getProperty() instanceof ElementProperty) {
				sb.append("[");
				sb.append(((ElementProperty)arbitraryProperty.getProperty()).getSequence());
				sb.append("]");
			} else {
				if (sb.length() != 0) {
					sb.append(".");
				}
				sb.append(arbitraryProperty.getResolvePropertyName());
			}
		}

		if (property instanceof ElementProperty) {
			sb.append("[");
			sb.append(((ElementProperty)property).getSequence());
			sb.append("]");
		} else {
			sb.append(generateOptions.getPropertyNameResolver(property).resolve(property));
		}
		boolean check = ArbitraryExpression.from(expression).equals(ArbitraryExpression.from(sb.toString()));
		return check;
	}

	public TraverseContext withNewArbitraryProperties(
		ArbitraryProperty arbitraryProperty
	) {
		List<ArbitraryProperty> list = new ArrayList<>(arbitraryProperties);
		list.add(arbitraryProperty);
		return new TraverseContext(
			list,
			generateOptions
		);
	}
}
