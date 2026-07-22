/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Manuel de la Peña
 */
@ExtendedObjectClassDefinition(
	category = "file-storage", liferayLearnMessageKey = "general",
	liferayLearnMessageResource = "portal-store-s3"
)
@Meta.OCD(
	id = "com.liferay.portal.store.s3.configuration.S3StoreConfiguration",
	localization = "content/Language", name = "s3-store-configuration-name"
)
public interface S3StoreConfiguration {

	@Meta.AD(
		description = "access-key-help", name = "access-key", required = false
	)
	public String accessKey();

	@Meta.AD(description = "bucket-name-help[s3]", name = "bucket-name")
	public String bucketName();

	@Meta.AD(
		deflt = "DEFAULT", description = "connection-protocol-help",
		name = "connection-protocol",
		optionValues = {"HTTP", "HTTPS", "DEFAULT"}, required = false
	)
	public String connectionProtocol();

	@Meta.AD(
		deflt = "10000", description = "connection-timeout-help",
		name = "connection-timeout", required = false
	)
	public int connectionTimeout();

	@Meta.AD(
		deflt = "7", description = "core-pool-size-help",
		name = "core-pool-size", required = false
	)
	public int corePoolSize();

	@Meta.AD(
		deflt = "5000", description = "http-client-max-connections-help",
		name = "http-client-max-connections", required = false
	)
	public int httpClientMaxConnections();

	@Meta.AD(
		deflt = "5", description = "http-client-max-error-retry-help",
		name = "http-client-max-error-retry", required = false
	)
	public int httpClientMaxErrorRetry();

	@Meta.AD(
		deflt = "20", description = "max-pool-size-help",
		name = "max-pool-size", required = false
	)
	public int maxPoolSize();

	@Meta.AD(
		deflt = "5242880", description = "minimum-uploads-part-size-help",
		name = "minimum-uploads-part-size", required = false
	)
	public int minimumUploadPartSize();

	@Meta.AD(
		deflt = "10485760", description = "multipart-upload-threshold-help",
		name = "multipart-upload-threshold", required = false
	)
	public int multipartUploadThreshold();

	@Meta.AD(
		description = "ntlm-proxy-domain-help", name = "ntlm-proxy-domain",
		required = false
	)
	public String ntlmProxyDomain();

	@Meta.AD(
		description = "ntlm-proxy-workstation-help",
		name = "ntlm-proxy-workstation", required = false
	)
	public String ntlmProxyWorkstation();

	@Meta.AD(
		deflt = "none", description = "proxy-auth-type-help",
		name = "proxy-auth-type",
		optionValues = {"username-password", "ntlm", "none"}, required = false
	)
	public String proxyAuthType();

	@Meta.AD(
		description = "set-the-proxy-host-the-client-uses-to-connect",
		name = "proxy-host", required = false
	)
	public String proxyHost();

	@Meta.AD(
		description = "set-the-proxy-password-to-use-if-connecting-through-a-proxy",
		name = "proxy-password", required = false, type = Meta.Type.Password
	)
	public String proxyPassword();

	@Meta.AD(
		deflt = "12345",
		description = "set-the-proxy-port-the-client-uses-to-connect",
		name = "proxy-port", required = false
	)
	public int proxyPort();

	@Meta.AD(
		description = "proxy-username-help", name = "proxy-username",
		required = false
	)
	public String proxyUsername();

	@Meta.AD(
		description = "s3-endpoint-help", name = "s3-endpoint", required = false
	)
	public String s3Endpoint();

	@Meta.AD(
		deflt = "false", description = "s3-pathstyle-help",
		name = "s3-pathstyle", required = false
	)
	public boolean s3PathStyle();

	@Meta.AD(
		deflt = "us-east-1", description = "s3-region-help", name = "s3-region",
		required = false
	)
	public String s3Region();

	@Meta.AD(
		deflt = "STANDARD", description = "s3-storage-class-help",
		name = "s3-storage-class",
		optionValues = {"REDUCED_REDUNDANCY", "STANDARD"}, required = false
	)
	public String s3StorageClass();

	@Meta.AD(
		description = "secret-key-help", name = "secret-key", required = false,
		type = Meta.Type.Password
	)
	public String secretKey();

	@Meta.AD(
		description = "signer-override-help", name = "signer-override",
		required = false
	)
	public String signerOverride();

}