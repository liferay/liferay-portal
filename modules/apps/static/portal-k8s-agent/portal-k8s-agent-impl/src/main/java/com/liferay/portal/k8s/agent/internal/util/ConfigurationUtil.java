/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collections;
import java.util.Enumeration;

import org.apache.felix.configurator.impl.json.BinUtil;
import org.apache.felix.configurator.impl.json.BinaryManager;
import org.apache.felix.configurator.impl.json.JSONUtil;
import org.apache.felix.configurator.impl.model.ConfigurationFile;

import org.osgi.framework.Bundle;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Gregory Amerson
 */
public class ConfigurationUtil {

	public static Configuration getConfiguration(
			ConfigurationAdmin configurationAdmin, String pid)
		throws Exception {

		if (pid.endsWith(_FILE_EXTENSION)) {
			pid = pid.substring(0, pid.length() - _FILE_EXTENSION.length());
		}

		int index = pid.indexOf(CharPool.TILDE);

		if (index <= 0) {
			index = pid.indexOf(CharPool.UNDERLINE);

			if (index <= 0) {
				index = pid.indexOf(CharPool.DASH);
			}
		}

		if (index > 0) {
			String name = pid.substring(index + 1);

			pid = pid.substring(0, index);

			return configurationAdmin.getFactoryConfiguration(
				pid, name, StringPool.QUESTION);
		}

		return configurationAdmin.getConfiguration(pid, StringPool.QUESTION);
	}

	public static ConfigurationFile getConfigurationFile(
			Bundle bundle, String fileName, String json, JSONUtil.Report report)
		throws MalformedURLException {

		if (!fileName.endsWith(_FILE_EXTENSION)) {
			throw new IllegalArgumentException("Invalid file " + fileName);
		}

		BinaryManager binaryManager = new BinaryManager(
			new BinUtil.ResourceProvider() {

				@Override
				public Enumeration<URL> findEntries(
					String path, String pattern) {

					return Collections.emptyEnumeration();
				}

				@Override
				public long getBundleId() {
					return bundle.getBundleId();
				}

				@Override
				public URL getEntry(String path) {
					return null;
				}

				@Override
				public String getIdentifier() {
					return fileName;
				}

			},
			report);

		return JSONUtil.readJSON(
			binaryManager, fileName, new URL("file", null, fileName),
			bundle.getBundleId(), json, report);
	}

	private static final String _FILE_EXTENSION =
		".client-extension-config.json";

}