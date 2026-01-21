/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Sergio González
 */
@ExtendedObjectClassDefinition(category = "adaptive-media")
@Meta.OCD(
	id = "com.liferay.adaptive.media.image.internal.configuration.AMImageConfiguration",
	localization = "content/Language",
	name = "adaptive-media-image-configuration-name"
)
public interface AMImageConfiguration {

	/**
	 * Set this to <code>true</code> to enable animated gif image scaling with
	 * gifsicle library. See https://www.lcdf.org/gifsicle for more information.
	 */
	@Meta.AD(
		deflt = "false", description = "gifsicle-enabled-key-description",
		name = "gifsicle-enabled", required = false
	)
	public boolean gifsicleEnabled();

	/**
	 * Sets the supported mime types that generate adaptive media images.
	 */
	@Meta.AD(
		deflt = "image/bmp|image/gif|image/jpeg|image/pjpeg|image/png|image/svg+xml|image/x-citrix-jpeg|image/x-citrix-png|image/x-ms-bmp|image/x-png",
		description = "supported-mime-types-key-description",
		name = "supported-mime-type", required = false
	)
	public String[] supportedMimeTypes();

}