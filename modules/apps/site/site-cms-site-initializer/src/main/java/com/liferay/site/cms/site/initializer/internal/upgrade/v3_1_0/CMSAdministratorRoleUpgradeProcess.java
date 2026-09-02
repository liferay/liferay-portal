/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Mario Gomes
 */
public class CMSAdministratorRoleUpgradeProcess extends UpgradeProcess {

	public CMSAdministratorRoleUpgradeProcess(
		CompanyLocalService companyLocalService,
		RoleLocalService roleLocalService) {

		_companyLocalService = companyLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		String externalReferenceCode =
			RoleConstants.toSystemRoleExternalReferenceCode(
				RoleConstants.CMS_ADMINISTRATOR);

		_companyLocalService.forEachCompanyId(
			companyId -> {
				Role role = _roleLocalService.fetchRole(
					companyId, RoleConstants.CMS_ADMINISTRATOR);

				if ((role == null) ||
					externalReferenceCode.equals(
						role.getExternalReferenceCode())) {

					return;
				}

				Role existingRole =
					_roleLocalService.fetchRoleByExternalReferenceCode(
						externalReferenceCode, companyId);

				if (existingRole != null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to assign the external reference code ",
								"\"", externalReferenceCode, "\" to the \"",
								RoleConstants.CMS_ADMINISTRATOR,
								"\" role in company ", companyId,
								" because another role already uses it"));
					}

					return;
				}

				role.setExternalReferenceCode(externalReferenceCode);

				_roleLocalService.updateRole(role);
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSAdministratorRoleUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final RoleLocalService _roleLocalService;

}