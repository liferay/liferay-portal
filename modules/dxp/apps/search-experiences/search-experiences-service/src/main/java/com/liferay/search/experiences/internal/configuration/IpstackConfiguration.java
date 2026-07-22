/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(
	category = "search-experiences",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.search.experiences.internal.configuration.IpstackConfiguration",
	localization = "content/Language", name = "ipstack-configuration-name"
)
public interface IpstackConfiguration {

	@Meta.AD(
		deflt = "", name = "api-key", required = false,
		type = Meta.Type.Password
	)
	public String apiKey();

	@Meta.AD(
		deflt = "http://api.ipstack.com", name = "api-url", required = false
	)
	public String apiURL();

	@Meta.AD(deflt = "604800", name = "cache-timeout", required = false)
	public int cacheTimeout();

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

}