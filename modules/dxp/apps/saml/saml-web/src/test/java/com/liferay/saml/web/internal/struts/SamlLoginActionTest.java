/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.struts;

import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pedro Victor Silvestre
 */
public class SamlLoginActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpLayoutSEOLinkManager();
		_setUpPortal();
		_setUpProps();
		_setUpSamlProviderConfigurationHelper();
		_setUpSamlSpIdpConnectionLocalService();

		_setUpSamlLoginAction();
	}

	@AfterClass
	public static void tearDownClass() {
		_listUtilMockedStatic.close();
	}

	@Test
	public void testExecute() throws Exception {
		String companyName = RandomTestUtil.randomString();
		String layoutHtmlTitle = RandomTestUtil.randomString();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(companyName, layoutHtmlTitle);

		_samlLoginAction.execute(
			mockHttpServletRequest, new MockHttpServletResponse());

		Definition definition = (Definition)mockHttpServletRequest.getAttribute(
			TilesUtil.DEFINITION);

		Assert.assertEquals(
			StringBundler.concat(
				companyName, StringPool.UNDERLINE, layoutHtmlTitle),
			MapUtil.getString(definition.getAttributes(), "title"));
	}

	private static void _setUpLayoutSEOLinkManager() {
		_layoutSEOLinkManager = new LayoutSEOLinkManager() {

			@Override
			public LayoutSEOLink getCanonicalLayoutSEOLink(
					Layout layout, Locale locale, String canonicalURL,
					ThemeDisplay themeDisplay)
				throws PortalException {

				return null;
			}

			@Override
			public String getFullPageTitle(
				Layout layout, String portletId, String tilesTitle,
				ListMergeable<String> titleListMergeable,
				ListMergeable<String> subtitleListMergeable, String companyName,
				Locale locale) {

				return StringBundler.concat(
					companyName, StringPool.UNDERLINE,
					layout.getHTMLTitle(locale));
			}

			@Override
			public List<LayoutSEOLink> getLocalizedLayoutSEOLinks(
					Layout layout, Locale locale, String canonicalURL,
					Set<Locale> availableLocales)
				throws PortalException {

				return null;
			}

		};
	}

	private static void _setUpPortal() throws Exception {
		_portal = Mockito.mock(Portal.class);

		Mockito.when(
			_portal.getCompanyId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			RandomTestUtil.randomLong()
		);
	}

	private static void _setUpProps() throws Exception {
		_props = Mockito.mock(Props.class);

		Mockito.when(
			_props.get("saml.idp.redirect.message.enabled")
		).thenReturn(
			"false"
		);
	}

	private static void _setUpSamlLoginAction() throws Exception {
		_samlLoginAction = new SamlLoginAction();

		ReflectionTestUtil.setFieldValue(
			_samlLoginAction, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_samlLoginAction, "_layoutSEOLinkManager", _layoutSEOLinkManager);
		ReflectionTestUtil.setFieldValue(_samlLoginAction, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(_samlLoginAction, "_props", _props);
		ReflectionTestUtil.setFieldValue(
			_samlLoginAction, "_samlProviderConfigurationHelper",
			_samlProviderConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_samlLoginAction, "_samlSpIdpConnectionLocalService",
			_samlSpIdpConnectionLocalService);
	}

	private static void _setUpSamlProviderConfigurationHelper() {
		_samlProviderConfigurationHelper = Mockito.mock(
			SamlProviderConfigurationHelper.class);

		Mockito.when(
			_samlProviderConfigurationHelper.isEnabled()
		).thenReturn(
			true
		);

		Mockito.when(
			_samlProviderConfigurationHelper.isRoleSp()
		).thenReturn(
			true
		);
	}

	private static void _setUpSamlSpIdpConnectionLocalService() {
		_samlSpIdpConnectionLocalService = Mockito.mock(
			SamlSpIdpConnectionLocalService.class);

		List<SamlSpIdpConnection> samlSpIdpConnections = new ArrayList<>();

		samlSpIdpConnections.add(Mockito.mock(SamlSpIdpConnection.class));

		Mockito.when(
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnections(
				Mockito.anyLong())
		).thenReturn(
			samlSpIdpConnections
		);

		_listUtilMockedStatic.when(
			() -> ListUtil.filter(
				Mockito.eq(samlSpIdpConnections), Mockito.any(Predicate.class))
		).thenReturn(
			samlSpIdpConnections
		);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		String companyName, String layoutHtmlTitle) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getName()
		).thenReturn(
			companyName
		);

		Mockito.when(
			themeDisplay.getCompany()
		).thenReturn(
			company
		);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getHTMLTitle(Mockito.any(Locale.class))
		).thenReturn(
			layoutHtmlTitle
		);

		Mockito.when(
			themeDisplay.getLayout()
		).thenReturn(
			layout
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.ENGLISH
		);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private static LayoutSEOLinkManager _layoutSEOLinkManager;
	private static final MockedStatic<ListUtil> _listUtilMockedStatic =
		Mockito.mockStatic(ListUtil.class);
	private static Portal _portal;
	private static Props _props;
	private static SamlLoginAction _samlLoginAction;
	private static SamlProviderConfigurationHelper
		_samlProviderConfigurationHelper;
	private static SamlSpIdpConnectionLocalService
		_samlSpIdpConnectionLocalService;

}