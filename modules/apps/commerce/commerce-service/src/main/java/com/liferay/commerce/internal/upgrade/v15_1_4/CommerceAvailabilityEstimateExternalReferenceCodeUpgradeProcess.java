/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_4;

import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceAvailabilityEstimateExternalReferenceCodeUpgradeProcess
	extends BaseExternalReferenceCodeUpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		super.doUpgrade();

		if (!hasColumn("CommerceAvailabilityEstimate", "status")) {
			alterTableAddColumn(
				"CommerceAvailabilityEstimate", "status", "INTEGER");
		}

		runSQL(
			"update CommerceAvailabilityEstimate set status = " +
				WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"CommerceAvailabilityEstimate"};
	}

}