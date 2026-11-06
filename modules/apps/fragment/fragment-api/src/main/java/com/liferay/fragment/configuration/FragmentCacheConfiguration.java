/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Lourdes Fernández Besada
 */
@ExtendedObjectClassDefinition(
	category = "page-fragments",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.fragment.configuration.FragmentCacheConfiguration",
	localization = "content/Language",
	name = "fragment-cache-configuration-name"
)
@ProviderType
public interface FragmentCacheConfiguration {

	@Meta.AD(
		description = "fragment-cache-hosts-allowed-description",
		name = "fragment-cache-hosts-allowed", required = false
	)
	public String[] fragmentCacheHostsAllowed();

}