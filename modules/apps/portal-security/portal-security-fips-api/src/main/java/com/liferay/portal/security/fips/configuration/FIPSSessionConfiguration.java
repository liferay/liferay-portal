/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.security.fips.constants.FIPSConstants;

/**
 * @author Manuele Castro
 */
@ExtendedObjectClassDefinition(
	generateUI = false, scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration",
	localization = "content/Language", name = "fips-session-configuration-name"
)
public interface FIPSSessionConfiguration {

	@Meta.AD(deflt = "15", name = "fips-session-idle-timeout", required = false)
	public int idleTimeout();

	@Meta.AD(
		deflt = FIPSConstants.TIME_UNIT_MINUTES, name = "time-unit",
		optionLabels = {
			FIPSConstants.TIME_UNIT_MINUTES, FIPSConstants.TIME_UNIT_HOURS
		},
		optionValues = {
			FIPSConstants.TIME_UNIT_MINUTES, FIPSConstants.TIME_UNIT_HOURS
		},
		required = false
	)
	public String idleTimeoutTimeUnit();

	@Meta.AD(deflt = "30", name = "fips-session-maximum-age", required = false)
	public int maximumAge();

	@Meta.AD(
		deflt = FIPSConstants.TIME_UNIT_DAYS, name = "time-unit",
		optionLabels = {
			FIPSConstants.TIME_UNIT_MINUTES, FIPSConstants.TIME_UNIT_HOURS,
			FIPSConstants.TIME_UNIT_DAYS
		},
		optionValues = {
			FIPSConstants.TIME_UNIT_MINUTES, FIPSConstants.TIME_UNIT_HOURS,
			FIPSConstants.TIME_UNIT_DAYS
		},
		required = false
	)
	public String maximumAgeTimeUnit();

}