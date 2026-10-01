/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsPortletInstanceConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Selena Aungst
 */
public class SearchResultsPortletDisplayContextTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_configurationProviderUtilMockedStatic.reset();
		_groupLocalServiceUtilMockedStatic.reset();
	}

	@Test
	public void testGetDisplayStyleGroupId() throws Exception {
		_setUpPortletDisplayStyleGroupExternalReferenceCode(null);

		Group scopeGroup = _getGroup();

		_assertDisplayStyleGroupId(scopeGroup, scopeGroup);

		_groupLocalServiceUtilMockedStatic.verifyNoInteractions();
	}

	@Test
	public void testGetDisplayStyleGroupIdWithExternalReferenceCode()
		throws Exception {

		Group expectedGroup = _getGroup();

		_setUpGroupLocalServiceUtil(expectedGroup);

		String externalReferenceCode = expectedGroup.getExternalReferenceCode();

		_setUpPortletDisplayStyleGroupExternalReferenceCode(
			externalReferenceCode);

		_assertDisplayStyleGroupId(expectedGroup, _getGroup());

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				externalReferenceCode, _COMPANY_ID),
			Mockito.times(1));
	}

	@Test
	public void testGetDisplayStyleGroupIdWithUnresolvedExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_setUpPortletDisplayStyleGroupExternalReferenceCode(
			externalReferenceCode);

		Group scopeGroup = _getGroup();

		_assertDisplayStyleGroupId(scopeGroup, scopeGroup);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				externalReferenceCode, _COMPANY_ID),
			Mockito.times(1));
	}

	private void _assertDisplayStyleGroupId(
			Group expectedGroup, Group scopeGroup)
		throws Exception {

		SearchResultsPortletDisplayContext searchResultsPortletDisplayContext =
			new SearchResultsPortletDisplayContext(
				_getHttpServletRequest(scopeGroup));

		Assert.assertEquals(
			expectedGroup.getGroupId(),
			searchResultsPortletDisplayContext.getDisplayStyleGroupId());
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

		return group;
	}

	private HttpServletRequest _getHttpServletRequest(Group scopeGroup) {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.doReturn(
			_getThemeDisplay(scopeGroup)
		).when(
			httpServletRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);

		return httpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(Group scopeGroup) {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			_COMPANY_ID
		).when(
			themeDisplay
		).getCompanyId();

		Mockito.doReturn(
			scopeGroup
		).when(
			themeDisplay
		).getScopeGroup();

		Mockito.doReturn(
			scopeGroup.getGroupId()
		).when(
			themeDisplay
		).getScopeGroupId();

		return themeDisplay;
	}

	private void _setUpGroupLocalServiceUtil(Group group) throws Exception {
		Mockito.when(
			GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), _COMPANY_ID)
		).thenReturn(
			group
		);
	}

	private void _setUpPortletDisplayStyleGroupExternalReferenceCode(
		String externalReferenceCode) {

		SearchResultsPortletInstanceConfiguration
			searchResultsPortletInstanceConfiguration = Mockito.mock(
				SearchResultsPortletInstanceConfiguration.class);

		Mockito.when(
			searchResultsPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		_configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			searchResultsPortletInstanceConfiguration
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<ConfigurationProviderUtil>
		_configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

}