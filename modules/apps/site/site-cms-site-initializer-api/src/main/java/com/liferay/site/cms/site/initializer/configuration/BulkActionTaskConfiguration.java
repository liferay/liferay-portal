/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Crescenzo Rega
 */
@ExtendedObjectClassDefinition(
	category = "object", featureFlagKey = "LPD-17564",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.site.cms.site.initializer.configuration.BulkActionTaskConfiguration",
	localization = "content/Language",
	name = "bulk-action-task-configuration-name"
)
public interface BulkActionTaskConfiguration {

	@Meta.AD(
		deflt = "15", description = "check-interval-in-minutes-description",
		min = "1", name = "check-interval", required = false
	)
	public int checkInterval();

}