/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.breadcrumb.web.internal.display.context;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.navigation.breadcrumb.web.internal.configuration.SiteNavigationBreadcrumbPortletInstanceConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class SiteNavigationBreadcrumbDisplayContextTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			_siteNavigationBreadcrumbPortletInstanceConfiguration
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_configurationProviderUtilMockedStatic.close();
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testGetDisplayStyleGroup() throws Exception {
		_setUpDisplayStyleGroupExternalReferenceCode(null);
		_setUpGroupLocalServiceUtil(_getGroup());

		Group group = _getGroup();

		_assertSiteNavigationBreadcrumbDisplayContext(
			_mockHttpServletRequest(group), group);

		_groupLocalServiceUtilMockedStatic.verifyNoInteractions();
	}

	@Test
	public void testGetDisplayStyleGroupWithConfiguration() throws Exception {
		Group group = _getGroup();

		_setUpDisplayStyleGroupExternalReferenceCode(
			group.getExternalReferenceCode());
		_setUpGroupLocalServiceUtil(group);

		_assertSiteNavigationBreadcrumbDisplayContext(
			_mockHttpServletRequest(_getGroup()), group);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), 0L),
			Mockito.times(2));
	}

	private void _assertSiteNavigationBreadcrumbDisplayContext(
			HttpServletRequest httpServletRequest, Group group)
		throws Exception {

		SiteNavigationBreadcrumbDisplayContext
			siteNavigationBreadcrumbDisplayContext =
				new SiteNavigationBreadcrumbDisplayContext(
					httpServletRequest, null);

		Assert.assertEquals(
			group.getGroupId(),
			siteNavigationBreadcrumbDisplayContext.getDisplayStyleGroupId());
		Assert.assertEquals(
			group.getGroupKey(),
			siteNavigationBreadcrumbDisplayContext.getDisplayStyleGroupKey());
	}

	private Group _getGroup() {
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
			RandomTestUtil.randomString()
		);

		return group;
	}

	private HttpServletRequest _mockHttpServletRequest(Group group) {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

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

	private void _setUpDisplayStyleGroupExternalReferenceCode(
		String externalReferenceCode) {

		Mockito.reset(_siteNavigationBreadcrumbPortletInstanceConfiguration);

		Mockito.when(
			_siteNavigationBreadcrumbPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);
	}

	private void _setUpGroupLocalServiceUtil(Group group) throws Exception {
		_groupLocalServiceUtilMockedStatic.reset();

		Mockito.when(
			GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), 0L)
		).thenReturn(
			group
		);
	}

	private static final MockedStatic<ConfigurationProviderUtil>
		_configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private static final SiteNavigationBreadcrumbPortletInstanceConfiguration
		_siteNavigationBreadcrumbPortletInstanceConfiguration = Mockito.mock(
			SiteNavigationBreadcrumbPortletInstanceConfiguration.class);

}