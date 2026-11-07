/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.search.DepotEntrySearch;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class DepotEntryAdminSearchUtilTest {

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

		Class<?> mvcRenderCommandClass = _mvcRenderCommand.getClass();

		_depotEntryAdminSearchUtilClass = Class.forName(
			"com.liferay.depot.web.internal.util.DepotEntryAdminSearchUtil",
			true, mvcRenderCommandClass.getClassLoader());

		_addDepotEntries();
	}

	@Test
	public void testGetDepotEntrySearch() throws Exception {
		GroupItemSelectorCriterion depotGroupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		depotGroupItemSelectorCriterion.setIncludeAllVisibleGroups(false);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		_assertDepotEntries(
			5,
			ReflectionTestUtil.invoke(
				_depotEntryAdminSearchUtilClass, "getDepotEntrySearch",
				new Class<?>[] {
					DepotEntryService.class, GroupItemSelectorCriterion.class,
					Language.class, PortletRequest.class, PortletResponse.class,
					PortletURL.class
				},
				_depotEntryService, depotGroupItemSelectorCriterion, _language,
				mockLiferayPortletActionRequest,
				new MockLiferayPortletRenderResponse(),
				new MockLiferayPortletURL()));
	}

	@Test
	@TestInfo("LPD-109885")
	public void testGetDepotEntrySearchWithDescriptionKeywords()
		throws Exception {

		String keywords = RandomTestUtil.randomString();

		DepotEntry depotEntry1 = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(),
				keywords + StringPool.SPACE + RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_initialDepotEntries.add(depotEntry1);

		DepotEntry depotEntry2 = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(),
				RandomTestUtil.randomString() + StringPool.SPACE + keywords),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_initialDepotEntries.add(depotEntry2);

		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		groupItemSelectorCriterion.setIncludeAllVisibleGroups(false);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter("keywords", keywords);

		DepotEntrySearch depotEntrySearch = ReflectionTestUtil.invoke(
			_depotEntryAdminSearchUtilClass, "getDepotEntrySearch",
			new Class<?>[] {
				DepotEntryService.class, GroupItemSelectorCriterion.class,
				Language.class, PortletRequest.class, PortletResponse.class,
				PortletURL.class
			},
			_depotEntryService, groupItemSelectorCriterion, _language,
			mockLiferayPortletActionRequest,
			new MockLiferayPortletRenderResponse(),
			new MockLiferayPortletURL());

		Assert.assertEquals(
			SetUtil.fromArray(
				depotEntry1.getDepotEntryId(), depotEntry2.getDepotEntryId()),
			new HashSet<>(
				ListUtil.toList(
					depotEntrySearch.getResults(),
					DepotEntry::getDepotEntryId)));
	}

	@Test
	public void testGetDepotEntrySearchWithViewPermission() throws Exception {
		DepotEntry depotEntry = _initialDepotEntries.get(0);

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				_resourcePermissionLocalService.setResourcePermissions(
					TestPropsValues.getCompanyId(), DepotEntry.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(depotEntry.getDepotEntryId()),
					role.getRoleId(), new String[] {ActionKeys.VIEW});

				GroupItemSelectorCriterion groupItemSelectorCriterion =
					new GroupItemSelectorCriterion();

				groupItemSelectorCriterion.setIncludeAllVisibleGroups(false);

				MockLiferayPortletActionRequest
					mockLiferayPortletActionRequest =
						new MockLiferayPortletActionRequest();

				mockLiferayPortletActionRequest.setAttribute(
					WebKeys.THEME_DISPLAY, _getThemeDisplay());
				mockLiferayPortletActionRequest.setParameter(
					"keywords", _keywords);

				_assertDepotEntries(
					1,
					ReflectionTestUtil.invoke(
						_depotEntryAdminSearchUtilClass, "getDepotEntrySearch",
						new Class<?>[] {
							DepotEntryService.class,
							GroupItemSelectorCriterion.class, Language.class,
							PortletRequest.class, PortletResponse.class,
							PortletURL.class
						},
						_depotEntryService, groupItemSelectorCriterion,
						_language, mockLiferayPortletActionRequest,
						new MockLiferayPortletRenderResponse(),
						new MockLiferayPortletURL()));
			});
	}

	@Test
	public void testGetGroupSearch() throws Exception {
		DepotEntry depotEntry = _initialDepotEntries.get(0);

		List<DepotEntryGroupRel> depotEntryGroupRels =
			_depotEntryGroupRelLocalService.getDepotEntryGroupRels(depotEntry);

		_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRels.get(0));

		_addDepotEntry(false, RandomTestUtil.randomString());

		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		groupItemSelectorCriterion.setIncludeAllVisibleGroups(false);

		_assertGroups(
			_initialDepotEntries.subList(1, 5),
			_getGroupSearch(groupItemSelectorCriterion));
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
				_depotEntryAdminSearchUtilClass, "getDepotEntrySearch",
				new Class<?>[] {
					DepotEntryService.class, GroupItemSelectorCriterion.class,
					Language.class, PortletRequest.class, PortletResponse.class,
					PortletURL.class
				},
				_depotEntryService, groupItemSelectorCriterion, _language,
				mockLiferayPortletActionRequest,
				new MockLiferayPortletRenderResponse(),
				new MockLiferayPortletURL()));
	}

	private void _addDepotEntries() throws Exception {
		_keywords = RandomTestUtil.randomString();

		for (int i = 0; i < 3; i++) {
			_addDepotEntry(true, _keywords + i);
		}

		_addDepotEntry(true, RandomTestUtil.randomString());

		DepotEntry stagedDepotEntry = _addDepotEntry(
			true, RandomTestUtil.randomString());

		GroupTestUtil.enableLocalStaging(stagedDepotEntry.getGroup());
	}

	private DepotEntry _addDepotEntry(boolean connected, String depotEntryName)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(LocaleUtil.getDefault(), depotEntryName),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_initialDepotEntries.add(depotEntry);

		if (connected) {
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group.getGroupId());
		}

		return depotEntry;
	}

	private void _assertDepotEntries(
		int total, DepotEntrySearch depotEntrySearch) {

		Assert.assertEquals(total, depotEntrySearch.getTotal());

		List<DepotEntry> curDepotEntries = depotEntrySearch.getResults();

		for (int i = 0; i < total; i++) {
			DepotEntry depotEntry1 = _initialDepotEntries.get(i);

			DepotEntry depotEntry2 = curDepotEntries.get(i);

			Assert.assertEquals(
				depotEntry1.getDepotEntryId(), depotEntry2.getDepotEntryId());
		}
	}

	private void _assertGroups(
		List<DepotEntry> depotEntries, GroupSearch groupSearch) {

		Assert.assertEquals(depotEntries.size(), groupSearch.getTotal());
		Assert.assertEquals(
			ListUtil.toList(depotEntries, DepotEntry::getGroupId),
			ListUtil.toList(groupSearch.getResults(), Group.GROUP_ID_ACCESSOR));
	}

	private GroupSearch _getGroupSearch(
			GroupItemSelectorCriterion groupItemSelectorCriterion)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return ReflectionTestUtil.invoke(
			_depotEntryAdminSearchUtilClass, "getGroupSearch",
			new Class<?>[] {
				DepotEntryService.class, GroupItemSelectorCriterion.class,
				Language.class, PortletRequest.class, PortletResponse.class,
				PortletURL.class
			},
			_depotEntryService, groupItemSelectorCriterion, _language,
			mockLiferayPortletActionRequest,
			new MockLiferayPortletRenderResponse(),
			new MockLiferayPortletURL());
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

	private Class<?> _depotEntryAdminSearchUtilClass;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DepotEntryService _depotEntryService;

	private Group _group;

	@DeleteAfterTestRun
	private final List<DepotEntry> _initialDepotEntries = new ArrayList<>();

	private String _keywords;

	@Inject
	private Language _language;

	@Inject(
		filter = "component.name=com.liferay.depot.web.internal.portlet.action.ViewMVCRenderCommand"
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}