/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import java.io.File;
import java.io.FileFilter;

import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Shuyang Zhou
 */
public class PortalClassPathUtil {

	public static ProcessConfig createBundleProcessConfig(Class<?> seedClass) {
		ProcessConfig.Builder builder = new ProcessConfig.Builder(
			_portalProcessConfig);

		builder.setReactClassLoader(
			AggregateClassLoader.getAggregateClassLoader(
				PortalClassLoaderUtil.getClassLoader(),
				seedClass.getClassLoader()));
		builder.setRuntimeClassPath(
			_buildRuntimeClasspath(
				seedClass, _portalProcessConfig.getRuntimeClassPath()));

		return builder.build();
	}

	public static ProcessConfig createProcessConfig(Class<?>... classes) {
		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		builder.setArguments(_processArgs);

		File[] files = _listClassPathFiles(classes);

		if (files.length == 0) {
			throw new IllegalStateException(
				"Class path files could not be loaded");
		}

		StringBundler sb = new StringBundler((files.length * 2) + 1);

		for (File file : files) {
			sb.append(file.getAbsolutePath());
			sb.append(File.pathSeparator);
		}

		sb.append(_portalProcessConfig.getBootstrapClassPath());

		String classpath = sb.toString();

		builder.setBootstrapClassPath(classpath);

		builder.setProcessLogConsumer(
			processLog -> {
				if (ProcessLog.Level.DEBUG == processLog.getLevel()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.INFO == processLog.getLevel()) {
					if (_log.isInfoEnabled()) {
						_log.info(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.WARN == processLog.getLevel()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else {
					_log.error(
						processLog.getMessage(), processLog.getThrowable());
				}
			});
		builder.setReactClassLoader(PortalClassLoaderUtil.getClassLoader());
		builder.setRuntimeClassPath(classpath);

		return builder.build();
	}

	public static ProcessConfig getPortalProcessConfig() {
		return _portalProcessConfig;
	}

	public static void initializeClassPaths(ServletContext servletContext) {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		if (classLoader == null) {
			Thread currentThread = Thread.currentThread();

			classLoader = currentThread.getContextClassLoader();
		}

		Class<?> shieldedContainerInitializerClass = null;

		try {
			shieldedContainerInitializerClass = classLoader.loadClass(
				"com.liferay.shielded.container.ShieldedContainerInitializer");
		}
		catch (ClassNotFoundException classNotFoundException) {
			_log.error(
				"Unable to load ShieldedContainerInitializer class",
				classNotFoundException);
		}

		File[] files = _listClassPathFiles(
			ServletException.class, CentralizedThreadLocal.class,
			shieldedContainerInitializerClass);

		if (files.length == 0) {
			throw new IllegalStateException(
				"Class path files could not be loaded");
		}

		StringBundler runtimeClassPathSB = new StringBundler(
			(files.length * 2) + 3);
		StringBundler bootstrapClassPathSB = new StringBundler(
			files.length * 2);

		if (servletContext != null) {
			runtimeClassPathSB.append(servletContext.getRealPath(""));
			runtimeClassPathSB.append("/WEB-INF/classes");
			runtimeClassPathSB.append(File.pathSeparator);
		}

		for (File file : files) {
			String filePath = file.getAbsolutePath();

			if (filePath.contains("petra")) {
				bootstrapClassPathSB.append(file.getAbsolutePath());
				bootstrapClassPathSB.append(File.pathSeparator);
			}

			runtimeClassPathSB.append(file.getAbsolutePath());
			runtimeClassPathSB.append(File.pathSeparator);
		}

		runtimeClassPathSB.setIndex(runtimeClassPathSB.index() - 1);

		if (bootstrapClassPathSB.index() > 0) {
			bootstrapClassPathSB.setIndex(bootstrapClassPathSB.index() - 1);
		}

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		builder.setArguments(_processArgs);
		builder.setBootstrapClassPath(bootstrapClassPathSB.toString());
		builder.setReactClassLoader(classLoader);
		builder.setRuntimeClassPath(runtimeClassPathSB.toString());

		_portalProcessConfig = builder.build();
	}

	private static String _buildRuntimeClasspath(
		Class<?> clazz, String portalRuntiemClasspath) {

		Set<Bundle> bundles = new LinkedHashSet<>();

		Bundle currentBundle = FrameworkUtil.getBundle(clazz);

		bundles.add(currentBundle);

		BundleWiring bundleWiring = currentBundle.adapt(BundleWiring.class);

		List<BundleWire> requiredBundleWires = bundleWiring.getRequiredWires(
			null);

		if (requiredBundleWires != null) {
			for (BundleWire bundleWire : requiredBundleWires) {
				BundleRevision bundleRevision = bundleWire.getProvider();

				Bundle requiredBundle = bundleRevision.getBundle();

				if (requiredBundle.getBundleId() != 0) {
					bundles.add(requiredBundle);
				}
			}
		}

		StringBundler sb = new StringBundler();

		for (Bundle bundle : bundles) {
			File bundleDataDir = bundle.getDataFile(null);

			File bundleDir = bundleDataDir.getParentFile();

			File[] files = bundleDir.listFiles(
				file -> file.isDirectory() && !file.equals(bundleDataDir));

			if ((files != null) && (files.length > 0)) {
				Arrays.sort(
					files,
					Comparator.comparing(
						file -> GetterUtil.getInteger(file.getName(), -1),
						Comparator.reverseOrder()));

				File bundleRevisionDir = files[0];

				File bundleFile = new File(bundleRevisionDir, "bundleFile");

				sb.append(bundleFile.getAbsolutePath());

				sb.append(File.pathSeparator);

				File cpLibDir = new File(bundleRevisionDir, ".cp");

				if (cpLibDir.exists()) {
					Queue<File> queue = new LinkedList<>();

					queue.add(cpLibDir);

					File currentFile = null;

					while ((currentFile = queue.poll()) != null) {
						if (currentFile.isDirectory()) {
							Collections.addAll(queue, currentFile.listFiles());
						}
						else {
							sb.append(currentFile.getAbsolutePath());
							sb.append(File.pathSeparator);
						}
					}
				}
			}
		}

		sb.append(portalRuntiemClasspath);

		return sb.toString();
	}

	private static File[] _listClassPathFiles(Class<?> clazz) {
		String className = clazz.getName();
		ClassLoader classLoader = clazz.getClassLoader();

		String pathOfClass = StringUtil.replace(
			className, CharPool.PERIOD, CharPool.SLASH);

		pathOfClass = pathOfClass.concat(".class");

		URL url = classLoader.getResource(pathOfClass);

		if (_log.isDebugEnabled()) {
			_log.debug("Build class path from " + url);
		}

		String protocol = url.getProtocol();

		if (protocol.equals("bundle") || protocol.equals("bundleresource")) {
			try {
				URLConnection urlConnection = url.openConnection();

				Class<?> urlConnectionClass = urlConnection.getClass();

				Method getLocalURLMethod = urlConnectionClass.getDeclaredMethod(
					"getLocalURL");

				getLocalURLMethod.setAccessible(true);

				url = (URL)getLocalURLMethod.invoke(urlConnection);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to resolve local URL from bundle", exception);

				return null;
			}
		}

		String path = URLCodec.decodeURL(url.getPath());

		if (_log.isDebugEnabled()) {
			_log.debug("Path " + path);
		}

		path = StringUtil.replace(path, CharPool.BACK_SLASH, CharPool.SLASH);

		if (_log.isDebugEnabled()) {
			_log.debug("Decoded path " + path);
		}

		if (ServerDetector.isWebLogic() && protocol.equals("zip")) {
			path = "file:".concat(path);
		}

		if ((ServerDetector.isJBoss() || ServerDetector.isWildfly()) &&
			(protocol.equals("vfs") || protocol.equals("vfsfile") ||
			 protocol.equals("vfszip"))) {

			int pos = path.indexOf(".jar/");

			if (pos != -1) {
				String jarFilePath = path.substring(0, pos + 4);

				File jarFile = new File(jarFilePath);

				if (jarFile.isFile()) {
					path = jarFilePath + '!' + path.substring(pos + 4);
				}
			}

			path = "file:".concat(path);
		}

		File dir = null;

		int pos = -1;

		if (!path.startsWith("file:") ||
			((pos = path.indexOf(CharPool.EXCLAMATION)) == -1)) {

			if (!path.endsWith(pathOfClass)) {
				_log.error(
					"Class " + className + " is not loaded from a JAR file");

				return null;
			}

			String classesDirName = path.substring(
				0, path.length() - pathOfClass.length());

			if (!classesDirName.endsWith("/WEB-INF/classes/")) {
				_log.error(
					StringBundler.concat(
						"Class ", className, " is not loaded from a standard ",
						"location (/WEB-INF/classes)"));

				return null;
			}

			String libDirName = classesDirName.substring(
				0, classesDirName.length() - "classes/".length());

			libDirName += "/lib";

			dir = new File(libDirName);
		}
		else {
			pos = path.lastIndexOf(CharPool.SLASH, pos);

			dir = new File(path.substring("file:".length(), pos));
		}

		if (!dir.isDirectory()) {
			_log.error(dir.toString() + " is not a directory");

			return null;
		}

		return dir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return false;
					}

					String name = file.getName();

					if (name.equals("bundleFile") || name.endsWith(".jar")) {
						return true;
					}

					return false;
				}

			});
	}

	private static File[] _listClassPathFiles(Class<?>... classes) {
		Set<File> filesSet = new HashSet<>();

		for (Class<?> clazz : classes) {
			File[] files = _listClassPathFiles(clazz);

			if (files != null) {
				Collections.addAll(filesSet, files);
			}
		}

		File[] files = filesSet.toArray(new File[0]);

		Arrays.sort(files);

		return files;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalClassPathUtil.class);

	private static ProcessConfig _portalProcessConfig;
	private static final List<String> _processArgs = Arrays.asList(
		"-Dconfiguration.impl.quiet=true", "-Djava.awt.headless=true",
		"-Dserver.detector.quiet=true", "-Dsystem.properties.quiet=true");

}