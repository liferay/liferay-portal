/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;

/**
 * @author Luis Ortiz
 */
public class DDMStorageLinkDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, "contentId", "DDMContent", "classPK", "DDMStorageLink"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, "storageId", "DDMField", "classPK", "DDMStorageLink"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, "storageId", "DDMFieldAttribute", "classPK",
				"DDMStorageLink"));
	}

}