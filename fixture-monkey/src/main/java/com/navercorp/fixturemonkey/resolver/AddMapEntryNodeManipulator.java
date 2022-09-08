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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.generator.ArbitraryContainerInfo;
import com.navercorp.fixturemonkey.api.generator.ArbitraryProperty;
import com.navercorp.fixturemonkey.api.type.Types;

@API(since = "0.4.0", status = Status.EXPERIMENTAL)
public final class AddMapEntryNodeManipulator implements NodeManipulator {
	private final ArbitraryTraverser traverser;

	public AddMapEntryNodeManipulator(ArbitraryTraverser traverser) {
		this.traverser = traverser;
	}

	@Override
	public void manipulate(ArbitraryNode arbitraryNode) {
		Class<?> nodeType = Types.getActualType(arbitraryNode.getProperty().getType());
		if (!Map.class.isAssignableFrom(nodeType)) {
			throw new IllegalArgumentException(
				"Can only add an entry to a map node."
					+ " node type: " + arbitraryNode.getProperty().getType().getTypeName()
			);
		}

		ArbitraryProperty arbitraryProperty = arbitraryNode.getArbitraryProperty();
		Map<String, ArbitraryContainerInfo> arbitraryContainerInfosByExpression = Collections.singletonMap(
			HEAD_NAME,
			new ArbitraryContainerInfo(1, 1, true)
		);

		ArbitraryNode entryNode = traverser.traverse(
				arbitraryProperty.getProperty(),
				arbitraryContainerInfosByExpression
			)
			.getChildren().get(0);

		arbitraryProperty.getChildProperties().add(entryNode.getProperty());
		arbitraryNode.getChildren().add(entryNode);
	}
}
