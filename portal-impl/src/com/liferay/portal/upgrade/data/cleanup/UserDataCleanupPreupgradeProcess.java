/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.UserAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;

/**
 * @author Luis Ortiz
 */
public class UserDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(
			new UserAllTablesOrphanReferencesDataCleanupPreupgradeProcess());
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].classNameId = (select classNameId ",
					"from ClassName_ where value = '", User.class.getName(),
					"')"),
				"classPK", "AssetEntry", "userId", "User_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].classNameId = (select classNameId ",
					"from ClassName_ where value = '", User.class.getName(),
					"')"),
				"classPK", "Contact_", "userId", "User_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].classNameId = (select classNameId ",
					"from ClassName_ where value = '", User.class.getName(),
					"')"),
				"classPK", "Group_", "userId", "User_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				"[$SOURCE_TABLE_ALIAS$].ownerType = " +
					PortletKeys.PREFS_OWNER_TYPE_USER,
				"ownerId", "PortalPreferences", "userId", "User_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				"[$SOURCE_TABLE_ALIAS$].ownerType = " +
					PortletKeys.PREFS_OWNER_TYPE_USER,
				"ownerId", "PortletPreferences", "userId", "User_"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					"[$SOURCE_TABLE_ALIAS$].scope = ",
					ResourceConstants.SCOPE_INDIVIDUAL, " and ",
					"[$SOURCE_TABLE_ALIAS$].name = '", User.class.getName(),
					"'"),
				"primKeyId", "ResourcePermission", "userId", "User_"));
	}

}