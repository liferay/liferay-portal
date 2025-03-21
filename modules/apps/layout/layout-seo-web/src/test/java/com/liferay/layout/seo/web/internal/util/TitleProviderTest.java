/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.util;

import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.layout.utility.page.kernel.provider.util.LayoutUtilityPageEntryLayoutProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Cristina González
 */
public class TitleProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_layoutUtilityPageEntryLayoutProviderUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_setUpPortalUtil();

		_titleProvider = new TitleProvider(
			new LayoutSEOLinkManager() {

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
					ListMergeable<String> subtitleListMergeable,
					String companyName, Locale locale) {

					Group group = layout.getGroup();

					try {
						return StringUtil.merge(
							new String[] {
								layout.getHTMLTitle(locale),
								group.getDescriptiveName(), companyName
							},
							_SEPARATOR);
					}
					catch (PortalException portalException) {
					}

					return "htmlTitle";
				}

				@Override
				public List<LayoutSEOLink> getLocalizedLayoutSEOLinks(
						Layout layout, Locale locale, String canonicalURL,
						Set<Locale> availableLocales)
					throws PortalException {

					return Collections.emptyList();
				}

			});
	}

	@Test
	public void testGetTitle() throws PortalException {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String companyName = RandomTestUtil.randomString();
		String groupDescriptiveName = RandomTestUtil.randomString();
		String htmlTitle = RandomTestUtil.randomString();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(
				companyName, groupDescriptiveName, htmlTitle,
				HttpServletResponse.SC_OK));

		Assert.assertEquals(
			StringUtil.merge(
				new String[] {htmlTitle, groupDescriptiveName, companyName},
				_SEPARATOR),
			_titleProvider.getTitle(mockHttpServletRequest));
	}

	@Test
	public void testGetTitleStatusErrorPage() throws PortalException {
		String groupDescriptiveName = RandomTestUtil.randomString();
		String htmlTitle = RandomTestUtil.randomString();

		_setUpLayoutUtilityPageEntryLayoutProviderUtil(
			groupDescriptiveName, htmlTitle);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String companyName = RandomTestUtil.randomString();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(
				companyName, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				HttpServletResponse.SC_NOT_FOUND));

		Assert.assertEquals(
			StringUtil.merge(
				new String[] {htmlTitle, groupDescriptiveName, companyName},
				_SEPARATOR),
			_titleProvider.getTitle(mockHttpServletRequest));
	}

	private Layout _getLayout(String groupDescriptiveName, String htmlTitle)
		throws PortalException {

		Layout layout = Mockito.mock(Layout.class);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getDescriptiveName()
		).thenReturn(
			groupDescriptiveName
		);

		Mockito.when(
			layout.getGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			layout.getHTMLTitle(Mockito.any(Locale.class))
		).thenReturn(
			htmlTitle
		);

		return layout;
	}

	private ThemeDisplay _getThemeDisplay(
			String companyName, String groupDescriptiveName, String htmlTitle,
			int status)
		throws PortalException {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getName()
		).thenReturn(
			companyName
		);

		themeDisplay.setCompany(company);

		themeDisplay.setLayout(_getLayout(groupDescriptiveName, htmlTitle));
		themeDisplay.setLocale(LocaleUtil.US);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletResponse.setStatus(status);

		themeDisplay.setResponse(mockHttpServletResponse);

		return themeDisplay;
	}

	private void _setUpLayoutUtilityPageEntryLayoutProviderUtil(
			String groupDescriptiveName, String htmlTitle)
		throws PortalException {

		Layout layout = _getLayout(groupDescriptiveName, htmlTitle);

		_layoutUtilityPageEntryLayoutProviderUtilMockedStatic.when(
			() ->
				LayoutUtilityPageEntryLayoutProviderUtil.
					getDefaultLayoutUtilityPageEntryLayout(
						Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			layout
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			new MockHttpServletRequest()
		);

		portalUtil.setPortal(portal);
	}

	private static final String _SEPARATOR = " - ";

	private static final MockedStatic<LayoutUtilityPageEntryLayoutProviderUtil>
		_layoutUtilityPageEntryLayoutProviderUtilMockedStatic =
			Mockito.mockStatic(LayoutUtilityPageEntryLayoutProviderUtil.class);

	private TitleProvider _titleProvider;

}