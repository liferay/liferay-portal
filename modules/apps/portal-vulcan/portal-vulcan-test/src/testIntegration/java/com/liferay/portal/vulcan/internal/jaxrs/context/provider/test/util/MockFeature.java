/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cristina González
 */
public class MockFeature {

	public MockFeature(Feature feature) {
		FeatureContext featureContext = new FeatureContext() {

			@Override
			public Configuration getConfiguration() {
				return null;
			}

			@Override
			public FeatureContext property(String s, Object object) {
				return null;
			}

			@Override
			public FeatureContext register(Class<?> clazz) {
				return this;
			}

			@Override
			public FeatureContext register(
				Class<?> clazz, Class<?>... classes) {

				return null;
			}

			@Override
			public FeatureContext register(Class<?> clazz, int i) {
				return null;
			}

			@Override
			public FeatureContext register(
				Class<?> clazz, Map<Class<?>, Integer> map) {

				return null;
			}

			@Override
			public FeatureContext register(Object object) {
				Class<?> clazz = object.getClass();

				_objects.put(clazz.getCanonicalName(), object);

				return this;
			}

			@Override
			public FeatureContext register(Object object, Class<?>... classes) {
				return null;
			}

			@Override
			public FeatureContext register(Object object, int integer) {
				return null;
			}

			@Override
			public FeatureContext register(
				Object object, Map<Class<?>, Integer> map) {

				return null;
			}

		};

		feature.configure(featureContext);
	}

	public Object getObject(String className) {
		return _objects.get(className);
	}

	private final Map<String, Object> _objects = new HashMap<>();

}