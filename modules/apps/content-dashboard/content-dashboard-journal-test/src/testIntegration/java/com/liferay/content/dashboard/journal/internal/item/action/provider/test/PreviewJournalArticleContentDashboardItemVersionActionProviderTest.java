/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.LayoutSetImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class
	PreviewJournalArticleContentDashboardItemVersionActionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);
	}

	@Test
	public void testGetContentDashboardItemAction() throws Exception {
		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(), 0);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(),
					_portal.getClassNameId(JournalArticle.class.getName()),
					journalArticle.getDDMStructureId(), true,
					WorkflowConstants.STATUS_APPROVED);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(_group.getGroupId());

			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				journalArticle.getUserId(), _group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getResourcePrimKey(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_DEFAULT, serviceContext);

			ThemeDisplay themeDisplay = _getThemeDisplay(LocaleUtil.US);

			themeDisplay.setCompany(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()));
			themeDisplay.setLayoutSet(new LayoutSetImpl());

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);
			mockHttpServletRequest.setAttribute(
				"LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER",
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey())));

			themeDisplay.setRequest(mockHttpServletRequest);

			themeDisplay.setURLCurrent("http://localhost:8080/currentURL");

			serviceContext.setRequest(mockHttpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			ContentDashboardItemVersionAction
				contentDashboardItemVersionAction =
					_contentDashboardItemVersionActionProvider.
						getContentDashboardItemVersionAction(
							journalArticle, mockHttpServletRequest);

			String url = contentDashboardItemVersionAction.getURL();

			Assert.assertTrue(url.contains("p_l_mode=preview"));
			Assert.assertTrue(url.contains("version=1.0"));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetContentDashboardItemActionWithoutDisplayPage()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayResourceResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		ContentDashboardItemVersionAction contentDashboardItemVersionAction =
			_contentDashboardItemVersionActionProvider.
				getContentDashboardItemVersionAction(
					journalArticle, mockHttpServletRequest);

		String url = contentDashboardItemVersionAction.getURL();

		Assert.assertTrue(url.contains("preview_article_content.jsp"));
		Assert.assertTrue(
			url.contains("articleId=" + journalArticle.getArticleId()));
		Assert.assertTrue(
			url.contains("groupId=" + journalArticle.getGroupId()));
		Assert.assertTrue(
			url.contains("version=" + journalArticle.getVersion()));
	}

	@Test
	public void testGetContentDashboardItemActionWithoutDisplayPageAndWithoutDDMTemplate()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		_journalArticleLocalService.updateDDMTemplateKey(
			journalArticle.getGroupId(), journalArticle.getClassNameId(),
			journalArticle.getDDMTemplateKey(), null);

		journalArticle = _journalArticleLocalService.getArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		Assert.assertNull(
			_contentDashboardItemVersionActionProvider.
				getContentDashboardItemVersionAction(
					journalArticle, mockHttpServletRequest));
	}

	@Test
	public void testIsShow() throws Exception {
		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(), 0);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(_group.getGroupId());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(),
					_portal.getClassNameId(JournalArticle.class.getName()),
					journalArticle.getDDMStructureId(), true,
					WorkflowConstants.STATUS_APPROVED);

			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				journalArticle.getUserId(), _group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getResourcePrimKey(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_DEFAULT, serviceContext);

			ThemeDisplay themeDisplay = _getThemeDisplay(LocaleUtil.US);

			themeDisplay.setCompany(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()));
			themeDisplay.setLayoutSet(new LayoutSetImpl());

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);
			mockHttpServletRequest.setAttribute(
				"LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER",
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey())));

			themeDisplay.setRequest(mockHttpServletRequest);

			themeDisplay.setURLCurrent("http://localhost:8080/currentURL");

			serviceContext.setRequest(mockHttpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			Assert.assertTrue(
				_contentDashboardItemVersionActionProvider.isShow(
					journalArticle, mockHttpServletRequest));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testIsShowWithoutDisplayPage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		Assert.assertTrue(
			_contentDashboardItemVersionActionProvider.isShow(
				journalArticle, mockHttpServletRequest));
	}

	@Test
	public void testIsShowWithoutDisplayPageAndWithoutDDMTemplate()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		_journalArticleLocalService.updateDDMTemplateKey(
			journalArticle.getGroupId(), journalArticle.getClassNameId(),
			journalArticle.getDDMTemplateKey(), null);

		journalArticle = _journalArticleLocalService.getArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		Assert.assertFalse(
			_contentDashboardItemVersionActionProvider.isShow(
				journalArticle, mockHttpServletRequest));
	}

	@Test
	public void testIsShowWithoutViewPermission() throws Exception {
		User user = UserTestUtil.addUser();

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(), 0);

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			ThemeDisplay themeDisplay = _getThemeDisplay(LocaleUtil.US);

			RoleTestUtil.removeResourcePermission(
				RoleConstants.GUEST, JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalArticle.getResourcePrimKey()),
				ActionKeys.VIEW);

			themeDisplay.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			Assert.assertFalse(
				_contentDashboardItemVersionActionProvider.isShow(
					journalArticle, mockHttpServletRequest));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private ThemeDisplay _getThemeDisplay(Locale locale) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(locale);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.journal.internal.item.action.provider.PreviewJournalArticleContentDashboardItemVersionActionProvider"
	)
	private ContentDashboardItemVersionActionProvider
		_contentDashboardItemVersionActionProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.layout.display.page.JournalArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<JournalArticle>
		_layoutDisplayPageProvider;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}