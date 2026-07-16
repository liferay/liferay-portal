/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * PoC configuration for LPD-92464. Each entry in {@link #cacheableEndpoints()}
 * has the form {@code <path pattern>;<Cache-Control value>}, where the path
 * pattern is matched against the request's absolute path and {@code *}
 * matches exactly one path segment (for example, a {id} path parameter).
 *
 * @author Jan Brychta
 */
@ExtendedObjectClassDefinition(
	category = "web-api", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.portal.vulcan.internal.configuration.HeadlessApiCacheConfiguration",
	localization = "content/Language",
	name = "headless-api-cache-configuration-name"
)
public interface HeadlessApiCacheConfiguration {

	@Meta.AD(
		deflt = "/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/*/taxonomy-categories;max-age=3600",
		name = "cacheable-endpoints", required = false
	)
	public String[] cacheableEndpoints();

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

}