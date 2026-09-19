/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Ivica Cardic
 */
@ExtendedObjectClassDefinition(category = "batch-engine")
@Meta.OCD(
	id = "com.liferay.batch.engine.configuration.BatchEngineTaskConfiguration",
	localization = "content/Language",
	name = "batch-engine-task-configuration-name"
)
public interface BatchEngineTaskConfiguration {

	@Meta.AD(
		deflt = "14",
		description = "completed-tasks-cleaner-scan-interval-description",
		min = "1", name = "completed-tasks-cleaner-scan-interval",
		required = false
	)
	public int completedTasksCleanerScanInterval();

	@Meta.AD(
		deflt = "60", description = "orphan-scan-interval-description",
		min = "1", name = "orphan-scan-interval", required = false
	)
	public int orphanScanInterval();

	@Meta.AD(
		deflt = "30", description = "orphanage-threshold-description",
		name = "orphanage-threshold", required = false
	)
	public int orphanageThreshold();

}