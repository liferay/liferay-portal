/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lpkg.deployer.internal;

import com.liferay.osgi.util.bundle.BundleStartLevelUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ModuleFrameworkPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;
import com.liferay.portal.lpkg.deployer.LPKGVerifier;
import com.liferay.portal.lpkg.deployer.LPKGVerifyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Shuyang Zhou
 */
@Component(service = LPKGDeployer.class)
public class DefaultLPKGDeployer implements LPKGDeployer {

	@Override
	public List<Bundle> deploy(BundleContext bundleContext, File lpkgFile)
		throws IOException {

		lpkgFile = lpkgFile.getCanonicalFile();

		Path lpkgFilePath = lpkgFile.toPath();

		if (!lpkgFilePath.startsWith(_deploymentDirPath)) {
			throw new LPKGVerifyException(
				StringBundler.concat(
					"Unable to deploy ", lpkgFile,
					" from outside the deployment directory ",
					_deploymentDirPath));
		}

		List<Bundle> oldBundles = _lpkgVerifier.verify(lpkgFile);

		for (Bundle bundle : oldBundles) {
			try {
				bundle.uninstall();

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Uninstalled older LPKG bundle ", bundle,
							" in order to install ", lpkgFile));
				}

				String location = LPKGLocationUtil.getLPKGLocation(lpkgFile);

