/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Olivér Kecskeméty
 */
@ExtendedObjectClassDefinition(
	category = "content-security-policy",
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfiguration",
	localization = "content/Language",
	name = "content-security-policy-configuration-name"
)
public interface ContentSecurityPolicyConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(deflt = "true", name = "report-only", required = false)
	public boolean reportOnly();

	@ExtendedAttributeDefinition(descriptionArguments = "[$NONCE$]")
	@Meta.AD(
		description = "content-security-policy-help",
		name = "content-security-policy", required = false
	)
	public String policy();

	@Meta.AD(
		deflt = "/api/,/combo,/documents/,/image/,/layouttpl/,/o/,/webdav/",
		description = "content-security-policy-excluded-paths-help",
		name = "excluded-paths", required = false
	)
	public String[] excludedPaths();

}