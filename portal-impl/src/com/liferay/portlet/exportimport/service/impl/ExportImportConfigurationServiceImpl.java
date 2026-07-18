/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.exportimport.service.impl;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portlet.exportimport.service.base.ExportImportConfigurationServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 * @author Levente Hudák
 */
public class ExportImportConfigurationServiceImpl
	extends ExportImportConfigurationServiceBaseImpl {

	@Override
	public void deleteExportImportConfiguration(
			long exportImportConfigurationId)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		GroupPermissionUtil.check(
			getPermissionChecker(), exportImportConfiguration.getGroupId(),
			ActionKeys.DELETE);

		exportImportConfigurationLocalService.deleteExportImportConfiguration(
			exportImportConfiguration);
	}

	@Override
	public ExportImportConfiguration moveExportImportConfigurationToTrash(
			long exportImportConfigurationId)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		GroupPermissionUtil.check(
			getPermissionChecker(), exportImportConfiguration.getGroupId(),
			ActionKeys.DELETE);

		return exportImportConfigurationLocalService.
			moveExportImportConfigurationToTrash(
				getUserId(), exportImportConfigurationId);
	}

	@Override
	public ExportImportConfiguration restoreExportImportConfigurationFromTrash(
			long exportImportConfigurationId)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		GroupPermissionUtil.check(
			getPermissionChecker(), exportImportConfiguration.getGroupId(),
			ActionKeys.DELETE);

		return exportImportConfigurationLocalService.
			restoreExportImportConfigurationFromTrash(
				getUserId(), exportImportConfigurationId);
	}

}