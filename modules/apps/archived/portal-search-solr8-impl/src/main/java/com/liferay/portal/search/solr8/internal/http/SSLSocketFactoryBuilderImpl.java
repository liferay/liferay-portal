/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.http;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.solr8.configuration.SolrSSLSocketFactoryConfiguration;

import java.security.KeyStore;

import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author László Csontos
 * @author André de Oliveira
 */
@Component(
	configurationPid = "com.liferay.portal.search.solr8.configuration.SolrSSLSocketFactoryConfiguration",
	service = SSLSocketFactoryBuilder.class
)
public class SSLSocketFactoryBuilderImpl implements SSLSocketFactoryBuilder {

	@Override
	public SSLConnectionSocketFactory build() throws Exception {
		KeyStore keyStore = _keyStoreLoader.load(
			_keyStoreType, _keyStorePath, _keyStorePassword);

		if (keyStore == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Use system defaults because there is no custom key store");
			}

			return SSLConnectionSocketFactory.getSystemSocketFactory();
		}

		TrustStrategy trustStrategy = null;

		if (_verifyServerCertificate) {
			KeyStore trustKeyStore = _keyStoreLoader.load(
				_trustStoreType, _trustStorePath, _trustStorePassword);

			if (trustKeyStore == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Use system defaults because there is no custom " +
							"trust store");
				}

				return SSLConnectionSocketFactory.getSystemSocketFactory();
			}
		}
		else {
			trustStrategy = new TrustSelfSignedStrategy();
		}

		HostnameVerifier hostnameVerifier = null;

		if (_verifyServerHostname) {
			hostnameVerifier =
				SSLConnectionSocketFactory.getDefaultHostnameVerifier();
		}

		SSLContextBuilder sslContextBuilder = SSLContexts.custom();

		sslContextBuilder.loadKeyMaterial(keyStore, _keyStorePassword);
		sslContextBuilder.loadTrustMaterial(trustStrategy);

		SSLContext sslContext = sslContextBuilder.build();

		try {
			return new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Use system defaults because the custom SSL socket " +
						"factory was not able to initialize",
					exception);
			}

			return SSLConnectionSocketFactory.getSystemSocketFactory();
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_solrSSLSocketFactoryConfiguration =
			ConfigurableUtil.createConfigurable(
				SolrSSLSocketFactoryConfiguration.class, properties);

		String keyStorePassword =
			_solrSSLSocketFactoryConfiguration.keyStorePassword();

		_keyStorePassword = keyStorePassword.toCharArray();

		_keyStorePath = _solrSSLSocketFactoryConfiguration.keyStorePath();
		_keyStoreType = _solrSSLSocketFactoryConfiguration.keyStoreType();

		String trustStorePassword =
			_solrSSLSocketFactoryConfiguration.trustStorePassword();

		_trustStorePassword = trustStorePassword.toCharArray();

		_trustStorePath = _solrSSLSocketFactoryConfiguration.trustStorePath();
		_trustStoreType = _solrSSLSocketFactoryConfiguration.trustStoreType();
		_verifyServerCertificate =
			_solrSSLSocketFactoryConfiguration.verifyServerCertificate();
		_verifyServerHostname =
			_solrSSLSocketFactoryConfiguration.verifyServerName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SSLSocketFactoryBuilderImpl.class);

	@Reference
	private KeyStoreLoader _keyStoreLoader;

	private volatile char[] _keyStorePassword;
	private volatile String _keyStorePath;
	private volatile String _keyStoreType = KeyStore.getDefaultType();
	private volatile SolrSSLSocketFactoryConfiguration
		_solrSSLSocketFactoryConfiguration;
	private volatile char[] _trustStorePassword;
	private volatile String _trustStorePath;
	private volatile String _trustStoreType = KeyStore.getDefaultType();
	private volatile boolean _verifyServerCertificate = true;
	private volatile boolean _verifyServerHostname = true;

}