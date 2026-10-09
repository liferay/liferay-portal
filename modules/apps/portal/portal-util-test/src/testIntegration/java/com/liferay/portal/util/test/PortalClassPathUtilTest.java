/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalClassPathUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class PortalClassPathUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testBoostrapClassPathManifest() {
		ProcessConfig processConfig =
			PortalClassPathUtil.getPortalProcessConfig();

		Set<String> liferayReleaseApps = new HashSet<>();

		for (String bootstrapClassPathEntry :
				StringUtil.split(
					processConfig.getBootstrapClassPath(),
					File.pathSeparatorChar)) {

			try (JarFile jarFile = new JarFile(
					new File(bootstrapClassPathEntry))) {

				Manifest manifest = jarFile.getManifest();

				if (manifest == null) {
					continue;
				}

				Attributes attributes = manifest.getMainAttributes();

				Set<Object> attributeSet = attributes.keySet();

				String attributeSetString = String.valueOf(attributeSet);

				if (attributeSetString.contains("Liferay-Releng")) {
					liferayReleaseApps.add(bootstrapClassPathEntry);
				}
			}
			catch (IOException ioException) {
				_log.error(
					"Unable to resolve bootstrap entry: " +
						bootstrapClassPathEntry + " from bundle",
					ioException);
			}
		}

		Assert.assertTrue(
			"Bootstrap class path should not contain JARs with " +
				"'Liferay-Releng-App' headers in Manifest.MF: " +
					liferayReleaseApps,
			liferayReleaseApps.isEmpty());
	}

	@Test
	public void testBoostrapClassPathPetra() {
		ProcessConfig processConfig =
			PortalClassPathUtil.getPortalProcessConfig();

		Set<String> nonpetraEntries = new HashSet<>();

		for (String bootstrapClassPathEntry :
				StringUtil.split(
					processConfig.getBootstrapClassPath(),
					File.pathSeparatorChar)) {

			if (bootstrapClassPathEntry.contains("petra")) {
				continue;
			}

			nonpetraEntries.add(bootstrapClassPathEntry);
		}

		Assert.assertTrue(
			"Bootstrap class path should not contain nonpetra JARs " +
				nonpetraEntries,
			nonpetraEntries.isEmpty());
	}

	@Test
	public void testGetPortalProcessConfig() {
		ProcessConfig processConfig =
			PortalClassPathUtil.getPortalProcessConfig();

		List<String> runtimeClassPathEntries = StringUtil.split(
			processConfig.getRuntimeClassPath(), File.pathSeparatorChar);

		Set<String> runtimeClassPathEntrySet = new HashSet<>();

		List<String> duplicateEntries = new ArrayList<>();

		for (String runtimeClassPathEntry : runtimeClassPathEntries) {
			if (runtimeClassPathEntrySet.add(runtimeClassPathEntry)) {
				continue;
			}

			duplicateEntries.add(runtimeClassPathEntry);
		}

		Assert.assertTrue(
			"Portal process config runtime class path should not contain " +
				"duplicate entries: " + duplicateEntries,
			duplicateEntries.isEmpty());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalClassPathUtilTest.class);

}