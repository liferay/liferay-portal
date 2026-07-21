/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_5_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceProductStatusUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			"update CommerceCatalog set status = " +
				WorkflowConstants.STATUS_APPROVED);
		runSQL(
			"update CommerceChannel set status = " +
				WorkflowConstants.STATUS_APPROVED);
		runSQL(
			"update CPMeasurementUnit set status = " +
				WorkflowConstants.STATUS_APPROVED);
		runSQL(
			"update CPOption set status = " +
				WorkflowConstants.STATUS_APPROVED);
		runSQL(
			"update CPOptionCategory set status = " +
				WorkflowConstants.STATUS_APPROVED);
		runSQL(
			"update CPOptionValue set status = " +
				WorkflowConstants.STATUS_APPROVED);
		runSQL(
			"update CPSpecificationOption set status = " +
				WorkflowConstants.STATUS_APPROVED);
		runSQL(
			"update CPTaxCategory set status = " +
				WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CommerceCatalog", "status INTEGER"),
			UpgradeProcessFactory.addColumns(
				"CommerceChannel", "status INTEGER"),
			UpgradeProcessFactory.addColumns(
				"CPMeasurementUnit", "status INTEGER"),
			UpgradeProcessFactory.addColumns("CPOption", "status INTEGER"),
			UpgradeProcessFactory.addColumns(
				"CPOptionCategory", "status INTEGER"),
			UpgradeProcessFactory.addColumns("CPOptionValue", "status INTEGER"),
			UpgradeProcessFactory.addColumns(
				"CPSpecificationOption", "status INTEGER"),
			UpgradeProcessFactory.addColumns("CPTaxCategory", "status INTEGER")
		};
	}

}