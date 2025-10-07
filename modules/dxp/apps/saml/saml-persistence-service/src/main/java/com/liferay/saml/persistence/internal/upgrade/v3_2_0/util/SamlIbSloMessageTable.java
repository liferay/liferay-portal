/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.internal.upgrade.v3_2_0.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Brian Wing Shun Chan
 * @generated
 * @see com.liferay.portal.tools.upgrade.table.builder.UpgradeTableBuilder
 */
public class SamlIbSloMessageTable {

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

	private static final String _TABLE_NAME = "SamlIbSloMessage";

	private static final String _TABLE_SQL_CREATE =
		"create table SamlIbSloMessage (samlIbSloMessageId LONG not null primary key,companyId LONG,createDate DATE null,logoutRequestXml TEXT null,samlIdpEntityId VARCHAR(1024) null,samlIdpSessionIndex VARCHAR(200) null)";

}