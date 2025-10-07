/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_24_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Mario Gomes
 */
public class ObjectDefinitionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			"update ObjectDefinition set enableFormContainer = [$TRUE$] " +
				"where not (system_ = [$TRUE$] and modifiable = [$FALSE$])");
		runSQL(
			"update ObjectDefinition set enableFormContainer = [$FALSE$] " +
				"where (system_ = [$TRUE$] and modifiable = [$FALSE$])");
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"ObjectDefinition", "enableFormContainer BOOLEAN")
		};
	}

}