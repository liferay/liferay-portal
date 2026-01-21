/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.session.timeout.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Iván Zaera Avellón
 */
@ExtendedObjectClassDefinition(
	category = "infrastructure",
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.frontend.js.web.internal.session.timeout.configuration.SessionTimeoutConfiguration",
	localization = "content/Language",
	name = "session-timeout-configuration-name"
)
public interface SessionTimeoutConfiguration {

	@Meta.AD(
		deflt = "false", description = "auto-extend-description",
		name = "auto-extend", required = false
	)
	public boolean autoExtend();

	@Meta.AD(
		deflt = "70", description = "auto-extend-offset-description",
		name = "auto-extend-offset", required = false
	)
	public int autoExtendOffset();

}