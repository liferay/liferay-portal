/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.gcs.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Shanon Mathai
 */
@ExtendedObjectClassDefinition(category = "file-storage")
@Meta.OCD(
	id = "com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration",
	localization = "content/Language", name = "gcs-store-configuration-name"
)
public interface GCSStoreConfiguration {

	@Meta.AD(
		deflt = "", description = "aes256-key-help", name = "aes256-key",
		required = false, type = Meta.Type.Password
	)
	public String aes256Key();

	@Meta.AD(description = "bucket-name-help[gcs]", name = "bucket-name")
	public String bucketName();

	@Meta.AD(
		deflt = "400", description = "initial-retry-delay-help",
		name = "initial-retry-delay", required = false
	)
	public int initialRetryDelay();

	@Meta.AD(
		deflt = "120000", description = "initial-rpc-timeout-help",
		name = "initial-rpc-timeout", required = false
	)
	public int initialRPCTimeout();

	@Meta.AD(
		deflt = "5", description = "max-retry-attempts-help",
		name = "max-retry-attempts", required = false
	)
	public int maxRetryAttempts();

	@Meta.AD(
		deflt = "10000", description = "max-retry-delay-help",
		name = "max-retry-delay", required = false
	)
	public int maxRetryDelay();

	@Meta.AD(
		deflt = "600000", description = "max-rpc-timeout-help",
		name = "max-rpc-timeout", required = false
	)
	public int maxRPCTimeout();

	@Meta.AD(
		deflt = "1.5", description = "retry-delay-multiplier-help",
		name = "retry-delay-multiplier", required = false
	)
	public double retryDelayMultiplier();

	@Meta.AD(
		deflt = "false", description = "retry-jitter-help",
		name = "retry-jitter", required = false
	)
	public boolean retryJitter();

	@Meta.AD(
		deflt = "1.0", description = "rpc-timeout-multiplier-help",
		name = "rpc-timeout-multiplier", required = false
	)
	public double rpcTimeoutMultiplier();

	@ExtendedAttributeDefinition(
		descriptionArguments = "https://cloud.google.com/iam/docs/creating-managing-service-account-keys"
	)
	@Meta.AD(
		description = "service-account-key-help", name = "service-account-key",
		required = false, type = Meta.Type.Password
	)
	public String serviceAccountKey();

}