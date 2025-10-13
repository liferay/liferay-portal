/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Luis Ortiz
 */
public class GroupDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(
			new DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
				"groupId", "Group_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				"[$SOURCE_TABLE_ALIAS$].ownerType = " +
					PortletKeys.PREFS_OWNER_TYPE_GROUP,
				"ownerId", "PortalPreferences", "groupId", "Group_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				"[$SOURCE_TABLE_ALIAS$].ownerType = " +
					PortletKeys.PREFS_OWNER_TYPE_GROUP,
				"ownerId", "PortletPreferences", "groupId", "Group_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				"[$SOURCE_TABLE_ALIAS$].scope = " +
					ResourceConstants.SCOPE_GROUP,
				"primKeyId", "ResourcePermission", "groupId", "Group_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].scope = ",
					ResourceConstants.SCOPE_INDIVIDUAL, " and ",
					"[$SOURCE_TABLE_ALIAS$].name = '", Group.class.getName(),
					"'"),
				"primKeyId", "ResourcePermission", "groupId", "Group_"));
	}

}