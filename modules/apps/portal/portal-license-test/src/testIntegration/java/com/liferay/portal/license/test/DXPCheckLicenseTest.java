/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.util.LicenseUtil;

import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DXPCheckLicenseTest extends BaseCheckLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && !isCMS());
	}

	@Test
	public void testCheckLicenseWithFreeTierBinaryFile2026_Q1_0()
		throws Exception {

		testCheckLicense("free_tier_binary_file_2026_Q1_0.li");
	}

	@Test
	public void testCheckLicenseWithFreeTierBinaryFileAfter2026_Q1_0()
		throws Exception {

		testCheckLicense("free_tier_binary_file_after_2026_Q1_0.li");
	}

	@Override
	protected App[] getApps() {
		return App.values();
	}

	@Override
	protected void testCheckLicense(String fileName) throws Exception {
		Path target = Path.of(LicenseUtil.LICENSE_REPOSITORY_DIR, fileName);

		try (InputStream inputStream =
				DXPCheckLicenseTest.class.getResourceAsStream(
					"dependencies/" + fileName)) {

			Files.createDirectories(target.getParent());

			Files.copy(
				inputStream, target, StandardCopyOption.REPLACE_EXISTING);
		}

		assertLicensePropertiesNotExisted(getPortalProductId());

		checkLicense(getPortalProductId());

		assertLicensePropertiesExisted(getPortalProductId());

		assertPortalLicenseRegistered();
	}

}