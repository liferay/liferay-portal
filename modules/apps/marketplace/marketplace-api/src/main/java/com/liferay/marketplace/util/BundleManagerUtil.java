/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.util;

import com.liferay.portal.kernel.deploy.auto.AutoDeployException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.ShutdownUtil;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

/**
 * @author Ryan Park
 */
public class BundleManagerUtil {

	public static Bundle getBundle(String symbolicName, String versionString) {
		Version version = Version.parseVersion(versionString);

		for (Bundle bundle : getBundles()) {
			if (symbolicName.equals(bundle.getSymbolicName()) &&
				version.equals(bundle.getVersion())) {

				return bundle;
			}
		}

		return null;
	}

	public static List<Bundle> getBundles() {
		return ListUtil.fromArray(_bundleContext.getBundles());
	}

	public static List<Bundle> getInstalledBundles() {
		List<Bundle> bundles = getBundles();

		Iterator<Bundle> iterator = bundles.iterator();

		while (iterator.hasNext()) {
			Bundle bundle = iterator.next();

			if (isInstalled(bundle)) {
				continue;
			}

			iterator.remove();
		}

		return bundles;
	}

	public static Manifest getManifest(File file) {
		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry zipEntry = zipFile.getEntry("META-INF/MANIFEST.MF");

			if (zipEntry == null) {
				return null;
			}

			try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
				return new Manifest(inputStream);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	public static void installLPKG(File file) throws Exception {
		File installFile = new File(_getInstallDirName(), file.getName());

		Files.move(
			file.toPath(), installFile.toPath(),
			StandardCopyOption.REPLACE_EXISTING);

		if (_isRestartRequired(installFile)) {
			ShutdownUtil.shutdown(0);
		}
	}

	public static boolean isInstalled(Bundle bundle) {
		return ArrayUtil.contains(_INSTALLED_BUNDLE_STATES, bundle.getState());
	}

	public static boolean isInstalled(String symbolicName, String version) {
		Bundle bundle = getBundle(symbolicName, version);

		if (bundle == null) {
			return false;
		}

		return isInstalled(bundle);
	}

	public static void uninstallBundle(Bundle bundle) {
		try {
			bundle.uninstall();
		}
		catch (BundleException bundleException) {
			_log.error(bundleException);
		}
	}

	public static void uninstallBundle(String symbolicName, String version) {
		Bundle bundle = getBundle(symbolicName, version);

		if (bundle == null) {
			return;
		}

		uninstallBundle(bundle);
	}

	private static String _getInstallDirName() throws Exception {
		String[] autoDeployDirNames = PropsUtil.getArray(
			PropsKeys.MODULE_FRAMEWORK_AUTO_DEPLOY_DIRS);

		if (ArrayUtil.isEmpty(autoDeployDirNames)) {
			throw new AutoDeployException(
				"The portal property \"" +
					PropsKeys.MODULE_FRAMEWORK_AUTO_DEPLOY_DIRS +
						"\" is not set");
		}

		String autoDeployDirName = autoDeployDirNames[0];

		for (String curDirName : autoDeployDirNames) {
			if (curDirName.endsWith("/marketplace")) {
				autoDeployDirName = curDirName;

				break;
			}
		}

		return autoDeployDirName;
	}

	private static boolean _isRestartRequired(File file) {
		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry zipEntry = zipFile.getEntry(
				"liferay-marketplace.properties");

			if (zipEntry == null) {
				return false;
			}

			Properties properties = new Properties();

			properties.load(zipFile.getInputStream(zipEntry));

			return GetterUtil.getBoolean(
				properties.getProperty("restart-required"), true);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to read liferay-marketplace.properties from " +
						file.getName(),
					exception);
			}
		}

		return false;
	}

	private static final int[] _INSTALLED_BUNDLE_STATES = {
		Bundle.ACTIVE, Bundle.INSTALLED, Bundle.RESOLVED
	};

	private static final Log _log = LogFactoryUtil.getLog(
		BundleManagerUtil.class);

	private static final BundleContext _bundleContext;

	static {
		Bundle bundle = FrameworkUtil.getBundle(BundleManagerUtil.class);

		_bundleContext = bundle.getBundleContext();
	}

}