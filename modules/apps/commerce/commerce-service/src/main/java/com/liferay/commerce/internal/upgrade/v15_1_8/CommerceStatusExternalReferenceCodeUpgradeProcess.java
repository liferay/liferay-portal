/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_8;

import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Matyas Wollner
 */
public class CommerceStatusExternalReferenceCodeUpgradeProcess
	extends BaseExternalReferenceCodeUpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		super.doUpgrade();

		runSQL(
			"update CommerceOrderItem set status = " +
				WorkflowConstants.STATUS_APPROVED);
		runSQL(
			"update CommerceShippingMethod set status = " +
				WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CommerceOrderItem", "status INTEGER"),
			UpgradeProcessFactory.addColumns(
				"CommerceShippingMethod", "status INTEGER")
		};
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"CommerceShippingMethod"};
	}

}