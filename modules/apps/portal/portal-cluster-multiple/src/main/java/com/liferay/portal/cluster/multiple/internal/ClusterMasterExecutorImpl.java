/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.concurrent.NoticeableFutureConverter;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterEvent;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterEventType;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.cluster.ClusterNodeResponse;
import com.liferay.portal.kernel.cluster.ClusterNodeResponses;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MethodHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(enabled = false, service = ClusterMasterExecutor.class)
public class ClusterMasterExecutorImpl implements ClusterMasterExecutor {

	@Override
	public void addClusterMasterTokenTransitionListener(
		ClusterMasterTokenTransitionListener
			clusterMasterTokenTransitionListener) {

		_clusterMasterTokenTransitionListeners.add(
			clusterMasterTokenTransitionListener);
	}

	@Override
	public <T> NoticeableFuture<T> executeOnMaster(
		MethodHandler methodHandler) {

		if (!_enabled) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Executing on the local node because the cluster master " +
						"executor is disabled");
			}

			DefaultNoticeableFuture<T> defaultNoticeableFuture =
				new DefaultNoticeableFuture<>();

			try {
				defaultNoticeableFuture.set((T)methodHandler.invoke());

				return defaultNoticeableFuture;
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}

		final String masterClusterNodeId = getMasterClusterNodeId(true);

		ClusterRequest clusterRequest = ClusterRequest.createUnicastRequest(
			methodHandler, masterClusterNodeId);

		try {
			return new NoticeableFutureConverter<T, ClusterNodeResponses>(
				_clusterExecutor.execute(clusterRequest)) {

				@Override
				protected T convert(ClusterNodeResponses clusterNodeResponses)
					throws Exception {

					ClusterNodeResponse clusterNodeResponse =
						clusterNodeResponses.getClusterResponse(
							masterClusterNodeId);

					return (T)clusterNodeResponse.getResult();
				}

			};
		}
		catch (Exception exception) {
			throw new SystemException(
				"Unable to execute on master " + masterClusterNodeId,
				exception);
		}
	}

	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	@Override
	public boolean isMaster() {
		if (isEnabled()) {
			return _master;
		}

		return true;
	}

	@Override
	public void removeClusterMasterTokenTransitionListener(
		ClusterMasterTokenTransitionListener
			clusterMasterTokenTransitionListener) {

		_clusterMasterTokenTransitionListeners.remove(
			clusterMasterTokenTransitionListener);
	}

	@Activate
	protected synchronized void activate(BundleContext bundleContext) {
		if (!_clusterExecutor.isEnabled()) {
			return;
		}

		_serviceRegistration = bundleContext.registerService(
			ClusterEventListener.class,
			new ClusterMasterTokenClusterEventListener(), null);

		_enabled = true;

		getMasterClusterNodeId(false);
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}

		_enabled = false;
	}

	protected String getMasterClusterNodeId(boolean notify) {
		ClusterExecutorImpl clusterExecutorImpl =
			(ClusterExecutorImpl)_clusterExecutor;

		ClusterChannel clusterChannel = clusterExecutorImpl.getClusterChannel();

		ClusterReceiver clusterReceiver = clusterChannel.getClusterReceiver();

		Address coordinatorAddress = clusterReceiver.getCoordinatorAddress();

		String masterClusterNodeId = clusterExecutorImpl.getClusterNodeId(
			coordinatorAddress);

		boolean master = coordinatorAddress.equals(
			clusterChannel.getLocalAddress());

		if (master == _master) {
			return masterClusterNodeId;
		}

		_master = master;

		if (notify) {
			notifyMasterTokenTransitionListeners(master);
		}

		return masterClusterNodeId;
	}

	protected void notifyMasterTokenTransitionListeners(
		boolean masterTokenAcquired) {

		for (ClusterMasterTokenTransitionListener
				clusterMasterTokenTransitionListener :
					_clusterMasterTokenTransitionListeners) {

			if (masterTokenAcquired) {
				clusterMasterTokenTransitionListener.masterTokenAcquired();
			}
			else {
				clusterMasterTokenTransitionListener.masterTokenReleased();
			}
		}
	}

	protected void setClusterMasterTokenTransitionListeners(
		Set<ClusterMasterTokenTransitionListener>
			clusterMasterTokenTransitionListeners) {

		_clusterMasterTokenTransitionListeners.addAll(
			clusterMasterTokenTransitionListeners);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterMasterExecutorImpl.class);

	@Reference
	private ClusterExecutor _clusterExecutor;

	private final Set<ClusterMasterTokenTransitionListener>
		_clusterMasterTokenTransitionListeners = new CopyOnWriteArraySet<>();
	private boolean _enabled;
	private volatile boolean _master;
	private ServiceRegistration<ClusterEventListener> _serviceRegistration;

	private class ClusterMasterTokenClusterEventListener
		implements ClusterEventListener {

		@Override
		public void processClusterEvent(ClusterEvent clusterEvent) {
			ClusterEventType clusterEventType =
				clusterEvent.getClusterEventType();

			if (clusterEventType ==
					ClusterEventType.COORDINATOR_ADDRESS_UPDATE) {

				getMasterClusterNodeId(true);
			}
		}

	}

}