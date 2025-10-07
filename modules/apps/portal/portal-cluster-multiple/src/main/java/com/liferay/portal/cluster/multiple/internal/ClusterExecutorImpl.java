/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.concurrent.ConcurrentReferenceValueHashMap;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.cluster.multiple.internal.jgroups.JGroupsClusterChannelFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterEvent;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterException;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterNodeResponse;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.PortalInetSocketAddressEventListener;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration",
	enabled = false, service = ClusterExecutor.class
)
public class ClusterExecutorImpl implements ClusterExecutor {

	@Override
	public FutureClusterResponses execute(ClusterRequest clusterRequest) {
		Set<String> clusterNodeIds = new HashSet<>();

		if (clusterRequest.isMulticast()) {
			clusterNodeIds = new HashSet<>(_clusterNodeStatuses.keySet());

			if (clusterRequest.isSkipLocal()) {
				clusterNodeIds.remove(
					_localClusterNodeStatus.getClusterNodeId());
			}
		}
		else {
			clusterNodeIds.addAll(clusterRequest.getTargetClusterNodeIds());
		}

		FutureClusterResponses futureClusterResponses =
			new FutureClusterResponses(clusterNodeIds);

		if (!clusterRequest.isFireAndForget()) {
			_futureClusterResponses.put(
				clusterRequest.getUuid(), futureClusterResponses);
		}

		if (clusterNodeIds.remove(_localClusterNodeStatus.getClusterNodeId())) {
			ClusterNodeResponse clusterNodeResponse = executeClusterRequest(
				clusterRequest);

			if (!clusterRequest.isFireAndForget()) {
				futureClusterResponses.addClusterNodeResponse(
					clusterNodeResponse);
			}
		}

		if (clusterRequest.isMulticast()) {
			_clusterChannel.sendMulticastMessage(clusterRequest);
		}
		else {
			for (String clusterNodeId : clusterNodeIds) {
				ClusterNodeStatus clusterNodeStatus = _clusterNodeStatuses.get(
					clusterNodeId);

				if (clusterNodeStatus == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to get cluster node ", clusterNodeId,
								" while executing ", clusterRequest));
					}

					continue;
				}

