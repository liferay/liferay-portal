/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class KBArticleServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
		_kbFolderClassNameId = ClassNameLocalServiceUtil.getClassNameId(
			KBFolderConstants.getClassName());
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group1, TestPropsValues.getUserId());
		_siteMemberUser = UserTestUtil.addUser(_group1.getGroupId());
		_testPortletId = "TEST_PORTLET_" + RandomTestUtil.randomString();
		_user = UserTestUtil.addUser();

		RoleTestUtil.addResourcePermission(
			_role, KBConstants.RESOURCE_NAME_ADMIN,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group1.getGroupId()),
			KBActionKeys.DELETE_KB_ARTICLES);

		_userGroupRoleLocalService.addUserGroupRoles(
			_siteMemberUser.getUserId(), _group1.getGroupId(),
			new long[] {_role.getRoleId()});
	}

	@Test
	public void testDeleteKBArticleAttachment() throws Exception {
		_testDeleteKBArticleAttachment();
		_testDeleteKBArticleAttachmentWithFileEntryInRootFolder();
		_testDeleteKBArticleAttachmentWithoutUpdatePermission();
	}

	@Test
	public void testDeleteKBArticles() throws Exception {
		_testDeleteKBArticles();
		_testDeleteKBArticlesWithKBArticleFromDifferentGroup();
		_testDeleteKBArticlesWithKBArticleFromDifferentGroupAndDeletePermission();
		_testDeleteKBArticlesWithKBArticlesFromDifferentGroups();
	}

	@FeatureFlag("LPD-11003")
	@Test
	public void testForceLockKBArticle() throws Exception {
		KBArticle kbArticle = _addKbArticle(new Date());
		User otherUser = UserTestUtil.addUser(_group1.getGroupId());

		try {
			_kbArticleLocalService.lockKBArticle(
				otherUser.getUserId(), kbArticle.getResourcePrimKey());

			Assert.assertTrue(
				_kbArticleLocalService.hasKBArticleLock(
					otherUser.getUserId(), kbArticle.getResourcePrimKey()));
			Assert.assertFalse(
				_kbArticleLocalService.hasKBArticleLock(
					TestPropsValues.getUserId(),
					kbArticle.getResourcePrimKey()));

			_kbArticleService.forceLockKBArticle(
				_group1.getGroupId(), kbArticle.getResourcePrimKey());

			Assert.assertFalse(
				_kbArticleLocalService.hasKBArticleLock(
					otherUser.getUserId(), kbArticle.getResourcePrimKey()));
			Assert.assertTrue(
				_kbArticleLocalService.hasKBArticleLock(
					TestPropsValues.getUserId(),
					kbArticle.getResourcePrimKey()));
		}
		finally {
			_kbArticleService.deleteKBArticle(kbArticle.getResourcePrimKey());
		}
	}

	@Test
	public void testGetAllDescendantKBArticles() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1, TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		KBArticle kbArticle1 = _addKbArticle(new Date(), serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		KBArticle kbArticle2 = _addKbArticle(new Date(), serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_siteMemberUser)) {

			_testGetAllDescendantKBArticles(
				WorkflowConstants.STATUS_APPROVED, kbArticle1);
			_testGetAllDescendantKBArticles(
				WorkflowConstants.STATUS_DRAFT, kbArticle2);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_testGetAllDescendantKBArticles(WorkflowConstants.STATUS_APPROVED);
			_testGetAllDescendantKBArticles(WorkflowConstants.STATUS_DRAFT);
		}
	}

	@Test
	public void testGetKBArticleAttachment() throws Exception {
		_testGetKBArticleAttachment();
		_testGetKBArticleAttachmentWithDraftKBArticle();
		_testGetKBArticleAttachmentWithFileEntryInDifferentRepository();
		_testGetKBArticleAttachmentWithFileEntryInRootFolder();
		_testGetKBArticleAttachmentWithNonexistentKBArticle();
		_testGetKBArticleAttachmentWithoutViewPermission();
	}

	@Test
	public void testGetKBArticlesByStatus() throws PortalException {
		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		_addKbArticle(new Date());
		_addKbArticle(new Date(System.currentTimeMillis() + (2 * Time.DAY)));

		KBArticle kbArticle = _addKbArticle(new Date());

		List<KBArticle> kbArticles = _kbArticleService.getKBArticles(
			_group1.getGroupId(), KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, KBArticlePriorityComparator.getInstance(true));

		Assert.assertEquals(kbArticles.toString(), 2, kbArticles.size());

		kbArticles = _kbArticleService.getKBArticles(
			_group1.getGroupId(), KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			KBArticlePriorityComparator.getInstance(true));

		Assert.assertEquals(kbArticles.toString(), 3, kbArticles.size());

		_kbArticleService.expireKBArticle(
			kbArticle.getResourcePrimKey(), _serviceContext);

		kbArticles = _kbArticleService.getKBArticles(
			_group1.getGroupId(), KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, KBArticlePriorityComparator.getInstance(true));

		Assert.assertEquals(kbArticles.toString(), 1, kbArticles.size());
	}

	@Test
	public void testUpdateKBArticle() throws Exception {
		_testUpdateKBArticle();
		_testUpdateKBArticleWithFileEntryInDifferentKBArticle();
		_testUpdateKBArticleWithFileEntryInDifferentRepository();
		_testUpdateKBArticleWithFileEntryInRootFolder();
		_testUpdateKBArticleWithNonexistentFileEntry();
	}

	private FileEntry _addAttachment(
			boolean addGroupPermissions, boolean addGuestPermissions)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(addGroupPermissions);
		serviceContext.setAddGuestPermissions(addGuestPermissions);

		return _addAttachment(serviceContext);
	}

	private FileEntry _addAttachment(KBArticle kbArticle) throws Exception {
		return _kbArticleLocalService.addAttachment(
			TestPropsValues.getUserId(), kbArticle.getResourcePrimKey(),
			RandomTestUtil.randomString() + ".txt",
			new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			ContentTypes.TEXT_PLAIN);
	}

	private FileEntry _addAttachment(ServiceContext serviceContext)
		throws Exception {

		return _addAttachment(_addKbArticle(new Date(), serviceContext));
	}

	private FileEntry _addFileEntry(long folderId) throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group1.getGroupId(), folderId,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), null, null, null,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			_serviceContext);
	}

	private Folder _addFolder(String name) throws Exception {
		return _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group1.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, name,
			RandomTestUtil.randomString(), _serviceContext);
	}

	private KBArticle _addKbArticle(Date displayDate) throws PortalException {
		return _addKbArticle(displayDate, _serviceContext);
	}

	private KBArticle _addKbArticle(
			Date displayDate, ServiceContext serviceContext)
		throws PortalException {

		return _kbArticleService.addKBArticle(
			null, _testPortletId, _kbFolderClassNameId,
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(), null, null,
			displayDate, null, null, null, serviceContext);
	}

	private KBArticle _addKbArticle(Group group) throws Exception {
		return _addKbArticle(
			new Date(),
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId()));
	}

	private void _assertNoSuchFileEntry(FileEntry fileEntry) {
		Assert.assertThrows(
			NoSuchFileEntryException.class,
			() -> _kbArticleService.getKBArticleAttachment(
				fileEntry.getFileEntryId()));
	}

	private void _testDeleteKBArticleAttachment() throws Exception {
		FileEntry fileEntry = _addAttachment(true, false);

		_kbArticleService.deleteKBArticleAttachment(fileEntry.getFileEntryId());

		Assert.assertNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));
	}

	private void _testDeleteKBArticleAttachmentWithFileEntryInRootFolder()
		throws Exception {

		FileEntry fileEntry = _addFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertThrows(
			NoSuchFileEntryException.class,
			() -> _kbArticleService.deleteKBArticleAttachment(
				fileEntry.getFileEntryId()));

		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));
	}

	private void _testDeleteKBArticleAttachmentWithoutUpdatePermission()
		throws Exception {

		FileEntry fileEntry = _addAttachment(true, false);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_siteMemberUser)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _kbArticleService.deleteKBArticleAttachment(
					fileEntry.getFileEntryId()));
		}

		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));
	}

	private void _testDeleteKBArticles() throws Exception {
		KBArticle kbArticle = _addKbArticle(_group1);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_siteMemberUser)) {

			_kbArticleService.deleteKBArticles(
				_group1.getGroupId(),
				new long[] {kbArticle.getResourcePrimKey()});
		}

		Assert.assertNull(
			_kbArticleLocalService.fetchLatestKBArticle(
				kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY));
	}

	private void _testDeleteKBArticlesWithKBArticleFromDifferentGroup()
		throws Exception {

		KBArticle kbArticle = _addKbArticle(_group2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_siteMemberUser)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _kbArticleService.deleteKBArticles(
					_group1.getGroupId(),
					new long[] {kbArticle.getResourcePrimKey()}));
		}

		Assert.assertNotNull(
			_kbArticleLocalService.fetchLatestKBArticle(
				kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY));
	}

	private void _testDeleteKBArticlesWithKBArticleFromDifferentGroupAndDeletePermission()
		throws Exception {

		KBArticle kbArticle = _addKbArticle(_group2);

		_regularRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			_regularRole.getCompanyId(), KBArticle.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(kbArticle.getResourcePrimKey()),
			_regularRole.getRoleId(), new String[] {KBActionKeys.DELETE});

		_userLocalService.addRoleUser(
			_regularRole.getRoleId(), _siteMemberUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_siteMemberUser)) {

			_kbArticleService.deleteKBArticles(
				_group1.getGroupId(),
				new long[] {kbArticle.getResourcePrimKey()});
		}

		Assert.assertNull(
			_kbArticleLocalService.fetchLatestKBArticle(
				kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY));
	}

	private void _testDeleteKBArticlesWithKBArticlesFromDifferentGroups()
		throws Exception {

		KBArticle kbArticle1 = _addKbArticle(_group1);
		KBArticle kbArticle2 = _addKbArticle(_group2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_siteMemberUser)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _kbArticleService.deleteKBArticles(
					_group1.getGroupId(),
					new long[] {
						kbArticle1.getResourcePrimKey(),
						kbArticle2.getResourcePrimKey()
					}));
		}

		Assert.assertNotNull(
			_kbArticleLocalService.fetchLatestKBArticle(
				kbArticle1.getResourcePrimKey(), WorkflowConstants.STATUS_ANY));
		Assert.assertNotNull(
			_kbArticleLocalService.fetchLatestKBArticle(
				kbArticle2.getResourcePrimKey(), WorkflowConstants.STATUS_ANY));
	}

	private void _testGetAllDescendantKBArticles(
			int status, KBArticle... expectedKBArticles)
		throws Exception {

		Assert.assertEquals(
			Arrays.asList(expectedKBArticles),
			_kbArticleService.getAllDescendantKBArticles(
				_group1.getGroupId(),
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID, status,
				KBArticlePriorityComparator.getInstance(true)));
	}

	private void _testGetKBArticleAttachment() throws Exception {
		FileEntry fileEntry = _addAttachment(true, false);

		FileEntry kbArticleAttachment =
			_kbArticleService.getKBArticleAttachment(
				fileEntry.getFileEntryId());

		Assert.assertEquals(
			fileEntry.getFileEntryId(), kbArticleAttachment.getFileEntryId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_siteMemberUser)) {

			kbArticleAttachment = _kbArticleService.getKBArticleAttachment(
				fileEntry.getFileEntryId());

			Assert.assertEquals(
				fileEntry.getFileEntryId(),
				kbArticleAttachment.getFileEntryId());
		}
	}

	private void _testGetKBArticleAttachmentWithDraftKBArticle()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1, TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		FileEntry fileEntry = _addAttachment(serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_siteMemberUser)) {

			FileEntry kbArticleAttachment =
				_kbArticleService.getKBArticleAttachment(
					fileEntry.getFileEntryId());

			Assert.assertEquals(
				fileEntry.getFileEntryId(),
				kbArticleAttachment.getFileEntryId());
		}
	}

	private void _testGetKBArticleAttachmentWithFileEntryInDifferentRepository()
		throws Exception {

		KBArticle kbArticle = _addKbArticle(new Date());

		Folder folder = _addFolder(
			String.valueOf(kbArticle.getResourcePrimKey()));

		_assertNoSuchFileEntry(_addFileEntry(folder.getFolderId()));
	}

	private void _testGetKBArticleAttachmentWithFileEntryInRootFolder()
		throws Exception {

		_assertNoSuchFileEntry(
			_addFileEntry(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
	}

	private void _testGetKBArticleAttachmentWithNonexistentKBArticle()
		throws Exception {

		Folder folder = _addFolder(RandomTestUtil.randomString());

		_assertNoSuchFileEntry(_addFileEntry(folder.getFolderId()));
	}

	private void _testGetKBArticleAttachmentWithoutViewPermission()
		throws Exception {

		FileEntry fileEntry = _addAttachment(false, false);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _kbArticleService.getKBArticleAttachment(
					fileEntry.getFileEntryId()));
		}
	}

	private void _testUpdateKBArticle() throws Exception {
		KBArticle kbArticle = _addKbArticle(new Date());

		FileEntry fileEntry = _addAttachment(kbArticle);

		_updateKBArticle(kbArticle, new long[] {fileEntry.getFileEntryId()});

		Assert.assertNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));
	}

	private void _testUpdateKBArticleWithFileEntryInDifferentKBArticle()
		throws Exception {

		KBArticle kbArticle = _addKbArticle(new Date());

		FileEntry fileEntry = _addAttachment(_addKbArticle(new Date()));

		Assert.assertThrows(
			NoSuchFileEntryException.class,
			() -> _updateKBArticle(
				kbArticle, new long[] {fileEntry.getFileEntryId()}));

		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));
	}

	private void _testUpdateKBArticleWithFileEntryInDifferentRepository()
		throws Exception {

		KBArticle kbArticle = _addKbArticle(new Date());

		Folder folder = _addFolder(
			String.valueOf(kbArticle.getResourcePrimKey()));

		FileEntry fileEntry = _addFileEntry(folder.getFolderId());

		Assert.assertThrows(
			NoSuchFileEntryException.class,
			() -> _updateKBArticle(
				kbArticle, new long[] {fileEntry.getFileEntryId()}));

		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));
	}

	private void _testUpdateKBArticleWithFileEntryInRootFolder()
		throws Exception {

		KBArticle kbArticle = _addKbArticle(new Date());

		FileEntry fileEntry = _addFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertThrows(
			NoSuchFileEntryException.class,
			() -> _updateKBArticle(
				kbArticle, new long[] {fileEntry.getFileEntryId()}));

		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));
	}

	private void _testUpdateKBArticleWithNonexistentFileEntry()
		throws Exception {

		KBArticle kbArticle = _addKbArticle(new Date());

		FileEntry fileEntry1 = _addAttachment(kbArticle);
		FileEntry fileEntry2 = _addAttachment(kbArticle);

		_updateKBArticle(kbArticle, new long[] {fileEntry1.getFileEntryId()});

		_updateKBArticle(
			kbArticle,
			new long[] {
				fileEntry1.getFileEntryId(), fileEntry2.getFileEntryId()
			});

		Assert.assertNull(
			_dlAppLocalService.fetchFileEntry(fileEntry2.getFileEntryId()));
	}

	private void _updateKBArticle(
			KBArticle kbArticle, long[] removeFileEntryIds)
		throws Exception {

		_kbArticleService.updateKBArticle(
			kbArticle.getResourcePrimKey(), kbArticle.getTitle(),
			kbArticle.getContent(), kbArticle.getDescription(), null, null,
			kbArticle.getDisplayDate(), null, null, null, removeFileEntryIds,
			_serviceContext);
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject
	private KBArticleService _kbArticleService;

	private long _kbFolderClassNameId;

	@DeleteAfterTestRun
	private Role _regularRole;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _siteMemberUser;

	private String _testPortletId;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}