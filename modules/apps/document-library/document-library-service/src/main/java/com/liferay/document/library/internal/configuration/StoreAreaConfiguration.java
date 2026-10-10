/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Adolfo Pérez
 */
@ExtendedObjectClassDefinition(
	category = "documents-and-media", generateUI = false
)
@Meta.OCD(
	id = "com.liferay.document.library.internal.configuration.StoreAreaConfiguration",
	localization = "content/Language", name = "store-area-configuration-name"
)
public interface StoreAreaConfiguration {

	@Meta.AD(
		deflt = "2", description = "store-area-cleanup-interval-help",
		min = "1", name = "store-area-cleanup-interval", required = false
	)
	public int cleanUpInterval();

	@Meta.AD(
		deflt = "31", description = "store-area-eviction-age-help",
		name = "store-area-eviction-age", required = false
	)
	public int evictionAge();

	@Meta.AD(
		deflt = "100", description = "store-area-eviction-quota-help",
		name = "store-area-eviction-quota", required = false
	)
	public int evictionQuota();

}