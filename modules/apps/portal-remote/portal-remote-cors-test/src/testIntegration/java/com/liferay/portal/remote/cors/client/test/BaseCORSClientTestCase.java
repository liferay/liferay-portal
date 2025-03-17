/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.client.test;

import com.liferay.petra.io.ClassLoaderObjectInputStream;
import com.liferay.petra.lang.ClassResolverUtil;
import com.liferay.petra.process.ClassPathUtil;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.local.LocalProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Application;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 * @author Marta Medio
 */
public abstract class BaseCORSClientTestCase {

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(BaseCORSClientTestCase.class);

		_bundleContext = bundle.getBundleContext();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		for (AutoCloseable autoCloseable : _autoCloseables) {
			autoCloseable.close();
		}

		_autoCloseables.clear();
	}

	protected void assertJaxRSUrl(
			String urlString, String method, boolean authenticate,
			boolean allowOrigin)
		throws Exception {

		assertJaxRSUrl(urlString, method, authenticate, allowOrigin, null);
	}

	protected void assertJaxRSUrl(
			String urlString, String method, boolean authenticate,
			boolean allowOrigin, String allowedOrigin)
		throws Exception {

		if (allowedOrigin == null) {
			allowedOrigin = _TEST_CORS_URI;
		}

		ProcessConfig.Builder builder = _generateTestBuilder();

		ProcessExecutor processExecutor = new LocalProcessExecutor();

		ProcessChannel<String[]> processChannel = processExecutor.execute(
			builder.build(),
			new AllowRestrictedHeadersCallable(
				"http://localhost:8080/o" + urlString, allowedOrigin, method,
				authenticate));

		Future<String[]> future = processChannel.getProcessNoticeableFuture();

		String[] results = future.get();

		if (allowOrigin) {
			Assert.assertEquals(allowedOrigin, results[0]);
		}
		else {
			Assert.assertNull(results[0]);
		}

		if (!HttpMethod.OPTIONS.equals(method)) {
			Assert.assertNotEquals(StringPool.BLANK, results[1]);
		}

		Assert.assertEquals("200", results[2]);
	}

	protected void assertJsonWSUrl(
			String urlString, String method, boolean allowOrigin)
		throws Exception {

		assertJsonWSUrl(urlString, method, allowOrigin, null);
	}

	protected void assertJsonWSUrl(
			String urlString, String method, boolean allowOrigin,
			String allowedOrigin)
		throws Exception {

		if (allowedOrigin == null) {
			allowedOrigin = _TEST_CORS_URI;
		}

		ProcessConfig.Builder builder = _generateTestBuilder();

		ProcessExecutor processExecutor = new LocalProcessExecutor();

		ProcessChannel<String[]> processChannel = processExecutor.execute(
			builder.build(),
			new AllowRestrictedHeadersCallable(
				"http://localhost:8080/api/jsonws" + urlString, allowedOrigin,
				method, true));

		Future<String[]> future = processChannel.getProcessNoticeableFuture();

		String[] results = future.get();

		if (allowOrigin) {
			Assert.assertEquals(allowedOrigin, results[0]);
		}
		else {
			Assert.assertNull(results[0]);
		}

		if (!HttpMethod.OPTIONS.equals(method)) {
			Assert.assertNotEquals(StringPool.BLANK, results[1]);
		}

		Assert.assertEquals("200", results[2]);
	}

	protected void createFactoryConfiguration(
		String configurationClassName, Dictionary<String, Object> properties) {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		Dictionary<String, Object> registrationProperties =
			HashMapDictionaryBuilder.<String, Object>put(
				Constants.SERVICE_PID, configurationClassName
			).build();

		ServiceRegistration<ManagedServiceFactory> serviceRegistration =
			_bundleContext.registerService(
				ManagedServiceFactory.class,
				new ManagedServiceFactory() {

					@Override
					public void deleted(String pid) {
					}

					@Override
					public String getName() {
						return "Test managed service factory for PID " +
							configurationClassName;
					}

					@Override
					public void updated(
						String pid, Dictionary<String, ?> updatedProperties) {

						if ((updatedProperties == null) ||
							(properties.size() > updatedProperties.size())) {

							return;
						}

						Enumeration<String> enumeration = properties.keys();

						while (enumeration.hasMoreElements()) {
							String key = enumeration.nextElement();

							if (!Objects.deepEquals(
									properties.get(key),
									updatedProperties.get(key))) {

								return;
							}
						}

						countDownLatch.countDown();
					}

				},
				registrationProperties);

		try {
			ServiceReference<ConfigurationAdmin> serviceReference =
				_bundleContext.getServiceReference(ConfigurationAdmin.class);

			ConfigurationAdmin configurationAdmin = _bundleContext.getService(
				serviceReference);

			Configuration configuration = null;

			try {
				configuration = configurationAdmin.createFactoryConfiguration(
					configurationClassName, StringPool.QUESTION);

				configuration.update(properties);

				countDownLatch.await(5, TimeUnit.MINUTES);

				_autoCloseables.add(configuration::delete);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
			catch (InterruptedException interruptedException) {
				try {
					configuration.delete();
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}

				throw new RuntimeException(interruptedException);
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	protected void registerJaxRsApplication(
		Application application, String path,
		Dictionary<String, Object> properties) {

		if ((properties == null) || properties.isEmpty()) {
			properties = new HashMapDictionary<>();
		}

		properties.put("liferay.access.control.disable", true);
		properties.put("liferay.oauth2", false);
		properties.put("osgi.jaxrs.application.base", "/" + path);

		ServiceRegistration<Application> serviceRegistration =
			_bundleContext.registerService(
				Application.class, application, properties);

		_autoCloseables.add(serviceRegistration::unregister);
	}

	private void _addToClassPath(StringBundler sb, Class<?> clazz) {
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL location = codeSource.getLocation();

		sb.append(File.pathSeparator);
		sb.append(location.getPath());
	}

	private ProcessConfig.Builder _generateTestBuilder() {
		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		List<String> arguments = new ArrayList<>();

		arguments.add("-Djava.net.preferIPv4Stack=true");

		if (Boolean.getBoolean("jvm.debug")) {
			arguments.add(
				"-agentlib:jdwp=transport=dt_socket,address=8001,server=y," +
					"suspend=y");
			arguments.add("-Djvm.debug=true");
		}

		arguments.add("-Dsun.net.http.allowRestrictedHeaders=true");
		arguments.add("-Dsun.zip.disableMemoryMapping=true");

		builder.setArguments(arguments);

		StringBundler sb = new StringBundler();

		sb.append(ClassPathUtil.getJVMClassPath(true));

		_addToClassPath(sb, AllowRestrictedHeadersCallable.class);
		_addToClassPath(sb, ClassPathUtil.class);
		_addToClassPath(sb, ClassResolverUtil.class);
		_addToClassPath(sb, ClassLoaderObjectInputStream.class);
		_addToClassPath(sb, StringBundler.class);
		_addToClassPath(sb, StringUtil.class);

		String classPath = sb.toString();

		builder.setBootstrapClassPath(classPath);
		builder.setRuntimeClassPath(classPath);

		return builder;
	}

	private static final String _TEST_CORS_URI = "http://test-cors.com";

	private static final Queue<AutoCloseable> _autoCloseables =
		new ArrayDeque<>();
	private static BundleContext _bundleContext;

}