/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.upgrade.v2_2_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Mariano Álvaro Sáiz
 */
public class AssetEntrySAPEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasTable("SAPEntry")) {
			return;
		}

		runSQL(
			StringBundler.concat(
				"update SAPEntry set allowedServiceSignatures = ",
				"'com.liferay.asset.kernel.service.",
				"AssetEntryService#incrementViewCounter",
				"(long,java.lang.String,long)' where name = ",
				"'ASSET_ENTRY_DEFAULT' and allowedServiceSignatures = ",
				"'com.liferay.asset.kernel.service.",
				"AssetEntryService#incrementViewCounter'"));
	}

}