				_clusterChannel.sendUnicastMessage(
					clusterRequest, clusterNodeStatus.getAddress());
			}
		}

		return futureClusterResponses;
	}

	@Override
	public InetAddress getBindInetAddress() {
		return _clusterChannelFactory.getBindInetAddress();
	}

	@Override
	public NetworkInterface getBindNetworkInterface() {
		return _clusterChannelFactory.getBindNetworkInterface();
	}

	@Override
	public List<ClusterEventListener> getClusterEventListeners() {
		return _serviceTrackerList.toList();
	}

	@Override
	public List<ClusterNode> getClusterNodes() {
		List<ClusterNode> clusterNodes = new ArrayList<>();

		for (ClusterNodeStatus clusterNodeStatus :
				_clusterNodeStatuses.values()) {

			clusterNodes.add(clusterNodeStatus.getClusterNode());
		}

		return clusterNodes;
	}

	@Override
	public ClusterNode getLocalClusterNode() {
		return _localClusterNodeStatus.getClusterNode();
	}

	@Override
	public boolean isClusterNodeAlive(String clusterNodeId) {
		return _clusterNodeStatuses.containsKey(clusterNodeId);
	}

	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_enabled = true;

		modified(properties);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ClusterEventListener.class);

		initialize(
			PropsUtil.get(PropsKeys.CLUSTER_LINK_CHANNEL_LOGIC_NAME_CONTROL),
			PropsUtil.get(PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL),
			PropsUtil.get(PropsKeys.CLUSTER_LINK_CHANNEL_NAME_CONTROL));

		_serviceRegistration = bundleContext.registerService(
			PortalInetSocketAddressEventListener.class,
			new ClusterExecutorPortalInetSocketAddressEventListener(),
			new HashMapDictionary<String, Object>());
	}

	@Deactivate
	protected void deactivate() {
		if (_clusterChannel != null) {
			_clusterChannel.close();
		}

		_clusterChannel = null;

		if (_executorService != null) {
			_executorService.shutdownNow();
		}

		_executorService = null;

		_serviceTrackerList.close();

		_clusterNodeStatuses.clear();
		_futureClusterResponses.clear();
		_localClusterNodeStatus = null;

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	protected ClusterNodeResponse executeClusterRequest(
		ClusterRequest clusterRequest) {

		Serializable payload = clusterRequest.getPayload();

		if (!(payload instanceof MethodHandler)) {
			return ClusterNodeResponse.createExceptionClusterNodeResponse(
				_localClusterNodeStatus.getClusterNode(),
				clusterRequest.getUuid(),
				new ClusterException(
					"Payload is not of type " + MethodHandler.class.getName()));
		}

		MethodHandler methodHandler = (MethodHandler)payload;

		ClusterInvokeThreadLocal.setEnabled(false);

		try {
			Object result = methodHandler.invoke();

			if ((result instanceof Serializable) || (result == null)) {
				return ClusterNodeResponse.createResultClusterNodeResponse(
					_localClusterNodeStatus.getClusterNode(),
					clusterRequest.getUuid(), (Serializable)result);
			}

			return ClusterNodeResponse.createExceptionClusterNodeResponse(
				_localClusterNodeStatus.getClusterNode(),
				clusterRequest.getUuid(),
				new ClusterException(
					StringBundler.concat(
						methodHandler, " returned value ", result,
						" that is not serializable")));
		}
		catch (Exception exception) {
			return ClusterNodeResponse.createExceptionClusterNodeResponse(
				_localClusterNodeStatus.getClusterNode(),
				clusterRequest.getUuid(), exception);
		}
		finally {
			ClusterInvokeThreadLocal.setEnabled(true);
		}
	}

	protected void fireClusterEvent(ClusterEvent clusterEvent) {
		for (ClusterEventListener listener : _serviceTrackerList) {
			listener.processClusterEvent(clusterEvent);
		}
	}

	protected ClusterChannel getClusterChannel() {
		return _clusterChannel;
	}

	protected String getClusterNodeId(Address address) {
		CompletableFuture<String> completableFuture =
			_clusterNodeIdCompletableFutures.computeIfAbsent(
				address, key -> new CompletableFuture<>());

		try {
			return completableFuture.get(
				clusterExecutorConfiguration.clusterNodeAddressTimeout(),
				TimeUnit.MILLISECONDS);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get cluster node with address " + address,
				exception);
		}

		return null;
	}

	protected ExecutorService getExecutorService() {
		return _executorService;
	}

	protected void handleReceivedClusterNodeResponse(
		ClusterNodeResponse clusterNodeResponse) {

		Exception exception = clusterNodeResponse.getException();

		if (exception == null) {
			Serializable result = clusterNodeResponse.getResult();

			if (result instanceof ClusterNodeStatus) {
				_memberJoined((ClusterNodeStatus)result);

				return;
			}
		}

		String uuid = clusterNodeResponse.getUuid();

		FutureClusterResponses futureClusterResponses =
			_futureClusterResponses.get(uuid);

		if (futureClusterResponses == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Unable to get response container for " + uuid);
			}

			return;
		}

		if (!futureClusterResponses.addClusterNodeResponse(
				clusterNodeResponse) &&
			_log.isWarnEnabled()) {

			ClusterNode clusterNode = clusterNodeResponse.getClusterNode();

			_log.warn(
				StringBundler.concat(
					"Unexpected cluster node ID ",
					clusterNode.getClusterNodeId(),
					" for response container with UUID ", uuid));
		}
	}

	protected Serializable handleReceivedClusterRequest(
		ClusterRequest clusterRequest) {

		Serializable payload = clusterRequest.getPayload();

		if (payload instanceof ClusterNodeStatus) {
			_memberJoined((ClusterNodeStatus)payload);

			return ClusterNodeResponse.createResultClusterNodeResponse(
				_localClusterNodeStatus.getClusterNode(),
				clusterRequest.getUuid(), _localClusterNodeStatus);
		}

		return executeClusterRequest(clusterRequest);
	}

	protected void initialize(
		String channelLogicName, String channelPropertiesLocation,
		String channelName) {

		if (Validator.isNull(channelPropertiesLocation)) {
			throw new IllegalStateException(
				"Set \"" + PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL +
					"\"");
		}

		if (Validator.isNull(channelName)) {
			throw new IllegalStateException(
				"Set \"" + PropsKeys.CLUSTER_LINK_CHANNEL_NAME_CONTROL + "\"");
		}

		_executorService = _portalExecutorManager.getPortalExecutor(
			ClusterExecutorImpl.class.getName());

		ClusterRequestReceiver clusterReceiver = new ClusterRequestReceiver(
			this);

		_clusterChannel = _clusterChannelFactory.createClusterChannel(
			_executorService, channelLogicName, channelPropertiesLocation,
			channelName, clusterReceiver);

		ClusterNode localClusterNode = new ClusterNode(
			_generateClusterNodeId(), _clusterChannel.getBindInetAddress());

		_localClusterNodeStatus = new ClusterNodeStatus(
			localClusterNode, _clusterChannel.getLocalAddress());

		_memberJoined(_localClusterNodeStatus);

		sendNotifyRequest();

		clusterReceiver.openLatch();

		_configurePortalInstanceCommunications();
	}

	protected void memberRemoved(List<Address> departAddresses) {
		for (Address address : departAddresses) {
			_clusterNodeIdCompletableFutures.remove(address);
		}

		List<ClusterNode> departClusterNodes = new ArrayList<>();

		Collection<ClusterNodeStatus> clusterNodeStatuses =
			_clusterNodeStatuses.values();

		Iterator<ClusterNodeStatus> iterator = clusterNodeStatuses.iterator();

		while (iterator.hasNext()) {
			ClusterNodeStatus clusterNodeStatus = iterator.next();

			if (departAddresses.contains(clusterNodeStatus.getAddress())) {
				departClusterNodes.add(clusterNodeStatus.getClusterNode());

				iterator.remove();
			}
		}

		if (departClusterNodes.isEmpty()) {
			return;
		}

		ClusterEvent clusterEvent = ClusterEvent.depart(departClusterNodes);

		fireClusterEvent(clusterEvent);
	}

	@Modified
	protected void modified(Map<String, Object> properies) {
		clusterExecutorConfiguration = ConfigurableUtil.createConfigurable(
			ClusterExecutorConfiguration.class, properies);

		_clusterChannelFactory = new JGroupsClusterChannelFactory(
			clusterExecutorConfiguration);
	}

	protected void sendNotifyRequest() {
		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			_localClusterNodeStatus, true);

		_clusterChannel.sendMulticastMessage(clusterRequest);
	}

	protected volatile ClusterExecutorConfiguration
		clusterExecutorConfiguration;

	private void _configurePortalInstanceCommunications() {
		if ((_localClusterNodeStatus == null) ||
			Validator.isNull(
				PropsUtil.get(PropsKeys.PORTAL_INSTANCE_PROTOCOL))) {

			return;
		}

		ClusterNode localClusterNode = _localClusterNodeStatus.getClusterNode();

		localClusterNode.setPortalProtocol(
			PropsUtil.get(PropsKeys.PORTAL_INSTANCE_PROTOCOL));
		localClusterNode.setPortalInetSocketAddress(
			_getConfiguredPortalInetSocketAddress());
	}

	private String _generateClusterNodeId() {
		UUID uuid = new UUID(
			SecureRandomUtil.nextLong(), SecureRandomUtil.nextLong());

		return uuid.toString();
	}

	private InetSocketAddress _getConfiguredPortalInetSocketAddress() {
		String portalInstanceInetSocketAddress = PropsUtil.get(
			PropsKeys.PORTAL_INSTANCE_INET_SOCKET_ADDRESS);

		if (Validator.isNull(portalInstanceInetSocketAddress)) {
			throw new IllegalArgumentException(
				"Portal instance host name and port needs to be set in the " +
					"property \"portal.instance.inet.socket.address\"");
		}

		String[] parts = StringUtil.split(
			portalInstanceInetSocketAddress, CharPool.COLON);

		if (parts.length != 2) {
			throw new IllegalArgumentException(
				"Unable to parse the portal instance host name and port from " +
					portalInstanceInetSocketAddress);
		}

		InetAddress hostInetAddress = null;

		try {
			hostInetAddress = InetAddress.getByName(parts[0]);
		}
		catch (UnknownHostException unknownHostException) {
			throw new IllegalArgumentException(
				"Unable to parse the portal instance host name and port from " +
					portalInstanceInetSocketAddress,
				unknownHostException);
		}

		int port = -1;

		try {
			port = GetterUtil.getIntegerStrict(parts[1]);
		}
		catch (NumberFormatException numberFormatException) {
			throw new IllegalArgumentException(
				"Unable to parse portal InetSocketAddress port from " +
					portalInstanceInetSocketAddress,
				numberFormatException);
		}

		return new InetSocketAddress(hostInetAddress, port);
	}

	private boolean _memberJoined(ClusterNodeStatus clusterNodeStatus) {
		CompletableFuture<String> completableFuture =
			_clusterNodeIdCompletableFutures.computeIfAbsent(
				clusterNodeStatus.getAddress(),
				key -> new CompletableFuture<>());

		completableFuture.complete(clusterNodeStatus.getClusterNodeId());

		ClusterNodeStatus oldClusterNodeStatus = _clusterNodeStatuses.put(
			clusterNodeStatus.getClusterNodeId(), clusterNodeStatus);

		if (oldClusterNodeStatus != null) {
			if (!oldClusterNodeStatus.equals(clusterNodeStatus)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Updated cluster node " +
							clusterNodeStatus.getClusterNode());
				}
			}

			return false;
		}

		ClusterEvent clusterEvent = ClusterEvent.join(
			clusterNodeStatus.getClusterNode());

		fireClusterEvent(clusterEvent);

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterExecutorImpl.class);

	private ClusterChannel _clusterChannel;
	private volatile ClusterChannelFactory _clusterChannelFactory;
	private final Map<Address, CompletableFuture<String>>
		_clusterNodeIdCompletableFutures = new ConcurrentHashMap<>();
	private final Map<String, ClusterNodeStatus> _clusterNodeStatuses =
		new ConcurrentHashMap<>();
	private boolean _enabled;
	private ExecutorService _executorService;
	private final Map<String, FutureClusterResponses> _futureClusterResponses =
		new ConcurrentReferenceValueHashMap<>(
			FinalizeManager.WEAK_REFERENCE_FACTORY);
	private ClusterNodeStatus _localClusterNodeStatus;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private ServiceRegistration<PortalInetSocketAddressEventListener>
		_serviceRegistration;
	private ServiceTrackerList<ClusterEventListener> _serviceTrackerList;

	private static class ClusterNodeStatus implements Serializable {

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof ClusterNodeStatus)) {
				return false;
			}

			ClusterNodeStatus clusterNodeStatus = (ClusterNodeStatus)object;

			if (Objects.equals(_address, clusterNodeStatus._address) &&
				Objects.equals(_clusterNode, clusterNodeStatus._clusterNode)) {

				return true;
			}

			return false;
		}

		public Address getAddress() {
			return _address;
		}

		public ClusterNode getClusterNode() {
			return _clusterNode;
		}

		public String getClusterNodeId() {
			return _clusterNode.getClusterNodeId();
		}

		@Override
		public int hashCode() {
			int hash = HashUtil.hash(0, _clusterNode);

			return HashUtil.hash(hash, _address);
		}

		private ClusterNodeStatus(ClusterNode clusterNode, Address address) {
			_clusterNode = clusterNode;
			_address = address;
		}

		private final Address _address;
		private final ClusterNode _clusterNode;

	}

	private class ClusterExecutorPortalInetSocketAddressEventListener
		implements PortalInetSocketAddressEventListener {

		@Override
		public void portalLocalInetSocketAddressConfigured(
			InetSocketAddress inetSocketAddress, boolean secure) {

			ClusterNode localClusterNode = getLocalClusterNode();

			if ((localClusterNode == null) ||
				(localClusterNode.getPortalProtocol() != null)) {

				return;
			}

			localClusterNode.setPortalInetSocketAddress(inetSocketAddress);

			if (secure) {
				localClusterNode.setPortalProtocol(Http.HTTPS);
			}
			else {
				localClusterNode.setPortalProtocol(Http.HTTP);
			}

			sendNotifyRequest();
		}

		@Override
		public void portalServerInetSocketAddressConfigured(
			InetSocketAddress inetSocketAddress, boolean secure) {
		}

	}

}