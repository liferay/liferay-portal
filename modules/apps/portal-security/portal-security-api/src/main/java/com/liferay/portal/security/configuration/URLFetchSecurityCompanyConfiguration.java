/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Lourdes Fernández Besada
 */
@ExtendedObjectClassDefinition(
	generateUI = false, scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.portal.security.configuration.URLFetchSecurityCompanyConfiguration"
)
public interface URLFetchSecurityCompanyConfiguration {

	@Meta.AD(required = false)
	public String[] urlHostsAllowed();

	@Meta.AD(deflt = "false", required = false)
	public boolean urlLocalNetworkAccessEnabled();

}