/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_3;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.List;

/**
 * @author Manuel de la Peña
 */
public class UpgradeOrganization extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updateOrganizationsType();
	}

	protected List<String> getOrganizationTypes() {
		List<String> organizationsTypes = ListUtil.fromArray(
			PropsUtil.getArray("organizations.types"));

		if (ListUtil.isEmpty(organizationsTypes)) {
			organizationsTypes.add("organization");
		}

		return organizationsTypes;
	}

	protected void updateOrganizationsType() throws Exception {
		String organizationTypesString = ListUtil.toString(
			getOrganizationTypes(), StringPool.NULL, "', '");

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			runSQL(
				StringBundler.concat(
					"update Organization_ set type_ = 'organization' where ",
					"type_ not in ('", organizationTypesString, "')"));
		}
	}

}