/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Iván Zaera Avellón
 */
@ExtendedObjectClassDefinition(
	category = "infrastructure", featureFlagKey = "LPD-85746",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY, strictScope = true
)
@Meta.OCD(
	id = "com.liferay.frontend.js.audiences.web.internal.configuration.FrontendJSAudiencesConfiguration",
	localization = "content/Language",
	name = "frontend-js-audiences-configuration-name"
)
public interface FrontendJSAudiencesConfiguration {

	@Meta.AD(
		deflt = "0", description = "detection-timeout-help",
		name = "detection-timeout", required = false
	)
	public int detectionTimeout();

	@Meta.AD(
		deflt = "false", description = "enable-log-help", name = "enable-log",
		required = false
	)
	public boolean enableLog();

}