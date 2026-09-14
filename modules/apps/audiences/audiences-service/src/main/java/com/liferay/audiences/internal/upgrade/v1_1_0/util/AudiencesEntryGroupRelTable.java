/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.internal.upgrade.v1_1_0.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Víctor Galán
 * @generated
 * @see com.liferay.portal.tools.upgrade.table.builder.UpgradeTableBuilder
 */
public class AudiencesEntryGroupRelTable {

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

	private static final String _TABLE_NAME = "AudiencesEntryGroupRel";

	private static final String _TABLE_SQL_CREATE =
		"create table AudiencesEntryGroupRel (mvccVersion LONG default 0 not null,audiencesEntryGroupRelId LONG not null primary key,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,audienceEntryERC VARCHAR(75) null,groupERC VARCHAR(75) null)";

}