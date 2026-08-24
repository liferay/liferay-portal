/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Jan Brychta
 */
@ExtendedObjectClassDefinition(
	category = "web-api", factoryInstanceLabelAttribute = "path",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.portal.vulcan.internal.configuration.HeadlessAPICacheCompanyConfiguration",
	localization = "content/Language",
	name = "headless-api-cache-company-configuration-name"
)
public interface HeadlessAPICacheCompanyConfiguration {

	@Meta.AD(
		deflt = "public",
		description = "headless-api-cache-control-description",
		name = "cache-control", optionLabels = {"private", "public"},
		optionValues = {"private", "public"}, required = false
	)
	public String cacheControl();

	@Meta.AD(
		deflt = "0", description = "max-age-description", name = "max-age",
		required = false
	)
	public int maxAge();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		description = "headless-api-cacheable-endpoint-path-description",
		name = "path"
	)
	public String path();

}