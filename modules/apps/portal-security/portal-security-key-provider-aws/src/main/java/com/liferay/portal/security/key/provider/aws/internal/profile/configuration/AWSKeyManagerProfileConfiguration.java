/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.profile.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Christopher Kian
 */
@ExtendedObjectClassDefinition(category = "key-manager")
@Meta.OCD(
	id = "com.liferay.portal.security.key.provider.aws.internal.profile.configuration.AWSKeyManagerProfileConfiguration",
	localization = "content/Language",
	name = "aws-key-manager-profile-configuration-name"
)
public interface AWSKeyManagerProfileConfiguration {

	@Meta.AD(deflt = "", name = "aws-account-id", required = false)
	public String awsAccountId();

	@Meta.AD(deflt = "", name = "aws-region", required = false)
	public String awsRegion();

	@Meta.AD(deflt = "", name = "key-arn-template", required = false)
	public String keyARNTemplate();

	@Meta.AD(deflt = "false", name = "strict-mode", required = false)
	public boolean strictMode();

	@Meta.AD(
		deflt = "aws", name = "vault-layer", optionLabels = {"AWS", "DB"},
		optionValues = {"aws", "db"}, required = false
	)
	public String vaultLayer();

}