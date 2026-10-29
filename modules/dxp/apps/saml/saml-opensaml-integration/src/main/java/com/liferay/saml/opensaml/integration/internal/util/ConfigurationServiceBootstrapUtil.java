/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

import net.shibboleth.utilities.java.support.xml.BasicParserPool;

import org.apache.xml.security.stax.ext.XMLSecurityConstants;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.opensaml.xmlsec.signature.support.Signer;

/**
 * @author Shuyang Zhou
 */
public class ConfigurationServiceBootstrapUtil {

	public static <T> T get(Class<T> configurationClass) {
		if (!_initialized) {
			_initialize();
		}

		return ConfigurationService.get(configurationClass);
	}

	public static <T> void register(
		Class<T> configurationClass, T configuration) {

		synchronized (_configurations) {
			_configurations.put(configurationClass, configuration);

			if (_initialized) {
				ConfigurationService.register(
					configurationClass, configuration);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static synchronized void _initialize() {
		if (_initialized) {
			return;
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				AggregateClassLoader.getAggregateClassLoader(
					ConfigurationServiceBootstrapUtil.class.getClassLoader(),
					PortalClassLoaderUtil.getClassLoader()))) {

			InitializationService.initialize();

			_initializeParserPool();

			Method method = Signer.class.getDeclaredMethod("getSignerProvider");

			method.setAccessible(true);

			method.invoke(null);

			method = SignatureValidator.class.getDeclaredMethod(
				"getSignatureValidationProvider");

			method.setAccessible(true);

			method.invoke(null);

			if (XMLSecurityConstants.xmlOutputFactory == null) {
				throw new IllegalStateException();
			}
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}

		synchronized (_configurations) {
			for (Map.Entry<Class<?>, Object> entry :
					_configurations.entrySet()) {

				ConfigurationService.register(
					(Class<Object>)entry.getKey(), entry.getValue());
			}

			_initialized = true;
		}
	}

	private static void _initializeParserPool() throws InitializationException {
		BasicParserPool parserPool = new BasicParserPool();

		parserPool.setBuilderFeatures(
			HashMapBuilder.put(
				"http://apache.org/xml/features/disallow-doctype-decl",
				Boolean.TRUE
			).put(
				"http://apache.org/xml/features/dom/defer-node-expansion",
				Boolean.FALSE
			).put(
				"http://javax.xml.XMLConstants/feature/secure-processing",
				Boolean.TRUE
			).put(
				"http://xml.org/sax/features/external-general-entities",
				Boolean.FALSE
			).put(
				"http://xml.org/sax/features/external-parameter-entities",
				Boolean.FALSE
			).build());

		parserPool.setDTDValidating(false);
		parserPool.setExpandEntityReferences(false);
		parserPool.setMaxPoolSize(50);
		parserPool.setNamespaceAware(true);

		try {
			parserPool.initialize();

			parserPool.getBuilder();

			XMLObjectProviderRegistry xmlObjectProviderRegistry =
				ConfigurationService.get(XMLObjectProviderRegistry.class);

			xmlObjectProviderRegistry.setParserPool(parserPool);
		}
		catch (Exception exception) {
			throw new InitializationException(
				"Unable to initialize parser pool: " + exception.getMessage(),
				exception);
		}
	}

	private static final Map<Class<?>, Object> _configurations =
		new HashMap<>();
	private static volatile boolean _initialized;

}