/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;

/**
 * @author Stefano Motta
 */
public class RoleExternalReferenceCodeUpgradeProcess
	extends BaseExternalReferenceCodeUpgradeProcess {

	@Override
	protected String[] getTableNames() {
		return new String[] {"Role_"};
	}

}