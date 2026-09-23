/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.upgrade.v4_4_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.internal.messaging.TestDispatchTaskExecutor;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.repository.DispatchFileRepository;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DispatchRepositoryResourcePermissionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.addUser(company);

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, user.getUserId(),
				TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST, null,
				RandomTestUtil.randomString(), false);

		byte[] bytes = RandomTestUtil.randomBytes();

		FileEntry fileEntry = _dispatchFileRepository.addFileEntry(
			user.getUserId(), dispatchTrigger.getDispatchTriggerId(),
			StringUtil.randomString() + ".zip", bytes.length,
			ContentTypes.APPLICATION_ZIP, new ByteArrayInputStream(bytes));

		Role guestRole = _roleLocalService.getRole(
			company.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			company.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), guestRole.getRoleId(),
			new String[] {ActionKeys.DOWNLOAD, ActionKeys.VIEW});

		DLFolder dlFolder = _dlFolderLocalService.getFolder(
			fileEntry.getFolderId());

		_resourcePermissionLocalService.setResourcePermissions(
			company.getCompanyId(), DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(dlFolder.getFolderId()), guestRole.getRoleId(),
			new String[] {ActionKeys.VIEW});
		_resourcePermissionLocalService.setResourcePermissions(
			company.getCompanyId(), DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(dlFolder.getParentFolderId()), guestRole.getRoleId(),
			new String[] {ActionKeys.VIEW});

		Role ownerRole = _roleLocalService.getRole(
			company.getCompanyId(), RoleConstants.OWNER);

		_updateResourcePermission(
			company.getCompanyId(), dlFolder.getFolderId(), ownerRole,
			user.getUserId());
		_updateResourcePermission(
			company.getCompanyId(), dlFolder.getParentFolderId(), ownerRole,
			_userLocalService.getGuestUserId(company.getCompanyId()));

		Role userRole = _roleLocalService.getRole(
			company.getCompanyId(), RoleConstants.USER);

		_resourcePermissionLocalService.setResourcePermissions(
			company.getCompanyId(), DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(dlFolder.getFolderId()), userRole.getRoleId(),
			new String[] {ActionKeys.ADD_DOCUMENT, ActionKeys.VIEW});
		_resourcePermissionLocalService.setResourcePermissions(
			company.getCompanyId(), DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(dlFolder.getParentFolderId()), userRole.getRoleId(),
			new String[] {ActionKeys.ADD_DOCUMENT, ActionKeys.VIEW});
		_resourcePermissionLocalService.setResourcePermissions(
			company.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), userRole.getRoleId(),
			new String[] {ActionKeys.DOWNLOAD, ActionKeys.VIEW});

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				guestRole.getRoleId(), ActionKeys.DOWNLOAD));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getFolderId()), ownerRole.getRoleId(),
				ActionKeys.ADD_DOCUMENT));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getParentFolderId()),
				ownerRole.getRoleId(), ActionKeys.VIEW));

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				guestRole.getRoleId(), ActionKeys.DOWNLOAD));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				guestRole.getRoleId(), ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				userRole.getRoleId(), ActionKeys.DOWNLOAD));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				userRole.getRoleId(), ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getFolderId()), guestRole.getRoleId(),
				ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getFolderId()), ownerRole.getRoleId(),
				ActionKeys.ADD_DOCUMENT));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getFolderId()), ownerRole.getRoleId(),
				ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getFolderId()), userRole.getRoleId(),
				ActionKeys.ADD_DOCUMENT));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getParentFolderId()),
				guestRole.getRoleId(), ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getParentFolderId()),
				ownerRole.getRoleId(), ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				company.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getParentFolderId()),
				userRole.getRoleId(), ActionKeys.ADD_DOCUMENT));
	}

	private void _updateResourcePermission(
			long companyId, long folderId, Role ownerRole, long ownerId)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(folderId),
			ownerRole.getRoleId(),
			new String[] {ActionKeys.ADD_DOCUMENT, ActionKeys.VIEW});

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				companyId, DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(folderId),
				ownerRole.getRoleId());

		resourcePermission.setOwnerId(ownerId);

		_resourcePermissionLocalService.updateResourcePermission(
			resourcePermission);
	}

	private static final String _CLASS_NAME =
		"com.liferay.dispatch.internal.upgrade.v4_4_0." +
			"DispatchRepositoryResourcePermissionUpgradeProcess";

	@Inject
	private DispatchFileRepository _dispatchFileRepository;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.dispatch.internal.upgrade.registry.DispatchServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private UserLocalService _userLocalService;

}