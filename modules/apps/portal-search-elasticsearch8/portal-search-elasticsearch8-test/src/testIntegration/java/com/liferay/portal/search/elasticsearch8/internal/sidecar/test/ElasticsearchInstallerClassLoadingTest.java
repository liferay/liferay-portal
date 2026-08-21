/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.sidecar.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Magdalena Jedraszak
 */
@RunWith(Arquillian.class)
public class ElasticsearchInstallerClassLoadingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCanLoadCommonsCompressClasses() throws Exception {
		Bundle testBundle = FrameworkUtil.getBundle(
			ElasticsearchInstallerClassLoadingTest.class);

		String symbolicName = "com.liferay.portal.search.elasticsearch8.impl";

		Bundle bundle = BundleUtil.getBundle(
			testBundle.getBundleContext(), symbolicName);

		Assert.assertNotNull(
			"Unable to find bundle with symbolic name: " + symbolicName,
			bundle);

		bundle.loadClass(
			"org.apache.commons.compress.archivers.tar.TarArchiveEntry");
		bundle.loadClass(
			"org.apache.commons.compress.archivers.tar.TarArchiveInputStream");
		bundle.loadClass(
			"org.apache.commons.compress.compressors.gzip." +
				"GzipCompressorInputStream");
	}

}