/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.configuration;

import java.util.Comparator;

/**
 * @author Petteri Karttunen
 */
public interface OpenSearchConfigurationWrapper
	extends Comparator<OpenSearchConfigurationObserver> {

	public String additionalIndexConfigurations();

	public String additionalTypeMappings();

	public int indexMaxResultWindow();

	public String indexNamePrefix();

	public String indexNumberOfReplicas();

	public String indexNumberOfShards();

	public boolean logExceptionsOnly();

	public String minimumRequiredNodeVersion();

	public String overrideTypeMappings();

	public void register(
		OpenSearchConfigurationObserver openSearchConfigurationObserver);

	public String remoteClusterConnectionId();

	public boolean trackTotalHits();

	public int trackTotalHitsLimit();

	public void unregister(
		OpenSearchConfigurationObserver openSearchConfigurationObserver);

}