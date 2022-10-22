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

package com.navercorp.fixturemonkey.api.property;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.navercorp.fixturemonkey.api.collection.LruCache;
import com.navercorp.fixturemonkey.api.type.Types;

@API(since = "0.4.0", status = Status.EXPERIMENTAL)
public final class PropertyCache {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyCache.class);

	private static final Map<Class<?>, Map<String, PropertyDescriptor>> PROPERTY_DESCRIPTORS =
		new LruCache<>(2000);
	private static final Map<Class<?>, Map<String, Field>> FIELDS = new LruCache<>(2000);
	private static final Map<Class<?>, Map<Method, Parameter[]>> PARAMETER_BY_FACTORY_METHOD =
		new LruCache<>(2000);

	public static List<Property> getProperties(AnnotatedType annotatedType) {
		Map<String, List<Property>> propertiesMap = new HashMap<>();

		Class<?> actualType = Types.getActualType(annotatedType.getType());
		Map<String, Field> fieldMap = getFields(actualType);
		for (Entry<String, Field> entry : fieldMap.entrySet()) {
			List<Property> properties = propertiesMap.computeIfAbsent(
				entry.getKey(), name -> new ArrayList<>()
			);
			properties.add(
				new FieldProperty(
					Types.resolveWithTypeReferenceGenerics(annotatedType, entry.getValue()),
					entry.getValue()
				)
			);
		}

		Map<String, PropertyDescriptor> propertyDescriptorMap = getPropertyDescriptors(actualType);
		for (Entry<String, PropertyDescriptor> entry : propertyDescriptorMap.entrySet()) {
			List<Property> properties = propertiesMap.computeIfAbsent(
				entry.getValue().getName(), name -> new ArrayList<>()
			);
			properties.add(
				new PropertyDescriptorProperty(
					Types.resolveWithTypeReferenceGenerics(annotatedType, entry.getValue()),
					entry.getValue()
				)
			);
		}

		List<Property> result = new ArrayList<>();
		for (List<Property> properties : propertiesMap.values()) {
			if (properties.size() == 1) {
				result.add(properties.get(0));
			} else {
				result.add(new CompositeProperty(properties.get(0), properties.get(1)));
			}
		}

		return Collections.unmodifiableList(result);
	}

	public static Optional<Property> getProperty(AnnotatedType annotatedType, String name) {
		return getProperties(annotatedType).stream()
			.filter(it -> name.equals(it.getName()))
			.findFirst();
	}

	public static Map<String, Field> getFields(Class<?> clazz) {
		return FIELDS.computeIfAbsent(clazz, type -> {
			Map<String, Field> result = new ConcurrentHashMap<>();
			List<Field> fields = ReflectionUtils.findFields(
				clazz, field -> !Modifier.isStatic(field.getModifiers()), HierarchyTraversalMode.TOP_DOWN);
			for (Field field : fields) {
				field.setAccessible(true);
				result.put(field.getName(), field);
			}
			return result;
		});
	}

	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> clazz) {
		return PROPERTY_DESCRIPTORS.computeIfAbsent(clazz, type -> {
			Map<String, PropertyDescriptor> result = new ConcurrentHashMap<>();
			try {
				PropertyDescriptor[] descriptors = Introspector.getBeanInfo(type)
					.getPropertyDescriptors();
				for (PropertyDescriptor descriptor : descriptors) {
					if (descriptor.getName().equals("class")) {
						continue;
					}
					result.put(descriptor.getName(), descriptor);
				}
			} catch (IntrospectionException ex) {
				LOGGER.warn("Introspect bean property is failed. type: " + clazz, ex);
			}
			return result;
		});
	}

	public static List<Property> getFactoryProperties(AnnotatedType annotatedType) {
		List<Property> properties = new ArrayList<>();
		Class<?> actualType = Types.getActualType(annotatedType);
		Map<String, Field> fieldsByName = getFields(actualType);
		Map<Method, Parameter[]> parametersByFactoryMethods = getParametersByFactoryMethods(actualType);

		for (Entry<Method, Parameter[]> parametersByFactoryMethod : parametersByFactoryMethods.entrySet()) {
			Method method = parametersByFactoryMethod.getKey();
			Parameter[] parameters = parametersByFactoryMethod.getValue();
			for (Parameter parameter : parameters) {
				Field field = fieldsByName.get(parameter.getName());
				FieldProperty fieldProperty = field != null ? new FieldProperty(field) : null;
				properties.add(
					new FactoryMethodProperty(
						parameter.getAnnotatedType(),
						method,
						parameter.getName(),
						fieldProperty
					)
				);
			}
			return properties;
		}
		return properties;
	}

	public static Map<Method, Parameter[]> getParametersByFactoryMethods(Class<?> clazz) {
		return PARAMETER_BY_FACTORY_METHOD.computeIfAbsent(clazz, type -> {
			Map<Method, Parameter[]> result = new ConcurrentHashMap<>();
			List<Method> factoryMethods = Arrays.stream(type.getDeclaredMethods())
				.filter(it -> Modifier.isStatic(it.getModifiers()) && it.getReturnType().equals(type))
				.collect(Collectors.toList());

			for (Method factoryMethod : factoryMethods) {
				result.put(factoryMethod, factoryMethod.getParameters());
			}
			return result;
		});
	}

	public static void clearCache() {
		PROPERTY_DESCRIPTORS.clear();
		FIELDS.clear();
	}
}
