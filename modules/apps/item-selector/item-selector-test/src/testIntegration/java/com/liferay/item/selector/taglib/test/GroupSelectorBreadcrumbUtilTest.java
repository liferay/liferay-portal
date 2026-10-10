/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.item.selector.taglib.GroupSelectorBreadcrumbUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class GroupSelectorBreadcrumbUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-57283"), @FeatureFlag("LPD-81022")}
	)
	@Test
	@TestInfo("LPD-81022")
	public void testGetBreadcrumbEntries() throws Exception {
		_testGetBreadcrumbEntries();
		_testGetBreadcrumbEntriesWithDesignLibraryGroup();
	}

	@FeatureFlag(enable = false, value = "LPD-81022")
	@Test
	@TestInfo("LPD-81022")
	public void testGetBreadcrumbEntriesWhenFeatureFlagIsDisabled()
		throws Exception {

		List<BreadcrumbEntry> breadcrumbEntries = _getBreadcrumbEntries(_group);

		_assertBreadcrumbEntry(
			breadcrumbEntries.get(0), "Sites and Libraries",
			"http//localhost/test?param_groupType=site;" +
				"param_showGroupSelector=true");
		Assert.assertEquals(
			breadcrumbEntries.toString(), 1, breadcrumbEntries.size());
	}

	private void _assertBreadcrumbEntry(
		BreadcrumbEntry breadcrumbEntry, String title, String url) {

		Assert.assertEquals(title, breadcrumbEntry.getTitle());
		Assert.assertEquals(url, breadcrumbEntry.getURL());
	}

	private List<BreadcrumbEntry> _getBreadcrumbEntries(Group group)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return GroupSelectorBreadcrumbUtil.getBreadcrumbEntries(
			group, mockHttpServletRequest,
			new MockLiferayPortletRenderResponse(),
			new MockLiferayPortletURL());
	}

	private void _testGetBreadcrumbEntries() throws Exception {
		List<BreadcrumbEntry> breadcrumbEntries = _getBreadcrumbEntries(_group);

		_assertBreadcrumbEntry(
			breadcrumbEntries.get(0), "Sites and Libraries",
			"http//localhost/test?param_groupType=;" +
				"param_showGroupSelector=true");
		_assertBreadcrumbEntry(
			breadcrumbEntries.get(1), "Sites",
			"http//localhost/test?param_groupType=site;" +
				"param_showGroupSelector=true");
		Assert.assertEquals(
			breadcrumbEntries.toString(), 2, breadcrumbEntries.size());
	}

	private void _testGetBreadcrumbEntriesWithDesignLibraryGroup()
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		List<BreadcrumbEntry> breadcrumbEntries = _getBreadcrumbEntries(
			depotEntry.getGroup());

		_assertBreadcrumbEntry(
			breadcrumbEntries.get(0), "Sites and Libraries",
			"http//localhost/test?param_groupType=;" +
				"param_showGroupSelector=true");
		_assertBreadcrumbEntry(
			breadcrumbEntries.get(1), "Design Libraries",
			"http//localhost/test?param_groupType=design-library;" +
				"param_showGroupSelector=true");
		Assert.assertEquals(
			breadcrumbEntries.toString(), 2, breadcrumbEntries.size());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}