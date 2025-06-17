/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Yuri Monteiro
 */
@ExtendedObjectClassDefinition(
	category = "object", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.object.configuration.ObjectEntryVersionConfiguration",
	localization = "content/Language",
	name = "object-entry-version-configuration-name"
)
public interface ObjectEntryVersionConfiguration {

	@Meta.AD(
		deflt = "0", description = "maximum-versions-per-entry-description",
		name = "maximum-versions-per-entry", required = false
	)
	public int maximumVersionsPerEntry();

	@Meta.AD(
		deflt = "0", description = "maximum-retention-period-description",
		name = "maximum-retention-period"
	)
	public int maximumRetentionPeriod();

}