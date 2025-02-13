/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matija Petanjek
 */
@RunWith(Arquillian.class)
public class MultiselectPicklistObjectFieldBusinessTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws PortalException {
		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, Collections.emptyList());

		_listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			listTypeDefinition.getListTypeDefinitionId(),
			_LIST_TYPE_ENTRY_KEY_1,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()));
		_listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			listTypeDefinition.getListTypeDefinitionId(),
			_LIST_TYPE_ENTRY_KEY_2,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()));

		_objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					new MultiselectPicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).listTypeDefinitionId(
						listTypeDefinition.getListTypeDefinitionId()
					).name(
						_OBJECT_FIELD_NAME
					).build()));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId());
	}

	@Test
	public void testPostMultiselectPicklistAsArrayOfMaps() throws Exception {
		JSONArray jsonArray = JSONUtil.getValueAsJSONArray(
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_OBJECT_FIELD_NAME,
					JSONUtil.putAll(
						JSONUtil.put("key", _LIST_TYPE_ENTRY_KEY_1),
						JSONUtil.put("key", _LIST_TYPE_ENTRY_KEY_2))
				).toString(),
				_objectDefinition.getRESTContextPath(), Http.Method.POST),
			"JSONArray/" + _OBJECT_FIELD_NAME);

		Assert.assertEquals(2, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			jsonObject.getString("key"), _LIST_TYPE_ENTRY_KEY_1);

		jsonObject = jsonArray.getJSONObject(1);

		Assert.assertEquals(
			jsonObject.getString("key"), _LIST_TYPE_ENTRY_KEY_2);
	}

	private static final String _LIST_TYPE_ENTRY_KEY_1 =
		RandomTestUtil.randomString();

	private static final String _LIST_TYPE_ENTRY_KEY_2 =
		RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME =
		"a" + RandomTestUtil.randomString();

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}