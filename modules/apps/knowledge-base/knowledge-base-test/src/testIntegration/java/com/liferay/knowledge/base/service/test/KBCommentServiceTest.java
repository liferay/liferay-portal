/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.service.KBCommentLocalService;
import com.liferay.knowledge.base.service.KBCommentService;
import com.liferay.knowledge.base.test.util.KBTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class KBCommentServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-115722")
	public void testDeleteKBComment() throws Exception {
		Group group = GroupTestUtil.addGroup();

		User user1 = UserTestUtil.addUser(group.getGroupId());

		KBArticle kbArticle = KBTestUtil.addKBArticle(group.getGroupId());

		KBComment kbComment = _kbCommentLocalService.addKBComment(
			user1.getUserId(), PortalUtil.getClassNameId(KBArticle.class),
			kbArticle.getResourcePrimKey(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		User user2 = UserTestUtil.addUser();

		KBComment user2KBComment = _kbCommentLocalService.createKBComment(
			kbComment.getKbCommentId());

		user2KBComment.setUserId(user2.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user2)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _kbCommentService.deleteKBComment(user2KBComment));
		}

		Assert.assertNotNull(
			_kbCommentLocalService.fetchKBComment(kbComment.getKbCommentId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1)) {

			_kbCommentService.deleteKBComment(kbComment);
		}

		Assert.assertNull(
			_kbCommentLocalService.fetchKBComment(kbComment.getKbCommentId()));
	}

	@Inject
	private KBCommentLocalService _kbCommentLocalService;

	@Inject
	private KBCommentService _kbCommentService;

}