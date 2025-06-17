/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntryContributorUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class DepotBreadcrumbEntryContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
	}

	@Test
	public void testContributeBreadcrumbEntries() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Group group = depotEntry.getGroup();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(group);

		List<BreadcrumbEntry> breadcrumbEntries =
			BreadcrumbEntryContributorUtil.contribute(
				new ArrayList<>(), mockHttpServletRequest);

		Assert.assertEquals(
			breadcrumbEntries.toString(), 3, breadcrumbEntries.size());

		_assertAssetLibraryBreadcrumbs(
			breadcrumbEntries, group.getDescriptiveName(),
			mockHttpServletRequest);

		BreadcrumbEntry homeBreadcrumbEntry = breadcrumbEntries.get(2);

		Assert.assertEquals(
			homeBreadcrumbEntry.toString(),
			_language.get(mockHttpServletRequest, "home"),
			homeBreadcrumbEntry.getTitle());

		_assertBreadcrumbEntryNotBrowsable(
			breadcrumbEntries.get(breadcrumbEntries.size() - 1));
	}

	@Test
	public void testContributeBreadcrumbEntriesWithGroupNotAssetLibrary()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(_group);

		ArrayList<BreadcrumbEntry> originalBreadcrumbEntries =
			new ArrayList<>();

		List<BreadcrumbEntry> breadcrumbEntries =
			BreadcrumbEntryContributorUtil.contribute(
				originalBreadcrumbEntries, mockHttpServletRequest);

		Assert.assertEquals(
			breadcrumbEntries.toString(), originalBreadcrumbEntries,
			breadcrumbEntries);
	}

	@Test
	public void testContributeBreadcrumbEntriesWithOriginalBreadcrumbEntries()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Group group = depotEntry.getGroup();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(group);

		ArrayList<BreadcrumbEntry> originalBreadcrumbEntries =
			new ArrayList<>();

		originalBreadcrumbEntries.add(_getBreadcrumbEntry());
		originalBreadcrumbEntries.add(_getBreadcrumbEntry());

		List<BreadcrumbEntry> breadcrumbEntries =
			BreadcrumbEntryContributorUtil.contribute(
				originalBreadcrumbEntries, mockHttpServletRequest);

		Assert.assertEquals(
			breadcrumbEntries.toString(), originalBreadcrumbEntries.size() + 2,
			breadcrumbEntries.size());

		_assertAssetLibraryBreadcrumbs(
			breadcrumbEntries,
			group.getDescriptiveName(_portal.getLocale(mockHttpServletRequest)),
			mockHttpServletRequest);

		for (int i = 0; i < originalBreadcrumbEntries.size(); i++) {
			BreadcrumbEntry breadcrumbEntry = breadcrumbEntries.get(i + 2);

			BreadcrumbEntry previousBreadcrumbEntry =
				originalBreadcrumbEntries.get(i);

			Assert.assertEquals(
				breadcrumbEntry.toString(), breadcrumbEntry.getTitle(),
				previousBreadcrumbEntry.getTitle());
		}

		_assertBreadcrumbEntryNotBrowsable(
			breadcrumbEntries.get(breadcrumbEntries.size() - 1));
	}

	private DepotEntry _addDepotEntry(String name, String description)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), description
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _assertAssetLibraryBreadcrumbs(
		List<BreadcrumbEntry> breadcrumbEntries, String depotName,
		MockHttpServletRequest mockHttpServletRequest) {

		BreadcrumbEntry assetLibrariesBreadcrumbEntry = breadcrumbEntries.get(
			0);

		Assert.assertEquals(
			assetLibrariesBreadcrumbEntry.toString(),
			_language.get(mockHttpServletRequest, "category.asset-libraries"),
			assetLibrariesBreadcrumbEntry.getTitle());

		BreadcrumbEntry assetLibraryBreadcrumbEntry = breadcrumbEntries.get(1);

		Assert.assertEquals(
			assetLibraryBreadcrumbEntry.toString(), depotName,
			assetLibraryBreadcrumbEntry.getTitle());
	}

	private void _assertBreadcrumbEntryNotBrowsable(
			BreadcrumbEntry breadcrumbEntry)
		throws Exception {

		Assert.assertFalse(
			breadcrumbEntry.toString(), breadcrumbEntry.isBrowsable());
		Assert.assertNull(breadcrumbEntry.toString(), breadcrumbEntry.getURL());
	}

	private BreadcrumbEntry _getBreadcrumbEntry() {
		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(RandomTestUtil.randomString());
		breadcrumbEntry.setURL(RandomTestUtil.randomString());

		return breadcrumbEntry;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(Group group)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay(group);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockLiferayResourceRequest mockPortletRequest =
			new MockLiferayResourceRequest();

		mockPortletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);
		mockPortletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, mockPortletRequest);

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(Group group) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(group.getDefaultLanguageId());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private Portal _portal;

}