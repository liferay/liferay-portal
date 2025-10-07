/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_4_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Alberto Sousa
 */
public class KaleoDefinitionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			StringBundler.concat(
				"update KaleoDefinition set status = ",
				WorkflowConstants.STATUS_APPROVED,
				" where active_ = [$TRUE$]"));
		runSQL(
			StringBundler.concat(
				"update KaleoDefinition set status = ",
				WorkflowConstants.STATUS_DRAFT, " where active_ = [$FALSE$]"));
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"KaleoDefinition", "status INTEGER")
		};
	}

}