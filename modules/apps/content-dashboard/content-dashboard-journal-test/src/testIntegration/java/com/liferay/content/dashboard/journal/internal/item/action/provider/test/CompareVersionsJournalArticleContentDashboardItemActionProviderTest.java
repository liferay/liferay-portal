/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Stefan Tanasie
 */
@RunWith(Arquillian.class)
public class
	CompareVersionsJournalArticleContentDashboardItemActionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);
	}

	@Test
	public void testGetContentDashboardItemAction() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		JournalTestUtil.updateArticle(journalArticle);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(
				mockHttpServletRequest, LocaleUtil.US,
				TestPropsValues.getUser()));

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(
				mockHttpServletRequest, LocaleUtil.US,
				TestPropsValues.getUser()));

		ContentDashboardItemVersionAction contentDashboardItemVersionAction =
			_contentDashboardItemVersionActionProvider.
				getContentDashboardItemVersionAction(
					journalArticle, mockHttpServletRequest);

		Assert.assertEquals(
			"compare-versions", contentDashboardItemVersionAction.getName());
		Assert.assertNull(contentDashboardItemVersionAction.getIcon());
		Assert.assertEquals(
			_language.get(LocaleUtil.US, "compare-to"),
			contentDashboardItemVersionAction.getLabel(LocaleUtil.US));

		String url = contentDashboardItemVersionAction.getURL();

		Assert.assertNotNull(url);
		Assert.assertTrue(
			url.contains("articleId=" + journalArticle.getArticleId()));
		Assert.assertTrue(url.contains("compare_versions"));
		Assert.assertTrue(
			url.contains("groupId=" + journalArticle.getGroupId()));
		Assert.assertTrue(
			url.contains("sourceVersion=" + journalArticle.getVersion()));

		JournalArticle latestJournalArticle =
			_journalArticleService.getLatestArticle(
				journalArticle.getResourcePrimKey());

		Assert.assertTrue(
			url.contains("targetVersion=" + latestJournalArticle.getVersion()));
	}

	@Test
	public void testIsShow() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		JournalTestUtil.updateArticle(journalArticle);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(
				mockHttpServletRequest, LocaleUtil.US,
				TestPropsValues.getUser()));

		Assert.assertTrue(
			_contentDashboardItemVersionActionProvider.isShow(
				journalArticle, mockHttpServletRequest));
	}

	@Test
	public void testIsShowWithoutPermissions() throws Exception {
		User user = UserTestUtil.addUser();

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(), 0);

			JournalTestUtil.updateArticle(journalArticle);

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY,
				_getThemeDisplay(mockHttpServletRequest, LocaleUtil.US, user));

			Assert.assertFalse(
				_contentDashboardItemVersionActionProvider.isShow(
					journalArticle, mockHttpServletRequest));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testIsShowWithoutVersions() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(
				mockHttpServletRequest, LocaleUtil.US,
				TestPropsValues.getUser()));

		Assert.assertFalse(
			_contentDashboardItemVersionActionProvider.isShow(
				journalArticle, mockHttpServletRequest));
	}

	private ThemeDisplay _getThemeDisplay(
			HttpServletRequest httpServletRequest, Locale locale, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(locale);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.journal.internal.item.action.provider.CompareVersionsJournalArticleContentDashboardItemVersionActionProvider"
	)
	private ContentDashboardItemVersionActionProvider
		_contentDashboardItemVersionActionProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleService _journalArticleService;

	@Inject
	private Language _language;

	@Inject
	private UserLocalService _userLocalService;

}