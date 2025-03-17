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
	factoryInstanceLabelAttribute = "servlet.context.helper.select.filter",
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	description = "web-context-cors-configuration-description", factory = true,
	id = "com.liferay.portal.remote.cors.configuration.WebContextCORSConfiguration",
	localization = "content/Language",
	name = "web-context-cors-configuration-name"
)
public interface WebContextCORSConfiguration {

	@Meta.AD(
		deflt = "(&(!(liferay.cors=false))(osgi.jaxrs.name=*))",
		description = "servlet-context-helper-select-filter-description",
		id = "servlet.context.helper.select.filter",
		name = "servlet-context-helper-select-filter-name", required = false
	)
	public String servletContextHelperSelectFilter();

	@Meta.AD(
		deflt = "*",
		description = "cors-configuration-filter-mapping-url-pattern-description",
		id = "filter.mapping.url.patterns",
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