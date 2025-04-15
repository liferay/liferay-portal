/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.test.util;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.junit.Before;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
public abstract class BaseDDMFormFieldTemplateContextContributorTestCase {

	@Before
	public void setUp() throws Exception {
		setUpLanguageUtil();
		setUpResourceBundleUtil();
	}

	protected DDMFormFieldRenderingContext
		createDDMFormFieldRenderingContext() {

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			0L
		).when(
			themeDisplay
		).getPlid();

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			true
		);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		ddmFormFieldRenderingContext.setHttpServletRequest(httpServletRequest);

		ddmFormFieldRenderingContext.setLocale(LocaleUtil.US);

		return ddmFormFieldRenderingContext;
	}

	protected DDMForm getDDMForm() {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setDefaultLocale(LocaleUtil.US);

		return ddmForm;
	}

	protected void setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			language.getAvailableLocales()
		).thenReturn(
			availableLocales
		);

		Mockito.when(
			language.getLanguageId(LocaleUtil.BRAZIL)
		).thenReturn(
			"pt_BR"
		);

		Mockito.when(
			language.getLanguageId(LocaleUtil.US)
		).thenReturn(
			"en_US"
		);

		languageUtil.setLanguage(language);
	}

	protected void setUpResourceBundleUtil() {
		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);

		ResourceBundle mockResourceBundle = Mockito.mock(ResourceBundle.class);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(Mockito.eq(LocaleUtil.US))
		).thenReturn(
			mockResourceBundle
		);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(
				Mockito.eq(LocaleUtil.BRAZIL))
		).thenReturn(
			mockResourceBundle
		);
	}

	protected Set<Locale> availableLocales = new HashSet<>(
		Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.US));
	protected Language language = Mockito.mock(Language.class);

}