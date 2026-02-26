/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.theme.dxp.override.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.InputStream;

import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Shuyang Zhou
 */
public abstract class BaseThemeFragmentBundleInstaller {

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		Bundle fragmentBundle = _getOrCreateFragmentBundle(bundleContext);

		Bundle systemBundle = bundleContext.getBundle(0);

		BundleContext systemBundleContext = systemBundle.getBundleContext();

		Bundle currentBundle = bundleContext.getBundle();

		systemBundleContext.addBundleListener(
			new BundleListener() {

				@Override
				public void bundleChanged(BundleEvent bundleEvent) {
					Bundle bundle = bundleEvent.getBundle();

					if (!bundle.equals(currentBundle)) {
						return;
					}

					if (bundleEvent.getType() == BundleEvent.STARTED) {

						// In case of STARTED, that means the current bundle
						// was updated or refreshed, we must unregister this
						// self monitor listener to release the previous bundle
						// revision

						systemBundleContext.removeBundleListener(this);

						return;
					}

					if (bundleEvent.getType() != BundleEvent.UNINSTALLED) {
						return;
					}

					try {
						fragmentBundle.uninstall();
					}
					catch (BundleException bundleException) {
						_log.error(
							StringBundler.concat(
								"Unable to uninstall fragment bundle ",
								fragmentBundle, " for host bundle ",
								getHostBundleSymbolicName()),
							bundleException);
					}

					systemBundleContext.removeBundleListener(this);
				}

			});
	}

	protected abstract String getHostBundleSymbolicName();

	protected abstract String[] getResources();

	private InputStream _generateBundleContent() throws Exception {
		String hostBundleSymbolicName = getHostBundleSymbolicName();

		Class<?> clazz = getClass();

		Bundle bundle = FrameworkUtil.getBundle(clazz);

		Version version = bundle.getVersion();

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (JarOutputStream jarOutputStream = new JarOutputStream(
					unsyncByteArrayOutputStream)) {

				Manifest manifest = new Manifest();

				Attributes attributes = manifest.getMainAttributes();

				attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
				attributes.putValue(
					Constants.BUNDLE_SYMBOLICNAME,
					hostBundleSymbolicName.concat("-fragment"));
				attributes.putValue(
					Constants.BUNDLE_VERSION, version.toString());
				attributes.putValue(
					Constants.FRAGMENT_HOST, hostBundleSymbolicName);
				attributes.putValue("Manifest-Version", "2");

				jarOutputStream.putNextEntry(
					new ZipEntry(JarFile.MANIFEST_NAME));

				manifest.write(jarOutputStream);

				jarOutputStream.closeEntry();

				for (String resource : getResources()) {
					jarOutputStream.putNextEntry(
						new ZipEntry("images/".concat(resource)));

					StreamUtil.transfer(
						clazz.getResourceAsStream(resource), jarOutputStream,
						false);

					jarOutputStream.closeEntry();
				}
			}

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	private Bundle _getOrCreateFragmentBundle(BundleContext bundleContext)
		throws Exception {

		String location = "theme-fragment:" + getHostBundleSymbolicName();

		Bundle fragmentBundle = bundleContext.getBundle(location);

		if (fragmentBundle == null) {
			fragmentBundle = bundleContext.installBundle(
				location, _generateBundleContent());
		}

		return fragmentBundle;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseThemeFragmentBundleInstaller.class);

}