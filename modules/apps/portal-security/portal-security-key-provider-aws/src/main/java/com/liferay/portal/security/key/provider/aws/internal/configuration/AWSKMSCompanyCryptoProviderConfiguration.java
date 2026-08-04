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
	id = "com.liferay.portal.security.key.provider.aws.internal.configuration.AWSKMSCompanyCryptoProviderConfiguration",
	localization = "content/Language",
	name = "aws-kms-company-crypto-provider-configuration-name"
)
public interface AWSKMSCompanyCryptoProviderConfiguration {

	@Meta.AD(deflt = "", name = "aws-account-id", required = false)
	public String awsAccountId();

	@Meta.AD(deflt = "", name = "aws-region", required = false)
	public String awsRegion();

	@Meta.AD(deflt = "AES_256_GCM", name = "cipher-mode", required = false)
	public String cipherMode();

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(deflt = "false", name = "fips-enforced", required = false)
	public boolean fipsEnforced();

	@Meta.AD(deflt = "", name = "key-arn-template", required = false)
	public String keyARNTemplate();

	@Meta.AD(deflt = "false", name = "use-fips-endpoint", required = false)
	public boolean useFIPSEndpoint();

}