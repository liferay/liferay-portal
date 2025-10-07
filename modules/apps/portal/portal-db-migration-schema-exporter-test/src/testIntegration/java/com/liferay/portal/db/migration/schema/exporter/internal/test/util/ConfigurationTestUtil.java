/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.test.util;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ConfigurationTestUtil {

	public static void deployConfiguration(
			ConfigurationAdmin configurationAdmin, String path, String pid)
		throws Exception {

		Assert.assertNull(
			configurationAdmin.listConfigurations("(service.pid=" + pid + ")"));

		try (AutoCloseable autoCloseable =
				_registerOnAfterDeleteConfigurationModelListener(pid)) {

			_createConfiguration(pid, _getConfigurationContent(path));

			Assert.assertNotNull(
				configurationAdmin.listConfigurations(
					"(service.pid=" + pid + ")"));

			_countDownLatch.await(1000, TimeUnit.SECONDS);
		}
	}

	public static Path getConfigurationPath(String pid) {
		return Paths.get(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, pid.concat(".config"));
	}

	private static Configuration _createConfiguration(
			String pid, String content)
		throws Exception {

		return com.liferay.portal.configuration.test.util.ConfigurationTestUtil.
			updateConfiguration(
				pid,
				() -> Files.write(
					getConfigurationPath(pid),
					content.getBytes(Charset.defaultCharset())));
	}

	private static String _getConfigurationContent(String path) {
		return "exportFilesPath=\"" + path + "\"\n";
	}

	private static AutoCloseable
		_registerOnAfterDeleteConfigurationModelListener(String pid) {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_countDownLatch = new CountDownLatch(1);

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				ConfigurationModelListener.class,
				new ConfigurationModelListener() {

					@Override
					public void onAfterDelete(String pid) {
						_countDownLatch.countDown();
					}

				},
				HashMapDictionaryBuilder.put(
					"model.class.name", pid
				).build());

		return serviceRegistration::unregister;
	}

	private static CountDownLatch _countDownLatch;

}