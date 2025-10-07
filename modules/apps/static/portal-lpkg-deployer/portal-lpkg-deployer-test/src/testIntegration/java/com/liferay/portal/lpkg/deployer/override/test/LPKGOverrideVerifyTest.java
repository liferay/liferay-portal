/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lpkg.deployer.override.test;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

/**
 * @author Matthew Tambara
 */
public class LPKGOverrideVerifyTest {

	@Test
	public void testOverriddenLPKGs() throws Exception {
		Bundle testBundle = FrameworkUtil.getBundle(
			LPKGOverrideVerifyTest.class);

		BundleContext bundleContext = testBundle.getBundleContext();

		Properties properties = new Properties();

		Path path = Paths.get(PropsValues.LIFERAY_HOME, "/overrides");

		Assert.assertTrue(Files.exists(path));

		properties.load(Files.newBufferedReader(path));

		Map<String, String> jars = new HashMap<>();

		List<String> statics = new ArrayList<>();

		List<String> wars = new ArrayList<>();

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String symbolicName = (String)entry.getKey();

			if (symbolicName.startsWith("static.")) {
				statics.add(symbolicName.substring(7));
			}
			else if (symbolicName.startsWith("war.")) {
				wars.add(symbolicName.substring(4));
			}
			else {
				jars.put((String)entry.getKey(), (String)entry.getValue());
			}
		}

		for (Bundle bundle : bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			String version = jars.remove(symbolicName);

			if (Validator.isNotNull(version)) {
				Assert.assertEquals(
					"JAR not successfully overridden: " + symbolicName,
					new Version(version), bundle.getVersion());
			}
			else if (statics.remove(symbolicName)) {
				String location = bundle.getLocation();

				Assert.assertTrue(
					"Static JAR not successfully overridden: " + symbolicName,
					location.contains("protocol=jar"));
			}
			else {
				String location = bundle.getLocation();

				if (location.contains("protocol=file")) {
					wars.remove(symbolicName);
				}
			}
		}

		List<Map.Entry<String, String>> leftoverEntries = new ArrayList<>();

		leftoverEntries.addAll(jars.entrySet());

		for (Map.Entry<String, String> entry : leftoverEntries) {
			if (entry.getValue() == null) {
				leftoverEntries.remove(entry);
			}
		}

		Collections.sort(
			leftoverEntries,
			new Comparator<Map.Entry>() {

				@Override
				public int compare(Map.Entry entry1, Map.Entry entry2) {
					String entrySymbolicName = (String)entry1.getKey();

					return entrySymbolicName.compareTo((String)entry2.getKey());
				}

			});

		Assert.assertTrue(
			"JARs not overridden: " + jars.entrySet(), jars.isEmpty());
		Assert.assertTrue(
			"Static JARs not overridden: " + statics, statics.isEmpty());
		Assert.assertTrue("WARs not overridden: " + wars, wars.isEmpty());
	}

}