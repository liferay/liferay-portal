/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

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
				"fieldMapping", "{}"
			).put(
				"key", "liferay-commerce"
			).put(
				"name", "Liferay Commerce Connector"
			).build()
		);

		Mockito.when(
			objectEntryLocalService.fetchObjectEntry(1L)
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
		Assert.assertFalse(
			pimConnectorJSONObject.toString(),
			pimConnectorJSONObject.has("fieldMapping"));
		Assert.assertEquals(
			"liferay-commerce", pimConnectorJSONObject.getString("key"));
		Assert.assertEquals(
			"Liferay Commerce Connector",
			pimConnectorJSONObject.getString("name"));
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

}