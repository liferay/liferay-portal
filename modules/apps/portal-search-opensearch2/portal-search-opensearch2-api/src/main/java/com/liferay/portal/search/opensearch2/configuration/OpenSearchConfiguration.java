/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "search")
@Meta.OCD(
	id = "com.liferay.portal.search.opensearch2.configuration.OpenSearchConfiguration",
	localization = "content/Language", name = "opensearch-configuration-name"
)
@ProviderType
public interface OpenSearchConfiguration {

	@Meta.AD(
		description = "remote-cluster-connection-id-help",
		name = "remote-cluster-connection-id", required = false
	)
	public String remoteClusterConnectionId();

	@Meta.AD(
		deflt = "liferay-", description = "index-name-prefix-help",
		name = "index-name-prefix", required = false
	)
	public String indexNamePrefix();

	@Meta.AD(
		deflt = "", description = "number-of-index-replicas-help",
		name = "number-of-index-replicas", required = false
	)
	public String indexNumberOfReplicas();

	@Meta.AD(
		deflt = "1", description = "number-of-index-shards-help",
		name = "number-of-index-shards", required = false
	)
	public String indexNumberOfShards();

	@Meta.AD(
		deflt = "10000", description = "index-max-result-window-help",
		name = "index-max-result-window", required = false
	)
	public int indexMaxResultWindow();

	@Meta.AD(
		description = "additional-index-configurations-help",
		name = "additional-index-configurations", required = false
	)
	public String additionalIndexConfigurations();

	@Meta.AD(
		description = "additional-type-mappings-help",
		name = "additional-type-mappings", required = false
	)
	public String additionalTypeMappings();

	@Meta.AD(
		description = "override-type-mappings-help",
		name = "override-type-mappings", required = false
	)
	public String overrideTypeMappings();

	@Meta.AD(
		deflt = "true", description = "log-exceptions-only-help",
		name = "log-exceptions-only", required = false
	)
	public boolean logExceptionsOnly();

	@Meta.AD(
		deflt = "0.0.0", description = "minimum-required-node-version-help",
		name = "minimum-required-node-version", required = false
	)
	public String minimumRequiredNodeVersion();

	@Meta.AD(
		deflt = "true", description = "track-total-hits-help",
		name = "track-total-hits", required = false
	)
	public boolean trackTotalHits();

	@Meta.AD(
		deflt = "2147483647", description = "track-total-hits-limit-help",
		max = "2147483647", name = "track-total-hits-limit", required = false
	)
	public int trackTotalHitsLimit();

}