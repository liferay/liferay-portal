/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.Dictionary;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class FreeMarkerEngineConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = new UpgradeProcess[1];

		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {
				}

				@Override
				public void registerReleaseCreationUpgradeSteps(
					UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<? extends UpgradeStep> clazz =
							upgradeStep.getClass();

						if (Objects.equals(
								clazz.getName(),
								"com.liferay.portal.template.freemarker." +
									"internal.upgrade.v1_0_0.FreeMarkerEngine" +
										"ConfigurationUpgradeProcess")) {

							upgradeProcesses[0] = (UpgradeProcess)upgradeStep;

							break;
						}
					}
				}

			});

		Configuration configuration = _configurationAdmin.getConfiguration(
			_PID, StringPool.QUESTION);

		Dictionary<String, Object> originalProperties =
			configuration.getProperties();

		try {
			String[] allowedClasses = {RandomTestUtil.randomString()};

			configuration.update(
				HashMapDictionaryBuilder.<String, Object>put(
					"allowedClasses", allowedClasses
				).build());

			upgradeProcesses[0].upgrade();

			configuration = _configurationAdmin.getConfiguration(
				_PID, StringPool.QUESTION);

			Dictionary<String, Object> properties =
				configuration.getProperties();

			Assert.assertArrayEquals(
				allowedClasses,
				(String[])properties.get("allowedInstantiableClasses"));

			String[] allowedInstantiableClasses = {
				RandomTestUtil.randomString()
			};

			configuration.update(
				HashMapDictionaryBuilder.<String, Object>put(
					"allowedClasses", allowedClasses
				).put(
					"allowedInstantiableClasses", allowedInstantiableClasses
				).build());

			upgradeProcesses[0].upgrade();

			configuration = _configurationAdmin.getConfiguration(
				_PID, StringPool.QUESTION);

			properties = configuration.getProperties();

			Assert.assertArrayEquals(
				allowedInstantiableClasses,
				(String[])properties.get("allowedInstantiableClasses"));
		}
		finally {
			if (originalProperties == null) {
				configuration.delete();
			}
			else {
				configuration.update(originalProperties);
			}
		}
	}

	private static final String _PID =
		"com.liferay.portal.template.freemarker.configuration." +
			"FreeMarkerEngineConfiguration";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject(
		filter = "component.name=com.liferay.portal.template.freemarker.internal.upgrade.registry.FreeMarkerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}