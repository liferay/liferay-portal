/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletCategoryKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Gabriel Lima
 */
public class BasePanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	@Test
	public void testGetGroup() throws Exception {
		_testGetGroupWithNonsiteAdministrationPortlet();
		_testGetGroupWithNullGroupProvider();
		_testGetGroupWithNullPortlet();
		_testGetGroupWithSiteAdministrationPortlet();
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		TestPanelApp testPanelApp = new TestPanelApp(
			Mockito.mock(Portlet.class));

		Assert.assertTrue(
			ListUtil.isEmpty(
				testPanelApp.getPanelAppNavigationItems(_httpServletRequest)));
	}

	private void _testGetGroupWithNonsiteAdministrationPortlet()
		throws Exception {

		Group controlPanelGroup = Mockito.mock(Group.class);

		Mockito.when(
			_themeDisplay.getControlPanelGroup()
		).thenReturn(
			controlPanelGroup
		);

		Portlet portlet = Mockito.mock(Portlet.class);

		Mockito.when(
			portlet.getControlPanelEntryCategory()
		).thenReturn(
			PortletCategoryKeys.PORTLET
		);

		TestPanelApp testPanelApp = new TestPanelApp(portlet);

		Assert.assertEquals(
			controlPanelGroup, testPanelApp.getGroup(_httpServletRequest));
	}

	private void _testGetGroupWithNullGroupProvider() throws Exception {
		Group controlPanelGroup = Mockito.mock(Group.class);

		Mockito.when(
			controlPanelGroup.isControlPanel()
		).thenReturn(
			true
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			controlPanelGroup
		);

		Portlet portlet = Mockito.mock(Portlet.class);

		Mockito.when(
			portlet.getControlPanelEntryCategory()
		).thenReturn(
			PortletCategoryKeys.SITE_ADMINISTRATION_CONTENT
		);

		TestPanelApp testPanelApp = new TestPanelApp(portlet);

		Assert.assertNull(testPanelApp.getGroup(_httpServletRequest));
	}

	private void _testGetGroupWithNullPortlet() throws Exception {
		Group controlPanelGroup = Mockito.mock(Group.class);

		Mockito.when(
			_themeDisplay.getControlPanelGroup()
		).thenReturn(
			controlPanelGroup
		);

		TestPanelApp testPanelApp = new TestPanelApp(null);

		Assert.assertEquals(
			controlPanelGroup, testPanelApp.getGroup(_httpServletRequest));
	}

	private void _testGetGroupWithSiteAdministrationPortlet() throws Exception {
		Group controlPanelGroup = Mockito.mock(Group.class);

		Mockito.when(
			controlPanelGroup.isControlPanel()
		).thenReturn(
			true
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			controlPanelGroup
		);

		Portlet portlet = Mockito.mock(Portlet.class);

		Mockito.when(
			portlet.getControlPanelEntryCategory()
		).thenReturn(
			PortletCategoryKeys.SITE_ADMINISTRATION_CONTENT
		);

		TestPanelApp testPanelApp = new TestPanelApp(portlet);

		GroupProvider groupProvider = Mockito.mock(GroupProvider.class);

		Group siteGroup = Mockito.mock(Group.class);

		Mockito.when(
			groupProvider.getGroup(_httpServletRequest)
		).thenReturn(
			siteGroup
		);

		testPanelApp.setGroupProvider(groupProvider);

		Assert.assertEquals(
			siteGroup, testPanelApp.getGroup(_httpServletRequest));
	}

	@Mock
	private HttpServletRequest _httpServletRequest;

	@Mock
	private ThemeDisplay _themeDisplay;

	private static class TestPanelApp extends BasePanelApp {

		public TestPanelApp(Portlet portlet) {
			_portlet = portlet;
		}

		@Override
		public Portlet getPortlet() {
			return _portlet;
		}

		@Override
		public String getPortletId() {
			return RandomTestUtil.randomString();
		}

		private final Portlet _portlet;

	}

}