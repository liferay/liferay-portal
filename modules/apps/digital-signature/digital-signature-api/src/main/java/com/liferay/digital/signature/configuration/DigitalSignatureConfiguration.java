/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author José Abelenda
 */
@ExtendedObjectClassDefinition(
	category = "digital-signature", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.digital.signature.configuration.DigitalSignatureConfiguration",
	localization = "content/Language",
	name = "digital-signature-configuration-name"
)
public interface DigitalSignatureConfiguration {

	public String accountBaseURI();

	public String apiAccountId();

	public String apiUsername();

	public boolean enabled();

	public String environment();

	public String integrationKey();

	public String rsaPrivateKey();

	public String siteSettingsStrategy();

}