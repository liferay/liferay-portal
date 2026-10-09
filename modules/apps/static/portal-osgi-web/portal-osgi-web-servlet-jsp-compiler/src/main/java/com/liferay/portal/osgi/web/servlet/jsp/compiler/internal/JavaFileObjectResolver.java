/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.concurrent.ConcurrentReferenceValueHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.osgi.web.servlet.jsp.compiler.internal.util.ClassPathUtil;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.JavaFileObject;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Raymond Augé
 */
public class JavaFileObjectResolver {

	public JavaFileObjectResolver(
		BundleWiring bundleWiring,
		Map<BundleWiring, Set<String>> bundleWiringPackageNames) {

		_bundleWiring = bundleWiring;
		_bundleWiringPackageNames = bundleWiringPackageNames;
	}

	public Collection<JavaFileObject> resolveClasses(
		boolean recurse, String packagePath) {

		List<JavaFileObject> javaFileObjects = new ArrayList<>();

		int options = 0;

		if (recurse) {
			options = BundleWiring.LISTRESOURCES_RECURSE;
		}

		javaFileObjects.addAll(
			_toJavaFileObjects(
				_bundleWiring.getBundle(),
				_bundleWiring.listResources(packagePath, "*.class", options)));

		String packageName = StringUtil.replace(
			packagePath, CharPool.SLASH, CharPool.PERIOD);

		for (Map.Entry<BundleWiring, Set<String>> entry :
				_bundleWiringPackageNames.entrySet()) {

			Set<String> packageNames = entry.getValue();

			if (!packageNames.contains(packageName)) {
				continue;
			}

			javaFileObjects.addAll(
				_resolveClasses(entry.getKey(), packagePath, options));
		}

		return javaFileObjects;
	}

	private String _getClassName(String classResourceName) {
		classResourceName = classResourceName.substring(
			0, classResourceName.length() - 6);

		return StringUtil.replace(
			classResourceName, CharPool.SLASH, CharPool.PERIOD);
	}

	private JavaFileObject _getJavaFileObject(
		URL resourceURL, String resourceName) {

		String protocol = resourceURL.getProtocol();

		String className = _getClassName(resourceName);

		if (protocol.equals("bundle") || protocol.equals("bundleresource")) {
			return new BundleJavaFileObject(className, resourceURL);
		}
		else if (protocol.equals("jar")) {
			try {
				return new JarJavaFileObject(
					className, ClassPathUtil.getFile(resourceURL),
					resourceName);
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}
		}
		else if (protocol.equals("vfs")) {
			try {
				return new VfsJavaFileObject(
					className, resourceURL, resourceName);
			}
			catch (MalformedURLException malformedURLException) {
				_log.error(malformedURLException);
			}
		}

		return null;
	}

	private Collection<JavaFileObject> _handleSystemBundle(
		BundleWiring bundleWiring, String path) {

		Collection<JavaFileObject> javaFileObjects = _javaFileObjects.get(path);

		if (javaFileObjects != null) {
			return javaFileObjects;
		}

		List<URL> urls = null;

		ClassLoader classLoader = bundleWiring.getClassLoader();

		try {
			Enumeration<URL> enumeration = classLoader.getResources(path);

			if ((enumeration != null) && enumeration.hasMoreElements()) {
				urls = Collections.list(enumeration);
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		if (ListUtil.isEmpty(urls)) {
			_javaFileObjects.put(path, Collections.<JavaFileObject>emptyList());

			return Collections.emptyList();
		}

		for (URL url : urls) {
			try {
				File file = ClassPathUtil.getFile(url);

				if (file == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Ignoring " + url +
								" while handling system bundle");
					}

					continue;
				}

				try (FileSystem fileSystem = FileSystems.newFileSystem(
						file.toPath(), (ClassLoader)null)) {

					FileSystemProvider fileSystemProvider =
						fileSystem.provider();

					try (DirectoryStream<Path> directoryStream =
							fileSystemProvider.newDirectoryStream(
								fileSystem.getPath(path),
								new DirectoryStream.Filter<Path>() {

									@Override
									public boolean accept(Path entryPath) {
										String entryPathString =
											entryPath.toString();

										return entryPathString.endsWith(
											".class");
									}

								})) {

						for (Path entryPath : directoryStream) {
							if (javaFileObjects == null) {
								javaFileObjects = new ArrayList<>();
							}

							String entryPathString = entryPath.toString();

							if (entryPathString.charAt(0) == CharPool.SLASH) {
								entryPathString = entryPathString.substring(1);
							}

							javaFileObjects.add(
								new JarJavaFileObject(
									_getClassName(entryPathString), file,
									entryPathString));
						}
					}
				}
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}
		}

		if (javaFileObjects == null) {
			javaFileObjects = Collections.<JavaFileObject>emptyList();
		}

		_javaFileObjects.put(path, javaFileObjects);

		return javaFileObjects;
	}

	private Collection<JavaFileObject> _resolveClasses(
		BundleWiring bundleWiring, String path, int options) {

		Bundle bundle = bundleWiring.getBundle();

		if (bundle.getBundleId() == 0) {
			return _handleSystemBundle(bundleWiring, path);
		}

		return _toJavaFileObjects(
			bundle, bundleWiring.listResources(path, "*.class", options));
	}

	private Collection<JavaFileObject> _toJavaFileObjects(
		Bundle bundle, Collection<String> resources) {

		if ((resources == null) || resources.isEmpty()) {
			return Collections.emptyList();
		}

		List<JavaFileObject> javaFileObjects = new ArrayList<>(
			resources.size());

		for (String resource : resources) {
			JavaFileObject javaFileObject = _getJavaFileObject(
				bundle.getResource(resource), resource);

			if (javaFileObject == null) {
				continue;
			}

			javaFileObjects.add(javaFileObject);
		}

		return javaFileObjects;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JavaFileObjectResolver.class);

	private final BundleWiring _bundleWiring;
	private final Map<BundleWiring, Set<String>> _bundleWiringPackageNames;
	private final Map<String, Collection<JavaFileObject>> _javaFileObjects =
		new ConcurrentReferenceValueHashMap<>(
			FinalizeManager.SOFT_REFERENCE_FACTORY);

}