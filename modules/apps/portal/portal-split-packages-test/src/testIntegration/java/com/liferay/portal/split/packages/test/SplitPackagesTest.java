/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.split.packages.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Tom Wang
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class SplitPackagesTest {

	@Test
	public void testSplitPackages() {
		Bundle testBundle = FrameworkUtil.getBundle(SplitPackagesTest.class);

		BundleContext bundleContext = testBundle.getBundleContext();

		Map<String, Set<Bundle>> packageBundlesMap = new TreeMap<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			if (bundleWiring == null) {
				continue;
			}

			for (BundleCapability bundleCapability :
					bundleWiring.getCapabilities(
						PackageNamespace.PACKAGE_NAMESPACE)) {

				Map<String, Object> attributes =
					bundleCapability.getAttributes();

				Set<Bundle> bundles = packageBundlesMap.computeIfAbsent(
					StringBundler.concat(
						attributes.get(PackageNamespace.PACKAGE_NAMESPACE),
						";version=",
						attributes.get(
							PackageNamespace.CAPABILITY_VERSION_ATTRIBUTE)),
					key -> new LinkedHashSet<>());

				bundles.add(bundle);
			}
		}

		List<String> splitPackages = new ArrayList<>();

		for (Map.Entry<String, Set<Bundle>> entry :
				packageBundlesMap.entrySet()) {

			Set<Bundle> bundles = entry.getValue();

			if (bundles.size() <= 1) {
				continue;
			}

			splitPackages.add(entry.getKey() + " in " + bundles);
		}

		Assert.assertTrue(
			"Detected split packages " + splitPackages,
			splitPackages.isEmpty());
	}

}