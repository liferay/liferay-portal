/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.upgrade.v3_0_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Brian Wing Shun Chan
 */
public class ClientExtensionEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (hasTable("RemoteAppEntry")) {
			dropTable("ClientExtensionEntry");

			alterTableName("RemoteAppEntry", "ClientExtensionEntry");
		}

		runSQL(
			"delete from ServiceComponent where buildNamespace = 'RemoteApp'");
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.alterColumnName(
				"RemoteAppEntry", "remoteAppEntryId", "clientExtensionEntryId")
		};
	}

}