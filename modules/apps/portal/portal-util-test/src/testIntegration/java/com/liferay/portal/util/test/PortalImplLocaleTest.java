/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.servlet.I18nServlet;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalImpl;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Filipe Afonso
 */
@RunWith(Arquillian.class)
public class PortalImplLocaleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_availableLocales = _language.getAvailableLocales();

		PropsValues.LOCALES_ENABLED = new String[] {
			"ca_ES", "en_US", "fr_FR", "de_DE", "pt_BR", "es_ES", "en_GB"
		};

		_language.init();

		LanguageResources.getSuperLocale(LocaleUtil.GERMANY);
		LanguageResources.getSuperLocale(LocaleUtil.US);

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		CompanyTestUtil.resetCompanyLocales(
			_group.getCompanyId(),
			Arrays.asList(
				LocaleUtil.fromLanguageId("ca_ES"), LocaleUtil.US,
				LocaleUtil.FRANCE, LocaleUtil.GERMANY, LocaleUtil.BRAZIL,
				LocaleUtil.SPAIN, LocaleUtil.UK),
			LocaleUtil.getDefault());

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.UK, LocaleUtil.GERMANY),
			LocaleUtil.GERMANY);
	}

	@After
	public void tearDown() throws Exception {
		PropsValues.LOCALES_ENABLED = _props.getArray(
			PropsKeys.LOCALES_ENABLED);

		_language.init();

		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), _availableLocales,
			LocaleUtil.getDefault());
	}

	@Test
	public void testInvalidResourceWithLocale() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			_testLocaleForLanguageId(
				"/en", "/WEB-INF/web.xml;.js", LocaleUtil.GERMANY);

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());
	}

	@Test
	public void testSiteAvailableLanguageId() throws Exception {
		_testLocaleForLanguageId("/en", LocaleUtil.UK);
	}

	@Test
	public void testSiteAvailableLocale() throws Exception {
		_testLocaleForLanguageId("/en_GB", LocaleUtil.UK);
	}

	@Test
	public void testSiteDefaultLanguageId() throws Exception {
		_testLocaleForLanguageId("/de", LocaleUtil.GERMANY);
	}

	@Test
	public void testSiteDefaultLocale() throws Exception {
		_testLocaleForLanguageId("/de_DE", LocaleUtil.GERMANY);
	}

	private void _testLocaleForLanguageId(
			String i18nLanguageId, Locale expectedLocale)
		throws Exception {

		_testLocaleForLanguageId(
			i18nLanguageId, _group.getFriendlyURL() + _layout.getFriendlyURL(),
			expectedLocale);
	}

	private MockHttpServletResponse _testLocaleForLanguageId(
			String i18nLanguageId, String pathInfo, Locale expectedLocale)
		throws Exception {

		MockServletContext mockServletContext = new MockServletContext() {
		};

		mockServletContext.setContextPath(StringPool.BLANK);
		mockServletContext.setServletContextName(StringPool.BLANK);

		_i18nServlet.init(new MockServletConfig(mockServletContext));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(
				mockServletContext, HttpMethods.GET, i18nLanguageId + pathInfo);

		mockHttpServletRequest.addHeader("Host", "localhost");
		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockHttpServletRequest.setPathInfo(pathInfo);
		mockHttpServletRequest.setServletPath(i18nLanguageId);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletRequest.setCookies(mockHttpServletResponse.getCookies());

		_i18nServlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			expectedLocale,
			_portalImpl.getLocale(
				mockHttpServletRequest, mockHttpServletResponse, false));

		return mockHttpServletResponse;
	}

	private Set<Locale> _availableLocales;

	@DeleteAfterTestRun
	private Group _group;

	private final I18nServlet _i18nServlet = new I18nServlet();

	@Inject
	private Language _language;

	@DeleteAfterTestRun
	private Layout _layout;

	private final PortalImpl _portalImpl = new PortalImpl();

	@Inject
	private Props _props;

}