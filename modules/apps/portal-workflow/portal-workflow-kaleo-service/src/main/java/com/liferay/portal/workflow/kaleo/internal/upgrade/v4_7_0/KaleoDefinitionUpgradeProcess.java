/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_7_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Feliphe Marinho
 */
public class KaleoDefinitionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL("update KaleoDefinition set system_ = [$FALSE$]");
		runSQL(
			StringBundler.concat(
				"update KaleoDefinition set system_ = [$TRUE$] where name in (",
				"'Change Tone', 'Content Gap Analysis', 'Find Matching ",
				"Assets', 'Fix Spelling and Grammar', 'Generate Content', ",
				"'Generate Field Value', 'Generate Image', 'Improve Writing', ",
				"'Liferay Search', 'Make Longer', 'Make Shorter', 'Page ",
				"Builder', 'SEO Studio Description Generator', 'SEO Studio ",
				"Title Generator', 'Translate Content')"));
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"KaleoDefinition", "system_ BOOLEAN")
		};
	}

}