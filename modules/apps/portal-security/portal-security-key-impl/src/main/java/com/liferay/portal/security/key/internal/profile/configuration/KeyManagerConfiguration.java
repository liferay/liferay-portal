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
@ExtendedObjectClassDefinition(category = "key-manager")
@Meta.OCD(
	id = "com.liferay.portal.security.key.internal.profile.configuration.KeyManagerConfiguration",
	localization = "content/Language", name = "key-manager-configuration-name"
)
public interface KeyManagerConfiguration {

	@Meta.AD(deflt = "custom", name = "active-profile-id", required = false)
	public String activeProfileId();

	@Meta.AD(
		deflt = "", description = "company-kek-identifier-help",
		name = "company-kek-identifier", required = false
	)
	public String companyKEKIdentifier();

	@Meta.AD(
		deflt = "300", description = "company-key-cache-ttl-help",
		name = "company-key-cache-ttl", required = false
	)
	public int companyKeyCacheTTL();

}