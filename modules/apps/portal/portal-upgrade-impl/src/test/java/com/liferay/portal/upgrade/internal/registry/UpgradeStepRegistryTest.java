/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.registry;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carlos Sierra Andrés
 */
public class UpgradeStepRegistryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCreateUpgradeInfos() {
		UpgradeStepRegistry upgradeStepRegistry = _createUpgradeStepRegistry(
			true);

		TestUpgradeStep testUpgradeStep = new TestUpgradeStep();

		upgradeStepRegistry.register(
			"0.0.0", "1.0.0", testUpgradeStep, testUpgradeStep, testUpgradeStep,
			testUpgradeStep);

		List<UpgradeInfo> upgradeInfos = upgradeStepRegistry.getUpgradeInfos();

		Assert.assertEquals(upgradeInfos.toString(), 1, upgradeInfos.size());

		UpgradeInfo upgradeInfo = upgradeInfos.get(0);

		Assert.assertEquals("0.0.0", upgradeInfo.getFromSchemaVersionString());
		Assert.assertEquals("1.0.0", upgradeInfo.getToSchemaVersionString());
		Assert.assertEquals(
			Arrays.asList(
				testUpgradeStep, testUpgradeStep, testUpgradeStep,
				testUpgradeStep),
			ReflectionTestUtil.getFieldValue(
				upgradeInfo.getUpgradeStep(), "arg$1"));
	}

	@Test
	public void testCreateUpgradeInfosWithNoSteps() {
		UpgradeStepRegistry upgradeStepRegistry = _createUpgradeStepRegistry(
			true);

		upgradeStepRegistry.register("0.0.0", "1.0.0");

		List<UpgradeInfo> upgradeInfos = upgradeStepRegistry.getUpgradeInfos();

		Assert.assertTrue(upgradeInfos.toString(), upgradeInfos.isEmpty());
	}

	@Test
	public void testCreateUpgradeInfosWithOneStep() {
		UpgradeStepRegistry upgradeStepRegistry = _createUpgradeStepRegistry(
			true);

		TestUpgradeStep testUpgradeStep = new TestUpgradeStep();

		upgradeStepRegistry.register("0.0.0", "1.0.0", testUpgradeStep);

		List<UpgradeInfo> upgradeInfos = upgradeStepRegistry.getUpgradeInfos();

		Assert.assertEquals(upgradeInfos.toString(), 1, upgradeInfos.size());
		Assert.assertEquals(
			new UpgradeInfo("0.0.0", "1.0.0", testUpgradeStep),
			upgradeInfos.get(0));
	}

	@Test
	public void testCreateUpgradeInfosWithPostUpgradeSteps() {
		_registerAndCheckPreAndPostUpgradeSteps(
			new UpgradeStep[0],
			new UpgradeStep[] {new TestUpgradeStep(), new TestUpgradeStep()});
	}

	@Test
	public void testCreateUpgradeInfosWithPreAndPostUpgradeSteps() {
		_registerAndCheckPreAndPostUpgradeSteps(
			new UpgradeStep[] {new TestUpgradeStep()},
			new UpgradeStep[] {new TestUpgradeStep()});
	}

	@Test
	public void testCreateUpgradeInfosWithPreUpgradeSteps() {
		_registerAndCheckPreAndPostUpgradeSteps(
			new UpgradeStep[] {new TestUpgradeStep(), new TestUpgradeStep()},
			new UpgradeStep[0]);
	}

	@Test
	public void testGetInitializationStep() {
		UpgradeStepRegistry upgradeStepRegistry = _createUpgradeStepRegistry(
			true);

		upgradeStepRegistry.registerInitialization();

		List<UpgradeInfo> upgradeInfos = upgradeStepRegistry.getUpgradeInfos();

		Assert.assertEquals(upgradeInfos.toString(), 1, upgradeInfos.size());

		UpgradeInfo upgradeInfo = upgradeInfos.get(0);

		Assert.assertEquals("0.0.0", upgradeInfo.getFromSchemaVersionString());
		Assert.assertEquals("1.0.0", upgradeInfo.getToSchemaVersionString());
		Assert.assertTrue(
			upgradeInfo.getUpgradeStep() instanceof DummyUpgradeStep);
	}

	@Test
	public void testGetInitializationStepWhenAnUpgradeProcessIsRegistered() {
		UpgradeStepRegistry upgradeStepRegistry = _createUpgradeStepRegistry(
			true);

		upgradeStepRegistry.registerInitialization();

		upgradeStepRegistry.register("1.0.0", "2.0.0", new TestUpgradeStep());

		List<UpgradeInfo> upgradeInfos = upgradeStepRegistry.getUpgradeInfos();

		Assert.assertEquals(upgradeInfos.toString(), 2, upgradeInfos.size());

		UpgradeInfo upgradeInfo = upgradeInfos.get(0);

		Assert.assertEquals("0.0.0", upgradeInfo.getFromSchemaVersionString());
		Assert.assertEquals("2.0.0", upgradeInfo.getToSchemaVersionString());
		Assert.assertTrue(
			upgradeInfo.getUpgradeStep() instanceof DummyUpgradeStep);
	}

	@Test
	public void testSkipInitializationStepWhenAnUpgradeProcessIsRegisteredAndPortalNotUpgraded() {
		UpgradeStepRegistry upgradeStepRegistry = _createUpgradeStepRegistry(
			false);

		upgradeStepRegistry.registerInitialization();

		TestUpgradeStep testUpgradeStep = new TestUpgradeStep();

		upgradeStepRegistry.register("1.0.0", "2.0.0", testUpgradeStep);

		List<UpgradeInfo> upgradeInfos = upgradeStepRegistry.getUpgradeInfos();

		Assert.assertEquals(upgradeInfos.toString(), 1, upgradeInfos.size());
		Assert.assertEquals(
			new UpgradeInfo("1.0.0", "2.0.0", testUpgradeStep),
			upgradeInfos.get(0));
	}

	@Test
	public void testSkipInitializationStepWhenPortalNotUpgraded() {
		UpgradeStepRegistry upgradeStepRegistry = _createUpgradeStepRegistry(
			false);

		upgradeStepRegistry.registerInitialization();

		List<UpgradeInfo> upgradeInfos = upgradeStepRegistry.getUpgradeInfos();

		Assert.assertEquals(upgradeInfos.toString(), 0, upgradeInfos.size());
	}

	private UpgradeStepRegistry _createUpgradeStepRegistry(
		boolean portalUpgraded) {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<UpgradeStepRegistrator> serviceRegistration =
			bundleContext.registerService(
				UpgradeStepRegistrator.class,
				upgradeStepRegistry -> {
				},
				null);

		return new UpgradeStepRegistry(
			bundleContext, portalUpgraded, serviceRegistration.getReference());
	}

	private void _registerAndCheckPreAndPostUpgradeSteps(
		UpgradeStep[] preUpgradeSteps, UpgradeStep[] postUpgradeSteps) {

		UpgradeProcess upgradeProcess = new UpgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
			}

			@Override
			protected UpgradeStep[] getPostUpgradeSteps() {
				return postUpgradeSteps;
			}

			@Override
			protected UpgradeStep[] getPreUpgradeSteps() {
				return preUpgradeSteps;
			}

		};

		UpgradeStepRegistry upgradeStepRegistry = _createUpgradeStepRegistry(
			true);

		upgradeStepRegistry.register("0.0.0", "1.0.0", upgradeProcess);

		UpgradeStep[] sortedUpgradeSteps = ArrayUtil.append(
			preUpgradeSteps, new UpgradeStep[] {upgradeProcess},
			postUpgradeSteps);

		List<UpgradeInfo> upgradeInfos = upgradeStepRegistry.getUpgradeInfos();

		Assert.assertEquals(upgradeInfos.toString(), 1, upgradeInfos.size());

		UpgradeInfo upgradeInfo = upgradeInfos.get(0);

		Assert.assertEquals("0.0.0", upgradeInfo.getFromSchemaVersionString());
		Assert.assertEquals("1.0.0", upgradeInfo.getToSchemaVersionString());
		Assert.assertEquals(
			Arrays.asList(sortedUpgradeSteps),
			ReflectionTestUtil.getFieldValue(
				upgradeInfo.getUpgradeStep(), "arg$1"));
	}

	private static class TestUpgradeStep implements UpgradeStep {

		@Override
		public void upgrade() {
		}

	}

}