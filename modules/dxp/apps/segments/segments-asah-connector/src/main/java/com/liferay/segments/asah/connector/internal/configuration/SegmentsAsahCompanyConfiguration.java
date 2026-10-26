/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author David Arques
 */
@ExtendedObjectClassDefinition(
	category = "segments", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.segments.asah.connector.internal.configuration.SegmentsAsahCompanyConfiguration",
	localization = "content/Language",
	name = "segments-asah-connector-company-configuration-name"
)
public interface SegmentsAsahCompanyConfiguration {

	@Meta.AD(
		deflt = "86400",
		description = "anonymous-user-segments-cache-expiration-time-description",
		name = "anonymous-user-segments-cache-expiration-time-name",
		required = false
	)
	public int anonymousUserSegmentsCacheExpirationTime();

}