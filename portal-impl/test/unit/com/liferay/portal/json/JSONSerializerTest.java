/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.model.UserTrackerPath;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.UserTrackerPathImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.LocalizationImpl;

import java.util.Collections;

import jodd.json.JsonSerializer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Igor Spasic
 */
public class JSONSerializerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(new LocalizationImpl());
	}

	@Test
	public void testSerializeDDMStructure() {
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		jsonSerializer.exclude("*.class");

		TestClass testClass = new TestClass();

		testClass.setName("test name");

		String json = jsonSerializer.serialize(testClass);

		Assert.assertTrue(json, json.contains("\"name\":\"test name\""));
	}

	@Test
	public void testSerializeDeep() {
		JSONInit.init();

		for (int i = 0; i <= Character.MAX_VALUE; i++) {
			_assertSerializeDeep(String.valueOf((char)i));
		}

		_assertSerializeDeep(Collections.singletonMap("null", null));
		_assertSerializeDeep(Collections.singletonMap(1, "key"));
		_assertSerializeDeep(
			LinkedHashMapBuilder.<String, Object>put(
				"array", new Object[] {"a", 1, null}
			).put(
				"boolean", true
			).put(
				"emptyList", Collections.emptyList()
			).put(
				"emptyMap", Collections.emptyMap()
			).put(
				"integer", -12
			).put(
				"jsonArray",
				() -> {
					JSONArray jsonArray = new JSONArrayImpl();

					return jsonArray.put(
						"a"
					).put(
						1
					);
				}
			).put(
				"jsonObject",
				() -> {
					JSONObject jsonObject = new JSONObjectImpl();

					return jsonObject.put("key", "value");
				}
			).put(
				"list",
				ListUtil.fromArray(
					Collections.singletonMap(
						"href",
						"https://www.liferay.com/web/guest?a=1&b=\"2\""),
					"\\")
			).put(
				"long", -1234567890123L
			).put(
				"string", "<a href=\"/web/guest\">\b\f\n\r\t\\</a></script>"
			).build());

		Object[] array = new Object[1];

		array[0] = array;

		_assertSerializeDeep(array);

		_assertSerializeDeep(Collections.singletonMap("double", 1.5));
		_assertSerializeDeep(
			Collections.singletonMap(
				"inputStream", new UnsyncByteArrayInputStream(new byte[0])));
		_assertSerializeDeep(
			Collections.singletonMap(
				"jsonSerializable", (JSONSerializable)() -> "{}"));
	}

	@Test
	public void testSerializeEntityWithGetOriginal() {
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		UserTrackerPath userTrackerPath = new UserTrackerPathImpl();

		String json = jsonSerializer.serialize(userTrackerPath);

		Assert.assertTrue(
			json, json.contains("\"originalUserTrackerId\":\"0\""));

		userTrackerPath.setUserTrackerId(1L);

		userTrackerPath.resetOriginalValues();

		json = jsonSerializer.serialize(userTrackerPath);

		Assert.assertTrue(
			json, json.contains("\"originalUserTrackerId\":\"1\""));
	}

	@Test
	public void testSerializeHits() {
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		String json = jsonSerializer.serialize(new HitsImpl());

		json = StringUtil.replace(json, CharPool.SPACE, StringPool.BLANK);

		Assert.assertTrue(json, json.contains("\"docs\":[]"));
		Assert.assertFalse(json, json.contains("\"query\""));
		Assert.assertTrue(json, json.contains("\"queryTerms\":null"));
		Assert.assertTrue(json, json.contains("\"scores\":"));
		Assert.assertTrue(json, json.contains("\"snippets\":["));
		Assert.assertTrue(json, json.contains("\"start\":\"0\""));
		Assert.assertTrue(json, json.contains("\"length\":0"));
	}

	@Test
	public void testSerializeServiceContext() {
		ServiceContext serviceContext = new ServiceContext();

		String[] groupPermissions = {"VIEW"};

		serviceContext.setAttribute("groupPermissions", groupPermissions);
		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(groupPermissions, null));

		String json = JSONFactoryUtil.serialize(serviceContext);

		ServiceContext deserializedServiceContext =
			(ServiceContext)JSONFactoryUtil.deserialize(json);

		ModelPermissions deserializedModelPermissions =
			deserializedServiceContext.getModelPermissions();

		Assert.assertArrayEquals(
			groupPermissions,
			deserializedModelPermissions.getActionIds(
				RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE));
	}

	@Test
	public void testSerializeTwice() {
		ServiceContext serviceContext = new ServiceContext();

		String[] groupPermissions = {"VIEW"};

		serviceContext.setAttribute("groupPermissions", groupPermissions);
		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(groupPermissions, null));

		String json1 = JSONFactoryUtil.serialize(serviceContext);

		ServiceContext deserializedServiceContext =
			(ServiceContext)JSONFactoryUtil.deserialize(json1);

		String json2 = JSONFactoryUtil.serialize(deserializedServiceContext);

		Assert.assertEquals(json1, json2);
	}

	private void _assertSerializeDeep(Object value) {
		JsonSerializer jsonSerializer = new JsonSerializer();

		jsonSerializer.deep(true);
		jsonSerializer.strictStringEncoding(true);

		StringBuilder sb = new StringBuilder();

		jsonSerializer.serialize(value, sb);

		JSONSerializerImpl jsonSerializerImpl = new JSONSerializerImpl();

		Assert.assertEquals(
			sb.toString(), jsonSerializerImpl.serializeDeep(value));
	}

	private class BaseTestClass {

		public String getName() {
			return _name;
		}

		public void setName(String name) {
			_name = name;
		}

		private String _name;

	}

	private class TestClass extends BaseTestClass {

		@Override
		public void setName(String name) {
			super.setName(name);
		}

	}

}