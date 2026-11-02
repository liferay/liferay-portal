/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionService;
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

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
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

		_mockObjectDefinition(
			PIMObjectDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING_RULE,
			"/pim/connector-field-mapping-rules");
		_mockObjectRelationship(
			"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
			RandomTestUtil.randomLong(),
			"r_pimConnectorToPIMConnectorFieldMappings_l_pimConnectorId",
			_PIM_FIELD_MAPPING_OBJECT_RELATIONSHIP_ID);
		_mockObjectRelationship(
			"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPING_RULES",
			RandomTestUtil.randomLong(),
			"r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId",
			_PIM_FIELD_MAPPING_RULE_OBJECT_RELATIONSHIP_ID);
		_mockPIMConnectorFieldMappingObjectDefinition();
		_mockPIMConnectorObjectEntry();
	}

	@After
	public void tearDown() {
		_objectDefinitionLocalServiceUtilMockedStatic.close();
		_objectEntryLocalServiceUtilMockedStatic.close();
		_objectRelationshipLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testGetReactData() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorFieldMappingObjectEntries(
			_mockPIMConnectorFieldMappingObjectEntry(
				_CHANNEL_FIELD, 1, "sku", "dynamicValue", StringPool.BLANK),
			_mockPIMConnectorFieldMappingObjectEntry(
				"name", 2, "title", "dynamicValue", StringPool.BLANK));
		_mockPIMConnectorFieldMappingRuleObjectEntries(
			_mockPIMConnectorFieldMappingRuleObjectEntry(
				"name", RandomTestUtil.randomLong(),
				HashMapBuilder.<String, Serializable>put(
					"textCase", RandomTestUtil.randomString()
				).build()),
			_mockPIMConnectorFieldMappingRuleObjectEntry(
				_CHANNEL_FIELD, 4,
				HashMapBuilder.<String, Serializable>put(
					"stripHTMLTags", true
				).put(
					"textCase", "upper"
				).put(
					"trimWhitespace", true
				).put(
					"truncateLength", 80
				).put(
					"urlEncode", true
				).put(
					"useFirstNonemptySource", true
				).build()));

		EditPIMConnectorFieldMappingsDisplayContext
			editPIMConnectorFieldMappingsDisplayContext =
				new EditPIMConnectorFieldMappingsDisplayContext(
					_httpServletRequest, _objectDefinitionService,
					_objectEntryLocalService, _objectFieldLocalService,
					_pimConnectorRegistry);

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
		Assert.assertEquals(
			Boolean.FALSE, reactData.get("channelFieldMultiple"));
		Assert.assertEquals(
			"/o/pim/connector-field-mapping-rules",
			reactData.get("mappingRuleAPIURL"));
		Assert.assertEquals(
			"r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId",
			reactData.get("mappingRuleObjectRelationshipObjectFieldName"));
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
		Assert.assertEquals("dynamicValue", jsonObject.getString("type"));
		Assert.assertEquals("sku", jsonObject.getString("sourceFieldName"));

		jsonObject = (JSONObject)reactData.get("mappingRule");

		Assert.assertEquals(4, jsonObject.getLong("id"));
		Assert.assertTrue(jsonObject.getBoolean("stripHTMLTags"));
		Assert.assertEquals("upper", jsonObject.getString("textCase"));
		Assert.assertTrue(jsonObject.getBoolean("trimWhitespace"));
		Assert.assertEquals(80, jsonObject.getInt("truncateLength"));
		Assert.assertTrue(jsonObject.getBoolean("urlEncode"));
		Assert.assertTrue(jsonObject.getBoolean("useFirstNonemptySource"));

		jsonArray = (JSONArray)reactData.get("objectDefinitions");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals("PIM Basic Product", jsonObject.getString("label"));

		jsonArray = jsonObject.getJSONArray("objectFields");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals("SKU", jsonObject.getString("label"));
		Assert.assertEquals("sku", jsonObject.getString("name"));

		_objectRelationshipLocalServiceUtilMockedStatic.when(
			() ->
				ObjectRelationshipLocalServiceUtil.
					fetchObjectRelationshipByExternalReferenceCode(
						"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
						_PIM_CONNECTOR_OBJECT_DEFINITION_ID)
		).thenReturn(
			null
		);

		_objectRelationshipLocalServiceUtilMockedStatic.when(
			() ->
				ObjectRelationshipLocalServiceUtil.
					fetchObjectRelationshipByExternalReferenceCode(
						"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPING_RULES",
						_PIM_CONNECTOR_OBJECT_DEFINITION_ID)
		).thenReturn(
			null
		);

		editPIMConnectorFieldMappingsDisplayContext =
			new EditPIMConnectorFieldMappingsDisplayContext(
				_httpServletRequest, _objectDefinitionService,
				_objectEntryLocalService, _objectFieldLocalService,
				_pimConnectorRegistry);

		reactData = editPIMConnectorFieldMappingsDisplayContext.getReactData();

		Assert.assertEquals(
			StringPool.BLANK,
			reactData.get("objectRelationshipObjectFieldName"));

		jsonArray = (JSONArray)reactData.get("fieldMappings");

		Assert.assertEquals(jsonArray.toString(), 0, jsonArray.length());

		jsonObject = (JSONObject)reactData.get("mappingRule");

		Assert.assertEquals(0, jsonObject.getLong("id"));
		Assert.assertFalse(jsonObject.getBoolean("stripHTMLTags"));

		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getPIMConnectorChannelFields()
		).thenReturn(
			Collections.singletonList(
				new PIMConnectorChannelField(
					RandomTestUtil.randomString(), true, _CHANNEL_FIELD, true,
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			pimConnector
		);

		editPIMConnectorFieldMappingsDisplayContext =
			new EditPIMConnectorFieldMappingsDisplayContext(
				_httpServletRequest, _objectDefinitionService,
				_objectEntryLocalService, _objectFieldLocalService,
				_pimConnectorRegistry);

		reactData = editPIMConnectorFieldMappingsDisplayContext.getReactData();

		Assert.assertEquals(
			Boolean.TRUE, reactData.get("channelFieldMultiple"));
		Assert.assertNull(reactData.get("mappingRule"));

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
				_httpServletRequest, _objectDefinitionService,
				_objectEntryLocalService, _objectFieldLocalService,
				_pimConnectorRegistry);

		reactData = editPIMConnectorFieldMappingsDisplayContext.getReactData();

		Assert.assertEquals(StringPool.BLANK, reactData.get("apiURL"));

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			null
		);

		editPIMConnectorFieldMappingsDisplayContext =
			new EditPIMConnectorFieldMappingsDisplayContext(
				_httpServletRequest, _objectDefinitionService,
				_objectEntryLocalService, _objectFieldLocalService,
				_pimConnectorRegistry);

		reactData = editPIMConnectorFieldMappingsDisplayContext.getReactData();

		Assert.assertEquals(_CHANNEL_FIELD, reactData.get("channelFieldLabel"));
		Assert.assertEquals(
			StringPool.BLANK,
			reactData.get("objectRelationshipObjectFieldName"));

		jsonArray = (JSONArray)reactData.get("fieldMappings");

		Assert.assertEquals(jsonArray.toString(), 0, jsonArray.length());
	}

	private void _mockObjectDefinition(
		String externalReferenceCode, String restContextPath) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getRESTContextPath()
		).thenReturn(
			restContextPath
		);

		_objectDefinitionLocalServiceUtilMockedStatic.when(
			() ->
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByExternalReferenceCode(
						externalReferenceCode, _COMPANY_ID)
		).thenReturn(
			objectDefinition
		);
	}

	private void _mockObjectRelationship(
		String externalReferenceCode, long objectFieldId,
		String objectFieldName, long objectRelationshipId) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			objectFieldName
		);

		Mockito.when(
			_objectFieldLocalService.fetchObjectField(objectFieldId)
		).thenReturn(
			objectField
		);

		ObjectRelationship objectRelationship = Mockito.mock(
			ObjectRelationship.class);

		Mockito.when(
			objectRelationship.getObjectFieldId2()
		).thenReturn(
			objectFieldId
		);

		Mockito.when(
			objectRelationship.getObjectRelationshipId()
		).thenReturn(
			objectRelationshipId
		);

		_objectRelationshipLocalServiceUtilMockedStatic.when(
			() ->
				ObjectRelationshipLocalServiceUtil.
					fetchObjectRelationshipByExternalReferenceCode(
						externalReferenceCode,
						_PIM_CONNECTOR_OBJECT_DEFINITION_ID)
		).thenReturn(
			objectRelationship
		);
	}

	private void _mockPIMConnector() {
		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getPIMConnectorChannelFields()
		).thenReturn(
			Collections.singletonList(
				new PIMConnectorChannelField(
					"SKU", false, _CHANNEL_FIELD, true,
					ObjectFieldConstants.BUSINESS_TYPE_TEXT))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			pimConnector
		);
	}

	private void _mockPIMConnectorFieldMappingObjectDefinition() {
		_mockObjectDefinition(
			PIMObjectDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING,
			"/pim/connector-field-mappings");

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
			"PIM Basic Product"
		);

		Mockito.when(
			productTypeObjectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			_objectDefinitionService.getCMSObjectDefinitions(
				Mockito.anyLong(), Mockito.any())
		).thenReturn(
			Collections.singletonList(productTypeObjectDefinition)
		);

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getLabel(LocaleUtil.US)
		).thenReturn(
			"SKU"
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			"sku"
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
				_GROUP_ID, _PIM_FIELD_MAPPING_OBJECT_RELATIONSHIP_ID, null,
				false, _OBJECT_ENTRY_ID, true, null, QueryUtil.ALL_POS,
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

	private void _mockPIMConnectorFieldMappingRuleObjectEntries(
		ObjectEntry... objectEntries) {

		_objectEntryLocalServiceUtilMockedStatic.when(
			() -> ObjectEntryLocalServiceUtil.getOneToManyObjectEntries(
				_GROUP_ID, _PIM_FIELD_MAPPING_RULE_OBJECT_RELATIONSHIP_ID, null,
				false, _OBJECT_ENTRY_ID, true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null)
		).thenReturn(
			Arrays.asList(objectEntries)
		);
	}

	private ObjectEntry _mockPIMConnectorFieldMappingRuleObjectEntry(
		String channelFieldName, long objectEntryId,
		Map<String, Serializable> values) {

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
			).putAll(
				values
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
			_PIM_CONNECTOR_OBJECT_DEFINITION_ID
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

	private static final String _CHANNEL_FIELD = "skus[].sku";

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _KEY = "liferay-commerce";

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final long _PIM_CONNECTOR_OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _PIM_FIELD_MAPPING_OBJECT_RELATIONSHIP_ID =
		RandomTestUtil.randomLong();

	private static final long _PIM_FIELD_MAPPING_RULE_OBJECT_RELATIONSHIP_ID =
		RandomTestUtil.randomLong();

	private HttpServletRequest _httpServletRequest;
	private final MockedStatic<ObjectDefinitionLocalServiceUtil>
		_objectDefinitionLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectDefinitionLocalServiceUtil.class);
	private final ObjectDefinitionService _objectDefinitionService =
		Mockito.mock(ObjectDefinitionService.class);
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