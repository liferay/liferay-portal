/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.internal.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class ConfigurationFileInstallerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCanTransformURL() {
		Bundle bundle = FrameworkUtil.getBundle(
			ConfigurationFileInstallerTest.class);

		try (ServiceTrackerList<FileInstaller> serviceTrackerList =
				ServiceTrackerListFactory.open(
					bundle.getBundleContext(), FileInstaller.class)) {

			FileInstaller configurationFileInstaller = null;

			for (FileInstaller fileInstaller : serviceTrackerList.toList()) {
				Class<?> clazz = fileInstaller.getClass();

				if (Objects.equals(
						clazz.getName(),
						"com.liferay.portal.file.install.internal." +
							"configuration.ConfigurationFileInstaller")) {

					configurationFileInstaller = fileInstaller;

					break;
				}
			}

			Assert.assertNotNull(configurationFileInstaller);

			File configFile = new File(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, "test.config");

			Assert.assertTrue(
				StringBundler.concat(
					"Configuration file ", configFile, " which is in ",
					PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
					" folder should transformed by ConfigurationFileInstaller"),
				configurationFileInstaller.canTransformURL(configFile));

			configFile = new File(
				PropsValues.MODULE_FRAMEWORK_MODULES_DIR, "test.config");

			Assert.assertFalse(
				StringBundler.concat(
					"Configuration file ", configFile, " which is not in ",
					PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
					" folder should not be transformed by ",
					"ConfigurationFileInstaller"),
				configurationFileInstaller.canTransformURL(configFile));
		}
	}

}