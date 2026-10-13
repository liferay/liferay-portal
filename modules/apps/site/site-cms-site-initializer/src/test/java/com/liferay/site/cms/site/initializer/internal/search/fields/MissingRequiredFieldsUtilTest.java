/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.fields;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class MissingRequiredFieldsUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetMissingRequiredFieldsCount() throws Exception {
		_testGetMissingRequiredFieldsCountAttachment();
		_testGetMissingRequiredFieldsCountBoolean();
		_testGetMissingRequiredFieldsCountInteger();
		_testGetMissingRequiredFieldsCountLocalized();
		_testGetMissingRequiredFieldsCountLong();
		_testGetMissingRequiredFieldsCountMultiselectPicklist();
		_testGetMissingRequiredFieldsCountNotRequired();
		_testGetMissingRequiredFieldsCountPicklist();
		_testGetMissingRequiredFieldsCountText();
	}

	private void _assertMissing(
		ObjectField objectField, JSONObject propertiesJSONObject) {

		Assert.assertEquals(
			1,
			MissingRequiredFieldsUtil.getMissingRequiredFieldsCount(
				"en_US", Collections.singletonList(objectField),
				propertiesJSONObject));
	}

	private void _assertNotMissing(
		ObjectField objectField, JSONObject propertiesJSONObject) {

		Assert.assertEquals(
			0,
			MissingRequiredFieldsUtil.getMissingRequiredFieldsCount(
				"en_US", Collections.singletonList(objectField),
				propertiesJSONObject));
	}

	private ObjectField _mockObjectField(
		String businessType, String dbType, boolean localized,
		boolean required) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.compareBusinessType(Mockito.anyString())
		).thenAnswer(
			invocation -> businessType.equals(invocation.getArgument(0))
		);

		Mockito.when(
			objectField.getDBType()
		).thenReturn(
			dbType
		);

		Mockito.when(
			objectField.getI18nObjectFieldName()
		).thenReturn(
			"field_i18n"
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			"field"
		);

		Mockito.when(
			objectField.isLocalized()
		).thenReturn(
			localized
		);

		Mockito.when(
			objectField.isRequired()
		).thenReturn(
			required
		);

		return objectField;
	}

	private void _testGetMissingRequiredFieldsCountAttachment() {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			ObjectFieldConstants.DB_TYPE_LONG, false, true);

		_assertMissing(
			objectField, JSONUtil.put("field", JSONUtil.put("id", 0)));
		_assertNotMissing(
			objectField,
			JSONUtil.put(
				"field", JSONUtil.put("id", RandomTestUtil.randomLong())));
	}

	private void _testGetMissingRequiredFieldsCountBoolean() {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
			ObjectFieldConstants.DB_TYPE_BOOLEAN, false, true);

		_assertMissing(objectField, JSONUtil.put("field", false));
		_assertNotMissing(objectField, JSONUtil.put("field", true));
	}

	private void _testGetMissingRequiredFieldsCountInteger() throws Exception {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
			ObjectFieldConstants.DB_TYPE_INTEGER, false, true);

		_assertMissing(
			objectField,
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomInt()));
		_assertMissing(
			objectField, JSONFactoryUtil.createJSONObject("{\"field\": null}"));
		_assertNotMissing(objectField, JSONUtil.put("field", 0));
	}

	private void _testGetMissingRequiredFieldsCountLocalized() {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, true, true);

		_assertMissing(
			objectField, JSONUtil.put("field", RandomTestUtil.randomString()));
		_assertMissing(
			objectField,
			JSONUtil.put(
				"field_i18n",
				JSONUtil.put("es_ES", RandomTestUtil.randomString())));
		_assertNotMissing(
			objectField,
			JSONUtil.put(
				"field_i18n",
				JSONUtil.put(
					"en_US", RandomTestUtil.randomString()
				).put(
					"es_ES", ""
				)));
	}

	private void _testGetMissingRequiredFieldsCountLong() {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP,
			ObjectFieldConstants.DB_TYPE_LONG, false, true);

		_assertMissing(objectField, JSONUtil.put("field", 0));
		_assertNotMissing(
			objectField, JSONUtil.put("field", RandomTestUtil.randomLong()));
	}

	private void _testGetMissingRequiredFieldsCountMultiselectPicklist() {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
			ObjectFieldConstants.DB_TYPE_STRING, false, true);

		_assertMissing(objectField, JSONUtil.put("field", JSONUtil.putAll()));
		_assertNotMissing(
			objectField,
			JSONUtil.put(
				"field",
				JSONUtil.putAll(
					JSONUtil.put("key", RandomTestUtil.randomString()))));
	}

	private void _testGetMissingRequiredFieldsCountNotRequired() {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, false, false);

		_assertNotMissing(
			objectField,
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
	}

	private void _testGetMissingRequiredFieldsCountPicklist() {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			ObjectFieldConstants.DB_TYPE_STRING, false, true);

		_assertMissing(
			objectField, JSONUtil.put("field", JSONUtil.put("key", "")));
		_assertNotMissing(
			objectField,
			JSONUtil.put(
				"field", JSONUtil.put("key", RandomTestUtil.randomString())));
	}

	private void _testGetMissingRequiredFieldsCountText() {
		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, false, true);

		Assert.assertEquals(
			2,
			MissingRequiredFieldsUtil.getMissingRequiredFieldsCount(
				"en_US", Arrays.asList(objectField, objectField), null));

		_assertMissing(objectField, JSONUtil.put("field", ""));
		_assertNotMissing(
			objectField, JSONUtil.put("field", RandomTestUtil.randomString()));
	}

}