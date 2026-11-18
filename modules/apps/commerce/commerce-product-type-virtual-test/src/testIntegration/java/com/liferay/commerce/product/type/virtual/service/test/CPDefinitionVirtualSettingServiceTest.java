/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPDefinitionVirtualSettingServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testAddCPDefinitionVirtualSetting() throws Exception {
		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog.getCommerceCatalogId()),
			_role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		FileEntry fileEntry1 = _addFileEntry(commerceCatalog);

		FileEntry fileEntry2 = _addFileEntry(GroupTestUtil.addGroup());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_assertMustHavePermission(
				fileEntry2,
				() -> _addCPDefinitionVirtualSetting(
					cpDefinition, 0, fileEntry2.getFileEntryId(), true));
			_assertMustHavePermission(
				fileEntry2,
				() -> _addCPDefinitionVirtualSetting(
					cpDefinition, fileEntry2.getFileEntryId(), 0, false));

			CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
				_addCPDefinitionVirtualSetting(
					cpDefinition, fileEntry1.getFileEntryId(),
					fileEntry1.getFileEntryId(), true);

			_assertFileEntry(cpDefinitionVirtualSetting, fileEntry1);
		}
	}

	@Test
	public void testUpdateCPDefinitionVirtualSetting() throws Exception {
		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog.getCommerceCatalogId()),
			_role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		FileEntry fileEntry1 = _addFileEntry(commerceCatalog);

		FileEntry fileEntry2 = _addFileEntry(GroupTestUtil.addGroup());

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingLocalService.
				addCPDefinitionVirtualSetting(
					CPDefinition.class.getName(),
					cpDefinition.getCPDefinitionId(), 0, null,
					RandomTestUtil.randomInt(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomInt(), false, 0, null, false, null, 0,
					false,
					ServiceContextTestUtil.getServiceContext(
						commerceCatalog.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_assertMustHavePermission(
				fileEntry2,
				() ->
					_cpDefinitionVirtualSettingService.
						updateCPDefinitionVirtualSetting(
							cpDefinitionVirtualSetting.
								getCPDefinitionVirtualSettingId(),
							0, null, 0, 0, 0, true, fileEntry2.getFileEntryId(),
							null, false, null, 0,
							_getServiceContext(cpDefinition)));
			_assertMustHavePermission(
				fileEntry2,
				() ->
					_cpDefinitionVirtualSettingService.
						updateCPDefinitionVirtualSetting(
							cpDefinitionVirtualSetting.
								getCPDefinitionVirtualSettingId(),
							0, null, 0, 0, 0, true, fileEntry2.getFileEntryId(),
							null, false, null, 0, false,
							_getServiceContext(cpDefinition)));
			_assertMustHavePermission(
				fileEntry2,
				() ->
					_cpDefinitionVirtualSettingService.
						updateCPDefinitionVirtualSetting(
							cpDefinitionVirtualSetting.
								getCPDefinitionVirtualSettingId(),
							fileEntry2.getFileEntryId(), null, 0, 0, 0, false,
							0, null, false, null, 0,
							_getServiceContext(cpDefinition)));
			_assertMustHavePermission(
				fileEntry2,
				() ->
					_cpDefinitionVirtualSettingService.
						updateCPDefinitionVirtualSetting(
							cpDefinitionVirtualSetting.
								getCPDefinitionVirtualSettingId(),
							fileEntry2.getFileEntryId(), null, 0, 0, 0, false,
							0, null, false, null, 0, false,
							_getServiceContext(cpDefinition)));

			CPDefinitionVirtualSetting updatedCPDefinitionVirtualSetting =
				_cpDefinitionVirtualSettingService.
					updateCPDefinitionVirtualSetting(
						cpDefinitionVirtualSetting.
							getCPDefinitionVirtualSettingId(),
						fileEntry1.getFileEntryId(), null, 0, 0, 0, true,
						fileEntry1.getFileEntryId(), null, false, null, 0,
						false, _getServiceContext(cpDefinition));

			_assertFileEntry(updatedCPDefinitionVirtualSetting, fileEntry1);
		}
	}

	private CPDefinitionVirtualSetting _addCPDefinitionVirtualSetting(
			CPDefinition cpDefinition, long fileEntryId, long sampleFileEntryId,
			boolean useSample)
		throws Exception {

		return _cpDefinitionVirtualSettingService.addCPDefinitionVirtualSetting(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			fileEntryId, null, 0, 0, 0, useSample, sampleFileEntryId, null,
			false, null, 0, false, _getServiceContext(cpDefinition));
	}

	private FileEntry _addFileEntry(CommerceCatalog commerceCatalog)
		throws Exception {

		return _cpdVirtualSettingFileEntryLocalService.addFileEntry(
			TestPropsValues.getUserId(), commerceCatalog.getGroupId(),
			CommerceCatalog.class.getName(),
			commerceCatalog.getCommerceCatalogId(),
			CPConstants.SERVICE_NAME_PRODUCT,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			new ByteArrayInputStream(RandomTestUtil.randomBytes()),
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN);
	}

	private FileEntry _addFileEntry(Group group) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setScopeGroupId(group.getGroupId());

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null, serviceContext);
	}

	private void _assertFileEntry(
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting,
		FileEntry fileEntry) {

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			cpDefinitionVirtualSetting.getSampleFileEntryId());

		List<CPDVirtualSettingFileEntry> cpdVirtualSettingFileEntries =
			cpDefinitionVirtualSetting.getCPDVirtualSettingFileEntries();

		Assert.assertEquals(
			cpdVirtualSettingFileEntries.toString(), 1,
			cpdVirtualSettingFileEntries.size());

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			cpdVirtualSettingFileEntries.get(0);

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			cpdVirtualSettingFileEntry.getFileEntryId());
	}

	private void _assertMustHavePermission(
		FileEntry fileEntry, ThrowingRunnable throwingRunnable) {

		PrincipalException.MustHavePermission mustHavePermission =
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class, throwingRunnable);

		Assert.assertArrayEquals(
			new String[] {ActionKeys.VIEW}, mustHavePermission.actionId);
		Assert.assertEquals(
			fileEntry.getFileEntryId(), mustHavePermission.resourceId);
		Assert.assertEquals(
			DLFileEntry.class.getName(), mustHavePermission.resourceName);
	}

	private ServiceContext _getServiceContext(CPDefinition cpDefinition)
		throws Exception {

		return ServiceContextTestUtil.getServiceContext(
			cpDefinition.getGroupId(), _user.getUserId());
	}

	@Inject
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Inject
	private CPDefinitionVirtualSettingService
		_cpDefinitionVirtualSettingService;

	@Inject
	private CPDVirtualSettingFileEntryLocalService
		_cpdVirtualSettingFileEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}