/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.bootstrap;

import com.liferay.petra.io.BigEndianCodec;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.local.LocalProcessExecutor;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.bootstrap.log.BundleStartStopLogger;
import com.liferay.portal.bootstrap.log.PortalSynchronousLogListener;
import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.concurrent.SystemExecutorServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedInputStream;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.lpkg.StaticLPKGResolver;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ModuleFrameworkPropsValues;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.module.framework.ModuleFramework;
import com.liferay.portal.spring.context.PortalContextLoaderListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Queue;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class ModuleFrameworkImpl implements ModuleFramework {

	@Override
	public Framework createFramework() throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Initializing the OSGi framework");
		}

		_validateModuleFrameworkBaseDirForEquinox();

		_initRequiredStartupDirs();

		Thread currentThread = Thread.currentThread();

		ServiceLoader<FrameworkFactory> serviceLoader = ServiceLoader.load(
			FrameworkFactory.class, currentThread.getContextClassLoader());

		Iterator<FrameworkFactory> iterator = serviceLoader.iterator();

		FrameworkFactory frameworkFactory = iterator.next();

		if (_log.isDebugEnabled()) {
			Class<?> clazz = frameworkFactory.getClass();

			_log.debug("Using the OSGi framework factory " + clazz.getName());
		}

		Map<String, String> properties = _buildFrameworkProperties(
			frameworkFactory.getClass());

		if (_log.isDebugEnabled()) {
			_log.debug("Creating a new OSGi framework instance");
		}

		_framework = frameworkFactory.newFramework(properties);

		return _framework;
	}

	public Bundle getBundle(
			BundleContext bundleContext, InputStream inputStream)
		throws PortalException {

		try {
			JarInputStream jarInputStream = new JarInputStream(inputStream);

			Manifest manifest = jarInputStream.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			String bundleSymbolicName = _parseBundleSymbolicName(attributes);

			String bundleVersionAttributeValue = attributes.getValue(
				Constants.BUNDLE_VERSION);

			Version bundleVersion = Version.parseVersion(
				bundleVersionAttributeValue);

			for (Bundle bundle : bundleContext.getBundles()) {
				if (bundleSymbolicName.equals(bundle.getSymbolicName()) &&
					bundleVersion.equals(bundle.getVersion())) {

					return bundle;
				}
			}

			return null;
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	@Override
	public Framework getFramework() {
		return _framework;
	}

	@Override
	public void initFramework() throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Initializing the new OSGi framework instance");
		}

		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		try {
			_framework.init();
		}
		finally {
			currentThread.setContextClassLoader(classLoader);
		}

		BundleContext bundleContext = _framework.getBundleContext();

		_bundleListener = new BundleStartStopLogger(bundleContext);

		bundleContext.addBundleListener(_bundleListener);

		ServiceReference<LogReaderService> serviceReference =
			bundleContext.getServiceReference(LogReaderService.class);

		if (serviceReference != null) {
			LogReaderService logReaderService = bundleContext.getService(
				serviceReference);

			_logListener = new PortalSynchronousLogListener();

			logReaderService.addLogListener(_logListener);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Initialized the OSGi framework");
		}
	}

	@Override
	public void registerContext(Object context) {
		if (context == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Registering context " + context);
		}

		if (context instanceof ConfigurableApplicationContext) {
			ConfigurableApplicationContext configurableApplicationContext =
				(ConfigurableApplicationContext)context;

			_registerApplicationContext(configurableApplicationContext);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Registered context " + context);
		}
	}

	@Override
	public void startFramework() throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Starting the OSGi framework");
		}

		_framework.start();

		_setUpPrerequisiteFrameworkService(_framework.getBundleContext());

		Bundle fileInstallBundle = _setUpInitialBundles();

		_startDynamicBundles(fileInstallBundle);

		if (_log.isInfoEnabled()) {
			_log.info(
				"Navigate to Control Panel > System > Gogo Shell and enter " +
					"\"lb\" to see all bundles");
		}

		FrameworkStartLevel frameworkStartLevel = _framework.adapt(
			FrameworkStartLevel.class);

		frameworkStartLevel.setStartLevel(
			ModuleFrameworkPropsValues.MODULE_FRAMEWORK_RUNTIME_START_LEVEL);

		if (_log.isDebugEnabled()) {
			_log.debug("Started the OSGi framework");
		}
	}

	@Override
	public void stopFramework(long timeout) throws Exception {
		if (_framework == null) {
			return;
		}

		FrameworkStartLevel frameworkStartLevel = _framework.adapt(
			FrameworkStartLevel.class);

		DefaultNoticeableFuture<FrameworkEvent> defaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		frameworkStartLevel.setStartLevel(
			ModuleFrameworkPropsValues.MODULE_FRAMEWORK_BEGINNING_START_LEVEL,
			frameworkEvent -> defaultNoticeableFuture.set(frameworkEvent));

		FrameworkEvent frameworkEvent = defaultNoticeableFuture.get();

		if (frameworkEvent.getType() != FrameworkEvent.STARTLEVEL_CHANGED) {
			ReflectionUtil.throwException(frameworkEvent.getThrowable());
		}

		BundleContext bundleContext = _framework.getBundleContext();

		ServiceReference<LogReaderService> serviceReference =
			bundleContext.getServiceReference(LogReaderService.class);

		if (serviceReference != null) {
			LogReaderService logReaderService = bundleContext.getService(
				serviceReference);

			logReaderService.removeLogListener(_logListener);
		}

		bundleContext.removeBundleListener(_bundleListener);

		_framework.stop();

		frameworkEvent = _framework.waitForStop(timeout);

		if (frameworkEvent.getType() == FrameworkEvent.WAIT_TIMEDOUT) {
			_log.error(
				StringBundler.concat(
					"OSGi framework event ", frameworkEvent,
					" triggered after a ", timeout, "ms timeout"));
		}
		else if (_log.isDebugEnabled()) {
			_log.debug(frameworkEvent);
		}

		if (Boolean.parseBoolean(System.getenv("LIFERAY_CLEAN_OSGI_STATE"))) {
			_cleanOSGiStateFolder();
		}
	}

	@Override
	public void unregisterContext(Object context) {
		if (context == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Unregistering context " + context);
		}

		if (!(context instanceof ConfigurableApplicationContext)) {
			return;
		}

		_unregisterApplicationContext((ConfigurableApplicationContext)context);

		if (_log.isDebugEnabled()) {
			_log.debug("Registered context " + context);
		}
	}

	private Bundle _addBundle(String location, InputStream inputStream)
		throws PortalException {

		if (_framework == null) {
			throw new IllegalStateException(
				"OSGi framework is not initialized");
		}

		BundleContext bundleContext = _framework.getBundleContext();

		if (inputStream != null) {
			UnsyncBufferedInputStream unsyncBufferedInputStream =
				new UnsyncBufferedInputStream(inputStream);

			unsyncBufferedInputStream.mark(1024 * 1000);

			Bundle bundle = null;

			if (location.contains("static=true")) {
				bundle = _getStaticBundle(
					bundleContext, unsyncBufferedInputStream, location);
			}
			else {
				bundle = getBundle(bundleContext, unsyncBufferedInputStream);
			}

			try {
				unsyncBufferedInputStream.reset();
			}
			catch (IOException ioException) {
				throw new PortalException(ioException);
			}

			if (bundle != null) {
				return bundle;
			}

			inputStream = unsyncBufferedInputStream;
		}

		try {
			return bundleContext.installBundle(location, inputStream);
		}
		catch (BundleException bundleException) {
			_log.error(bundleException);

			throw new PortalException(bundleException);
		}
	}

	private Map<String, String> _buildFrameworkProperties(Class<?> clazz)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Building OSGi framework properties");
		}

		// Release

		Map<String, String> properties = new HashMap<>();

		properties.put(
			Constants.BUNDLE_DESCRIPTION, ReleaseInfo.getReleaseInfo());
		properties.put(Constants.BUNDLE_NAME, ReleaseInfo.getName());
		properties.put(Constants.BUNDLE_VENDOR, ReleaseInfo.getVendor());
		properties.put(Constants.BUNDLE_VERSION, ReleaseInfo.getVersion());

		// Framework

		properties.put(
			Constants.FRAMEWORK_BUNDLE_PARENT,
			Constants.FRAMEWORK_BUNDLE_PARENT_APP);
		properties.put(
			Constants.FRAMEWORK_STORAGE,
			PropsValues.MODULE_FRAMEWORK_STATE_DIR);

		properties.put(
			"ds.lock.timeout.milliseconds",
			GetterUtil.getString(
				SystemProperties.get("ds.lock.timeout.milliseconds"),
				"1800000"));
		properties.put(
			"ds.stop.timeout.milliseconds",
			GetterUtil.getString(
				SystemProperties.get("ds.stop.timeout.milliseconds"),
				"1800000"));
		properties.put("eclipse.security", null);
		properties.put("java.security.manager", null);
		properties.put("org.osgi.framework.security", null);

		File file = new File(PropsValues.LIFERAY_HOME);

		URI uri = file.toURI();

		uri = uri.normalize();

		properties.put("osgi.home", uri.toString());

		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL codeSourceURL = codeSource.getLocation();

		properties.put(
			FrameworkPropsKeys.OSGI_FRAMEWORK, codeSourceURL.toExternalForm());

		properties.put(
			FrameworkPropsKeys.OSGI_INSTALL_AREA,
			PropsValues.MODULE_FRAMEWORK_BASE_DIR);

		// Overrides

		Properties extraProperties = PropsUtil.getProperties(
			PropsKeys.MODULE_FRAMEWORK_PROPERTIES, true);

		String extraCapabilities = extraProperties.getProperty(
			Constants.FRAMEWORK_SYSTEMCAPABILITIES_EXTRA);

		Attributes attributes = _getExtraManifestAttributes();

		String provideCapability = attributes.getValue(
			Constants.PROVIDE_CAPABILITY);

		if ((extraCapabilities != null) && !extraCapabilities.isEmpty()) {
			provideCapability = provideCapability.concat(
				",".concat(extraCapabilities));
		}

		extraProperties.setProperty(
			Constants.FRAMEWORK_SYSTEMCAPABILITIES_EXTRA, provideCapability);

		for (Map.Entry<Object, Object> entry : extraProperties.entrySet()) {
			String key = (String)entry.getKey();

			String value = (String)entry.getValue();

			// We need to support an empty string and a null value distinctly.
			// This is due to some different behaviors between OSGi
			// implementations. If a property is passed as xyz= it will be
			// treated as an empty string. Otherwise, xyz=null will be treated
			// as an explicit null value.

			if (value.equals(StringPool.NULL)) {
				value = null;
			}

			properties.put(key, value);
		}

		properties.put(
			Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
			_getSystemPackagesExtra(
				attributes.getValue(Constants.EXPORT_PACKAGE)));

		if (_log.isDebugEnabled()) {
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				_log.debug(
					StringBundler.concat(
						"OSGi framework property key \"", entry.getKey(),
						"\" with value \"", entry.getValue(), "\""));
			}
		}

		return properties;
	}

	private long _calculateChecksum(File file) {
		CRC32 crc32 = new CRC32();

		String fileName = file.getName();

		crc32.update(fileName.getBytes());

		_calculateChecksum(file.canWrite() ? 1000L : -1000L, crc32);
		_calculateChecksum(file.lastModified() / 1000, crc32);
		_calculateChecksum(file.length(), crc32);

		return crc32.getValue();
	}

	private void _calculateChecksum(long l, CRC32 crc) {
		for (int i = 0; i < 8; i++) {
			crc.update((int)(l & 0xFF));

			l >>= 8;
		}
	}

	private void _cleanOSGiStateFolder() throws Exception {
		Files.walkFileTree(
			Paths.get(PropsValues.MODULE_FRAMEWORK_STATE_DIR),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
						Path dirPath, IOException ioException)
					throws IOException {

					String name = dirPath.toString();

					if (name.contains(".cp")) {
						Files.delete(dirPath);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					File file = filePath.toFile();

					String name = file.getName();

					if (name.endsWith(".bin") || name.endsWith(".jar")) {
						Files.delete(filePath);

						return FileVisitResult.CONTINUE;
					}

					if (!name.equals("bundleFile")) {
						return FileVisitResult.CONTINUE;
					}

					try (ZipFile zipFile = new ZipFile(file)) {
						ZipEntry zipEntry = zipFile.getEntry(
							"liferay-marketplace.properties");

						if (zipEntry != null) {
							return FileVisitResult.CONTINUE;
						}

						zipEntry = zipFile.getEntry(
							"WEB-INF/liferay-plugin-package.properties");

						if (zipEntry != null) {
							return FileVisitResult.CONTINUE;
						}

						Properties properties = new Properties();

						try (InputStream inputStream = zipFile.getInputStream(
								zipFile.getEntry("META-INF/MANIFEST.MF"))) {

							properties.load(inputStream);
						}

						if (properties.containsKey("Liferay-WAB-LPKG-URL")) {
							return FileVisitResult.CONTINUE;
						}
					}

					Files.delete(filePath);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private Set<Bundle> _deployStaticBundlesFromFile(
			File file, Set<String> overrideStaticFileNames)
		throws Exception {

		Set<Bundle> bundles = new HashSet<>();

		String path = _getLPKGLocation(file);

		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			List<ZipEntry> zipEntries = new ArrayList<>();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String name = StringUtil.toLowerCase(zipEntry.getName());

				if (!name.endsWith(".jar")) {
					continue;
				}

				if (!overrideStaticFileNames.isEmpty()) {
					String fileName = _extractFileName(name);

					fileName = fileName.concat(".jar");

					if (overrideStaticFileNames.contains(fileName)) {
						if (_log.isInfoEnabled()) {
							_log.info(
								StringBundler.concat(
									zipFile.getName(), StringPool.COLON,
									zipEntry, " is overridden by ",
									PropsValues.MODULE_FRAMEWORK_BASE_DIR,
									"/static/", fileName));
						}

						continue;
					}
				}

				zipEntries.add(zipEntry);
			}

			Collections.sort(
				zipEntries,
				new Comparator<ZipEntry>() {

					@Override
					public int compare(ZipEntry zipEntry1, ZipEntry zipEntry2) {
						String name1 = zipEntry1.getName();
						String name2 = zipEntry2.getName();

						return name1.compareTo(name2);
					}

				});

			for (ZipEntry zipEntry : zipEntries) {
				String zipEntryName = zipEntry.getName();

				String location = StringBundler.concat(
					zipEntryName, "?lpkgPath=", path,
					"&protocol=lpkg&static=true");

				try (InputStream inputStream = zipFile.getInputStream(
						zipEntry)) {

					Bundle bundle = _installInitialBundle(
						location, inputStream);

					if (bundle != null) {
						bundles.add(bundle);
					}
				}
			}
		}

		return bundles;
	}

	private String _extractFileName(String string) {
		int endIndex = string.indexOf(CharPool.DASH);

		if (endIndex == -1) {
			endIndex = string.indexOf(CharPool.QUESTION);

			if (endIndex == -1) {
				endIndex = string.length();
			}
		}

		int beginIndex = string.lastIndexOf(CharPool.SLASH, endIndex) + 1;

		return string.substring(beginIndex, endIndex);
	}

	private OSGiBeanProperties _findOSGiBeanProperties(Object bean) {
		Class<?> clazz = bean.getClass();

		if (!(bean instanceof BaseLocalService)) {
			return clazz.getAnnotation(OSGiBeanProperties.class);
		}

		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			if ((interfaceClass == BaseLocalService.class) ||
				!BaseLocalService.class.isAssignableFrom(interfaceClass)) {

				continue;
			}

			OSGiBeanProperties osgiBeanProperties =
				interfaceClass.getAnnotation(OSGiBeanProperties.class);

			if (osgiBeanProperties != null) {
				return osgiBeanProperties;
			}
		}

		return null;
	}

	private Attributes _getExtraManifestAttributes() {
		try (InputStream inputStream =
				ModuleFrameworkImpl.class.getResourceAsStream(
					"/META-INF/system.packages.extra.mf")) {

			Manifest manifest = new Manifest(inputStream);

			Attributes attributes = manifest.getMainAttributes();

			if (_bundleHeaderReplacerBiFunction == null) {
				return attributes;
			}

			Map<Object, Object> modifiedAttributes =
				_bundleHeaderReplacerBiFunction.apply(
					"SystemBundle#", attributes);

			attributes.clear();

			for (Map.Entry<Object, Object> entry :
					modifiedAttributes.entrySet()) {

				attributes.put(entry.getKey(), entry.getValue());
			}

			return attributes;
		}
		catch (IOException ioException) {
			return ReflectionUtil.throwException(ioException);
		}
	}

	private String _getFragmentHost(Bundle bundle) {
		Dictionary<String, String> dictionary = bundle.getHeaders(
			StringPool.BLANK);

		String fragmentHost = dictionary.get(Constants.FRAGMENT_HOST);

		if (fragmentHost == null) {
			return null;
		}

		int index = fragmentHost.indexOf(CharPool.SEMICOLON);

		if (index != -1) {
			fragmentHost = fragmentHost.substring(0, index);
		}

		return fragmentHost;
	}

	private String _getLPKGLocation(File lpkgFile) {
		String uriString = String.valueOf(lpkgFile.toURI());

		return StringUtil.replace(
			uriString, CharPool.BACK_SLASH, CharPool.FORWARD_SLASH);
	}

	private Dictionary<String, Object> _getProperties(
		OSGiBeanProperties osgiBeanProperties, Object bean, String beanName) {

		HashMapDictionary<String, Object> properties =
			new HashMapDictionary<>();

		if (osgiBeanProperties != null) {
			properties.putAll(
				OSGiBeanProperties.Convert.toMap(osgiBeanProperties));
		}

		properties.put(ServicePropsKeys.BEAN_ID, beanName);
		properties.put(ServicePropsKeys.ORIGINAL_BEAN, Boolean.TRUE);
		properties.put(ServicePropsKeys.VENDOR, ReleaseInfo.getVendor());

		if (bean instanceof BaseService) {
			Class<?> beanClass = bean.getClass();

			JSONWebService jsonWebService = beanClass.getAnnotation(
				JSONWebService.class);

			if (jsonWebService == null) {
				for (Class<?> interfaceClass : beanClass.getInterfaces()) {
					if ((interfaceClass == BaseService.class) ||
						!BaseService.class.isAssignableFrom(interfaceClass)) {

						continue;
					}

					jsonWebService = interfaceClass.getAnnotation(
						JSONWebService.class);

					if (jsonWebService != null) {
						break;
					}
				}
			}

			if (jsonWebService != null) {
				properties.put(
					"json.web.service.context.name",
					PortalContextLoaderListener.getPortalServletContextName());
				properties.put(
					"json.web.service.context.path",
					PortalContextLoaderListener.getPortalServletContextPath());
			}
		}

		return properties;
	}

	private Bundle _getStaticBundle(
			BundleContext bundleContext, InputStream inputStream,
			String location)
		throws PortalException {

		try {
			JarInputStream jarInputStream = new JarInputStream(inputStream);

			Manifest manifest = jarInputStream.getManifest();

			if (manifest == null) {
				throw new IllegalStateException(
					"No manifest found at location " + location);
			}

			Attributes attributes = manifest.getMainAttributes();

			String bundleSymbolicName = _parseBundleSymbolicName(attributes);

			String bundleVersionAttributeValue = attributes.getValue(
				Constants.BUNDLE_VERSION);

			Version bundleVersion = Version.parseVersion(
				bundleVersionAttributeValue);

			for (Bundle bundle : bundleContext.getBundles()) {
				if (bundleSymbolicName.equals(bundle.getSymbolicName())) {
					if (bundleVersion.equals(bundle.getVersion())) {
						return bundle;
					}

					bundle.uninstall();

					_refreshBundles(Collections.singletonList(bundle));

					return null;
				}
			}

			return null;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private String _getSystemPackagesExtra(String exportedPackages) {
		String[] systemPackagesExtra =
			ModuleFrameworkPropsValues.MODULE_FRAMEWORK_SYSTEM_PACKAGES_EXTRA;

		StringBundler sb = new StringBundler();

		for (String extraPackage : systemPackagesExtra) {
			sb.append(extraPackage);
			sb.append(StringPool.COMMA);
		}

		sb.append(exportedPackages);

		if (_log.isDebugEnabled()) {
			String s = sb.toString();

			s = StringUtil.replace(s, ',', '\n');

			_log.debug(
				"The portal's system bundle is exporting the following " +
					"packages:\n" + s);
		}

		return sb.toString();
	}

	private void _initRequiredStartupDirs() {
		if (_log.isDebugEnabled()) {
			_log.debug("Initializing required startup directories");
		}

		for (String dirName : PropsValues.MODULE_FRAMEWORK_AUTO_DEPLOY_DIRS) {
			FileUtil.mkdirs(dirName);
		}

		FileUtil.mkdirs(PropsValues.MODULE_FRAMEWORK_BASE_DIR + "/static");
		FileUtil.mkdirs(
			PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR + "/override");
	}

	private void _installBundlesFromDir(
			String dirPath, Map<Long, Long> checksums,
			Set<String> fragmentHosts)
		throws Exception {

		BundleContext bundleContext = _framework.getBundleContext();

		File dir = new File(dirPath);

		dir = dir.getCanonicalFile();

		for (File file :
				dir.listFiles((folder, name) -> name.endsWith(".jar"))) {

			URI uri = file.toURI();

			uri = uri.normalize();

			String location = uri.toString();

			if (bundleContext.getBundle(location) != null) {
				continue;
			}

			try (InputStream inputStream = new FileInputStream(file)) {
				Bundle bundle = bundleContext.installBundle(
					location, inputStream);

				checksums.put(bundle.getBundleId(), _calculateChecksum(file));

				if ((bundle.getState() != Bundle.INSTALLED) &&
					(bundle.getState() != Bundle.RESOLVED)) {

					// Defense for bundle blacklist auto uninstall

					continue;
				}

				BundleStartLevel bundleStartLevel = bundle.adapt(
					BundleStartLevel.class);

				Dictionary<String, String> headers = bundle.getHeaders(
					StringPool.BLANK);

				String header = headers.get("Web-ContextPath");

				if (header == null) {
					bundleStartLevel.setStartLevel(
						ModuleFrameworkPropsValues.
							MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL);
				}
				else {
					bundleStartLevel.setStartLevel(
						ModuleFrameworkPropsValues.
							MODULE_FRAMEWORK_WEB_START_LEVEL);
				}

				if (_isFragmentBundle(bundle)) {
					fragmentHosts.add(_getFragmentHost(bundle));
				}
				else {
					bundle.start();
				}
			}
			catch (BundleException bundleException) {
				_log.error(
					"Unable to install bundle at " + location, bundleException);
			}
		}
	}

	private void _installConfigs(ClassLoader classLoader) throws Exception {
		BundleContext bundleContext = _framework.getBundleContext();

		Class<?> configurationFileInstallerClass = classLoader.loadClass(
			"com.liferay.portal.file.install.internal.configuration." +
				"ConfigurationFileInstaller");

		Constructor<?> constructor =
			configurationFileInstallerClass.getDeclaredConstructor(
				classLoader.loadClass("org.osgi.service.cm.ConfigurationAdmin"),
				String.class);

		constructor.setAccessible(true);

		Method canTransformURLMethod =
			configurationFileInstallerClass.getDeclaredMethod(
				"canTransformURL", File.class);
		Method transformURLMethod =
			configurationFileInstallerClass.getDeclaredMethod(
				"transformURL", File.class);

		Object configurationFileInstaller = constructor.newInstance(
			bundleContext.getService(
				bundleContext.getServiceReference(
					"org.osgi.service.cm.ConfigurationAdmin")),
			ModuleFrameworkPropsValues.
				MODULE_FRAMEWORK_FILE_INSTALL_CONFIG_ENCODING);

		File dir = new File(PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR);

		dir = dir.getCanonicalFile();

		for (File file : _listConfigs(dir)) {
			if ((boolean)canTransformURLMethod.invoke(
					configurationFileInstaller, file)) {

				try {
					transformURLMethod.invoke(configurationFileInstaller, file);
				}
				catch (InvocationTargetException invocationTargetException) {
					_log.error(
						"Unable to install " + file, invocationTargetException);
				}
			}
		}
	}

	private Map<Long, Long> _installDynamicBundles() throws Exception {
		Map<Long, Long> checksums = new HashMap<>();

		Set<String> fragmentHosts = new HashSet<>();

		_installBundlesFromDir(
			PropsValues.MODULE_FRAMEWORK_PORTAL_DIR, checksums, fragmentHosts);
		_installBundlesFromDir(
			PropsValues.MODULE_FRAMEWORK_MODULES_DIR, checksums, fragmentHosts);

		if (!fragmentHosts.isEmpty()) {
			List<Bundle> refreshBundles = new ArrayList<>();

			BundleContext bundleContext = _framework.getBundleContext();

			for (Bundle bundle : bundleContext.getBundles()) {
				if (fragmentHosts.remove(bundle.getSymbolicName())) {
					refreshBundles.add(bundle);

					if (fragmentHosts.isEmpty()) {
						break;
					}
				}
			}

			_refreshBundles(refreshBundles);
		}

		return checksums;
	}

	private Bundle _installInitialBundle(
		String location, InputStream inputStream) {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Adding initial bundle " + location);
			}

			Bundle bundle = _addBundle(location, inputStream);

			if (_log.isDebugEnabled()) {
				_log.debug("Added initial bundle " + bundle);
			}

			if ((bundle == null) || _isFragmentBundle(bundle)) {
				return bundle;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Setting bundle ", bundle, " at start level ",
						ModuleFrameworkPropsValues.
							MODULE_FRAMEWORK_BEGINNING_START_LEVEL));
			}

			BundleStartLevel bundleStartLevel = bundle.adapt(
				BundleStartLevel.class);

			bundleStartLevel.setStartLevel(
				ModuleFrameworkPropsValues.
					MODULE_FRAMEWORK_BEGINNING_START_LEVEL);

			return bundle;
		}
		catch (Exception exception) {
			_log.error(exception);

			return null;
		}
	}

	private boolean _isFragmentBundle(Bundle bundle) {
		BundleRevision bundleRevision = bundle.adapt(BundleRevision.class);

		if ((bundleRevision.getTypes() & BundleRevision.TYPE_FRAGMENT) == 0) {
			return false;
		}

		return true;
	}

	private boolean _isValid(String pathName) {
		int index = pathName.lastIndexOf(CharPool.DASH);

		if (index == -1) {
			return true;
		}

		String version = pathName.substring(index + 1, pathName.length() - 4);

		int count = StringUtil.count(version, CharPool.PERIOD);

		if ((count == 2) || (count == 3)) {
			return false;
		}

		return true;
	}

	private List<File> _listConfigs(File dir) {
		if (!dir.isDirectory()) {
			return Collections.<File>emptyList();
		}

		BundleContext bundleContext = _framework.getBundleContext();

		String subdirMode = bundleContext.getProperty(
			"file.install.subdir.mode");

		if (Objects.equals(subdirMode, "recurse")) {
			Queue<File> queue = new LinkedList<>();

			queue.add(dir);

			List<File> files = new ArrayList<>();

			File curDir = null;

			while ((curDir = queue.poll()) != null) {
				for (File file : curDir.listFiles()) {
					if (file.isDirectory()) {
						queue.add(file);
					}
					else {
						String name = file.getName();

						if (name.endsWith(".cfg") || name.endsWith(".config")) {
							files.add(file);
						}
					}
				}
			}

			return files;
		}

		return Arrays.asList(
			dir.listFiles(
				file -> {
					if (file.isFile()) {
						String name = file.getName();

						if (name.endsWith(".cfg") || name.endsWith(".config")) {
							return true;
						}
					}

					return false;
				}));
	}

	private String _parseBundleSymbolicName(Attributes attributes) {
		String bundleSymbolicName = attributes.getValue(
			Constants.BUNDLE_SYMBOLICNAME);

		int index = bundleSymbolicName.indexOf(CharPool.SEMICOLON);

		if (index != -1) {
			bundleSymbolicName = bundleSymbolicName.substring(0, index);
		}

		return bundleSymbolicName;
	}

	private void _refreshBundles(List<Bundle> refreshBundles) {
		FrameworkWiring frameworkWiring = _framework.adapt(
			FrameworkWiring.class);

		DefaultNoticeableFuture<FrameworkEvent> defaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		frameworkWiring.refreshBundles(
			refreshBundles,
			frameworkEvent -> defaultNoticeableFuture.set(frameworkEvent));

		try {
			FrameworkEvent frameworkEvent = defaultNoticeableFuture.get();

			if (frameworkEvent.getType() != FrameworkEvent.PACKAGES_REFRESHED) {
				throw frameworkEvent.getThrowable();
			}
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}
	}

	private void _registerApplicationContext(
		ConfigurableApplicationContext configurableApplicationContext) {

		if (_log.isDebugEnabled()) {
			_log.debug("Register application context");
		}

		Collection<ServiceRegistration<?>> serviceRegistrations =
			new ConcurrentLinkedQueue<>();

		ExecutorService executorService =
			SystemExecutorServiceUtil.getExecutorService();
		List<Future<Void>> futures = new ArrayList<>();

		ConfigurableListableBeanFactory configurableListableBeanFactory =
			configurableApplicationContext.getBeanFactory();

		Iterator<String> iterator =
			configurableListableBeanFactory.getBeanNamesIterator();

		iterator.forEachRemaining(
			beanName -> futures.add(
				executorService.submit(
					() -> {
						Object bean = null;

						try {
							bean = configurableApplicationContext.getBean(
								beanName);
						}
						catch (Exception exception) {
							_log.error(exception);
						}

						if (bean != null) {
							ServiceRegistration<?> serviceRegistration =
								_registerService(
									_framework.getBundleContext(), beanName,
									bean);

							if (serviceRegistration != null) {
								serviceRegistrations.add(serviceRegistration);
							}
						}
					},
					null)));

		for (Future<Void> future : futures) {
			try {
				future.get();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		_springContextServices.put(
			configurableApplicationContext, serviceRegistrations);
	}

	private void _registerDynamicBundles(
			Map<Long, Long> checksums, BundleContext bundleContext)
		throws Exception {

		byte[] bytes = new byte[checksums.size() * 16];

		int index = 0;

		for (Map.Entry<Long, Long> entry : checksums.entrySet()) {
			BigEndianCodec.putLong(bytes, index, entry.getKey());
			BigEndianCodec.putLong(bytes, index + 8, entry.getValue());

			index += 16;
		}

		try (OutputStream outputStream = new FileOutputStream(
				bundleContext.getDataFile("bundles.checksum"), true)) {

			outputStream.write(bytes);
		}
	}

	private ServiceRegistration<?> _registerService(
		BundleContext bundleContext, String beanName, Object bean) {

		OSGiBeanProperties osgiBeanProperties = _findOSGiBeanProperties(bean);

		Set<String> names = OSGiBeanProperties.Service.interfaceNames(
			bean, osgiBeanProperties,
			ModuleFrameworkPropsValues.
				MODULE_FRAMEWORK_SERVICES_IGNORED_INTERFACES);

		if (names.isEmpty()) {
			return null;
		}

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				names.toArray(new String[0]), bean,
				_getProperties(osgiBeanProperties, bean, beanName));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Registered service as " + serviceRegistration.getReference());
		}

		return serviceRegistration;
	}

	private Bundle _setUpInitialBundles() throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Starting initial bundles");
		}

		BundleContext bundleContext = _framework.getBundleContext();

		ThrowableCollector throwableCollector = new ThrowableCollector();

		Dictionary<String, Object> dictionary = new HashMapDictionary<>();

		dictionary.put("throwable.collector", "initial.bundles");

		bundleContext.registerService(
			ThrowableCollector.class, throwableCollector, dictionary);

		Set<Bundle> bundles = new HashSet<>();

		final List<Path> jarPaths = new ArrayList<>();

		Files.walkFileTree(
			Paths.get(PropsValues.MODULE_FRAMEWORK_BASE_DIR, "static"),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path fileNamePath = filePath.getFileName();

					String fileName = StringUtil.toLowerCase(
						fileNamePath.toString());

					if (!fileName.endsWith(".jar")) {
						return FileVisitResult.CONTINUE;
					}

					if (_isValid(fileName)) {
						jarPaths.add(filePath.toAbsolutePath());

						return FileVisitResult.CONTINUE;
					}

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Override static jar " + fileName +
								" has an invalid name and will be ignored");
					}

					return FileVisitResult.CONTINUE;
				}

			});

		for (String staticJarFileName :
				ModuleFrameworkPropsValues.MODULE_FRAMEWORK_STATIC_JARS) {

			Path staticJarPath = Paths.get(
				PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR,
				staticJarFileName);

			if (Files.exists(staticJarPath)) {
				jarPaths.add(staticJarPath);
			}
			else {
				_log.error("Missing " + staticJarPath);
			}
		}

		Collections.sort(jarPaths);

		List<Bundle> refreshBundles = new ArrayList<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			String location = bundle.getLocation();

			if (!location.contains("protocol=jar&static=true")) {
				continue;
			}

			URI uri = new URI(location);

			if (Files.exists(
					Paths.get(
						new URI(
							uri.getScheme(), uri.getAuthority(), uri.getPath(),
							null, uri.getFragment())))) {

				bundles.add(bundle);

				continue;
			}

			bundle.uninstall();

			refreshBundles.add(bundle);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Uninstalled orphan overriding static JAR bundle " +
						location);
			}
		}

		_refreshBundles(refreshBundles);

		refreshBundles.clear();

		Set<String> overrideStaticFileNames = new HashSet<>();

		for (Path jarPath : jarPaths) {
			try (InputStream inputStream = Files.newInputStream(jarPath)) {
				File file = jarPath.toFile();

				file = file.getCanonicalFile();

				String uriString = String.valueOf(file.toURI());

				String location = uriString.concat("?protocol=jar&static=true");

				Bundle bundle = _installInitialBundle(location, inputStream);

				if (bundle != null) {
					bundles.add(bundle);

					overrideStaticFileNames.add(
						uriString.substring(
							uriString.lastIndexOf(CharPool.SLASH) + 1));
				}
			}
		}

		String deployDir = PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR;

		for (String staticFileName :
				StaticLPKGResolver.getStaticLPKGFileNames()) {

			File file = new File(deployDir + StringPool.SLASH + staticFileName);

			file = file.getCanonicalFile();

			if (file.exists()) {
				bundles.addAll(
					_deployStaticBundlesFromFile(
						file, overrideStaticFileNames));
			}
		}

		Set<String> overrideLPKGFileNames = new HashSet<>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				Paths.get(deployDir, "override"))) {

			for (Path path : directoryStream) {
				String fileName = String.valueOf(path.getFileName());

				String pathName = StringUtil.toLowerCase(fileName);

				if (pathName.endsWith("jar")) {
					overrideLPKGFileNames.add(fileName);
				}
			}
		}

		if (!overrideLPKGFileNames.isEmpty()) {
			for (Bundle bundle : bundleContext.getBundles()) {
				if (bundle.getBundleId() == 0) {
					continue;
				}

				String location = bundle.getLocation();

				location = _extractFileName(location);

				if (overrideLPKGFileNames.contains(location)) {
					bundle.uninstall();
				}
			}
		}

		Bundle fileInstallBundle = null;

		Iterator<Bundle> bundleIterator = bundles.iterator();

		while (bundleIterator.hasNext()) {
			Bundle bundle = bundleIterator.next();

			if (bundle.getState() == Bundle.UNINSTALLED) {
				bundleIterator.remove();

				continue;
			}

			if (!_isFragmentBundle(bundle)) {
				if (Objects.equals(
						bundle.getSymbolicName(),
						"com.liferay.portal.file.install.impl")) {

					fileInstallBundle = bundle;
				}

				bundle.stop();
			}
		}

		FrameworkStartLevel frameworkStartLevel = _framework.adapt(
			FrameworkStartLevel.class);

		DefaultNoticeableFuture<FrameworkEvent> defaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		frameworkStartLevel.setStartLevel(
			ModuleFrameworkPropsValues.MODULE_FRAMEWORK_BEGINNING_START_LEVEL,
			frameworkEvent -> defaultNoticeableFuture.set(frameworkEvent));

		FrameworkEvent frameworkEvent = defaultNoticeableFuture.get();

		if (frameworkEvent.getType() != FrameworkEvent.STARTLEVEL_CHANGED) {
			ReflectionUtil.throwException(frameworkEvent.getThrowable());
		}

		FrameworkWiring frameworkWiring = _framework.adapt(
			FrameworkWiring.class);

		frameworkWiring.resolveBundles(bundles);

		_startConfigurationBundles(bundles);

		BundleWiring bundleWiring = fileInstallBundle.adapt(BundleWiring.class);

		_installConfigs(bundleWiring.getClassLoader());

		if (ModuleFrameworkPropsValues.
				MODULE_FRAMEWORK_CONCURRENT_STARTUP_ENABLED) {

			ExecutorService executorService =
				SystemExecutorServiceUtil.getExecutorService();

			List<Future<Void>> futures = new ArrayList<>(bundles.size());

			for (Bundle bundle : bundles) {
				if (!_isFragmentBundle(bundle) &&
					(bundle != fileInstallBundle)) {

					futures.add(
						executorService.submit(
							() -> {
								bundle.start();

								return null;
							}));
				}
			}

			for (Future<Void> future : futures) {
				try {
					future.get();
				}
				catch (ExecutionException executionException) {
					throwableCollector.collect(executionException.getCause());
				}
				catch (InterruptedException interruptedException) {
					throwableCollector.collect(interruptedException);
				}
			}
		}
		else {
			for (Bundle bundle : bundles) {
				if (!_isFragmentBundle(bundle) &&
					(bundle != fileInstallBundle)) {

					bundle.start();
				}
			}
		}

		throwableCollector.rethrow();

		Bundle[] installedBundles = bundleContext.getBundles();

		Set<Bundle> fragmentBundles = new HashSet<>();

		for (Bundle bundle : installedBundles) {
			if (_isFragmentBundle(bundle)) {
				fragmentBundles.add(bundle);
			}
		}

		frameworkWiring.resolveBundles(fragmentBundles);

		if (_log.isInfoEnabled()) {
			_log.info("Started initial bundles");
		}

		return fileInstallBundle;
	}

	private void _setUpPrerequisiteFrameworkService(
		BundleContext bundleContext) {

		if (_log.isDebugEnabled()) {
			_log.debug("Setting up required service");
		}

		bundleContext.registerService(
			ProcessExecutor.class, new LocalProcessExecutor(), null);
	}

	private void _startConfigurationBundles(Collection<Bundle> bundles)
		throws Exception {

		Iterator<Bundle> iterator = bundles.iterator();

		while (iterator.hasNext()) {
			Bundle bundle = iterator.next();

			if (_configurationBundleSymbolicNames.contains(
					bundle.getSymbolicName())) {

				bundle.start();

				iterator.remove();
			}
		}
	}

	private void _startDynamicBundles(Bundle fileInstallBundle)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Starting dynamic bundles");
		}

		Map<Long, Long> dynamicBundleChecksums = _installDynamicBundles();

		FrameworkStartLevel frameworkStartLevel = _framework.adapt(
			FrameworkStartLevel.class);

		DefaultNoticeableFuture<FrameworkEvent> defaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		frameworkStartLevel.setStartLevel(
			ModuleFrameworkPropsValues.
				MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL,
			frameworkEvent -> defaultNoticeableFuture.set(frameworkEvent));

		FrameworkEvent frameworkEvent = defaultNoticeableFuture.get();

		if (frameworkEvent.getType() == FrameworkEvent.ERROR) {
			ReflectionUtil.throwException(frameworkEvent.getThrowable());
		}

		if (_log.isInfoEnabled()) {
			_log.info("Started dynamic bundles");
		}

		DefaultNoticeableFuture<FrameworkEvent> webDefaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		if (_log.isInfoEnabled()) {
			_log.info("Starting web bundles");
		}

		frameworkStartLevel.setStartLevel(
			ModuleFrameworkPropsValues.MODULE_FRAMEWORK_WEB_START_LEVEL,
			webFrameworkEvent -> webDefaultNoticeableFuture.set(
				webFrameworkEvent));

		FrameworkEvent webFrameworkEvent = webDefaultNoticeableFuture.get();

		if (webFrameworkEvent.getType() == FrameworkEvent.ERROR) {
			ReflectionUtil.throwException(webFrameworkEvent.getThrowable());
		}

		if (dynamicBundleChecksums.isEmpty()) {
			fileInstallBundle.start();
		}
		else {
			SynchronousBundleListener synchronousBundleListener = event -> {
				if (event.getType() != BundleEvent.STARTING) {
					return;
				}

				Bundle currentBundle = event.getBundle();

				if (currentBundle != fileInstallBundle) {
					return;
				}

				try {
					_registerDynamicBundles(
						dynamicBundleChecksums,
						currentBundle.getBundleContext());
				}
				catch (Exception exception) {
					_log.error(
						"Unable to register dynamic bundle checksums",
						exception);
				}
			};

			BundleContext bundleContext = _framework.getBundleContext();

			bundleContext.addBundleListener(synchronousBundleListener);

			try {
				fileInstallBundle.start();
			}
			finally {
				bundleContext.removeBundleListener(synchronousBundleListener);
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info("Started web bundles");
		}
	}

	private void _unregisterApplicationContext(
		ConfigurableApplicationContext configurableApplicationContext) {

		Collection<ServiceRegistration<?>> serviceRegistrations =
			_springContextServices.remove(configurableApplicationContext);

		if (serviceRegistrations == null) {
			return;
		}

		for (ServiceRegistration<?> serviceRegistration :
				serviceRegistrations) {

			try {
				serviceRegistration.unregister();
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Service registration " + serviceRegistration +
							" is already unregistered",
						illegalStateException);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void _validateModuleFrameworkBaseDirForEquinox() throws Exception {
		File baseDir = new File(PropsValues.MODULE_FRAMEWORK_BASE_DIR);

		baseDir = baseDir.getAbsoluteFile();

		// See LPS-71758. Do what Equinox does internally to get a file path and
		// validate it. Equinox converts a File into a URL using File#toURL(),
		// and later creates the OSGi persistence directory using the URL, which
		// does not properly handle special character escaping and decoding.

		URL url = baseDir.toURL();

		File equinoxBaseDir = new File(url.getFile());

		if (!baseDir.equals(equinoxBaseDir) && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"The module.framework.base.dir path \"", baseDir,
					"\" contains characters that Equinox cannot handle. The ",
					"OSGi persistence data will be stored under \"",
					equinoxBaseDir, "\""));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ModuleFrameworkImpl.class);

	private static final BiFunction
		<String, Map<Object, Object>, Map<Object, Object>>
			_bundleHeaderReplacerBiFunction;
	private static final List<String> _configurationBundleSymbolicNames =
		Arrays.asList(
			ModuleFrameworkPropsValues.
				MODULE_FRAMEWORK_CONFIGURATION_BUNDLE_SYMBOLIC_NAMES);

	static {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object instance = null;

		try {
			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.tools.jakarta.ee.transformer.function." +
					"BundleHeaderReplacerBiFunction");

			instance = clazz.newInstance();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			if (!(reflectiveOperationException instanceof
					ClassNotFoundException)) {

				throw new ExceptionInInitializerError(
					reflectiveOperationException);
			}
		}

		_bundleHeaderReplacerBiFunction =
			(BiFunction<String, Map<Object, Object>, Map<Object, Object>>)
				instance;
	}

	private BundleListener _bundleListener;
	private volatile Framework _framework;
	private LogListener _logListener;
	private final Map
		<ConfigurableApplicationContext, Collection<ServiceRegistration<?>>>
			_springContextServices = new ConcurrentHashMap<>();

}