/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster;

import com.liferay.portal.search.engine.adapter.cluster.ClusterHealthStatus;

/**
 * @author Michael C. Han
 */
public class ClusterHealthStatusTranslatorUtil {

	public static org.elasticsearch.cluster.health.ClusterHealthStatus
		translate(ClusterHealthStatus clusterHealthStatus) {

		if (clusterHealthStatus == ClusterHealthStatus.GREEN) {
			return org.elasticsearch.cluster.health.ClusterHealthStatus.GREEN;
		}

		if (clusterHealthStatus == ClusterHealthStatus.RED) {
			return org.elasticsearch.cluster.health.ClusterHealthStatus.RED;
		}

		if (clusterHealthStatus == ClusterHealthStatus.YELLOW) {
			return org.elasticsearch.cluster.health.ClusterHealthStatus.YELLOW;
		}

		throw new IllegalArgumentException(
			"Unknown status: " + clusterHealthStatus);
	}

	public static ClusterHealthStatus translate(
		org.elasticsearch.cluster.health.ClusterHealthStatus
			clusterHealthStatus) {

		if (clusterHealthStatus ==
				org.elasticsearch.cluster.health.ClusterHealthStatus.GREEN) {

			return ClusterHealthStatus.GREEN;
		}

		if (clusterHealthStatus ==
				org.elasticsearch.cluster.health.ClusterHealthStatus.RED) {

			return ClusterHealthStatus.RED;
		}

		if (clusterHealthStatus ==
				org.elasticsearch.cluster.health.ClusterHealthStatus.YELLOW) {

			return ClusterHealthStatus.YELLOW;
		}

		throw new IllegalArgumentException(
			"Unknown status: " + clusterHealthStatus);
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

		throw new IllegalArgumentException("Unknown status: " + status);
	}

}