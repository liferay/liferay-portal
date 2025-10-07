/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.war.deploy.directory.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class WarDeployDirectoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeFalse(DBPartition.isPartitionEnabled());
	}

	@Test
	public void testAutoDeployDir() {
		File deployDir = new File(
			PropsUtil.get(PropsKeys.AUTO_DEPLOY_DEPLOY_DIR));

		Assert.assertTrue(deployDir.exists());
		Assert.assertTrue(deployDir.isDirectory());

		File[] deployDirFiles = deployDir.listFiles();

		Assert.assertEquals(
			deployDirFiles.toString(), 0, deployDirFiles.length);
	}

	@Test
	public void testPortalWarDir() {
		Bundle currentBundle = FrameworkUtil.getBundle(
			WarDeployDirectoryTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		List<String> installedWars = new ArrayList<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			String location = bundle.getLocation();

			if (location.startsWith("webbundle:")) {
				installedWars.add(
					location.substring(
						10, location.indexOf(StringPool.QUESTION)));
			}
		}

		File portalWarDir = new File(
			PropsUtil.get("module.framework.portal.war.dir"));

		Assert.assertTrue(portalWarDir.exists());
		Assert.assertTrue(portalWarDir.isDirectory());

		for (File file : portalWarDir.listFiles()) {
			Assert.assertTrue(installedWars.remove(file.getAbsolutePath()));
		}

		Assert.assertTrue(installedWars.isEmpty());
	}

	@Test
	public void testWarDir() throws Exception {
		Bundle currentBundle = FrameworkUtil.getBundle(
			WarDeployDirectoryTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		CountDownLatch addingBundleCountDownLatch = new CountDownLatch(1);
		CountDownLatch removeBundleCountDownLatch = new CountDownLatch(1);

		String testWarFileName = "test-war-dir.war";

		BundleTracker<Bundle> bundleTracker = new BundleTracker<Bundle>(
			bundleContext, Bundle.ACTIVE, null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent event) {
				String bundleLocation = bundle.getLocation();

				if (bundleLocation.contains(testWarFileName)) {
					addingBundleCountDownLatch.countDown();

					return bundle;
				}

				return null;
			}

			@Override
			public void removedBundle(
				Bundle bundle, BundleEvent bundleEvent, Bundle object) {

				removeBundleCountDownLatch.countDown();
			}

		};

		bundleTracker.open();

		File testWarFileInDeployDir = new File(
			PropsUtil.get(PropsKeys.AUTO_DEPLOY_DEPLOY_DIR), testWarFileName);

		Assert.assertFalse(testWarFileInDeployDir.exists());

		File testWarFileInWarDir = new File(
			PropsUtil.get(PropsKeys.MODULE_FRAMEWORK_WAR_DIR), testWarFileName);

		Assert.assertFalse(testWarFileInWarDir.exists());

		try {
			File portalWarDir = new File(
				PropsUtil.get("module.framework.portal.war.dir"));

			File[] files = portalWarDir.listFiles();

			File warFile = files[0];

			Files.copy(warFile.toPath(), testWarFileInDeployDir.toPath());

			addingBundleCountDownLatch.await();

			Assert.assertFalse(testWarFileInDeployDir.exists());
			Assert.assertTrue(testWarFileInWarDir.exists());
		}
		finally {
			testWarFileInWarDir.delete();

			removeBundleCountDownLatch.await();

			bundleTracker.close();
		}
	}

}