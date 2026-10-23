/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.profile.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
@ExtendedObjectClassDefinition(
	category = "key-manager", featureFlagKey = "LPD-88411"
)
@Meta.OCD(
	id = "com.liferay.portal.security.key.internal.profile.configuration.KeyManagerCustomProfileConfiguration",
	localization = "content/Language",
	name = "key-manager-custom-profile-configuration-name"
)
public interface KeyManagerCustomProfileConfiguration {

	@Meta.AD(
		deflt = "db-company-crypto", name = "company-dek-provider-id",
		required = false
	)
	public String companyDEKProviderId();

	@Meta.AD(
		deflt = "db-company-crypto", name = "company-kek-provider-id",
		required = false
	)
	public String companyKEKProviderId();

	@Meta.AD(
		deflt = "db-company-secret", name = "company-secret-provider-id",
		required = false
	)
	public String companySecretProviderId();

	@Meta.AD(
		deflt = "db-system-crypto", name = "system-dek-provider-id",
		required = false
	)
	public String systemDEKProviderId();

	@Meta.AD(
		deflt = "db-system-crypto", name = "system-kek-provider-id",
		required = false
	)
	public String systemKEKProviderId();

	@Meta.AD(
		deflt = "db-system-secret", name = "system-secret-provider-id",
		required = false
	)
	public String systemSecretProviderId();

}