/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.connector.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Katie Nesterovich
 */
@Meta.OCD(
	id = "com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorConfiguration",
	localization = "content/Language",
	name = "commerce-avalara-connector-configuration-name"
)
public interface CommerceAvalaraConnectorConfiguration {

	@Meta.AD(name = "account-number", required = false)
	public String accountNumber();

	@Meta.AD(name = "license-key", required = false, type = Meta.Type.Password)
	public String licenseKey();

	@Meta.AD(name = "service-url", required = false)
	public String serviceURL();

}