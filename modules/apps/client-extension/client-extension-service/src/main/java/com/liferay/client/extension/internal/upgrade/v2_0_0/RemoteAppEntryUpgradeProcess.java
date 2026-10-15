/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.upgrade.v2_0_0;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Iván Zaera Avellón
 */
public class RemoteAppEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasColumn("RemoteAppEntry", "instanceable")) {
			alterTableAddColumn("RemoteAppEntry", "instanceable", "BOOLEAN");

			runSQL("update RemoteAppEntry set instanceable = [$TRUE$]");
		}

		if (!hasColumn("RemoteAppEntry", "portletCategoryName")) {
			alterTableAddColumn(
				"RemoteAppEntry", "portletCategoryName", "VARCHAR(75)");

			runSQL(
				"update RemoteAppEntry set portletCategoryName = " +
					"'category.remote-apps'");
		}

		alterTableAddColumn("RemoteAppEntry", "properties", "TEXT");

		if (!hasColumn("RemoteAppEntry", "type_")) {
			alterTableAddColumn("RemoteAppEntry", "type_", "VARCHAR(75)");

			runSQL(
				StringBundler.concat(
					"update RemoteAppEntry set type_ = '",
					ClientExtensionEntryConstants.TYPE_IFRAME, "'"));
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"RemoteAppEntry", "customElementCSSURLs TEXT",
				"customElementHTMLElementName VARCHAR(255)",
				"customElementURLs TEXT"),
			UpgradeProcessFactory.alterColumnName(
				"RemoteAppEntry", "url", "iFrameURL")
		};
	}

}