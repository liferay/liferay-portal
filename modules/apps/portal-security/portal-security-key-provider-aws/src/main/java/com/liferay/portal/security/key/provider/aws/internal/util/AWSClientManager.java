/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.util;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * @author Christopher Kian
 */
public class AWSClientManager<T> {

	public AWSClientManager(
		ClientFactory<T> clientFactory, String fipsEndpointTemplate,
		String region, boolean useFIPSEndpoint) {

		this(
			clientFactory, fipsEndpointTemplate, region,
			AWSRegionUtil::getRegion, useFIPSEndpoint);
	}

	public void close() {
		_writeLock.lock();

		try {
			_closed = true;

			_closeClient();
		}
		finally {
			_writeLock.unlock();
		}
	}

	public <R> R execute(AWSOperation<T, R> awsOperation) throws Exception {
		while (true) {
			_readLock.lock();

			try {
				if (_closed) {
					throw new IllegalStateException(
						"AWS client manager is closed");
				}

				if (_client != null) {
					return awsOperation.apply(_client);
				}
			}
			finally {
				_readLock.unlock();
			}

			_writeLock.lock();

			try {
				if (_closed) {
					throw new IllegalStateException(
						"AWS client manager is closed");
				}

				if (_client == null) {
					_client = _clientFactory.build(
						_awsCredentialsProvider, _getEndpointConfiguration(),
						_region);
				}
			}
			finally {
				_writeLock.unlock();
			}
		}
	}

	public String getRegion() {
		return _region;
	}

	public void updateConfiguration(String region, boolean useFIPSEndpoint) {
		region = _resolveRegion(region, _regionSupplier);

		_writeLock.lock();

		try {
			if (!Objects.equals(region, _region) ||
				(useFIPSEndpoint != _useFIPSEndpoint)) {

				_region = region;
				_useFIPSEndpoint = useFIPSEndpoint;

				_closeClient();
			}
		}
		finally {
			_writeLock.unlock();
		}
	}

	@FunctionalInterface
	public interface AWSOperation<T, R> {

		public R apply(T client) throws Exception;

	}

	@FunctionalInterface
	public interface ClientFactory<T> {

		public T build(
				AWSCredentialsProvider awsCredentialsProvider,
				AwsClientBuilder.EndpointConfiguration endpointConfiguration,
				String region)
			throws Exception;

	}

	protected AWSClientManager(
		ClientFactory<T> clientFactory, String fipsEndpointTemplate,
		String region, Supplier<String> regionSupplier,
		boolean useFIPSEndpoint) {

		_clientFactory = clientFactory;
		_fipsEndpointTemplate = fipsEndpointTemplate;
		_region = _resolveRegion(region, regionSupplier);
		_regionSupplier = regionSupplier;
		_useFIPSEndpoint = useFIPSEndpoint;

		_awsCredentialsProvider =
			DefaultAWSCredentialsProviderChain.getInstance();

		ReentrantReadWriteLock reentrantReadWriteLock =
			new ReentrantReadWriteLock();

		_readLock = reentrantReadWriteLock.readLock();
		_writeLock = reentrantReadWriteLock.writeLock();
	}

	private void _closeClient() {
		if (_client == null) {
			return;
		}

		try {
			if (_client instanceof AmazonWebServiceClient) {
				AmazonWebServiceClient amazonWebServiceClient =
					(AmazonWebServiceClient)_client;

				amazonWebServiceClient.shutdown();
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to cleanly shut down AWS client", exception);
			}
		}

		_client = null;
	}

	private AwsClientBuilder.EndpointConfiguration _getEndpointConfiguration() {
		if (!_useFIPSEndpoint || Validator.isNull(_fipsEndpointTemplate) ||
			Validator.isNull(_region)) {

			return null;
		}

		String endpoint = StringUtil.replace(
			_fipsEndpointTemplate, "{region}", _region);

		return new AwsClientBuilder.EndpointConfiguration(endpoint, _region);
	}

	private String _resolveRegion(
		String region, Supplier<String> regionSupplier) {

		if (Validator.isNull(region)) {
			return regionSupplier.get();
		}

		return region;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AWSClientManager.class);

	private final AWSCredentialsProvider _awsCredentialsProvider;
	private volatile T _client;
	private final ClientFactory<T> _clientFactory;
	private volatile boolean _closed;
	private final String _fipsEndpointTemplate;
	private final Lock _readLock;
	private volatile String _region;
	private final Supplier<String> _regionSupplier;
	private volatile boolean _useFIPSEndpoint;
	private final Lock _writeLock;

}