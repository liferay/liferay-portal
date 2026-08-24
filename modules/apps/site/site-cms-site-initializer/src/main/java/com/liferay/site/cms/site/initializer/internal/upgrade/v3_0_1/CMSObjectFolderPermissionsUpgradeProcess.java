/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_1;

import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Víctor Galán
 */
public class CMSObjectFolderPermissionsUpgradeProcess extends UpgradeProcess {

	public CMSObjectFolderPermissionsUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectFolderLocalService objectFolderLocalService,
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_companyLocalService = companyLocalService;
		_objectFolderLocalService = objectFolderLocalService;
		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _upgradeCompany(long companyId) throws PortalException {
		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.CMS_ADMINISTRATOR);

		if (role == null) {
			return;
		}

		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS,
				companyId);

		if (objectFolder == null) {
			return;
		}

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, ObjectFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectFolder.getObjectFolderId()), role.getRoleId(),
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					ObjectFolder.class.getName()),
				ResourceAction::getActionId, String.class));
	}

	private final CompanyLocalService _companyLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;

}