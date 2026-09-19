/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.exception.DuplicateKBArticleExternalReferenceCodeException;
import com.liferay.knowledge.base.exception.KBArticleContentException;
import com.liferay.knowledge.base.exception.KBArticleDisplayDateException;
import com.liferay.knowledge.base.exception.KBArticleExpirationDateException;
import com.liferay.knowledge.base.exception.KBArticleParentException;
import com.liferay.knowledge.base.exception.KBArticleReviewDateException;
import com.liferay.knowledge.base.exception.KBArticleSourceURLException;
import com.liferay.knowledge.base.exception.KBArticleStatusException;
import com.liferay.knowledge.base.exception.KBArticleTitleException;
import com.liferay.knowledge.base.exception.KBArticleUrlTitleException;
import com.liferay.knowledge.base.exception.LockedKBArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBCommentLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;

import java.io.InputStream;

import java.time.Duration;
import java.time.Instant;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class KBArticleLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_kbArticleClassNameId = ClassNameLocalServiceUtil.getClassNameId(
			KBArticleConstants.getClassName());
		_kbFolderClassNameId = ClassNameLocalServiceUtil.getClassNameId(
			KBFolderConstants.getClassName());

		_user = TestPropsValues.getUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, _user.getUserId());
	}

	@Test
	public void testAddApprovedKBArticleInsideApprovedKBArticle()
		throws Exception {

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		KBArticle parentKBArticle = _addKbArticle();

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test(expected = KBArticleStatusException.class)
	public void testAddApprovedKBArticleInsideDraftKBArticle()
		throws Exception {

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		KBArticle parentKBArticle = _addKbArticle();

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test
	public void testAddApprovedKBArticleInsideNonlatestApprovedKBArticle()
		throws Exception {

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		KBArticle parentKBArticle = _addKbArticle();

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		parentKBArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), parentKBArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null,
			parentKBArticle.getDisplayDate(), null, null, null, null,
			_serviceContext);

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test
	public void testAddDraftKBArticleInsideApprovedKBArticle()
		throws Exception {

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		KBArticle parentKBArticle = _addKbArticle();

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test
	public void testAddDraftKBArticleInsideDraftKBArticle() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		KBArticle parentKBArticle = _addKbArticle();

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test
	public void testAddDraftKBArticleUpdatesAssetEntry() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		KBArticle kbArticle = _addKbArticle();

		Assert.assertNotNull(
			_assetEntryLocalService.getEntry(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey()));

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		KBArticle draftKBArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, kbArticle.getDisplayDate(),
			null, null, null, null, _serviceContext);

		Assert.assertNotNull(
			_assetEntryLocalService.getEntry(
				KBArticle.class.getName(), draftKBArticle.getKbArticleId()));

		Assert.assertNotNull(
			_assetEntryLocalService.getEntry(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey()));

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		kbArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, kbArticle.getDisplayDate(),
			null, null, null, null, _serviceContext);

		Assert.assertNull(
			_assetEntryLocalService.fetchEntry(
				KBArticle.class.getName(), draftKBArticle.getKbArticleId()));
		Assert.assertNotNull(
			_assetEntryLocalService.getEntry(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey()));
	}

	@Test(expected = KBArticleDisplayDateException.class)
	public void testAddKBArticleDisplayDateException() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			null, new Date(System.currentTimeMillis() + (1 * Time.DAY)), null,
			null, _serviceContext);
	}

	@Test
	public void testAddKBArticleDisplayDateKBArticleStatusScheduled()
		throws Exception {

		Date displayDate = new Date(
			System.currentTimeMillis() + (2 * Time.DAY));

		Date expirationDate = new Date(displayDate.getTime() + (1 * Time.DAY));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			displayDate, expirationDate, null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, kbArticle.getStatus());
	}

	@Test(expected = KBArticleExpirationDateException.class)
	public void testAddKBArticleInvalidExpirationDateBeforeDisplayDateKBArticleExpirationDateException()
		throws Exception {

		Date displayDate = new Date(
			System.currentTimeMillis() + (2 * Time.DAY));

		Date expirationDate = new Date(displayDate.getTime() - (1 * Time.DAY));

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			displayDate, expirationDate, RandomTestUtil.nextDate(), null,
			_serviceContext);
	}

	@Test(expected = KBArticleExpirationDateException.class)
	public void testAddKBArticleInvalidExpirationDateException()
		throws Exception {

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		Instant instant = Instant.now();

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), Date.from(instant.minus(Duration.ofDays(1))), null,
			null, _serviceContext);
	}

	@Test(expected = KBArticleReviewDateException.class)
	public void testAddKBArticleInvalidReviewDateException() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		Instant instant = Instant.now();

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), null, Date.from(instant.minus(Duration.ofDays(1))),
			null, _serviceContext);
	}

	@Test(expected = KBArticleDisplayDateException.class)
	public void testAddKBArticleShouldFailIfDisplayDateIsNull()
		throws Exception {

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			null, null, null, null, _serviceContext);
	}

	@Test
	public void testAddKBArticleUpdatesExpirationReviewDate() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		Date expirationDate = new Date(
			System.currentTimeMillis() + (1 * Time.DAY));
		Date reviewDate = new Date(System.currentTimeMillis() + (1 * Time.DAY));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), expirationDate, reviewDate, null, _serviceContext);

		Assert.assertEquals(expirationDate, kbArticle.getExpirationDate());
		Assert.assertEquals(reviewDate, kbArticle.getReviewDate());

		expirationDate = new Date(System.currentTimeMillis() + (2 * Time.DAY));
		reviewDate = new Date(System.currentTimeMillis() + (2 * Time.DAY));

		_kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, kbArticle.getDisplayDate(),
			expirationDate, reviewDate, null, null, _serviceContext);

		KBArticle latestKBArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			expirationDate, latestKBArticle.getExpirationDate());
		Assert.assertEquals(reviewDate, latestKBArticle.getReviewDate());
	}

	@Test(expected = KBArticleContentException.class)
	public void testAddKBArticleWithBlankContent() throws Exception {
		String content = StringPool.BLANK;

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(), content,
			StringUtil.randomString(), null, StringUtil.randomString(),
			new Date(), null, null, null, _serviceContext);
	}

	@Test
	public void testAddKBArticleWithBlankSourceURL() throws Exception {
		String sourceURL = StringPool.BLANK;

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			sourceURL, new Date(), null, null, null, _serviceContext);

		Assert.assertTrue(Validator.isNull(kbArticle.getSourceURL()));
	}

	@Test(expected = KBArticleTitleException.class)
	public void testAddKBArticleWithBlankTitle() throws Exception {
		String title = StringPool.BLANK;

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID, title,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, StringUtil.randomString(),
			new Date(), null, null, null, _serviceContext);
	}

	@Test
	public void testAddKBArticleWithBlankURLTitle() throws Exception {
		String urlTitle = StringPool.BLANK;

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), urlTitle, StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		Assert.assertTrue(Validator.isNotNull(kbArticle.getUrlTitle()));
	}

	@Test
	public void testAddKBArticleWithCustomHTML() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.security.antisamy.internal." +
					"AntiSamySanitizerImpl",
				LoggerTestUtil.WARN)) {

			String content =
				"<a href=\"http://www.liferay.com\" target=\"_blank\" />";

			KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
				null, _user.getUserId(), _kbFolderClassNameId,
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				StringUtil.randomString(), StringUtil.randomString(), content,
				StringUtil.randomString(), null, null, new Date(), null, null,
				null, _serviceContext);

			Matcher matcher = _targetBlankPattern.matcher(
				kbArticle.getContent());

			Assert.assertTrue(matcher.matches());
		}
	}

	@Test
	public void testAddKBArticleWithDisplayDateExpirationDateReviewDate()
		throws Exception {

		Date displayDate = new Date();
		Date expirationDate = new Date(
			System.currentTimeMillis() + (2 * Time.MINUTE));
		Date reviewDate = new Date(System.currentTimeMillis() + Time.MINUTE);

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			displayDate, expirationDate, reviewDate, null, _serviceContext);

		Assert.assertEquals(displayDate, kbArticle.getDisplayDate());
		Assert.assertEquals(expirationDate, kbArticle.getExpirationDate());
		Assert.assertEquals(reviewDate, kbArticle.getReviewDate());
	}

	@Test(expected = KBArticleUrlTitleException.class)
	public void testAddKBArticleWithDuplicateURLTitle() throws Exception {
		String urlTitle = StringUtil.randomString();

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), urlTitle, StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), urlTitle, StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test(expected = DuplicateKBArticleExternalReferenceCodeException.class)
	public void testAddKBArticleWithExistingExternalReferenceCode()
		throws Exception {

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			RandomTestUtil.randomString(), _user.getUserId(),
			_kbFolderClassNameId, KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			StringPool.BLANK, new Date(), null, null, null, _serviceContext);

		_kbArticleLocalService.addKBArticle(
			kbArticle.getExternalReferenceCode(), _user.getUserId(),
			_kbFolderClassNameId, KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			StringPool.BLANK, new Date(), null, null, null, _serviceContext);
	}

	@Test
	public void testAddKBArticleWithExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			externalReferenceCode, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			StringPool.BLANK, new Date(), null, null, null, _serviceContext);

		Assert.assertEquals(
			externalReferenceCode, kbArticle.getExternalReferenceCode());
	}

	@Test(expected = KBArticleParentException.class)
	public void testAddKBArticleWithInvalidParentClassNameId()
		throws Exception {

		long invalidParentClassNameId = 123456789L;

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), invalidParentClassNameId,
			RandomTestUtil.nextLong(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test(expected = KBArticleSourceURLException.class)
	public void testAddKBArticleWithInvalidSourceURL() throws Exception {
		String sourceURL = "InvalidURL";

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			sourceURL, new Date(), null, null, null, _serviceContext);
	}

	@Test(expected = KBArticleUrlTitleException.class)
	public void testAddKBArticleWithInvalidURLTitle() throws Exception {
		String invalidURLTitle = "#$%&/(";

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), invalidURLTitle,
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), null, null, null, _serviceContext);
	}

	@Test(expected = KBArticleUrlTitleException.class)
	public void testAddKBArticleWithLargeURLTitle() throws Exception {
		int urlTitleMaxSize = ModelHintsUtil.getMaxLength(
			KBArticle.class.getName(), "urlTitle");

		String invalidURLTitle = StringUtil.randomString(urlTitleMaxSize + 1);

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), invalidURLTitle,
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), null, null, null, _serviceContext);
	}

	@Test(expected = KBArticleContentException.class)
	public void testAddKBArticleWithNullContent() throws Exception {
		String content = null;

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(), content,
			StringUtil.randomString(), null, StringUtil.randomString(),
			new Date(), null, null, null, _serviceContext);
	}

	@Test
	public void testAddKBArticleWithNullSourceURL() throws Exception {
		String sourceURL = null;

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			sourceURL, new Date(), null, null, null, _serviceContext);

		Assert.assertTrue(Validator.isNull(kbArticle.getSourceURL()));
	}

	@Test(expected = KBArticleTitleException.class)
	public void testAddKBArticleWithNullTitle() throws Exception {
		String title = null;

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID, title,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, StringUtil.randomString(),
			new Date(), null, null, null, _serviceContext);
	}

	@Test
	public void testAddKBArticleWithNullURLTitle() throws Exception {
		String urlTitle = null;

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), urlTitle, StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		Assert.assertTrue(Validator.isNotNull(kbArticle.getUrlTitle()));
	}

	@Test
	public void testAddKBArticleWithValidParentKBArticle() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			kbArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test
	public void testAddKBArticleWithValidParentKBFolder() throws Exception {
		KBFolder kbFolder = _kbFolderLocalService.addKBFolder(
			null, _user.getUserId(), _group.getGroupId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			_serviceContext);

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			kbFolder.getPrimaryKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);
	}

	@Test
	public void testAddKBArticleWithValidSourceURL() throws Exception {
		String sourceURL = "http://www.liferay.com";

		_kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			sourceURL, new Date(), null, null, null, _serviceContext);
	}

	@Test
	public void testAddKBArticleWithoutExternalReferenceCode()
		throws Exception {

		KBArticle kbArticle1 = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null,
			StringPool.BLANK, new Date(), null, null, null, _serviceContext);

		String externalReferenceCode = kbArticle1.getExternalReferenceCode();

		Assert.assertEquals(externalReferenceCode, kbArticle1.getUuid());

		KBArticle kbArticle2 =
			_kbArticleLocalService.getLatestKBArticleByExternalReferenceCode(
				_group.getGroupId(), externalReferenceCode);

		Assert.assertEquals(kbArticle1, kbArticle2);
	}

	@Test
	public void testAddKBArticlesMarkdownWithNoWorkflow() throws Exception {
		updateWorkflowDefinitionForKBArticle("");

		importMarkdownArticles();
	}

	@Test
	public void testAddKBArticlesMarkdownWithSingleApproverWorkflow()
		throws Exception {

		updateWorkflowDefinitionForKBArticle("Single Approver@1");

		importMarkdownArticles();
	}

	@Test
	public void testBuildTreePathAfterMoveKBArticle() throws Exception {
		KBFolder kbFolder = _kbFolderLocalService.addKBFolder(
			null, _user.getUserId(), _group.getGroupId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			_serviceContext);

		KBFolder childKBFolder = _kbFolderLocalService.addKBFolder(
			null, _user.getUserId(), _group.getGroupId(), _kbFolderClassNameId,
			kbFolder.getKbFolderId(), StringUtil.randomString(),
			StringUtil.randomString(), _serviceContext);

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			childKBFolder.getKbFolderId(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, StringPool.BLANK, new Date(), null,
			null, null, _serviceContext);

		String originalKBArticleTreePath = kbArticle.buildTreePath();

		_kbArticleLocalService.moveKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			_kbFolderClassNameId, KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			kbArticle.getPriority());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		String newKBArticleTreePath = String.valueOf(CharPool.SLASH);

		Assert.assertEquals(newKBArticleTreePath, kbArticle.buildTreePath());

		_kbArticleLocalService.moveKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			_kbFolderClassNameId, childKBFolder.getKbFolderId(),
			kbArticle.getPriority());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			originalKBArticleTreePath, kbArticle.buildTreePath());
	}

	@Test
	public void testBuildTreePathAfterMoveKBFolder() throws Exception {
		KBFolder kbFolder = _kbFolderLocalService.addKBFolder(
			null, _user.getUserId(), _group.getGroupId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			_serviceContext);

		KBFolder childKBFolder = _kbFolderLocalService.addKBFolder(
			null, _user.getUserId(), _group.getGroupId(), _kbFolderClassNameId,
			kbFolder.getKbFolderId(), StringUtil.randomString(),
			StringUtil.randomString(), _serviceContext);

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			childKBFolder.getKbFolderId(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, StringPool.BLANK, new Date(), null,
			null, null, _serviceContext);

		String originalKBArticleTreePath = kbArticle.buildTreePath();

		_kbFolderLocalService.moveKBFolder(
			childKBFolder.getKbFolderId(),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String newKBArticleTreePath = StringBundler.concat(
			CharPool.SLASH, childKBFolder.getKbFolderId(), CharPool.SLASH);

		Assert.assertEquals(newKBArticleTreePath, kbArticle.buildTreePath());

		_kbFolderLocalService.moveKBFolder(
			childKBFolder.getKbFolderId(), kbFolder.getKbFolderId());
		Assert.assertEquals(
			originalKBArticleTreePath, kbArticle.buildTreePath());
	}

	@Test(expected = KBArticleStatusException.class)
	public void testCheckKBArticlesFailsWhenPublishingAndParentKBArticleIsScheduled()
		throws Exception {

		Date displayDate = new Date(
			System.currentTimeMillis() + (2 * Time.DAY));

		KBArticle parentKBArticle = _addKbArticle(displayDate);

		KBArticle childKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, displayDate, null, null,
			null, _serviceContext);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, parentKBArticle.getStatus());
		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, childKBArticle.getStatus());

		childKBArticle.setDisplayDate(new Date());

		_kbArticleLocalService.updateKBArticle(childKBArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());
	}

	@Test
	public void testCheckKBArticlesWhenChildKBArticleIsPublishedAfterParentKBArticleIsPublished()
		throws Exception {

		Date displayDate = new Date(
			System.currentTimeMillis() + (2 * Time.DAY));

		KBArticle parentKBArticle = _addKbArticle(displayDate);

		KBArticle childKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, displayDate, null, null,
			null, _serviceContext);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		childKBArticle = _kbArticleLocalService.fetchKBArticle(
			childKBArticle.getKbArticleId());
		parentKBArticle = _kbArticleLocalService.fetchKBArticle(
			parentKBArticle.getKbArticleId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, childKBArticle.getStatus());
		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, parentKBArticle.getStatus());

		parentKBArticle.setDisplayDate(new Date());

		parentKBArticle = _kbArticleLocalService.updateKBArticle(
			parentKBArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		childKBArticle = _kbArticleLocalService.fetchKBArticle(
			childKBArticle.getKbArticleId());
		parentKBArticle = _kbArticleLocalService.fetchKBArticle(
			parentKBArticle.getKbArticleId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, childKBArticle.getStatus());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, parentKBArticle.getStatus());

		childKBArticle.setDisplayDate(new Date());

		childKBArticle = _kbArticleLocalService.updateKBArticle(childKBArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		childKBArticle = _kbArticleLocalService.fetchKBArticle(
			childKBArticle.getKbArticleId());
		parentKBArticle = _kbArticleLocalService.fetchKBArticle(
			parentKBArticle.getKbArticleId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, childKBArticle.getStatus());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, parentKBArticle.getStatus());
	}

	@Test
	public void testCheckKBArticlesWhenChildKBArticleIsPublishedOnlyAfterParentKBArticleIsPublished()
		throws Exception {

		Date displayDate = new Date(
			System.currentTimeMillis() + (2 * Time.DAY));

		KBArticle parentKBArticle = _addKbArticle(displayDate);

		KBArticle childKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, displayDate, null, null,
			null, _serviceContext);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		childKBArticle = _kbArticleLocalService.fetchKBArticle(
			childKBArticle.getKbArticleId());
		parentKBArticle = _kbArticleLocalService.fetchKBArticle(
			parentKBArticle.getKbArticleId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, childKBArticle.getStatus());
		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, parentKBArticle.getStatus());

		displayDate = new Date(System.currentTimeMillis() - (2 * Time.MINUTE));

		childKBArticle.setDisplayDate(displayDate);

		childKBArticle = _kbArticleLocalService.updateKBArticle(childKBArticle);

		try {
			_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof KBArticleStatusException);
		}

		childKBArticle = _kbArticleLocalService.fetchKBArticle(
			childKBArticle.getKbArticleId());
		parentKBArticle = _kbArticleLocalService.fetchKBArticle(
			parentKBArticle.getKbArticleId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, childKBArticle.getStatus());
		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, parentKBArticle.getStatus());

		parentKBArticle.setDisplayDate(displayDate);

		parentKBArticle = _kbArticleLocalService.updateKBArticle(
			parentKBArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		childKBArticle = _kbArticleLocalService.fetchKBArticle(
			childKBArticle.getKbArticleId());
		parentKBArticle = _kbArticleLocalService.fetchKBArticle(
			parentKBArticle.getKbArticleId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, childKBArticle.getStatus());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, parentKBArticle.getStatus());
	}

	@Test
	public void testDeleteGroupKBArticlesDeletesKBArticles() throws Exception {
		_addKbArticle();

		Assert.assertEquals(
			1,
			_kbArticleLocalService.getGroupKBArticlesCount(
				_group.getGroupId(), WorkflowConstants.STATUS_ANY));

		_kbArticleLocalService.deleteGroupKBArticles(_group.getGroupId());

		Assert.assertEquals(
			0,
			_kbArticleLocalService.getGroupKBArticlesCount(
				_group.getGroupId(), WorkflowConstants.STATUS_ANY));
	}

	@Test
	public void testDeleteGroupKBArticlesDeletesSubscriptions()
		throws Exception {

		_addKbArticle();

		int subscriptionsCount =
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				_user.getUserId());

		_kbArticleLocalService.subscribeGroupKBArticles(
			_user.getUserId(), _group.getGroupId());

		Assert.assertEquals(
			subscriptionsCount + 1,
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				_user.getUserId()));

		_kbArticleLocalService.deleteGroupKBArticles(_group.getGroupId());

		Assert.assertEquals(
			subscriptionsCount,
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				_user.getUserId()));
	}

	@Test
	public void testDeleteKBArticleDeletesAssetEntry() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		Assert.assertNotNull(
			AssetEntryLocalServiceUtil.fetchEntry(
				KBArticleConstants.getClassName(), kbArticle.getClassPK()));

		_kbArticleLocalService.deleteKBArticle(kbArticle);

		Assert.assertNull(
			AssetEntryLocalServiceUtil.fetchEntry(
				KBArticleConstants.getClassName(), kbArticle.getClassPK()));
	}

	@Test
	public void testDeleteKBArticleDeletesChildKBArticles() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		KBArticle childKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), kbArticle.getClassNameId(),
			kbArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		_kbArticleLocalService.deleteKBArticle(kbArticle);

		Assert.assertNull(
			_kbArticleLocalService.fetchKBArticle(
				childKBArticle.getKbArticleId()));
	}

	@Test
	public void testDeleteKBArticleDeletesKBComments() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		_kbCommentLocalService.addKBComment(
			_user.getUserId(), kbArticle.getClassNameId(),
			kbArticle.getClassPK(), StringUtil.randomString(), _serviceContext);

		_kbArticleLocalService.deleteKBArticle(kbArticle);

		Assert.assertEquals(
			0,
			_kbCommentLocalService.getKBCommentsCount(
				KBArticleConstants.getClassName(), kbArticle.getClassPK()));
	}

	@Test
	public void testDeleteKBArticleDeletesRatings() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		RatingsEntryLocalServiceUtil.updateEntry(
			_user.getUserId(), KBArticleConstants.getClassName(),
			kbArticle.getClassPK(), 1.0, _serviceContext);

		_kbArticleLocalService.deleteKBArticle(kbArticle);

		Assert.assertNull(
			RatingsStatsLocalServiceUtil.fetchStats(
				KBArticleConstants.getClassName(), kbArticle.getClassPK()));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testDeleteKBArticleWithLock() throws PortalException {
		KBArticle kbArticle = _addKbArticle();

		_testKBArticleLock(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.deleteKBArticle(kbArticle));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testDeleteKBArticleWithLockByPreviousUser() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		_testKBArticleWithLockByPreviousUser(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.deleteKBArticle(kbArticle));
	}

	@Test
	public void testDraftKBArticleDoesNotExpire() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		KBArticle kbArticle = _addKbArticle();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, kbArticle.getStatus());

		kbArticle = _kbArticleLocalService.expireKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(), _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, kbArticle.getStatus());
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testExpireKBArticleWithLock() throws PortalException {
		KBArticle kbArticle = _addKbArticle();

		_testKBArticleLock(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.expireKBArticle(
				_user.getUserId(), kbArticle.getResourcePrimKey(),
				_serviceContext));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testExpireKBArticleWithLockByPreviousUser() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		_testKBArticleWithLockByPreviousUser(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.expireKBArticle(
				_user.getUserId(), kbArticle.getResourcePrimKey(),
				_serviceContext));
	}

	@Test
	public void testFetchKBArticleByUrlTitle() throws Exception {
		KBFolder kbFolder = _kbFolderLocalService.addKBFolder(
			null, _user.getUserId(), _group.getGroupId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			_serviceContext);

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			kbFolder.getKbFolderId(), "Article with versions",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		Assert.assertEquals(1, kbArticle.getVersion());

		kbArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, kbArticle.getDisplayDate(),
			null, null, null, null, _serviceContext);

		Assert.assertEquals(2, kbArticle.getVersion());

		kbArticle = _kbArticleLocalService.fetchKBArticleByUrlTitle(
			kbArticle.getGroupId(), kbFolder.getUrlTitle(),
			kbArticle.getUrlTitle());

		Assert.assertEquals(2, kbArticle.getVersion());
	}

	@Test
	public void testFetchPersistedModelByResourcePrimKey() throws Exception {
		KBArticle kBArticle = _addKbArticle();

		PersistedModel persistedModel =
			_kbArticleLocalService.fetchPersistedModel(
				kBArticle.getResourcePrimKey());

		Assert.assertNotNull(persistedModel);
	}

	@Test
	public void testGetAllDescendantKBArticles() throws Exception {
		KBArticle parentKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Parent Article",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle childKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			parentKBArticle.getResourcePrimKey(), "Child Article",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle grandchildKBArticleA = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			childKBArticle.getResourcePrimKey(), "Grandchild Article A",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle greatGrandchildKBArticleA =
			_kbArticleLocalService.addKBArticle(
				null, _user.getUserId(), _kbArticleClassNameId,
				grandchildKBArticleA.getResourcePrimKey(),
				"GreatGrandchild Article A", StringUtil.randomString(),
				StringUtil.randomString(), StringUtil.randomString(), null,
				null, new Date(), null, null, null, _serviceContext);

		KBArticle grandchildKBArticleB = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			childKBArticle.getResourcePrimKey(), "Grandchild Article B",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle greatGrandchildKBArticleB =
			_kbArticleLocalService.addKBArticle(
				null, _user.getUserId(), _kbArticleClassNameId,
				grandchildKBArticleB.getResourcePrimKey(),
				"GreatGrandchild Article B", StringUtil.randomString(),
				StringUtil.randomString(), StringUtil.randomString(), null,
				null, new Date(), null, null, null, _serviceContext);

		List<KBArticle> kbArticleAndAllDescendantKBArticles =
			_kbArticleLocalService.getAllDescendantKBArticles(
				parentKBArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED,
				KBArticlePriorityComparator.getInstance(true));

		Assert.assertEquals(
			kbArticleAndAllDescendantKBArticles.toString(), 5,
			kbArticleAndAllDescendantKBArticles.size());

		KBArticle currentChildKBArticle =
			kbArticleAndAllDescendantKBArticles.get(0);
		KBArticle currentGrandchildKBArticleA =
			kbArticleAndAllDescendantKBArticles.get(1);
		KBArticle currentGreatGrandchildKBArticleA =
			kbArticleAndAllDescendantKBArticles.get(2);
		KBArticle currentGrandchildKBArticleB =
			kbArticleAndAllDescendantKBArticles.get(3);
		KBArticle currentGreatGrandchildKBArticleB =
			kbArticleAndAllDescendantKBArticles.get(4);

		Assert.assertEquals(
			childKBArticle.getResourcePrimKey(),
			currentChildKBArticle.getResourcePrimKey());
		Assert.assertEquals(
			grandchildKBArticleA.getResourcePrimKey(),
			currentGrandchildKBArticleA.getResourcePrimKey());
		Assert.assertEquals(
			greatGrandchildKBArticleA.getResourcePrimKey(),
			currentGreatGrandchildKBArticleA.getResourcePrimKey());
		Assert.assertEquals(
			grandchildKBArticleB.getResourcePrimKey(),
			currentGrandchildKBArticleB.getResourcePrimKey());
		Assert.assertEquals(
			greatGrandchildKBArticleB.getResourcePrimKey(),
			currentGreatGrandchildKBArticleB.getResourcePrimKey());
	}

	@Test
	public void testGetKBArticleAndAllDescendantKBArticles() throws Exception {
		KBArticle parentKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Parent Article",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle childKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			parentKBArticle.getResourcePrimKey(), "Child Article",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle grandchildKBArticleA = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			childKBArticle.getResourcePrimKey(), "Grandchild Article A",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle greatGrandchildKBArticleA =
			_kbArticleLocalService.addKBArticle(
				null, _user.getUserId(), _kbArticleClassNameId,
				grandchildKBArticleA.getResourcePrimKey(),
				"GreatGrandchild Article A", StringUtil.randomString(),
				StringUtil.randomString(), StringUtil.randomString(), null,
				null, new Date(), null, null, null, _serviceContext);

		KBArticle grandchildKBArticleB = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			childKBArticle.getResourcePrimKey(), "Grandchild Article B",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle greatGrandchildKBArticleB =
			_kbArticleLocalService.addKBArticle(
				null, _user.getUserId(), _kbArticleClassNameId,
				grandchildKBArticleB.getResourcePrimKey(),
				"GreatGrandchild Article B", StringUtil.randomString(),
				StringUtil.randomString(), StringUtil.randomString(), null,
				null, new Date(), null, null, null, _serviceContext);

		List<KBArticle> kbArticleAndAllDescendantKBArticles =
			_kbArticleLocalService.getKBArticleAndAllDescendantKBArticles(
				parentKBArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED,
				KBArticlePriorityComparator.getInstance(true));

		Assert.assertEquals(
			kbArticleAndAllDescendantKBArticles.toString(), 6,
			kbArticleAndAllDescendantKBArticles.size());

		KBArticle currentParentKBArticle =
			kbArticleAndAllDescendantKBArticles.get(0);
		KBArticle currentChildKBArticle =
			kbArticleAndAllDescendantKBArticles.get(1);
		KBArticle currentGrandchildKBArticleA =
			kbArticleAndAllDescendantKBArticles.get(2);
		KBArticle currentGreatGrandchildKBArticleA =
			kbArticleAndAllDescendantKBArticles.get(3);
		KBArticle currentGrandchildKBArticleB =
			kbArticleAndAllDescendantKBArticles.get(4);
		KBArticle currentGreatGrandchildKBArticleB =
			kbArticleAndAllDescendantKBArticles.get(5);

		Assert.assertEquals(
			parentKBArticle.getResourcePrimKey(),
			currentParentKBArticle.getResourcePrimKey());
		Assert.assertEquals(
			childKBArticle.getResourcePrimKey(),
			currentChildKBArticle.getResourcePrimKey());
		Assert.assertEquals(
			grandchildKBArticleA.getResourcePrimKey(),
			currentGrandchildKBArticleA.getResourcePrimKey());
		Assert.assertEquals(
			greatGrandchildKBArticleA.getResourcePrimKey(),
			currentGreatGrandchildKBArticleA.getResourcePrimKey());
		Assert.assertEquals(
			grandchildKBArticleB.getResourcePrimKey(),
			currentGrandchildKBArticleB.getResourcePrimKey());
		Assert.assertEquals(
			greatGrandchildKBArticleB.getResourcePrimKey(),
			currentGreatGrandchildKBArticleB.getResourcePrimKey());
	}

	@Test(expected = KBArticleParentException.class)
	public void testMoveKBArticleToInvalidParentKBArticle() throws Exception {
		KBArticle parentKBArticle = _addKbArticle();

		KBArticle childKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle grandChildKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbArticleClassNameId,
			childKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		_kbArticleLocalService.moveKBArticle(
			_user.getUserId(), parentKBArticle.getResourcePrimKey(),
			_kbArticleClassNameId, grandChildKBArticle.getResourcePrimKey(),
			grandChildKBArticle.getPriority());
	}

	@Test
	public void testMoveKBArticleToParentKBArticleInHomeFolder()
		throws Exception {

		KBArticle kbArticle = _addKbArticle();

		KBArticle parentKBArticle = _addKbArticle();

		_kbArticleLocalService.moveKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			_kbArticleClassNameId, parentKBArticle.getResourcePrimKey(),
			parentKBArticle.getPriority());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			_kbArticleClassNameId, kbArticle.getParentResourceClassNameId());
		Assert.assertEquals(
			parentKBArticle.getResourcePrimKey(),
			kbArticle.getParentResourcePrimKey());
	}

	@Test
	public void testMoveKBArticleToParentKBArticleInKBFolder()
		throws Exception {

		KBArticle kbArticle = _addKbArticle();

		KBFolder kbFolder = _kbFolderLocalService.addKBFolder(
			null, _user.getUserId(), _group.getGroupId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			_serviceContext);

		KBArticle parentKBArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			kbFolder.getKbFolderId(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		_kbArticleLocalService.moveKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			_kbArticleClassNameId, parentKBArticle.getResourcePrimKey(),
			parentKBArticle.getPriority());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			_kbArticleClassNameId, kbArticle.getParentResourceClassNameId());
		Assert.assertEquals(
			parentKBArticle.getResourcePrimKey(),
			kbArticle.getParentResourcePrimKey());
		Assert.assertEquals(
			kbFolder.getKbFolderId(), kbArticle.getKbFolderId());
	}

	@Test
	public void testMoveKBArticleToParentKBFolderInHomeFolder()
		throws Exception {

		KBArticle kbArticle = _addKbArticle();

		KBFolder parentKBFolder = _kbFolderLocalService.addKBFolder(
			null, _user.getUserId(), _group.getGroupId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			_serviceContext);

		_kbArticleLocalService.moveKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			_kbFolderClassNameId, parentKBFolder.getKbFolderId(),
			kbArticle.getPriority());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			_kbFolderClassNameId, kbArticle.getParentResourceClassNameId());
		Assert.assertEquals(
			parentKBFolder.getKbFolderId(),
			kbArticle.getParentResourcePrimKey());
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testMoveKBArticleToTrashKBArticleWithLock()
		throws PortalException {

		KBArticle kbArticle = _addKbArticle();

		_testKBArticleLock(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.moveKBArticleToTrash(
				_user.getUserId(), kbArticle.getResourcePrimKey()));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testMoveKBArticleToTrashKBArticleWithLockByPreviousUser()
		throws Exception {

		KBArticle kbArticle = _addKbArticle();

		_testKBArticleWithLockByPreviousUser(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.moveKBArticleToTrash(
				_user.getUserId(), kbArticle.getResourcePrimKey()));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testMoveKBArticleWithLock() throws PortalException {
		KBArticle kbArticle = _addKbArticle();
		KBArticle parentKBArticle = _addKbArticle();

		_testKBArticleLock(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.moveKBArticle(
				_user.getUserId(), kbArticle.getResourcePrimKey(),
				_kbArticleClassNameId, parentKBArticle.getResourcePrimKey(),
				parentKBArticle.getPriority()));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testMoveKBArticleWithLockByPreviousUser() throws Exception {
		KBArticle kbArticle = _addKbArticle();
		KBArticle parentKBArticle = _addKbArticle();

		_testKBArticleWithLockByPreviousUser(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.moveKBArticle(
				_user.getUserId(), kbArticle.getResourcePrimKey(),
				_kbArticleClassNameId, parentKBArticle.getResourcePrimKey(),
				parentKBArticle.getPriority()));
	}

	@Test
	public void testPreviousAndNextKBArticles() throws Exception {
		KBArticle parentKBArticle = _addKbArticle();

		KBArticle childKBArticle1 = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle childKBArticle2 = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), parentKBArticle.getClassNameId(),
			parentKBArticle.getResourcePrimKey(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			_serviceContext);

		KBArticle topLevelKBArticle = _addKbArticle();

		KBArticle[] parentPreviousAndNextKBArticles =
			_kbArticleLocalService.getPreviousAndNextKBArticles(
				parentKBArticle.getKbArticleId());

		Assert.assertNull(parentPreviousAndNextKBArticles[0]);
		Assert.assertEquals(
			childKBArticle1, parentPreviousAndNextKBArticles[2]);

		KBArticle[] child1PreviousAndNextKBArticles =
			_kbArticleLocalService.getPreviousAndNextKBArticles(
				childKBArticle1.getKbArticleId());

		Assert.assertEquals(
			parentKBArticle, child1PreviousAndNextKBArticles[0]);
		Assert.assertEquals(
			childKBArticle2, child1PreviousAndNextKBArticles[2]);

		KBArticle[] child2PreviousAndNextKBArticles =
			_kbArticleLocalService.getPreviousAndNextKBArticles(
				childKBArticle2.getKbArticleId());

		Assert.assertEquals(
			childKBArticle1, child2PreviousAndNextKBArticles[0]);
		Assert.assertEquals(
			topLevelKBArticle, child2PreviousAndNextKBArticles[2]);

		KBArticle[] topLevelPreviousAndNextKBArticles =
			_kbArticleLocalService.getPreviousAndNextKBArticles(
				topLevelKBArticle.getKbArticleId());

		Assert.assertEquals(
			childKBArticle2, topLevelPreviousAndNextKBArticles[0]);
		Assert.assertNull(topLevelPreviousAndNextKBArticles[2]);
	}

	@Test
	public void testRemoveExpirationReviewDate() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		Date expirationDate = new Date(
			System.currentTimeMillis() + (1 * Time.DAY));
		Date reviewDate = new Date(System.currentTimeMillis() + (1 * Time.DAY));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), expirationDate, reviewDate, null, _serviceContext);

		_kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, kbArticle.getDisplayDate(),
			null, null, null, null, _serviceContext);

		KBArticle latestKBArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertNull(latestKBArticle.getExpirationDate());
		Assert.assertNull(latestKBArticle.getReviewDate());
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testRevertKBArticleWithLock() throws PortalException {
		KBArticle kbArticle = _addKbArticle();

		int firstVersion = kbArticle.getVersion();

		_kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			null, _serviceContext);

		_testKBArticleLock(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.revertKBArticle(
				_user.getUserId(), kbArticle.getResourcePrimKey(), firstVersion,
				_serviceContext));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testRevertKBArticleWithLockByPreviousUser() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		int firstVersion = kbArticle.getVersion();

		_kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			null, _serviceContext);

		_testKBArticleWithLockByPreviousUser(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.revertKBArticle(
				_user.getUserId(), kbArticle.getResourcePrimKey(), firstVersion,
				_serviceContext));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testUpdateAndUnlockKBArticleWithPreviousLockByCurrentUser()
		throws Exception {

		KBArticle kbArticle = _addKbArticle();

		_kbArticleLocalService.lockKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey());

		_kbArticleLocalService.updateAndUnlockKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			null, _serviceContext);

		Assert.assertFalse(
			_kbArticleLocalService.hasKBArticleLock(
				_user.getUserId(), kbArticle.getResourcePrimKey()));
	}

	@Test(expected = KBArticleDisplayDateException.class)
	public void testUpdateKBArticleDisplayDateException() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			RandomTestUtil.nextDate(),
			new Date(System.currentTimeMillis() + (1 * Time.DAY)), null, null,
			_serviceContext);

		_kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			kbArticle.getTitle(), kbArticle.getContent(),
			kbArticle.getDescription(), null, kbArticle.getSourceURL(), null,
			kbArticle.getExpirationDate(), kbArticle.getReviewDate(), null,
			null, _serviceContext);
	}

	@Test
	public void testUpdateKBArticleDisplayDateExpiredKBArticleCanBePublished()
		throws Exception {

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), null, null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());

		kbArticle = _kbArticleLocalService.expireKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(), _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, kbArticle.getStatus());

		Date displayDate = new Date(System.currentTimeMillis() + Time.DAY);

		kbArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, displayDate, null, null,
			null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, kbArticle.getStatus());

		displayDate = new Date(System.currentTimeMillis() - Time.DAY);

		kbArticle.setDisplayDate(displayDate);

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.fetchKBArticle(
			kbArticle.getKbArticleId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());
	}

	@Test
	public void testUpdateKBArticleDisplayDateOnDraftKBArticleUpdatesStatusToScheduled()
		throws Exception {

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		Date displayDate = new Date(
			System.currentTimeMillis() - (2 * Time.DAY));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			displayDate, null, null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, kbArticle.getStatus());

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		displayDate = new Date(System.currentTimeMillis() + (2 * Time.DAY));

		kbArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, displayDate, null, null,
			null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, kbArticle.getStatus());
	}

	@Test
	public void testUpdateKBArticleDisplayDateUpdatesKBArticleStatusToApproved()
		throws Exception {

		Date displayDate = new Date(
			System.currentTimeMillis() + (2 * Time.DAY));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			displayDate, null, null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, kbArticle.getStatus());

		displayDate = new Date(System.currentTimeMillis() - (2 * Time.DAY));

		kbArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, displayDate, null, null,
			null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());
	}

	@Test
	public void testUpdateKBArticleDisplayDateUpdatesKBArticleStatusToScheduled()
		throws Exception {

		Date displayDate = new Date(
			System.currentTimeMillis() - (2 * Time.DAY));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			displayDate, null, null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());

		displayDate = new Date(System.currentTimeMillis() + (2 * Time.DAY));

		kbArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, displayDate, null, null,
			null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, kbArticle.getStatus());
	}

	@Test
	public void testUpdateKBArticleExpirationDateUpdatesStatus()
		throws Exception {

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		Date expirationDate = new Date(
			System.currentTimeMillis() + (1 * Time.DAY));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), expirationDate, null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());

		kbArticle = _kbArticleLocalService.expireKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(), _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, kbArticle.getStatus());

		expirationDate = new Date(System.currentTimeMillis() + (2 * Time.DAY));

		kbArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, kbArticle.getDisplayDate(),
			expirationDate, null, null, null, _serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());
	}

	@Test
	public void testUpdateKBArticleUpdatesAssetEntry() throws Exception {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		KBArticle kbArticle = _addKbArticle();

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		Assert.assertNull(assetEntry.getExpirationDate());

		kbArticle = _kbArticleLocalService.expireKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(), _serviceContext);

		assetEntry = _assetEntryLocalService.getEntry(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		Assert.assertEquals(
			kbArticle.getExpirationDate(), assetEntry.getExpirationDate());
		Assert.assertFalse(assetEntry.isVisible());

		kbArticle = _kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, kbArticle.getDisplayDate(),
			null, null, null, null, _serviceContext);

		assetEntry = _assetEntryLocalService.getEntry(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		Assert.assertNull(assetEntry.getExpirationDate());
		Assert.assertEquals(kbArticle.getTitle(), assetEntry.getTitle());
		Assert.assertTrue(assetEntry.isVisible());
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testUpdateKBArticleWithLockByPreviousUser() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		_testKBArticleWithLockByPreviousUser(
			kbArticle.getResourcePrimKey(),
			() -> _kbArticleLocalService.updateKBArticle(
				_user.getUserId(), kbArticle.getResourcePrimKey(),
				StringUtil.randomString(), StringUtil.randomString(),
				StringUtil.randomString(), null, null, new Date(), null, null,
				null, null, new ServiceContext()));
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testUpdateKBArticleWithPreviousLockByCurrentUser()
		throws Exception {

		KBArticle kbArticle = _addKbArticle();

		_kbArticleLocalService.lockKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey());

		_kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			null, _serviceContext);

		Assert.assertTrue(
			_kbArticleLocalService.hasKBArticleLock(
				_user.getUserId(), kbArticle.getResourcePrimKey()));

		_kbArticleLocalService.unlockKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey());
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testUpdateKBArticleWithoutPreviousLock() throws Exception {
		KBArticle kbArticle = _addKbArticle();

		_kbArticleLocalService.updateKBArticle(
			_user.getUserId(), kbArticle.getResourcePrimKey(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, new Date(), null, null, null,
			null, _serviceContext);

		Assert.assertFalse(
			_kbArticleLocalService.hasKBArticleLock(
				_user.getUserId(), kbArticle.getResourcePrimKey()));
	}

	protected void importMarkdownArticles() throws PortalException {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		String fileName = "markdown-articles.zip";

		InputStream zipFileInputStream = classLoader.getResourceAsStream(
			fileName);

		_kbArticleLocalService.addKBArticlesMarkdown(
			_user.getUserId(), _group.getGroupId(),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName, true,
			zipFileInputStream, _serviceContext);
	}

	protected void updateWorkflowDefinitionForKBArticle(
			String workflowDefinition)
		throws PortalException {

		WorkflowDefinitionLinkLocalServiceUtil.updateWorkflowDefinitionLink(
			_user.getUserId(), _user.getCompanyId(), _group.getGroupId(),
			KBArticle.class.getName(), 0, 0, workflowDefinition);
	}

	private KBArticle _addKbArticle() throws PortalException {
		return _addKbArticle(new Date());
	}

	private KBArticle _addKbArticle(Date displayDate) throws PortalException {
		return _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(), _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			displayDate, null, null, null, _serviceContext);
	}

	private void _testKBArticleLock(
			long resourcePrimKey,
			UnsafeRunnable<PortalException> unsafeRunnable)
		throws PortalException {

		_kbArticleLocalService.lockKBArticle(
			_user.getUserId(), resourcePrimKey);

		Assert.assertTrue(
			_kbArticleLocalService.hasKBArticleLock(
				_user.getUserId(), resourcePrimKey));

		unsafeRunnable.run();

		Assert.assertFalse(
			LockManagerUtil.isLocked(
				KBArticleConstants.getClassName(), resourcePrimKey));
	}

	private void _testKBArticleWithLockByPreviousUser(
			long resourcePrimKey,
			UnsafeRunnable<PortalException> unsafeRunnable)
		throws Exception {

		User previousUser = UserTestUtil.addUser(_group.getGroupId());

		Lock lock = _kbArticleLocalService.lockKBArticle(
			previousUser.getUserId(), resourcePrimKey);

		Assert.assertTrue(
			_kbArticleLocalService.hasKBArticleLock(
				previousUser.getUserId(), resourcePrimKey));

		Assert.assertFalse(
			_kbArticleLocalService.hasKBArticleLock(
				_user.getUserId(), resourcePrimKey));

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (LockedKBArticleException lockedKBArticleException) {
			Lock duplicateLock = lockedKBArticleException.getLock();

			Assert.assertEquals(duplicateLock.getLockId(), lock.getLockId());
		}

		_kbArticleLocalService.unlockKBArticle(
			previousUser.getUserId(), resourcePrimKey);

		Assert.assertFalse(
			LockManagerUtil.isLocked(
				KBArticleConstants.getClassName(), resourcePrimKey));
	}

	private static final Pattern _targetBlankPattern = Pattern.compile(
		".*target=\"_blank\".*");

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private long _kbArticleClassNameId;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject
	private KBCommentLocalService _kbCommentLocalService;

	private long _kbFolderClassNameId;

	@Inject
	private KBFolderLocalService _kbFolderLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}