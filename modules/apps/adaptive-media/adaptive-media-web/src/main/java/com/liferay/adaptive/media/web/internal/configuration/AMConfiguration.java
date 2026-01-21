/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Sergio González
 */
@ExtendedObjectClassDefinition(category = "adaptive-media")
@Meta.OCD(
	id = "com.liferay.adaptive.media.web.internal.configuration.AMConfiguration",
	localization = "content/Language",
	name = "adaptive-media-configuration-name"
)
public interface AMConfiguration {

	/**
	 * Sets the size of core workers to process adaptive media.
	 */
	@Meta.AD(
		deflt = "2", description = "workers-core-size-key-description",
		name = "workers-core-size", required = false
	)
	public int workersCoreSize();

	/**
	 * Sets the maximum size of workers to process adaptive media.
	 */
	@Meta.AD(
		deflt = "5", description = "workers-max-size-key-description",
		name = "workers-max-size", required = false
	)
	public int workersMaxSize();

}