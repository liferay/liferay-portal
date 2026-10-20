/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.document.library.kernel.exception.FileEntryLockException;
import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderServiceUtil;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.lock.NoSuchLockException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;

import java.io.InputStream;

import java.util.Collections;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mika Koivisto
 */
@RunWith(Arquillian.class)
public class DLCheckInCheckOutTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		RoleTestUtil.addResourcePermission(
			RoleConstants.POWER_USER, DLFolderConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.ADD_DOCUMENT);

		RoleTestUtil.addResourcePermission(
			RoleConstants.GUEST, DLConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			ActionKeys.VIEW);

		_authorUser = UserTestUtil.addUser("author", _group.getGroupId());
		_overriderUser = UserTestUtil.addUser("overrider", _group.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), 0);

		_folder = createFolder("CheckInCheckOutTest");

		_fileEntry = createFileEntry(_FILE_NAME);
	}

	@After
	public void tearDown() throws Exception {
		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, DLConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			ActionKeys.VIEW);
	}

	@Test
	public void testAdminCancelCheckOut() throws Exception {
		FileEntry fileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				TestPropsValues.getUser())) {

			DLAppServiceUtil.cancelCheckOut(fileEntry.getFileEntryId());

			fileEntry = DLAppServiceUtil.getFileEntry(
				fileEntry.getFileEntryId());

			Assert.assertFalse(fileEntry.isCheckedOut());
		}
	}

	@Test(expected = FileEntryLockException.MustOwnLock.class)
	public void testAdminOverrideCheckout() throws Exception {
		FileEntry fileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				TestPropsValues.getUser())) {

			DLAppServiceUtil.checkInFileEntry(
				fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
				StringPool.NULL, _serviceContext);
		}
	}

	@Test(expected = FileEntryLockException.MustOwnLock.class)
	public void testAdminUpdateCheckedOutFile() throws Exception {
		FileEntry fileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				TestPropsValues.getUser())) {

			updateFileEntry(fileEntry.getFileEntryId());
		}
	}

	@Test
	public void testCancelCheckIn() throws Exception {
		DLAppServiceUtil.checkOutFileEntry(
			_fileEntry.getFileEntryId(), _serviceContext);

		Folder folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Date lastPostDate = folder.getLastPostDate();

		FileEntry fileEntry = DLAppServiceUtil.getFileEntry(
			_fileEntry.getFileEntryId());

		FileVersion fileVersion = fileEntry.getLatestFileVersion();

		Assert.assertEquals("PWC", fileVersion.getVersion());

		getAssetEntry(fileVersion.getFileVersionId(), true);

		DLAppServiceUtil.cancelCheckOut(_fileEntry.getFileEntryId());

		folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		DateTestUtil.assertEquals(lastPostDate, folder.getLastPostDate());

		fileEntry = DLAppServiceUtil.getFileEntry(_fileEntry.getFileEntryId());

		Assert.assertEquals("1.0", fileEntry.getVersion());
	}

	@Test
	public void testCancelCheckOut() throws Exception {
		FileEntry fileEntry = createFileEntry(StringUtil.randomString());

		DLAppServiceUtil.checkOutFileEntry(
			fileEntry.getFileEntryId(), _serviceContext);

		DLAppServiceUtil.cancelCheckOut(fileEntry.getFileEntryId());

		fileEntry = DLAppServiceUtil.getFileEntry(fileEntry.getFileEntryId());

		Assert.assertFalse(fileEntry.isCheckedOut());
	}

	@Test
	public void testCancelCheckOutVersion() throws Exception {
		FileEntry fileEntry = createFileEntry(StringUtil.randomString());

		DLAppServiceUtil.checkOutFileEntry(
			fileEntry.getFileEntryId(), _serviceContext);

		DLAppServiceUtil.cancelCheckOut(fileEntry.getFileEntryId());

		fileEntry = DLAppServiceUtil.getFileEntry(fileEntry.getFileEntryId());

		Assert.assertEquals(
			fileEntry.getVersion(), DLFileEntryConstants.VERSION_DEFAULT);
	}

	@Test
	@TestInfo("LPD-110152")
	public void testCancelCheckOutWhenLockIsExpired() throws Exception {
		_testCancelCheckOutWhenLockIsExpired();
		_testCancelCheckOutWhenLockIsExpiredWithPermissionOverrideCheckout();
		_testCancelCheckOutWhenLockIsExpiredWithoutPermissionOverrideCheckout();
	}

	@Test
	public void testCancelCheckOutWithPermissionOverrideCheckout()
		throws Exception {

		Role role = RoleTestUtil.addRole(
			"Overrider", RoleConstants.TYPE_REGULAR,
			DLFileEntryConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.OVERRIDE_CHECKOUT);

		try {
			UserLocalServiceUtil.setRoleUsers(
				role.getRoleId(), new long[] {_overriderUser.getUserId()});

			FileEntry fileEntry = null;

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_authorUser)) {

				fileEntry = createFileEntry(StringUtil.randomString());

				DLAppServiceUtil.checkOutFileEntry(
					fileEntry.getFileEntryId(), _serviceContext);
			}

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_overriderUser)) {

				DLAppServiceUtil.cancelCheckOut(fileEntry.getFileEntryId());

				fileEntry = DLAppServiceUtil.getFileEntry(
					fileEntry.getFileEntryId());

				Assert.assertFalse(fileEntry.isCheckedOut());
			}
		}
		finally {
			RoleLocalServiceUtil.deleteRole(role.getRoleId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testCancelCheckOutWithoutPermissionOverrideCheckout()
		throws Exception {

		FileEntry fileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			DLAppServiceUtil.cancelCheckOut(fileEntry.getFileEntryId());
		}
	}

	@Test
	public void testCheckIn() throws Exception {
		for (int i = 0; i < 2; i++) {
			DLAppServiceUtil.checkOutFileEntry(
				_fileEntry.getFileEntryId(), _serviceContext);

			Folder folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

			Date lastPostDate = folder.getLastPostDate();

			FileVersion fileVersion = _fileEntry.getLatestFileVersion();

			Assert.assertEquals("PWC", fileVersion.getVersion());

			getAssetEntry(fileVersion.getFileVersionId(), true);

			if (i == 1) {
				updateFileEntry(_fileEntry.getFileEntryId());
			}

			DLAppServiceUtil.checkInFileEntry(
				_fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
				StringPool.BLANK, _serviceContext);

			folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

			Assert.assertFalse(lastPostDate.after(folder.getLastPostDate()));

			FileEntry fileEntry = DLAppServiceUtil.getFileEntry(
				_fileEntry.getFileEntryId());

			Assert.assertEquals("1." + (i + 1), fileEntry.getVersion());

			fileVersion = fileEntry.getFileVersion();

			getAssetEntry(fileVersion.getFileVersionId(), false);
		}
	}

	@Test
	@TestInfo("LPD-110152")
	public void testCheckInWhenLockIsExpired() throws Exception {
		_testCheckInWhenLockIsExpired();
		_testCheckInWhenLockIsExpiredWithoutPermissionOverrideCheckout();
	}

	@Test(expected = FileEntryLockException.MustOwnLock.class)
	public void testCheckInWithPermissionOverrideCheckout() throws Exception {
		Role role = RoleTestUtil.addRole(
			"Overrider", RoleConstants.TYPE_REGULAR,
			DLFileEntryConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.OVERRIDE_CHECKOUT);

		try {
			UserLocalServiceUtil.setRoleUsers(
				role.getRoleId(), new long[] {_overriderUser.getUserId()});

			FileEntry fileEntry = null;

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_authorUser)) {

				fileEntry = createFileEntry(StringUtil.randomString());

				DLAppServiceUtil.checkOutFileEntry(
					fileEntry.getFileEntryId(), _serviceContext);
			}

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_overriderUser)) {

				DLAppServiceUtil.checkInFileEntry(
					fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
					StringPool.NULL, _serviceContext);
			}
		}
		finally {
			RoleLocalServiceUtil.deleteRole(role.getRoleId());
		}
	}

	@Test(expected = FileEntryLockException.MustOwnLock.class)
	public void testCheckInWithoutPermissionOverrideCheckout()
		throws Exception {

		FileEntry fileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			DLAppServiceUtil.checkInFileEntry(
				fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
				StringUtil.randomString(), _serviceContext);
		}
	}

	@Test
	public void testCheckOut() throws Exception {
		Folder folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Date lastPostDate = folder.getLastPostDate();

		DLAppServiceUtil.checkOutFileEntry(
			_fileEntry.getFileEntryId(), _serviceContext);

		folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		DateTestUtil.assertEquals(lastPostDate, folder.getLastPostDate());

		FileVersion fileVersion = _fileEntry.getLatestFileVersion();

		Assert.assertEquals("PWC", fileVersion.getVersion());

		getAssetEntry(fileVersion.getFileVersionId(), true);
	}

	@Test
	public void testCheckOutOldStoreFile() throws Exception {
		_renameDLStoreFile();

		Folder folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Date lastPostDate = folder.getLastPostDate();

		DLAppServiceUtil.checkOutFileEntry(
			_fileEntry.getFileEntryId(), _serviceContext);

		folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		DateTestUtil.assertEquals(lastPostDate, folder.getLastPostDate());

		FileVersion fileVersion = _fileEntry.getLatestFileVersion();

		Assert.assertEquals("PWC", fileVersion.getVersion());

		getAssetEntry(fileVersion.getFileVersionId(), true);
	}

	@Test
	@TestInfo("LPD-110152")
	public void testCheckOutWhenLockIsExpired() throws Exception {
		Role role = RoleTestUtil.addRole(
			"Updater", RoleConstants.TYPE_REGULAR,
			DLFileEntryConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.UPDATE);

		try {
			UserLocalServiceUtil.setRoleUsers(
				role.getRoleId(), new long[] {_overriderUser.getUserId()});

			_testCheckOutWhenLockIsExpired();
			_testCheckOutWhenLockIsExpiredWithoutCheckOutOwnership();
		}
		finally {
			RoleLocalServiceUtil.deleteRole(role.getRoleId());
		}
	}

	@Test
	@TestInfo("LPD-110125")
	public void testCheckOutWithExistingDLFileVersionIdFromAnotherFileEntry()
		throws Exception {

		FileEntry otherFileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			otherFileEntry = createFileEntry(StringUtil.randomString());
		}

		DLFileEntry otherDLFileEntry =
			DLFileEntryLocalServiceUtil.getDLFileEntry(
				otherFileEntry.getFileEntryId());

		DLFileVersion originalOtherDLFileVersion =
			otherDLFileEntry.getFileVersion();

		long otherFileVersionId = originalOtherDLFileVersion.getFileVersionId();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId(), 0);

		serviceContext.setAttribute(
			"existingDLFileVersionId", otherFileVersionId);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			FileEntry fileEntry = createFileEntry(StringUtil.randomString());

			AssertUtils.assertFailure(
				NoSuchFileVersionException.class,
				"No DLFileVersion exists with the primary key " +
					otherFileVersionId,
				() -> DLAppServiceUtil.checkOutFileEntry(
					fileEntry.getFileEntryId(), serviceContext));
		}

		DLFileVersion otherDLFileVersion =
			DLFileVersionLocalServiceUtil.getDLFileVersion(otherFileVersionId);

		Assert.assertEquals("1.0", otherDLFileVersion.getVersion());
		Assert.assertEquals(
			_overriderUser.getUserId(), otherDLFileVersion.getUserId());
	}

	@Test
	@TestInfo("LPD-110147")
	public void testGetFileEntryLockWithoutViewPermission() throws Exception {
		_serviceContext.setAddGuestPermissions(false);

		FileEntry fileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);

			Assert.assertNotNull(
				DLFileEntryServiceUtil.getFileEntryLock(
					fileEntry.getFileEntryId()));
		}

		_user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertNull(
				DLFileEntryServiceUtil.getFileEntryLock(
					fileEntry.getFileEntryId()));
		}
	}

	@Test
	@TestInfo("LPD-110147")
	public void testRefreshFileEntryLock() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			FileEntry fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);

			Lock lock = LockManagerUtil.getLock(
				DLFileEntry.class.getName(), fileEntry.getFileEntryId());

			_assertExpirationDate(
				DLFileEntryImpl.LOCK_EXPIRATION_TIME,
				DLAppServiceUtil.refreshFileEntryLock(
					lock.getUuid(), lock.getCompanyId(), 0));
			_assertExpirationDate(
				DLFileEntryImpl.LOCK_EXPIRATION_TIME,
				DLAppServiceUtil.refreshFileEntryLock(
					lock.getUuid(), lock.getCompanyId(), -1));
			_assertExpirationDate(
				DLFileEntryImpl.LOCK_EXPIRATION_TIME,
				DLAppServiceUtil.refreshFileEntryLock(
					lock.getUuid(), lock.getCompanyId(), Long.MAX_VALUE));
		}
	}

	@Test(expected = NoSuchLockException.class)
	@TestInfo("LPD-110147")
	public void testRefreshFileEntryLockWithFolderLock() throws Exception {
		Lock lock = DLAppServiceUtil.lockFolder(
			_group.getGroupId(), _folder.getFolderId());

		DLFileEntryServiceUtil.refreshFileEntryLock(
			lock.getUuid(), lock.getCompanyId(), 0);
	}

	@Test
	@TestInfo("LPD-110147")
	public void testRefreshFileEntryLockWithPermissionOverrideCheckout()
		throws Exception {

		Role role = RoleTestUtil.addRole(
			"Overrider", RoleConstants.TYPE_REGULAR,
			DLFileEntryConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.OVERRIDE_CHECKOUT);

		try {
			UserLocalServiceUtil.setRoleUsers(
				role.getRoleId(), new long[] {_overriderUser.getUserId()});

			FileEntry fileEntry = null;

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_authorUser)) {

				fileEntry = createFileEntry(StringUtil.randomString());

				DLAppServiceUtil.checkOutFileEntry(
					fileEntry.getFileEntryId(), _serviceContext);
			}

			Lock lock = LockManagerUtil.getLock(
				DLFileEntry.class.getName(), fileEntry.getFileEntryId());

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_overriderUser)) {

				_assertExpirationDate(
					DLFileEntryImpl.LOCK_EXPIRATION_TIME,
					DLAppServiceUtil.refreshFileEntryLock(
						lock.getUuid(), lock.getCompanyId(), 0));
			}
		}
		finally {
			RoleLocalServiceUtil.deleteRole(role.getRoleId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	@TestInfo("LPD-110147")
	public void testRefreshFileEntryLockWithoutPermissionOverrideCheckout()
		throws Exception {

		FileEntry fileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);
		}

		Lock lock = LockManagerUtil.getLock(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			DLAppServiceUtil.refreshFileEntryLock(
				lock.getUuid(), lock.getCompanyId(), 0);
		}
	}

	@Test
	@TestInfo("LPD-110147")
	public void testRefreshFolderLock() throws Exception {
		Lock lock = DLAppServiceUtil.lockFolder(
			_group.getGroupId(), _folder.getFolderId());

		_assertExpirationDate(
			DLFolderImpl.LOCK_EXPIRATION_TIME,
			DLAppServiceUtil.refreshFolderLock(
				lock.getUuid(), lock.getCompanyId(), 0));
		_assertExpirationDate(
			DLFolderImpl.LOCK_EXPIRATION_TIME,
			DLAppServiceUtil.refreshFolderLock(
				lock.getUuid(), lock.getCompanyId(), -1));
		_assertExpirationDate(
			DLFolderImpl.LOCK_EXPIRATION_TIME,
			DLAppServiceUtil.refreshFolderLock(
				lock.getUuid(), lock.getCompanyId(), Long.MAX_VALUE));
	}

	@Test(expected = NoSuchLockException.class)
	@TestInfo("LPD-110147")
	public void testRefreshFolderLockWithFileEntryLock() throws Exception {
		FileEntry fileEntry = createFileEntry(StringUtil.randomString());

		DLAppServiceUtil.checkOutFileEntry(
			fileEntry.getFileEntryId(), _serviceContext);

		Lock lock = LockManagerUtil.getLock(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		DLFolderServiceUtil.refreshFolderLock(
			lock.getUuid(), lock.getCompanyId(), 0);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	@TestInfo("LPD-110147")
	public void testRefreshFolderLockWithoutUpdatePermission()
		throws Exception {

		Lock lock = DLAppServiceUtil.lockFolder(
			_group.getGroupId(), _folder.getFolderId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			DLAppServiceUtil.refreshFolderLock(
				lock.getUuid(), lock.getCompanyId(), 0);
		}
	}

	@Test
	public void testUpdateFileEntry() throws Exception {
		Folder folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Date lastPostDate = folder.getLastPostDate();

		FileEntry fileEntry = updateFileEntry(_fileEntry.getFileEntryId());

		folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Assert.assertFalse(lastPostDate.after(folder.getLastPostDate()));

		Assert.assertEquals("1.1", fileEntry.getVersion());

		getAssetEntry(fileEntry.getFileEntryId(), true);
	}

	@Test
	public void testUpdateFileEntry2() throws Exception {
		DLAppServiceUtil.checkOutFileEntry(
			_fileEntry.getFileEntryId(), _serviceContext);

		Folder folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Date lastPostDate = folder.getLastPostDate();

		FileEntry fileEntry = updateFileEntry(_fileEntry.getFileEntryId());

		Assert.assertEquals("1.0", fileEntry.getVersion());

		FileVersion fileVersion = fileEntry.getLatestFileVersion();

		Assert.assertEquals("PWC", fileVersion.getVersion());

		DLAppServiceUtil.checkInFileEntry(
			_fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
			StringPool.BLANK, _serviceContext);

		folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Assert.assertFalse(lastPostDate.after(folder.getLastPostDate()));

		fileEntry = DLAppServiceUtil.getFileEntry(_fileEntry.getFileEntryId());

		Assert.assertEquals("1.1", fileEntry.getVersion());

		getAssetEntry(fileVersion.getFileVersionId(), false);
	}

	@Test
	public void testUpdateFileEntryOldStoreFile() throws Exception {
		_renameDLStoreFile();

		DLAppServiceUtil.checkOutFileEntry(
			_fileEntry.getFileEntryId(), _serviceContext);

		Folder folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Date lastPostDate = folder.getLastPostDate();

		FileEntry fileEntry = updateFileEntry(_fileEntry.getFileEntryId());

		Assert.assertEquals("1.0", fileEntry.getVersion());

		FileVersion fileVersion = fileEntry.getLatestFileVersion();

		Assert.assertEquals("PWC", fileVersion.getVersion());

		DLAppServiceUtil.checkInFileEntry(
			_fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
			StringPool.BLANK, _serviceContext);

		folder = DLAppServiceUtil.getFolder(_folder.getFolderId());

		Assert.assertFalse(lastPostDate.after(folder.getLastPostDate()));

		fileEntry = DLAppServiceUtil.getFileEntry(_fileEntry.getFileEntryId());

		Assert.assertEquals("1.1", fileEntry.getVersion());

		getAssetEntry(fileVersion.getFileVersionId(), false);
	}

	@Test
	@TestInfo("LPD-110152")
	public void testUpdateFileEntryWhenLockIsExpired() throws Exception {
		Role role = RoleTestUtil.addRole(
			"Updater", RoleConstants.TYPE_REGULAR,
			DLFileEntryConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.UPDATE);

		try {
			UserLocalServiceUtil.setRoleUsers(
				role.getRoleId(), new long[] {_overriderUser.getUserId()});

			FileEntry fileEntry = _addCheckedOutFileEntryWithExpiredLock();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_overriderUser)) {

				Assert.assertThrows(
					FileEntryLockException.MustOwnLock.class,
					() -> updateFileEntry(
						fileEntry.getFileEntryId(), fileEntry.getFileName()));
			}

			DLFileVersion dlFileVersion =
				DLFileVersionLocalServiceUtil.getLatestFileVersion(
					fileEntry.getFileEntryId(), false);

			Assert.assertEquals(
				_authorUser.getUserId(), dlFileVersion.getUserId());

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_authorUser)) {

				updateFileEntry(
					fileEntry.getFileEntryId(), fileEntry.getFileName());

				DLAppServiceUtil.checkInFileEntry(
					fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
					StringUtil.randomString(), _serviceContext);
			}

			Assert.assertFalse(
				DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
					fileEntry.getFileEntryId()));
		}
		finally {
			RoleLocalServiceUtil.deleteRole(role.getRoleId());
		}
	}

	@Test
	@TestInfo("LPD-110152")
	public void testUpdateFolderWhenLockIsExpired() throws Exception {
		FileEntry fileEntry = _addCheckedOutFileEntryWithExpiredLock();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DLFileEntryMetadata.class.getName());

		DLFileEntryType dlFileEntryType =
			DLFileEntryTypeServiceUtil.addFileEntryType(
				null, _group.getGroupId(), ddmStructure.getStructureId(), null,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute(
			"defaultFileEntryTypeId", dlFileEntryType.getFileEntryTypeId());
		serviceContext.setAttribute(
			"dlFileEntryTypesSearchContainerPrimaryKeys",
			String.valueOf(dlFileEntryType.getFileEntryTypeId()));
		serviceContext.setAttribute(
			"restrictionType",
			DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW);

		DLAppServiceUtil.updateFolder(
			_folder.getFolderId(), _folder.getName(), _folder.getDescription(),
			serviceContext);

		DLFileVersion dlFileVersion =
			DLFileVersionLocalServiceUtil.getLatestFileVersion(
				fileEntry.getFileEntryId(), false);

		Assert.assertEquals(
			dlFileEntryType.getFileEntryTypeId(),
			dlFileVersion.getFileEntryTypeId());
		Assert.assertEquals(_authorUser.getUserId(), dlFileVersion.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			DLAppServiceUtil.checkInFileEntry(
				fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
				StringUtil.randomString(), _serviceContext);
		}

		Assert.assertFalse(
			DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
				fileEntry.getFileEntryId()));
	}

	@Test(expected = FileEntryLockException.MustOwnLock.class)
	public void testUpdateWithPermissionOverrideCheckout() throws Exception {
		Role role = RoleTestUtil.addRole(
			"Overrider", RoleConstants.TYPE_REGULAR,
			DLFileEntryConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.OVERRIDE_CHECKOUT);

		RoleTestUtil.addResourcePermission(
			role, DLFileEntryConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.UPDATE);

		try {
			UserLocalServiceUtil.setRoleUsers(
				role.getRoleId(), new long[] {_overriderUser.getUserId()});

			FileEntry fileEntry = null;

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_authorUser)) {

				fileEntry = createFileEntry(StringUtil.randomString());

				DLAppServiceUtil.checkOutFileEntry(
					fileEntry.getFileEntryId(), _serviceContext);
			}

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_overriderUser)) {

				updateFileEntry(fileEntry.getFileEntryId());
			}
		}
		finally {
			RoleLocalServiceUtil.deleteRole(role.getRoleId());
		}
	}

	protected FileEntry createFileEntry(String fileName) throws Exception {
		long repositoryId = _group.getGroupId();

		InputStream inputStream = new UnsyncByteArrayInputStream(
			_TEST_CONTENT.getBytes());

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, repositoryId, _folder.getFolderId(), fileName,
			ContentTypes.TEXT_PLAIN, fileName, null, null, null, inputStream,
			_TEST_CONTENT.length(), null, null, null, _serviceContext);

		Assert.assertNotNull(fileEntry);

		Assert.assertEquals("1.0", fileEntry.getVersion());

		Assert.assertNotNull(getAssetEntry(fileEntry.getFileEntryId(), true));

		return fileEntry;
	}

	protected Folder createFolder(String folderName) throws Exception {
		long repositoryId = _group.getGroupId();

		Folder folder = DLAppServiceUtil.addFolder(
			null, repositoryId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			folderName, StringPool.BLANK, _serviceContext);

		Assert.assertNotNull(folder);

		folder = DLAppServiceUtil.getFolder(folder.getFolderId());

		Assert.assertNotNull(folder);

		return folder;
	}

	protected AssetEntry getAssetEntry(long assetClassPK, boolean expectExists)
		throws Exception {

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			DLFileEntryConstants.getClassName(), assetClassPK);

		if (expectExists) {
			Assert.assertNotNull(assetEntry);
		}
		else {
			Assert.assertNull(assetEntry);
		}

		return assetEntry;
	}

	protected FileEntry updateFileEntry(long fileEntryId) throws Exception {
		return updateFileEntry(fileEntryId, _FILE_NAME);
	}

	protected FileEntry updateFileEntry(long fileEntryId, String fileName)
		throws Exception {

		String content = _TEST_CONTENT + "\n" + System.currentTimeMillis();

		InputStream inputStream = new UnsyncByteArrayInputStream(
			content.getBytes());

		return DLAppServiceUtil.updateFileEntry(
			fileEntryId, fileName, ContentTypes.TEXT_PLAIN, fileName, null,
			null, null, DLVersionNumberIncrease.MINOR, inputStream,
			content.length(), null, null, null, _serviceContext);
	}

	private FileEntry _addCheckedOutFileEntryWithExpiredLock()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			FileEntry fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);

			LockManagerUtil.unlock(
				DLFileEntry.class.getName(), fileEntry.getFileEntryId());

			Assert.assertTrue(
				DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
					fileEntry.getFileEntryId()));

			return fileEntry;
		}
	}

	private void _assertExpirationDate(long expirationTime, Lock lock) {
		Date expirationDate = lock.getExpirationDate();

		Assert.assertTrue(expirationDate.after(new Date()));
		Assert.assertFalse(
			expirationDate.after(
				new Date(System.currentTimeMillis() + expirationTime)));
	}

	private void _renameDLStoreFile() throws Exception {
		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(
			_fileEntry.getFileEntryId());

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		DLStoreUtil.deleteFile(
			dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
			dlFileEntry.getName(), dlFileVersion.getStoreFileName());
		DLStoreUtil.updateFile(
			DLStoreRequest.builder(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName()
			).versionLabel(
				dlFileVersion.getVersion()
			).build(),
			new UnsyncByteArrayInputStream(_TEST_CONTENT.getBytes()));
	}

	private void _testCancelCheckOutWhenLockIsExpired() throws Exception {
		FileEntry fileEntry = _addCheckedOutFileEntryWithExpiredLock();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			DLAppServiceUtil.cancelCheckOut(fileEntry.getFileEntryId());
		}

		Assert.assertFalse(
			DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
				fileEntry.getFileEntryId()));
	}

	private void _testCancelCheckOutWhenLockIsExpiredWithPermissionOverrideCheckout()
		throws Exception {

		Role role = RoleTestUtil.addRole(
			"Overrider", RoleConstants.TYPE_REGULAR,
			DLFileEntryConstants.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			ActionKeys.OVERRIDE_CHECKOUT);

		try {
			UserLocalServiceUtil.setRoleUsers(
				role.getRoleId(), new long[] {_overriderUser.getUserId()});

			FileEntry fileEntry = _addCheckedOutFileEntryWithExpiredLock();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_overriderUser)) {

				DLAppServiceUtil.cancelCheckOut(fileEntry.getFileEntryId());
			}

			Assert.assertFalse(
				DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
					fileEntry.getFileEntryId()));
		}
		finally {
			RoleLocalServiceUtil.deleteRole(role.getRoleId());
		}
	}

	private void _testCancelCheckOutWhenLockIsExpiredWithoutPermissionOverrideCheckout()
		throws Exception {

		FileEntry fileEntry = _addCheckedOutFileEntryWithExpiredLock();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> DLAppServiceUtil.cancelCheckOut(
					fileEntry.getFileEntryId()));
		}

		Assert.assertTrue(
			DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
				fileEntry.getFileEntryId()));
	}

	private void _testCheckInWhenLockIsExpired() throws Exception {
		FileEntry fileEntry1 = _addCheckedOutFileEntryWithExpiredLock();
		FileEntry fileEntry2 = _addCheckedOutFileEntryWithExpiredLock();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			DLAppServiceUtil.checkInFileEntry(
				fileEntry1.getFileEntryId(), DLVersionNumberIncrease.MINOR,
				StringUtil.randomString(), _serviceContext);
			DLAppServiceUtil.checkInFileEntry(
				fileEntry2.getFileEntryId(), StringUtil.randomString(),
				_serviceContext);
		}

		Assert.assertFalse(
			DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
				fileEntry1.getFileEntryId()));
		Assert.assertFalse(
			DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
				fileEntry2.getFileEntryId()));
	}

	private void _testCheckInWhenLockIsExpiredWithoutPermissionOverrideCheckout()
		throws Exception {

		FileEntry fileEntry = _addCheckedOutFileEntryWithExpiredLock();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			Assert.assertThrows(
				FileEntryLockException.MustOwnLock.class,
				() -> DLAppServiceUtil.checkInFileEntry(
					fileEntry.getFileEntryId(), DLVersionNumberIncrease.MINOR,
					StringUtil.randomString(), _serviceContext));
			Assert.assertThrows(
				FileEntryLockException.MustOwnLock.class,
				() -> DLAppServiceUtil.checkInFileEntry(
					fileEntry.getFileEntryId(), StringUtil.randomString(),
					_serviceContext));
		}

		Assert.assertTrue(
			DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
				fileEntry.getFileEntryId()));
	}

	private void _testCheckOutWhenLockIsExpired() throws Exception {
		FileEntry fileEntry = null;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_authorUser)) {

			fileEntry = createFileEntry(StringUtil.randomString());

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), null, 1, _serviceContext);
		}

		Thread.sleep(100);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			Assert.assertNull(
				LockManagerUtil.fetchLock(
					DLFileEntry.class.getName(), fileEntry.getFileEntryId()));

			Assert.assertFalse(
				DLFileEntryLocalServiceUtil.isFileEntryCheckedOut(
					fileEntry.getFileEntryId()));

			DLAppServiceUtil.checkOutFileEntry(
				fileEntry.getFileEntryId(), _serviceContext);
		}

		DLFileVersion dlFileVersion =
			DLFileVersionLocalServiceUtil.getLatestFileVersion(
				fileEntry.getFileEntryId(), false);

		Assert.assertEquals(
			_overriderUser.getUserId(), dlFileVersion.getUserId());
	}

	private void _testCheckOutWhenLockIsExpiredWithoutCheckOutOwnership()
		throws Exception {

		FileEntry fileEntry = _addCheckedOutFileEntryWithExpiredLock();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_overriderUser)) {

			Assert.assertThrows(
				FileEntryLockException.MustOwnLock.class,
				() -> DLAppServiceUtil.checkOutFileEntry(
					fileEntry.getFileEntryId(), _serviceContext));
		}

		Assert.assertNull(
			LockManagerUtil.fetchLock(
				DLFileEntry.class.getName(), fileEntry.getFileEntryId()));

		DLFileVersion dlFileVersion =
			DLFileVersionLocalServiceUtil.getLatestFileVersion(
				fileEntry.getFileEntryId(), false);

		Assert.assertEquals(_authorUser.getUserId(), dlFileVersion.getUserId());
	}

	private static final String _FILE_NAME = "test1.txt";

	private static final String _TEST_CONTENT =
		"LIFERAY\nEnterprise. Open Source. For Life.";

	@DeleteAfterTestRun
	private User _authorUser;

	private FileEntry _fileEntry;
	private Folder _folder;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _overriderUser;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}