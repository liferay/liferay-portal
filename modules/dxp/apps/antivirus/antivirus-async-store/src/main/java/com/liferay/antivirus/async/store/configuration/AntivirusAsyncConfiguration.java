/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Raymond Augé
 */
@ExtendedObjectClassDefinition(category = "antivirus")
@Meta.OCD(
	id = "com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration",
	localization = "content/Language",
	name = "async-antivirus-configuration-name"
)
public interface AntivirusAsyncConfiguration {

	@Meta.AD(
		deflt = "0 0 23 * * ?", description = "batch-scan-cron-expression-help",
		name = "batch-scan-cron-expression", required = false
	)
	public String batchScanCronExpression();

	@Meta.AD(
		description = "maximum-queue-size-help", name = "maximum-queue-size",
		required = false
	)
	public int maximumQueueSize();

	@ExtendedAttributeDefinition(
		descriptionArguments = {"5", "5", "10", "20", "40", "80"}
	)
	@Meta.AD(
		deflt = "0 0/5 * * * ?", description = "retry-cron-expression-help",
		name = "retry-cron-expression", required = false
	)
	public String retryCronExpression();

}