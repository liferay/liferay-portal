/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageService;
import com.liferay.wiki.test.util.WikiTestUtil;

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
public class WikiPageServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_node = WikiTestUtil.addNode(_group.getGroupId());
	}

	@Test
	public void testAddPage() throws Exception {
		_user = UserTestUtil.addUser();

		Assert.assertThrows(
			PrincipalException.MustHavePermission.class, () -> _addPage());

		_userLocalService.addGroupUser(_group.getGroupId(), _user);

		WikiPage page = _addPage();

		Assert.assertEquals(_user.getUserId(), page.getUserId());
	}

	@Test
	@TestInfo("LPD-115513")
	public void testCopyPageAttachments() throws Exception {
		_templateGroup = GroupTestUtil.addGroup();

		WikiNode templateNode = WikiTestUtil.addNode(
			_templateGroup.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_templateGroup.getGroupId());

		serviceContext.setAddGuestPermissions(false);

		WikiPage templatePage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), templateNode.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			serviceContext);

		WikiTestUtil.addPageAttachment(
			TestPropsValues.getUserId(), templateNode.getNodeId(),
			templatePage.getTitle(), getClass());

		WikiPage page = WikiTestUtil.addPage(
			_group.getGroupId(), _node.getNodeId(), true);

		_user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(_group.getGroupId(), _user);

		PrincipalException.MustHavePermission mustHavePermission =
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _copyPageAttachments(page, templatePage));

		Assert.assertArrayEquals(
			new String[] {ActionKeys.VIEW}, mustHavePermission.actionId);
		Assert.assertEquals(
			templatePage.getResourcePrimKey(), mustHavePermission.resourceId);
		Assert.assertEquals(
			WikiPage.class.getName(), mustHavePermission.resourceName);

		Assert.assertEquals(0, page.getAttachmentsFileEntriesCount());

		_userLocalService.addGroupUser(_templateGroup.getGroupId(), _user);

		_copyPageAttachments(page, templatePage);

		Assert.assertEquals(1, page.getAttachmentsFileEntriesCount());
	}

	private WikiPage _addPage() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			return _wikiPageService.addPage(
				RandomTestUtil.randomString(), _node.getNodeId(),
				RandomTestUtil.randomString(),
				WikiPageConstants.VERSION_DEFAULT,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, "creole", true, null, null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}
	}

	private void _copyPageAttachments(WikiPage page, WikiPage templatePage)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_wikiPageService.copyPageAttachments(
				templatePage.getNodeId(), templatePage.getTitle(),
				page.getNodeId(), page.getTitle());
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	private WikiNode _node;

	@DeleteAfterTestRun
	private Group _templateGroup;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WikiPageService _wikiPageService;

}