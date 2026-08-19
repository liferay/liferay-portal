/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.util;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Joshua Cords
 */
public class AssetListTypePropertiesUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_setUpJSONFactoryUtil();

		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);
		_listTypeEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ListTypeEntryLocalServiceUtil.class);
		_objectDefinitionLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectDefinitionLocalServiceUtil.class);
		_objectFieldLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectFieldLocalServiceUtil.class);
		_portalUtilMockedStatic = Mockito.mockStatic(PortalUtil.class);
	}

	@AfterClass
	public static void tearDownClass() {
		_featureFlagManagerUtilMockedStatic.close();
		_listTypeEntryLocalServiceUtilMockedStatic.close();
		_objectDefinitionLocalServiceUtilMockedStatic.close();
		_objectFieldLocalServiceUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_featureFlagManagerUtilMockedStatic.reset();
		_listTypeEntryLocalServiceUtilMockedStatic.reset();
		_objectDefinitionLocalServiceUtilMockedStatic.reset();
		_objectFieldLocalServiceUtilMockedStatic.reset();
		_portalUtilMockedStatic.reset();

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-74731"))
		).thenReturn(
			true
		);

		_setUpLanguageUtil();
	}

	@Test
	public void testGetTypePropertiesJSONArrayEmitsOneGroupPerPair() {
		_setUpObjectDefinition(
			_CLASS_NAME_ID_1, _LABEL_1, _CLASS_TYPE_ID_1,
			Collections.singletonList(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, false, "title")));
		_setUpObjectDefinition(
			_CLASS_NAME_ID_2, _LABEL_2, _CLASS_TYPE_ID_2,
			Arrays.asList(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, false, "title"),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER, false,
					"priority")));

		JSONArray jsonArray =
			AssetListTypePropertiesUtil.getTypePropertiesJSONArray(
				new long[] {_CLASS_NAME_ID_1, _CLASS_NAME_ID_2},
				new long[] {_CLASS_TYPE_ID_1, _CLASS_TYPE_ID_2}, _COMPANY_ID,
				LocaleUtil.US);

		Assert.assertEquals(jsonArray.toString(), 3, jsonArray.length());

		JSONObject groupJSONObject1 = jsonArray.getJSONObject(1);

		Assert.assertEquals(_LABEL_1, groupJSONObject1.getString("label"));

		JSONArray itemsJSONArray1 = groupJSONObject1.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray1.toString(), 1, itemsJSONArray1.length());

		JSONObject itemJSONObject1 = itemsJSONArray1.getJSONObject(0);

		Assert.assertEquals(
			_CLASS_NAME_ID_1, itemJSONObject1.getLong("classNameId"));
		Assert.assertEquals(
			_CLASS_TYPE_ID_1, itemJSONObject1.getLong("classTypeId"));
		Assert.assertEquals("title", itemJSONObject1.getString("name"));

		JSONObject groupJSONObject2 = jsonArray.getJSONObject(2);

		Assert.assertEquals(_LABEL_2, groupJSONObject2.getString("label"));

		JSONArray itemsJSONArray2 = groupJSONObject2.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray2.toString(), 2, itemsJSONArray2.length());

		JSONObject itemJSONObject2 = itemsJSONArray2.getJSONObject(0);

		Assert.assertEquals(
			_CLASS_NAME_ID_2, itemJSONObject2.getLong("classNameId"));
		Assert.assertEquals(
			_CLASS_TYPE_ID_2, itemJSONObject2.getLong("classTypeId"));
		Assert.assertEquals("title", itemJSONObject2.getString("name"));

		JSONObject itemJSONObject3 = itemsJSONArray2.getJSONObject(1);

		Assert.assertEquals(
			_CLASS_NAME_ID_2, itemJSONObject3.getLong("classNameId"));
		Assert.assertEquals(
			_CLASS_TYPE_ID_2, itemJSONObject3.getLong("classTypeId"));
		Assert.assertEquals("priority", itemJSONObject3.getString("name"));
		Assert.assertEquals("integer", itemJSONObject3.getString("type"));
	}

	@Test
	public void testGetTypePropertiesJSONArrayEmitsSortableFlag() {
		_setUpObjectDefinition(
			_CLASS_NAME_ID_1, _LABEL_1, _CLASS_TYPE_ID_1,
			Arrays.asList(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN, false,
					"active"),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE, false, "due"),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER, false, "rank"),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT, false,
					"summary"),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
					false, "labels"),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, false,
					"category"),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT, false,
					"body"),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, false, "title")));

		JSONArray jsonArray =
			AssetListTypePropertiesUtil.getTypePropertiesJSONArray(
				new long[] {_CLASS_NAME_ID_1}, new long[] {_CLASS_TYPE_ID_1},
				_COMPANY_ID, LocaleUtil.US);

		JSONArray itemsJSONArray = jsonArray.getJSONObject(
			1
		).getJSONArray(
			"items"
		);

		Assert.assertEquals(
			itemsJSONArray.toString(), 8, itemsJSONArray.length());

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

			String name = itemJSONObject.getString("name");

			boolean expectedSortable = true;

			if (name.equals("body") || name.equals("labels") ||
				name.equals("summary")) {

				expectedSortable = false;
			}

			Assert.assertEquals(
				itemJSONObject.toString(), expectedSortable,
				itemJSONObject.getBoolean("sortable"));
		}
	}

	@Test
	public void testGetTypePropertiesJSONArrayEmitsTypeGroupWithEmptyItemsWhenNoFieldsFilterable() {
		_setUpObjectDefinition(
			_CLASS_NAME_ID_1, _LABEL_1, _CLASS_TYPE_ID_1,
			Collections.singletonList(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT, false,
					RandomTestUtil.randomString())));

		JSONArray jsonArray =
			AssetListTypePropertiesUtil.getTypePropertiesJSONArray(
				new long[] {_CLASS_NAME_ID_1}, new long[] {_CLASS_TYPE_ID_1},
				_COMPANY_ID, LocaleUtil.US);

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		JSONObject groupJSONObject = jsonArray.getJSONObject(1);

		Assert.assertEquals(_LABEL_1, groupJSONObject.getString("label"));

		JSONArray itemsJSONArray = groupJSONObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 0, itemsJSONArray.length());
	}

	@Test
	public void testGetTypePropertiesJSONArrayExcludesMetadataFieldsFromTypeGroup() {
		_setUpObjectDefinition(
			_CLASS_NAME_ID_1, _LABEL_1, _CLASS_TYPE_ID_1,
			Arrays.asList(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT, false,
					RandomTestUtil.randomString()),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, true,
					RandomTestUtil.randomString()),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, false, "title")));

		JSONArray jsonArray =
			AssetListTypePropertiesUtil.getTypePropertiesJSONArray(
				new long[] {_CLASS_NAME_ID_1}, new long[] {_CLASS_TYPE_ID_1},
				_COMPANY_ID, LocaleUtil.US);

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		JSONArray itemsJSONArray = jsonArray.getJSONObject(
			1
		).getJSONArray(
			"items"
		);

		Assert.assertEquals(
			itemsJSONArray.toString(), 1, itemsJSONArray.length());
		Assert.assertEquals(
			"title",
			itemsJSONArray.getJSONObject(
				0
			).getString(
				"name"
			));
	}

	@Test
	public void testGetTypePropertiesJSONArrayIncludesOneTypeGroup() {
		_setUpObjectDefinition(
			_CLASS_NAME_ID_1, _LABEL_1, _CLASS_TYPE_ID_1,
			Collections.singletonList(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, false, "title")));

		JSONArray jsonArray =
			AssetListTypePropertiesUtil.getTypePropertiesJSONArray(
				new long[] {_CLASS_NAME_ID_1}, new long[] {_CLASS_TYPE_ID_1},
				_COMPANY_ID, LocaleUtil.US);

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		JSONObject groupJSONObject = jsonArray.getJSONObject(1);

		Assert.assertEquals(_LABEL_1, groupJSONObject.getString("label"));

		JSONArray itemsJSONArray = groupJSONObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			_CLASS_NAME_ID_1, itemJSONObject.getLong("classNameId"));
		Assert.assertEquals(
			_CLASS_TYPE_ID_1, itemJSONObject.getLong("classTypeId"));
		Assert.assertEquals("title", itemJSONObject.getString("name"));
		Assert.assertEquals("text", itemJSONObject.getString("type"));
	}

	@Test
	public void testGetTypePropertiesJSONArrayIncludesPicklistOptions() {
		long listTypeDefinitionId = RandomTestUtil.randomLong();

		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, false,
			RandomTestUtil.randomString());

		Mockito.when(
			objectField.getListTypeDefinitionId()
		).thenReturn(
			listTypeDefinitionId
		);

		_setUpObjectDefinition(
			_CLASS_NAME_ID_1, _LABEL_1, _CLASS_TYPE_ID_1,
			Collections.singletonList(objectField));

		ListTypeEntry approvedListTypeEntry = Mockito.mock(ListTypeEntry.class);

		Mockito.when(
			approvedListTypeEntry.getKey()
		).thenReturn(
			"approved"
		);

		Mockito.when(
			approvedListTypeEntry.getName(LocaleUtil.US, true)
		).thenReturn(
			"Approved"
		);

		ListTypeEntry draftListTypeEntry = Mockito.mock(ListTypeEntry.class);

		Mockito.when(
			draftListTypeEntry.getKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			draftListTypeEntry.getName(LocaleUtil.US, true)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		_listTypeEntryLocalServiceUtilMockedStatic.when(
			() -> ListTypeEntryLocalServiceUtil.getListTypeEntries(
				listTypeDefinitionId)
		).thenReturn(
			Arrays.asList(approvedListTypeEntry, draftListTypeEntry)
		);

		JSONArray jsonArray =
			AssetListTypePropertiesUtil.getTypePropertiesJSONArray(
				new long[] {_CLASS_NAME_ID_1}, new long[] {_CLASS_TYPE_ID_1},
				_COMPANY_ID, LocaleUtil.US);

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		JSONObject itemJSONObject = jsonArray.getJSONObject(
			1
		).getJSONArray(
			"items"
		).getJSONObject(
			0
		);

		Assert.assertEquals("picklist", itemJSONObject.getString("type"));

		JSONArray optionsJSONArray = itemJSONObject.getJSONArray("options");

		Assert.assertEquals(
			optionsJSONArray.toString(), 2, optionsJSONArray.length());
		Assert.assertEquals(
			"Approved",
			optionsJSONArray.getJSONObject(
				0
			).getString(
				"label"
			));
		Assert.assertEquals(
			"approved",
			optionsJSONArray.getJSONObject(
				0
			).getString(
				"value"
			));
	}

	@Test
	public void testGetTypePropertiesJSONArrayReturnsOnlyCommonFieldsGroupWhenNoClassNameIds() {
		JSONArray jsonArray =
			AssetListTypePropertiesUtil.getTypePropertiesJSONArray(
				new long[0], new long[0], _COMPANY_ID, LocaleUtil.US);

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject groupJSONObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			"common-fields", groupJSONObject.getString("label"));

		JSONArray itemsJSONArray = groupJSONObject.getJSONArray("items");

		Assert.assertEquals(
			itemsJSONArray.toString(), 12, itemsJSONArray.length());

		Set<String> actualNames = JSONUtil.toStringSet(itemsJSONArray, "name");

		String[] expectedNames = {
			Field.CREATE_DATE, Field.DISPLAY_DATE, Field.EXPIRATION_DATE,
			Field.MODIFIED_DATE, Field.PRIORITY, Field.PUBLISH_DATE,
			Field.REVIEW_DATE, Field.STATUS, Field.TITLE, Field.USER_NAME,
			"externalReferenceCode", "viewCount"
		};

		for (String expectedName : expectedNames) {
			Assert.assertTrue(expectedName, actualNames.contains(expectedName));
		}
	}

	private static void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private ObjectField _mockObjectField(
		String businessType, boolean metadata, String name) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getBusinessType()
		).thenReturn(
			businessType
		);

		Mockito.when(
			objectField.getLabel(LocaleUtil.US, true)
		).thenReturn(
			name
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			objectField.isMetadata()
		).thenReturn(
			metadata
		);

		return objectField;
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);

		languageUtil.setLanguage(language);
	}

	private void _setUpObjectDefinition(
		long classNameId, String label, long objectDefinitionId,
		List<ObjectField> objectFields) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getLabel(LocaleUtil.US, true)
		).thenReturn(
			label
		);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			objectDefinitionId
		);

		_objectDefinitionLocalServiceUtilMockedStatic.when(
			() ->
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByClassName(
						_COMPANY_ID, "com.liferay.test.Class" + classNameId)
		).thenReturn(
			objectDefinition
		);

		_objectFieldLocalServiceUtilMockedStatic.when(
			() -> ObjectFieldLocalServiceUtil.getObjectFields(
				objectDefinitionId)
		).thenReturn(
			objectFields
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(classNameId)
		).thenReturn(
			"com.liferay.test.Class" + classNameId
		);
	}

	private static final long _CLASS_NAME_ID_1 = RandomTestUtil.randomLong();

	private static final long _CLASS_NAME_ID_2 = RandomTestUtil.randomLong();

	private static final long _CLASS_TYPE_ID_1 = RandomTestUtil.randomLong();

	private static final long _CLASS_TYPE_ID_2 = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _LABEL_1 = RandomTestUtil.randomString();

	private static final String _LABEL_2 = RandomTestUtil.randomString();

	private static MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic;
	private static MockedStatic<ListTypeEntryLocalServiceUtil>
		_listTypeEntryLocalServiceUtilMockedStatic;
	private static MockedStatic<ObjectDefinitionLocalServiceUtil>
		_objectDefinitionLocalServiceUtilMockedStatic;
	private static MockedStatic<ObjectFieldLocalServiceUtil>
		_objectFieldLocalServiceUtilMockedStatic;
	private static MockedStatic<PortalUtil> _portalUtilMockedStatic;

}