				if (!location.equals(bundle.getLocation()) &&
					Files.deleteIfExists(Paths.get(bundle.getLocation())) &&
					_log.isInfoEnabled()) {

					_log.info(
						"Removed old LPKG bundle " + bundle.getLocation());
				}
			}
			catch (BundleException bundleException) {
				_log.error(
					StringBundler.concat(
						"Unable to uninstall ", bundle, " in order to install ",
						lpkgFile),
					bundleException);
			}
		}

		try {
			String location = LPKGLocationUtil.getLPKGLocation(lpkgFile);

			Bundle lpkgBundle = bundleContext.getBundle(location);

			if (lpkgBundle != null) {
				return Collections.emptyList();
			}

			_deployLicense(lpkgFile);

			List<Bundle> bundles = new ArrayList<>();

			lpkgBundle = bundleContext.installBundle(
				location, toBundle(lpkgFile));

			if (lpkgBundle.getState() == Bundle.UNINSTALLED) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Skipped deployment of outdated LPKG " + lpkgFile);
				}

				return bundles;
			}

			BundleStartLevel bundleStartLevel = lpkgBundle.adapt(
				BundleStartLevel.class);

			bundleStartLevel.setStartLevel(
				ModuleFrameworkPropsValues.
					MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL);

			bundles.add(lpkgBundle);

			List<Bundle> newBundles = _lpkgBundleTracker.getObject(lpkgBundle);

			if (newBundles != null) {
				bundles.addAll(newBundles);
			}

			if (!oldBundles.isEmpty()) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Start refreshing references to point to the new " +
							"bundle " + lpkgBundle);
				}

				FrameworkEvent frameworkEvent = _refreshBundles(
					null, bundleContext);

				if (frameworkEvent.getType() ==
						FrameworkEvent.PACKAGES_REFRESHED) {

					if (_log.isInfoEnabled()) {
						_log.info(
							"Finished refreshing references to point to the " +
								"new bundle " + lpkgBundle);
					}
				}
				else {
					throw new Exception(
						StringBundler.concat(
							"Unable to refresh references to the new bundle ",
							lpkgBundle, " because of framework event ",
							frameworkEvent),
						frameworkEvent.getThrowable());
				}
			}

			return bundles;
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	@Override
	public Map<Bundle, List<Bundle>> getDeployedLPKGBundles() {
		return _lpkgBundleTracker.getTracked();
	}

	@Override
	public InputStream toBundle(File lpkgFile) throws IOException {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream(
					(int)(lpkgFile.length() + 512))) {

			try (ZipFile zipFile = new ZipFile(lpkgFile);
				JarOutputStream jarOutputStream = new JarOutputStream(
					unsyncByteArrayOutputStream)) {

				String name = lpkgFile.getName();

				_writeManifest(
					zipFile, jarOutputStream,
					name.substring(0, name.length() - 5));

				ZipEntry zipEntry = zipFile.getEntry(
					"liferay-marketplace.properties");

				jarOutputStream.putNextEntry(new ZipEntry(zipEntry.getName()));

				StreamUtil.transfer(
					zipFile.getInputStream(zipEntry), jarOutputStream, false);

				jarOutputStream.closeEntry();
			}

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		try {
			_activate(bundleContext);
		}
		catch (Throwable throwable) {
			_throwableCollector.collect(throwable);
		}
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) {
		_lpkgBundleTracker.close();

		_wabBundleTracker.close();
	}

	private void _activate(BundleContext bundleContext) throws Exception {
		bundleContext.registerService(
			URLStreamHandlerService.class.getName(),
			new LPKGURLStreamHandlerService(_urls),
			HashMapDictionaryBuilder.<String, Object>put(
				URLConstants.URL_HANDLER_PROTOCOL, new String[] {"lpkg"}
			).build());

		_wabBundleTracker = new BundleTracker<>(
			bundleContext, ~Bundle.UNINSTALLED,
			new WABWrapperBundleTrackerCustomizer(bundleContext));

		_wabBundleTracker.open();

		Set<Bundle> removalPendingBundles = new HashSet<>();

		_deploymentDirPath = _getDeploymentDirPath();

		Path overrideDirPath = _deploymentDirPath.resolve("override");

		List<File> jarFiles = _scanFiles(overrideDirPath, ".jar", true, false);

		removalPendingBundles.addAll(
			_uninstallOrphanOverridingJars(bundleContext, jarFiles));

		List<File> warFiles = _scanFiles(overrideDirPath, ".war", true, false);

		_uninstallOrphanOverridingWars(bundleContext, warFiles);

		if (!removalPendingBundles.isEmpty()) {
			if (_log.isInfoEnabled()) {
				_log.info("Start refreshing uninstalled orphan bundles");
			}

			FrameworkEvent frameworkEvent = _refreshBundles(
				removalPendingBundles, bundleContext);

			if (frameworkEvent.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
				if (_log.isInfoEnabled()) {
					_log.info("Finished refreshing uninstalled orphan bundles");
				}
			}
			else {
				throw new Exception(
					"Unable to refresh uninstalled orphan bundles because of " +
						"framework event " + frameworkEvent,
					frameworkEvent.getThrowable());
			}
		}

		LPKGBundleTrackerCustomizer lpkgBundleTrackerCustomizer =
			new LPKGBundleTrackerCustomizer(
				bundleContext, _urls, _toFileNames(jarFiles, warFiles));

		_lpkgBundleTracker = new BundleTracker<>(
			bundleContext, ~Bundle.UNINSTALLED, lpkgBundleTrackerCustomizer);

		_lpkgBundleTracker.open();

		lpkgBundleTrackerCustomizer.cleanTrackedBundles(
			_lpkgBundleTracker.getBundles());

		List<File> lpkgFiles = _scanFiles(
			_deploymentDirPath, ".lpkg", false, true);

		if (lpkgFiles.isEmpty()) {
			return;
		}

		List<File> explodedLPKGFiles = new ArrayList<>();

		Iterator<File> iterator = lpkgFiles.iterator();

		while (iterator.hasNext()) {
			File lpkgFile = iterator.next();

			List<File> innerLPKGFiles = ContainerLPKGUtil.deploy(
				lpkgFile, null);

			if (innerLPKGFiles != null) {
				iterator.remove();

				explodedLPKGFiles.addAll(innerLPKGFiles);
			}
		}

		lpkgFiles.addAll(explodedLPKGFiles);

		_installLPKGs(bundleContext, lpkgFiles);

		_installOverrideJars(bundleContext, jarFiles);

		_installOverrideWars(bundleContext, warFiles);
	}

	private void _deployLicense(File file) {
		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String zipEntryName = zipEntry.getName();

				if (!zipEntryName.endsWith(".xml")) {
					continue;
				}

				try (InputStream inputStream = zipFile.getInputStream(
						zipEntry)) {

					String content = StreamUtil.toString(inputStream);

					Document document = SAXReaderUtil.read(content);

					Element rootElement = document.getRootElement();

					String rootElementName = rootElement.getName();

					if (!rootElementName.equals("license") &&
						!rootElementName.equals("licenses")) {

						continue;
					}

					JSONObject jsonObject = JSONUtil.put("licenseXML", content);

					LicenseManagerUtil.registerLicense(jsonObject);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}
		catch (Exception exception) {
			_log.error("Unable to register license", exception);
		}
	}

	private Path _getDeploymentDirPath() throws Exception {
		File deploymentDir = new File(
			PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR);

		deploymentDir = deploymentDir.getCanonicalFile();

		deploymentDir.mkdirs();

		return deploymentDir.toPath();
	}

	private void _installLPKGs(
		BundleContext bundleContext, List<File> lpkgFiles) {

		for (File lpkgFile : lpkgFiles) {
			try {
				List<Bundle> bundles = deploy(bundleContext, lpkgFile);

				if (!bundles.isEmpty()) {
					Bundle lpkgBundle = bundles.get(0);

					lpkgBundle.start();
				}
			}
			catch (Exception exception) {
				_log.error("Unable to deploy LPKG file " + lpkgFile, exception);
			}
		}
	}

	private void _installOverrideJars(
			BundleContext bundleContext, List<File> jarFiles)
		throws Exception {

		for (File jarFile : jarFiles) {
			String location = _LPKG_OVERRIDE_PREFIX.concat(
				jarFile.getCanonicalPath());

			Bundle jarBundle = bundleContext.getBundle(location);

			if (jarBundle != null) {
				if (_log.isInfoEnabled()) {
					_log.info("Using overriding JAR bundle " + location);
				}

				continue;
			}

			jarBundle = bundleContext.installBundle(
				location, new FileInputStream(jarFile));

			BundleStartLevelUtil.setStartLevelAndStart(
				jarBundle,
				ModuleFrameworkPropsValues.
					MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL,
				bundleContext);

			if (_log.isInfoEnabled()) {
				_log.info("Installed override JAR bundle " + location);
			}
		}
	}

	private void _installOverrideWars(
			BundleContext bundleContext, List<File> warFiles)
		throws Exception {

		Properties properties = _loadOverrideWarsProperties(bundleContext);

		Path osgiWarDir = Paths.get(PropsValues.MODULE_FRAMEWORK_WAR_DIR);

		boolean modified = false;

		for (File warFile : warFiles) {
			String sourceLocation = warFile.getCanonicalPath();

			String targetLocation = properties.getProperty(sourceLocation);

			if (targetLocation != null) {
				if (_log.isInfoEnabled()) {
					_log.info("Using overridding WAR bundle " + targetLocation);
				}

				continue;
			}

			Path sourceWarPath = warFile.toPath();

			Path targetWarPath = osgiWarDir.resolve(
				sourceWarPath.getFileName());

			Files.copy(
				sourceWarPath, targetWarPath,
				StandardCopyOption.REPLACE_EXISTING);

			targetLocation = targetWarPath.toString();

			properties.put(sourceLocation, targetLocation);

			if (_log.isInfoEnabled()) {
				_log.info("Deployed override WAR bundle to " + targetLocation);
			}

			modified = true;
		}

		if (modified) {
			_saveOverrideWarsProperties(bundleContext, properties);
		}
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

	private Properties _loadOverrideWarsProperties(BundleContext bundleContext)
		throws Exception {

		Bundle bundle = bundleContext.getBundle(0);

		BundleContext systemBundleContext = bundle.getBundleContext();

		File overrideWarsPropertiesFile = systemBundleContext.getDataFile(
			"override-wars.properties");

		Properties overrideWarsProperties = new Properties();

		if (overrideWarsPropertiesFile.exists()) {
			try (InputStream inputStream = new FileInputStream(
					overrideWarsPropertiesFile)) {

				overrideWarsProperties.load(inputStream);
			}
		}

		return overrideWarsProperties;
	}

	/**
	 * @see FrameworkWiring#getRemovalPendingBundles
	 */
	private FrameworkEvent _refreshBundles(
			Collection<Bundle> bundles, BundleContext bundleContext)
		throws Exception {

		Bundle systemBundle = bundleContext.getBundle(0);

		FrameworkWiring frameworkWiring = systemBundle.adapt(
			FrameworkWiring.class);

		final DefaultNoticeableFuture<FrameworkEvent> defaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		frameworkWiring.refreshBundles(
			bundles,
			new FrameworkListener() {

				@Override
				public void frameworkEvent(FrameworkEvent frameworkEvent) {
					defaultNoticeableFuture.set(frameworkEvent);
				}

			});

		return defaultNoticeableFuture.get();
	}

	private void _saveOverrideWarsProperties(
			BundleContext bundleContext, Properties properties)
		throws Exception {

		Bundle bundle = bundleContext.getBundle(0);

		BundleContext systemBundleContext = bundle.getBundleContext();

		File overrideWarsPropertiesFile = systemBundleContext.getDataFile(
			"override-wars.properties");

		try (OutputStream outputStream = new FileOutputStream(
				overrideWarsPropertiesFile)) {

			properties.store(outputStream, null);
		}
	}

	private List<File> _scanFiles(
			Path dirPath, String extension, boolean checkFileName,
			boolean recursive)
		throws IOException {

		if (Files.notExists(dirPath)) {
			return Collections.emptyList();
		}

		List<File> files = new ArrayList<>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				dirPath)) {

			for (Path path : directoryStream) {
				String pathName = StringUtil.toLowerCase(
					String.valueOf(path.getFileName()));

				if (!pathName.endsWith(extension)) {
					if (recursive && Files.isDirectory(path)) {
						files.addAll(
							_scanFiles(
								path, extension, checkFileName, recursive));
					}

					continue;
				}

				if (checkFileName && !_isValid(pathName)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Override file " + path +
								" has an invalid name and will be ignored");
					}

					continue;
				}

				files.add(path.toFile());
			}
		}

		return files;
	}

	private Set<String> _toFileNames(List<File> jarFiles, List<File> warFiles) {
		Set<String> fileNames = new HashSet<>();

		for (File file : jarFiles) {
			fileNames.add(StringUtil.toLowerCase(file.getName()));
		}

		for (File file : warFiles) {
			fileNames.add(StringUtil.toLowerCase(file.getName()));
		}

		return fileNames;
	}

	private Set<Bundle> _uninstallOrphanOverridingJars(
			BundleContext bundleContext, List<File> jarFiles)
		throws Exception {

		Set<Bundle> removedBundles = new HashSet<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			String location = bundle.getLocation();

			if (!location.startsWith(_LPKG_OVERRIDE_PREFIX)) {
				continue;
			}

			String filePath = location.substring(
				_LPKG_OVERRIDE_PREFIX.length());

			if (jarFiles.contains(new File(filePath))) {
				continue;
			}

			bundle.uninstall();

			removedBundles.add(bundle);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Uninstalled orphan overriding JAR bundle " + location);
			}
		}

		return removedBundles;
	}

	private void _uninstallOrphanOverridingWars(
			BundleContext bundleContext, List<File> warFiles)
		throws Exception {

		Properties properties = _loadOverrideWarsProperties(bundleContext);

		Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();

		Iterator<Map.Entry<Object, Object>> iterator = entrySet.iterator();

		boolean modified = false;

		while (iterator.hasNext()) {
			Map.Entry<Object, Object> entry = iterator.next();

			if (warFiles.contains(new File((String)entry.getKey()))) {
				continue;
			}

			iterator.remove();

			Files.deleteIfExists(Paths.get((String)entry.getValue()));

			modified = true;
		}

		if (modified) {
			_saveOverrideWarsProperties(bundleContext, properties);
		}
	}

	private void _writeManifest(
			ZipFile zipFile, JarOutputStream jarOutputStream,
			String symbolicName)
		throws IOException {

		Manifest manifest = new Manifest();

		Attributes attributes = manifest.getMainAttributes();

		Properties properties = new Properties();

		properties.load(
			zipFile.getInputStream(
				zipFile.getEntry("liferay-marketplace.properties")));

		attributes.putValue(
			Constants.BUNDLE_DESCRIPTION,
			properties.getProperty("description"));

		attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
		attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, symbolicName);
		attributes.putValue(
			Constants.BUNDLE_VERSION, properties.getProperty("version"));
		attributes.putValue("Liferay-Releng-Bundle-Type", "lpkg");
		attributes.putValue("Manifest-Version", "2");

		jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

		manifest.write(jarOutputStream);

		jarOutputStream.closeEntry();
	}

	private static final String _LPKG_OVERRIDE_PREFIX = "LPKG-Override::";

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultLPKGDeployer.class);

	private Path _deploymentDirPath;
	private BundleTracker<List<Bundle>> _lpkgBundleTracker;

	@Reference
	private LPKGVerifier _lpkgVerifier;

	@Reference(target = "(throwable.collector=initial.bundles)")
	private ThrowableCollector _throwableCollector;

	private final Map<String, URL> _urls = new ConcurrentHashMap<>();
	private BundleTracker<Bundle> _wabBundleTracker;

}