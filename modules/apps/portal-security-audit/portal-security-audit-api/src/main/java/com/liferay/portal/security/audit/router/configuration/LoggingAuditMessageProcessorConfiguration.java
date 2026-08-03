/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Brian Greenwald
 * @author Prathima Shreenath
 */
@ExtendedObjectClassDefinition(category = "audit")
@Meta.OCD(
	id = "com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration",
	localization = "content/Language",
	name = "logging-audit-message-processor-configuration-name"
)
public interface LoggingAuditMessageProcessorConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "CSV", name = "log-message-format",
		optionValues = {"CSV", "JSON"}, required = false
	)
	public String logMessageFormat();

	@Meta.AD(deflt = "false", name = "output-to-console", required = false)
	public boolean outputToConsole();

}