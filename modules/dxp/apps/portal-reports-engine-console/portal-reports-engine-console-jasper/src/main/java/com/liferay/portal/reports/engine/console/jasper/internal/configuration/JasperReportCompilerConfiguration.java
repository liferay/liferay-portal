/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.jasper.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alberto Sousa
 */
@ExtendedObjectClassDefinition(
	category = "reports", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.portal.reports.engine.console.jasper.internal.configuration.JasperReportCompilerConfiguration",
	localization = "content/Language",
	name = "report-template-security-configuration-name"
)
public interface JasperReportCompilerConfiguration {

	@Meta.AD(
		deflt = "",
		description = "report-template-security-allowed-class-names-description",
		name = "allowed-class-names", required = false
	)
	public String[] allowedClassNames();

	@Meta.AD(
		deflt = "true",
		description = "report-template-security-enabled-description",
		name = "enabled", required = false
	)
	public boolean enabled();

}