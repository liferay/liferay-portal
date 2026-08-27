/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Christopher Kian
 */
@ExtendedObjectClassDefinition(category = "key-manager")
@Meta.OCD(
	id = "com.liferay.portal.security.key.provider.aws.internal.configuration.AWSSecretsManagerSystemSecretProviderConfiguration",
	localization = "content/Language",
	name = "aws-secrets-manager-system-secret-provider-configuration-name"
)
public interface AWSSecretsManagerSystemSecretProviderConfiguration {

	@Meta.AD(deflt = "", name = "arn-template", required = false)
	public String arnTemplate();

	@Meta.AD(deflt = "", name = "aws-account-id", required = false)
	public String awsAccountId();

	@Meta.AD(deflt = "", name = "aws-region", required = false)
	public String awsRegion();

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "30", max = "30", min = "7", name = "recovery-window-in-days",
		required = false
	)
	public long recoveryWindowInDays();

	@Meta.AD(deflt = "false", name = "use-fips-endpoint", required = false)
	public boolean useFIPSEndpoint();

}