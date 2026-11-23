/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.internal.upgrade.v2_6_0.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Christopher Kian
 * @generated
 * @see com.liferay.portal.tools.upgrade.table.builder.UpgradeTableBuilder
 */
public class AuditEventAccountEntryRelTable {

	public static UpgradeProcess create() {
		return new UpgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
				if (!hasTable(_TABLE_NAME)) {
					runSQL(_TABLE_SQL_CREATE);
				}
			}

		};
	}

	private static final String _TABLE_NAME = "Audit_EventAccountEntryRel";

	private static final String _TABLE_SQL_CREATE =
		"create table Audit_EventAccountEntryRel (auditEventAccountEntryRelId LONG not null primary key,companyId LONG,createDate DATE null,accountEntryId LONG,auditEventId LONG)";

}