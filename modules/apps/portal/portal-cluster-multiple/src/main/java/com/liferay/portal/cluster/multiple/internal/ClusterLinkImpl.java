/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.cluster.multiple.internal.jgroups.JGroupsClusterChannelFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterLink;
import com.liferay.portal.kernel.cluster.Priority;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration",
	enabled = false, service = ClusterLink.class
)
public class ClusterLinkImpl implements ClusterLink {

	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	@Override
	public void sendMulticastMessage(Message message, Priority priority) {
		ClusterChannel clusterChannel = getChannel(priority);

		clusterChannel.sendMulticastMessage(message);
	}

	@Override
	public void sendUnicastMessage(
		Address address, Message message, Priority priority) {

		if (_localAddresses.contains(address)) {
			sendLocalMessage(message);

			return;
		}

		ClusterChannel clusterChannel = getChannel(priority);

		clusterChannel.sendUnicastMessage(message, address);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_enabled = true;

		modified(properties);

		initialize(
			_getChannelSettings(
				PropsKeys.CLUSTER_LINK_CHANNEL_LOGIC_NAME_TRANSPORT),
			_getChannelSettings(
				PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_TRANSPORT),
			_getChannelSettings(PropsKeys.CLUSTER_LINK_CHANNEL_NAME_TRANSPORT));
	}

	@Deactivate
	protected void deactivate() {
		if (_clusterChannels != null) {
			for (ClusterChannel clusterChannel : _clusterChannels) {
				clusterChannel.close();
			}
		}

		_localAddresses = null;
		_clusterChannels = null;
		_clusterReceivers = null;

		if (_executorService != null) {
			_executorService.shutdownNow();
		}

		_executorService = null;
	}

	protected ClusterChannel getChannel(Priority priority) {
		int channelIndex =
			priority.ordinal() * _channelCount / MAX_CHANNEL_COUNT;

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Select channel number ", channelIndex, " for priority ",
					priority));
		}

		return _clusterChannels.get(channelIndex);
	}

	protected ExecutorService getExecutorService() {
		return _executorService;
	}

	protected List<Address> getLocalAddresses() {
		return _localAddresses;
	}

	protected void initialize(
		Map<String, String> channelLogicNames,
		Map<String, String> channelPropertiesLocations,
		Map<String, String> channelNames) {

		_executorService = _portalExecutorManager.getPortalExecutor(
			ClusterLinkImpl.class.getName());

		try {
			_initChannels(
				channelLogicNames, channelPropertiesLocations, channelNames);
		}
		catch (Exception exception) {
			_log.error("Unable to initialize channels", exception);

			throw new IllegalStateException(exception);
		}

		for (ClusterReceiver clusterReceiver : _clusterReceivers) {
			clusterReceiver.openLatch();
		}
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_clusterChannelFactory = new JGroupsClusterChannelFactory(
			ConfigurableUtil.createConfigurable(
				ClusterExecutorConfiguration.class, properties));
	}

	protected void sendLocalMessage(Message message) {
		String destinationName = message.getDestinationName();

		if (Validator.isNotNull(destinationName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Sending local cluster link message ", message, " to ",
						destinationName));
			}

			ClusterInvokeThreadLocal.setEnabled(false);

			try {
				_messageBus.sendMessage(destinationName, message);
			}
			finally {
				ClusterInvokeThreadLocal.setEnabled(true);
			}
		}
		else {
			_log.error(
				"Local cluster link message has no destination " + message);
		}
	}

	private Map<String, String> _getChannelSettings(String propertyPrefix) {
		Map<String, String> channelSettings = new HashMap<>();

		Properties channelProperties = PropsUtil.getProperties(
			propertyPrefix, true);

		for (Map.Entry<Object, Object> entry : channelProperties.entrySet()) {
			channelSettings.put(
				(String)entry.getKey(), (String)entry.getValue());
		}

		return channelSettings;
	}

	private void _initChannels(
			Map<String, String> channelLogicNames,
			Map<String, String> channelPropertiesLocations,
			Map<String, String> channelNames)
		throws Exception {

		_channelCount = channelPropertiesLocations.size();

		if ((_channelCount <= 0) || (_channelCount > MAX_CHANNEL_COUNT)) {
			throw new IllegalArgumentException(
				"Channel count must be between 1 and " + MAX_CHANNEL_COUNT);
		}

		_localAddresses = new ArrayList<>(_channelCount);
		_clusterChannels = new ArrayList<>(_channelCount);
		_clusterReceivers = new ArrayList<>(_channelCount);

		List<String> keys = new ArrayList<>(
			channelPropertiesLocations.keySet());

		Collections.sort(keys);

		for (String key : keys) {
			String channelPropertiesLocation = channelPropertiesLocations.get(
				key);
			String channelName = channelNames.get(key);

			if (Validator.isNull(channelPropertiesLocation) ||
				Validator.isNull(channelName)) {

				continue;
			}

			String channelLogicName = channelLogicNames.get(key);
			ClusterReceiver clusterReceiver = new ClusterForwardReceiver(this);

			ClusterChannel clusterChannel =
				_clusterChannelFactory.createClusterChannel(
					_executorService, channelLogicName,
					channelPropertiesLocation, channelName, clusterReceiver);

			_clusterChannels.add(clusterChannel);

			_clusterReceivers.add(clusterReceiver);
			_localAddresses.add(clusterChannel.getLocalAddress());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterLinkImpl.class);

	private int _channelCount;
	private volatile ClusterChannelFactory _clusterChannelFactory;
	private List<ClusterChannel> _clusterChannels;
	private List<ClusterReceiver> _clusterReceivers;
	private boolean _enabled;
	private ExecutorService _executorService;
	private List<Address> _localAddresses;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

}