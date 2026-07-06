/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_0_0;

import com.liferay.portal.kernel.upgrade.BaseIndexedColumnSizeUpgradeProcess;

/**
 * @author Roselaine Marques
 */
public class ObjectEntryIndexedColumnSizeUpgradeProcess
	extends BaseIndexedColumnSizeUpgradeProcess {

	@Override
	protected String getColumnName() {
		return "externalReferenceCode";
	}

	@Override
	protected String[] getGroupByColumnNames() {
		return new String[] {"companyId", "groupId", "objectDefinitionId"};
	}

	@Override
	protected int getMaxColumnLength() {
		return 500;
	}

	@Override
	protected String getTableName() {
		return "ObjectEntry";
	}

}