/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.test.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.item.selector.taglib.servlet.taglib.ImageSelectorTag;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class ItemSelectorRepositoryEntryBrowserUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_themeDisplay.setCompany(_company);
		_themeDisplay.setRefererGroupId(_company.getGroupId());
		_themeDisplay.setScopeGroupId(_company.getGroupId());
		_themeDisplay.setSiteGroupId(_company.getGroupId());

		_user = UserTestUtil.addUser();

		_themeDisplay.setUser(_user);
	}

	@Test
	public void testAddPortletBreadcrumbEntries() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(_company.getGroupId());

		DLTestUtil.addDLFileEntry(dlFolder.getFolderId());

		List<BreadcrumbEntry> breadcrumbEntries = _getBreadcrumbEntries(
			dlFolder.getFolderId());

		Assert.assertEquals(
			breadcrumbEntries.toString(), 3, breadcrumbEntries.size());

		BreadcrumbEntry breadcrumbEntry = breadcrumbEntries.get(2);

		Assert.assertEquals(dlFolder.getName(), breadcrumbEntry.getTitle());
	}

	@Test
	public void testCompanyGroupBreadcrumbEntry() throws Exception {
		Group controlPanelGroup = GroupLocalServiceUtil.getGroup(
			_company.getCompanyId(), "Control Panel");

		long originalScopeGroupId = _themeDisplay.getScopeGroupId();

		try {
			_themeDisplay.setScopeGroupId(controlPanelGroup.getGroupId());

			List<BreadcrumbEntry> breadcrumbEntries = _getBreadcrumbEntries(0);

			Group companyGroup = _company.getGroup();

			BreadcrumbEntry breadcrumbEntry = breadcrumbEntries.get(1);

			Assert.assertEquals(
				companyGroup.getName(_themeDisplay.getLanguageId()),
				breadcrumbEntry.getTitle());
		}
		finally {
			_themeDisplay.setScopeGroupId(originalScopeGroupId);
		}
	}

	private List<BreadcrumbEntry> _getBreadcrumbEntries(long folderId)
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, _themeDisplay);

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				new MockLiferayPortletActionRequest();

			mockLiferayPortletActionRequest.setAttribute(
				WebKeys.THEME_DISPLAY, _themeDisplay);

			Bundle bundle = FrameworkUtil.getBundle(ImageSelectorTag.class);

			Class<?> clazz = bundle.loadClass(
				"com.liferay.item.selector.taglib.internal.util." +
					"ItemSelectorRepositoryEntryBrowserUtil");

			ReflectionTestUtil.invoke(
				clazz, "addPortletBreadcrumbEntries",
				new Class<?>[] {
					long.class, String.class, HttpServletRequest.class,
					LiferayPortletRequest.class, LiferayPortletResponse.class,
					PortletURL.class
				},
				folderId, "", mockHttpServletRequest,
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse(),
				new MockLiferayPortletURL());

			return (List<BreadcrumbEntry>)mockHttpServletRequest.getAttribute(
				WebKeys.PORTLET_BREADCRUMBS);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Inject
	private static CompanyLocalService _companyLocalService;

	private Company _company;
	private final ThemeDisplay _themeDisplay = new ThemeDisplay();
	private User _user;

}