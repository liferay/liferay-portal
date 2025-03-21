/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.search.location;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Rodrigo Paulino
 */
public class SearchLocationDDMFormFieldTemplateContextContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_setUpGooglePlacesUtil();
		_setUpJSONFactory();
		_setUpJSONFactoryUtil();
		_setUpLanguage();
		_setUpResourceBundleUtil();
	}

	@Test
	public void testGetGooglePlacesAPIKey() {
		DDMFormField ddmFormField =
			DDMFormTestUtil.createSearchLocationDDMFormField(
				DDMFormValuesTestUtil.createLocalizedValue(
					Arrays.toString(new String[] {"two-columns"}),
					LocaleUtil.US),
				StringUtil.randomString(),
				DDMFormValuesTestUtil.createLocalizedValue(
					Arrays.toString(new String[] {"city", "country"}),
					LocaleUtil.US));

		ddmFormField.setProperty(
			"googlePlacesAPIKey", "googlePlacesAPIKeyDDMFormField");

		Map<String, Object> parameters =
			_searchLocationDDMFormFieldTemplateContextContributor.getParameters(
				ddmFormField, _createDDMFormFieldRenderingContext());

		Assert.assertEquals(
			"googlePlacesAPIKeyDDMFormField",
			parameters.get("googlePlacesAPIKey"));
	}

	@Test
	public void testGetParameters() throws Exception {
		Map<String, Object> parameters =
			_searchLocationDDMFormFieldTemplateContextContributor.getParameters(
				DDMFormTestUtil.createSearchLocationDDMFormField(
					DDMFormValuesTestUtil.createLocalizedValue(
						Arrays.toString(new String[] {"two-columns"}),
						LocaleUtil.US),
					StringUtil.randomString(),
					DDMFormValuesTestUtil.createLocalizedValue(
						Arrays.toString(new String[] {"city", "country"}),
						LocaleUtil.US)),
				_createDDMFormFieldRenderingContext());

		Assert.assertEquals(
			"googlePlacesAPIKey", parameters.get("googlePlacesAPIKey"));
		JSONAssert.assertEquals(
			JSONUtil.put(
				"city", "City"
			).put(
				"country", "Country"
			).toString(),
			String.valueOf(parameters.get("labels")),
			JSONCompareMode.STRICT_ORDER);
		Assert.assertEquals(
			Arrays.toString(new String[] {"two-columns"}),
			parameters.get("layout"));
		Assert.assertEquals(
			Arrays.toString(new String[] {"city", "country"}),
			parameters.get("visibleFields"));
	}

	private static void _mockGet(String key, String message) {
		Mockito.when(
			_language.get(Mockito.any(ResourceBundle.class), Mockito.eq(key))
		).thenReturn(
			message
		);
	}

	private static void _setUpGooglePlacesUtil() {
		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);
		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		Mockito.when(
			portletPreferences.getValue(
				Mockito.nullable(String.class), Mockito.nullable(String.class))
		).thenReturn(
			"googlePlacesAPIKey"
		);

		PrefsPropsUtil prefsPropsUtil = new PrefsPropsUtil();

		PrefsProps prefsProps = Mockito.mock(PrefsProps.class);

		ReflectionTestUtil.setFieldValue(
			_searchLocationDDMFormFieldTemplateContextContributor,
			"_groupLocalService", groupLocalService);

		Mockito.when(
			prefsProps.getPreferences(Mockito.anyLong())
		).thenReturn(
			portletPreferences
		);

		prefsPropsUtil.setPrefsProps(prefsProps);
	}

	private static void _setUpJSONFactory() {
		ReflectionTestUtil.setFieldValue(
			_searchLocationDDMFormFieldTemplateContextContributor,
			"_jsonFactory", new JSONFactoryImpl());
	}

	private static void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private static void _setUpLanguage() {
		_language = Mockito.mock(Language.class);

		_mockGet("address", "Address");
		_mockGet("city", "City");
		_mockGet("country", "Country");
		_mockGet("postal-code", "Postal Code");
		_mockGet("state", "State");

		ReflectionTestUtil.setFieldValue(
			_searchLocationDDMFormFieldTemplateContextContributor, "_language",
			_language);
	}

	private static void _setUpResourceBundleUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);

		portalUtil.setPortal(portal);

		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(LocaleUtil.BRAZIL)
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);
	}

	private DDMFormFieldRenderingContext _createDDMFormFieldRenderingContext() {
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		ddmFormFieldRenderingContext.setHttpServletRequest(
			mockHttpServletRequest);

		ddmFormFieldRenderingContext.setLocale(LocaleUtil.US);
		ddmFormFieldRenderingContext.setProperty("groupId", _GROUP_ID);

		return ddmFormFieldRenderingContext;
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static Language _language;
	private static final SearchLocationDDMFormFieldTemplateContextContributor
		_searchLocationDDMFormFieldTemplateContextContributor =
			new SearchLocationDDMFormFieldTemplateContextContributor();

}