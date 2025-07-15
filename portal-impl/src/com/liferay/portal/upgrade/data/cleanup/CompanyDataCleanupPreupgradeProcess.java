/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.portal.kernel.upgrade.data.cleanup.AllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Luis Ortiz
 */
public class CompanyDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(
			new AllTablesOrphanReferencesDataCleanupPreupgradeProcess(
				"companyId", "Company"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				"ownerId = " + PortletKeys.PREFS_OWNER_TYPE_COMPANY, "ownerId",
				"PortalPreferences", "companyId", "Company"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				"ownerId = " + PortletKeys.PREFS_OWNER_TYPE_COMPANY, "ownerId",
				"PortletPreferences", "companyId", "Company"));
	}

}