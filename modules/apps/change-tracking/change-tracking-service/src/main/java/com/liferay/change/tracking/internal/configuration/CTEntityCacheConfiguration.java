/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author David Truong
 */
@ExtendedObjectClassDefinition(category = "publications")
@Meta.OCD(
	id = "com.liferay.change.tracking.internal.configuration.CTEntityCacheConfiguration",
	localization = "content/Language",
	name = "publications-entity-cache-configuration-name"
)
public interface CTEntityCacheConfiguration {

	@Meta.AD(
		deflt = "500", description = "entity-cache-threshold-help",
		name = "entity-cache-threshold", required = false
	)
	public int entityCacheThreshold();

}