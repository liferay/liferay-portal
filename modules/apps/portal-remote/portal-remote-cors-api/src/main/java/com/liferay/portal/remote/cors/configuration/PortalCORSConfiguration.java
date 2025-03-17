/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "security-tools",
	factoryInstanceLabelAttribute = "configuration.name",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "portal-cors-configuration-description", factory = true,
	id = "com.liferay.portal.remote.cors.configuration.PortalCORSConfiguration",
	localization = "content/Language", name = "portal-cors-configuration-name"
)
public interface PortalCORSConfiguration {

	@Meta.AD(deflt = "true", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		description = "portal-cors-name-description", id = "configuration.name",
		name = "portal-cors-name", required = false
	)
	public String name();

	@Meta.AD(
		deflt = "/api/jsonws/*|/documents/*|/image/*|/o/api/*|/o/graphql",
		description = "cors-configuration-filter-mapping-url-pattern-description",
		id = "filter.mapping.url.pattern",
		name = "cors-configuration-filter-mapping-url-pattern", required = false
	)
	public String[] filterMappingURLPatterns();

	@Meta.AD(
		deflt = "Access-Control-Allow-Credentials: true|Access-Control-Allow-Headers: *|Access-Control-Allow-Methods: *|Access-Control-Allow-Origin: http://localhost:8080,http://127.0.0.1:8080,::1",
		description = "cors-configuration-cors-headers-description",
		id = "headers", name = "cors-configuration-cors-headers",
		required = false
	)
	public String[] headers();

}