/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.geocoder.bing.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Andrea Di Giorgi
 */
@ExtendedObjectClassDefinition(category = "shipping")
@Meta.OCD(
	id = "com.liferay.commerce.geocoder.bing.internal.configuration.BingCommerceGeocoderConfiguration",
	localization = "content/Language",
	name = "commerce-geocoder-bing-configuration-name"
)
public interface BingCommerceGeocoderConfiguration {

	@Meta.AD(
		description = "set-the-key-for-the-bing-maps-api-integration",
		name = "api-key", required = false, type = Meta.Type.Password
	)
	public String apiKey();

}