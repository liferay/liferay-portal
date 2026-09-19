/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.search.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class SharingEntrySearchDLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_groupUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.POWER_USER);

		_title = StringUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		_fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), "text/plain", _title,
			StringUtil.randomString(), StringUtil.randomString(),
			StringPool.BLANK, "Searching".getBytes(), null, null, null,
			serviceContext);

		_classNameId = _classNameLocalService.getClassNameId(
			DLFileEntry.class.getName());
	}

	@Test
	public void testUserCanSearchSharedPrivateDocument() throws Exception {
		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, _groupUser.getUserId(),
			_classNameId, _fileEntry.getFileEntryId(), _group.getGroupId(),
			true, Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.getIndexer(
			DLFileEntryConstants.getClassName());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(_fileEntry.getCompanyId());
			searchContext.setGroupIds(
				new long[] {_fileEntry.getRepositoryId()});
			searchContext.setKeywords(_title);
			searchContext.setUserId(_groupUser.getUserId());

			Hits hits = indexer.search(searchContext);

			Assert.assertEquals(hits.toString(), 1, hits.getLength());
		}
	}

	@Test
	public void testUserCanSearchSharedPrivateDocumentSharedToUserGroup()
		throws Exception {

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		try {
			_userGroupLocalService.addUserUserGroup(
				_groupUser.getUserId(), userGroup);

			_sharingEntryLocalService.addSharingEntry(
				null, TestPropsValues.getUserId(), 0,
				userGroup.getUserGroupId(), 0, _classNameId,
				_fileEntry.getFileEntryId(), _group.getGroupId(), true,
				Arrays.asList(SharingEntryAction.VIEW), null,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			Indexer<DLFileEntry> indexer = IndexerRegistryUtil.getIndexer(
				DLFileEntryConstants.getClassName());

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(_groupUser);

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_groupUser, permissionChecker)) {

				SearchContext searchContext = new SearchContext();

				searchContext.setCompanyId(_fileEntry.getCompanyId());
				searchContext.setGroupIds(
					new long[] {_fileEntry.getRepositoryId()});
				searchContext.setKeywords(_title);
				searchContext.setUserId(_groupUser.getUserId());

				Hits hits = indexer.search(searchContext);

				Assert.assertEquals(hits.toString(), 1, hits.getLength());
			}
		}
		finally {
			_userGroupLocalService.deleteUserUserGroup(
				_groupUser.getUserId(), userGroup);

			_userGroupLocalService.deleteUserGroup(userGroup);
		}
	}

	@Test
	public void testUserCannotSearchPrivateDocument() throws Exception {
		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.getIndexer(
			DLFileEntryConstants.getClassName());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(_fileEntry.getCompanyId());
			searchContext.setGroupIds(
				new long[] {_fileEntry.getRepositoryId()});
			searchContext.setKeywords(_title);
			searchContext.setUserId(_groupUser.getUserId());

			Hits hits = indexer.search(searchContext);

			Assert.assertEquals(hits.toString(), 0, hits.getLength());
		}
	}

	private long _classNameId;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _groupUser;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	private String _title;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

}