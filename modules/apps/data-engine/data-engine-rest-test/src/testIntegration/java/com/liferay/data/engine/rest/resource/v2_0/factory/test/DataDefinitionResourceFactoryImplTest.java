/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.factory.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DataDefinitionResourceFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testHttpServletRequestLocale() throws Exception {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.httpServletRequest(
				new MockHttpServletRequest() {

					@Override
					public Object getAttribute(String name) {
						if (StringUtil.equals(name, WebKeys.CURRENT_URL)) {
							return "http://localhost:" +
								PortalUtil.getPortalServerPort(false) +
									"/currentURL";
						}
						else if (StringUtil.equals(
									name, WebKeys.I18N_LANGUAGE_ID)) {

							return LocaleUtil.toLanguageId(LocaleUtil.BRAZIL);
						}
						else if (StringUtil.equals(name, WebKeys.LOCALE)) {
							return LocaleUtil.BRAZIL;
						}
						else if (StringUtil.equals(
									name, WebKeys.THEME_DISPLAY)) {

							return themeDisplay;
						}

						return super.getAttribute(name);
					}

				}
			).user(
				UserTestUtil.addUser()
			).build();

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			dataDefinitionResource.
				getDataDefinitionDataDefinitionFieldFieldTypes());

		_assertTranslations(jsonArray, LocaleUtil.BRAZIL);

		Map<String, JSONObject> fieldTypeJSONObjects = JSONUtil.toJSONObjectMap(
			jsonArray, "name");

		JSONArray fieldsJSONArray = JSONUtil.getValueAsJSONArray(
			fieldTypeJSONObjects.get("text"), "JSONObject/settingsContext",
			"JSONArray/pages", "JSONObject/0", "JSONArray/rows", "JSONObject/0",
			"JSONArray/columns", "JSONObject/0", "JSONArray/fields");

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(i);

			Assert.assertFalse(fieldJSONObject.isNull("value"));
		}
	}

	@Test
	public void testPreferredLocale() throws Exception {
		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.preferredLocale(
				LocaleUtil.BRAZIL
			).user(
				UserTestUtil.addUser()
			).build();

		_assertTranslations(
			_jsonFactory.createJSONArray(
				dataDefinitionResource.
					getDataDefinitionDataDefinitionFieldFieldTypes()),
			LocaleUtil.BRAZIL);
	}

	@Test
	public void testUserLocale() throws Exception {
		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				UserTestUtil.addUser()
			).build();

		_assertTranslations(
			_jsonFactory.createJSONArray(
				dataDefinitionResource.
					getDataDefinitionDataDefinitionFieldFieldTypes()),
			LocaleUtil.US);
	}

	private void _assertTranslations(JSONArray jsonArray, Locale locale) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Map<String, Object> properties =
				_ddmFormFieldTypeServicesRegistry.getDDMFormFieldTypeProperties(
					jsonObject.getString("name"));

			Assert.assertEquals(
				_translate(
					MapUtil.getString(
						properties, "ddm.form.field.type.description"),
					_getResourceBundle(jsonObject.getString("name"), locale)),
				jsonObject.getString("description"));
		}
	}

	private ResourceBundle _getResourceBundle(
		String ddmFormFieldTypeName, Locale locale) {

		DDMFormFieldType ddmFormFieldType =
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
				ddmFormFieldTypeName);

		return new AggregateResourceBundle(
			ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass()),
			ResourceBundleUtil.getBundle(
				"content.Language", locale, ddmFormFieldType.getClass()),
			_portal.getResourceBundle(locale));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group.getGroupId());

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(group.getCompanyId()), group,
			layout);

		themeDisplay.setLocale(LocaleUtil.BRAZIL);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return themeDisplay;
	}

	private String _translate(String key, ResourceBundle resourceBundle) {
		if (Validator.isNull(key)) {
			return StringPool.BLANK;
		}

		return GetterUtil.getString(
			ResourceBundleUtil.getString(resourceBundle, key), key);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Portal _portal;

}