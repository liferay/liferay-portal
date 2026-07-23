/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
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
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class SpaceDepotEntryBreadcrumbEntryContributorTest {

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

		CMSTestUtil.getOrAddGroup(
			SpaceDepotEntryBreadcrumbEntryContributorTest.class);
	}

	@Test
	public void testContributeBreadcrumbEntries() throws Exception {
		_testContributeBreadcrumbEntriesWithGroupNotSpaceDepotEntry();
		_testContributeBreadcrumbEntriesWithOriginalBreadcrumbEntries();
		_testContributeBreadcrumbEntriesWithSpaceDepotEntry();
	}

	private DepotEntry _addDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _assertBreadcrumbEntryNotBrowsable(
		BreadcrumbEntry breadcrumbEntry) {

		Assert.assertFalse(
			breadcrumbEntry.toString(), breadcrumbEntry.isBrowsable());
		Assert.assertNull(breadcrumbEntry.toString(), breadcrumbEntry.getURL());
	}

	private void _assertSpaceDepotEntryBreadcrumbEntry(
			BreadcrumbEntry breadcrumbEntry, DepotEntry depotEntry,
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		Group group = depotEntry.getGroup();

		Assert.assertEquals(
			breadcrumbEntry.toString(),
			group.getDescriptiveName(_portal.getLocale(mockHttpServletRequest)),
			breadcrumbEntry.getTitle());

		String url = breadcrumbEntry.getURL();

		Assert.assertNotNull(breadcrumbEntry.toString(), url);
		Assert.assertTrue(
			breadcrumbEntry.toString(), url.contains("/cms/e/space/"));
		Assert.assertTrue(
			breadcrumbEntry.toString(),
			url.endsWith("/" + depotEntry.getDepotEntryId()));
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
		themeDisplay.setPathFriendlyURLPublic("/web");
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _testContributeBreadcrumbEntriesWithGroupNotSpaceDepotEntry()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		List<BreadcrumbEntry> breadcrumbEntries =
			BreadcrumbEntryContributorUtil.contribute(
				Collections.emptyList(), _getMockHttpServletRequest(_group));

		Assert.assertEquals(
			breadcrumbEntries.toString(), Collections.emptyList(),
			breadcrumbEntries);
	}

	private void _testContributeBreadcrumbEntriesWithOriginalBreadcrumbEntries()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(depotEntry.getGroup());

		List<BreadcrumbEntry> originalBreadcrumbEntries = List.of(
			_getBreadcrumbEntry(), _getBreadcrumbEntry());

		List<BreadcrumbEntry> breadcrumbEntries =
			BreadcrumbEntryContributorUtil.contribute(
				originalBreadcrumbEntries, mockHttpServletRequest);

		Assert.assertEquals(
			breadcrumbEntries.toString(), originalBreadcrumbEntries.size() + 1,
			breadcrumbEntries.size());

		_assertSpaceDepotEntryBreadcrumbEntry(
			breadcrumbEntries.get(0), depotEntry, mockHttpServletRequest);

		Assert.assertEquals(
			breadcrumbEntries.subList(1, breadcrumbEntries.size()),
			originalBreadcrumbEntries);

		_assertBreadcrumbEntryNotBrowsable(
			breadcrumbEntries.get(breadcrumbEntries.size() - 1));
	}

	private void _testContributeBreadcrumbEntriesWithSpaceDepotEntry()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(depotEntry.getGroup());

		List<BreadcrumbEntry> breadcrumbEntries =
			BreadcrumbEntryContributorUtil.contribute(
				Collections.emptyList(), mockHttpServletRequest);

		Assert.assertEquals(
			breadcrumbEntries.toString(), 2, breadcrumbEntries.size());

		_assertSpaceDepotEntryBreadcrumbEntry(
			breadcrumbEntries.get(0), depotEntry, mockHttpServletRequest);

		BreadcrumbEntry homeBreadcrumbEntry = breadcrumbEntries.get(1);

		Assert.assertEquals(
			homeBreadcrumbEntry.toString(),
			_language.get(mockHttpServletRequest, "home"),
			homeBreadcrumbEntry.getTitle());

		_assertBreadcrumbEntryNotBrowsable(
			breadcrumbEntries.get(breadcrumbEntries.size() - 1));
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