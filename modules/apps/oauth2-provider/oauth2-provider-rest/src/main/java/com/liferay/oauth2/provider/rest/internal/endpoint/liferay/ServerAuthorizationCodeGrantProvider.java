/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.liferay;

import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = "timeout:Integer=15",
	service = ServerAuthorizationCodeGrantProvider.class
)
public class ServerAuthorizationCodeGrantProvider {

	public ServerAuthorizationCodeGrant getServerAuthorizationCodeGrant(
		String code) {

		if (!_clusterMasterExecutor.isEnabled() ||
			_clusterMasterExecutor.isMaster()) {

			return _getServerAuthorizationCodeGrant(code);
		}

		Future<ServerAuthorizationCodeGrant> future =
			_clusterMasterExecutor.executeOnMaster(
				new MethodHandler(
					_getServerAuthorizationCodeGrantMethodKey, code));

		try {
			return future.get(_timeout, TimeUnit.SECONDS);
		}
		catch (Exception exception) {
			_log.error(
				"Timeout getting code grant from master node", exception);

			return null;
		}
	}

	public List<ServerAuthorizationCodeGrant> getServerAuthorizationCodeGrants(
		Client client, UserSubject userSubject) {

		if (!_clusterMasterExecutor.isEnabled() ||
			_clusterMasterExecutor.isMaster()) {

			return _getServerAuthorizationCodeGrants(client, userSubject);
		}

		Future<List<ServerAuthorizationCodeGrant>> future =
			_clusterMasterExecutor.executeOnMaster(
				new MethodHandler(
					_getServerAuthorizationCodeGrantsMethodKey, client,
					userSubject));

		try {
			return future.get(_timeout, TimeUnit.SECONDS);
		}
		catch (Exception exception) {
			_log.error(
				"Timeout getting code grants from master node", exception);

			return Collections.emptyList();
		}
	}

	public void putServerAuthorizationCodeGrant(
		ServerAuthorizationCodeGrant serverAuthorizationCodeGrant) {

		if (!_clusterMasterExecutor.isEnabled() ||
			_clusterMasterExecutor.isMaster()) {

			_putServerAuthorizationCodeGrant(serverAuthorizationCodeGrant);
		}
		else {
			Future<?> future = _clusterMasterExecutor.executeOnMaster(
				new MethodHandler(
					_putServerAuthorizationCodeGrantMethodKey,
					serverAuthorizationCodeGrant));

			try {
				future.get(_timeout, TimeUnit.SECONDS);
			}
			catch (Exception exception) {
				_log.error(
					"Timeout setting code grant to master node", exception);
			}
		}
	}

