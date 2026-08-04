/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_6_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionCProductExternalReferenceCodeUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			"update CPDefinition set CProductExternalReferenceCode = (select " +
				"CProduct.externalReferenceCode from CProduct where " +
					"CProduct.CProductId = CPDefinition.CProductId)");
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CPDefinition", "CProductExternalReferenceCode VARCHAR(75)")
		};
	}

}