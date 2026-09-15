/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal.jgroups;

import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.cluster.multiple.internal.ClusterChannel;
import com.liferay.portal.cluster.multiple.internal.ClusterChannelFactory;
import com.liferay.portal.cluster.multiple.internal.ClusterReceiver;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SocketUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import javax.crypto.spec.SecretKeySpec;

import org.jgroups.conf.ConfiguratorFactory;
import org.jgroups.conf.ProtocolConfiguration;
import org.jgroups.conf.ProtocolStackConfigurator;
import org.jgroups.protocols.SYM_ENCRYPT;
import org.jgroups.stack.Protocol;

/**
 * @author Tina Tian
 */
public class JGroupsClusterChannelFactory implements ClusterChannelFactory {

	public JGroupsClusterChannelFactory(
		ClusterExecutorConfiguration clusterExecutorConfiguration) {

		_clusterExecutorConfiguration = clusterExecutorConfiguration;

		_initSystemProperties(
			PropsUtil.getArray(
				PropsKeys.CLUSTER_LINK_CHANNEL_SYSTEM_PROPERTIES));

		_initBindAddress(
			GetterUtil.getString(
				PropsUtil.get(PropsKeys.CLUSTER_LINK_AUTODETECT_ADDRESS)));
	}

	@Override
	public ClusterChannel createClusterChannel(
		ExecutorService executorService, String channleLogicName,
		String channelPropertiesLocation, String clusterName,
		ClusterReceiver clusterReceiver) {

		try {
			return new JGroupsClusterChannel(
				executorService, channleLogicName,
				_parseChannelProperties(channelPropertiesLocation, clusterName),
				clusterName, clusterReceiver, _bindInetAddress,
				_clusterExecutorConfiguration, _classLoaders);
		}
		catch (Exception exception) {
			throw new SystemException(
				"Unable to create JGroupsClusterChannel", exception);
		}
	}

	@Override
	public InetAddress getBindInetAddress() {
		return _bindInetAddress;
	}

	@Override
	public NetworkInterface getBindNetworkInterface() {
		return _bindNetworkInterface;
	}

	private InputStream _getInputStream(String channelPropertiesLocation)
		throws IOException {

		InputStream inputStream = ConfiguratorFactory.getConfigStream(
			channelPropertiesLocation);

		if (inputStream == null) {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			inputStream = classLoader.getResourceAsStream(
				channelPropertiesLocation);
		}

		if (inputStream == null) {
			throw new FileNotFoundException(
				"Unable to load channel properties from " +
					channelPropertiesLocation);
		}

		return inputStream;
	}

