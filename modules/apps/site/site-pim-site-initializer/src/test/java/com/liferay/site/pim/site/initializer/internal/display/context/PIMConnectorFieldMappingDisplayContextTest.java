/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 */
public class PIMConnectorFieldMappingDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_httpServletRequest.getParameter("objectEntryId")
		).thenReturn(
			"1"
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			"/cms"
		);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPathFriendlyURLPublic()
		).thenReturn(
			"/web"
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			themeDisplay.getScopeGroupId()
		).thenReturn(
			20121L
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			_URL_CURRENT
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(_httpServletRequest, "connectors")
		).thenReturn(
			"Connectors"
		);

		Mockito.when(
			language.get(_httpServletRequest, "edit")
		).thenReturn(
			"edit"
		);

		Mockito.when(
			language.get(_httpServletRequest, "field-mapping")
		).thenReturn(
			"Field Mapping"
		);

		Mockito.when(
			language.get(_httpServletRequest, "no-fields-were-found")
		).thenReturn(
			"No fields were found."
		);

		Mockito.when(
			language.get(
				_httpServletRequest,
				"this-connector-does-not-declare-any-field")
		).thenReturn(
			"This connector does not declare any field."
		);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(language);
	}

	@Test
	public void testGetBreadcrumbProps() {
		Map<String, Object> breadcrumbProps = _createDisplayContext(
			HashMapBuilder.<String, Serializable>put(
				"name", "Ushio Commerce"
			).build()
		).getBreadcrumbProps();

		Assert.assertEquals(Boolean.TRUE, breadcrumbProps.get("hideSpace"));
		Assert.assertEquals("lg", breadcrumbProps.get("size"));

		JSONArray actionItemsJSONArray = (JSONArray)breadcrumbProps.get(
			"actionItems");

		Assert.assertEquals(
			actionItemsJSONArray.toString(), 1, actionItemsJSONArray.length());

		JSONObject actionItemJSONObject = actionItemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			"/web/cms/edit-connector?backURL=" +
				URLCodec.encodeURL(_URL_CURRENT) + "&objectEntryId=1",
			actionItemJSONObject.getString("href"));
		Assert.assertEquals("edit", actionItemJSONObject.getString("label"));

		JSONArray breadcrumbItemsJSONArray = (JSONArray)breadcrumbProps.get(
			"breadcrumbItems");

		Assert.assertEquals(
			breadcrumbItemsJSONArray.toString(), 2,
			breadcrumbItemsJSONArray.length());

		JSONObject breadcrumbItemJSONObject =
			breadcrumbItemsJSONArray.getJSONObject(0);

		Assert.assertFalse(breadcrumbItemJSONObject.getBoolean("active"));
		Assert.assertEquals(
			"/web/cms/connectors", breadcrumbItemJSONObject.getString("href"));
		Assert.assertEquals(
			"Connectors", breadcrumbItemJSONObject.getString("label"));

		breadcrumbItemJSONObject = breadcrumbItemsJSONArray.getJSONObject(1);

		Assert.assertTrue(breadcrumbItemJSONObject.getBoolean("active"));
		Assert.assertEquals("", breadcrumbItemJSONObject.getString("href"));
		Assert.assertEquals(
			"Ushio Commerce", breadcrumbItemJSONObject.getString("label"));
	}

	@Test
	public void testGetBreadcrumbPropsWhenObjectEntryIsMissing() {
		PIMConnectorFieldMappingDisplayContext
			pimConnectorFieldMappingDisplayContext =
				new PIMConnectorFieldMappingDisplayContext(
					_httpServletRequest,
					Mockito.mock(ObjectEntryLocalService.class));

		Map<String, Object> breadcrumbProps =
			pimConnectorFieldMappingDisplayContext.getBreadcrumbProps();

		JSONArray breadcrumbItemsJSONArray = (JSONArray)breadcrumbProps.get(
			"breadcrumbItems");

		JSONObject breadcrumbItemJSONObject =
			breadcrumbItemsJSONArray.getJSONObject(1);

		Assert.assertEquals(
			"Field Mapping", breadcrumbItemJSONObject.getString("label"));
	}

	@Test
	public void testGetContextParams() {
		Map<String, String> contextParams = _createDisplayContext(
			HashMapBuilder.<String, Serializable>put(
				"name", "Ushio Commerce"
			).build()
		).getContextParams();

		Assert.assertEquals("1", contextParams.get("objectEntryId"));
		Assert.assertEquals(
			"/web/cms/map-channel-field?objectEntryId=1",
			URLCodec.decodeURL(contextParams.get("mapChannelFieldURL")));
	}

	@Test
	public void testGetEmptyState() {
		Map<String, Object> emptyState = _createDisplayContext(
			HashMapBuilder.<String, Serializable>put(
				"name", "Ushio Commerce"
			).build()
		).getEmptyState();

		Assert.assertEquals(
			"This connector does not declare any field.",
			emptyState.get("description"));
		Assert.assertEquals("No fields were found.", emptyState.get("title"));
	}

	private PIMConnectorFieldMappingDisplayContext _createDisplayContext(
		Map<String, Serializable> values) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			values
		);

		ObjectEntryLocalService objectEntryLocalService = Mockito.mock(
			ObjectEntryLocalService.class);

		Mockito.when(
			objectEntryLocalService.fetchObjectEntry(1L)
		).thenReturn(
			objectEntry
		);

		return new PIMConnectorFieldMappingDisplayContext(
			_httpServletRequest, objectEntryLocalService);
	}

	private static final String _URL_CURRENT =
		"/web/cms/field-mapping?objectEntryId=1";

	private HttpServletRequest _httpServletRequest;

}