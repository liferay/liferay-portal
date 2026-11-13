/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBCommentConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBCommentLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class KBCommentLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, _user.getUserId());
	}

	@Test
	@TestInfo("LPD-115327")
	public void testGetKBCommentsCount() throws Exception {
		KBArticle kbArticle1 = _addKBArticle();

		KBComment kbComment = _addKBComment(kbArticle1);

		_addKBComment(kbArticle1);
		_addKBComment(kbArticle1);

		KBArticle kbArticle2 = _addKBArticle();

		_addKBComment(kbArticle2);

		_assertKBCommentsCount(3, 3, kbArticle1);

		_kbCommentLocalService.updateStatus(
			_user.getUserId(), kbComment.getKbCommentId(),
			KBCommentConstants.STATUS_COMPLETED, _serviceContext);

		_assertKBCommentsCount(3, 2, kbArticle1);
	}

	@Test
	@TestInfo("LPD-115327")
	public void testUpdateStatus() throws Exception {
		KBComment kbComment = _addKBComment(_addKBArticle());

		Assert.assertEquals(
			KBCommentConstants.STATUS_NEW, kbComment.getStatus());

		_kbCommentLocalService.updateStatus(
			_user.getUserId(), kbComment.getKbCommentId(),
			KBCommentConstants.STATUS_IN_PROGRESS, _serviceContext);

		kbComment = _kbCommentLocalService.getKBComment(
			kbComment.getKbCommentId());

		Assert.assertEquals(
			KBCommentConstants.STATUS_IN_PROGRESS, kbComment.getStatus());

		_kbCommentLocalService.updateStatus(
			_user.getUserId(), kbComment.getKbCommentId(),
			KBCommentConstants.STATUS_COMPLETED, _serviceContext);

		kbComment = _kbCommentLocalService.getKBComment(
			kbComment.getKbCommentId());

		Assert.assertEquals(
			KBCommentConstants.STATUS_COMPLETED, kbComment.getStatus());
	}

	private KBArticle _addKBArticle() throws Exception {
		return _kbArticleLocalService.addKBArticle(
			null, _user.getUserId(),
			_classNameLocalService.getClassNameId(
				KBFolderConstants.getClassName()),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			new Date(), null, null, null, _serviceContext);
	}

	private KBComment _addKBComment(KBArticle kbArticle) throws Exception {
		return _kbCommentLocalService.addKBComment(
			_user.getUserId(), kbArticle.getClassNameId(),
			kbArticle.getClassPK(), StringUtil.randomString(), _serviceContext);
	}

	private void _assertKBCommentsCount(
		int expectedKBCommentsCount, int expectedPendingKBCommentsCount,
		KBArticle kbArticle) {

		Assert.assertEquals(
			expectedKBCommentsCount,
			_kbCommentLocalService.getKBCommentsCount(
				KBArticleConstants.getClassName(), kbArticle.getClassPK()));
		Assert.assertEquals(
			expectedPendingKBCommentsCount,
			_kbCommentLocalService.getKBCommentsCount(
				KBArticleConstants.getClassName(), kbArticle.getClassPK(),
				new int[] {
					KBCommentConstants.STATUS_IN_PROGRESS,
					KBCommentConstants.STATUS_NEW
				}));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject
	private KBCommentLocalService _kbCommentLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}