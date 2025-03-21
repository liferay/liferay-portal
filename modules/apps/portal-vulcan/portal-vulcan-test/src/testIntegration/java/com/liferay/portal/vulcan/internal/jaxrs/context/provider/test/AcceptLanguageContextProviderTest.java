/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockFeature;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.test.util.MockMessage;

import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.HttpHeaders;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.apache.cxf.jaxrs.ext.ContextProvider;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class AcceptLanguageContextProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_availableLocales = LanguageUtil.getAvailableLocales();
		_defaultLocale = LocaleUtil.getDefault();

		_company = CompanyTestUtil.addCompany();

		CompanyTestUtil.resetCompanyLocales(
			_company.getCompanyId(),
			Arrays.asList(
				LocaleUtil.BRAZIL, new Locale("ca", "ES", "VALENCIA"),
				LocaleUtil.GERMAN, LocaleUtil.JAPAN, new Locale("sr_RS_latin"),
				LocaleUtil.TAIWAN),
			LocaleUtil.TAIWAN);

		_company = CompanyLocalServiceUtil.getCompany(_company.getCompanyId());

		_user = UserTestUtil.addCompanyAdminUser(_company);

		_group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _user.getUserId(), 0L);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		CompanyTestUtil.resetCompanyLocales(
			PortalUtil.getDefaultCompanyId(), _availableLocales,
			_defaultLocale);
	}

	@Before
	public void setUp() {
		MockFeature mockFeature = new MockFeature(_feature);

		_contextProvider =
			(ContextProvider<AcceptLanguage>)mockFeature.getObject(
				"com.liferay.portal.vulcan.internal.jaxrs.context.provider." +
					"AcceptLanguageContextProvider");
	}

	@Test
	public void testCreateContextWithAuthenticatedUser() throws Exception {
		User user = UserTestUtil.addUser(
			_group.getGroupId(), LocaleUtil.BRAZIL);

		_testCreateContext(Http.Method.GET, user, LocaleUtil.BRAZIL);
		_testCreateContext(Http.Method.POST, user, LocaleUtil.BRAZIL);
	}

	@Test
	public void testCreateContextWithGuestUser() throws Exception {
		User user = _company.getGuestUser();

		_testCreateContext(Http.Method.GET, user, LocaleUtil.TAIWAN);
		_testCreateContext(Http.Method.POST, user, LocaleUtil.TAIWAN);
	}

	private void _testCreateContext(
			Http.Method method, User user, Locale userLocale)
		throws Exception {

		// One locale

		_contextProvider.createContext(
			new MockMessage(
				new AcceptLanguageMockHttpServletRequest(
					method, user, LocaleUtil.JAPAN)));

		// One locale with variant

		Locale caLocale = new Locale("ca", "ES", "VALENCIA");

		AcceptLanguage acceptLanguage = _contextProvider.createContext(
			new MockMessage(
				new AcceptLanguageMockHttpServletRequest(
					method, user, caLocale)));

		Assert.assertEquals(caLocale, acceptLanguage.getPreferredLocale());

		Locale srLocale = new Locale("sr", "RS", "latin");

		acceptLanguage = _contextProvider.createContext(
			new MockMessage(
				new AcceptLanguageMockHttpServletRequest(
					method, user, srLocale)));

		Assert.assertEquals(srLocale, acceptLanguage.getPreferredLocale());

		// One partial locale

		acceptLanguage = _contextProvider.createContext(
			new MockMessage(
				new AcceptLanguageMockHttpServletRequest(
					method, user, new Locale("pt", ""))));

		Assert.assertEquals(
			LocaleUtil.BRAZIL, acceptLanguage.getPreferredLocale());

		// Three locales

		acceptLanguage = _contextProvider.createContext(
			new MockMessage(
				new AcceptLanguageMockHttpServletRequest(
					method, user, LocaleUtil.GERMAN, LocaleUtil.JAPAN,
					LocaleUtil.US)));

		Assert.assertEquals(
			LocaleUtil.GERMAN, acceptLanguage.getPreferredLocale());

		// No locales

		Assert.assertEquals(userLocale, user.getLocale());

		acceptLanguage = _contextProvider.createContext(
			new MockMessage(
				new AcceptLanguageMockHttpServletRequest(method, user)));

		Assert.assertEquals(
			user.getLocale(), acceptLanguage.getPreferredLocale());

		// Unavailable locale

		acceptLanguage = _contextProvider.createContext(
			new MockMessage(
				new AcceptLanguageMockHttpServletRequest(
					method, user, LocaleUtil.SPAIN)));

		if (Objects.equals(method, Http.Method.GET)) {
			Assert.assertEquals(
				user.getLocale(), acceptLanguage.getPreferredLocale());
		}
		else {
			try {
				Locale locale = acceptLanguage.getPreferredLocale();

				Assert.fail(
					"The locale  " + locale + " should not be available");
			}
			catch (Exception exception) {
				Assert.assertEquals(
					NotAcceptableException.class, exception.getClass());
				Assert.assertEquals(
					"No locales match the accepted languages: es-es",
					exception.getMessage());
			}
		}
	}

	private static Set<Locale> _availableLocales;
	private static Company _company;
	private static Locale _defaultLocale;
	private static Group _group;
	private static User _user;

	private ContextProvider<AcceptLanguage> _contextProvider;

	@Inject(
		filter = "component.name=com.liferay.portal.vulcan.internal.jaxrs.feature.VulcanFeature"
	)
	private Feature _feature;

	private class AcceptLanguageMockHttpServletRequest
		extends MockHttpServletRequest {

		public AcceptLanguageMockHttpServletRequest(
				Http.Method method, User user, Locale... locales)
			throws PortalException {

			if (ArrayUtil.isNotEmpty(locales)) {
				addHeader(
					HttpHeaders.ACCEPT_LANGUAGE,
					StringUtil.merge(
						LocaleUtil.toW3cLanguageIds(locales),
						StringPool.COMMA));
			}

			addHeader("Host", _company.getVirtualHostname());

			if (!user.isGuestUser()) {
				setAttribute(WebKeys.USER_ID, user.getUserId());
			}

			setMethod(method.toString());

			if (ArrayUtil.isNotEmpty(locales)) {
				setPreferredLocales(Arrays.asList(locales));
			}

			setRemoteHost(_company.getPortalURL(_group.getGroupId()));
		}

	}

}