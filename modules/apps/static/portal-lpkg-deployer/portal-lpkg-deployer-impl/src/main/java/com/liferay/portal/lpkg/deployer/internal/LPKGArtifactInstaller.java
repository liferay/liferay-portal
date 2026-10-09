/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lpkg.deployer.internal;

import com.liferay.osgi.util.bundle.BundleStartLevelUtil;
import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ModuleFrameworkPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = FileInstaller.class)
public class LPKGArtifactInstaller implements FileInstaller {

	@Override
	public boolean canTransformURL(File file) {
		String name = StringUtil.toLowerCase(file.getName());

		return name.endsWith(".lpkg");
	}

	@Override
	public URL transformURL(File file) throws Exception {
		String canonicalPath = LPKGLocationUtil.getLPKGLocation(file);

		Bundle bundle = _bundleContext.getBundle(canonicalPath);

		if (bundle != null) {
			_update(file, _readMarketplaceProperties(file));

			return null;
		}

		Properties properties = new Properties();

		List<File> lpkgFiles = ContainerLPKGUtil.deploy(file, properties);

		if (lpkgFiles == null) {
			_install(file, properties);

			return null;
		}

		try (SafeCloseable safeCloseable =
				LPKGBatchInstallThreadLocal.
					setBatchInstallInProcessWithSafeCloseable(true)) {

			_batchInstall(lpkgFiles);
		}

		return null;
	}

	@Override
	public void uninstall(File file) throws Exception {
		Bundle bundle = _bundleContext.getBundle(
			LPKGLocationUtil.getLPKGLocation(file));

		if (bundle != null) {
			bundle.uninstall();
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private void _batchInstall(List<File> lpkgFiles) throws Exception {
		Map<Bundle, List<Bundle>> lpkgBundles = new HashMap<>();

		for (File file : lpkgFiles) {
			Properties properties = new Properties();

			try (ZipFile zipFile = new ZipFile(file)) {
				ZipEntry zipEntry = zipFile.getEntry(
					"liferay-marketplace.properties");

				if (zipEntry != null) {
					try (InputStream inputStream = zipFile.getInputStream(
							zipEntry)) {

						properties.load(inputStream);
					}
				}
			}

			List<Bundle> bundles = _install(file, properties);

			if (bundles.isEmpty()) {
				continue;
			}

			lpkgBundles.put(bundles.remove(0), bundles);
		}

		for (Map.Entry<Bundle, List<Bundle>> entry : lpkgBundles.entrySet()) {
			List<Bundle> bundles = entry.getValue();

			for (Bundle bundle : bundles) {
				Dictionary<String, String> headers = bundle.getHeaders(
					StringPool.BLANK);

				String header = headers.get("Web-ContextPath");

				try {
					if (header == null) {
						BundleStartLevelUtil.setStartLevelAndStart(
							bundle,
							ModuleFrameworkPropsValues.
								MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL,
							_bundleContext);
					}
					else {
						BundleStartLevelUtil.setStartLevelAndStart(
							bundle,
							ModuleFrameworkPropsValues.
								MODULE_FRAMEWORK_WEB_START_LEVEL,
							_bundleContext);
					}
				}
				catch (BundleException bundleException) {
					_log.error(
						"Rollback bundle installation for " + bundles,
						bundleException);

					Bundle lpkgBundle = entry.getKey();

					lpkgBundle.uninstall();

					break;
				}
			}
		}
	}

	private List<Bundle> _install(File file, Properties properties)
		throws Exception {

		String canonicalPath = LPKGLocationUtil.getLPKGLocation(file);

		Bundle existingBundle = _bundleContext.getBundle(canonicalPath);

		if (existingBundle != null) {
			_update(file, properties);

			return Collections.emptyList();
		}

		if (GetterUtil.getBoolean(
				properties.getProperty("restart-required"), true)) {

			if (existingBundle == null) {
				_logRestartRequired(canonicalPath);
			}

			return Collections.emptyList();
		}

		List<Bundle> bundles = _lpkgDeployer.deploy(_bundleContext, file);

		if (bundles.isEmpty()) {
			return Collections.emptyList();
		}

		Bundle lpkgBundle = bundles.get(0);

		try {
			lpkgBundle.start();
		}
		catch (BundleException bundleException) {
			_log.error(
				StringBundler.concat(
					"Unable to start ", lpkgBundle, " for ", file),
				bundleException);
		}

		return bundles;
	}

	private void _logRestartRequired(String canonicalPath) {
		if (_log.isInfoEnabled()) {
			_log.info(
				"The portal instance needs to be restarted to complete the " +
					"installation of " + canonicalPath);
		}
	}

	private Properties _readMarketplaceProperties(File file) {
		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry zipEntry = zipFile.getEntry(
				"liferay-marketplace.properties");

			if (zipEntry == null) {
				return null;
			}

			Properties properties = new Properties();

			try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
				properties.load(inputStream);
			}

			return properties;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to read liferay-marketplace.properties from " +
						file.getName(),
					exception);
			}
		}

		return null;
	}

	private FrameworkEvent _refreshBundles(Set<Bundle> bundles)
		throws Exception {

		Bundle systemBundle = _bundleContext.getBundle(0);

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

	private void _update(File file, Properties properties) throws Exception {
		String canonicalPath = LPKGLocationUtil.getLPKGLocation(file);

		Bundle bundle = _bundleContext.getBundle(canonicalPath);

		if (bundle == null) {
			return;
		}

		Version currentVersion = bundle.getVersion();

		Version newVersion = new Version(properties.getProperty("version"));

		if (newVersion.compareTo(currentVersion) <= 0) {
			return;
		}

		if (GetterUtil.getBoolean(
				properties.getProperty("restart-required"), true)) {

			_logRestartRequired(canonicalPath);

			return;
		}

		Map<Bundle, List<Bundle>> deployedLPKGBundles =
			_lpkgDeployer.getDeployedLPKGBundles();

		List<Bundle> installedBundles = deployedLPKGBundles.get(bundle);

		Set<Bundle> wrapperBundles = new HashSet<>();

		for (Bundle installedBundle : installedBundles) {
			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			if (!Boolean.getBoolean(headers.get("Wrapper-Bundle"))) {
				continue;
			}

			wrapperBundles.add(installedBundle);
		}

		if (!wrapperBundles.isEmpty()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Refreshing ", wrapperBundles, " to update ", bundle));
			}

			FrameworkEvent frameworkEvent = _refreshBundles(wrapperBundles);

			if (frameworkEvent.getType() != FrameworkEvent.PACKAGES_REFRESHED) {
				_log.error(
					StringBundler.concat(
						"Unable to refresh ", wrapperBundles,
						" because of framework event ", frameworkEvent),
					frameworkEvent.getThrowable());
			}
		}

		bundle.update(_lpkgDeployer.toBundle(file));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LPKGArtifactInstaller.class);

	private BundleContext _bundleContext;

	@Reference
	private LPKGDeployer _lpkgDeployer;

}