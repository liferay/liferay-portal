/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.oauth2.provider.rest.internal.bearer.token.provider;

import com.liferay.commerce.punchout.oauth2.provider.PunchOutAccessTokenProvider;
import com.liferay.commerce.punchout.oauth2.provider.model.PunchOutAccessToken;
import com.liferay.commerce.punchout.oauth2.provider.rest.internal.bearer.token.provider.configuration.PunchOutAccessTokenProviderConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(
	configurationPid = "com.liferay.commerce.punchout.oauth2.provider.rest.internal.bearer.token.provider.configuration.PunchOutAccessTokenProviderConfiguration",
	property = "timeout:Integer=15", service = PunchOutAccessTokenProvider.class
)
public class PunchOutAccessTokenProviderImpl
	implements PunchOutAccessTokenProvider {

	@Override
	public PunchOutAccessToken generatePunchOutAccessToken(
		long groupId, long commerceAccountId, String currencyCode,
		String userEmailAddress, String commerceOrderUuid,
		HashMap<String, Object> punchOutSessionAttributes) {

		PunchOutAccessToken punchOutAccessToken = _generatePunchOutAccessToken(
			groupId, commerceAccountId, currencyCode, userEmailAddress,
			commerceOrderUuid, punchOutSessionAttributes);

		if (!_clusterMasterExecutor.isEnabled() ||
			_clusterMasterExecutor.isMaster()) {

			_putPunchOutAccessToken(punchOutAccessToken);
		}
		else {
			Future<?> future = _clusterMasterExecutor.executeOnMaster(
				new MethodHandler(
					_putPunchOutAccessTokenMethodKey, punchOutAccessToken));

			try {
				future.get(_timeout, TimeUnit.SECONDS);
			}
			catch (Exception exception) {
				_log.error(
					"Timeout setting punch out access token to master node",
					exception);
			}
		}

		return punchOutAccessToken;
	}

	@Override
	public PunchOutAccessToken getPunchOutAccessToken(String token) {
		if (!_clusterMasterExecutor.isEnabled() ||
			_clusterMasterExecutor.isMaster()) {

			return _getPunchOutAccessToken(token);
		}

		Future<PunchOutAccessToken> future =
			_clusterMasterExecutor.executeOnMaster(
				new MethodHandler(_getPunchOutAccessTokenMethodKey, token));

		try {
			return future.get(_timeout, TimeUnit.SECONDS);
		}
		catch (Exception exception) {
			_log.error(
				"Timeout getting punch out access token from master node",
				exception);

			return null;
		}
	}

	@Override
	public PunchOutAccessToken removePunchOutAccessToken(String token) {
		if (!_clusterMasterExecutor.isEnabled() ||
			_clusterMasterExecutor.isMaster()) {

			return _removePunchOutAccessToken(token);
		}

		Future<PunchOutAccessToken> future =
			_clusterMasterExecutor.executeOnMaster(
				new MethodHandler(_removePunchOutAccessTokenMethodKey, token));

		try {
			return future.get(_timeout, TimeUnit.SECONDS);
		}
		catch (Exception exception) {
			_log.error(
				"Timeout removing punch out access token from master node",
				exception);

			return null;
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_timeout = MapUtil.getInteger(properties, "timeout");

		_punchOutAccessTokenProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				PunchOutAccessTokenProviderConfiguration.class, properties);
	}

	private static void _cleanUp() {
		while (_punchOutAccessTokenDelayQueue.poll() != null);
	}

	private static void _putPunchOutAccessToken(
		PunchOutAccessToken punchOutAccessToken) {

		_cleanUp();

		_punchOutAccessTokenDelayQueue.add(
			new PunchOutAccessTokenDelayed(punchOutAccessToken));
	}

	private static PunchOutAccessToken _removePunchOutAccessToken(
		String token) {

		_cleanUp();

		AtomicReference<PunchOutAccessToken>
			punchOutAccessTokenAtomicReference = new AtomicReference<>();

		_punchOutAccessTokenDelayQueue.removeIf(
			punchOutAccessTokenDelayed -> {
				PunchOutAccessToken punchOutAccessToken =
					punchOutAccessTokenDelayed.getPunchOutAccessToken();

				String tokenString = Base64.encodeToURL(
					punchOutAccessToken.getToken());

				if (MessageDigest.isEqual(
						token.getBytes(StandardCharsets.UTF_8),
						tokenString.getBytes(StandardCharsets.UTF_8))) {

					punchOutAccessTokenAtomicReference.compareAndSet(
						null, punchOutAccessToken);

					return true;
				}

				return false;
			});

		return punchOutAccessTokenAtomicReference.get();
	}

	private PunchOutAccessToken _generatePunchOutAccessToken(
		long groupId, long commerceAccountId, String currencyCode,
		String userEmailAddress, String commerceOrderUuid,
		HashMap<String, Object> punchOutSessionAttributes) {

		PunchOutAccessToken punchOutAccessToken = new PunchOutAccessToken();

		punchOutAccessToken.setGroupId(groupId);
		punchOutAccessToken.setCommerceAccountId(commerceAccountId);
		punchOutAccessToken.setCurrencyCode(currencyCode);
		punchOutAccessToken.setIssuedAt(System.currentTimeMillis());

		int expiresInSeconds =
			_punchOutAccessTokenProviderConfiguration.accessTokenExpiresIn();

		long expiresInMilliseconds = TimeUnit.MILLISECONDS.convert(
			expiresInSeconds, TimeUnit.SECONDS);

		punchOutAccessToken.setExpiresIn(expiresInMilliseconds);

		punchOutAccessToken.setToken(
			_generateSecureRandomBytes(
				_punchOutAccessTokenProviderConfiguration.
					accessTokenKeyByteSize()));
		punchOutAccessToken.setUserEmailAddress(userEmailAddress);
		punchOutAccessToken.setCommerceOrderUuid(commerceOrderUuid);
		punchOutAccessToken.setPunchOutSessionAttributes(
			punchOutSessionAttributes);

		return punchOutAccessToken;
	}

	private byte[] _generateSecureRandomBytes(int size) {
		byte[] bytes = new byte[size];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = SecureRandomUtil.nextByte();
		}

		return bytes;
	}

	private PunchOutAccessToken _getPunchOutAccessToken(String token) {
		_cleanUp();

		for (PunchOutAccessTokenDelayed punchOutAccessTokenDelayed :
				_punchOutAccessTokenDelayQueue) {

			PunchOutAccessToken punchOutAccessToken =
				punchOutAccessTokenDelayed.getPunchOutAccessToken();

			String tokenString = Base64.encodeToURL(
				punchOutAccessToken.getToken());

			if (MessageDigest.isEqual(
					token.getBytes(StandardCharsets.UTF_8),
					tokenString.getBytes(StandardCharsets.UTF_8))) {

				return punchOutAccessToken;
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PunchOutAccessTokenProviderImpl.class);

	private static final MethodKey _getPunchOutAccessTokenMethodKey =
		new MethodKey(
			PunchOutAccessTokenProviderImpl.class, "_getPunchOutAccessToken",
			String.class);
	private static final DelayQueue<PunchOutAccessTokenDelayed>
		_punchOutAccessTokenDelayQueue = new DelayQueue<>();
	private static final MethodKey _putPunchOutAccessTokenMethodKey =
		new MethodKey(
			PunchOutAccessTokenProviderImpl.class, "_putPunchOutAccessToken",
			PunchOutAccessToken.class);
	private static final MethodKey _removePunchOutAccessTokenMethodKey =
		new MethodKey(
			PunchOutAccessTokenProviderImpl.class, "_removePunchOutAccessToken",
			String.class);

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	private PunchOutAccessTokenProviderConfiguration
		_punchOutAccessTokenProviderConfiguration;
	private int _timeout;

	private static class PunchOutAccessTokenDelayed implements Delayed {

		public PunchOutAccessTokenDelayed(
			PunchOutAccessToken punchOutAccessToken) {

			_punchOutAccessToken = punchOutAccessToken;
		}

		@Override
		public int compareTo(Delayed delayed) {
			return _comparator.compare(
				this, (PunchOutAccessTokenDelayed)delayed);
		}

		@Override
		public long getDelay(TimeUnit unit) {
			long expirationTime = _getExpirationTime();

			return unit.convert(
				expirationTime - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
		}

		public PunchOutAccessToken getPunchOutAccessToken() {
			return _punchOutAccessToken;
		}

		private long _getExpirationTime() {
			return _punchOutAccessToken.getIssuedAt() +
				_punchOutAccessToken.getExpiresIn();
		}

		private static final Comparator<PunchOutAccessTokenDelayed>
			_comparator = Comparator.comparing(
				PunchOutAccessTokenDelayed::_getExpirationTime);

		private final PunchOutAccessToken _punchOutAccessToken;

	}

}