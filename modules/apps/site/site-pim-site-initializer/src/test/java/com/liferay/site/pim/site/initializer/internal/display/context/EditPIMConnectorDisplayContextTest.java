/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalService;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		_testGetReactData();
		_testGetReactDataWithConfiguration();
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

	private void _testGetReactData() throws Exception {
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
				httpServletRequest,
				Mockito.mock(ListTypeEntryLocalService.class), objectDefinition,
				objectEntryLocalService,
				Mockito.mock(ObjectFieldLocalService.class),
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
			httpServletRequest, Mockito.mock(ListTypeEntryLocalService.class),
			objectDefinition, objectEntryLocalService,
			Mockito.mock(ObjectFieldLocalService.class),
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

	private void _testGetReactDataWithConfiguration() throws Exception {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			language.format(
				Mockito.eq(httpServletRequest), Mockito.eq("x-configuration"),
				Mockito.eq((Object)"Liferay Commerce"))
		).thenReturn(
			"Liferay Commerce Configuration"
		);

		languageUtil.setLanguage(language);

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

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

		ObjectEntryLocalService objectEntryLocalService = Mockito.mock(
			ObjectEntryLocalService.class);

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			objectEntry.getExternalReferenceCode()
		).thenReturn(
			_PIM_CONNECTOR_EXTERNAL_REFERENCE_CODE
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

		Mockito.when(
			objectEntryLocalService.fetchObjectEntry(1)
		).thenReturn(
			objectEntry
		);

		ObjectDefinition pimConnectorConfigurationObjectDefinition =
			Mockito.mock(ObjectDefinition.class);

		Mockito.when(
			pimConnectorConfigurationObjectDefinition.getObjectDefinitionId()
		).thenReturn(
			_PIM_CONNECTOR_CONFIGURATION_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			pimConnectorConfigurationObjectDefinition.getRESTContextPath()
		).thenReturn(
			"/pim/connector-configuration-liferay-commerce"
		);

		ObjectEntry pimConnectorConfigurationObjectEntry = Mockito.mock(
			ObjectEntry.class);

		Mockito.when(
			pimConnectorConfigurationObjectEntry.getExternalReferenceCode()
		).thenReturn(
			_PIM_CONNECTOR_EXTERNAL_REFERENCE_CODE
		);

		Mockito.when(
			pimConnectorConfigurationObjectEntry.getObjectDefinitionId()
		).thenReturn(
			_PIM_CONNECTOR_CONFIGURATION_OBJECT_DEFINITION_ID
		);

		Calendar calendar = new GregorianCalendar(
			2026, Calendar.NOVEMBER, 2, 13, 45);

		Date date = calendar.getTime();

		Mockito.when(
			pimConnectorConfigurationObjectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"clientSecret", RandomTestUtil.randomString()
			).put(
				"date", date
			).put(
				"dateTime", date
			).put(
				"id", RandomTestUtil.randomLong()
			).put(
				"url", "https://commerce.minium.com"
			).build()
		);

		PIMConnector pimConnector = _mockPIMConnector(
			"liferay-commerce", "Liferay Commerce");

		Mockito.when(
			pimConnector.
				getPIMConnectorConfigurationObjectDefinitionExternalReferenceCode()
		).thenReturn(
			_PIM_CONNECTOR_CONFIGURATION_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE
		);

		try (MockedStatic<ObjectDefinitionLocalServiceUtil>
				objectDefinitionLocalServiceUtilMockedStatic =
					Mockito.mockStatic(ObjectDefinitionLocalServiceUtil.class);
			MockedStatic<ObjectEntryLocalServiceUtil>
				objectEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					ObjectEntryLocalServiceUtil.class)) {

			objectDefinitionLocalServiceUtilMockedStatic.when(
				() ->
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByExternalReferenceCode(
							_PIM_CONNECTOR_CONFIGURATION_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE,
							_COMPANY_ID)
			).thenReturn(
				pimConnectorConfigurationObjectDefinition
			);

			objectEntryLocalServiceUtilMockedStatic.when(
				() -> ObjectEntryLocalServiceUtil.fetchObjectEntry(
					_PIM_CONNECTOR_EXTERNAL_REFERENCE_CODE, 0,
					_PIM_CONNECTOR_CONFIGURATION_OBJECT_DEFINITION_ID)
			).thenReturn(
				pimConnectorConfigurationObjectEntry
			);

			List<ObjectField> objectFields = ListUtil.fromArray(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE,
					RandomTestUtil.randomString(), false, "date", false),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
					RandomTestUtil.randomString(), false, "dateTime", false),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED,
					"Client Secret", false, "clientSecret", true),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
					RandomTestUtil.randomString(), true, "id", false),
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					RandomTestUtil.randomString(), false, "url", true));

			ObjectFieldLocalService objectFieldLocalService = Mockito.mock(
				ObjectFieldLocalService.class);

			Mockito.when(
				objectFieldLocalService.getObjectFields(
					_PIM_CONNECTOR_CONFIGURATION_OBJECT_DEFINITION_ID)
			).thenReturn(
				objectFields
			);

			EditPIMConnectorDisplayContext editPIMConnectorDisplayContext =
				new EditPIMConnectorDisplayContext(
					httpServletRequest,
					Mockito.mock(ListTypeEntryLocalService.class),
					Mockito.mock(ObjectDefinition.class),
					objectEntryLocalService, objectFieldLocalService,
					ListUtil.fromArray(pimConnector));

			Map<String, Object> reactData =
				editPIMConnectorDisplayContext.getReactData();

			JSONObject pimConnectorJSONObject = (JSONObject)reactData.get(
				"pimConnector");

			Assert.assertEquals(
				_PIM_CONNECTOR_EXTERNAL_REFERENCE_CODE,
				pimConnectorJSONObject.getString("externalReferenceCode"));

			JSONArray pimConnectorsJSONArray = (JSONArray)reactData.get(
				"pimConnectors");

			pimConnectorJSONObject = pimConnectorsJSONArray.getJSONObject(0);

			JSONObject configurationJSONObject =
				pimConnectorJSONObject.getJSONObject("configuration");

			Assert.assertEquals(
				"/o/pim/connector-configuration-liferay-commerce",
				configurationJSONObject.getString("apiURL"));
			Assert.assertEquals(
				"Liferay Commerce Configuration",
				configurationJSONObject.getString("title"));

			JSONArray fieldsJSONArray = configurationJSONObject.getJSONArray(
				"fields");

			Assert.assertEquals(
				fieldsJSONArray.toString(), 4, fieldsJSONArray.length());

			JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(2);

			Assert.assertEquals(
				ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED,
				fieldJSONObject.getString("businessType"));
			Assert.assertEquals(
				"Client Secret", fieldJSONObject.getString("label"));
			Assert.assertEquals(
				"clientSecret", fieldJSONObject.getString("name"));
			Assert.assertTrue(fieldJSONObject.getBoolean("required"));

			fieldJSONObject = fieldsJSONArray.getJSONObject(3);

			Assert.assertEquals("url", fieldJSONObject.getString("name"));

			JSONObject configurationValuesJSONObject =
				(JSONObject)reactData.get("configuration");

			Assert.assertEquals(
				_PIM_CONNECTOR_EXTERNAL_REFERENCE_CODE,
				configurationValuesJSONObject.getString(
					"externalReferenceCode"));

			JSONObject valuesJSONObject =
				configurationValuesJSONObject.getJSONObject("values");

			Assert.assertEquals(
				Portal.TEMP_OBFUSCATION_VALUE,
				valuesJSONObject.getString("clientSecret"));
			Assert.assertEquals(
				"2026-11-02", valuesJSONObject.getString("date"));
			Assert.assertEquals(
				"2026-11-02T13:45", valuesJSONObject.getString("dateTime"));
			Assert.assertFalse(valuesJSONObject.has("id"));
			Assert.assertEquals(
				"https://commerce.minium.com",
				valuesJSONObject.getString("url"));
		}
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String
		_PIM_CONNECTOR_CONFIGURATION_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE =
			RandomTestUtil.randomString();

	private static final long
		_PIM_CONNECTOR_CONFIGURATION_OBJECT_DEFINITION_ID =
			RandomTestUtil.randomLong();

	private static final String _PIM_CONNECTOR_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

}