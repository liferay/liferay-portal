/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.arquillian.extension.junit.bridge.client;

import com.liferay.arquillian.extension.junit.bridge.command.RunNotifierCommand;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.SecureRandom;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
public class ClientState {

	public void filterTestClasses(
		Class<?> testClass, Predicate<Class<?>> predicate) {

		Set<Class<?>> testClasses = _getTestClasses(testClass);

		testClasses.removeIf(predicate);
	}

	public Connection open(
			Class<?> testClass,
			Map<String, List<String>> filteredMethodNamesMap)
		throws Throwable {

		if (Boolean.valueOf(
				System.getProperty("liferay.arquillian.dump.only"))) {

			if (!_dumped) {
				BndBundleUtil.createBundle(
					true, filteredMethodNamesMap, "127.0.0.1", 0, 0);

				_dumped = true;
			}

			return _dummyConnection;
		}

		if (_bundleId == 0) {
			ServerSocket serverSocket = _socketState.getServerSocket();

			Random random = new SecureRandom();

			long passCode = random.nextLong();

			_frameworkState.connect();

			_bundleId = _installBundle(
				filteredMethodNamesMap, serverSocket.getInetAddress(),
				serverSocket.getLocalPort(), passCode);

			try {
				_frameworkState.startBundle(_bundleId);

				_socketState.connect(passCode);
			}
			catch (Throwable throwable) {
				_frameworkState.uninstallBundle(_bundleId);

				throw throwable;
			}
		}

		return new Connection() {

			@Override
			public void close() throws IOException {
				Set<Class<?>> testClasses = _getTestClasses(testClass);

				testClasses.remove(testClass);

				if (testClasses.isEmpty()) {
					try {
						_frameworkState.uninstallBundle(_bundleId);
					}
					catch (Throwable throwable) {
						throw new IOException(
							"Unable to uninstall bundle " + _bundleId + ": " +
								throwable);
					}
					finally {
						_frameworkState.close();

						_socketState.close();

						_testClasses = null;
						_bundleId = 0;
					}
				}
			}

			@Override
			public void execute(
					String testClassName, Consumer<RunNotifierCommand> consumer)
				throws Exception {

				_socketState.writeUTF(testClassName);

				Object object = null;

				while ((object = _socketState.readObject()) != null) {
					consumer.accept((RunNotifierCommand)object);
				}
			}

		};
	}

	public void removeTestClass(Class<?> testClass) {
		Set<Class<?>> testClasses = _getTestClasses(testClass);

		testClasses.remove(testClass);
	}

	public interface Connection extends Closeable {

		public void execute(
				String testClassName, Consumer<RunNotifierCommand> consumer)
			throws Exception;

	}

	private Set<Class<?>> _getTestClasses(Class<?> testClass) {
		if (_testClasses == null) {
			Set<Class<?>> testClasses = new HashSet<>();

			ProtectionDomain protectionDomain = testClass.getProtectionDomain();

			CodeSource codeSource = protectionDomain.getCodeSource();

			URL locationURL = codeSource.getLocation();

			ClassLoader classLoader = testClass.getClassLoader();

			try {
				Path startPath = Paths.get(locationURL.toURI());

				Files.walkFileTree(
					startPath,
					new SimpleFileVisitor<Path>() {

						@Override
						public FileVisitResult visitFile(
							Path filePath,
							BasicFileAttributes basicFileAttributes) {

							String relativePathString = String.valueOf(
								startPath.relativize(filePath));

							if (!relativePathString.endsWith("Test.class")) {
								return FileVisitResult.CONTINUE;
							}

							relativePathString = relativePathString.substring(
								0, relativePathString.length() - 6);

							relativePathString = relativePathString.replace(
								File.separatorChar, '.');

							try {
								Class<?> clazz = classLoader.loadClass(
									relativePathString);

								RunWith runWith = clazz.getAnnotation(
									RunWith.class);

								if ((runWith == null) ||
									(runWith.value() != Arquillian.class)) {

									return FileVisitResult.CONTINUE;
								}

								if (clazz.getAnnotation(Ignore.class) == null) {
									testClasses.add(clazz);
								}
							}
							catch (ClassNotFoundException
										classNotFoundException) {

								throw new RuntimeException(
									classNotFoundException);
							}

							return FileVisitResult.CONTINUE;
						}

					});
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
			catch (URISyntaxException uriSyntaxException) {
				throw new RuntimeException(uriSyntaxException);
			}

			if (!testClasses.contains(testClass)) {
				testClasses.clear();

				testClasses.add(testClass);
			}

			_testClasses = testClasses;
		}

		return _testClasses;
	}

	private long _installBundle(
			Map<String, List<String>> filteredMethodNamesMap,
			InetAddress inetAddress, int port, long passCode)
		throws Throwable {

		byte[] bytes = BndBundleUtil.createBundle(
			Boolean.valueOf(System.getProperty("liferay.arquillian.copy.jar")),
			filteredMethodNamesMap, inetAddress.getHostAddress(), port,
			passCode);

		return _frameworkState.installBundle(
			String.valueOf(System.currentTimeMillis()), bytes);
	}

	private static long _bundleId;

	private static final Connection _dummyConnection = new Connection() {

		@Override
		public void close() {
		}

		@Override
		public void execute(
			String testClassName, Consumer<RunNotifierCommand> consumer) {
		}

	};

	private static boolean _dumped;
	private static final FrameworkState _frameworkState = new FrameworkState();
	private static final SocketState _socketState = new SocketState();
	private static Set<Class<?>> _testClasses;

}