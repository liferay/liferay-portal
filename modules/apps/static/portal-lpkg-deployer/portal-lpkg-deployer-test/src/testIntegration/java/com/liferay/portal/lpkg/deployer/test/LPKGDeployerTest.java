/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lpkg.deployer.test;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.lpkg.StaticLPKGResolver;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Method;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Matthew Tambara
 */
public class LPKGDeployerTest {

	@Test
	public void testDeployedLPKGs() throws Exception {
		Bundle testBundle = FrameworkUtil.getBundle(LPKGDeployerTest.class);

		BundleContext bundleContext = testBundle.getBundleContext();

		final String lpkgDeployerDirString =
			PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR;

		Assert.assertNotNull(
			"The property \"module.framework.marketplace.dir\" is null",
			lpkgDeployerDirString);

		Path lpkgDeployerDirPath = Paths.get(lpkgDeployerDirString);

		Assert.assertTrue(
			"The path " + lpkgDeployerDirString + " does not exist",
			Files.exists(lpkgDeployerDirPath));

		final List<File> lpkgFiles = new ArrayList<>();

		Files.walkFileTree(
			lpkgDeployerDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path fileNamePath = filePath.getFileName();

					String fileName = StringUtil.toLowerCase(
						fileNamePath.toString());

					if (fileName.endsWith(".md")) {
						return FileVisitResult.CONTINUE;
					}

					if (!fileName.endsWith(".lpkg")) {
						Assert.fail(
							StringBundler.concat(
								"Unexpected file ", filePath, " in ",
								lpkgDeployerDirString));
					}

					lpkgFiles.add(filePath.toFile());

					return FileVisitResult.CONTINUE;
				}

			});

		Assert.assertFalse(
			"There are no LPKG files in " + lpkgDeployerDirString,
			lpkgFiles.isEmpty());

		ServiceTracker<LPKGDeployer, LPKGDeployer> serviceTracker =
			new ServiceTracker<>(bundleContext, LPKGDeployer.class, null);

		serviceTracker.open();

		LPKGDeployer lpkgDeployer = serviceTracker.getService();

		Bundle lpkgDeployerBundle = FrameworkUtil.getBundle(
			lpkgDeployer.getClass());

		Class<?> clazz = lpkgDeployerBundle.loadClass(
			"com.liferay.portal.lpkg.deployer.internal.LPKGLocationUtil");

		Method generateInnerBundleLocationMethod = clazz.getDeclaredMethod(
			"generateInnerBundleLocation", Bundle.class, String.class);
		Method lpkgLocationMethod = clazz.getDeclaredMethod(
			"getLPKGLocation", File.class);

		serviceTracker.close();

		Map<Bundle, List<Bundle>> deployedLPKGBundles =
			lpkgDeployer.getDeployedLPKGBundles();

		for (File lpkgFile : lpkgFiles) {
			String lpkgLocation = (String)lpkgLocationMethod.invoke(
				null, lpkgFile);

			Bundle lpkgBundle = bundleContext.getBundle(lpkgLocation);

			Assert.assertNotNull(
				"No matching LPKG bundle for " + lpkgLocation, lpkgBundle);

			List<Bundle> expectedAppBundles = new ArrayList<>(
				deployedLPKGBundles.get(lpkgBundle));

			Assert.assertNotNull(
				StringBundler.concat(
					"Registered LPKG bundles ", deployedLPKGBundles.keySet(),
					" do not contain ", lpkgBundle),
				expectedAppBundles);

			Collections.sort(expectedAppBundles);

			List<Bundle> actualAppBundles = new ArrayList<>();

			ZipFile zipFile = new ZipFile(lpkgFile);

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			String symbolicName = lpkgBundle.getSymbolicName();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String name = zipEntry.getName();

				if (name.endsWith(".jar")) {
					if (_staticLPKGBundleSymbolicNames.contains(symbolicName)) {
						File file = new File(
							lpkgDeployerDirString + StringPool.SLASH +
								lpkgFile.getName());

						String location = StringBundler.concat(
							name, "?lpkgPath=",
							lpkgLocationMethod.invoke(null, file),
							"&protocol=lpkg&static=true");

						Bundle bundle = bundleContext.getBundle(location);

						Assert.assertNotNull(
							"No matching static bundle for " + location,
							bundle);
					}
					else {
						String location =
							(String)generateInnerBundleLocationMethod.invoke(
								null, lpkgBundle, name);

						Bundle bundle = bundleContext.getBundle(location);

						Assert.assertNotNull(
							"No matching app bundle for " + location, bundle);

						actualAppBundles.add(bundle);
					}
				}

				if (!name.endsWith(".war")) {
					continue;
				}

				String location =
					(String)generateInnerBundleLocationMethod.invoke(
						null, lpkgBundle, name);

				Bundle bundle = bundleContext.getBundle(location);

				Assert.assertNotNull(
					"No matching app bundle for " + location, bundle);

				actualAppBundles.add(bundle);

				String contextName = name.substring(
					0, name.lastIndexOf(".war"));

				int index = contextName.lastIndexOf('-');

				if (index >= 0) {
					contextName = contextName.substring(0, index);
				}

				String portalProfileNames = null;

				Path tempFilePath = Files.createTempFile(null, null);

				try (InputStream inputStream1 = zipFile.getInputStream(
						zipEntry)) {

					Files.copy(
						inputStream1, tempFilePath,
						StandardCopyOption.REPLACE_EXISTING);

					try (ZipFile zipFile2 = new ZipFile(tempFilePath.toFile());
						InputStream inputStream2 = zipFile2.getInputStream(
							new ZipEntry(
								"WEB-INF/liferay-plugin-package.properties"))) {

						if (inputStream2 != null) {
							Properties properties = new Properties();

							properties.load(inputStream2);

							String configuredServletContextName =
								properties.getProperty("servlet-context-name");

							if (configuredServletContextName != null) {
								contextName = configuredServletContextName;
							}

							portalProfileNames = properties.getProperty(
								"liferay-portal-profile-names");
						}
					}
				}
				finally {
					Files.delete(tempFilePath);
				}

				StringBundler sb = new StringBundler(13);

				sb.append("webbundle:/");
				sb.append(URLCodec.encodeURL(lpkgBundle.getSymbolicName()));
				sb.append(StringPool.DASH);
				sb.append(lpkgBundle.getVersion());
				sb.append(StringPool.SLASH);
				sb.append(contextName);
				sb.append(".war?Bundle-Version=");
				sb.append(bundle.getVersion());
				sb.append("&Web-ContextPath=/");
				sb.append(contextName);
				sb.append("&protocol=lpkg");

				if (Validator.isNotNull(portalProfileNames)) {
					sb.append("&liferay-portal-profile-names=");
					sb.append(portalProfileNames);
				}

				location = sb.toString();

				Assert.assertNotNull(
					StringBundler.concat(
						"Missing WAR bundle for wrapper bundle ", bundle,
						" with expected location ", location),
					bundleContext.getBundle(location));
			}

			if (!symbolicName.equals("static")) {
				Collections.sort(actualAppBundles);

				Assert.assertEquals(
					StringBundler.concat(
						"LPKG bundle ", lpkgBundle, " expects app bundles ",
						expectedAppBundles, " but has actual app bundles ",
						actualAppBundles),
					expectedAppBundles, actualAppBundles);
			}
		}
	}

	private static final List<String> _staticLPKGBundleSymbolicNames =
		StaticLPKGResolver.getStaticLPKGBundleSymbolicNames();

}