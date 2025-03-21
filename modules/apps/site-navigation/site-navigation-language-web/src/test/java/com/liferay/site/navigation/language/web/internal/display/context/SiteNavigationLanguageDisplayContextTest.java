/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.language.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.navigation.language.web.internal.configuration.SiteNavigationLanguagePortletInstanceConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class SiteNavigationLanguageDisplayContextTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpConfigurationProviderUtil();
		_setUpGroupLocalServiceUtil();

		Mockito.reset(_siteNavigationLanguagePortletInstanceConfiguration);
	}

	@After
	public void tearDown() {
		_configurationProviderUtilMockedStatic.close();
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testGetDisplayStyleGroupKey() throws Exception {
		SiteNavigationLanguageDisplayContext
			siteNavigationLanguageDisplayContext =
				new SiteNavigationLanguageDisplayContext(
					_mockHttpServletRequest());

		Assert.assertEquals(
			GroupConstants.GUEST,
			siteNavigationLanguageDisplayContext.getDisplayStyleGroupKey());
	}

	@Test
	public void testGetDisplayStyleGroupKeyWithConfiguration()
		throws Exception {

		Mockito.when(
			_siteNavigationLanguagePortletInstanceConfiguration.
				displayStyleGroupKey()
		).thenReturn(
			GroupConstants.CONTROL_PANEL
		);

		SiteNavigationLanguageDisplayContext
			siteNavigationLanguageDisplayContext =
				new SiteNavigationLanguageDisplayContext(
					_mockHttpServletRequest());

		Assert.assertEquals(
			GroupConstants.GUEST,
			siteNavigationLanguageDisplayContext.getDisplayStyleGroupKey());
	}

	@Test
	public void testGetDisplayStyleGroupKeyWithDisplayStyleGroupExternalReferenceCode()
		throws Exception {

		SiteNavigationLanguageDisplayContext
			siteNavigationLanguageDisplayContext =
				new SiteNavigationLanguageDisplayContext(
					_mockHttpServletRequest());

		Group group = _getGroup(RandomTestUtil.randomString());

		String groupExternalReferenceCode = group.getExternalReferenceCode();

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				groupExternalReferenceCode, _COMPANY_ID)
		).thenReturn(
			group
		);

		Mockito.when(
			_siteNavigationLanguagePortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			groupExternalReferenceCode
		);

		Assert.assertEquals(
			group.getGroupKey(),
			siteNavigationLanguageDisplayContext.getDisplayStyleGroupKey());
	}

	@Test
	@TestInfo("LPD-33733")
	public void testGetDisplayStyleGroupKeyWithMissingDisplayStyleGroupExternalReferenceCode()
		throws Exception {

		SiteNavigationLanguageDisplayContext
			siteNavigationLanguageDisplayContext =
				new SiteNavigationLanguageDisplayContext(
					_mockHttpServletRequest());

		Mockito.when(
			_siteNavigationLanguagePortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Assert.assertEquals(
			StringPool.BLANK,
			siteNavigationLanguageDisplayContext.getDisplayStyleGroupKey());
	}

	@Test
	public void testGetLanguageIdsFromInstanceSettings() throws Exception {
		SiteNavigationLanguageDisplayContext
			siteNavigationLanguageDisplayContext =
				new SiteNavigationLanguageDisplayContext(
					_mockHttpServletRequest());

		Mockito.when(
			_siteNavigationLanguagePortletInstanceConfiguration.languageIds()
		).thenReturn(
			StringUtil.merge(_availableLocales, StringPool.COMMA)
		);

		for (String languageId :
				siteNavigationLanguageDisplayContext.getLanguageIds()) {

			Assert.assertTrue(
				_availableLocales.contains(
					LocaleUtil.fromLanguageId(languageId)));
		}
	}

	@Test
	public void testGetLanguageIdsFromSiteSettings() throws Exception {
		_setUpLanguage();
		_setUpLanguageUtil();

		SiteNavigationLanguageDisplayContext
			siteNavigationLanguageDisplayContext =
				new SiteNavigationLanguageDisplayContext(
					_mockHttpServletRequest());

		for (String languageId :
				siteNavigationLanguageDisplayContext.getLanguageIds()) {

			Assert.assertTrue(
				_availableLocales.contains(
					LocaleUtil.fromLanguageId(languageId)));
		}
	}

	private Group _getGroup(String groupKey) {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			group.getGroupKey()
		).thenReturn(
			groupKey
		);

		return group;
	}

	private HttpServletRequest _mockHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Group group = _getGroup(GroupConstants.GUEST);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			(ThemeDisplay)httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		return httpServletRequest;
	}

	private void _setUpConfigurationProviderUtil() {
		_configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			_siteNavigationLanguagePortletInstanceConfiguration
		);
	}

	private void _setUpGroupLocalServiceUtil() {
		Group group = new GroupImpl();

		group.setGroupKey(GroupConstants.GUEST);

		Mockito.when(
			GroupLocalServiceUtil.fetchGroup(Mockito.anyLong())
		).thenReturn(
			group
		);
	}

	private void _setUpLanguage() {
		Mockito.when(
			_language.getAvailableLocales()
		).thenReturn(
			_availableLocales
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.getAvailableLocales(Mockito.anyLong())
		).thenReturn(
			_availableLocales
		);

		languageUtil.setLanguage(_language);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final Set<Locale> _availableLocales = new HashSet<>(
		Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US));
	private final MockedStatic<ConfigurationProviderUtil>
		_configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private final Language _language = Mockito.mock(Language.class);
	private final SiteNavigationLanguagePortletInstanceConfiguration
		_siteNavigationLanguagePortletInstanceConfiguration = Mockito.mock(
			SiteNavigationLanguagePortletInstanceConfiguration.class);

}