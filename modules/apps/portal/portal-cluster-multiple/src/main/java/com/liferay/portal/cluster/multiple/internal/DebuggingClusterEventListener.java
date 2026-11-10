/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.ClusterEvent;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterEventType;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tina Tian
 */
@Component(
	configurationPid = "com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, enabled = false,
	service = ClusterEventListener.class
)
public class DebuggingClusterEventListener implements ClusterEventListener {

	@Override
	public void processClusterEvent(ClusterEvent clusterEvent) {
		if (!_clusterExecutorConfiguration.debugEnabled() ||
			!_log.isInfoEnabled()) {

			return;
		}

		ClusterEventType clusterEventType = clusterEvent.getClusterEventType();

		List<ClusterNode> clusterNodes = clusterEvent.getClusterNodes();

		StringBundler sb = new StringBundler((clusterNodes.size() * 3) + 3);

		sb.append("Cluster event ");
		sb.append(clusterEventType);
		sb.append(StringPool.NEW_LINE);

		for (ClusterNode clusterNode : clusterNodes) {
			sb.append("Cluster node ");
			sb.append(clusterNode);
			sb.append(StringPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		_log.info(sb.toString());
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_clusterExecutorConfiguration = ConfigurableUtil.createConfigurable(
			ClusterExecutorConfiguration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DebuggingClusterEventListener.class);

	private volatile ClusterExecutorConfiguration _clusterExecutorConfiguration;

}