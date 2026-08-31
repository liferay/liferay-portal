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
		String awsRegion, ClientFactory<T> clientFactory,
		String fipsEndpointTemplate, boolean useFIPSEndpoint) {

		this(
			awsRegion, AWSRegionUtil::getRegion, clientFactory,
			fipsEndpointTemplate, useFIPSEndpoint);
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
						_awsCredentialsProvider, _awsRegion,
						_getEndpointConfiguration());
				}
			}
			finally {
				_writeLock.unlock();
			}
		}
	}

	public String getAWSRegion() {
		return _awsRegion;
	}

	public void updateConfiguration(String awsRegion, boolean useFIPSEndpoint) {
		awsRegion = _resolveAWSRegion(awsRegion, _awsRegionSupplier);

		_writeLock.lock();

		try {
			if (!Objects.equals(awsRegion, _awsRegion) ||
				(useFIPSEndpoint != _useFIPSEndpoint)) {

				_awsRegion = awsRegion;
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
				AWSCredentialsProvider awsCredentialsProvider, String awsRegion,
				AwsClientBuilder.EndpointConfiguration endpointConfiguration)
			throws Exception;

	}

	protected AWSClientManager(
		String awsRegion, Supplier<String> awsRegionSupplier,
		ClientFactory<T> clientFactory, String fipsEndpointTemplate,
		boolean useFIPSEndpoint) {

		_awsRegion = _resolveAWSRegion(awsRegion, awsRegionSupplier);
		_awsRegionSupplier = awsRegionSupplier;
		_clientFactory = clientFactory;
		_fipsEndpointTemplate = fipsEndpointTemplate;
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
		if (!_useFIPSEndpoint || Validator.isNull(_awsRegion) ||
			Validator.isNull(_fipsEndpointTemplate)) {

			return null;
		}

		String endpoint = StringUtil.replace(
			_fipsEndpointTemplate, "{region}", _awsRegion);

		return new AwsClientBuilder.EndpointConfiguration(endpoint, _awsRegion);
	}

	private String _resolveAWSRegion(
		String awsRegion, Supplier<String> awsRegionSupplier) {

		if (Validator.isNull(awsRegion)) {
			return awsRegionSupplier.get();
		}

		return awsRegion;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AWSClientManager.class);

	private final AWSCredentialsProvider _awsCredentialsProvider;
	private volatile String _awsRegion;
	private final Supplier<String> _awsRegionSupplier;
	private volatile T _client;
	private final ClientFactory<T> _clientFactory;
	private volatile boolean _closed;
	private final String _fipsEndpointTemplate;
	private final Lock _readLock;
	private volatile boolean _useFIPSEndpoint;
	private final Lock _writeLock;

}