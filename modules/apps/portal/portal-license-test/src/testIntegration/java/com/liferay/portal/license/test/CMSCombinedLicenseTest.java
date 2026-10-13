/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.license.util.App;

import org.junit.Assume;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class CMSCombinedLicenseTest extends BaseCombinedLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && isCMS());
	}

	@Override
	protected App[] getApps() {
		return new App[] {App.CMP};
	}

}