/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.resource.v1_0.test.util;

import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManager;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Time;

import java.util.Date;
import java.util.Objects;

/**
 * @author Alberto Sousa
 */
public class CMPLicenseTestUtil {

	public static AutoCloseable withAppDisabled() {
		return _withApp(false, null);
	}

	public static AutoCloseable withAppExpired() {
		return _withApp(true, new Date(System.currentTimeMillis() - Time.DAY));
	}

	private static AutoCloseable _withApp(
		boolean appEnabled, Date expirationDate) {

		LicenseManager licenseManager = LicenseManagerUtil.getLicenseManager();

		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			LicenseManagerUtil.class, "_licenseManager",
			ProxyUtil.newProxyInstance(
				LicenseManager.class.getClassLoader(),
				new Class<?>[] {LicenseManager.class},
				(proxy, method, arguments) -> {
					String methodName = method.getName();

					if (Objects.equals(methodName, "getAppExpirationDate") &&
						Objects.equals(arguments[0], App.CMP)) {

						return expirationDate;
					}

					if (Objects.equals(methodName, "isAppEnabled") &&
						Objects.equals(arguments[0], App.CMP)) {

						return appEnabled;
					}

					return method.invoke(licenseManager, arguments);
				}));
	}

}