/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

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
@ProviderType
public interface DigitalSignatureConfiguration {

	public String accountBaseURI();

	public String apiAccountId();

	public String apiUsername();

	public boolean enabled();

	public String environment();

	@Meta.AD(deflt = "60000", required = false)
	public int httpTimeout();

	@Meta.AD(type = Meta.Type.Password)
	public String integrationKey();

	@Meta.AD(type = Meta.Type.Password)
	public String rsaPrivateKey();

	public String siteSettingsStrategy();

}