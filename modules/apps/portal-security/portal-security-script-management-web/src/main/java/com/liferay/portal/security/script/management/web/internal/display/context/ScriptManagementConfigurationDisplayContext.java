/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.display.context;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;

/**
 * @author Feliphe Marinho
 */
public class ScriptManagementConfigurationDisplayContext {

	public ScriptManagementConfigurationDisplayContext(
		ScriptManagementConfigurationHelper
			scriptManagementConfigurationHelper) {

		_scriptManagementConfigurationHelper =
			scriptManagementConfigurationHelper;
	}

	public boolean isAllowScriptContentToBeExecutedOrIncluded() {
		return _scriptManagementConfigurationHelper.
			isAllowScriptContentToBeExecutedOrIncluded();
	}

	public boolean isScriptManagementConfigurationDefined()
		throws ConfigurationException {

		return _scriptManagementConfigurationHelper.
			isScriptManagementConfigurationDefined();
	}

	private final ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

}