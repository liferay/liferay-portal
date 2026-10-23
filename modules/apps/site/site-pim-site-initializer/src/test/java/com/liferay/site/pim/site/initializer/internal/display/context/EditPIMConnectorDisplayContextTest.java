/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class EditPIMConnectorDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetReactData() throws Exception {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			language.format(
				httpServletRequest, "edit-x", "Liferay Commerce Connector",
				false)
		).thenReturn(
			"Edit Liferay Commerce Connector"
		);

		Mockito.when(
			language.get(httpServletRequest, "new-connector")
		).thenReturn(
			"New Connector"
		);

		languageUtil.setLanguage(language);

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getRESTContextPath()
		).thenReturn(
			"/pim/connectors"
		);

		ObjectEntryLocalService objectEntryLocalService = Mockito.mock(
			ObjectEntryLocalService.class);

		EditPIMConnectorDisplayContext editPIMConnectorDisplayContext =
			new EditPIMConnectorDisplayContext(
				httpServletRequest, objectDefinition, objectEntryLocalService,
				ListUtil.fromArray(
					_mockPIMConnector("liferay-commerce", "Liferay Commerce")));

		Map<String, Object> reactData =
			editPIMConnectorDisplayContext.getReactData();

		Assert.assertEquals("/o/pim/connectors", reactData.get("apiURL"));
		Assert.assertEquals("/web/cms/connectors", reactData.get("backURL"));
		Assert.assertEquals(0L, reactData.get("objectEntryId"));
		Assert.assertNull(reactData.get("pimConnector"));
		Assert.assertEquals("New Connector", reactData.get("title"));

		JSONArray pimConnectorsJSONArray = (JSONArray)reactData.get(
			"pimConnectors");

		Assert.assertEquals(
			pimConnectorsJSONArray.toString(), 1,
			pimConnectorsJSONArray.length());

		JSONObject pimConnectorJSONObject =
			pimConnectorsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			"liferay-commerce", pimConnectorJSONObject.getString("key"));
		Assert.assertEquals(
			"Liferay Commerce", pimConnectorJSONObject.getString("name"));

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"active", true
			).put(
				"key", "liferay-commerce"
			).put(
				"name", "Liferay Commerce Connector"
			).build()
		);

		Mockito.when(
			objectEntryLocalService.fetchObjectEntry(1)
		).thenReturn(
			objectEntry
		);

		Mockito.when(
			httpServletRequest.getParameter("objectEntryId")
		).thenReturn(
			"1"
		);

		editPIMConnectorDisplayContext = new EditPIMConnectorDisplayContext(
			httpServletRequest, objectDefinition, objectEntryLocalService,
			ListUtil.fromArray(
				_mockPIMConnector("liferay-commerce", "Liferay Commerce")));

		reactData = editPIMConnectorDisplayContext.getReactData();

		Assert.assertEquals(1L, reactData.get("objectEntryId"));
		Assert.assertEquals(
			"Edit Liferay Commerce Connector", reactData.get("title"));

		pimConnectorJSONObject = (JSONObject)reactData.get("pimConnector");

		Assert.assertTrue(
			pimConnectorJSONObject.toString(),
			pimConnectorJSONObject.getBoolean("active"));
		Assert.assertEquals(
			"liferay-commerce", pimConnectorJSONObject.getString("key"));
		Assert.assertEquals(
			"Liferay Commerce Connector",
			pimConnectorJSONObject.getString("name"));
	}

	@Test
	public void testGetReactDataWithSettings() throws Exception {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			language.format(
				Mockito.eq(httpServletRequest), Mockito.eq("x-settings"),
				Mockito.eq((Object)"Liferay Commerce"))
		).thenReturn(
			"Liferay Commerce Settings"
		);

		languageUtil.setLanguage(language);

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			httpServletRequest.getParameter("objectEntryId")
		).thenReturn(
			"1"
		);

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			objectEntry.getExternalReferenceCode()
		).thenReturn(
			"PIM_CONNECTOR_ERC"
		);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"active", true
			).put(
				"key", "liferay-commerce"
			).put(
				"name", RandomTestUtil.randomString()
			).build()
		);

		ObjectEntryLocalService objectEntryLocalService = Mockito.mock(
			ObjectEntryLocalService.class);

		Mockito.when(
			objectEntryLocalService.fetchObjectEntry(1)
		).thenReturn(
			objectEntry
		);

		ObjectDefinition settingsObjectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			settingsObjectDefinition.getObjectDefinitionId()
		).thenReturn(
			_SETTINGS_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			settingsObjectDefinition.getRESTContextPath()
		).thenReturn(
			"/pim/liferay-commerce-connector-settings"
		);

		ObjectEntry settingsObjectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			settingsObjectEntry.getExternalReferenceCode()
		).thenReturn(
			"PIM_CONNECTOR_ERC"
		);

		Mockito.when(
			settingsObjectEntry.getObjectDefinitionId()
		).thenReturn(
			_SETTINGS_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			settingsObjectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"clientSecret", RandomTestUtil.randomString()
			).put(
				"id", 2L
			).put(
				"url", "https://commerce.minium.com"
			).build()
		);

		PIMConnector pimConnector = _mockPIMConnector(
			"liferay-commerce", "Liferay Commerce");

		Mockito.when(
			pimConnector.getSettingsObjectDefinitionExternalReferenceCode()
		).thenReturn(
			_SETTINGS_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE
		);

		try (MockedStatic<ObjectDefinitionLocalServiceUtil>
				objectDefinitionLocalServiceUtilMockedStatic =
					Mockito.mockStatic(ObjectDefinitionLocalServiceUtil.class);
			MockedStatic<ObjectEntryLocalServiceUtil>
				objectEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					ObjectEntryLocalServiceUtil.class);
			MockedStatic<ObjectFieldLocalServiceUtil>
				objectFieldLocalServiceUtilMockedStatic = Mockito.mockStatic(
					ObjectFieldLocalServiceUtil.class)) {

			objectDefinitionLocalServiceUtilMockedStatic.when(
				() ->
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByExternalReferenceCode(
							_SETTINGS_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE,
							_COMPANY_ID)
			).thenReturn(
				settingsObjectDefinition
			);

			objectEntryLocalServiceUtilMockedStatic.when(
				() -> ObjectEntryLocalServiceUtil.fetchObjectEntry(
					"PIM_CONNECTOR_ERC", 0, _SETTINGS_OBJECT_DEFINITION_ID)
			).thenReturn(
				settingsObjectEntry
			);

			List<ObjectField> objectFields = ListUtil.fromArray(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED,
					"Client Secret", false, "clientSecret", true),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER, "ID", true,
					"id", false),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, "URL", false,
					"url", true));

			objectFieldLocalServiceUtilMockedStatic.when(
				() -> ObjectFieldLocalServiceUtil.getObjectFields(
					_SETTINGS_OBJECT_DEFINITION_ID)
			).thenReturn(
				objectFields
			);

			Mockito.when(
				themeDisplay.getCompanyId()
			).thenReturn(
				_COMPANY_ID
			);

			EditPIMConnectorDisplayContext editPIMConnectorDisplayContext =
				new EditPIMConnectorDisplayContext(
					httpServletRequest, Mockito.mock(ObjectDefinition.class),
					objectEntryLocalService, ListUtil.fromArray(pimConnector));

			Map<String, Object> reactData =
				editPIMConnectorDisplayContext.getReactData();

			JSONObject pimConnectorJSONObject = (JSONObject)reactData.get(
				"pimConnector");

			Assert.assertEquals(
				"PIM_CONNECTOR_ERC",
				pimConnectorJSONObject.getString("externalReferenceCode"));

			JSONArray pimConnectorsJSONArray = (JSONArray)reactData.get(
				"pimConnectors");

			pimConnectorJSONObject = pimConnectorsJSONArray.getJSONObject(0);

			JSONObject settingsJSONObject =
				pimConnectorJSONObject.getJSONObject("settings");

			Assert.assertEquals(
				"/o/pim/liferay-commerce-connector-settings",
				settingsJSONObject.getString("apiURL"));
			Assert.assertEquals(
				"Liferay Commerce Settings",
				settingsJSONObject.getString("title"));

			JSONArray fieldsJSONArray = settingsJSONObject.getJSONArray(
				"fields");

			Assert.assertEquals(
				fieldsJSONArray.toString(), 2, fieldsJSONArray.length());

			JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(0);

			Assert.assertEquals(
				ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED,
				fieldJSONObject.getString("businessType"));
			Assert.assertEquals(
				"Client Secret", fieldJSONObject.getString("label"));
			Assert.assertEquals(
				"clientSecret", fieldJSONObject.getString("name"));
			Assert.assertTrue(fieldJSONObject.getBoolean("required"));

			fieldJSONObject = fieldsJSONArray.getJSONObject(1);

			Assert.assertEquals("url", fieldJSONObject.getString("name"));

			JSONObject settingsValuesJSONObject = (JSONObject)reactData.get(
				"settings");

			Assert.assertEquals(
				"PIM_CONNECTOR_ERC",
				settingsValuesJSONObject.getString("externalReferenceCode"));

			JSONObject valuesJSONObject =
				settingsValuesJSONObject.getJSONObject("values");

			Assert.assertEquals(
				Portal.TEMP_OBFUSCATION_VALUE,
				valuesJSONObject.getString("clientSecret"));
			Assert.assertFalse(valuesJSONObject.has("id"));
			Assert.assertEquals(
				"https://commerce.minium.com",
				valuesJSONObject.getString("url"));
		}
	}

	private ObjectField _mockObjectField(
		String businessType, String label, boolean metadata, String name,
		boolean required) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.compareBusinessType(Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> businessType.equals(
				invocationOnMock.getArgument(0))
		);

		Mockito.when(
			objectField.getBusinessType()
		).thenReturn(
			businessType
		);

		Mockito.when(
			objectField.getLabel(LocaleUtil.US)
		).thenReturn(
			label
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

		Mockito.when(
			objectField.isRequired()
		).thenReturn(
			required
		);

		return objectField;
	}

	private PIMConnector _mockPIMConnector(String key, String name) {
		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			pimConnector.getName(LocaleUtil.US)
		).thenReturn(
			name
		);

		return pimConnector;
	}

	private ThemeDisplay _mockThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			"/web/cms/connectors"
		);

		return themeDisplay;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String
		_SETTINGS_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE =
			RandomTestUtil.randomString();

	private static final long _SETTINGS_OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

}