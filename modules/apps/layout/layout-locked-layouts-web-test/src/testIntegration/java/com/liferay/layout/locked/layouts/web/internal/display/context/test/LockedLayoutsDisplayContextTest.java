/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.locked.layouts.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.model.LockedLayout;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.lock.model.Lock;
import com.liferay.portal.lock.service.LockLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class LockedLayoutsDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());
	}

	@Test
	public void testGetSearchContainerLockedLayoutsFilterByContentPage()
		throws Exception {

		Layout draftLayout = _getDraftLayout();

		_lockLayout(draftLayout, _user);

		_assertSearchContainerLayoutPlids(
			1, new long[] {draftLayout.getPlid()},
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("content-page")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsFilterByContentPageTemplate()
		throws Exception {

		Layout draftLayout = _getDraftLayoutPageTemplateEntry(
			LayoutPageTemplateEntryTypeConstants.BASIC);

		_lockLayout(draftLayout, _user);

		_lockLayout(_getDraftLayout(), _user);

		_assertSearchContainerLayoutPlids(
			1, new long[] {draftLayout.getPlid()},
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("content-page-template")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsFilterByDisplayPageTemplate()
		throws Exception {

		Layout draftLayout = _getDraftLayoutPageTemplateEntry(
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

		_lockLayout(draftLayout, _user);

		_lockLayout(_getDraftLayout(), _user);

		_assertSearchContainerLayoutPlids(
			1, new long[] {draftLayout.getPlid()},
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("display-page-template")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsFilterByMasterPage()
		throws Exception {

		Layout draftLayout = _getDraftLayoutPageTemplateEntry(
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		_lockLayout(draftLayout, _user);

		_lockLayout(_getDraftLayout(), _user);

		_assertSearchContainerLayoutPlids(
			1, new long[] {draftLayout.getPlid()},
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("master-page")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsFilterByUtilityPage()
		throws Exception {

		Layout draftLayout = _getDraftLayoutUtilityPageEntry();

		_lockLayout(draftLayout, _user);

		_lockLayout(_getDraftLayout(), _user);

		_assertSearchContainerLayoutPlids(
			1, new long[] {draftLayout.getPlid()},
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("utility-page")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsOrderByLastAutosaveAscending()
		throws Exception {

		_assertSearchContainerLockedLayoutCreateDates(
			_getLockedLayoutsCreateDates(null),
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("last-autosave", "asc")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsOrderByLastAutosaveDescending()
		throws Exception {

		_assertSearchContainerLockedLayoutCreateDates(
			_getLockedLayoutsCreateDates(Collections::reverse),
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("last-autosave", "desc")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsOrderByNameAscending()
		throws Exception {

		_assertSearchContainerLockedLayoutsNames(
			_getLockedLayoutsNames(
				list -> Collections.sort(list, String.CASE_INSENSITIVE_ORDER)),
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("name", "asc")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsOrderByNameDescending()
		throws Exception {

		_assertSearchContainerLockedLayoutsNames(
			_getLockedLayoutsNames(
				list -> Collections.sort(
					list, String.CASE_INSENSITIVE_ORDER.reversed())),
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("name", "desc")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsOrderByUserAscending()
		throws Exception {

		_assertSearchContainerLockedLayoutsUserNames(
			_getLockedLayoutsUserNames(
				list -> Collections.sort(list, String.CASE_INSENSITIVE_ORDER)),
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("user", "asc")));
	}

	@Test
	public void testGetSearchContainerLockedLayoutsOrderByUserDescending()
		throws Exception {

		_assertSearchContainerLockedLayoutsUserNames(
			_getLockedLayoutsUserNames(
				list -> Collections.sort(
					list, String.CASE_INSENSITIVE_ORDER.reversed())),
			_getSearchContainer(
				_getMockLiferayPortletRenderRequest("user", "desc")));
	}

	private void _assertSearchContainerLayoutPlids(
		int expectedSize, long[] expectedLayoutPlids,
		SearchContainer<LockedLayout> searchContainer) {

		List<LockedLayout> lockedLayouts = searchContainer.getResults();

		Assert.assertEquals(
			lockedLayouts.toString(), expectedSize, searchContainer.getTotal());

		for (LockedLayout lockedLayout : lockedLayouts) {
			Assert.assertTrue(
				ArrayUtil.contains(
					expectedLayoutPlids, lockedLayout.getPlid()));
		}
	}

	private void _assertSearchContainerLockedLayoutCreateDates(
		List<Date> expectedCreateDates,
		SearchContainer<LockedLayout> searchContainer) {

		List<LockedLayout> lockedLayouts = searchContainer.getResults();

		Assert.assertEquals(
			lockedLayouts.toString(), expectedCreateDates.size(),
			searchContainer.getTotal());

		for (int i = 0; i < expectedCreateDates.size(); i++) {
			LockedLayout lockedLayout = lockedLayouts.get(i);

			Assert.assertEquals(
				expectedCreateDates.get(i), lockedLayout.getLastAutoSaveDate());
		}
	}

	private void _assertSearchContainerLockedLayoutsNames(
		List<String> expectedNames,
		SearchContainer<LockedLayout> searchContainer) {

		List<LockedLayout> lockedLayouts = searchContainer.getResults();

		Assert.assertEquals(
			lockedLayouts.toString(), expectedNames.size(),
			searchContainer.getTotal());

		for (int i = 0; i < expectedNames.size(); i++) {
			LockedLayout lockedLayout = lockedLayouts.get(i);

			Assert.assertEquals(expectedNames.get(i), lockedLayout.getName());
		}
	}

	private void _assertSearchContainerLockedLayoutsUserNames(
		List<String> expectedUserNames,
		SearchContainer<LockedLayout> searchContainer) {

		List<LockedLayout> lockedLayouts = searchContainer.getResults();

		Assert.assertEquals(
			lockedLayouts.toString(), expectedUserNames.size(),
			searchContainer.getTotal());

		for (int i = 0; i < expectedUserNames.size(); i++) {
			LockedLayout lockedLayout = lockedLayouts.get(i);

			Assert.assertEquals(
				expectedUserNames.get(i), lockedLayout.getUserName());
		}
	}

	private Layout _getDraftLayout() throws Exception {
		return _getDraftLayout(LayoutConstants.TYPE_CONTENT);
	}

	private Layout _getDraftLayout(String type) throws Exception {
		if (Objects.equals(LayoutConstants.TYPE_ASSET_DISPLAY, type) ||
			Objects.equals(LayoutConstants.TYPE_UTILITY, type)) {

			_serviceContext.setAttribute(
				"layout.instanceable.allowed", Boolean.TRUE);
		}

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(), type,
			UnicodePropertiesBuilder.put(
				LayoutTypeSettingsConstants.KEY_PUBLISHED, "true"
			).buildString(),
			false, false, Collections.emptyMap(), 0, _serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout.setStatus(WorkflowConstants.STATUS_DRAFT);

		return _layoutLocalService.updateLayout(draftLayout);
	}

	private Layout _getDraftLayoutPageTemplateEntry(
			int layoutPageTemplateEntryType)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(), layoutPageTemplateEntryType,
				WorkflowConstants.STATUS_APPROVED);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout.setStatus(WorkflowConstants.STATUS_DRAFT);

		return _layoutLocalService.updateLayout(draftLayout);
	}

	private Layout _getDraftLayoutUtilityPageEntry() throws Exception {
		Layout draftLayout = _getDraftLayout(LayoutConstants.TYPE_UTILITY);

		_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
			null, _serviceContext.getUserId(),
			_serviceContext.getScopeGroupId(), draftLayout.getClassPK(), 0,
			false, RandomTestUtil.randomString(),
			LayoutUtilityPageEntryConstants.TYPE_SC_INTERNAL_SERVER_ERROR, 0,
			_serviceContext);

		return draftLayout;
	}

	private List<Date> _getLockedLayoutsCreateDates(
			UnsafeConsumer<List<Date>, Exception>
				createDatesOrderUnsafeConsumer)
		throws Exception {

		List<Date> createDates = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			Layout draftLayout = _getDraftLayout();

			_lockLayout(draftLayout, _user);

			Lock lock = _lockLocalService.getLock(
				Layout.class.getName(), draftLayout.getPlid());

			createDates.add(lock.getCreateDate());
		}

		if (createDatesOrderUnsafeConsumer != null) {
			createDatesOrderUnsafeConsumer.accept(createDates);
		}

		return createDates;
	}

	private List<String> _getLockedLayoutsNames(
			UnsafeConsumer<List<String>, Exception> namesOrderUnsafeConsumer)
		throws Exception {

		List<String> names = new ArrayList<>();

		Locale locale = LocaleUtil.getDefault();

		for (int i = 0; i < 5; i++) {
			Layout draftLayout = _getDraftLayout();

			_lockLayout(draftLayout, _user);

			names.add(draftLayout.getName(locale.getLanguage()));
		}

		namesOrderUnsafeConsumer.accept(names);

		return names;
	}

	private List<String> _getLockedLayoutsUserNames(
			UnsafeConsumer<List<String>, Exception>
				userNamesOrderUnsafeConsumer)
		throws Exception {

		List<String> userNames = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			Layout draftLayout = _getDraftLayout();

			User user = UserTestUtil.addUser();

			_lockLayout(draftLayout, user);

			userNames.add(user.getFullName());
		}

		userNamesOrderUnsafeConsumer.accept(userNames);

		return userNames;
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _group.getCompanyId());

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletRenderRequest;
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			String type)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter("type", type);

		return mockLiferayPortletRenderRequest;
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			String orderByCol, String orderByType)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter("orderByCol", orderByCol);
		mockLiferayPortletRenderRequest.setParameter(
			"orderByType", orderByType);

		return mockLiferayPortletRenderRequest;
	}

	private SearchContainer<LockedLayout> _getSearchContainer(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.layout.locked.layouts.web.internal.display." +
					"context.LockedLayoutsDisplayContext"),
			"getSearchContainer", new Class<?>[0]);
	}

	private void _lockLayout(Layout layout, User user) throws Exception {
		MockActionRequest mockActionRequest = new MockActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(layout);
		themeDisplay.setUser(user);

		mockActionRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		_layoutLockManager.getLock(mockActionRequest);

		Lock lock = _lockLocalService.fetchLock(
			Layout.class.getName(), layout.getPlid());

		Assert.assertNotNull(lock);
		Assert.assertEquals(user.getUserId(), lock.getUserId());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private LockLocalService _lockLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.locked.layouts.web.internal.portlet.LockedLayoutsPortlet"
	)
	private Portlet _portlet;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}