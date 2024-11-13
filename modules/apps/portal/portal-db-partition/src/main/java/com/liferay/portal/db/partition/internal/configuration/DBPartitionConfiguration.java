/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alberto Chaparro
 */
@ExtendedObjectClassDefinition(category = "infrastructure", generateUI = false)
@Meta.OCD(
	id = "com.liferay.portal.db.partition.internal.configuration.DBPartitionConfiguration",
	localization = "content/Language", name = "db-partition-configuration-name"
)
public interface DBPartitionConfiguration {

	@Meta.AD(
		deflt = "liferay/background_task|liferay/background_task_status|liferay/scheduler_engine|liferay/scheduler_scripting|liferay/cache_replication",
		description = "excluded-message-bus-destination-names-description",
		name = "excluded-message-bus-destination-names", required = false
	)
	public String[] excludedMessageBusDestinationNames();

	@Meta.AD(
		deflt = "com.liferay.analytics.settings.internal.scheduler.CheckAnalyticsConnectionsSchedulerJobConfiguration|com.liferay.portal.store.s3.scheduler.AbortedMultipartUploadCleanerSchedulerJobConfiguration",
		description = "excluded-scheduler-job-names-description",
		name = "excluded-scheduler-job-names", required = false
	)
	public String[] excludedSchedulerJobNames();

}