/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;

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
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
@Sync
public class
	CopyLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCActionCommandTest {

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

		_serviceContext = _getServiceContext(
			_group, TestPropsValues.getUserId());

		_layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, StringUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testCopyLayoutPageTemplateEntriesAndCollections()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, StringUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					_serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(BlogsEntry.class.getName()), 0, false,
				WorkflowConstants.STATUS_DRAFT);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				new long[] {
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId()
				},
				new long[] {
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				}),
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					_group.getGroupId(),
					_getName(layoutPageTemplateCollection.getName()),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE));
		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				_getName(layoutPageTemplateEntry.getName()),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE));
	}

	@Test
	public void testCopyLayoutPageTemplateEntryMasterLayout() throws Exception {
		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				_getName(_layoutPageTemplateEntry.getName()),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				null,
				new long[] {
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				}),
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				_getName(_layoutPageTemplateEntry.getName()),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));
	}

	@Test
	public void testCopyLayoutPageTemplateEntryRollbackMVCActionCommand()
		throws Exception {

		_layoutLocalService.deleteLayout(_layoutPageTemplateEntry.getPlid());

		long originalLayoutsCount = _layoutLocalService.getLayoutsCount(
			_group.getGroupId());

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				null,
				new long[] {
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				}),
			new MockLiferayPortletActionResponse());

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				_getName(_layoutPageTemplateEntry.getName()),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));

		Assert.assertEquals(
			originalLayoutsCount,
			_layoutLocalService.getLayoutsCount(_group.getGroupId()));
	}

	@Test
	public void testCopyLayoutPageTemplateEntryUniqueNameMVCActionCommand()
		throws Exception {

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				_getName(_layoutPageTemplateEntry.getName()),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));

		ActionRequest actionRequest = _getMockLiferayPortletActionRequest(
			null,
			new long[] {
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
			});

		_mvcActionCommand.processAction(
			actionRequest, new MockLiferayPortletActionResponse());
		_mvcActionCommand.processAction(
			actionRequest, new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				_getName(_layoutPageTemplateEntry.getName()),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));
		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				StringUtil.appendParentheticalSuffix(
					_layoutPageTemplateEntry.getName(),
					LanguageUtil.get(LocaleUtil.getSiteDefault(), "copy") +
						StringPool.SPACE + 1),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long[] layoutPageTemplateCollectionsIds,
			long[] layoutPageTemplateEntriesIds)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"copyPermissions", Boolean.TRUE.toString());

		if (layoutPageTemplateCollectionsIds != null) {
			mockLiferayPortletActionRequest.setParameter(
				"layoutPageTemplateCollectionsIds",
				ArrayUtil.toStringArray(layoutPageTemplateCollectionsIds));
		}

		if (layoutPageTemplateEntriesIds != null) {
			mockLiferayPortletActionRequest.setParameter(
				"layoutPageTemplateEntriesIds",
				ArrayUtil.toStringArray(layoutPageTemplateEntriesIds));
		}

		mockLiferayPortletActionRequest.setParameter(
			"layoutParentPageTemplateCollectionId",
			String.valueOf(
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT));

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private String _getName(String name) {
		return StringUtil.appendParentheticalSuffix(
			name, LanguageUtil.get(_serviceContext.getLocale(), "copy"));
	}

	private ServiceContext _getServiceContext(Group group, long userId)
		throws Exception {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group, userId);

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	private LayoutPageTemplateEntry _layoutPageTemplateEntry;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/copy_layout_page_template_entries_and_layout_page_template_collections"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}