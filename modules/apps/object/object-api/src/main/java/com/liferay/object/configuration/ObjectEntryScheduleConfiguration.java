/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jhosseph Gonzalez
 */
@ExtendedObjectClassDefinition(
	category = "object", featureFlagKey = "LPD-17564",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.object.configuration.ObjectEntryScheduleConfiguration",
	localization = "content/Language", name = "schedule-configuration-name"
)
@ProviderType
public interface ObjectEntryScheduleConfiguration {

	@Meta.AD(
		deflt = "15", description = "object-entry-check-interval-description",
		min = "1", name = "object-entry-check-interval", required = false
	)
	public int checkInterval();

}