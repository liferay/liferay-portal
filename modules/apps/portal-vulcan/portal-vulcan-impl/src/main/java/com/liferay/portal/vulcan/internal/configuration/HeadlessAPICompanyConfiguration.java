/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alberto Javier Moreno Lage
 */
@ExtendedObjectClassDefinition(
	category = "web-api", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration",
	localization = "content/Language",
	name = "headless-api-company-configuration-name"
)
public interface HeadlessAPICompanyConfiguration {

	@Meta.AD(
		deflt = "1", description = "graphql-batch-operation-limit-description",
		name = "graphql-batch-operation-limit", required = false
	)
	public int graphQLBatchOperationLimit();

	@Meta.AD(
		deflt = "500", description = "page-size-limit-description",
		name = "page-size-limit", required = false
	)
	public int pageSizeLimit();

	@Meta.AD(
		deflt = "20", description = "query-depth-limit-description",
		name = "query-depth-limit", required = false
	)
	public int queryDepthLimit();

}