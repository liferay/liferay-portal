/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Yang Cao
 */
@RunWith(Arquillian.class)
public class JournalArticleInfoItemProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext();
	}

	@Test
	public void testGetInfoItemFromJournalInfoItemProvider() throws Exception {
		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalArticle updatedArticle = JournalTestUtil.updateArticle(
			article, RandomTestUtil.randomString(), article.getContent(), false,
			false, _serviceContext);

		InfoItemIdentifier infoItemIdentifier = new ClassPKInfoItemIdentifier(
			article.getResourcePrimKey());

		InfoItemObjectProvider<JournalArticle> journalArticleInfoItemProvider =
			(InfoItemObjectProvider<JournalArticle>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					JournalArticle.class.getName(),
					infoItemIdentifier.getInfoItemServiceFilter());

		JournalArticle publishedArticle =
			journalArticleInfoItemProvider.getInfoItem(infoItemIdentifier);

		Assert.assertEquals(article.getTitle(), publishedArticle.getTitle());

		JournalArticle draftArticle =
			journalArticleInfoItemProvider.getInfoItem(
				new ClassPKInfoItemIdentifier(
					article.getResourcePrimKey(),
					InfoItemIdentifier.VERSION_LATEST));

		Assert.assertEquals(updatedArticle.getTitle(), draftArticle.getTitle());
	}

	@Test(expected = NoSuchInfoItemException.class)
	public void testGetInvalidInfoItemFromJournalInfoItemProvider()
		throws Exception {

		InfoItemIdentifier infoItemIdentifier = new ClassPKInfoItemIdentifier(
			RandomTestUtil.randomLong());

		InfoItemObjectProvider<JournalArticle> journalArticleInfoItemProvider =
			(InfoItemObjectProvider<JournalArticle>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					JournalArticle.class.getName(),
					infoItemIdentifier.getInfoItemServiceFilter());

		journalArticleInfoItemProvider.getInfoItem(infoItemIdentifier);
	}

	@Test
	public void testGetPendingArticleJournalInfoItemProviderWithSignedInUser()
		throws Exception {

		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticleLocalServiceUtil.updateStatus(
			article.getUserId(), article.getId(),
			WorkflowConstants.STATUS_PENDING, null, _serviceContext);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			InfoItemIdentifier infoItemIdentifier =
				new ClassPKInfoItemIdentifier(article.getResourcePrimKey());

			InfoItemObjectProvider<JournalArticle>
				journalArticleInfoItemProvider =
					(InfoItemObjectProvider<JournalArticle>)
						_infoItemServiceRegistry.getFirstInfoItemService(
							InfoItemObjectProvider.class,
							JournalArticle.class.getName(),
							infoItemIdentifier.getInfoItemServiceFilter());

			Assert.assertNotNull(
				journalArticleInfoItemProvider.getInfoItem(infoItemIdentifier));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetPendingArticleJournalInfoItemProviderWithSignedOutUser()
		throws Exception {

		JournalArticle approvedArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticleLocalServiceUtil.updateStatus(
			approvedArticle.getUserId(), approvedArticle.getId(),
			WorkflowConstants.STATUS_PENDING, null, _serviceContext);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(
					UserLocalServiceUtil.getGuestUser(
						approvedArticle.getCompanyId())));

			InfoItemIdentifier infoItemIdentifier =
				new ClassPKInfoItemIdentifier(
					approvedArticle.getResourcePrimKey());

			InfoItemObjectProvider<JournalArticle>
				journalArticleInfoItemProvider =
					(InfoItemObjectProvider<JournalArticle>)
						_infoItemServiceRegistry.getFirstInfoItemService(
							InfoItemObjectProvider.class,
							JournalArticle.class.getName(),
							infoItemIdentifier.getInfoItemServiceFilter());

			Assert.assertEquals(
				approvedArticle,
				journalArticleInfoItemProvider.getInfoItem(infoItemIdentifier));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetScheduledArticleJournalInfoItemProvider()
		throws Exception {

		LocalDateTime localDateTime = LocalDateTime.now();

		localDateTime = localDateTime.plusDays(1);

		ZonedDateTime zonedDateTime = localDateTime.atZone(
			ZoneId.systemDefault());

		Date displayDate = Date.from(zonedDateTime.toInstant());

		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), null,
			LocaleUtil.getSiteDefault(), displayDate, null, true, true,
			_serviceContext);

		InfoItemIdentifier infoItemIdentifier = new ClassPKInfoItemIdentifier(
			article.getResourcePrimKey());

		InfoItemObjectProvider<JournalArticle> journalArticleInfoItemProvider =
			(InfoItemObjectProvider<JournalArticle>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					JournalArticle.class.getName(),
					infoItemIdentifier.getInfoItemServiceFilter());

		Assert.assertNotNull(
			journalArticleInfoItemProvider.getInfoItem(infoItemIdentifier));
	}

	@Test
	public void testGetTrashedArticleJournalInfoItemProvider()
		throws Exception {

		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticleLocalServiceUtil.moveArticleToTrash(
			article.getUserId(), article);

		InfoItemIdentifier infoItemIdentifier = new ClassPKInfoItemIdentifier(
			article.getResourcePrimKey());

		InfoItemObjectProvider<JournalArticle> journalArticleInfoItemProvider =
			(InfoItemObjectProvider<JournalArticle>)
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					JournalArticle.class.getName(),
					infoItemIdentifier.getInfoItemServiceFilter());

		Assert.assertNull(
			journalArticleInfoItemProvider.getInfoItem(infoItemIdentifier));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private ServiceContext _serviceContext;

}