	private void _initBindAddress(String autodetectAddress) {
		if (Validator.isNull(autodetectAddress)) {
			return;
		}

		String host = autodetectAddress;
		int port = 80;

		int index = autodetectAddress.indexOf(CharPool.COLON);

		if (index != -1) {
			host = autodetectAddress.substring(0, index);
			port = GetterUtil.getInteger(
				autodetectAddress.substring(index + 1), port);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Autodetecting JGroups outgoing IP address and interface ",
					"for ", host, ":", port));
		}

		try {
			SocketUtil.BindInfo bindInfo = SocketUtil.getBindInfo(host, port);

			_bindInetAddress = bindInfo.getInetAddress();

			_bindNetworkInterface = bindInfo.getNetworkInterface();
		}
		catch (IOException ioException1) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to detect bind address for JGroups, using " +
						"loopback");

				if (_log.isDebugEnabled()) {
					_log.debug(ioException1);
				}
			}

			_bindInetAddress = InetAddress.getLoopbackAddress();

			try {
				_bindNetworkInterface = NetworkInterface.getByInetAddress(
					_bindInetAddress);
			}
			catch (IOException ioException2) {
				_log.error(
					"Unable to bind to lopoback interface", ioException2);
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Setting JGroups outgoing IP address to ",
					_bindInetAddress.getHostAddress(), " and interface to ",
					_bindNetworkInterface.getName()));
		}
	}

	private void _initSystemProperties(String[] channelSystemPropertiesArray) {
		for (String channelSystemProperty : channelSystemPropertiesArray) {
			int index = channelSystemProperty.indexOf(CharPool.COLON);

			if (index == -1) {
				continue;
			}

			String key = channelSystemProperty.substring(0, index);
			String value = channelSystemProperty.substring(index + 1);

			System.setProperty(key, value);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Setting system property {key=", key, ", value=", value,
						"}"));
			}
		}
	}

	private ProtocolStackConfigurator _parseChannelProperties(
			String channelPropertiesLocation, String clusterName)
		throws Exception {

		try (InputStream inputStream = _getInputStream(
				channelPropertiesLocation)) {

			String configXML = StreamUtil.toString(inputStream);

			int index = 0;

			StringBundler sb = new StringBundler();

			while (index < configXML.length()) {
				int startIndex = configXML.indexOf(
					StringPool.DOLLAR_AND_OPEN_CURLY_BRACE, index);

				if (startIndex < 0) {
					break;
				}

				int endIndex = configXML.indexOf(
					StringPool.CLOSE_CURLY_BRACE, startIndex);

				if (endIndex < 0) {
					break;
				}

				String propertyKey = configXML.substring(
					startIndex + 2, endIndex);

				Object value = PropsUtil.get(
					StringUtil.replace(
						propertyKey, _ENCODED_CHARACTERS,
						_ORIGINAL_CHARACTERS));

				if (propertyKey.equals(PropsKeys.CLUSTER_LINK_AUTH_VALUE) &&
					value.equals("liferay-cluster") && _log.isWarnEnabled() &&
					_defaultSecretWarning) {

					_log.warn(
						StringBundler.concat(
							"Clustering authentication is using default ",
							"cluster link authentication value. Please ",
							"configure the property \"",
							PropsKeys.CLUSTER_LINK_AUTH_VALUE,
							"\" in portal.properties."));

					_defaultSecretWarning = false;
				}

				if (value instanceof String) {
					sb.append(configXML.substring(index, startIndex));
					sb.append(
						StringUtil.replace(
							(String)value, _ORIGINAL_CHARACTERS,
							_ENCODED_CHARACTERS));
				}
				else {
					sb.append(configXML.substring(index, endIndex + 1));
				}

				index = endIndex + 1;
			}

			if (sb.length() > 0) {
				if (index < configXML.length()) {
					sb.append(configXML.substring(index));
				}

				configXML = sb.toString();
			}

			ProtocolStackConfigurator protocolStackConfigurator =
				ConfiguratorFactory.getStackConfigurator(
					new UnsyncByteArrayInputStream(
						configXML.getBytes(StringPool.UTF8)));

			if (channelPropertiesLocation.startsWith(
					"jgroups/secure/sym_encrypt/")) {

				if (_log.isWarnEnabled() && _defaultSymEncryptWarning) {
					_log.warn(
						StringBundler.concat(
							"Clustering authentication is using SYM_ENCRYPT ",
							"default implementation. Please note that this ",
							"implementation is not secure enough to be used ",
							"in production. Refer to the documentation for ",
							"details on configuring secure JGroups ",
							"connections."));

					_defaultSymEncryptWarning = false;
				}

				return new SymEncryptProtocolStackConfigurator(
					clusterName, protocolStackConfigurator);
			}

			return protocolStackConfigurator;
		}
	}

	private static final String[] _ENCODED_CHARACTERS = {
		StringPool.AMPERSAND_ENCODED, StringPool.QUOTE_ENCODED,
		StringPool.APOSTROPHE_ENCODED, "&gt;", "&lt;"
	};

	private static final String[] _ORIGINAL_CHARACTERS = {
		StringPool.AMPERSAND, StringPool.QUOTE, StringPool.APOSTROPHE,
		StringPool.GREATER_THAN, StringPool.LESS_THAN
	};

	private static final Log _log = LogFactoryUtil.getLog(
		JGroupsClusterChannelFactory.class);

	private static boolean _defaultSecretWarning = true;
	private static boolean _defaultSymEncryptWarning = true;

	private InetAddress _bindInetAddress;
	private NetworkInterface _bindNetworkInterface;
	private final ConcurrentMap<ClassLoader, ClassLoader> _classLoaders =
		new ConcurrentReferenceKeyHashMap<>(
			FinalizeManager.WEAK_REFERENCE_FACTORY);
	private volatile ClusterExecutorConfiguration _clusterExecutorConfiguration;

	private static class SymEncryptProtocolStackConfigurator
		implements ProtocolStackConfigurator {

		@Override
		public void afterCreation(Protocol protocol) {
			if (!(protocol instanceof SYM_ENCRYPT symEncrypt) ||
				(symEncrypt.keystoreName() != null)) {

				return;
			}

			symEncrypt.setSecretKey(
				new SecretKeySpec(
					DigesterUtil.digestRaw(DigesterUtil.SHA_256, _clusterName),
					"AES"));
		}

		@Override
		public List<ProtocolConfiguration> getProtocolStack() {
			return _protocolStackConfigurator.getProtocolStack();
		}

		@Override
		public String getProtocolStackString() {
			return _protocolStackConfigurator.getProtocolStackString();
		}

		private SymEncryptProtocolStackConfigurator(
			String clusterName,
			ProtocolStackConfigurator protocolStackConfigurator) {

			_clusterName = clusterName;
			_protocolStackConfigurator = protocolStackConfigurator;
		}

		private final String _clusterName;
		private final ProtocolStackConfigurator _protocolStackConfigurator;

	}

}