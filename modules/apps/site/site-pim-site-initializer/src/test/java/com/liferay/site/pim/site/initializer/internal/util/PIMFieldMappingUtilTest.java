/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Andrea Sbarra
 */
public class PIMFieldMappingUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(_jsonFactory);
	}

	@Test
	public void testGetMappingsJSONArray() throws Exception {
		JSONArray mappingsJSONArray = PIMFieldMappingUtil.getMappingsJSONArray(
			_getFieldMappingJSONObject(
				"{\"skus[].sku\": [{\"attribute\": \"code\", \"source\": " +
					"\"\", \"type\": \"attribute\"}]}"),
			"skus[].sku");

		Assert.assertEquals(
			mappingsJSONArray.toString(), 1, mappingsJSONArray.length());

		JSONObject mappingJSONObject = mappingsJSONArray.getJSONObject(0);

		Assert.assertEquals("code", mappingJSONObject.getString("attribute"));
		Assert.assertFalse(PIMFieldMappingUtil.isFixedValue(mappingJSONObject));
	}

	@Test
	public void testGetMappingsJSONArrayWithFixedValue() throws Exception {
		JSONArray mappingsJSONArray = PIMFieldMappingUtil.getMappingsJSONArray(
			_getFieldMappingJSONObject(
				"{\"catalogId\": [{\"type\": \"fixedValue\", \"value\": " +
					"\"12345\"}]}"),
			"catalogId");

		Assert.assertEquals(
			mappingsJSONArray.toString(), 1, mappingsJSONArray.length());

		JSONObject mappingJSONObject = mappingsJSONArray.getJSONObject(0);

		Assert.assertTrue(PIMFieldMappingUtil.isFixedValue(mappingJSONObject));
		Assert.assertEquals("12345", mappingJSONObject.getString("value"));
	}

	@Test
	public void testGetMappingsJSONArrayWithLegacyFieldMapping()
		throws Exception {

		JSONArray mappingsJSONArray = PIMFieldMappingUtil.getMappingsJSONArray(
			_getFieldMappingJSONObject("{\"name\": \"name\"}"), "name");

		Assert.assertEquals(
			mappingsJSONArray.toString(), 1, mappingsJSONArray.length());

		JSONObject mappingJSONObject = mappingsJSONArray.getJSONObject(0);

		Assert.assertEquals("name", mappingJSONObject.getString("attribute"));
		Assert.assertEquals("", mappingJSONObject.getString("source"));
		Assert.assertEquals(
			PIMFieldMappingUtil.TYPE_ATTRIBUTE,
			mappingJSONObject.getString("type"));
	}

	@Test
	public void testGetMappingsJSONArrayWithUnmappedChannelField()
		throws Exception {

		JSONArray mappingsJSONArray = PIMFieldMappingUtil.getMappingsJSONArray(
			_getFieldMappingJSONObject("{}"), "name");

		Assert.assertEquals(
			mappingsJSONArray.toString(), 0, mappingsJSONArray.length());
	}

	@Test
	public void testIsFixedValueWithMissingType() throws Exception {
		JSONObject mappingJSONObject = _jsonFactory.createJSONObject(
			"{\"attribute\": \"code\"}");

		Assert.assertFalse(PIMFieldMappingUtil.isFixedValue(mappingJSONObject));
	}

	private JSONObject _getFieldMappingJSONObject(String fieldMapping)
		throws Exception {

		return _jsonFactory.createJSONObject(fieldMapping);
	}

	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

}