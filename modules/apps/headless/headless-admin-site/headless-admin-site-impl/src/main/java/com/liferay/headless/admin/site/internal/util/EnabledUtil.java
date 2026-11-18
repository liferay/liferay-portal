/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.util;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

/**
 * @author Alejandro Tardín
 */
public class EnabledUtil {

	public static void checkAddWidgetPageEnabled(Company company) {
		if (LazyReferencingThreadLocal.isEnabled() ||
			ExportImportThreadLocal.isExportInProcess() ||
			ExportImportThreadLocal.isImportInProcess() ||
			ExportImportThreadLocal.isStagingInProcess()) {

			return;
		}

		FeatureFlagManagerUtil.checkEnabled(
			company.getCompanyId(), "LPD-76864");
	}

	public static void checkDesignLibrariesEnabled(Company company) {
		FeatureFlagManagerUtil.checkEnabled(
			company.getCompanyId(), "LPD-57283");
	}

	public static void checkDesignLibraryMasterPagesEnabled(Company company) {
		checkDesignLibrariesEnabled(company);

		FeatureFlagManagerUtil.checkEnabled(
			company.getCompanyId(), "LPD-81022");
	}

	public static void checkPrivateLayoutEnabled(long groupId)
		throws PortalException {

		if (LazyReferencingThreadLocal.isEnabled() ||
			ExportImportThreadLocal.isExportInProcess() ||
			ExportImportThreadLocal.isImportInProcess() ||
			ExportImportThreadLocal.isStagingInProcess()) {

			return;
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (group.isLayoutSetPrototype()) {
			return;
		}

		FeatureFlagManagerUtil.checkEnabled(group.getCompanyId(), "LPD-38869");
	}

}