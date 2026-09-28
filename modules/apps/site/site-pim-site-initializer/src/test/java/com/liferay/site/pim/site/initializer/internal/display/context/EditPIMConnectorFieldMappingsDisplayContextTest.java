/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionServiceUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class EditPIMConnectorFieldMappingsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.format(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.any(Object.class), Mockito.anyBoolean())
		).thenAnswer(
			invocationOnMock ->
				invocationOnMock.getArgument(1) + ": " +
					invocationOnMock.getArgument(2)
		);

		languageUtil.setLanguage(language);

		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_httpServletRequest.getParameter("channelField")
		).thenReturn(
			_CHANNEL_FIELD
		);

		Mockito.when(
			_httpServletRequest.getParameter("objectEntryId")
		).thenReturn(
			String.valueOf(_OBJECT_ENTRY_ID)
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			"/cms"
		);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			themeDisplay.getPathFriendlyURLPublic()
		).thenReturn(
			"/web"
		);

		Mockito.when(
			themeDisplay.getPathThemeSpritemap()
		).thenReturn(
			"/icons.svg"
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			"/web/cms/field-mappings"
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		_mockPIMConnectorFieldMappingObjectDefinition();
		_mockPIMConnectorObjectEntry();
		_mockPIMObjectRelationship();
	}

	@After
	public void tearDown() {
		_objectDefinitionLocalServiceUtilMockedStatic.close();
		_objectDefinitionServiceUtilMockedStatic.close();
		_objectEntryLocalServiceUtilMockedStatic.close();
		_objectRelationshipLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testGetReactData() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorFieldMappingObjectEntries(
			_mockPIMConnectorFieldMappingObjectEntry(
				_CHANNEL_FIELD, 1, "code", "dynamicValue", StringPool.BLANK),
			_mockPIMConnectorFieldMappingObjectEntry(
				"name", 2, "name", "dynamicValue", StringPool.BLANK));

		EditPIMConnectorFieldMappingsDisplayContext
			editPIMConnectorFieldMappingsDisplayContext =
				new EditPIMConnectorFieldMappingsDisplayContext(
					_httpServletRequest, _objectEntryLocalService,
					_objectFieldLocalService, _pimConnectorRegistry);

		Map<String, Object> reactData =
			editPIMConnectorFieldMappingsDisplayContext.getReactData();

		Assert.assertEquals(
			"/o/pim/connector-field-mappings", reactData.get("apiURL"));
		Assert.assertEquals(
			"/web/cms/field-mappings?backURL=%2Fweb%2Fcms%2Ffield-mappings" +
				"&objectEntryId=" + _OBJECT_ENTRY_ID,
			reactData.get("backURL"));
		Assert.assertEquals(_CHANNEL_FIELD, reactData.get("channelField"));
		Assert.assertEquals("SKU", reactData.get("channelFieldLabel"));
		Assert.assertEquals(_OBJECT_ENTRY_ID, reactData.get("objectEntryId"));
		Assert.assertEquals(
			"r_pimConnectorToPIMConnectorFieldMappings_l_pimConnectorId",
			reactData.get("objectRelationshipObjectFieldName"));
		Assert.assertEquals("/icons.svg", reactData.get("spritemap"));
		Assert.assertEquals("edit-x: SKU", reactData.get("title"));

		JSONArray jsonArray = (JSONArray)reactData.get("fieldMappings");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(1, jsonObject.getLong("id"));
		Assert.assertEquals("code", jsonObject.getString("sourceFieldName"));
		Assert.assertEquals("dynamicValue", jsonObject.getString("type"));

		jsonArray = (JSONArray)reactData.get("objectDefinitions");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals("PIM Base SKU", jsonObject.getString("label"));

		jsonArray = jsonObject.getJSONArray("objectFields");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals("Code", jsonObject.getString("label"));
		Assert.assertEquals("code", jsonObject.getString("name"));

		_objectRelationshipLocalServiceUtilMockedStatic.when(
			() ->
				ObjectRelationshipLocalServiceUtil.
					fetchObjectRelationshipByExternalReferenceCode(
						"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
						_CONNECTOR_OBJECT_DEFINITION_ID)
		).thenReturn(
			null
		);

		editPIMConnectorFieldMappingsDisplayContext =
			new EditPIMConnectorFieldMappingsDisplayContext(
				_httpServletRequest, _objectEntryLocalService,
				_objectFieldLocalService, _pimConnectorRegistry);

		reactData = editPIMConnectorFieldMappingsDisplayContext.getReactData();

		Assert.assertEquals(
			StringPool.BLANK,
			reactData.get("objectRelationshipObjectFieldName"));

		jsonArray = (JSONArray)reactData.get("fieldMappings");

		Assert.assertEquals(jsonArray.toString(), 0, jsonArray.length());

		_objectDefinitionLocalServiceUtilMockedStatic.when(
			() ->
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByExternalReferenceCode(
						PIMObjectDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING,
						_COMPANY_ID)
		).thenReturn(
			null
		);

		editPIMConnectorFieldMappingsDisplayContext =
			new EditPIMConnectorFieldMappingsDisplayContext(
				_httpServletRequest, _objectEntryLocalService,
				_objectFieldLocalService, _pimConnectorRegistry);

		reactData = editPIMConnectorFieldMappingsDisplayContext.getReactData();

		Assert.assertEquals(StringPool.BLANK, reactData.get("apiURL"));

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			null
		);

		editPIMConnectorFieldMappingsDisplayContext =
			new EditPIMConnectorFieldMappingsDisplayContext(
				_httpServletRequest, _objectEntryLocalService,
				_objectFieldLocalService, _pimConnectorRegistry);

		reactData = editPIMConnectorFieldMappingsDisplayContext.getReactData();

		Assert.assertEquals(_CHANNEL_FIELD, reactData.get("channelFieldLabel"));
		Assert.assertEquals(
			StringPool.BLANK,
			reactData.get("objectRelationshipObjectFieldName"));

		jsonArray = (JSONArray)reactData.get("fieldMappings");

		Assert.assertEquals(jsonArray.toString(), 0, jsonArray.length());
	}

	private void _mockPIMConnector() {
		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getPIMConnectorChannelFields(LocaleUtil.US)
		).thenReturn(
			Collections.singletonList(
				new PIMConnectorChannelField(
					"SKU", false, _CHANNEL_FIELD, true))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			pimConnector
		);
	}

	private void _mockPIMConnectorFieldMappingObjectDefinition() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getRESTContextPath()
		).thenReturn(
			"/pim/connector-field-mappings"
		);

		_objectDefinitionLocalServiceUtilMockedStatic.when(
			() ->
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByExternalReferenceCode(
						PIMObjectDefinitionConstants.
							EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING,
						_COMPANY_ID)
		).thenReturn(
			objectDefinition
		);

		ObjectDefinition productTypeObjectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			productTypeObjectDefinition.getClassName()
		).thenReturn(
			"com.liferay.object.model.ObjectDefinition#P4R4"
		);

		Mockito.when(
			productTypeObjectDefinition.getLabel(LocaleUtil.US)
		).thenReturn(
			"PIM Base SKU"
		);

		Mockito.when(
			productTypeObjectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		_objectDefinitionServiceUtilMockedStatic.when(
			() -> ObjectDefinitionServiceUtil.getCMSObjectDefinitions(
				Mockito.anyLong(), Mockito.any())
		).thenReturn(
			Collections.singletonList(productTypeObjectDefinition)
		);

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getLabel(LocaleUtil.US)
		).thenReturn(
			"Code"
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			"code"
		);

		Mockito.when(
			_objectFieldLocalService.getObjectFields(_OBJECT_DEFINITION_ID)
		).thenReturn(
			Collections.singletonList(objectField)
		);
	}

	private void _mockPIMConnectorFieldMappingObjectEntries(
		ObjectEntry... objectEntries) {

		_objectEntryLocalServiceUtilMockedStatic.when(
			() -> ObjectEntryLocalServiceUtil.getOneToManyObjectEntries(
				_GROUP_ID, _OBJECT_RELATIONSHIP_ID, null, false,
				_OBJECT_ENTRY_ID, true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null)
		).thenReturn(
			Arrays.asList(objectEntries)
		);
	}

	private ObjectEntry _mockPIMConnectorFieldMappingObjectEntry(
		String channelFieldName, long objectEntryId, String sourceFieldName,
		String type, String value) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getObjectEntryId()
		).thenReturn(
			objectEntryId
		);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"channelFieldName", channelFieldName
			).put(
				"priority", 0
			).put(
				"sourceClassName", StringPool.BLANK
			).put(
				"sourceFieldName", sourceFieldName
			).put(
				"type", type
			).put(
				"value", value
			).build()
		);

		return objectEntry;
	}

	private void _mockPIMConnectorObjectEntry() {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			objectEntry.getObjectDefinitionId()
		).thenReturn(
			_CONNECTOR_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			objectEntry.getObjectEntryId()
		).thenReturn(
			_OBJECT_ENTRY_ID
		);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"key", _KEY
			).build()
		);

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			objectEntry
		);
	}

	private void _mockPIMObjectRelationship() {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			"r_pimConnectorToPIMConnectorFieldMappings_l_pimConnectorId"
		);

		Mockito.when(
			_objectFieldLocalService.fetchObjectField(_OBJECT_FIELD_ID)
		).thenReturn(
			objectField
		);

		ObjectRelationship objectRelationship = Mockito.mock(
			ObjectRelationship.class);

		Mockito.when(
			objectRelationship.getObjectFieldId2()
		).thenReturn(
			_OBJECT_FIELD_ID
		);

		Mockito.when(
			objectRelationship.getObjectRelationshipId()
		).thenReturn(
			_OBJECT_RELATIONSHIP_ID
		);

		_objectRelationshipLocalServiceUtilMockedStatic.when(
			() ->
				ObjectRelationshipLocalServiceUtil.
					fetchObjectRelationshipByExternalReferenceCode(
						"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
						_CONNECTOR_OBJECT_DEFINITION_ID)
		).thenReturn(
			objectRelationship
		);
	}

	private static final String _CHANNEL_FIELD = "skus[].sku";

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _CONNECTOR_OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _KEY = "liferay-commerce";

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_FIELD_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_RELATIONSHIP_ID =
		RandomTestUtil.randomLong();

	private HttpServletRequest _httpServletRequest;
	private final MockedStatic<ObjectDefinitionLocalServiceUtil>
		_objectDefinitionLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectDefinitionLocalServiceUtil.class);
	private final MockedStatic<ObjectDefinitionServiceUtil>
		_objectDefinitionServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectDefinitionServiceUtil.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final MockedStatic<ObjectEntryLocalServiceUtil>
		_objectEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectEntryLocalServiceUtil.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final MockedStatic<ObjectRelationshipLocalServiceUtil>
		_objectRelationshipLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectRelationshipLocalServiceUtil.class);
	private final PIMConnectorRegistry _pimConnectorRegistry = Mockito.mock(
		PIMConnectorRegistry.class);

}