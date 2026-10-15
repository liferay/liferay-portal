/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.internal.upgrade.v1_1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.type.virtual.model.impl.CPDefinitionVirtualSettingImpl;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionVirtualSettingUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (hasColumn(
				CPDefinitionVirtualSettingImpl.TABLE_NAME, "classNameId")) {

			String template = StringUtil.read(
				CPDefinitionVirtualSettingUpgradeProcess.class.
					getResourceAsStream(
						"dependencies/CPDefinitionVirtualSetting.sql"));

			long classNameId = ClassNameLocalServiceUtil.getClassNameId(
				CPDefinition.class.getName());

			template = StringUtil.replace(
				template, "(?)", "\'" + classNameId + "\'");

			runSQLTemplate(template, false);
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CPDefinitionVirtualSetting", "classNameId LONG",
				"override BOOLEAN"),
			UpgradeProcessFactory.alterColumnName(
				"CPDefinitionVirtualSetting", "CPDefinitionId", "classPK")
		};
	}

}