/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class DeleteArticlesMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDoProcessAction() throws Exception {
		JournalArticle articleVersion1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticle articleVersion2 = JournalTestUtil.updateArticle(
			articleVersion1, RandomTestUtil.randomString(),
			articleVersion1.getContent(), true, false,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			"articleUrl", articleVersion1.getUrlTitle());
		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(articleVersion1.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"rowIds",
			new String[] {
				articleVersion1.getArticleId() + "_version_" +
					articleVersion1.getVersion()
			});

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		JournalArticle persistedJournalArticle =
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), articleVersion1.getArticleId(),
				articleVersion1.getVersion());

		Assert.assertNull(persistedJournalArticle);

		persistedJournalArticle = _journalArticleLocalService.getArticle(
			_group.getGroupId(), articleVersion1.getArticleId());

		Assert.assertFalse(persistedJournalArticle.isInTrash());

		mockLiferayPortletActionRequest.setParameter(
			"articleUrl", articleVersion2.getUrlTitle());
		mockLiferayPortletActionRequest.setParameter(
			"rowIds",
			new String[] {
				articleVersion2.getArticleId() + "_version_" +
					articleVersion2.getVersion()
			});

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		persistedJournalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				articleVersion2.getResourcePrimKey(),
				WorkflowConstants.STATUS_IN_TRASH);

		Assert.assertNotNull(persistedJournalArticle);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject(filter = "mvc.command.name=/journal/delete_articles")
	private MVCActionCommand _mvcActionCommand;

}