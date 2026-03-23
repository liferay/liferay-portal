/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.util;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;

/**
 * @author Alejandro Tardín
 */
public class EnabledUtil {

	public static void checkEnabled(Company company) {
		checkEnabled(company, false);
	}

	public static void checkEnabled(Company company, boolean privateLayout) {

		_log.error(String.format("!!! THREAD CHECK: [ID: %d] [Name: %s] [ExportActive: %b]",
			Thread.currentThread().getId(),
			Thread.currentThread().getName(),
			ExportImportThreadLocal.isExportInProcess()));

		if (LazyReferencingThreadLocal.isEnabled() ||
			ExportImportThreadLocal.isExportInProcess() ||
			ExportImportThreadLocal.isImportInProcess() ||
			ExportImportThreadLocal.isStagingInProcess()) {

			return;
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				company.getCompanyId(), "LPD-35443") ||
			(privateLayout &&
			 !FeatureFlagManagerUtil.isEnabled(
				 company.getCompanyId(), "LPD-38869"))) {

			throw new UnsupportedOperationException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EnabledUtil.class);

}