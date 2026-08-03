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
	id = "com.liferay.portal.security.audit.router.configuration.CSVLogMessageFormatterConfiguration",
	localization = "content/Language",
	name = "csv-log-message-formatter-configuration-name"
)
public interface CSVLogMessageFormatterConfiguration {

	@Meta.AD(
		deflt = "additionalInfo|className|classPK|clientHost|clientIP|companyId|eventType|message|serverName|serverPort|sessionID|timestamp|userEmailAddress|userId|userLogin|userName",
		name = "columns", required = false
	)
	public String[] columns();

}