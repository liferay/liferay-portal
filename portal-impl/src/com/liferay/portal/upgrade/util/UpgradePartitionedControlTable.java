/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.util;

import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Sofía Mendoza Gutiérrez
 */
public class UpgradePartitionedControlTable extends UpgradeProcess {

	public UpgradePartitionedControlTable(String tableName) {
		_tableName = tableName;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (CompanyThreadLocal.getCompanyId() !=
				PortalInstancePool.getDefaultCompanyId()) {

			return;
		}

		for (long companyId : PortalInstancePool.getCompanyIds()) {
			DBPartitionUtil.replaceByTable(
				connection, companyId, _tableName, true);
		}
	}

	@Override
	protected boolean isSkipUpgradeProcess() {
		return !PropsValues.DATABASE_PARTITION_ENABLED;
	}

	private final String _tableName;

}