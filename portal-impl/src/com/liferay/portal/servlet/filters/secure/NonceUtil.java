/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.secure;

import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterNodeResponse;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Chow
 * @author Cristina Rodríguez
 * @author Mariano Álvaro Sáiz
 */
public class NonceUtil {

	public static String generate(long companyId, String remoteAddress) {
		String companyKey = null;

		try {
			Company company = CompanyLocalServiceUtil.getCompanyById(companyId);

			companyKey = company.getKey();
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Invalid companyId " + companyId, exception);
		}

		long timestamp = System.currentTimeMillis();

		String nonce = DigesterUtil.digestHex(
			DigesterUtil.MD5, remoteAddress, String.valueOf(timestamp),
			companyKey);

		_nonceDelayQueue.put(new NonceDelayed(nonce));

		return nonce;
	}

	public static boolean verify(String nonce) {
		_cleanUp();

		if (_verifyInLocalNode(nonce) || _verifyInCluster(nonce)) {
			return true;
		}

		return false;
	}

	private static void _cleanUp() {
		while (_nonceDelayQueue.poll() != null);
	}

	private static boolean _verifyInCluster(String nonce) {
		if (!ClusterExecutorUtil.isEnabled()) {
			return false;
		}

		MethodHandler methodHandler = new MethodHandler(
			_verifyInLocalNode, nonce);

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			methodHandler, true);

		FutureClusterResponses futureClusterResponses =
			ClusterExecutorUtil.execute(clusterRequest);

		BlockingQueue<ClusterNodeResponse> clusterNodeResponses =
			futureClusterResponses.getPartialResults();

		try {
			while (!(clusterNodeResponses.isEmpty() &&
					 futureClusterResponses.isDone())) {

				ClusterNodeResponse clusterNodeResponse =
					clusterNodeResponses.poll(
						_NONCE_CLUSTER_TIMEOUT, TimeUnit.MILLISECONDS);

				if (clusterNodeResponse == null) {
					_log.error(
						"Timeout waiting for nonce verification in the " +
							"cluster");

					return false;
				}

				if (GetterUtil.getBoolean(clusterNodeResponse.getResult())) {
					return true;
				}
			}
		}
		catch (InterruptedException interruptedException) {
			_log.error(
				"Interrupted while waiting for nonce verification in the " +
					"cluster",
				interruptedException);
		}

		return false;
	}

	private static boolean _verifyInLocalNode(String nonce) {
		_cleanUp();

		return _nonceDelayQueue.remove(new NonceDelayed(nonce));
	}

	private static final long _NONCE_CLUSTER_TIMEOUT = GetterUtil.getLong(
		PropsUtil.get(PropsKeys.WEBDAV_NONCE_CLUSTER_TIMEOUT), 10000);

	private static final long _NONCE_EXPIRATION =
		PropsValues.WEBDAV_NONCE_EXPIRATION * Time.MINUTE;

	private static final Log _log = LogFactoryUtil.getLog(NonceUtil.class);

	private static final DelayQueue<NonceDelayed> _nonceDelayQueue =
		new DelayQueue<>();
	private static final MethodKey _verifyInLocalNode = new MethodKey(
		NonceUtil.class, "_verifyInLocalNode", String.class);

	private static class NonceDelayed implements Delayed {

		public NonceDelayed(String nonce) {
			if (nonce == null) {
				throw new NullPointerException("Nonce is null");
			}

			_nonce = nonce;

			_createTime = System.currentTimeMillis();
		}

		@Override
		public int compareTo(Delayed delayed) {
			NonceDelayed nonceDelayed = (NonceDelayed)delayed;

			long result = _createTime - nonceDelayed._createTime;

			if (result == 0) {
				return 0;
			}
			else if (result > 0) {
				return 1;
			}

			return -1;
		}

		@Override
		public boolean equals(Object object) {
			NonceDelayed nonceDelayed = (NonceDelayed)object;

			return _nonce.equals(nonceDelayed._nonce);
		}

		@Override
		public long getDelay(TimeUnit timeUnit) {
			long leftDelayTime =
				_NONCE_EXPIRATION + _createTime - System.currentTimeMillis();

			return timeUnit.convert(leftDelayTime, TimeUnit.MILLISECONDS);
		}

		@Override
		public int hashCode() {
			return _nonce.hashCode();
		}

		private final long _createTime;
		private final String _nonce;

	}

}