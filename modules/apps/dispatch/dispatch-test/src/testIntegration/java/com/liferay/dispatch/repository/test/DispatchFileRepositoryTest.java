/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.repository.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.internal.messaging.TestDispatchTaskExecutor;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.repository.DispatchFileRepository;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
public class DispatchFileRepositoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddFileEntry() throws Exception {
		User user = UserTestUtil.addUser();

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
			fileEntry.getCompanyId(), RoleConstants.GUEST);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				fileEntry.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				guestRole.getRoleId(), ActionKeys.DOWNLOAD));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				fileEntry.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				guestRole.getRoleId(), ActionKeys.VIEW));

		Role userRole = _roleLocalService.getRole(
			fileEntry.getCompanyId(), RoleConstants.USER);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				fileEntry.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				userRole.getRoleId(), ActionKeys.DOWNLOAD));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				fileEntry.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				userRole.getRoleId(), ActionKeys.VIEW));

		DLFolder dlFolder = _dlFolderLocalService.getFolder(
			fileEntry.getFolderId());

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				fileEntry.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getFolderId()), guestRole.getRoleId(),
				ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				fileEntry.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getParentFolderId()),
				guestRole.getRoleId(), ActionKeys.VIEW));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				fileEntry.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getFolderId()), userRole.getRoleId(),
				ActionKeys.ADD_DOCUMENT));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				fileEntry.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFolder.getParentFolderId()),
				userRole.getRoleId(), ActionKeys.ADD_DOCUMENT));

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());

		Assert.assertThrows(
			PrincipalException.MustHavePermission.class,
			() -> _dlAppService.addFileEntry(
				null, dlFolder.getRepositoryId(), dlFolder.getFolderId(),
				StringUtil.randomString() + ".zip",
				ContentTypes.APPLICATION_ZIP, RandomTestUtil.randomString(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomBytes(), null, null, null,
				ServiceContextTestUtil.getServiceContext(
					dlFolder.getGroupId(), user.getUserId())));
	}

	@Inject
	private DispatchFileRepository _dispatchFileRepository;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}