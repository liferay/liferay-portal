/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.reports.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alejandro Tardín
 */
@ExtendedObjectClassDefinition(
	category = "pages", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	description = "layout-reports-google-pagespeed-configuration-description",
	id = "com.liferay.layout.reports.web.internal.configuration.LayoutReportsGooglePageSpeedGroupConfiguration",
	localization = "content/Language",
	name = "layout-reports-google-pagespeed-group-configuration-name"
)
public interface LayoutReportsGooglePageSpeedGroupConfiguration {

	@ExtendedAttributeDefinition(
		descriptionArguments = "https://developers.google.com/speed/docs/insights/v5/get-started"
	)
	@Meta.AD(
		description = "get-your-api-key-at-x", name = "api-key",
		required = false
	)
	public String apiKey();

	@Meta.AD(deflt = "true", name = "enable-google-pagespeed", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "MOBILE", description = "strategy-description",
		name = "strategy", optionLabels = {"mobile", "desktop"},
		optionValues = {"MOBILE", "DESKTOP"}, required = false
	)
	public String strategy();

}