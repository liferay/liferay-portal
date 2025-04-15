/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.sort.display.context.builder;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.search.web.internal.sort.configuration.SortPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.sort.display.context.SortDisplayContext;
import com.liferay.portal.search.web.internal.sort.portlet.SortPortletPreferencesImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Joshua Cords
 */
public class SortDisplayContextBuilderTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDisplayStyleGroup() throws Exception {
		_setUpGroupLocalServiceUtil(_getGroup());
		_setUpPortletDisplayStyleGroupExternalReferenceCode(null);

		_assertDisplayContext(_getGroup());

		_groupLocalServiceUtilMockedStatic.verifyNoInteractions();
	}

	@Test
	public void testGetDisplayStyleGroupWithConfiguration() throws Exception {
		Group group = _getGroup();

		_setUpGroupLocalServiceUtil(group);
		_setUpPortletDisplayStyleGroupExternalReferenceCode(
			group.getExternalReferenceCode());

		_assertDisplayContext(group);

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), 0L),
			Mockito.times(1));
	}

	private void _assertDisplayContext(Group group) throws Exception {
		SortDisplayContextBuilder sortDisplayContextBuilder =
			new SortDisplayContextBuilder(
				new LanguageImpl(), _portal, _getRenderRequest(group),
				new SortPortletPreferencesImpl(null));

		SortDisplayContext sortDisplayContext =
			sortDisplayContextBuilder.build();

		Assert.assertEquals(
			group.getGroupId(), sortDisplayContext.getDisplayStyleGroupId());
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

	private PortletDisplay _getPortletDisplay() {
		PortletDisplay portletDisplay = Mockito.mock(PortletDisplay.class);

		Mockito.when(
			portletDisplay.getPortletResource()
		).thenReturn(
			"test"
		);

		return portletDisplay;
	}

	private RenderRequest _getRenderRequest(Group group) {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.doReturn(
			_getThemeDisplay(group)
		).when(
			renderRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);

		return renderRequest;
	}

	private ThemeDisplay _getThemeDisplay(Group group) {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			LocaleUtil.getDefault()
		).when(
			themeDisplay
		).getLocale();

		Mockito.doReturn(
			_getPortletDisplay()
		).when(
			themeDisplay
		).getPortletDisplay();

		if (group != null) {
			Mockito.doReturn(
				group
			).when(
				themeDisplay
			).getScopeGroup();
		}

		return themeDisplay;
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

	private void _setUpPortletDisplayStyleGroupExternalReferenceCode(
		String externalReferenceCode) {

		SortPortletInstanceConfiguration sortPortletInstanceConfiguration =
			Mockito.mock(SortPortletInstanceConfiguration.class);

		Mockito.when(
			sortPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		_configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			sortPortletInstanceConfiguration
		);
	}

	private static final MockedStatic<ConfigurationProviderUtil>
		_configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private final Portal _portal = Mockito.mock(Portal.class);

}