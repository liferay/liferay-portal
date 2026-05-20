/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal.jgroups;

import com.liferay.petra.io.Deserializer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.cluster.multiple.internal.ClusterReceiver;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;

/**
 * @author Tina Tian
 */
public class JGroupsReceiver implements Receiver {

	public JGroupsReceiver(
		ClusterReceiver clusterReceiver,
		Map<ClassLoader, ClassLoader> classLoaders) {

		if (clusterReceiver == null) {
			throw new NullPointerException("Cluster receiver is null");
		}

		_clusterReceiver = clusterReceiver;
		_classLoaders = classLoaders;

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				_portalStarted.set(true);

				return null;
			});
	}

	@Override
	public void receive(Message message) {
		byte[] rawBuffer = message.getArray();

		if (rawBuffer == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message content is null");
			}

			return;
		}

		ByteBuffer byteBuffer = ByteBuffer.wrap(
			rawBuffer, message.getOffset(), message.getLength());

		Deserializer deserializer = new Deserializer(byteBuffer.slice());

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_classLoaders.computeIfAbsent(
					contextClassLoader,
					keyClassLoader ->
						AggregateClassLoader.getAggregateClassLoader(
							keyClassLoader,
							JGroupsReceiver.class.getClassLoader())))) {

			_clusterReceiver.receive(
				deserializer.readObject(), new AddressImpl(message.getSrc()));
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (!_portalStarted.get()) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to deserialize message payload during startup",
						classNotFoundException);
				}

				return;
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to deserialize message payload",
					classNotFoundException);
			}
		}
	}

	@Override
	public void viewAccepted(View view) {
		if (_log.isInfoEnabled()) {
			_log.info("Accepted view " + view);
		}

		List<Address> addresses = new ArrayList<>();
		Address coordinatorAddress = null;

		List<org.jgroups.Address> jGroupsAddresses = view.getMembers();

		for (int i = 0; i < jGroupsAddresses.size(); i++) {
			Address address = new AddressImpl(jGroupsAddresses.get(i));

			if (i == 0) {
				coordinatorAddress = address;
			}

			addresses.add(address);
		}

		_clusterReceiver.coordinatorAddressUpdated(coordinatorAddress);

		_clusterReceiver.addressesUpdated(addresses);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JGroupsReceiver.class);

	private final Map<ClassLoader, ClassLoader> _classLoaders;
	private final ClusterReceiver _clusterReceiver;
	private final AtomicBoolean _portalStarted = new AtomicBoolean(false);

}