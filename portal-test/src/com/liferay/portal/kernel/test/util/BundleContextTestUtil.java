/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Shuyang Zhou
 */
public class BundleContextTestUtil {

	public static BundleContext getBundleContext(Class<?> clazz) {
		Bundle bundle = FrameworkUtil.getBundle(clazz);

		return bundle.getBundleContext();
	}

}