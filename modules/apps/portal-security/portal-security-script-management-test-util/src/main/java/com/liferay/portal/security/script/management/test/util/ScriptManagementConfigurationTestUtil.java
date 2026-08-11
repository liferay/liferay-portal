/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.test.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.script.management.configuration.GroovyScriptUsesCheckThreadLocal;
import com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration;

import java.io.Closeable;

/**
 * @author Feliphe Marinho
 */
public class ScriptManagementConfigurationTestUtil {

	public static void delete() {
		try (SafeCloseable safeCloseable =
				GroovyScriptUsesCheckThreadLocal.setEnabledWithSafeCloseable(
					false)) {

			ConfigurationTestUtil.deleteConfiguration(
				ScriptManagementConfiguration.class.getName());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void save(boolean allowScriptContentToBeExecutedOrIncluded)
		throws Exception {

		try (SafeCloseable safeCloseable =
				GroovyScriptUsesCheckThreadLocal.setEnabledWithSafeCloseable(
					false)) {

			ConfigurationTestUtil.saveConfiguration(
				ScriptManagementConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"allowScriptContentToBeExecutedOrIncluded",
					allowScriptContentToBeExecutedOrIncluded
				).build());
		}
	}

	public static Closeable saveWithCloseable(
			boolean allowScriptContentToBeExecutedOrIncluded)
		throws Exception {

		save(allowScriptContentToBeExecutedOrIncluded);

		return () -> {
			try {
				save(!allowScriptContentToBeExecutedOrIncluded);
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

}