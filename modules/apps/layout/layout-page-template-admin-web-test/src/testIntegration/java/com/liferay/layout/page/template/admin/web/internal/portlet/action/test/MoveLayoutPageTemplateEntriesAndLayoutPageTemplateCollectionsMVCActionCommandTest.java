/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Barbara Cabrera
 */
@RunWith(Arquillian.class)
@Sync
public class
	MoveLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = _getServiceContext(
			_group, TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testMoveLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommand()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection1 =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, StringUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);
		LayoutPageTemplateCollection layoutPageTemplateCollection2 =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, StringUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry1 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, StringUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);
		LayoutPageTemplateEntry layoutPageTemplateEntry2 =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, StringUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		LayoutPageTemplateCollection layoutParentPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, StringUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				new long[] {
					layoutPageTemplateCollection1.
						getLayoutPageTemplateCollectionId(),
					layoutPageTemplateCollection2.
						getLayoutPageTemplateCollectionId()
				},
				new long[] {
					layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
					layoutPageTemplateEntry2.getLayoutPageTemplateEntryId()
				},
				layoutParentPageTemplateCollection.
					getLayoutPageTemplateCollectionId()),
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					_group.getGroupId(),
					layoutPageTemplateCollection1.getName(),
					layoutParentPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE));
		Assert.assertNotNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					_group.getGroupId(),
					layoutPageTemplateCollection2.getName(),
					layoutParentPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE));
		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				layoutParentPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateEntry1.getName(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE));
		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				layoutParentPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateEntry2.getName(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long[] layoutPageTemplateCollectionsIds,
			long[] layoutPageTemplateEntriesIds,
			long layoutParentPageTemplateCollectionId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"layoutPageTemplateCollectionsIds",
			ArrayUtil.toStringArray(layoutPageTemplateCollectionsIds));
		mockLiferayPortletActionRequest.setParameter(
			"layoutPageTemplateEntriesIds",
			ArrayUtil.toStringArray(layoutPageTemplateEntriesIds));
		mockLiferayPortletActionRequest.setParameter(
			"layoutParentPageTemplateCollectionId",
			String.valueOf(layoutParentPageTemplateCollectionId));
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private ServiceContext _getServiceContext(Group group, long userId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group, userId);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Layout controlPanelLayout = _layoutLocalService.getLayout(
			_portal.getControlPanelPlid(TestPropsValues.getCompanyId()));

		themeDisplay.setLayout(controlPanelLayout);

		themeDisplay.setLayoutSet(_group.getPublicLayoutSet());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/move_layout_page_template_entries_and_layout_page_template_collections"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}