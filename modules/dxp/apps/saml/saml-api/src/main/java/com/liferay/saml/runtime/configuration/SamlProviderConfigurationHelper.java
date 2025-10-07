/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.runtime.configuration;

import com.liferay.portal.kernel.util.UnicodeProperties;

/**
 * @author Mika Koivisto
 */
public interface SamlProviderConfigurationHelper {

	public SamlProviderConfiguration getSamlProviderConfiguration();

	public boolean isEnabled();

	public boolean isLDAPImportEnabled();

	public boolean isRoleIb();

	public boolean isRoleIdp();

	public boolean isRoleSp();

	public void updateProperties(UnicodeProperties unicodeProperties)
		throws Exception;

}