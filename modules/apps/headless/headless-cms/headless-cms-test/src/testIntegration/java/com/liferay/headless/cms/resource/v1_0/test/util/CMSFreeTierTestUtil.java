/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test.util;

import com.liferay.portal.kernel.license.util.LicenseManager;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.util.Objects;

/**
 * @author Verónica González
 */
public class CMSFreeTierTestUtil {

	public static AutoCloseable withFreeTier() {
		LicenseManager licenseManager = LicenseManagerUtil.getLicenseManager();

		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			LicenseManagerUtil.class, "_licenseManager",
			ProxyUtil.newProxyInstance(
				LicenseManager.class.getClassLoader(),
				new Class<?>[] {LicenseManager.class},
				(proxy, method, arguments) -> {
					if (Objects.equals(method.getName(), "isFreeTier")) {
						return true;
					}

					return method.invoke(licenseManager, arguments);
				}));
	}

}