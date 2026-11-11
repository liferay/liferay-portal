/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.internal;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class CerebroEngineClientImplTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_cerebroEngineClientImpl, "_jsonFactory", _jsonFactoryImpl);
	}

	@Test
	public void testGetSiteMetrics() throws Exception {
		int value = RandomTestUtil.randomInt();

		_responseMap = HashMapBuilder.<String, Object>put(
			"data",
			HashMapBuilder.<String, Object>put(
				"site",
				HashMapBuilder.<String, Object>put(
					"visitorsMetric",
					HashMapBuilder.<String, Object>put(
						"value", value
					).build()
				).build()
			).build()
		).build();

		JSONObject jsonObject = _jsonFactoryImpl.createJSONObject(
			_cerebroEngineClientImpl.getSiteMetrics(
				RandomTestUtil.randomString(), Mockito.mock(FaroProject.class),
				RandomTestUtil.randomString(), RandomTestUtil.randomInt()));

		JSONObject visitorsMetricJSONObject = jsonObject.getJSONObject(
			"visitorsMetric");

		Assert.assertEquals(value, visitorsMetricJSONObject.getInt("value"));

		Assert.assertEquals("/graphql", _path);
	}

	@Test
	public void testIsCustomEventsLimitReached() throws Exception {
		_responseMap = HashMapBuilder.<String, Object>put(
			"data",
			HashMapBuilder.<String, Object>put(
				"customEventLimitReached", true
			).build()
		).build();

		Assert.assertTrue(
			_cerebroEngineClientImpl.isCustomEventsLimitReached(
				Mockito.mock(FaroProject.class)));

		Assert.assertEquals("/graphql", _path);
	}

	private final CerebroEngineClientImpl _cerebroEngineClientImpl =
		new CerebroEngineClientImpl() {

			@Override
			protected <T> T post(
				FaroProject faroProject, Map<String, String> headers,
				String path, Map<String, List<String>> queryParameters,
				Object requestBody, Class<T> responseType,
				Map<String, Object> uriVariables) {

				_path = path;

				return (T)_responseMap;
			}

		};

	private final JSONFactoryImpl _jsonFactoryImpl = new JSONFactoryImpl();
	private String _path;
	private Map<String, Object> _responseMap;

}