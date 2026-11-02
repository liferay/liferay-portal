/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.storage.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.json.storage.model.JSONStorageEntryTable;
import com.liferay.json.storage.service.JSONStorageEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class JSONStorageEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		_jsonStorageEntryLocalService.deleteJSONStorageEntries(
			_CLASS_NAME_ID, _CLASS_PK_1);
		_jsonStorageEntryLocalService.deleteJSONStorageEntries(
			_CLASS_NAME_ID, _CLASS_PK_2);
		_jsonStorageEntryLocalService.deleteJSONStorageEntries(
			_CLASS_NAME_ID, _CLASS_PK_3);
	}

	@Test
	public void testEmptyArray() {
		String json = "[]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		_assertJSONStoreEntryCount(_CLASS_PK_1, 1);

		json = "[[[\"value\"]]]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		_assertJSONStoreEntryCount(_CLASS_PK_1, 3);

		json = "[]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		_assertJSONStoreEntryCount(_CLASS_PK_1, 1);
	}

	@Test
	public void testEmptyObject() {
		String json = "{}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		_assertJSONStoreEntryCount(_CLASS_PK_1, 1);

		json = "{\"root\": {\"key\": \"value\"}}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		_assertJSONStoreEntryCount(_CLASS_PK_1, 2);

		json = "{}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		_assertJSONStoreEntryCount(_CLASS_PK_1, 1);
	}

	@Test
	public void testGetMissing() {
		Assert.assertNull(
			_jsonStorageEntryLocalService.getJSONArray(
				_CLASS_NAME_ID, _CLASS_PK_1));
		Assert.assertNull(
			_jsonStorageEntryLocalService.getJSONObject(
				_CLASS_NAME_ID, _CLASS_PK_1));
		Assert.assertNull(
			_jsonStorageEntryLocalService.getJSONSerializable(
				_CLASS_NAME_ID, _CLASS_PK_1));
		Assert.assertNull(
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1));
	}

	@Test
	public void testInvalidJSON() {
		try {
			_jsonStorageEntryLocalService.addJSONStorageEntries(
				_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, "null");

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Invalid JSON: null", illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testNullValue() {
		String json = "{\"key\": null}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONObject jsonObject = _jsonStorageEntryLocalService.getJSONObject(
			_CLASS_NAME_ID, _CLASS_PK_1);

		Assert.assertNull(jsonObject.get("key"));

		json = "[null]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		JSONArray jsonArray = _jsonStorageEntryLocalService.getJSONArray(
			_CLASS_NAME_ID, _CLASS_PK_1);

		Assert.assertEquals(1, jsonArray.length());
		Assert.assertNull(jsonArray.get(0));
	}

	@Test
	public void testReplaceArraysWithObjects() {
		String json = "[[], [1, 2]]";

		_jsonStorageEntryLocalService.addJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			String.valueOf(
				_jsonStorageEntryLocalService.getJSONArray(
					_CLASS_NAME_ID, _CLASS_PK_1)),
			true);
		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		json = "{\"a\": {}, \"b\": {\"c\": 1}}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			String.valueOf(
				_jsonStorageEntryLocalService.getJSONObject(
					_CLASS_NAME_ID, _CLASS_PK_1)),
			true);
		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);
	}

	@Test
	public void testReplaceObjectsWithArrays() {
		String json = "{\"a\": {}, \"b\": {\"c\": 1}}";

		_jsonStorageEntryLocalService.addJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			String.valueOf(
				_jsonStorageEntryLocalService.getJSONObject(
					_CLASS_NAME_ID, _CLASS_PK_1)),
			true);
		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		json = "[[], [1, 2]]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			String.valueOf(
				_jsonStorageEntryLocalService.getJSONArray(
					_CLASS_NAME_ID, _CLASS_PK_1)),
			true);
		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);
	}

	@Test
	public void testUpdateArray() {
		String json = "[1, 2]";

		_jsonStorageEntryLocalService.addJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		json = "[\"a\", \"b\", \"c\"]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		json = "[true, false]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		json = "[3.14]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);
	}

	@Test
	public void testUpdateArrayObjects() {
		String json = "[[{\"a\": \"1\", \"b\": \"2\"}, {\"c\": \"3\"}]]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			String.valueOf(
				_jsonStorageEntryLocalService.getJSONArray(
					_CLASS_NAME_ID, _CLASS_PK_1)),
			true);
		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		json = "[[{\"a\": \"one\"}, {\"b\": \"two\", \"c\": \"three\"}]]";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			String.valueOf(
				_jsonStorageEntryLocalService.getJSONArray(
					_CLASS_NAME_ID, _CLASS_PK_1)),
			true);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);
	}

	@Test
	public void testUpdateObject() {
		String json = "{\"a\": 1, \"b\": 2}";

		_jsonStorageEntryLocalService.addJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		json = "{\"b\": true, \"c\": false}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);

		json = "{\"pi\": 3.14}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);
	}

	@Test
	public void testUpdateObjectArrays() {
		String json = "{\"a\": {\"b\": [1, 2]}}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);
		JSONAssert.assertEquals(
			json,
			String.valueOf(
				_jsonStorageEntryLocalService.getJSONObject(
					_CLASS_NAME_ID, _CLASS_PK_1)),
			true);

		json = "{\"a\": {\"b\": [2, 3]}}";

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
			_COMPANY_ID, _CLASS_NAME_ID, _CLASS_PK_1, json);

		JSONAssert.assertEquals(
			json,
			String.valueOf(
				_jsonStorageEntryLocalService.getJSONObject(
					_CLASS_NAME_ID, _CLASS_PK_1)),
			true);
		JSONAssert.assertEquals(
			json,
			_jsonStorageEntryLocalService.getJSON(_CLASS_NAME_ID, _CLASS_PK_1),
			true);
	}

	private void _assertJSONStoreEntryCount(long classPK, int expectedCount) {
		long count = _jsonStorageEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.count(
			).from(
				JSONStorageEntryTable.INSTANCE
			).where(
				JSONStorageEntryTable.INSTANCE.classNameId.eq(
					_CLASS_NAME_ID
				).and(
					JSONStorageEntryTable.INSTANCE.classPK.eq(classPK)
				)
			));

		Assert.assertEquals(expectedCount, count);
	}

	private static final long _CLASS_NAME_ID = 0;

	private static final long _CLASS_PK_1 = 1;

	private static final long _CLASS_PK_2 = 2;

	private static final long _CLASS_PK_3 = 3;

	private static final long _COMPANY_ID = 0;

	@Inject
	private JSONStorageEntryLocalService _jsonStorageEntryLocalService;

}