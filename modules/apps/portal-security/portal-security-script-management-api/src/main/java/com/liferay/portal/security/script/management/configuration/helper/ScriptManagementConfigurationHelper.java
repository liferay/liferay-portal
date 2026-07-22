/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.configuration.helper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;

/**
 * @author Feliphe Marinho
 */
public interface ScriptManagementConfigurationHelper {

	public String getScriptManagementConfigurationPortletURL()
		throws PortalException;

	public boolean isAllowScriptContentToBeExecutedOrIncluded();

	public boolean isScriptManagementConfigurationDefined()
		throws ConfigurationException;

}