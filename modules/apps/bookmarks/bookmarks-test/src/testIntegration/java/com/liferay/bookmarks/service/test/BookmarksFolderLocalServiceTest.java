/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.bookmarks.test.util.BookmarksTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.SystemEventLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 * @author Roberto Díaz
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class BookmarksFolderLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddSubfolderPermission() throws Exception {
		BookmarksFolder folder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		Role role = RoleLocalServiceUtil.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_SITE,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(), BookmarksFolder.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			ActionKeys.ADD_SUBFOLDER);

		User user = UserTestUtil.addGroupUser(_group, role.getName());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		Assert.assertTrue(
			_bookmarksFolderModelResourcePermission.contains(
				permissionChecker, folder, ActionKeys.ADD_FOLDER));
	}

	@Test
	public void testDeleteFolderSystemEvent() throws Exception {
		BookmarksFolder folder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		BookmarksFolder childFolder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), folder.getFolderId(),
			RandomTestUtil.randomString());

		BookmarksFolderLocalServiceUtil.deleteFolder(folder);

		long classNameId = ClassNameLocalServiceUtil.getClassNameId(
			BookmarksFolder.class);

		List<SystemEvent> systemEvents =
			SystemEventLocalServiceUtil.getSystemEvents(
				_group.getGroupId(), classNameId, folder.getFolderId(),
				SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(systemEvents.toString(), 1, systemEvents.size());

		systemEvents = SystemEventLocalServiceUtil.getSystemEvents(
			_group.getGroupId(), classNameId, childFolder.getFolderId(),
			SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(systemEvents.toString(), 0, systemEvents.size());
	}

	@Test
	public void testGetNoAssetFolders() throws Exception {
		BookmarksFolder folder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			BookmarksFolder.class.getName(), folder.getFolderId());

		Assert.assertNotNull(assetEntry);

		AssetEntryLocalServiceUtil.deleteAssetEntry(assetEntry);

		List<BookmarksFolder> folders =
			BookmarksFolderLocalServiceUtil.getNoAssetFolders();

		Assert.assertEquals(folders.toString(), 1, folders.size());
		Assert.assertEquals(folder, folders.get(0));
	}

	@Test
	public void testMoveFolder() throws Exception {
		BookmarksFolder folder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		long initialParentFolderId = folder.getParentFolderId();

		BookmarksFolder destinationFolder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		folder = BookmarksFolderLocalServiceUtil.moveFolder(
			folder.getFolderId(), destinationFolder.getFolderId());

		Assert.assertNotEquals(
			initialParentFolderId, folder.getParentFolderId());
		Assert.assertEquals(
			destinationFolder.getFolderId(), folder.getParentFolderId());
	}

	@Test
	public void testSubscribeFolder() throws Exception {
		BookmarksFolder folder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		testSubscribeFolder(folder.getFolderId(), folder.getFolderId());
	}

	@Test
	public void testSubscribeRootFolder() throws Exception {
		testSubscribeFolder(
			BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_group.getGroupId());
	}

	@Test
	public void testUnsubscribeFolder() throws Exception {
		BookmarksFolder folder = BookmarksTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		testUnsubscribeFolder(folder.getFolderId(), folder.getFolderId());
	}

	@Test
	public void testUnsubscribeRootFolder() throws Exception {
		testUnsubscribeFolder(
			BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_group.getGroupId());
	}

	protected void testSubscribeFolder(
			long folderId, long expectedSubscriptionClassPK)
		throws Exception {

		int initialUserSubscriptionsCount =
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				TestPropsValues.getUserId());

		BookmarksFolderLocalServiceUtil.subscribeFolder(
			TestPropsValues.getUserId(), _group.getGroupId(), folderId);

		Assert.assertEquals(
			initialUserSubscriptionsCount + 1,
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				TestPropsValues.getUserId()));

		boolean exists = false;

		List<Subscription> subscriptions =
			SubscriptionLocalServiceUtil.getUserSubscriptions(
				TestPropsValues.getUserId(), BookmarksFolder.class.getName());

		for (Subscription subscription : subscriptions) {
			if (Objects.equals(
					subscription.getClassName(),
					BookmarksFolder.class.getName()) &&
				(subscription.getClassPK() == expectedSubscriptionClassPK)) {

				exists = true;

				break;
			}
		}

		Assert.assertTrue("Subscription does not exist", exists);
	}

	protected void testUnsubscribeFolder(
			long folderId, long expectedSubscriptionClassPK)
		throws Exception {

		int initialUserSubscriptionsCount =
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				TestPropsValues.getUserId());

		BookmarksFolderLocalServiceUtil.subscribeFolder(
			TestPropsValues.getUserId(), _group.getGroupId(), folderId);

		BookmarksFolderLocalServiceUtil.unsubscribeFolder(
			TestPropsValues.getUserId(), _group.getGroupId(), folderId);

		Assert.assertEquals(
			initialUserSubscriptionsCount,
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				TestPropsValues.getUserId()));

		List<Subscription> subscriptions =
			SubscriptionLocalServiceUtil.getUserSubscriptions(
				TestPropsValues.getUserId(), BookmarksFolder.class.getName());

		for (Subscription subscription : subscriptions) {
			Assert.assertFalse(
				"Subscription exists",
				Objects.equals(
					subscription.getClassName(),
					BookmarksFolder.class.getName()) &&
				(subscription.getClassPK() == expectedSubscriptionClassPK));
		}
	}

	@Inject(
		filter = "model.class.name=com.liferay.bookmarks.model.BookmarksFolder"
	)
	private ModelResourcePermission<BookmarksFolder>
		_bookmarksFolderModelResourcePermission;

	@DeleteAfterTestRun
	private Group _group;

}