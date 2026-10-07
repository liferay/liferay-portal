/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.configuration.URLFetchSecurityCompanyConfiguration;

/**
 * @author Lourdes Fernández Besada
 */
public class URLFetchSecurityCompanyConfigurationUtil {

	public static <T> T swap(
			String[] urlHostsAllowed, boolean urlLocalNetworkAccessEnabled,
			UnsafeSupplier<T, Exception> unsafeSupplier)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					URLFetchSecurityCompanyConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"urlHostsAllowed", urlHostsAllowed
					).put(
						"urlLocalNetworkAccessEnabled",
						urlLocalNetworkAccessEnabled
					).build())) {

			return unsafeSupplier.get();
		}
	}

	public static <T> T swapURLLocalNetworkAccessEnabled(
			boolean urlLocalNetworkAccessEnabled,
			UnsafeSupplier<T, Exception> unsafeSupplier)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					URLFetchSecurityCompanyConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"urlLocalNetworkAccessEnabled",
						urlLocalNetworkAccessEnabled
					).build())) {

			return unsafeSupplier.get();
		}
	}

}