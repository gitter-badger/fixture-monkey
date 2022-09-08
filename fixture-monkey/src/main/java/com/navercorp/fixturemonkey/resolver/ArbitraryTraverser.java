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

import static com.navercorp.fixturemonkey.Constants.HEAD_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import com.navercorp.fixturemonkey.Constants;
import com.navercorp.fixturemonkey.api.generator.ArbitraryContainerInfo;
import com.navercorp.fixturemonkey.api.generator.ArbitraryContainerInfoGenerator;
import com.navercorp.fixturemonkey.api.generator.ArbitraryProperty;
import com.navercorp.fixturemonkey.api.generator.ArbitraryPropertyGenerator;
import com.navercorp.fixturemonkey.api.generator.ArbitraryPropertyGeneratorContext;
import com.navercorp.fixturemonkey.api.matcher.MatcherOperator;
import com.navercorp.fixturemonkey.api.option.GenerateOptions;
import com.navercorp.fixturemonkey.api.property.Property;

@API(since = "0.4.0", status = Status.EXPERIMENTAL)
public final class ArbitraryTraverser {
	private final GenerateOptions generateOptions;

	public ArbitraryTraverser(GenerateOptions generateOptions) {
		this.generateOptions = generateOptions;
	}

	public ArbitraryNode traverse(
		Property property,
		Map<String, ArbitraryContainerInfo> arbitraryContainerInfosByExpression
	) {
		ArbitraryPropertyGenerator arbitraryPropertyGenerator =
			this.generateOptions.getArbitraryPropertyGenerator(property);

		ArbitraryContainerInfo containerInfo = arbitraryContainerInfosByExpression.get(HEAD_NAME);

		ArbitraryProperty arbitraryProperty = arbitraryPropertyGenerator.generate(
			new ArbitraryPropertyGeneratorContext(
				property,
				null,
				null,
				containerInfo,
				this.generateOptions
			)
		);

		return this.traverse(
			arbitraryProperty,
			arbitraryContainerInfosByExpression,
			new TraverseContext(
				new ArrayList<>(),
				generateOptions
			)
		);
	}

	private ArbitraryNode traverse(
		ArbitraryProperty arbitraryProperty,
		Map<String, ArbitraryContainerInfo> arbitraryContainerInfosByExpression,
		TraverseContext traverseContext
	) {
		List<ArbitraryNode> children = new ArrayList<>();

		List<Property> childProperties = arbitraryProperty.getChildProperties();
		for (int index = 0; index < childProperties.size(); index++) {
			Property childProperty = childProperties.get(index);
			ArbitraryPropertyGenerator arbitraryPropertyGenerator =
				this.generateOptions.getArbitraryPropertyGenerator(childProperty);

			ArbitraryContainerInfo containerInfo = arbitraryContainerInfosByExpression.entrySet().stream()
				.filter(it -> traverseContext.isMatch(childProperty, it.getKey()))
				.map(Entry::getValue)
				.findFirst()
				.orElse(null);

			ArbitraryProperty childArbitraryProperty = arbitraryPropertyGenerator.generate(
				new ArbitraryPropertyGeneratorContext(
					childProperty,
					arbitraryProperty.getContainerInfo() != null ? index : null,
					arbitraryProperty,
					containerInfo,
					this.generateOptions
				)
			);

			ArbitraryNode childNode = this.traverse(
				childArbitraryProperty,
				arbitraryContainerInfosByExpression,
				traverseContext.withNewArbitraryProperties(childArbitraryProperty)
			);
			children.add(childNode);
		}

		return new ArbitraryNode(
			arbitraryProperty,
			children
		);
	}
}
