/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

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
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class DepotAdminGroupSearchProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_depotAdminGroupSearchProvider = _getDepotAdminGroupSearchProvider();

		_addDepotEntries();
	}

	@Test
	public void testGetGroupSearch() throws Exception {
		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		groupItemSelectorCriterion.setIncludeAllVisibleGroups(false);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		_assertDepotEntries(
			4,
			ReflectionTestUtil.invoke(
				_depotAdminGroupSearchProvider, "getGroupSearch",
				new Class<?>[] {
					GroupItemSelectorCriterion.class, PortletRequest.class,
					PortletURL.class
				},
				groupItemSelectorCriterion, mockLiferayPortletActionRequest,
				new MockLiferayPortletURL()));
	}

	@Test
	public void testGetGroupSearchWithKeywords() throws Exception {
		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		groupItemSelectorCriterion.setIncludeAllVisibleGroups(false);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter("keywords", _keywords);

		_assertDepotEntries(
			3,
			ReflectionTestUtil.invoke(
				_depotAdminGroupSearchProvider, "getGroupSearch",
				new Class<?>[] {
					GroupItemSelectorCriterion.class, PortletRequest.class,
					PortletURL.class
				},
				groupItemSelectorCriterion, mockLiferayPortletActionRequest,
				new MockLiferayPortletURL()));
	}

	private void _addDepotEntries() throws Exception {
		_keywords = RandomTestUtil.randomString();

		for (int i = 0; i < 3; i++) {
			_addDepotEntry(_keywords + i);
		}

		_addDepotEntry(RandomTestUtil.randomString());
	}

	private void _addDepotEntry(String depotEntryName) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(LocaleUtil.getDefault(), depotEntryName),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());
	}

	private void _assertDepotEntries(int total, GroupSearch groupSearch)
		throws Exception {

		Assert.assertEquals(total, groupSearch.getTotal());

		List<Group> groups = groupSearch.getResults();

		for (int i = 0; i < total; i++) {
			Group group = groups.get(i);

			DepotEntry depotEntry = _depotEntries.get(i);

			Group depotEntryGroup = depotEntry.getGroup();

			Assert.assertEquals(
				depotEntryGroup.getName(LocaleUtil.getDefault()),
				group.getName(LocaleUtil.getDefault()));
		}
	}

	private Object _getDepotAdminGroupSearchProvider() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(_getMockHttpServletRequest());

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			"com.liferay.depot.web.internal.util." +
				"DepotAdminGroupSearchProvider");
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Object _depotAdminGroupSearchProvider;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;
	private String _keywords;

	@Inject(
		filter = "component.name=com.liferay.depot.web.internal.portlet.action.ViewMVCRenderCommand"
	)
	private MVCRenderCommand _mvcRenderCommand;

}