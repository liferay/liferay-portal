/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.reflect.ReflectionUtil;

import java.io.InputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.cli.ExitCodes;
import org.elasticsearch.common.hash.MessageDigests;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.logging.LogConfigurator;
import org.elasticsearch.common.settings.KeyStoreWrapper;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.BoundTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.http.HttpServerTransport;
import org.elasticsearch.xcontent.XContentType;

/**
 * @author Tina Tian
 */
public class ElasticsearchServerUtil {

	public static String getAddress() throws ProcessException {
		try {
			ClassLoader classLoader =
				ElasticsearchServerUtil.class.getClassLoader();

			Method getInstanceMethod = ReflectionUtil.getDeclaredMethod(
				classLoader.loadClass(
					"org.elasticsearch.injection.guice.Injector"),
				"getInstance", Class.class);
			Method injectorMethod = ReflectionUtil.getDeclaredMethod(
				classLoader.loadClass("org.elasticsearch.node.Node"),
				"injector");

			HttpServerTransport httpServerTransport =
				(HttpServerTransport)getInstanceMethod.invoke(
					injectorMethod.invoke(
						_nodeField.get(_instanceField.get(null))),
					HttpServerTransport.class);

			BoundTransportAddress boundTransportAddress =
				httpServerTransport.boundAddress();

			TransportAddress publishAddress =
				boundTransportAddress.publishAddress();

			return publishAddress.toString();
		}
		catch (Exception exception) {
			throw new ProcessException(exception);
		}
	}

	public static void shutdown() {
		try {
			_stopMethod.invoke(null);
		}
		catch (Exception exception) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("Unable to invoke stop method", exception);
			}

			System.exit(ExitCodes.CODE_ERROR);
		}

		_shutdownCountDownLatch.countDown();
	}

	public static void start() throws ProcessException {
		InputStream originalSystemInInputStream = System.in;

		try (UnsyncByteArrayInputStream unsyncByteArrayInputStream =
				new UnsyncByteArrayInputStream(_getSidecarServerArgs())) {

			System.setIn(unsyncByteArrayInputStream);

			_mainMethod.invoke(null, (Object)null);

			System.setSecurityManager(null);

			_addShutdownHook();
		}
		catch (Exception exception) {
			throw new ProcessException(
				"Unable to start Elasticsearch server", exception);
		}
		finally {
			System.setIn(originalSystemInInputStream);
		}
	}

	public static void waitForShutdown() throws ProcessException {
		try {
			_shutdownCountDownLatch.await();
		}
		catch (InterruptedException interruptedException) {
			throw new ProcessException(
				"Sidecar main thread is interrupted", interruptedException);
		}
	}

	private static void _addShutdownHook() throws ReflectiveOperationException {
		synchronized (_hooksField.getDeclaringClass()) {
			Map<Thread, Thread> hooks = (Map<Thread, Thread>)_hooksField.get(
				null);

			Set<Thread> threads = new HashSet<>(hooks.keySet());

			hooks.clear();

			Thread shutdownHook = new Thread(
				() -> {
					try {
						_shutdownCountDownLatch.await();
					}
					catch (InterruptedException interruptedException) {
						if (_logger.isDebugEnabled()) {
							_logger.debug(interruptedException);
						}
					}

					for (Thread thread : threads) {
						thread.start();
					}

					for (Thread thread : threads) {
						while (true) {
							try {
								thread.join();

								break;
							}
							catch (InterruptedException interruptedException) {
								if (_logger.isDebugEnabled()) {
									_logger.debug(interruptedException);
								}
							}
						}
					}
				},
				"Elasticsearch Server Shutdown Hook");

			hooks.put(shutdownHook, shutdownHook);
		}
	}

	private static byte[] _getSidecarServerArgs() {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			StreamOutput streamOutput = new OutputStreamStreamOutput(
				unsyncByteArrayOutputStream)) {

			streamOutput.writeBoolean(false);
			streamOutput.writeBoolean(false);
			streamOutput.writeOptionalString(null);
			streamOutput.writeString(KeyStoreWrapper.class.getName());

			try (KeyStoreWrapper keyStoreWrapper = KeyStoreWrapper.create()) {
				streamOutput.writeInt(keyStoreWrapper.getFormatVersion());
				streamOutput.writeBoolean(keyStoreWrapper.hasPassword());
				streamOutput.writeBoolean(false);
				streamOutput.writeVInt(1);
				streamOutput.writeString(KeyStoreWrapper.SEED_SETTING.getKey());

				ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(
					ElasticsearchServerUtil.class.getSimpleName());

				byte[] bytes = byteBuffer.array();

				MessageDigest messageDigest = MessageDigests.sha256();

				streamOutput.writeByteArray(bytes);
				streamOutput.writeByteArray(messageDigest.digest(bytes));
				streamOutput.writeBoolean(false);
			}

			Method method = ReflectionUtil.getDeclaredMethod(
				LogConfigurator.class, "configureESLogging");

			method.invoke(null);

			Settings.Builder builder = Settings.builder();

			builder.loadFromSource(
				System.getProperty("sidecar.settings"), XContentType.YAML);

			Settings settings = builder.build();

			method = ReflectionUtil.getDeclaredMethod(
				Settings.class, "writeTo", new Class<?>[] {StreamOutput.class});

			method.invoke(settings, streamOutput);

			streamOutput.writeString(System.getProperty("es.path.conf"));
			streamOutput.writeString(settings.get("path.logs"));

			streamOutput.flush();

			return unsyncByteArrayOutputStream.toByteArray();
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to prepare sidecar server arguments", exception);
		}
	}

	private static final Logger _logger = LogManager.getLogger(
		ElasticsearchServerUtil.class);

	private static final Field _hooksField;
	private static final Field _instanceField;
	private static final Method _mainMethod;
	private static final Field _nodeField;
	private static final CountDownLatch _shutdownCountDownLatch =
		new CountDownLatch(1);
	private static final Method _stopMethod;

	static {
		try {
			ClassLoader classLoader =
				ElasticsearchServerUtil.class.getClassLoader();

			_hooksField = ReflectionUtil.getDeclaredField(
				classLoader.loadClass("java.lang.ApplicationShutdownHooks"),
				"hooks");

			Class<?> elasticsearchClass = classLoader.loadClass(
				"org.elasticsearch.bootstrap.Elasticsearch");

			_instanceField = ReflectionUtil.getDeclaredField(
				elasticsearchClass, "INSTANCE");
			_mainMethod = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "main", String[].class);
			_nodeField = ReflectionUtil.getDeclaredField(
				elasticsearchClass, "node");
			_stopMethod = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "shutdown");
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

}