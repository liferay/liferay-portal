/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPageTemplatesAdminDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpLanguageUtil();
		_setUpPortalUtil();
		_setUpPortletURLBuilder();
		_setUpThemeDisplay();
	}

	@After
	public void tearDown() {
		_portletURLBuilderMockedStatic.close();
		_stagingGroupHelperUtilMockedStatic.close();
	}

	@Test
	public void testGetNavigationItems() {
		_setUpGroup(false);
		_setUpStagingGroupHelper(false, false);

		LayoutPageTemplatesAdminDisplayContext
			layoutPageTemplatesAdminDisplayContext =
				new LayoutPageTemplatesAdminDisplayContext(
					_liferayPortletRequest, _liferayPortletResponse);

		List<NavigationItem> navigationItems =
			layoutPageTemplatesAdminDisplayContext.getNavigationItems();

		Assert.assertEquals(
			navigationItems.toString(), 3, navigationItems.size());

		NavigationItem navigationItem = navigationItems.get(0);

		Assert.assertEquals("Masters", navigationItem.get("label"));

		navigationItem = navigationItems.get(1);

		Assert.assertEquals("Page Templates", navigationItem.get("label"));

		navigationItem = navigationItems.get(2);

		Assert.assertEquals(
			"Display Page Templates", navigationItem.get("label"));
	}

	@Test
	public void testGetNavigationItemsInCompanyGroup() {
		_setUpGroup(true);
		_setUpStagingGroupHelper(false, false);

		LayoutPageTemplatesAdminDisplayContext
			layoutPageTemplatesAdminDisplayContext =
				new LayoutPageTemplatesAdminDisplayContext(
					_liferayPortletRequest, _liferayPortletResponse);

		Assert.assertTrue(
			ListUtil.isEmpty(
				layoutPageTemplatesAdminDisplayContext.getNavigationItems()));
	}

	@Test
	public void testGetNavigationItemsInLocalLiveStagingGroup() {
		_setUpGroup(false);
		_setUpStagingGroupHelper(true, false);

		LayoutPageTemplatesAdminDisplayContext
			layoutPageTemplatesAdminDisplayContext =
				new LayoutPageTemplatesAdminDisplayContext(
					_liferayPortletRequest, _liferayPortletResponse);

		List<NavigationItem> navigationItems =
			layoutPageTemplatesAdminDisplayContext.getNavigationItems();

		Assert.assertEquals(
			navigationItems.toString(), 0, navigationItems.size());
	}

	@Test
	public void testGetNavigationItemsInRemoteLiveStagingGroup() {
		_setUpGroup(false);
		_setUpStagingGroupHelper(false, true);

		LayoutPageTemplatesAdminDisplayContext
			layoutPageTemplatesAdminDisplayContext =
				new LayoutPageTemplatesAdminDisplayContext(
					_liferayPortletRequest, _liferayPortletResponse);

		List<NavigationItem> navigationItems =
			layoutPageTemplatesAdminDisplayContext.getNavigationItems();

		Assert.assertEquals(
			navigationItems.toString(), 0, navigationItems.size());
	}

	private void _setUpGroup(boolean company) {
		Mockito.when(
			_group.isCompany()
		).thenReturn(
			company
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));

		Mockito.when(
			languageUtil.get(_httpServletRequest, "display-page-templates")
		).thenReturn(
			"Display Page Templates"
		);

		Mockito.when(
			languageUtil.get(_httpServletRequest, "masters")
		).thenReturn(
			"Masters"
		);

		Mockito.when(
			languageUtil.get(_httpServletRequest, "page-templates")
		).thenReturn(
			"Page Templates"
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));

		Mockito.when(
			portalUtil.getHttpServletRequest(_liferayPortletRequest)
		).thenReturn(
			_httpServletRequest
		);
	}

	private void _setUpPortletURLBuilder() {
		Mockito.when(
			PortletURLBuilder.createRenderURL(_liferayPortletResponse)
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);
	}

	private void _setUpStagingGroupHelper(
		boolean localLiveGroup, boolean removeLiveGroup) {

		StagingGroupHelper stagingGroupHelper = Mockito.mock(
			StagingGroupHelper.class);

		Mockito.when(
			StagingGroupHelperUtil.getStagingGroupHelper()
		).thenReturn(
			stagingGroupHelper
		);

		Mockito.when(
			stagingGroupHelper.isLocalLiveGroup(_group)
		).thenReturn(
			localLiveGroup
		);

		Mockito.when(
			stagingGroupHelper.isRemoteLiveGroup(_group)
		).thenReturn(
			removeLiveGroup
		);
	}

	private void _setUpThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final LiferayPortletRequest _liferayPortletRequest = Mockito.mock(
		LiferayPortletRequest.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final MockedStatic<PortletURLBuilder>
		_portletURLBuilderMockedStatic = Mockito.mockStatic(
			PortletURLBuilder.class);
	private final MockedStatic<StagingGroupHelperUtil>
		_stagingGroupHelperUtilMockedStatic = Mockito.mockStatic(
			StagingGroupHelperUtil.class);

}