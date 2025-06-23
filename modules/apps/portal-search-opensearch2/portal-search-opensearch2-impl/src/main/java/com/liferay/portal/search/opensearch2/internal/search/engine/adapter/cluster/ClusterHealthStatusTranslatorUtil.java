/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.cluster;

import com.liferay.portal.search.engine.adapter.cluster.ClusterHealthStatus;

import org.opensearch.client.opensearch._types.HealthStatus;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class ClusterHealthStatusTranslatorUtil {

	public static HealthStatus translate(
		ClusterHealthStatus clusterHealthStatus) {

		if (clusterHealthStatus == ClusterHealthStatus.GREEN) {
			return HealthStatus.Green;
		}

		if (clusterHealthStatus == ClusterHealthStatus.RED) {
			return HealthStatus.Red;
		}

		if (clusterHealthStatus == ClusterHealthStatus.YELLOW) {
			return HealthStatus.Yellow;
		}

		throw new IllegalArgumentException(
			"Unknown cluster health status " + clusterHealthStatus);
	}

	public static ClusterHealthStatus translate(HealthStatus healthStatus) {
		if (healthStatus == HealthStatus.Green) {
			return ClusterHealthStatus.GREEN;
		}

		if (healthStatus == HealthStatus.Red) {
			return ClusterHealthStatus.RED;
		}

		if (healthStatus == HealthStatus.Yellow) {
			return ClusterHealthStatus.YELLOW;
		}

		throw new IllegalArgumentException(
			"Unknown health status " + healthStatus);
	}

	public static ClusterHealthStatus translate(String status) {
		if (status.equals("green")) {
			return ClusterHealthStatus.GREEN;
		}

		if (status.equals("red")) {
			return ClusterHealthStatus.RED;
		}

		if (status.equals("yellow")) {
			return ClusterHealthStatus.YELLOW;
		}

		throw new IllegalArgumentException("Unknown health status " + status);
	}

}