	public ServerAuthorizationCodeGrant removeServerAuthorizationCodeGrant(
		String code) {

		if (!_clusterMasterExecutor.isEnabled() ||
			_clusterMasterExecutor.isMaster()) {

			return _removeServerAuthorizationCodeGrant(code);
		}

		Future<ServerAuthorizationCodeGrant> future =
			_clusterMasterExecutor.executeOnMaster(
				new MethodHandler(
					_removeServerAuthorizationCodeGrantMethodMethodKey, code));

		try {
			return future.get(_timeout, TimeUnit.SECONDS);
		}
		catch (Exception exception) {
			_log.error(
				"Timeout removing code grant from master node", exception);

			return null;
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_timeout = MapUtil.getInteger(properties, "timeout");
	}

	private static void _cleanUp() {
		while (_serverAuthorizationCodeGrantDelayeds.poll() != null);
	}

	private static ServerAuthorizationCodeGrant
		_getServerAuthorizationCodeGrant(String code) {

		_cleanUp();

		for (ServerAuthorizationCodeGrantDelayed
				serverAuthorizationCodeGrantDelayed :
					_serverAuthorizationCodeGrantDelayeds) {

			ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
				serverAuthorizationCodeGrantDelayed.
					getServerAuthorizationCodeGrant();

			if (code.equals(serverAuthorizationCodeGrant.getCode())) {
				return serverAuthorizationCodeGrant;
			}
		}

		return null;
	}

	private static List<ServerAuthorizationCodeGrant>
		_getServerAuthorizationCodeGrants(
			Client client, UserSubject userSubject) {

		_cleanUp();

		List<ServerAuthorizationCodeGrant> serverAuthorizationCodeGrants =
			new ArrayList<>();

		for (ServerAuthorizationCodeGrantDelayed
				serverAuthorizationCodeGrantDelayed :
					_serverAuthorizationCodeGrantDelayeds) {

			ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
				serverAuthorizationCodeGrantDelayed.
					getServerAuthorizationCodeGrant();

			if (!client.equals(serverAuthorizationCodeGrant.getClient()) ||
				!userSubject.equals(
					serverAuthorizationCodeGrant.getSubject())) {

				continue;
			}

			serverAuthorizationCodeGrants.add(serverAuthorizationCodeGrant);
		}

		return serverAuthorizationCodeGrants;
	}

	private static void _putServerAuthorizationCodeGrant(
		ServerAuthorizationCodeGrant serverAuthorizationCodeGrant) {

		_cleanUp();

		_serverAuthorizationCodeGrantDelayeds.add(
			new ServerAuthorizationCodeGrantDelayed(
				serverAuthorizationCodeGrant));
	}

	private static ServerAuthorizationCodeGrant
		_removeServerAuthorizationCodeGrant(String code) {

		_cleanUp();

		for (ServerAuthorizationCodeGrantDelayed
				serverAuthorizationCodeGrantDelayed :
					_serverAuthorizationCodeGrantDelayeds) {

			ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
				serverAuthorizationCodeGrantDelayed.
					getServerAuthorizationCodeGrant();

			if (code.equals(serverAuthorizationCodeGrant.getCode()) &&
				_serverAuthorizationCodeGrantDelayeds.remove(
					serverAuthorizationCodeGrantDelayed)) {

				return serverAuthorizationCodeGrant;
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServerAuthorizationCodeGrantProvider.class);

	private static final MethodKey _getServerAuthorizationCodeGrantMethodKey =
		new MethodKey(
			ServerAuthorizationCodeGrantProvider.class,
			"_getServerAuthorizationCodeGrant", String.class);
	private static final MethodKey _getServerAuthorizationCodeGrantsMethodKey =
		new MethodKey(
			ServerAuthorizationCodeGrantProvider.class,
			"_getServerAuthorizationCodeGrants", String.class);
	private static final MethodKey _putServerAuthorizationCodeGrantMethodKey =
		new MethodKey(
			ServerAuthorizationCodeGrantProvider.class,
			"_putServerAuthorizationCodeGrant",
			ServerAuthorizationCodeGrant.class);
	private static final MethodKey
		_removeServerAuthorizationCodeGrantMethodMethodKey = new MethodKey(
			ServerAuthorizationCodeGrantProvider.class,
			"_removeServerAuthorizationCodeGrant", String.class);
	private static final DelayQueue<ServerAuthorizationCodeGrantDelayed>
		_serverAuthorizationCodeGrantDelayeds = new DelayQueue<>();

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	private int _timeout;

	private static class ServerAuthorizationCodeGrantDelayed
		implements Delayed {

		public ServerAuthorizationCodeGrantDelayed(
			ServerAuthorizationCodeGrant serverAuthorizationCodeGrant) {

			_serverAuthorizationCodeGrant = serverAuthorizationCodeGrant;
		}

		@Override
		public int compareTo(Delayed delayed) {
			return _comparator.compare(
				this, (ServerAuthorizationCodeGrantDelayed)delayed);
		}

		@Override
		public long getDelay(TimeUnit unit) {
			long expirationTime = TimeUnit.MILLISECONDS.convert(
				_getExpirationTime(), TimeUnit.SECONDS);

			return unit.convert(
				expirationTime - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
		}

		public ServerAuthorizationCodeGrant getServerAuthorizationCodeGrant() {
			return _serverAuthorizationCodeGrant;
		}

		private long _getExpirationTime() {
			return _serverAuthorizationCodeGrant.getIssuedAt() +
				_serverAuthorizationCodeGrant.getExpiresIn();
		}

		private static final Comparator<ServerAuthorizationCodeGrantDelayed>
			_comparator = Comparator.comparing(
				ServerAuthorizationCodeGrantDelayed::_getExpirationTime);

		private final ServerAuthorizationCodeGrant
			_serverAuthorizationCodeGrant;

	}

}