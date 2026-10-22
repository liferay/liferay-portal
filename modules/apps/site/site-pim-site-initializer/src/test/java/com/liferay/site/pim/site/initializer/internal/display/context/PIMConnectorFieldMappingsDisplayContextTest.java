/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class PIMConnectorFieldMappingsDisplayContextTest {

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
			String.valueOf(_OBJECT_ENTRY_ID)
		);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPathFriendlyURLPublic()
		).thenReturn(
			"/web"
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			"/cms"
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
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

		LanguageUtil languageUtil = new LanguageUtil();

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
			language.get(
				_httpServletRequest,
				"the-connector-and-all-its-field-mappings-will-be-deleted.-" +
					"this-action-cannot-be-undone")
		).thenReturn(
			"The connector and all its field mappings will be deleted. This " +
				"action cannot be undone."
		);

		Mockito.when(
			language.get(_httpServletRequest, "delete")
		).thenReturn(
			"delete"
		);

		Mockito.when(
			language.get(_httpServletRequest, "execute")
		).thenReturn(
			"execute"
		);

		Mockito.when(
			language.get(
				_httpServletRequest,
				"execution-has-started-successfully-and-will-continue-in-the-" +
					"background")
		).thenReturn(
			"Execution has started successfully and will continue in the " +
				"background."
		);

		Mockito.when(
			language.get(_httpServletRequest, "field-mappings")
		).thenReturn(
			"Field Mappings"
		);

		Mockito.when(
			language.get(_httpServletRequest, "no-fields-were-found")
		).thenReturn(
			"No fields were found."
		);

		Mockito.when(
			language.get(_httpServletRequest, "schedule")
		).thenReturn(
			"schedule"
		);

		Mockito.when(
			language.get(
				_httpServletRequest,
				"this-connector-does-not-declare-any-fields")
		).thenReturn(
			"This connector does not declare any fields."
		);

		Mockito.when(
			language.format(
				Mockito.eq(_httpServletRequest), Mockito.eq("delete-x"),
				Mockito.any(Object.class))
		).thenAnswer(
			invocationOnMock -> "Delete " + invocationOnMock.getArgument(2)
		);

		languageUtil.setLanguage(language);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(Mockito.anyString())
		).thenReturn(
			Mockito.mock(PIMConnector.class)
		);
	}

	@Test
	public void testGetBreadcrumbProps() throws Exception {
		PIMConnectorFieldMappingsDisplayContext
			pimConnectorFieldMappingsDisplayContext =
				new PIMConnectorFieldMappingsDisplayContext(
					_httpServletRequest, _mockObjectDefinition(),
					Mockito.mock(ObjectEntryLocalService.class),
					_pimConnectorRegistry);

		Map<String, Object> breadcrumbProps =
			pimConnectorFieldMappingsDisplayContext.getBreadcrumbProps();

		JSONArray jsonArray = (JSONArray)breadcrumbProps.get("breadcrumbItems");

		JSONObject jsonObject = jsonArray.getJSONObject(1);

		Assert.assertEquals("Field Mappings", jsonObject.getString("label"));

		String name = RandomTestUtil.randomString();

		pimConnectorFieldMappingsDisplayContext =
			_createPIMConnectorFieldMappingsDisplayContext(
				HashMapBuilder.<String, Serializable>put(
					"name", name
				).build());

		breadcrumbProps =
			pimConnectorFieldMappingsDisplayContext.getBreadcrumbProps();

		jsonArray = (JSONArray)breadcrumbProps.get("actionItems");

		Assert.assertEquals(jsonArray.toString(), 3, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			StringBundler.concat(
				"/web/cms/edit-connector?backURL=",
				URLCodec.encodeURL(_URL_CURRENT), "&objectEntryId=",
				_OBJECT_ENTRY_ID),
			jsonObject.getString("href"));
		Assert.assertEquals("edit", jsonObject.getString("label"));
		Assert.assertEquals("pencil", jsonObject.getString("symbolLeft"));

		jsonObject = jsonArray.getJSONObject(1);

		Assert.assertEquals(
			StringBundler.concat(
				"/o/pim/connector/schedule?backURL=",
				URLCodec.encodeURL(_URL_CURRENT), "&objectEntryId=",
				_OBJECT_ENTRY_ID),
			jsonObject.getString("href"));
		Assert.assertEquals("schedule", jsonObject.getString("label"));
		Assert.assertEquals("date-time", jsonObject.getString("symbolLeft"));

		jsonObject = jsonArray.getJSONObject(2);

		Assert.assertEquals("text-danger", jsonObject.getString("className"));
		Assert.assertEquals(
			"The connector and all its field mappings will be deleted. This " +
				"action cannot be undone.",
			jsonObject.getString("confirmationMessage"));
		Assert.assertEquals(
			"Delete " + name, jsonObject.getString("confirmationTitle"));
		Assert.assertEquals(
			"/o/c/pimconnectors/" + _OBJECT_ENTRY_ID,
			jsonObject.getString("href"));
		Assert.assertEquals("delete", jsonObject.getString("label"));
		Assert.assertEquals(
			"/web/cms/connectors", jsonObject.getString("redirect"));
		Assert.assertEquals("trash", jsonObject.getString("symbolLeft"));
		Assert.assertEquals("asyncDelete", jsonObject.getString("target"));

		jsonArray = (JSONArray)breadcrumbProps.get("breadcrumbItems");

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertFalse(jsonObject.getBoolean("active"));
		Assert.assertEquals(
			"/web/cms/connectors", jsonObject.getString("href"));
		Assert.assertEquals("Connectors", jsonObject.getString("label"));

		jsonObject = jsonArray.getJSONObject(1);

		Assert.assertTrue(jsonObject.getBoolean("active"));
		Assert.assertEquals("", jsonObject.getString("href"));
		Assert.assertEquals(name, jsonObject.getString("label"));

		Assert.assertEquals(Boolean.TRUE, breadcrumbProps.get("hideSpace"));
		Assert.assertEquals(
			Boolean.TRUE, breadcrumbProps.get("mappingComplete"));
		Assert.assertEquals("lg", breadcrumbProps.get("size"));

		pimConnectorFieldMappingsDisplayContext =
			_createPIMConnectorFieldMappingsDisplayContext(
				HashMapBuilder.<String, Serializable>put(
					"active", true
				).put(
					"name", name
				).build());

		breadcrumbProps =
			pimConnectorFieldMappingsDisplayContext.getBreadcrumbProps();

		jsonArray = (JSONArray)breadcrumbProps.get("actionItems");

		Assert.assertEquals(jsonArray.toString(), 4, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(1);

		Assert.assertEquals(
			"/o/pim/connector/execute?objectEntryId=" + _OBJECT_ENTRY_ID,
			jsonObject.getString("href"));
		Assert.assertEquals("execute", jsonObject.getString("id"));
		Assert.assertEquals("execute", jsonObject.getString("label"));
		Assert.assertEquals(
			"Execution has started successfully and will continue in the " +
				"background.",
			jsonObject.getString("successMessage"));
		Assert.assertEquals("play", jsonObject.getString("symbolLeft"));
		Assert.assertEquals("asyncPost", jsonObject.getString("target"));
	}

	@Test
	public void testGetBreadcrumbPropsWhenMappingIsIncomplete()
		throws Exception {

		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getPIMConnectorChannelFields()
		).thenReturn(
			Collections.singletonList(
				new PIMConnectorChannelField("sku", false, "sku", true, "Text"))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(Mockito.anyString())
		).thenReturn(
			pimConnector
		);

		try (MockedStatic<ObjectRelationshipLocalServiceUtil>
				objectRelationshipLocalServiceUtilMockedStatic =
					Mockito.mockStatic(
						ObjectRelationshipLocalServiceUtil.class)) {

			PIMConnectorFieldMappingsDisplayContext
				pimConnectorFieldMappingsDisplayContext =
					_createPIMConnectorFieldMappingsDisplayContext(
						HashMapBuilder.<String, Serializable>put(
							"active", true
						).put(
							"key", "liferay-commerce"
						).put(
							"name", RandomTestUtil.randomString()
						).build());

			Map<String, Object> breadcrumbProps =
				pimConnectorFieldMappingsDisplayContext.getBreadcrumbProps();

			JSONArray jsonArray = (JSONArray)breadcrumbProps.get("actionItems");

			Assert.assertEquals(jsonArray.toString(), 3, jsonArray.length());

			JSONObject jsonObject = jsonArray.getJSONObject(0);

			Assert.assertEquals("edit", jsonObject.getString("label"));

			jsonObject = jsonArray.getJSONObject(1);

			Assert.assertEquals("schedule", jsonObject.getString("label"));

			jsonObject = jsonArray.getJSONObject(2);

			Assert.assertEquals("delete", jsonObject.getString("label"));

			Assert.assertEquals(
				Boolean.FALSE, breadcrumbProps.get("mappingComplete"));
		}
	}

	@Test
	public void testGetContextParams() {
		PIMConnectorFieldMappingsDisplayContext
			pimConnectorFieldMappingsDisplayContext =
				_createPIMConnectorFieldMappingsDisplayContext(
					HashMapBuilder.<String, Serializable>put(
						"name", RandomTestUtil.randomString()
					).build());

		Map<String, String> contextParams =
			pimConnectorFieldMappingsDisplayContext.getContextParams();

		Assert.assertEquals(
			StringBundler.concat(
				"/web/cms/edit-field-mappings?backURL=",
				URLCodec.encodeURL(_URL_CURRENT), "&objectEntryId=",
				_OBJECT_ENTRY_ID),
			URLCodec.decodeURL(contextParams.get("editFieldMappingsURL")));
		Assert.assertEquals(
			String.valueOf(_OBJECT_ENTRY_ID),
			contextParams.get("objectEntryId"));
	}

	@Test
	public void testGetEmptyState() {
		PIMConnectorFieldMappingsDisplayContext
			pimConnectorFieldMappingsDisplayContext =
				_createPIMConnectorFieldMappingsDisplayContext(
					HashMapBuilder.<String, Serializable>put(
						"name", RandomTestUtil.randomString()
					).build());

		Map<String, Object> emptyState =
			pimConnectorFieldMappingsDisplayContext.getEmptyState();

		Assert.assertEquals(
			"This connector does not declare any fields.",
			emptyState.get("description"));
		Assert.assertEquals("No fields were found.", emptyState.get("title"));
	}

	private PIMConnectorFieldMappingsDisplayContext
		_createPIMConnectorFieldMappingsDisplayContext(
			Map<String, Serializable> values) {

		ObjectEntryLocalService objectEntryLocalService = Mockito.mock(
			ObjectEntryLocalService.class);

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			values
		);

		Mockito.when(
			objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			objectEntry
		);

		return new PIMConnectorFieldMappingsDisplayContext(
			_httpServletRequest, _mockObjectDefinition(),
			objectEntryLocalService, _pimConnectorRegistry);
	}

	private ObjectDefinition _mockObjectDefinition() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getRESTContextPath()
		).thenReturn(
			"/c/pimconnectors"
		);

		return objectDefinition;
	}

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final String _URL_CURRENT =
		"/web/cms/field-mappings?objectEntryId=" + _OBJECT_ENTRY_ID;

	private HttpServletRequest _httpServletRequest;
	private final PIMConnectorRegistry _pimConnectorRegistry = Mockito.mock(
		PIMConnectorRegistry.class);

}