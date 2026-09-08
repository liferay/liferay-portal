/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.dto.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.OneOfPropertyTestEntitySerDes;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.OneOfPropertyTestEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class OneOfPropertyTestEntityTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testToJSON() throws Exception {
		_testToJSON(_ARRAY, _ARRAY_JSON);
		_testToJSON(Arrays.asList(_ARRAY), _ARRAY_JSON);
		_testToJSON(_map, _MAP_JSON);
		_testToJSON(_STRING, _STRING_JSON);
	}

	@Test
	public void testToString() throws Exception {
		_testToString(_ARRAY, _ARRAY_JSON);
		_testToString(Arrays.asList(_ARRAY), _ARRAY_JSON);
		_testToString(_map, _MAP_JSON);
		_testToString(_STRING, _STRING_JSON);
	}

	private void _testToJSON(Object oneOfProperty, String expectedJSON)
		throws Exception {

		com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.
			OneOfPropertyTestEntity oneOfPropertyTestEntity =
				new com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.
					OneOfPropertyTestEntity();

		oneOfPropertyTestEntity.setOneOfProperty(oneOfProperty);

		JSONAssert.assertEquals(
			expectedJSON,
			OneOfPropertyTestEntitySerDes.toJSON(oneOfPropertyTestEntity),
			true);
	}

	private void _testToString(Object oneOfProperty, String expectedJSON)
		throws Exception {

		OneOfPropertyTestEntity oneOfPropertyTestEntity =
			new OneOfPropertyTestEntity();

		oneOfPropertyTestEntity.setOneOfProperty(oneOfProperty);

		JSONAssert.assertEquals(
			expectedJSON, oneOfPropertyTestEntity.toString(), true);
	}

	private static final Object[] _ARRAY = {
		Collections.singletonMap("key", "value"), "quoted \"string\"", 1, true
	};

	private static final String _ARRAY_JSON =
		"{\"oneOfProperty\": [{\"key\": \"value\"}, \"quoted " +
			"\\\"string\\\"\", 1, true]}";

	private static final String _MAP_JSON =
		"{\"oneOfProperty\": {\"array\": [\"a\", \"b\"], \"collection\": " +
			"[\"c\", \"d\"], \"map\": {\"key\": \"value\"}, \"string\": " +
				"\"quoted \\\"string\\\"\"}}";

	private static final String _STRING = "quoted \"string\"";

	private static final String _STRING_JSON =
		"{\"oneOfProperty\": \"quoted \\\"string\\\"\"}";

	private static final Map<String, Object> _map =
		HashMapBuilder.<String, Object>put(
			"array", new Object[] {"a", "b"}
		).put(
			"collection", Arrays.asList("c", "d")
		).put(
			"map", Collections.singletonMap("key", "value")
		).put(
			"string", "quoted \"string\""
		).build();

}